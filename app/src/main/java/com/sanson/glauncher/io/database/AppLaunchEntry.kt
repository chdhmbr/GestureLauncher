package com.sanson.glauncher.io.database

import com.sanson.glauncher.processing.data.Gesture

data class AppLaunchEntry(
    val id: String,
    val appName: String,
    val packageName: String,
    val intentAction: String,
    val gesture: Gesture
)