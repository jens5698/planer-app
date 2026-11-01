package de.planer.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.planer.app.data.ListNote
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.launch

/** Notiz bearbeiten; [noteId] 0 legt eine neue an. Gespeichert wird beim Verlassen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(repository: TaskRepository, listId: Long, noteId: Long, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var loaded by remember { mutableStateOf<ListNote?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var ready by remember { mutableStateOf(noteId == 0L) }
    val focus = remember { FocusRequester() }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            repository.getNote(noteId)?.let {
                loaded = it
                if (title.isEmpty() && body.isEmpty()) { title = it.title; body = it.body }
            }
            ready = true
        } else {
            focus.requestFocus()
        }
    }

    val saveAndBack: () -> Unit = {
        scope.launch {
            if (ready) {
                val base = loaded ?: ListNote(listId = listId, title = "", body = "")
                if (base.title != title || base.body != body || base.id == 0L) {
                    repository.saveNote(base.copy(title = title.trim(), body = body.trimEnd()))
                }
            }
            onBack()
        }
    }
    BackHandler(onBack = saveAndBack)

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0L) "Neue Notiz" else "Notiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = saveAndBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Speichern und zurück") }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            loaded?.let { repository.deleteNote(it) }
                            onBack()
                        }
                    }) { Icon(Icons.Outlined.Delete, contentDescription = "Notiz löschen") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 8.dp)) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Titel") },
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().focusRequester(focus),
            )
            TextField(
                value = body,
                onValueChange = { body = it },
                placeholder = { Text("Notiz …") },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
