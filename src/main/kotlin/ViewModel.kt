import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface Draw {
    fun drawMenu()
    fun drawAccOptions()
    fun drawUserInterface()
    fun drawMenuTests()
    fun drawEnter()
    fun drawTest(questions: Pair<List<Pair<String, Int>>, List<Map<out String?, Int>>>, operand : Operations)
}

interface UserData {
    fun loadDataUser(fileName: String): MutableList<User>
    fun createUser(name: String?, password: String?, email: String?, list: MutableList<User>): User
    fun addUser(user: User, users: MutableList<User>)
    fun saveUserData(
        user: User,
        userList: MutableList<User>,
        fileName: String,
        operationType: String,
        newValue: String?
    )

    fun getUsers(): MutableList<User>
    fun getUser(email: String?, password: String?, list: MutableList<User>): User
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
    fun onEmailChange(user: User)
    fun onPasswordChange(user: User)
    fun onChekedValid(input: String?, value: String, userList: MutableList<User>) : Boolean
}

interface TestsRepository {
    fun getQustions(operand: Operations): Pair<List<Pair<String, Int>>, List<Map<out String?, Int>>>
}

class TestsRepositoryImpl : TestsRepository {
    override fun getQustions(operand: Operations): Pair<List<Pair<String, Int>>, List<Map<out String?, Int>>> {
        when (operand) {
            Operations.PLUS -> {
                return Pair(
                    listOf(
                        Pair("Скільки буде 2 + 2?", 4),
                        Pair("Скільки буде 5 + 3?", 8),
                        Pair("Яка сума 10 і 5?", 15),
                        Pair("Яка сума 6 і 4?", 10),
                        Pair("Скільки буде 24 + 4?", 28),
                    ),
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
                )
            }

            Operations.MINUS -> {
                return Pair(
                    listOf(
                        Pair("Скільки буде 2 - 2", 0),
                        Pair("Скільки буде 5 - 3?", 2),
                        Pair("Яка різниця між 10 і 5?", 5),
                        Pair("Відніміть від 93 число 25", 68),
                        Pair("Скільки буде 24 - 4?", 20),
                    ),
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
                )
            }

            Operations.MULTIPLY -> {
                return Pair(
                    listOf(
                        Pair("Скільки буде 2 * 2", 4),
                        Pair("Скільки буде 5 * 3?", 15),
                        Pair("Обчисліть 10 * 5", 50),
                        Pair("Скільки буде 6 * 4?", 24),
                        Pair("Скільки буде 3 * 33?", 99),
                    ),
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
                )
            }
        }
    }
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

