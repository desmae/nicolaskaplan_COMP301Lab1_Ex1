package com.example.quicknotes

import android.os.Bundle
import android.text.style.BackgroundColorSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quicknotes.ui.theme.QuickNotesTheme
import kotlinx.coroutines.selects.select

data class Note(
    val title: String,
    val content: String
    // perhaps add a date field
)
// to do: use Card for individual note items
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickNotesTheme {
                val notes = remember { mutableStateListOf(
                    Note("Buy list", "video game, milk, bread, apple, wine, steak, cheese"),
                    Note("Band Names", "crazy dudes, tonic, creatine, spoiled milk, beetle"),
                    Note("ideas", "i want to go on more walks this week, the weather's going to get cold soon.")
                )}

                NoteList(notes = notes, onNoteClick = {}, modifier = Modifier)
                NotesScreen(notes = notes, onNoteClick = {})
            }

        }
    }
}

@Composable
fun NoteList(notes: List<Note>, onNoteClick:(Note) -> Unit, modifier: Modifier) {
    var clickedItem by remember { mutableIntStateOf(-1) }
    LazyColumn(modifier = modifier.padding(top = 16.dp)) {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onNoteClick(note)},
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

            ) {
                Column {
                    Text(note.title, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.content,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}
@Composable
fun NotesScreen(notes: MutableList<Note>,
                onNoteClick: (Note) -> Unit,
                ) {
    var selectedNote by remember { mutableStateOf<Note?>(null)}
    Scaffold( floatingActionButton = {
        FloatingActionButton(
            onClick = {  selectedNote = Note("","") })
        {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Note"
            )
        }
    }) { paddingValues ->
        if (selectedNote != null) {
            ViewEditNoteScreen(
                note = selectedNote!!,
                onSave = { updatedNote ->
                    val index = notes
                        .indexOf(selectedNote!!)
                    if (index != -1) {
                        notes[index] = updatedNote
                        } else {
                            notes.add(updatedNote)
                        }
                        selectedNote = null
    },
    onCancel = { selectedNote = null }
            )
            } else {
                NoteList(
                    notes = notes,
                    onNoteClick = { note -> selectedNote = note},
                    modifier = Modifier.padding(paddingValues)
                )
            }

    }

}

@Composable
fun ViewEditNoteScreen(note: Note, onSave: (Note) -> Unit, onCancel: () -> Unit)
{
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it},
            label = { Text("Title")})
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it},
            label = { Text("Content")},
            modifier = Modifier.height(200.dp))
        Button(onClick =  onCancel) {
            Text("Cancel")
        }
        Button(onClick = { onSave(Note(title, content))}) {
            Text("Save")
        }
    }

}
