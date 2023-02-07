package manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    final String messageToShowInMyTests = "Возвращает пустой список задач";

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        initTasks();
    }

    @Test
    void createInMemoryTaskManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, messageToShowInMyTests);
        assertEquals(0, tasks.size(), messageToShowInMyTests);
        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, messageToShowInMyTests);
        assertEquals(0, epics.size(), messageToShowInMyTests);
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, messageToShowInMyTests);
        assertEquals(0, subtasks.size(), messageToShowInMyTests);
    }
}