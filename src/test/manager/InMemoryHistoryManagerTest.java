package manager;

import Tasks.Task;
import manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static Tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    final String messageToShowInMyTests = "История не пустая";

    Task task = new Task("Test Task", NEW, "Test Task description", 15, LocalDateTime.now());

    @Test
    void shouldAddInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, messageToShowInMyTests);
        assertEquals(1, history.size(), messageToShowInMyTests);
    }

    @Test
    void shouldRemoveFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), messageToShowInMyTests);

    }
}
