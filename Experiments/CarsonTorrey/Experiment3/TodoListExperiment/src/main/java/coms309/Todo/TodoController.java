package coms309.Todo;

import coms309.people.Person;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class TodoController
{
    HashMap<String, Todo> TodoList = new HashMap<>();
    @GetMapping("/todo")
    public  HashMap<String, Todo> getAllTodos() { return TodoList; }

    @GetMapping("/todo/lookup/{Action}")
    public Todo getAction(@PathVariable String Action){
        return TodoList.get(Action);
    }
    @PostMapping("/todo")
    public void CreateTodo(@RequestBody Todo todo)
    {
        TodoList.put(todo.title, todo);
    }

    @GetMapping("/todo/format")
    public String format()
    {
        String todoFormat = "";
        for(Todo todo : TodoList.values())
        {
            todoFormat = todoFormat + "\n " + todo.toString();
        }
        return todoFormat;
    }

    @GetMapping("todo/lookupid/{id}")
    public Todo LookupTodoID(@PathVariable String id)
    {
        Todo foundTodo = new Todo();
        int todoID = Integer.parseInt(id);
        for(Todo todo : TodoList.values())
        {
            if(todo.id == todoID)
            {
                foundTodo = todo;
            }
        }

       return foundTodo;
    }

    @DeleteMapping("/todo/deleteid/{id}")
    public void deleteID(@PathVariable String id)
    {
        int todoID = Integer.parseInt(id);
        for(Todo todo : TodoList.values())
        {
            if(todo.id == todoID)
            {
                TodoList.remove(todo.title);
                break;
            }
        }
    }

    @DeleteMapping("/todo/deleteTitle/{title}")
    public void deleteTitle(@PathVariable String title)
    {
        TodoList.remove(title);
    }
    @GetMapping("/todo/getcompletedtodos")
    public HashMap<String, Todo> GetCompletedTodos()
    {
        HashMap<String, Todo> completed = new HashMap<>();
        for(Todo todo : TodoList.values())
            if(todo.completed)
                completed.put(todo.title, todo);
        return completed;
    }

    @PatchMapping("/todo/complete/{id}")
    public void completeTodo(@PathVariable String id, @RequestBody boolean completedAction)
    {
        int todoID = Integer.parseInt(id);
        for(Todo todo : TodoList.values())
        {
            if(todo.id == todoID)
            {
                TodoList.get(todo.title).completed = completedAction;
            }
        }
    }

    @GetMapping("/todo/complete/list")
    public HashMap<String, Todo> GetCompletedList()
    {
        HashMap<String, Todo> completed = new HashMap<>();
        for(Todo todo : TodoList.values())
            if(todo.completed)
                completed.put(todo.title, todo);
        return completed;
    }
}

