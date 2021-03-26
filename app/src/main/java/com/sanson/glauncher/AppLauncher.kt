package com.sanson.glauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sanson.glauncher.io.database.AppLaunchEntry

class AppLauncher(private val context: Context) {

    fun launch(launchEntry: AppLaunchEntry) {
        val intent = Intent().apply {
            component = ComponentName(launchEntry.packageName, launchEntry.intentAction)
        }
        context.startActivity(intent)
    }
}
