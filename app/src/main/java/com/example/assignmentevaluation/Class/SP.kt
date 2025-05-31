package com.example.assinment_evaluation.Class

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

class SP(private var context: Context) {
    private var sharedPreferences = context.getSharedPreferences("AEPreferences", MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun addS(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun addB(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getS(key: String): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun getB(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }


}
