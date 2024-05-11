import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.decodeFromString


@Serializable
data class User(
    var name: String? = null,
    var password: String? = null,
    var id: Int = 0,
    var resPlus: Int = 0,
    var resMinus: Int = 0,
    var resMultiply: Int = 0
)

@Serializable
data class UserList(
    var users: MutableList<User> = mutableListOf()
)

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
    fun saveDataUsers(userList: UserList, fileName: String)
    fun saveDataUserName(user: User, userList: MutableList<User>, fileName: String, name: String?)
    fun saveDataUserPassword(user: User, userList: MutableList<User>, fileName: String, password: String?)
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
    fun registration()
    fun changeName()
    fun changePassword()
}

open class Tests {
    val questionsPlus: List<Pair<String, Int>> = listOf(
        Pair("Скільки буде 2 + 2?", 4),
        Pair("Скільки буде 5 + 3?", 8),
        Pair("Яка сума 10 і 5?", 15),
        Pair("Яка сума 6 і 4?", 10),
        Pair("Скільки буде 24 + 4?", 28),

        )
    val variantsPlus: List<Map<out String?, Int>> =
        listOf(
            mapOf(
                "а" to 4,
                "б" to 5,
                "в" to 6,
                "г" to 8,
            ),
            mapOf(
                "а" to 7,
                "б" to 9,
                "в" to 8,
                "г" to 10,
            ),
            mapOf(
                "а" to 13,
                "б" to 14,
                "в" to 15,
                "г" to 18,
            ),
            mapOf(
                "а" to 10,
                "б" to 15,
                "в" to 6,
                "г" to 9,
            ),
            mapOf(
                "а" to 24,
                "б" to 28,
                "в" to 29,
                "г" to 30,
            ),

            )
    val questionsMinus: List<Pair<String, Int>> = listOf(
        Pair("Скільки буде 2 - 2", 0),
        Pair("Скільки буде 5 - 3?", 2),
        Pair("Яка різниця між 10 і 5?", 5),
        Pair("Відніміть від 93 число 25", 68),
        Pair("Скільки буде 24 - 4?", 20),
    )
    val variantsMinus: List<Map<out String?, Int>> =
        listOf(
            mapOf(
                "а" to 0,
                "б" to -1,
                "в" to 1,
                "г" to 2,
            ),
            mapOf(
                "а" to 3,
                "б" to 2,
                "в" to 1,
                "г" to 0,
            ),
            mapOf(
                "а" to 4,
                "б" to 3,
                "в" to 5,
                "г" to 6,
            ),
            mapOf(
                "а" to 68,
                "б" to 65,
                "в" to 67,
                "г" to 69,
            ),
            mapOf(
                "а" to 21,
                "б" to 20,
                "в" to 25,
                "г" to 22,
            ),
        )
    val questionsMultiply: List<Pair<String, Int>> = listOf(
        Pair("Скільки буде 2 * 2", 4),
        Pair("Скільки буде 5 * 3?", 15),
        Pair("Обчисліть 10 * 5", 50),
        Pair("Скільки буде 6 * 4?", 24),
        Pair("Скільки буде 3 * 33?", 99),
    )
    val variantsMultiply: List<Map<out String?, Int>> =
        listOf(
            mapOf(
                "а" to 4,
                "б" to 5,
                "в" to 3,
                "г" to 6,
            ),
            mapOf(
                "а" to 10,
                "б" to 15,
                "в" to 20,
                "г" to 14,
            ),
            mapOf(
                "а" to 15,
                "б" to 25,
                "в" to 50,
                "г" to 55,
            ),
            mapOf(
                "а" to 24,
                "б" to 28,
                "в" to 22,
                "г" to 20,
            ),
            mapOf(
                "а" to 88,
                "б" to 77,
                "в" to 66,
                "г" to 99,
            ),
        )
}

