package com.example.logbook2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logbook2.data.TodoRepository
import com.example.logbook2.data.entity.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val allTodos: Flow<List<Todo>> = repository.allTodos

    fun insert(title: String, description: String) {
        viewModelScope.launch {
            val newTodo = Todo(title = title, description = description)
            repository.insert(newTodo)
        }
    }

    fun update(todo: Todo) {
        viewModelScope.launch {
            repository.update(todo)
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }
}