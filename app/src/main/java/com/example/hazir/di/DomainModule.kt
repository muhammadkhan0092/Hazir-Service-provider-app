package com.example.hazir.di

import com.example.hazir.data.repository.FirebaseCategoryRepository
import com.example.hazir.data.repository.FirebaseChatRepository
import com.example.hazir.data.repository.FirebaseGigDetailRepository
import com.example.hazir.data.repository.FirebasePostRepository
import com.example.hazir.data.repository.FirebaseUserRepository
import com.example.hazir.domain.CategoryRepository
import com.example.hazir.domain.ChatRepository
import com.example.hazir.domain.GigDetailRepository
import com.example.hazir.domain.PostRepository
import com.example.hazir.domain.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun providesCategoryRepo(repo : FirebaseCategoryRepository) : CategoryRepository{
        return repo
    }
    @Provides
    @Singleton
    fun providesChatRepository(repo : FirebaseChatRepository) : ChatRepository{
        return repo
    }
    @Provides
    @Singleton
    fun providesGigDetailRepository(repo : FirebaseGigDetailRepository) : GigDetailRepository{
        return repo
    }
    @Provides
    @Singleton
    fun providesPostRepository(repo : FirebasePostRepository) : PostRepository{
        return repo
    }
    @Provides
    @Singleton
    fun providesUserRepo(repo : FirebaseUserRepository) : UserRepository{
        return repo
    }
}