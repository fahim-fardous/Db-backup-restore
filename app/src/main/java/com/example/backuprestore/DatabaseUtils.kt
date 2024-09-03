package com.example.backuprestore

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseUtils {
    fun backupDatabase(context: Context) {
        val db = AppDatabase.getInstance(context)
        db.close()
        val dbFile: File = context.getDatabasePath("TaskHive.db")
        val backupDir = File(Environment.getExternalStorageDirectory(), "Backup")
        val fileName =
            "Backup (${getDateTimeFromMillis(System.currentTimeMillis(), "dd-MM-yyyy-hh:mm")})"
        val path = backupDir.path + File.separator + fileName
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        val saveFile = File(path)
        if (saveFile.exists()) {
            saveFile.delete()
        }
        try {
            if (saveFile.createNewFile()) {
                val bufferSize = 8 * 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                val saveDb: OutputStream = FileOutputStream(path)
                val indDb: InputStream = FileInputStream(dbFile)
                do {
                    bytesRead = indDb.read(buffer, 0, bufferSize)
                    if (bytesRead < 0) {
                        break
                    }
                    saveDb.write(buffer, 0, bytesRead)
                } while (true)
                saveDb.flush()
                indDb.close()
                saveDb.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun restoreHelper(context: Context) {
        try {
            val fileUri = Uri.fromFile(File(generateBackupFilePath()))
            val inputStream = context.contentResolver.openInputStream(fileUri)
            println("restoring ")
            restoreDatabase(inputStream, context)
            inputStream?.close()
        } catch (e: IOException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    private fun restoreDatabase(inputStreamNewDB: InputStream?, context: Context) {
        val dbFile = context.getDatabasePath("TaskHive.db")

        // Ensure the database is closed before restoration
        AppDatabase.getInstance(context).close()

        if (inputStreamNewDB != null) {
            try {
                inputStreamNewDB.use { input ->
                    FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // After restoring, reinitialize the Room database
                AppDatabase.getInstance(context)  // Reinitialize Room with the restored database
                Log.d("LOGGER", "Database restored successfully")
            } catch (e: IOException) {
                Log.d("LOGGER", "Error during restoration: ${e.message}")
                e.printStackTrace()
            }
        } else {
            Log.d("LOGGER", "Restore - InputStream is null")
        }
    }


    fun deleteDatabase(context: Context) {
        AppDatabase.getInstance(context).close()
        context.deleteDatabase("TaskHive.db")
    }

    private fun getDateTimeFromMillis(
        millis: Long,
        pattern: String,
    ): String {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
        return simpleDateFormat.format(millis)
    }

    private fun generateBackupFilePath(): String {
        val sDir = File(Environment.getExternalStorageDirectory(), "Backup")
        val fileName =
            "Backup (${getDateTimeFromMillis(System.currentTimeMillis(), "dd-MM-yyyy-hh:mm")}).db"
        if (!sDir.exists()) {
            sDir.mkdirs()
        }
        return sDir.path + File.separator + fileName
    }

    @Throws(IOException::class)
    private fun copyFile(
        fromFile: FileInputStream,
        toFile: FileOutputStream,
    ) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            fromChannel?.close()
            toChannel?.close()
        }
    }
}
