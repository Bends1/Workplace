import kotlinx.serialization.Serializable

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