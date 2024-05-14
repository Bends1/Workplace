import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface Draw {
    fun drawMenu()
    fun drawAccOptions()
    fun drawUserInterface()
    fun drawMenuTests()
    fun drawEnter()
    fun drawTest(n: Int, arr: List<Pair<String, Int>>, variant: List<Map<out String?, Int>>)
}
interface UserData {
    fun loadDataUser(fileName: String): MutableList<User>
    fun createUser(name: String, password: String, list: MutableList<User>): User
    fun addUser(user: User, users: MutableList<User>)
    fun saveUserData(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        operationType: String,
        newValue: String?
    )

    fun saveResults(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        resPlus: Int,
        resMinus: Int,
        resMultiply: Int
    )
}

interface UserInterface {
    fun registration(userList: UserList)
    fun changeName(user: User)
    fun changePassword(user: User)
}
open class UserDataImpl : UserData {
    override fun loadDataUser(fileName: String): MutableList<User> {
        val json = File(fileName).readText()
        return if (json.isEmpty() || json == "[]") {
            mutableListOf()
        } else {
            val userList = Json.decodeFromString<UserList>(json)
            userList.users.toMutableList()
        }
    }

    override fun createUser(name: String, password: String, list: MutableList<User>): User {
        val id = list.size - 1
        return User(name, password, id)
    }

    override fun addUser(user: User, users: MutableList<User>) {
        val maxId = users.maxByOrNull { it.id }?.id ?: 0
        val newUser = user.copy(id = maxId + 1)
        users.add(newUser)
        val userListObject = UserList(users)
        val json = Json.encodeToString(userListObject)
        File("users.json").writeText(json)
    }

    override fun saveUserData(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        operationType: String,
        newValue: String?
    ) {
        val updatedUserList = userList.map {
            when (operationType) {
                "name" -> {
                    if (it.id == user.id) {
                        it.copy(name = newValue ?: it.name, password = user.password)
                    } else {
                        it
                    }
                }

                "password" -> {
                    if (it.id == user.id) {
                        it.copy(password = newValue ?: it.password, name = user.name)
                    } else {
                        it
                    }
                }

                else -> it
            }
        }
        val updatedUserListObject = UserList(updatedUserList.toMutableList())
        val json = Json.encodeToString(updatedUserListObject)
        File(fileName).writeText(json)
    }

    override fun saveResults(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        resPlus: Int,
        resMinus: Int,
        resMultiply: Int
    ) {
        val updatedUserList = userList.map {
            if (it.id == user.id) {
                it.copy(
                    resPlus = resPlus,
                    resMinus = resMinus,
                    resMultiply = resMultiply,
                    password = user.password,
                    name = user.name,
                )
            } else {
                it
            }
        }
        val updatedUserListObject = UserList(updatedUserList.toMutableList())
        val json = Json.encodeToString(updatedUserListObject)
        File(fileName).writeText(json)
    }

}

class UserInterfaceImpl : UserInterface, UserDataImpl() {
    override fun registration(userList: UserList) {
        println("Введіть своє ім'я: ")
        val name = readLine()
        if (name == "" || name!!.contains(" ")) {
            println("Ім'я не повинно містити пробіли")
            registration(userList)
        }
        for (i in userList.users.indices) {
            if (name == userList.users[i].name) {
                println("Ім'я вже зайнято")
                registration(userList)
            }
        }
        println("Введіть свій пароль: ")
        val password = readLine()
        if (password == "" || password!!.contains(" ")) {
            println("Пароль не повинен містити пробіли")
            registration(userList)
        }
        val user = createUser(name, password, userList.users)
        addUser(user, userList.users)
        println("Ви зареєструвалися ваше ім'я: ${user.name}, ваш пароль: ${user.password}")
        println("Дякую, що зареєструвалися, тепер увійдіть в аккаунт")
    }

    override fun changeName(user: User) {
        val userList = UserList(loadDataUser("users.json"))
        var isNameAvailable: Boolean
        var name: String?

        do {
            println("Введіть своє нове ім'я: ")
            name = readLine()
            if (name.isNullOrBlank() || name.contains(" ")) {
                println("Ім'я не повинно містити пробіли")
                isNameAvailable = false
            } else {
                isNameAvailable = true
                for (i in userList.users.indices) {
                    if (name == userList.users[i].name) {
                        println("Ім'я вже зайнято")
                        isNameAvailable = false
                        break
                    }
                }
            }
        } while (!isNameAvailable)

        user.name = name
        println("Ваше нове ім'я - $name")
        saveUserData(user, userList.users, "users.json", "name", name)
    }

    override fun changePassword(user: User) {
        var password: String?
        var userList = UserList(loadDataUser("users.json"))
        do {
            userList = UserList(loadDataUser("users.json"))
            println("Введіть свій новий пароль: ")
            password = readLine()
            if (password.isNullOrBlank() || password.contains(" ")) {
                println("Пароль не повинен містити пробіли")
            }
        } while (password.isNullOrBlank() || password.contains(" "))

        user.password = password
        println(user.password)
        println("Ваше новий пароль - $password")
        saveUserData(user, userList.users, "users.json", "password", password)
    }
}
class Menu : Draw {
    private val userData = UserDataImpl()
    private val userInterface = UserInterfaceImpl()
    private val tests = Tests()
    private var currentUser: User = User()
    private var userList = UserList(userData.loadDataUser("users.json"))
    private var userInput: String? = null

