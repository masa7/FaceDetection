package com.example.facedetection.utils

import android.util.Log
import com.example.facedetection.utils.SingletonContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime

class FileUtils(fileName: String) {
    private var file: File
    private var fn: String

    init {
        // for data export
        val ctx = SingletonContext.applicationContext()
        fn = fileName
        file = File(ctx.filesDir, fileName)
    }

    fun saveFile(str: String?){
        try{
            FileWriter(file, true).use { writer -> writer.write(str) }
        }catch (e: IOException){
            Log.d("Error", "Error occurs in $fn")
        }
    }

    fun readFile(): String? {
        var text: String? = null
        try{
            BufferedReader(FileReader(file)).use { br -> text = br.readLine() }
        }catch (e: IOException){
            e.printStackTrace()
        }
        return text
    }
}