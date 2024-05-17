/*
package com.example.rosavtodorproject2.domain.useCases

import androidx.lifecycle.LiveData
import com.example.rosavtodorproject2.data.models.User
import com.example.rosavtodorproject2.data.repositories.UserRepository
import javax.inject.Inject

class UsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    val currentUser: LiveData<User> = userRepository.currentUser
    val contacts: LiveData<List<User>> = userRepository.userContacts

    fun setNewNameToCurrentUser(newCurrentUserName:String){
        userRepository.setNewNameToCurrentUser(newCurrentUserName)
    }
}*/
