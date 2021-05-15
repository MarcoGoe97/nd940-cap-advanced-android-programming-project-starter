package com.example.android.politicalpreparedness.util


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val message: String?, val statusCode: Int? = null) :
        Result<Nothing>()
}

/**
 * `true` if [Result] is of type [Result.Success] & holds non-null [Result.Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success