package com.example.hazir.utils

suspend fun <T> firebaseListSafeCall(
    action: suspend () -> List<T>
) : Result<List<T>> {
    return try {
        Result.Success(action())
    }
    catch (e : Exception){
        Result.Error("")
    }
}
suspend fun <T> firebaseGetSafeCall(
    action: suspend () -> T
) : Result<T> {
    return try {
        Result.Success(action())
    }
    catch (e : Exception){
        Result.Error("")
    }
}

suspend fun  firebaseUpsertSafeCall(
    action: suspend () -> Unit
) : Result<Unit>{
    return try {
        Result.Success(action())
    }
    catch (e : Exception){
        Result.Error("")
    }
}