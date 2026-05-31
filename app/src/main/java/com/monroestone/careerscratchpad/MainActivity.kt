package com.monroestone.careerscratchpad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.monroestone.careerscratchpad.lab.LabRegistry
import com.monroestone.careerscratchpad.labs.secretmessage.SecretMessageLab
import com.monroestone.careerscratchpad.ui.CareerScratchpadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Register labs
        LabRegistry.registerLab(SecretMessageLab())

        enableEdgeToEdge()
        setContent {
            CareerScratchpadTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    MainScreenContent(
        labs = LabRegistry.labs,
        currentLab = LabRegistry.currentLab,
        onLabSelected = { LabRegistry.selectLab(it.id) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    labs: List<com.monroestone.careerscratchpad.lab.Lab>,
    currentLab: com.monroestone.careerscratchpad.lab.Lab?,
    onLabSelected: (com.monroestone.careerscratchpad.lab.Lab) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LabSwitcher(
                        labs = labs,
                        currentLab = currentLab,
                        onLabSelected = onLabSelected
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (currentLab != null) {
                currentLab.Content()
            } else {
                Text(text = "No labs registered. Please add a lab to LabRegistry.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabSwitcher(
    labs: List<com.monroestone.careerscratchpad.lab.Lab>,
    currentLab: com.monroestone.careerscratchpad.lab.Lab?,
    onLabSelected: (com.monroestone.careerscratchpad.lab.Lab) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = currentLab?.title ?: "Select Lab",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            labs.forEach { lab ->
                DropdownMenuItem(
                    text = { Text(lab.title) },
                    onClick = {
                        onLabSelected(lab)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val mockLabs = listOf(
        object : com.monroestone.careerscratchpad.lab.Lab {
            override val id = "mock-1"
            override val title = "Mock Lab 1"
            override val category = com.monroestone.careerscratchpad.lab.LabCategory.COMPOSE_UI
            @Composable override fun Content() { Text("Content of Mock Lab 1") }
        },
        object : com.monroestone.careerscratchpad.lab.Lab {
            override val id = "mock-2"
            override val title = "Mock Lab 2"
            override val category = com.monroestone.careerscratchpad.lab.LabCategory.ALGORITHM
            @Composable override fun Content() { Text("Content of Mock Lab 2") }
        }
    )
    CareerScratchpadTheme {
        MainScreenContent(
            labs = mockLabs,
            currentLab = mockLabs[0],
            onLabSelected = {}
        )
    }
}
