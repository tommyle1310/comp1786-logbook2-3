package com.example.logbook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logbook2.ui.theme.Logbook2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Logbook2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoListScreen()
                }
            }
        }
    }
}

// data class to store obj
data class TodoItem(val id: Int, val title: String, val isCompleted: Boolean)

@Composable
fun TodoListScreen() {
    // State to store tasks
    var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
    // State to store inpu tquery
    var newTaskTitle by remember { mutableStateOf("") }
    // increment id variable
    var nextId by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Todo List",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // row for creating new task
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                label = { Text("Enter a new task") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    if (newTaskTitle.isNotBlank()) {
                        todoItems = todoItems + TodoItem(nextId, newTaskTitle, false)
                        nextId += 1
                        newTaskTitle = "" // set text input
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add Task")
            }
        }

        // tasks display
        if (todoItems.isEmpty()) {
            Text(
                text = "No tasks available",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(todoItems) { item ->
                    TodoItemRow(
                        item = item,
                        onToggleComplete = { updatedItem ->
                            todoItems = todoItems.map {
                                if (it.id == updatedItem.id) updatedItem.copy(isCompleted = !updatedItem.isCompleted)
                                else it
                            }
                        },
                        onDelete = { deletedItem ->
                            todoItems = todoItems.filter { it.id != deletedItem.id }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    onToggleComplete: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox mark completed task
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggleComplete(item) },
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = item.title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                style = if (item.isCompleted) {
                    MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )

            // delete btn
            IconButton(onClick = { onDelete(item) }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}