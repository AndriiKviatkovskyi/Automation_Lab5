package org.kviat;

import java.time.LocalDateTime;

public class SimpleTask {
    private String name;
    private LocalDateTime deadline;
    private boolean completed;

    public SimpleTask(String name, LocalDateTime deadline) {
        this.name = name;
        this.deadline = deadline;
        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}