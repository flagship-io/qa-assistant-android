package com.abtasty.qa_assistant_android

import com.abtasty.flagship.hits.Hit
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.visitor.VisitorDelegateDTO

//interface IQAAssistant {
//
//    fun open(qaAssistant: QAAssistant): IQAAssistant?
//    fun onCoreModuleEvent(event: String, content: JSONObject)
//    fun sendToCoreModule(event: String, content: JSONObject): JSONObject?
//    fun close()
//}

interface IQAAssistant2 {
    fun open(): IQAAssistant2 { return this }
    fun close()

    fun onVisitorChanged(visitorDelegateDTO: VisitorDelegateDTO, campaigns: List<Campaign>? = null)
    fun onHitEmitted(hit: Hit.HitDTO)
//    fun onFlagsUpdated(flags: JSONObject) {}
}