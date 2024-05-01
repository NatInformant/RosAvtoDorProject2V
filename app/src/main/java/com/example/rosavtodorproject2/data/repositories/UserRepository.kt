package com.example.rosavtodorproject2.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rosavtodorproject2.data.dataSource.AdvertisementsDataSourceHardCode
import com.example.rosavtodorproject2.data.models.User
import com.example.rosavtodorproject2.ioc.AppComponentScope
import javax.inject.Inject

@AppComponentScope
class UserRepository @Inject constructor(
    val dataSource: AdvertisementsDataSourceHardCode
) {
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>  = _currentUser

    private val _userContacts = MutableLiveData<List<User>>(emptyList())
    val userContacts: LiveData<List<User>> = _userContacts
    fun setNewNameToCurrentUser(newCurrentUserName:String){
        AdvertisementsDataSourceHardCode.currentUser.name=newCurrentUserName;
        updateCurrentUser()
    }
    fun updateUsers() {
        _userContacts.value = dataSource.loadUserContacts()
    }
    fun updateCurrentUser(){
        _currentUser.value = AdvertisementsDataSourceHardCode.currentUser
    }
}