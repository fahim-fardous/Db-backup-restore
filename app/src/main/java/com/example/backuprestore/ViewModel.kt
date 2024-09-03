package com.example.backuprestore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewModel:ViewModel() {
    private val _state = MutableStateFlow<List<Data>>(emptyList())
    val state = _state.asStateFlow()

    fun insertData(data: Data, context: Context) = viewModelScope.launch{
        val db = AppDatabase.getInstance(context).dataDao().insert(data)
        getData(context)
    }

    fun getData(context: Context) = viewModelScope.launch{
        val db = AppDatabase.getInstance(context).dataDao().getAll()
        _state.value = db
    }

    fun backupDatabase(context: Context) {
        DatabaseUtils.backupDatabase(context)
        // Show success message
    }

    fun restoreDatabase(context: Context) = viewModelScope.launch{
        DatabaseUtils.restoreHelper(context)
        val db = AppDatabase.getInstance(context).dataDao().getAll()
        _state.value = db
        // Show success message
    }

    fun deleteDatabase(context: Context) = viewModelScope.launch{
        DatabaseUtils.deleteDatabase(context)
        val db = AppDatabase.getInstance(context).dataDao().getAll()
        _state.value = db
        // Show success message
    }



    private fun getDateTimeFromMillis(millis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
