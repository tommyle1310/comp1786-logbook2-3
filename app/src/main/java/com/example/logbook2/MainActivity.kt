package com.example.logbook2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logbook2.data.TodoRepository
import com.example.logbook2.data.database.DatabaseProvider
import com.example.logbook2.data.entity.Todo
import com.example.logbook2.ui.theme.Logbook2Theme
import com.example.logbook2.viewmodel.TodoViewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    // Tạo ViewModel với Factory để truyền Repository
    private val todoViewModel: TodoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val repository = TodoRepository(database.todoDao())
                return TodoViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Logbook2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoListScreen(todoViewModel)
                }
            }
        }
    }
}

@Composable
fun TodoListScreen(viewModel: TodoViewModel) {
    // Lấy danh sách todos từ ViewModel
    val todoItems by viewModel.allTodos.collectAsState(initial = emptyList())
    // State để lưu input
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }

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

        // Row để nhập task mới
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                label = { Text("Enter task title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("Enter task description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    if (newTaskTitle.isNotBlank()) {
                        viewModel.insert(newTaskTitle, newTaskDescription)
                        newTaskTitle = ""
                        newTaskDescription = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Task")
            }
        }

        // Hiển thị danh sách task
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
                            viewModel.update(updatedItem)
                        },
                        onDelete = { deletedItem ->
                            viewModel.delete(deletedItem)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemRow(
    item: Todo,
    onToggleComplete: (Todo) -> Unit,
    onDelete: (Todo) -> Unit
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
            // Checkbox để đánh dấu hoàn thành
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = {
                    onToggleComplete(item.copy(isCompleted = !item.isCompleted))
                },
                modifier = Modifier.padding(end = 8.dp)
            )

            // Hiển thị tiêu đề và mô tả
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = if (item.isCompleted) {
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                )
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            // Nút xóa
            IconButton(onClick = { onDelete(item) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}