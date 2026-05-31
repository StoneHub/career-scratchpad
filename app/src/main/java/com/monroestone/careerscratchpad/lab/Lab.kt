package com.monroestone.careerscratchpad.lab

import androidx.compose.runtime.Composable

interface Lab {
    val id: String
    val title: String
    val category: LabCategory
    
    @Composable
    fun Content()
}

enum class LabCategory {
    ALGORITHM,
    COMPOSE_UI,
    API_DESIGN,
    ARCHITECTURE,
    DATA_PARSING,
    CODE_ANALYSIS,
    RESPONSE_EVALUATION,
    CAREER_PREP
}
