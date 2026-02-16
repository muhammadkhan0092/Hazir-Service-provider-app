package com.example.hazir.data.sources

import com.example.hazir.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteAuthSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun createUserWithEmailAndPassword(
        email : String,
        password : String
    ) : Result<String>{
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId= result.user?.uid
            if(userId==null) Result.Error("") else Result.Success(userId)
        }
        catch (e : Exception){
            Result.Error("")
        }
    }
    suspend fun deleteUser(uid: String?) : Result<Unit> {
        val currentUser = firebaseAuth.currentUser
        return if(currentUser!=null){
            try {
                currentUser.delete().await()
                Result.Success(Unit)
            }
            catch (e : Exception){
                Result.Error("Error Deleting User")
            }
        }
        else{
            Result.Error("No User Exist")
        }
    }


    fun signOut() = firebaseAuth.signOut()
    fun getUserId() = firebaseAuth.currentUser?.uid?:""
    fun isUerLoggedIn(): Boolean = firebaseAuth.currentUser != null
}