package com.dhirajmane.splitcrew

import android.content.Context
import com.dhirajmane.splitcrew.data.AppDatabase
import com.dhirajmane.splitcrew.data.GroupRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.getDatabase(context)

    val groupRepository: GroupRepository by lazy {
        GroupRepository(database.groupDao())
    }
}