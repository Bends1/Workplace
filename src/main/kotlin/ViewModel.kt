import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface Draw {
    fun drawMenu()
    fun drawAccOptions()
    fun drawUserInterface()
    fun drawMenuTests()
    fun drawEnter()
    fun drawTest(testsRepository: TestsRepositoryImpl)
}
interface UserData {
    fun loadDataUser(fileName: String): MutableList<User>
    fun createUser(name: String?, password: String?, list: MutableList<User>): User
    fun addUser(user: User, users: MutableList<User>)
    fun saveUserData(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        operationType: String,
        newValue: String?
    )
    fun getUsers() : MutableList<User>
    fun getUser(name : String?, password: String?, list: MutableList<User>) : User
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
    fun registration(userList: MutableList<User>)
    fun onNameChange(user: User)
    fun onPasswordChange(user: User)
    fun onChekedValid(input : String?, value: String, userList: MutableList<User>)
}
interface TestsRepository {
    val questions : List<Pair<String, Int>>
    val variants : List<Map<out String?, Int>>
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

    override fun createUser(name: String?, password: String?, list: MutableList<User>): User {
        return User(name, password, list.size - 1)
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
    override fun getUser(name : String?, password: String?, list: MutableList<User>) : User {
        return list.find { it.name == name && it.password == password } ?: User()
    }

    override fun getUsers() : MutableList<User> {
        return  UserList(loadDataUser("users.json")).users
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
    override fun onChekedValid(input: String?, value: String, userList : MutableList<User>) {
        if (input!!.contains(" ")) {
            if(value == "name"){
                println("Ім'я не повинно містити пробіли")
            }
            else {
                println("Пароль не повинен містити пробіли")
            }
            registration(userList)
        } else if(input == "") {
            if(value == "name"){
                println("Ім'я не повинно бути порожнім")
            }
            else {
                println("Пароль не повинен бути порожнім")
            }
            registration(userList)
        }
    }
    override fun registration(userList: MutableList<User>) {
        println("Введіть своє ім'я: ")
        val name = readLine()
        onChekedValid(name, "name", userList)
        userList.forEach {
            if (name == it.name) {
                println("Ім'я вже зайнято")
                registration(userList)
                return
            }
        }
        println("Введіть свій пароль: ")
        val password = readLine()
        onChekedValid(password, "password", userList)
        val user = createUser(name, password, userList)
        addUser(user, userList)
        println("Ви зареєструвалися ваше ім'я: ${user.name}, ваш пароль: ${user.password}")
        println("Дякую, що зареєструвалися, тепер увійдіть в аккаунт")
    }

    override fun onNameChange(user: User) {
        val userList = getUsers()
        var isNameAvailable: Boolean
        var name: String?

        do {
            println("Введіть своє нове ім'я: ")
            name = readLine()
            if (name.isNullOrBlank() || name.contains(" ")) {
                println("Ім'я не повинно містити пробіли та бути пустим")
                isNameAvailable = false
            } else {
                isNameAvailable = true
                for (i in userList.indices) {
                    if (name == userList[i].name) {
                        println("Ім'я вже зайнято")
                        isNameAvailable = false
                        break
                    }
                }
            }
        } while (!isNameAvailable)

        user.name = name
        println("Ваше нове ім'я - $name")
        saveUserData(user, userList, "users.json", "name", name)
    }

    override fun onPasswordChange(user: User) {
        var password: String?
        var userList = getUsers()
        do {
            userList = getUsers()
            println("Введіть свій новий пароль: ")
            password = readLine()
            if (password.isNullOrBlank() || password.contains(" ")) {
                println("Пароль не повинен містити пробіли та бути пустим")
            }
        } while (password.isNullOrBlank() || password.contains(" "))

        user.password = password
        println(user.password)
        println("Ваше новий пароль - $password")
        saveUserData(user, userList, "users.json", "password", password)
    }
}
class Menu : Draw {
    private val userData = UserDataImpl()
    private val userInterface = UserInterfaceImpl()
    private var currentUser: User = User()
    private var userList = userData.getUsers()
    private var userInput: String? = null

    override fun drawMenu() {
        userList = userData.getUsers()
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
        userList = userData.getUsers()
        val list = userList
        if (list.isNotEmpty()) {
            currentUser = userData.getUser(enterName, enterPassword, userList)
            if(currentUser != User()){
                println("Вітаю ${currentUser.name}!")
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
                    userInterface.onNameChange(currentUser)
                    drawAccOptions()
                }

                2 -> {
                    userInterface.onPasswordChange(currentUser)
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
        userList = userData.getUsers()
        val currentUserData = userList.find { it.id == currentUser.id }
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
                "1" -> drawTest(TestsRepositoryImpl.PLUS)
                "2" -> drawTest(TestsRepositoryImpl.MINUS)
                "3" -> drawTest(TestsRepositoryImpl.MULTIPLY)
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
            println("Помилка: дані користувача не були знайдені, зареєструйте новий аккаунт")
            drawMenu()
        }
    }

    private fun showResult(testRepository : TestsRepositoryImpl) {
        val testFinal = "Тест завершено! Ваша оцінка -"
        when (testRepository) {
            TestsRepositoryImpl.PLUS -> println("$testFinal ${currentUser.resPlus}")
            TestsRepositoryImpl.MINUS -> println("$testFinal ${currentUser.resMinus}")
            TestsRepositoryImpl.MULTIPLY -> println("$testFinal ${currentUser.resMultiply}")
        }
    }

    override fun drawTest(testRepository: TestsRepositoryImpl) {
        val userList = UserList(userData.loadDataUser("users.json"))
        var resPlus = currentUser.resPlus
        var resMinus = currentUser.resMinus
        var resMultiply = currentUser.resMultiply

        when (testRepository) {
            TestsRepositoryImpl.PLUS -> resPlus = 0
            TestsRepositoryImpl.MINUS -> resMinus = 0
            TestsRepositoryImpl.MULTIPLY -> resMultiply = 0
        }

        val questions = testRepository.questions
        val variants = testRepository.variants

        questions.forEachIndexed { index, pair ->
            println(pair.first)
            variants[index].forEach { (key, value) ->
                println("$key) $value")
            }

            var userAnswer: String?
            do {
                userAnswer = readLine()
                if (userAnswer !in listOf("а", "б", "в", "г")) {
                    println("Введіть одну з літер: 'а', 'б', 'в', 'г'")
                }
            } while (userAnswer !in listOf("а", "б", "в", "г"))

            if (variants[index][userAnswer] == questions[index].second) {
                when (testRepository) {
                    TestsRepositoryImpl.PLUS -> resPlus += 20
                    TestsRepositoryImpl.MINUS -> resMinus += 20
                    TestsRepositoryImpl.MULTIPLY -> resMultiply += 20
                }
            }
        }

        currentUser.resPlus = resPlus
        currentUser.resMinus = resMinus
        currentUser.resMultiply = resMultiply

        userData.saveResults(currentUser, userList.users, "users.json", resPlus, resMinus, resMultiply)
        showResult(testRepository)
        drawMenuTests()
    }
}
