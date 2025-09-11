package coms309.Todo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Todo
{
    public int id;
    public String title;
    public String description;
    public int dueDate;
    public Boolean completed = false;

    public Todo(int id, String title, String desc, int due, Boolean done){
        this.id = id;
        this.title = title;
        this.description = desc;
        this.dueDate = due;
        this.completed = done;
    }

    @Override
    public String toString() {
        return "ID: " + id + " "
                + "Title: " + title + " "
                + "Description: " + description + " "
                + "Due Date: " + dueDate + " "
                + "Completed: " + completed;
    }
}
