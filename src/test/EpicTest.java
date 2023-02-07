import manager.InMemoryTaskManager;
import manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static Tasks.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    //assertEquals(Object expected, Object actual, [String message])
    //Первый — это ожидаемый результат, второй — фактический. Третий аргумент — необязательная строка,
    //которая выведется, если фактический результат не равен ожидаемому, другими словами, если тест обнаружит ошибку.

    @Test
    public void shouldUpdateEpicStatus() {

        TaskManager testManager = new InMemoryTaskManager();

        Epic epic1 = new Epic(1,"Epic 1", NEW, "Epic 1 description");
        Epic epic2 = new Epic(2,"Epic 2", NEW, "Epic 2 description");
        Epic epic3 = new Epic(3,"Epic 3", NEW, "Epic 3 description");
        Epic epic4 = new Epic(4,"Epic 4", NEW, "Epic 4 description");
        final int epicId1 = testManager.addNewEpic(epic1);
        final int epicId2 = testManager.addNewEpic(epic2);
        final int epicId3 = testManager.addNewEpic(epic3);
        final int epicId4 = testManager.addNewEpic(epic4);

        Subtask subtask1 = new Subtask("Subtask 1", NEW, "Subtask 1 description", 15, LocalDateTime.of(2022,8, 23, 18, 20), epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", NEW, "Subtask 2 description", 20, LocalDateTime.of(2022,8, 23, 20, 20), epicId1);
        Subtask subtask3 = new Subtask("Subtask 3", DONE, "Subtask 3 description", 25, LocalDateTime.of(2022,8, 20, 18, 20), epicId2);
        Subtask subtask4 = new Subtask("Subtask 4", DONE, "Subtask 4 description", 35, LocalDateTime.of(2022,8, 19, 18, 20), epicId2);
        Subtask subtask5 = new Subtask("Subtask 5", NEW, "Subtask 5 description", 15, LocalDateTime.of(2022,7, 23, 18, 20), epicId3);
        Subtask subtask6 = new Subtask("Subtask 6", DONE, "Subtask 6 description", 20, LocalDateTime.of(2022,7, 23, 20, 20), epicId3);
        Subtask subtask7 = new Subtask("Subtask 7", IN_PROGRESS, "Subtask 7 description", 25, LocalDateTime.of(2022,6, 20, 18, 20), epicId4);
        Subtask subtask8 = new Subtask("Subtask 8", IN_PROGRESS, "Subtask 8 description", 35, LocalDateTime.of(2022,6, 19, 18, 20), epicId4);

        testManager.addNewSubtask(subtask1);
        testManager.addNewSubtask(subtask2);
        testManager.addNewSubtask(subtask3);
        testManager.addNewSubtask(subtask4);
        testManager.addNewSubtask(subtask5);
        testManager.addNewSubtask(subtask6);
        testManager.addNewSubtask(subtask7);
        testManager.addNewSubtask(subtask8);


        final List<Epic> eps = testManager.getEpics();
        final List<Subtask> subs = testManager.getSubtasks();

        assertNotNull(subs, "Список подзадач пустой");
        assertNotNull(eps, "Список эпиков пустой");
        assertEquals(4, eps.size(), "Не верное количество эпиков");
        assertEquals(8, subs.size(), "Не верное количество подзадач");
        assertEquals(epic1, eps.get(0), "Эпики не совпадают");
        // все подзадачи со статусом NEW
        assertEquals(NEW, epic1.getStatus(), "Статусы не совпадают");
        // все подзадачи со статусом DONE
        assertEquals(DONE, epic2.getStatus(), "Статусы не совпадают");
        // Подзадачи со статусами NEW и DONE
        assertEquals(NEW, epic3.getStatus(), "Статусы не совпадают");
        // Подзадачи со статусом IN_PROGRESS
        assertEquals(IN_PROGRESS, epic4.getStatus(), "Статусы не совпадают");
    }
}