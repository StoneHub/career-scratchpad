package com.monroestone.careerscratchpad.lab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LabRegistry {
    private val _labs = mutableListOf<Lab>()
    val labs: List<Lab> get() = _labs

    var currentLab by mutableStateOf<Lab?>(null)
        private set

    fun registerLab(lab: Lab) {
        if (_labs.none { it.id == lab.id }) {
            _labs.add(lab)
        }
        if (currentLab == null) {
            currentLab = lab
        }
    }

    fun selectLab(labId: String) {
        currentLab = _labs.find { it.id == labId }
    }
}
