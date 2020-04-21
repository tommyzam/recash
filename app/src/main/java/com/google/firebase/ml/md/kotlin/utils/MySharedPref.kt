package com.google.firebase.ml.md.kotlin.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPref private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    fun saveUserId(id: String?) {
        editor.putString(USER_ID, id).commit()
    }

    val userId: String?
        get() = sharedPreferences.getString(USER_ID, null)

    companion object {
        private const val MY_PREF_NAME = "recash"
        private const val USER_ID = "user_id"
        private var mySharedPref: MySharedPref? = null
        @Synchronized
        fun getInstance(context: Context): MySharedPref? {
            if (mySharedPref == null) mySharedPref = MySharedPref(context)
            return mySharedPref
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
}