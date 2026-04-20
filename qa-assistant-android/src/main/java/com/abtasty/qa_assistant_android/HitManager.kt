package com.abtasty.qa_assistant_android

import com.abtasty.flagship.hits.Hit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HitManager {

    private val _hits = MutableStateFlow<ArrayList<Hit.HitDTO>>(ArrayList())
    val hits: StateFlow<ArrayList<Hit.HitDTO>> = _hits.asStateFlow()


    fun addHit(hit: Hit.HitDTO) {
        println("[QA ASSISTANT] Hit added to QA Assistant : $hit")
        _hits.value = ArrayList<Hit.HitDTO>(_hits.value).apply { add(hit) }
    }

    fun clearHits() {
        _hits.value = ArrayList()
    }

}