package manager.file;

import manager.*;
import Tasks.*;
import manager.history.HistoryManager;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static Tasks.TaskStatus.IN_PROGRESS;
import static Tasks.TaskStatus.NEW;


public class FileBackedTasksManager extends InMemoryTaskManager {
    File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    // метод, который будет сохранять текущее состояние менеджера в указанный файл
    // Исключения вида IOException нужно отлавливать внутри метода save и кидать собственное непроверяемое исключение ManagerSaveException

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int nextId = 0;
            List<Integer> history = Collections.emptyList();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    history = historyFromString(lines[i+1]);
                    break;
                }
                final Task task = taskFromString(line);
                final int id = task.getId();
                if(id > tasksManager.idGenerator) {
                    tasksManager.idGenerator = id;
                }
                tasksManager.addNewTask(task);
            }
            for (Map.Entry<Integer, Subtask> e : tasksManager.subtasks.entrySet()) {
                final Subtask subtask = e.getValue();
                final Epic epic = tasksManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            for (Integer taskId : history) {
                tasksManager.historyManager.add(tasksManager.getTask(taskId));
            }
            tasksManager.idGenerator = nextId;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasksManager;
    }

    protected void save() {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write("id" + "," + "type" + "," + "name" + "," + "status" + "," + "description" + "," + "duration" + "," + "startTime" + "," + "epicId" + "\n");
                for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                    Task task = entry.getValue();
                    fw.write(toString(task));
                    fw.write(System.lineSeparator());
                }

                for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                    Epic epic = entry.getValue();
                    fw.write(toString(epic));
                    fw.write(System.lineSeparator());
                }

                for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                    Subtask subtask = entry.getValue();
                    fw.write(toString(subtask));
                    fw.write(System.lineSeparator());
                }

                fw.write(System.lineSeparator());
                fw.write(historyToString(historyManager));
                fw.close();

            } catch (FileNotFoundException e) {
                throw new ManagerSaveException("Файл не найден");
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи");
            }
    }

    // метод сохранения задачи в строку
    String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "," + task.getEpicId();
    }


    // метод создания задачи из строки
    static Task taskFromString(String value) {

        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskTypes type = TaskTypes.valueOf(values[1]);
        final String name = values[2];
        final TaskStatus status = TaskStatus.valueOf(values[3]);
        final String description = values[4];
        final long duration = Long.parseLong(values[5]);
        final LocalDateTime startTime = LocalDateTime.parse(values[6]);

        if(type == TaskTypes.TASK) {
            return new Task(id, name, status, description, duration, startTime);
        }

        if (type == TaskTypes.SUBTASK) {
            final int epicId = Integer.parseInt(values[7]);
            return new Subtask(id, name, status, description, duration, startTime, epicId);
        }

        return new Epic(id, name, status, description, duration, startTime);
    }

    // метод для сохранения менеджера истории в CSV
    public static String historyToString(HistoryManager manager) {
        final List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < history.size(); i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }

        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {

        final String[] values = value.split(",");
        final ArrayList<Integer> ids = new ArrayList<>(values.length);
        for (String id : values) {
            ids.add(Integer.parseInt(id));
        }
        return ids;
    }

    protected void addTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK -> tasks.put(id, task);
            case SUBTASK -> subtasks.put(id, (Subtask) task);
            case EPIC -> epics.put(id, (Epic) task);

        }
    }

    protected Task findTask(Integer id) {
        final Task task = tasks.get(id);
        if(task != null) {
            return task;
        }

        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }

        return epics.get(id);
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public int addNewTask(Task task) {
        final int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int id = super.addNewTask(subtask);
        save();
        return id;
    }
}
