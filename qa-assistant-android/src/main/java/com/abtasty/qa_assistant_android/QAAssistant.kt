package com.abtasty.qa_assistant_android

import android.content.Context
import android.util.Log
//import com.abtasty.flagship.main.Flagship
import com.abtasty.flagship.qa_assistant.QAAssistantBridge
import com.abtasty.flagship.qa_assistant.QAAssistantBridgeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class QAAssistant() {
    private var contextWeakReference : WeakReference<Context>? = null
    private var scope: CoroutineScope? = null
    private var eventsJob: Job? = null


    fun open(context: Context) {
        try {
            println("[QA ASSISTANT] OPEN")
            if (Flagship.getStatus() != Flagship.FlagshipStatus.INITIALIZED) {
                println("[QA ASSISTANT] Flagship SDK is not initialized. Please call Flagship.start() before opening the QA Assistant.")
                return
            }
            contextWeakReference = WeakReference(context)
            QAAssistantBridge.initialize()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            eventsJob = scope?.launch {
                QAAssistantBridge.qaAssistantCoreEvents?.collect { event ->
                    when (event) {
                        is QAAssistantBridgeEvent.QAAssistantSendHit -> {
                            println("CORE SEND HIT : " + event.hit)
                        }
                        else -> {

                        }
                    }
                }
            }
            QAAssistantBridge.tryEmitEvent(QAAssistantBridgeEvent.QAAssistantOpen())
            println("[QA ASSISTANT] EMIT OPEN")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        contextWeakReference?.clear()
    }
}