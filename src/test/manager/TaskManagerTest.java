package manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import static Tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;


    protected void initTasks() {
        task = new Task("Test Task", NEW, "Test Task description",  15, LocalDateTime.now());
        taskManager.addNewTask(task);
        epic = new Epic("Test Epic", NEW,"Test Epic description",  0, task.getEndTime());
        taskManager.addNewEpic(epic);
        subtask = new Subtask("Test Subtask", NEW,"Test Subtask description",  30, task.getEndTime(), epic.getId());
        taskManager.addNewSubtask(subtask);
    }

    final String messageIfNull = "Задача не найдена";
    final String messageIfNotEquals = "Задачи не совпадают";
    final String messageIfWrongQuantity = "Количество задач не совпадает";

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", NEW, "Test addNewTask description",  25, LocalDateTime.of(2022,8, 23, 18, 20));
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, messageIfNull);
        assertEquals(task, savedTask, messageIfNotEquals);    }

    @Test
    private void shouldGetTasks() {
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, messageIfNull);
        assertEquals(1, tasks.size(), messageIfWrongQuantity);
        assertEquals(task, tasks.get(0), messageIfNotEquals);
    }

    @Test
    private void shouldGetSubtasks() {
        final List<Subtask> tasks = taskManager.getSubtasks();

        assertNotNull(tasks, messageIfNull);
        assertEquals(1, tasks.size(), messageIfWrongQuantity);
        assertEquals(subtask, tasks.get(0), messageIfNotEquals);
    }

    @Test
    void shouldGetEpics() {
        final List<Epic> tasks = taskManager.getEpics();

        assertNotNull(tasks, messageIfNull);
        assertEquals(1, tasks.size(), messageIfWrongQuantity);
        assertEquals(epic, tasks.get(0), messageIfNotEquals);
    }

    @Test
    private void shouldGetEpicSubtasks() {
        final List<Subtask> tasks = taskManager.getEpicSubtasks(epic.getId());
        assertNotNull(tasks, "подзадачи не возвращаются");
        assertEquals(1, tasks.size(), "неверное количество подзадач");

    }
}