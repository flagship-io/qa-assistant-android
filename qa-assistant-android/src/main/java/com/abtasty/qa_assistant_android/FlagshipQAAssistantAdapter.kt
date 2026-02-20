package com.abtasty.qa_assistant_android

import com.abtasty.flagship.qa_assistant.QAAssistantBridge
import com.abtasty.flagship.qa_assistant.QAAssistantBridge2
import com.abtasty.flagship.qa_assistant.QAAssistantBridgeEvent
import com.abtasty.flagship.qa_assistant.QAAssistantCoreEventListener
import com.abtasty.flagship.qa_assistant.QAAssistantEventListener
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

class FlagshipQAAssistantAdapter2(val qaAssistant: QAAssistant2): IQAAssistant2 {

    var _qaAssistantMutableSharedFlow: MutableSharedFlow<QAAssistantBridgeEvent>? = null

    val _qaAssistantSharedFlow: SharedFlow<QAAssistantBridgeEvent>?
    get() = _qaAssistantMutableSharedFlow?.asSharedFlow()

    override fun initialize(): IQAAssistant2? {
        try {
            _qaAssistantMutableSharedFlow = QAAssistantBridge2.initialize()
            QAAssistantBridge2.coroutineScope.launch {
                _qaAssistantMutableSharedFlow?.collect { event ->
                    when (event) {
                        is QAAssistantBridgeEvent.QAAssistantSendHit -> qaAssistant.onHitEmitted(event.hit)
                        else -> {
                            println("#QA [QA ASSISTANT] Adapter2 RECEIVED EVENT : $event")
                        }
                    }
                }
            }
            return this
        } catch (e: Exception) {
            return null
        }
    }

    override fun open() {
        _qaAssistantMutableSharedFlow?.tryEmit(QAAssistantBridgeEvent.QAAssistantOpen())
    }

    override fun close() {
        _qaAssistantMutableSharedFlow?.tryEmit(QAAssistantBridgeEvent.QAAssistantClose())
    }


}