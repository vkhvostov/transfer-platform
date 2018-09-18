package test.interview.config

/**
 * Created on 18.09.18
 * TODO: Add comment
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A? = null): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                arg ?: throw NotInitializedException("First should be invoked getInstance with argument")
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

class NotInitializedException(message: String): Exception(message)