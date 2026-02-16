package com.example.hazir.domain

import com.example.hazir.models.UserData
import com.example.hazir.utils.Result

interface UserRepository {
    suspend fun getUser() : Result<UserData>
}