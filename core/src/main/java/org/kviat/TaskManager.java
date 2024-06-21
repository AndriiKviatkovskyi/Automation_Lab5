package org.kviat;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<SimpleTask> simpleTasks;

    public TaskManager() {
        this.simpleTasks = new ArrayList<>();
    }

    public void addTask(SimpleTask simpleTask) {
        simpleTasks.add(simpleTask);
    }

    public void removeTask(SimpleTask simpleTask) {
        simpleTasks.remove(simpleTask);
    }

    public List<SimpleTask> getTasks() {
        return simpleTasks;
    }

}