class Menu : UserInterface, Draw, UserData, Tests() {
    private var onEnter = false
    private var currentUser: User = User()
    private var userList = UserList(loadDataUser("users.json"))
    private var userInput: String? = ""
    override fun drawMenu() {
        userList = UserList(loadDataUser("users.json"))
        currentUser = User()
        println(
            "1.Щоб ввійти в аккаунт\n" +
                    "2.Щоб зарееструватися"
        )
        userInput = readLine()
        when (userInput) {
            "1" -> drawEnter()
            "2" -> registration()
            else -> {
                println("Введіть корректне значення")
                drawMenu()
            }
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
    }

    override fun saveDataUsers(userList: UserList, fileName: String) {
        val json = Json.encodeToString(userList)
        File(fileName).writeText(json)
    }

    override fun saveDataUserName(user: User, userList: MutableList<User>, fileName: String, name: String?) {
        val updatedUserList = userList.map {
            if (it.id == user.id) {
                it.copy(name = name ?: it.name, password = user.password)
            } else {
                it
            }
        }
        val updatedUserListObject = UserList(updatedUserList.toMutableList())
        val json = Json.encodeToString(updatedUserListObject)
        File(fileName).writeText(json)
    }

    override fun saveDataUserPassword(user: User, userList: MutableList<User>, fileName: String, password: String?) {
        val updatedUserList = userList.map {
            if (it.id == user.id) {
                it.copy(password = password ?: it.password, name = user.name)
            } else {
                it
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

    override fun loadDataUser(fileName: String): MutableList<User> {
        val json = File(fileName).readText()
        return if (json.isEmpty() || json == "[]") {
            mutableListOf()
        } else {
            val userList = Json.decodeFromString<UserList>(json)
            userList.users.toMutableList()
        }
    }

    override fun registration() {
        userList = UserList(loadDataUser("users.json"))
        println("Введіть своє ім'я: ")
        val name = readLine()
        if (name == "" || name!!.contains(" ")) {
            println("Ім'я не повинно містити пробіли")
            registration()
        }
        for (i in userList.users.indices) {
            if (name == userList.users[i].name) {
                println("Ім'я вже зайнято")
                registration()
            }
        }
        println("Введіть свій пароль: ")
        val password = readLine()
        if (password == "" || password!!.contains(" ")) {
            println("Пароль не повинен містити пробіли")
            registration()
        }
        val user = createUser(name, password, userList.users)
        addUser(user, userList.users)
        println("Ви зареєструвалися ваше ім'я: ${user.name}, ваш пароль: ${user.password}")
        saveDataUsers(userList, "users.json")
        println("Дякую, що зареєструвалися, тепер увійдіть в аккаунт")
        drawEnter()
    }

    override fun drawEnter() {
        currentUser = User()
        var isCorrect = false
        println("Введіть ваше ім'я: ")
        userInput = readLine()
        val enterName = userInput
        println("Введіть ваш пароль: ")
        userInput = readLine()
        val enterPassword = userInput
        val file = File("users.json")
        if (file.exists()) {
            saveDataUsers(userList, "users.json")
            if (userList.users.isNotEmpty()) {
                val data = loadDataUser("users.json")
                for (i in data.indices) {
                    if (enterName == data[i].name && enterPassword == data[i].password) {
                        isCorrect = true
                        currentUser.name = data[i].name
                        currentUser.password = data[i].password
                        currentUser.id = data[i].id
                        break
                    }
                }
            } else {
                println("У вас немає аккаунтів, зареєструйтеся")
                registration()
            }
            if (isCorrect) {
                println("Вітаю ${enterName}!")
                onEnter = true
                drawUserInterface()
            } else {
                println("Ім'я чи пароль введені неправильно, спробуйте ще раз")
                drawEnter()
            }
        } else {
            println("У вас немає аккаунтів, спочатку зареєструйтеся")
            registration()
        }

    }


    override fun drawUserInterface() {
        println(
            "Щоб побачити меню тестів натисніть - 1\n" +
                    "Щоб ввійти в особистий кабінет натисніть - 2"
        )
        userInput = readLine()
        when (userInput) {
            "1" -> drawMenuTests()
            "2" -> drawAccOptions()
            else -> {
                println("Введіть корректне значення")
                drawUserInterface()
            }
        }
    }

    override fun drawAccOptions() {
        println(
            "Щоб змінити ім'я натисніть - 1\n" +
                    "Щоб змінити пароль натисніть - 2\n" +
                    "Щоб вийти з своєї сессії натисніть - 3\n" +
                    "Назад - 4"
        )
        userInput = readLine()
        when (userInput) {
            "1" -> changeName()
            "2" -> changePassword()
            "3" -> {
                onEnter = false
                drawMenu()
            }

            "4" -> drawUserInterface()
            else -> {
                println("Введіть корректне значення")
                drawAccOptions()
            }
        }
    }

    override fun changeName() {
        var name: String?
        do {
            println("Введіть своє нове ім'я: ")
            name = readLine()
            if (name.isNullOrBlank() || name.contains(" ")) {
                println("Ім'я не повинно містити пробіли")
            } else if (name == currentUser.name) {
                println("Ви ввели своє ім'я, нічого не змінилось")
                drawAccOptions()
            } else {
                for (i in userList.users.indices) {
                    if (name == userList.users[i].name) {
                        println("Ім'я вже зайнято")
                        changeName()
                    }
                }
            }
        } while (name.isNullOrBlank() || name.contains(" "))

        currentUser.name = name
        println("Ваше нове ім'я - $name")
        saveDataUserName(currentUser, userList.users, "users.json", name)
        drawAccOptions()
    }

    override fun changePassword() {
        var password: String?
        do {
            println("Введіть свій новий пароль: ")
            password = readLine()
            if (password.isNullOrBlank() || password.contains(" ")) {
                println("Пароль не повинен містити пробіли")
            } else if (password == currentUser.password) {
                println("Ви ввели свій старий пароль, нічого не змінилось")
                drawAccOptions()
            }
        } while (password.isNullOrBlank() || password.contains(" "))

        currentUser.password = password
        println(currentUser.password)
        println("Ваше новий пароль - $password")
        saveDataUserPassword(currentUser, userList.users, "users.json", password)
        drawAccOptions()
    }

    override fun drawMenuTests() {
        userList = UserList(loadDataUser("users.json"))
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
                "1" -> drawTest(1, questionsPlus, variantsPlus)
                "2" -> drawTest(2, questionsMinus, variantsMinus)
                "3" -> drawTest(3, questionsMultiply, variantsMultiply)
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

    override fun drawTest(n: Int, arr: List<Pair<String, Int>>, variant: List<Map<out String?, Int>>) {
        userList = UserList(loadDataUser("users.json"))
        var resPlus: Int = currentUser.resPlus
        var resMinus: Int = currentUser.resMinus
        var resMultiply: Int = currentUser.resMultiply
        when (n) {
            1 -> resPlus = 0
            2 -> resMinus = 0
            3 -> resMultiply = 0
        }
        var i = 0
        while (i < arr.size) {
            println(arr[i].first)
            val map = variant[i]
            for ((key, value) in map) {
                println("$key) $value")
            }
            val userAnswer: String? = readLine()
            if (userAnswer != "а" && userAnswer != "б" && userAnswer != "в" && userAnswer != "г") {
                println("Введіть 1 з літер: 'а', 'б', 'в', 'г'")
                continue
            }
            if (map[userAnswer] == arr[i].second) {
                when (n) {
                    1 -> {
                        resPlus += 20
                        currentUser.resPlus = resPlus
                    }

                    2 -> {
                        resMinus += 20
                        currentUser.resMinus = resMinus
                    }

                    3 -> {
                        resMultiply += 20
                        currentUser.resMultiply = resMultiply
                    }
                }
            }
            i++
        }
        saveResults(
            currentUser,
            userList.users,
            "users.json",
            currentUser.resPlus,
            currentUser.resMinus,
            currentUser.resMultiply
        )
        when (n) {
            1 -> println("Тест завершено! Ваша оцінка - ${currentUser.resPlus}")
            2 -> println("Тест завершено! Ваша оцінка - ${currentUser.resMinus}")
            3 -> println("Тест завершено! Ваша оцінка - ${currentUser.resMultiply}")
        }
        drawMenuTests()
    }

}

fun main() {
    val menu = Menu()
    menu.drawMenu()
}