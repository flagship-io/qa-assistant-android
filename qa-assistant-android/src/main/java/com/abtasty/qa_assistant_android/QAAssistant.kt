package com.abtasty.qa_assistant_android

import android.content.Context
import com.abtasty.flagship.qa_assistant.QAAssistantCoreEventListener
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.lang.ref.WeakReference

//object QAAssistant {
//    private var contextWeakReference: WeakReference<Context>? = null
//    private var adapter: IQAAssistant? = null
//
//    fun open(context: Context) {
//        contextWeakReference = WeakReference(context)
//        adapter = initAdapter()
//    }
//
//    internal fun initAdapter(): IQAAssistant? {
//        return FlagshipQAAssistantAdapter().open(this)
//    }
//
//    internal fun onCoreModuleEvent(type: String, data: JSONObject) {
//        println("[QA ASSISTANT] CORE EVENT RECEIVED : $type -> $data")
//        when (type) {
//            "HIT" -> { /* Afficher le hit dans l'UI */
//            }
//
//            "FLAGS_FETCHED" -> { /* Mettre à jour les flags */
//            }
//        }
//    }
//
//    fun sendEventToCoreModule(type: String, data: JSONObject): JSONObject? {
//        return try {
//            adapter?.sendToCoreModule(type, data)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    fun close() {
//        adapter?.close()
//        adapter = null
//        contextWeakReference?.clear()
//    }
//
//}

object QAAssistant2 : IQAAssistant2 {

    private var contextWeakReference: WeakReference<Context>? = null
    private var adapter: IQAAssistant2? = null
    private var overlayButton: QAOverlayButton? = null

    fun open(context: Context) {
        contextWeakReference = WeakReference(context.applicationContext)
        setAdapter()
        open()
        showOverlay(context)
    }

    private fun setAdapter() {
        try {
            adapter = FlagshipQAAssistantAdapter2(this).initialize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showOverlay(context: Context) {
        if (overlayButton == null) {
            overlayButton = QAOverlayButton.create(context)
        }
    }

    override fun open() {
        adapter?.open()
    }

    override fun close() {
        overlayButton?.dismiss()
        overlayButton = null
        adapter?.close()
        adapter = null
        contextWeakReference?.clear()
        contextWeakReference = null
    }

    override fun onVisitorChanged(jsonVisitor: JSONObject) {

    }
}


//import android.content.Context
//import com.abtasty.flagship.main.Flagship
//import com.abtasty.flagship.qa_assistant.QAAssistantBridge
//import com.abtasty.flagship.qa_assistant.QAAssistantBridgeEvent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import java.lang.ref.WeakReference
//
//class QAAssistant() {
//    private var contextWeakReference : WeakReference<Context>? = null
//    private var scope: CoroutineScope? = null
//    private var eventsJob: Job? = null
//
//
//    /**
//     * Compatibility with old version of Flagship SDK
//     */
//    fun open(context: Context, visitor: com.abtasty.flagship.visitor.Visitor) {
//        open(context)
//    }
//
//    private fun open(context: Context) {
//        try {
//            println("[QA ASSISTANT] OPEN")
//            if (Flagship.getStatus() != Flagship.FlagshipStatus.INITIALIZED) {
//                println("[QA ASSISTANT] Flagship SDK is not initialized. Please call Flagship.start() before opening the QA Assistant.")
//                return
//            }
//            contextWeakReference = WeakReference(context)
//            QAAssistantBridge.initialize()
//            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//            eventsJob = scope?.launch {
//                QAAssistantBridge.qaAssistantCoreEvents?.collect { event ->
//                    when (event) {
//                        is QAAssistantBridgeEvent.QAAssistantSendHit -> {
//                            println("CORE SEND HIT : " + event.hit)
//                        }
//                        else -> {
//
//                        }
//                    }
//                }
//            }
//            QAAssistantBridge.tryEmitEvent(QAAssistantBridgeEvent.QAAssistantOpen())
//            println("[QA ASSISTANT] EMIT OPEN")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun close() {
//        contextWeakReference?.clear()
//    }
//}