    override fun createUser(name: String?, password: String?,email : String?, list: MutableList<User>): User {
        return User(name, password, email, list.size - 1)
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
                        it.copy(name = newValue ?: it.name, password = user.password, email = user.email)
                    } else {
                        it
                    }
                }

                "password" -> {
                    if (it.id == user.id) {
                        it.copy(password = newValue ?: it.password, name = user.name, email = user.email)
                    } else {
                        it
                    }
                }
                "email" -> {
                    if (it.id == user.id) {
                        it.copy(email = newValue ?: it.email, name = user.name, password = user.password)
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

    override fun getUser(email: String?, password: String?, list: MutableList<User>): User {
        return list.find { it.email == email && it.password == password } ?: User()
    }

    override fun getUsers(): MutableList<User> {
        return UserList(loadDataUser("users.json")).users
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
                    email = user.email,
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
    override fun onChekedValid(input: String?, value: String, userList: MutableList<User>) : Boolean {
        if (input!!.contains(" ") || input == "") {
            if (value == "name") {
                println("Ім'я не повинно містити пробілів чи бути порожнім")
                return false
            } else if (value == "password"){
                println("Пароль не повинен бути порожнім чи містити пробіли")
                return false
            }
        }
        if(value == "email"){
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
            if(!emailRegex.matches(input)){
                println("Такої елетронної адреси не існує")
                return false
            }
            return true
        }
        return true
    }

    override fun registration(userList: MutableList<User>) {
        var name : String?
        var password : String?
        var email : String?
        do{
            println("Введіть своє ім'я: ")
            name = readLine()
        }while(!onChekedValid(name, "name", userList))
        do{
            var isValid = true
            println("Введіть свій емейл: ")
            email = readln()
            userList.forEach {
                if (email == it.email) {
                    println("Електронна адреса вже зайнята")
                    isValid = false
                }
            }
        }while(!onChekedValid(email, "email", userList) || !isValid)
        do{
            println("Введіть свій пароль: ")
            password = readLine()
        }while(!onChekedValid(password, "password", userList))
        val user = createUser(name, password, email, userList)
        addUser(user, userList)
        println("Ви зареєструвалися ваше ім'я: ${user.name}, ваша елетронна адреса: ${user.email}, ваш пароль: ${user.password}")
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
            } else isNameAvailable = true
        } while (!isNameAvailable)

        user.name = name
        println("Ваше нове ім'я - $name")
        saveUserData(user, userList, "users.json", "name", name)
    }
    override fun onEmailChange(user: User) {
        val userList = getUsers()
        var isNameAvailable: Boolean
        var email: String?

        do {
            println("Введіть свою нову електронну адресу: ")
            email = readLine()
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
            if(email == user.email){
                break
            }
            if(!email?.let { emailRegex.matches(it) }!!){
                println("Такої елетронної адреси не існує")
                isNameAvailable = false
            } else {
                isNameAvailable = true
                for (i in userList.indices) {
                    if (email == userList[i].email) {
                        println("Електронна адреса вже зайнята")
                        isNameAvailable = false
                        break
                    }
                }
            }
        } while (!isNameAvailable)

        user.email = email
        println("Ваша нова електронна адреса - $email")
        saveUserData(user, userList, "users.json", "email", email)
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
    private val testRepository = TestsRepositoryImpl()
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
        val (enterEmail, enterPassword) = getUserInput()
        userList = userData.getUsers()
        val list = userList
        if (list.isNotEmpty()) {
            currentUser = userData.getUser(enterEmail, enterPassword, userList)
            if (currentUser != User()) {
                println("Вітаю ${currentUser.name}!")
                drawUserInterface()
            } else {
                println("Електронна адреса чи пароль введені неправильно, спробуйте ще раз")
                drawMenu()
            }
        } else {
            println("У вас немає аккаунтів, спочатку зареєструйтеся")
            userInterface.registration(userList)
            drawEnter()
        }
    }

    private fun getUserInput(): Pair<String?, String?> {
        println("Введіть вашу електронну адресу: ")
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
                        "Щоб змінити свій емеіл натисніть - 3\n" +
                        "Щоб вийти з своєї сессії натисніть - 4\n" +
                        "Назад - 5"
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
                    userInterface.onEmailChange(currentUser)
                    drawAccOptions()
                }

                4 -> {
                    drawMenu()
                }

                5 -> drawUserInterface()
                else -> {
                    println("Введіть корректне значення")
                }
            }
        } while (userInput != "1" && userInput != "2" && userInput != "3" && userInput != "4" && userInput != "5")
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
                "1" -> {
                    val questions = testRepository.getQustions(Operations.PLUS)
                    drawTest(questions, Operations.PLUS)
                }
                "2" -> {
                    val questions = testRepository.getQustions(Operations.MINUS)
                    drawTest(questions, Operations.MINUS)
                }
                "3" -> {
                    val questions = testRepository.getQustions(Operations.MULTIPLY)
                    drawTest(questions, Operations.MULTIPLY)
                }
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

    private fun showResult(operand : Operations) {
        val testFinal = "Тест завершено! Ваша оцінка -"
        when (operand) {
            Operations.PLUS -> println("$testFinal ${currentUser.resPlus}")
            Operations.MINUS -> println("$testFinal ${currentUser.resMinus}")
            Operations.MULTIPLY -> println("$testFinal ${currentUser.resMultiply}")
        }
    }

    override fun drawTest(questions : Pair<List<Pair<String, Int>>, List<Map<out String?, Int>>>, operand : Operations) {
        val userList = userData.getUsers()
        var resPlus = currentUser.resPlus
        var resMinus = currentUser.resMinus
        var resMultiply = currentUser.resMultiply

        when (operand) {
            Operations.PLUS -> resPlus = 0
            Operations.MINUS -> resMinus = 0
            Operations.MULTIPLY -> resMultiply = 0
        }
        questions.first.forEachIndexed { index, pair ->
            println(pair.first)
            questions.second[index].forEach { (key, value) ->
                println("$key) $value")
            }

            var userAnswer: String?
            do {
                userAnswer = readLine()
                if (userAnswer !in listOf("а", "б", "в", "г")) {
                    println("Введіть одну з літер: 'а', 'б', 'в', 'г'")
                }
            } while (userAnswer !in listOf("а", "б", "в", "г"))

            if (questions.second[index][userAnswer] == questions.first[index].second) {
                when (operand) {
                    Operations.PLUS -> resPlus += 20
                    Operations.MINUS -> resMinus += 20
                    Operations.MULTIPLY -> resMultiply += 20
                }
            }
        }

        currentUser.resPlus = resPlus
        currentUser.resMinus = resMinus
        currentUser.resMultiply = resMultiply

        userData.saveResults(currentUser, userList, "users.json", resPlus, resMinus, resMultiply)
        showResult(operand)
        drawMenuTests()
    }
}
