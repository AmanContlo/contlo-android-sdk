package com.contlo.androidsdk.api

import android.content.Context
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author Aman
 * Base class for calling Http method,
 **/
class HttpClient {

    fun sendPOSTRequest(url: String, data: String): String {
        return sendPOSTRequest(Contlo.getContext(), url, data)
    }
    fun sendPOSTRequest(context: Context, url: String, data: String): String {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            addGlobalHeaders(context, connection)
            try {
                if(data.isNotBlank()) {
                    connection.doOutput = true
                    OutputStreamWriter(connection.outputStream).use {
                        it.write(data)
                    }
                }

                // Get response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_ACCEPTED ) {
                    val inputStream = connection.inputStream
                    val responseString = convertStreamToString(inputStream)
                    ContloUtils.printLog(context, TAG, "Response for url $url with body: $data :: Success code: $responseCode, Response: $responseString")
                    return responseString
                }


                else {
                    val errorStream = connection.errorStream
                    return if (errorStream != null) {
                        val error = convertStreamToString(errorStream)
                        ContloUtils.printLog(context, TAG, "Error: $error")
                        error
                    } else {
                        ContloUtils.printLog(context, TAG, "Error: HTTP error code $responseCode")
                        "Error: HTTP error code $responseCode"
                    }
                }
            } catch (IOE: IOException) {
                IOE.printStackTrace()
                throw IOE
            } finally {
                connection.disconnect()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    private fun addGlobalHeaders(context: Context, connection: HttpURLConnection) {
        connection.apply {
            setRequestProperty("accept", "application/json")
            setRequestProperty("X-API-KEY", ContloPreference.getInstance(context).getApiKey())
            setRequestProperty("content-type", "application/json")
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

    companion object {
        const val TAG = "HttpClient"
    }

}


