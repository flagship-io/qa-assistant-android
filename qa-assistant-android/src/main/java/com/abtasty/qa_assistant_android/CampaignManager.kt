package com.abtasty.qa_assistant_android

import android.content.Context
import android.content.SharedPreferences
import com.abtasty.flagship.model.Campaign
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.edit
import com.abtasty.flagship.utils.FlagshipConstants
import com.abtasty.flagship.utils.FlagshipLogManager
import com.abtasty.flagship.utils.LogManager
import org.json.JSONObject

class CampaignManager {

    private data class CampaignsHttpResponse(
        val code: Int,
        val body: String?,
        val errorBody: String?,
        val lastModified: String?,
    )


    companion object {
        private const val PREFS_NAME = "qa_assistant_prefs"
        private const val KEY_LAST_MODIFIED = "campaigns_last_modified"
        private const val KEY_CACHED_CAMPAIGNS = "campaigns_cached_json"
        private const val BUCKETING_BASE_URL = "https://cdn.flagship.io/%s/bucketing.json"
        private const val TIMEOUT_CONNECT = 5000 // 10 secondes
        private const val TIMEOUT_READ = 15000 // 15 secondes

    }

    private var refContext: WeakReference<Context>? = null

    private var prefs: SharedPreferences? = null

    private var campaigns: ArrayList<Campaign>? = null

    suspend fun parseBucketingFile(envId: String) {
        try {
            val url = BUCKETING_BASE_URL.format(envId)
            val response = sendBucketingFileHttpRequest(url)

            when (response.code) {
                HttpURLConnection.HTTP_OK -> { // 200
                    println("[QA ASSISTANT] Campaigns downloaded successfully")
                    response.body?.let { jsonBody ->
                        campaigns = parseCampaigns(jsonBody)
                        saveCacheHeaders(response.lastModified)
                        saveCachedCampaigns(jsonBody)
                        campaigns
                    }
                }

                HttpURLConnection.HTTP_NOT_MODIFIED -> { // 304
                    println("[QA ASSISTANT] Campaigns not modified, using cached version")
                    campaigns = loadCachedCampaigns()
                    campaigns

                }

                else -> {
                    println("[QA ASSISTANT] Error fetching campaigns: ${response?.code}")
                    null
                }
            }
        } catch (e: IOException) {
            println("[QA ASSISTANT] Network error while fetching campaigns: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            println("[QA ASSISTANT] Error fetching campaigns: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun sendBucketingFileHttpRequest(urlString: String): CampaignsHttpResponse {
        var connection: HttpURLConnection? = null

        try {
            val url = URL(urlString)
            println("URL = " + url)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_CONNECT
                readTimeout = TIMEOUT_READ
                setRequestProperty("Content-Type", "application/json")

                System.getProperty("http.agent")?.let {
                    setRequestProperty("User-Agent", it)
                }
                setRequestProperty("If-Modified-Since", loadCacheHeaders())
            }

            val responseCode = connection.responseCode
            val body = when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    connection.inputStream.bufferedReader().use { it.readText() }
                }

                else -> null
            }

            val errorBody = when {
                responseCode >= 400 -> {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                }

                else -> null
            }

            println("[QA ASSISTANT] Campaigns HTTP response: $errorBody")
            return CampaignsHttpResponse(
                code = responseCode,
                body = body,
                errorBody = errorBody,
                lastModified = connection.getHeaderField("Last-Modified"),
            )
        } finally {
            connection?.disconnect()
        }
    }

    fun initPreferences() {
        prefs = refContext?.get()?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun saveCacheHeaders(lastModified: String?) {
        prefs?.edit {
            lastModified?.let {
                putString(KEY_LAST_MODIFIED, it)
                println("[QA ASSISTANT] Saved Last-Modified: $it")
            }
        }
    }

    private fun saveCachedCampaigns(jsonBody: String) {
        prefs?.edit {
            putString(KEY_CACHED_CAMPAIGNS, jsonBody)
            println("[QA ASSISTANT] Campaigns cached successfully")
        }
    }

    private fun loadCacheHeaders(): String? {
        return prefs?.getString(KEY_LAST_MODIFIED, null)?.let { lastModified ->
            println("[QA ASSISTANT] Sending If-Modified-Since: $lastModified")
            lastModified
        }
    }

    private fun loadCachedCampaigns(): ArrayList<Campaign>? {
        val cachedJson = prefs?.getString(KEY_CACHED_CAMPAIGNS, null)
        return if (cachedJson != null) {
            println("[QA ASSISTANT] Loading campaigns from cache")
            parseCampaigns(cachedJson)
        } else {
            println("[QA ASSISTANT] No cached campaigns found")
            null
        }
    }


    fun parseCampaigns(jsonBody: String): ArrayList<Campaign>? {
        if (jsonBody.isNotEmpty()) {
            try {
                val json = JSONObject(jsonBody)
                val panic = json.has("panic")
                if (!panic) return Campaign.parse(json.getJSONArray("campaigns"))
            } catch (e: Exception) {
                FlagshipLogManager.log(
                    FlagshipLogManager.Tag.PARSING,
                    LogManager.Level.ERROR,
                    FlagshipConstants.Errors.PARSING_CAMPAIGN_ERROR
                )
            }
        }
        return null
    }

    suspend fun updateCampaigns(context: Context, envId: String): ArrayList<Campaign>? {
        refContext = WeakReference(context)
        initPreferences()
        parseBucketingFile(envId)
        return campaigns
    }


}