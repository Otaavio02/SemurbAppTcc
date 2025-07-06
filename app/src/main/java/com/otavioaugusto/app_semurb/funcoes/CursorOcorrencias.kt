package com.otavioaugusto.app_semurb.utils

import android.database.Cursor

fun Cursor.getStringOrNull(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return if (index >= 0 && !isNull(index)) getString(index) else null
}