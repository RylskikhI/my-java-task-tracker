package manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import manager.file.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private File file;

    @BeforeEach
    public void setUp() {
        file = new File ("/test_" + System.nanoTime() + ".csv");
        taskManager = new FileBackedTasksManager(file);
        initTasks();
    }

    @AfterEach
    protected void tearDown() throws IOException {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldLoadFromFile() throws IOException {
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = manager.getTasks();
        assertNotNull(task, "возвращает не пустой список задач");
        assertEquals(2, tasks.size(), "возвращает не пустой список задач");
        final List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "возвращает не пустой список эпиков");
        assertEquals(0, epics.size(), "возвращает не пустой список эпиков");
        final List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "возвращает не пустой список подзадач");
        assertEquals(0, subtasks.size(), "возвращает не пустой список подзадач");
        final List<Task> history = manager.getHistory();
        assertNotNull(history, "возвращает не пустой список истории");
        assertEquals(0, history.size(), "возвращает не пустой список истории");

    }

}

