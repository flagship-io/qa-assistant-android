package com.abtasty.qa_assistant_android

import org.json.JSONObject

//interface IQAAssistant {
//
//    fun open(qaAssistant: QAAssistant): IQAAssistant?
//    fun onCoreModuleEvent(event: String, content: JSONObject)
//    fun sendToCoreModule(event: String, content: JSONObject): JSONObject?
//    fun close()
//}

interface IQAAssistant2 {
    fun initialize(): IQAAssistant2? {
        return null
    }
    fun open()
    fun close()

    fun onVisitorChanged(jsonVisitor: JSONObject) {}
    fun onHitEmitted(jsonHit: JSONObject) {}
    fun onFlagsUpdated(flags: JSONObject) {}
}