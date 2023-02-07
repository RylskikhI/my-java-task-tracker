package manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.List;

public interface TaskManager {

    // получение списка всех задач
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();

    // удаление всех задач
    void deleteTasks();
    void deleteSubtasks();
    void deleteEpics();

    // получение по идентификатору
    Task getTask(int id);
    Subtask getSubtask(int id);
    Epic getEpic(int id);

    // Создание. Сам объект должен передаваться в качестве параметра.
    int addNewTask(Task task);
    Integer addNewSubtask(Subtask subtask);
    int addNewEpic(Epic epic);

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    // Удаление по идентификатору.
    void removeTask(int id);
    void removeSubtask(int id);
    void removeEpic(int id);

    // Получение списка всех подзадач определённого эпика.
    List<Subtask> getEpicSubtasks(int epicId);

    // Отображение последних просмотренных задач
    List<Task> getHistory();

    List<Task> getPriorTasks();
}



