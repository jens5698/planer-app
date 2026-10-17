package de.planer.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(repository: TaskRepository, onOpenList: (Long) -> Unit) {
    val lists by remember { repository.observeLists() }.collectAsState(initial = emptyList())
    val tags by remember { repository.observeTags() }.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var newName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Listen") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { newName = "" }) { Icon(Icons.Filled.Add, contentDescription = "Liste anlegen") }
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(lists, key = { it.id }) { list ->
                ListItem(headlineContent = { Text(list.name) }, modifier = Modifier.clickable { onOpenList(list.id) })
            }
            if (tags.isNotEmpty()) {
                item { HorizontalDivider() }
                item { ListItem(headlineContent = { Text("Tags") }) }
                items(tags, key = { "tag-${it.id}" }) { tag ->
                    ListItem(headlineContent = { Text("#${tag.name}") })
                }
            }
        }
    }

    newName?.let { name ->
        AlertDialog(
            onDismissRequest = { newName = null },
            title = { Text("Neue Liste") },
            text = { OutlinedTextField(value = name, onValueChange = { newName = it }) },
            confirmButton = {
                TextButton(enabled = name.isNotBlank(), onClick = {
                    scope.launch { repository.addList(name.trim()) }
                    newName = null
                }) { Text("Anlegen") }
            },
            dismissButton = { TextButton(onClick = { newName = null }) { Text("Abbrechen") } },
        )
    }
}
