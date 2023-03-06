package com.contlo.contlosdk

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    fun sendRequest(url: String, headers: HashMap<String, String>, params: JSONObject?, method: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method

        // Add headers
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }

        // Add parameters (for POST requests)
        if (method == "POST" && params != null) {
            connection.doOutput = true
            val postData = params.toString()
            OutputStreamWriter(connection.outputStream).use {
                it.write(postData)
            }
        }

        // Get response
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_ACCEPTED ) {
            val inputStream = connection.inputStream
            val response = convertStreamToString(inputStream)
            println("Response: $response")
            return response
        } else {
            val errorStream = connection.errorStream
            if (errorStream != null) {
                val error = convertStreamToString(errorStream)
                println("Error: $error")
                return error
            } else {
                println("Error: HTTP error code $responseCode")
                return "Error: HTTP error code $responseCode"
            }
        }
    }


    private fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }
        reader.close()
        return stringBuilder.toString()
    }

}


