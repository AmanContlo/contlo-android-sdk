package com.contlo.androidsdk.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.contlo.androidsdk.api.ContloAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WorkScheduler(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {

            val contloAPI = ContloAPI(applicationContext)
            val prop = JSONObject()
            contloAPI.sendEvent("mobile_app_updated",prop)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
