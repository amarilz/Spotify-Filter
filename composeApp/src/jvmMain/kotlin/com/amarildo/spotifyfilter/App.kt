package com.amarildo.spotifyfilter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amarildo.spotifyfilter.viewmodel.MyViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(vm: MyViewModel = viewModel()) {
    MaterialTheme {
        val scope = rememberCoroutineScope() // Crea uno scope per le coroutine

        var configurationFilePath by remember { mutableStateOf("") }
        var databaseFilePath by remember { mutableStateOf("") }
        var textUrlFromBrowser by remember { mutableStateOf("") }

        var clickedStart by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            InputRow(
                label = "configuration file",
                value = configurationFilePath,
                onValueChange = { newText -> configurationFilePath = newText },
                onFocusAction = {
                    scope.launch {
                        configurationFilePath = FileLocator().getDbFile()
                        vm.selectedConfigurationFile(configurationFilePath)
                    }
                }
            )
            InputRow(
                label = "database file",
                value = databaseFilePath,
                onValueChange = { newText -> databaseFilePath = newText },
                onFocusAction = {
                    scope.launch {
                        databaseFilePath = FileLocator().getDbFile()
                        vm.selectedDatabaseFile(databaseFilePath)
                    }
                }
            )

            if (!clickedStart) {
                Button(
                    onClick = {
                        vm.openBrowser()
                        clickedStart = !clickedStart
                    }
                ) {
                    Text("start")
                }
            } else {
                Button(
                    onClick = {
                        vm.finalizePlaylist(textUrlFromBrowser)
                    }
                ) {
                    Text("send url")
                }
            }

            if (clickedStart) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically
                ) {
                    OutlinedTextField(
                        value = textUrlFromBrowser,
                        onValueChange = { newText ->
                            textUrlFromBrowser = newText
                        },
                        label = { Text("browser url") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun InputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onFocusAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocusAction()
                    }
                }
        )
    }
}
