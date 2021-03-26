package com.sanson.glauncher

class AppInformationListEntry(
    val appName: String,
    val packageName: String,

    val intentAction: String
) {

    override fun toString(): String {
        return appName
    }
}