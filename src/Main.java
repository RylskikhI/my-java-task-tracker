

import manager.Managers;
import manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;


import java.time.LocalDateTime;

import static Tasks.TaskStatus.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        // Создание
        Task task1 = new Task(1, "Task 1", NEW, "Task 1 description", 0L, LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", IN_PROGRESS, "Task 1 description", 1L, LocalDateTime.now());
        final int taskId1 = manager.addNewTask(task1);
        final int taskId2 = manager.addNewTask(task2);


        Epic epic1 = new Epic(1,"Epic #1", NEW, "Epic #1 description");
        Epic epic2 = new Epic(2,"Epic #2", NEW, "Epic #2 description");
        final int epicId1 = manager.addNewEpic(epic1);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", NEW, "Subtask 1 description", 15, LocalDateTime.of(2022,8, 23, 18, 20), epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", IN_PROGRESS, "Subtask 2 description", 20, LocalDateTime.of(2022,8, 23, 20, 20), epicId1);
        Subtask subtask3 = new Subtask("Subtask 3", NEW, "Subtask 3 description", 25, LocalDateTime.of(2022,8, 20, 18, 20), epicId2);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);

        //Получение и обновление
        final Task task = manager.getTask(taskId2);
        task.setStatus(NEW);
        manager.updateTask(task);
        System.out.println("Задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        Subtask subtask = manager.getSubtask(subtaskId1);
        subtask.setStatus(DONE);
        manager.updateSubtask(subtask);
        System.out.println("Подзадачи:");
        for (Task st : manager.getSubtasks()) {
            System.out.println(st);
            // manager.deleteSubtasks(); метод работает, если раскомментить строчку - сабтаски удалятся и метод вывода сабтасок выведет null
        }

        final Epic epic = manager.getEpic(epicId1);
        manager.updateEpic(epic);
        System.out.println("Эпики:");
        for (Task e : manager.getEpics()) {
            System.out.println(e);
        }

        //запрос задач
        manager.getTasks();

        // Вывод истории
        manager.getHistory();

        // Вывод списка всех сабтасок определённого эпика
        manager.getEpicSubtasks(epicId1);



        // Удаление Эпика по Id
        manager.removeTask(taskId2);
        manager.removeEpic(epicId1);

        System.out.println("После удаления остались задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

    }
}
