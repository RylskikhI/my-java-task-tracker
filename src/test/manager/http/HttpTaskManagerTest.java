package manager.http;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import manager.Managers;
import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static Tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = Managers.getDefaultKVServer();
        taskManager = new HttpTaskManager(KVServer.PORT);
        initTasks();
    }

    @AfterEach
    protected void tearDown() {
        kvServer.stop();
    }

    @Test
    public void loadFromHttpServer() {
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());
        HttpTaskManager taskManager = new HttpTaskManager(KVServer.PORT, true);
        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Возвращает не пустой список задач");
        assertEquals(2, tasks.size(), "Возвращает не пустой список задач");
        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Возвращает не пустой список эпиков");
        assertEquals(1, epics.size(), "Возвращает не пустой список эпиков");
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Возвращает не пустой список подзадач");
        assertEquals(0, subtasks.size(), "Возвращает не пустой список подзадач");
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Возвращает не пустой список истории");
        assertEquals(2, history.size(), "Возвращает не пустой список истории");
        assertEquals(subtask.getId(), taskManager.getIdGenerator(), "Не установлено значение generatorId");
    }
}