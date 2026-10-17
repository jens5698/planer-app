package de.planer.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import de.planer.app.data.TaskList
import de.planer.app.data.TaskRepository
import de.planer.app.ui.ListIcon
import de.planer.app.ui.components.IconPicker
import kotlinx.coroutines.launch

/** Zustand des Listen-Dialogs; [list] ist null beim Anlegen. */
private data class ListDraft(val list: TaskList?, val name: String, val icon: ListIcon)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(repository: TaskRepository, onOpenList: (Long) -> Unit, onSearch: () -> Unit) {
    val lists by remember { repository.observeLists() }.collectAsState(initial = emptyList())
    val tags by remember { repository.observeTags() }.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var draft by remember { mutableStateOf<ListDraft?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listen") },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = "Suchen") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { draft = ListDraft(null, "", ListIcon.LIST) }) {
                Icon(Icons.Filled.Add, contentDescription = "Liste anlegen")
            }
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(lists, key = { it.id }) { list ->
                ListItem(
                    leadingContent = { Icon(ListIcon.of(list.icon).vector, contentDescription = null) },
                    headlineContent = { Text(list.name) },
                    trailingContent = {
                        IconButton(onClick = { draft = ListDraft(list, list.name, ListIcon.of(list.icon)) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Liste bearbeiten")
                        }
                    },
                    modifier = Modifier.clickable { onOpenList(list.id) },
                )
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

    draft?.let { d ->
        AlertDialog(
            onDismissRequest = { draft = null },
            title = { Text(if (d.list == null) "Neue Liste" else "Liste bearbeiten") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = d.name,
                        onValueChange = { draft = d.copy(name = it) },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text("Symbol", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    IconPicker(selected = d.icon, onSelect = { draft = d.copy(icon = it) })
                    // Die letzte Liste bleibt, damit neue Aufgaben immer ein Ziel haben.
                    if (d.list != null && lists.size > 1) {
                        TextButton(
                            onClick = {
                                scope.launch { repository.deleteList(d.list) }
                                draft = null
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        ) { Text("Liste mit allen Aufgaben löschen", color = MaterialTheme.colorScheme.error) }
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = d.name.isNotBlank(), onClick = {
                    val name = d.name.trim()
                    scope.launch {
                        if (d.list == null) repository.addList(name, d.icon.name)
                        else repository.updateList(d.list.copy(name = name, icon = d.icon.name))
                    }
                    draft = null
                }) { Text("Speichern") }
            },
            dismissButton = { TextButton(onClick = { draft = null }) { Text("Abbrechen") } },
        )
    }
}
