package com.bca.music.util

import android.app.Activity
import android.content.Context

class SharedPreferences {
    companion object {
        fun save(context: Context, trackId: String) {
            val sharedPreference = (context as Activity).getSharedPreferences("BCA",Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()

            editor.putString("trackId", trackId)
            editor.apply()
        }

        fun read(context: Context): String? {
            val sharedPreference = (context as Activity).getSharedPreferences("BCA",Context.MODE_PRIVATE)

            return sharedPreference.getString("trackId", "")
        }

        fun clear(context: Context) {
            val sharedPreference = (context as Activity).getSharedPreferences("BCA",Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()

            editor.clear()
            editor.remove("trackId")
            editor.apply()
        }
    }
}
