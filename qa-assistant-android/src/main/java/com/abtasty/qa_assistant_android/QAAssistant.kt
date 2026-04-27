package com.abtasty.qa_assistant_android

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.abtasty.flagship.hits.Hit
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.qa_assistant.QAAssistantBridge
import com.abtasty.flagship.visitor.VisitorDelegateDTO
import com.abtasty.qa_assistant_android.ui.navigation.QAAContentNavigator
import com.abtasty.qa_assistant_android.ui.navigation.savedScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

object QAAssistant2 : IQAAssistant2, LifecycleEventObserver {

    private var contextWeakReference: WeakReference<Context>? = null

    internal var envID: String? = null

    private var _coroutineScope: CoroutineScope? = null
    internal val coroutineScope: CoroutineScope
        get() = _coroutineScope ?: throw IllegalStateException("QAAssistant n'est pas initialisé. Appelez open() d'abord.")

    private var adapter: IQAAssistant2? = null
    private var overlayButton: QAOverlayButton? = null

    internal val campaignManager = CampaignManager()
    internal var currentVisitor: VisitorDelegateDTO? = null

    internal val hitManager = HitManager()

    fun open(context: Context, envID: String) {
        contextWeakReference = WeakReference(context.applicationContext)
        this.envID = envID
        initializeCoroutineScope()
        _coroutineScope?.launch(Dispatchers.Main) {
            println("CT >> " + Thread.currentThread().name)
            bindToApplicationLifecycle()
        }
        setAdapter()
//        _coroutineScope?.launch {
//            val campaignsUpdated = context.let {
//                campaignManager.updateCampaigns(it, envID)
//            }
//            println("[QA ASSISTANT] Campaigns updated : $campaignsUpdated")
//        }
//        setAdapter()
//        open()
        showOverlay(context)
    }

    private fun initializeCoroutineScope() {
        if (_coroutineScope == null) {
            _coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }

    private fun bindToApplicationLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (_coroutineScope == null) {
                    initializeCoroutineScope()
                }
            }
            Lifecycle.Event.ON_STOP -> {
                // L'application passe en arrière-plan
                // Vous pouvez choisir de garder le scope actif ou de le suspendre
            }
            Lifecycle.Event.ON_DESTROY -> {

                _coroutineScope?.cancel()
                _coroutineScope = null
            }
            else -> {}
        }
    }

    private fun setAdapter() {
        try {
            adapter = FlagshipQAAssistantAdapter2(this).open()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showOverlay(context: Context) {
        if (overlayButton == null) {
            overlayButton = QAOverlayButton.create(context)
        }
    }


    override fun close() {
        overlayButton?.dismiss()
        overlayButton = null
        adapter?.close()
        adapter = null
        savedScreen = null
        contextWeakReference?.clear()
        contextWeakReference = null
        _coroutineScope?.cancel()
        _coroutineScope = null
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onVisitorChanged(visitorDelegateDTO: VisitorDelegateDTO, campaigns: List<Campaign>?) {
        currentVisitor = visitorDelegateDTO
        println("[QA ASSISTANT] Flags updated from CORE : ${visitorDelegateDTO.flags}")
        _coroutineScope?.launch {
            val campaignsUpdated = contextWeakReference?.get()?.let {
                if (envID == null) return@let
                campaignManager.updateCampaigns(it, envID!!, visitorDelegateDTO, campaigns)
            }
            println("[QA ASSISTANT] Campaigns updated : $campaignsUpdated")
            for (c in campaigns!!) {
                println("#Var Campaign : " + c.toString())
            }
        }
    }

    override fun onHitEmitted(hit: Hit.HitDTO) {
        println("[QA ASSISTANT] Hit emitted from CORE : $hit")
        hitManager.addHit(hit)
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