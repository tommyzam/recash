package com.google.firebase.ml.md.kotlin

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        val item:String,
        val points: Int)