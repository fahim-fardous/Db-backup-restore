package com.example.backuprestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.backuprestore.ui.theme.BackupRestoreTheme

class MainActivity : ComponentActivity() {
    val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackupRestoreTheme {
                Greeting(
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun Greeting(viewModel: ViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getData(context)
    }
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LazyColumn {
                items(state) {
                    Text(text = it.name)
                }
            }
            OutlinedTextField(value = name, onValueChange = { name = it })
            Button(onClick = { viewModel.insertData(Data(name = name), context) }) {
                Text(text = "Insert")
            }
            Button(onClick = { viewModel.deleteDatabase(context) }) {
                Text(text = "Delete")
            }
            Button(onClick = { viewModel.backupDatabase(context) }) {
                Text(text = "Back up")
            }
            Button(onClick = { viewModel.restoreDatabase(context) }) {
                Text(text = "Restore")
            }
        }
    }
}
