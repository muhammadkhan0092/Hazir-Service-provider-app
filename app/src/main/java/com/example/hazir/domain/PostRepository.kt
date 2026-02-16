package com.example.hazir.domain

import com.example.hazir.models.DataPost
import com.example.hazir.utils.Result

interface PostRepository {
    suspend fun updatePost(post: DataPost) : Result<Unit>
}