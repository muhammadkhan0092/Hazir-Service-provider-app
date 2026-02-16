package com.example.hazir.domain

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.hazir.models.GigData
import com.example.hazir.utils.Resource
import com.example.hazir.utils.Result
import kotlinx.coroutines.launch

interface CategoryRepository {
    suspend fun getSpecificCategoryDetail(category: String): Result<List<GigData>>
    suspend fun getDistinctCategories(): Result<List<String>>
}