    override fun drawMenu() {
        userList = UserList(userData.loadDataUser("users.json"))
        currentUser = User()
        do {
            println(
                "1.Щоб ввійти в аккаунт\n" +
                        "2.Щоб зарееструватися"
            )
            userInput = readLine()
            when (userInput?.toIntOrNull()) {
                1 -> drawEnter()
                2 -> {
                    userInterface.registration(userList)
                    drawEnter()
                }

                else -> {
                    println("Введіть корректне значення")
                }
            }
        } while (userInput != "1" && userInput != "2")
    }

    override fun drawEnter() {
        currentUser = User()
        val (enterName, enterPassword) = getUserInput()
        userList = UserList(userData.loadDataUser("users.json"))
        val list = userList.users
        if (list.isNotEmpty()) {
            val user = list.find { it.name == enterName && it.password == enterPassword }
            if (user != null) {
                println("Вітаю $enterName!")
                currentUser = user
                drawUserInterface()
            } else {
                println("Ім'я чи пароль введені неправильно, спробуйте ще раз")
                drawMenu()
            }
        } else {
            println("У вас немає аккаунтів, спочатку зареєструйтеся")
            userInterface.registration(userList)
            drawEnter()
        }
    }

    private fun getUserInput(): Pair<String?, String?> {
        println("Введіть ваше ім'я: ")
        val enterName = readLine()
        println("Введіть ваш пароль: ")
        val enterPassword = readLine()
        return Pair(enterName, enterPassword)
    }


    override fun drawUserInterface() {
        do {
            println(
                "Щоб побачити меню тестів натисніть - 1\n" +
                        "Щоб ввійти в особистий кабінет натисніть - 2"
            )
            userInput = readLine()
            when (userInput?.toIntOrNull()) {
                1 -> drawMenuTests()
                2 -> drawAccOptions()
                else -> {
                    println("Введіть корректне значення")
                }
            }
        } while (userInput != "1" && userInput != "2")
    }

    override fun drawAccOptions() {
        do {
            println(
                "Щоб змінити ім'я натисніть - 1\n" +
                        "Щоб змінити пароль натисніть - 2\n" +
                        "Щоб вийти з своєї сессії натисніть - 3\n" +
                        "Назад - 4"
            )
            userInput = readLine()
            when (userInput?.toIntOrNull()) {
                1 -> {
                    userInterface.changeName(currentUser)
                    drawAccOptions()
                }

                2 -> {
                    userInterface.changePassword(currentUser)
                    drawAccOptions()
                }

                3 -> {
                    drawMenu()
                }

                4 -> drawUserInterface()
                else -> {
                    println("Введіть корректне значення")
                }
            }
        } while (userInput != "1" && userInput != "2" && userInput != "3" && userInput != "4")
    }

    override fun drawMenuTests() {
        userList = UserList(userData.loadDataUser("users.json"))
        val currentUserData = userList.users.find { it.id == currentUser.id }
        if (currentUserData != null) {
            currentUser = currentUserData
            println(
                "1.Тест на додавання[${currentUser.resPlus}/100]\n" +
                        "2.Тест на віднімання[${currentUser.resMinus}/100]\n" +
                        "3.Тест на множення[${currentUser.resMultiply}/100]\n" +
                        "4.Дізнатися середню оцінку\n" +
                        "5.Назад"
            )
            userInput = readLine()
            when (userInput) {
                "1" -> drawTest(1, tests.questionsPlus, tests.variantsPlus)
                "2" -> drawTest(2, tests.questionsMinus, tests.variantsMinus)
                "3" -> drawTest(3, tests.questionsMultiply, tests.variantsMultiply)
                "4" -> {
                    val midResult: Float = (currentUser.resPlus + currentUser.resMinus + currentUser.resMultiply) / 3.0F
                    println("Ваша середня оцінка - $midResult ")
                    drawMenuTests()
                }

                "5" -> drawUserInterface()
                else -> {
                    println("Введіть корректне значення")
                    drawMenuTests()
                }
            }
        } else {
            println("Помилка: дані користувача не були знайдені")
            drawMenuTests()
        }
    }

    private fun showResult(n: Int) {
        val testFinal = "Тест завершено! Ваша оцінка -"
        when (n) {
            1 -> println("$testFinal ${currentUser.resPlus}")
            2 -> println("$testFinal ${currentUser.resMinus}")
            3 -> println("$testFinal ${currentUser.resMultiply}")
        }
    }

    override fun drawTest(n: Int, arr: List<Pair<String, Int>>, variant: List<Map<out String?, Int>>) {
        userList = UserList(userData.loadDataUser("users.json"))
        var (resPlus, resMinus, resMultiply) = listOf(
            currentUser.resPlus,
            currentUser.resMinus,
            currentUser.resMultiply
        )

        when (n) {
            1 -> resPlus = 0
            2 -> resMinus = 0
            3 -> resMultiply = 0
        }

        arr.forEachIndexed { index, pair ->
            println(pair.first)
            variant[index].forEach { (key, value) ->
                println("$key) $value")
            }
            var userAnswer: String?
            do {
                userAnswer = readLine()
                if (userAnswer !in listOf("а", "б", "в", "г")) {
                    println("Введіть 1 з літер: 'а', 'б', 'в', 'г'")
                }
            } while (userAnswer !in listOf("а", "б", "в", "г"))

            if (variant[index][userAnswer] == arr[index].second) {
                when (n) {
                    1 -> resPlus += 20
                    2 -> resMinus += 20
                    3 -> resMultiply += 20
                }
            }
        }

        currentUser.resPlus = resPlus
        currentUser.resMinus = resMinus
        currentUser.resMultiply = resMultiply

        userData.saveResults(currentUser, userList.users, "users.json", resPlus, resMinus, resMultiply)
        showResult(n)
        drawMenuTests()
    }
}
