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

enum class Operations{
    PLUS,
    MINUS,
    MULTIPLY
}