package com.abtasty.qa_assistant_android

import androidx.compose.ui.res.colorResource
import com.abtasty.flagship.hits.Hit
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.qa_assistant.QAAssistantBridge
import com.abtasty.flagship.qa_assistant.QAAssistantBridge2
import com.abtasty.flagship.qa_assistant.QAAssistantBridgeEvent
import com.abtasty.flagship.qa_assistant.QAAssistantCoreEventListener
import com.abtasty.flagship.qa_assistant.QAAssistantEventListener
import com.abtasty.flagship.visitor.VisitorDelegateDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

//class FlagshipQAAssistantAdapter: IQAAssistant {
//
//    private var coreEventListener: QAAssistantCoreEventListener? = null
//    private var qaAssistantEventListener: QAAssistantEventListener? = null
//
//    override fun open(qaAssistant: QAAssistant): IQAAssistant? {
//        return try {
//            coreEventListener = QAAssistantCoreEventListener { event: String, content: JSONObject ->
//                qaAssistant.onCoreModuleEvent(event, content)
//                onCoreModuleEvent(event, content)
//                null
//            }
//            coreEventListener?.let { listener ->
//                //Set listener in core module to get all message from Core Module
//                QAAssistantBridge.registerQAAssistantCoreEventListener(listener)
//            }
//            qaAssistantEventListener = QAAssistantBridge.getQAAssistantEventListener()
//            sendToCoreModule("QA_OPEN", JSONObject())
//            this
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    /**
//     * Core module -> QA Assistant module Communication
//     */
//    override fun onCoreModuleEvent(event: String, content: JSONObject) {
//
//    }
//
//    /**
//     * QA Assistant module -> Core module Communication
//     */
//    override fun sendToCoreModule(
//        event: String,
//        content: JSONObject
//    ): JSONObject? {
//        qaAssistantEventListener?.onEvent(event, content)
//        return null
//    }
//
//    override fun close() {
//        coreEventListener?.let { QAAssistantBridge.unregisterQAAssistantCoreEventListener() }
//        coreEventListener = null
//        QAAssistantBridge.reset()
//    }
//}

class FlagshipQAAssistantAdapter2(val qaAssistant: QAAssistant2) : IQAAssistant2 {

    var _qaAssistantMutableSharedFlow: MutableSharedFlow<QAAssistantBridgeEvent>? = null
    private var collectionJob: Job? = null


//    val _qaAssistantSharedFlow: SharedFlow<QAAssistantBridgeEvent>?
//    get() = _qaAssistantMutableSharedFlow?.asSharedFlow()

    override fun open(): IQAAssistant2 {
        try {
            if (_qaAssistantMutableSharedFlow != null && collectionJob?.isActive == true) {
                println("#QA [QA ASSISTANT] Adapter already initialized, skipping")
            } else {
                _qaAssistantMutableSharedFlow = QAAssistantBridge2.initialize()
                collectionJob = QAAssistantBridge2.coroutineScope?.launch {
                    println("#QA ASSISTANT COLLECTER INIT")
                    _qaAssistantMutableSharedFlow?.collect { event ->
                        when (event) {
                            is QAAssistantBridgeEvent.QAAssistantSendHit -> this@FlagshipQAAssistantAdapter2.onHitEmitted(
                                event.hit
                            )

                            is QAAssistantBridgeEvent.QAAssistantVisitorUpdated -> this@FlagshipQAAssistantAdapter2.onVisitorChanged(
                                event.visitorDelegateDTO,
                                event.campaings
                            )

                            else -> {
                                println("#QA [QA ASSISTANT] Adapter2 RECEIVED EVENT : $event")
                            }
                        }
                    }
                }
//                collectionJob?.start()
                println("#QA ASSISTANT COLLECTER EMIT OPEN")
                _qaAssistantMutableSharedFlow?.tryEmit(QAAssistantBridgeEvent.QAAssistantOpen())
            }
        } catch (e: Exception) {
        }
        return this
    }

    override fun close() {
        _qaAssistantMutableSharedFlow?.tryEmit(QAAssistantBridgeEvent.QAAssistantClose())
        collectionJob?.cancel()

    }

    override fun onVisitorChanged(visitorDelegateDTO: VisitorDelegateDTO, campaigns: List<Campaign>?) {
        qaAssistant.onVisitorChanged(visitorDelegateDTO, campaigns)
    }

    override fun onHitEmitted(hit: Hit.HitDTO) {
        qaAssistant.onHitEmitted(hit)
    }
}