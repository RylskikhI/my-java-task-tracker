package manager;

import Tasks.*;
import manager.history.HistoryManager;

import java.time.LocalDateTime;
import java.util.*;

import static Tasks.TaskStatus.IN_PROGRESS;
import static Tasks.TaskStatus.NEW;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<LocalDateTime, Task> priorTasks = new TreeMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idGenerator = 0;


    // получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // получение по идентификатору
    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // удаление всех задач
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addNewTask(Task task) {
        final int id = idGenerator++;
        task.setId(id);
        tasks.put(id, task);
        add(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = idGenerator++;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        final int id = idGenerator++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        add(subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpic(epicId);
        return id;
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        delete(savedTask);
        tasks.put(id, task);
        add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        delete(subtask);
        subtasks.put(id, subtask);
        add(subtask);
        updateEpic(epicId);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    // Удаление по идентификатору.
    @Override
    public void removeTask(int id) {
        final Task task = tasks.remove(id);
        if (task == null) {
            return;
        }
        delete(task);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        delete(subtask);
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeEpic(int id) {
        List<Subtask> subtasksIds = getEpicSubtasks(id);
        for (int i = 0; i < subtasksIds.size(); i++) {
            Subtask subtask = subtasksIds.get(i);
            removeSubtask(subtask.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    // Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> tasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtasksIds()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }


    // Апдейт статуса у Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subs = epic.getSubtasksIds();
        if (subs.isEmpty()) {
            epic.setStatus(NEW);
        }
        TaskStatus status = null;
        for (int id : subs) {
            final Subtask subtask =subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
            }
            if (status == subtask.getStatus() && status != IN_PROGRESS) {
                continue;
            }
            epic.setStatus(IN_PROGRESS);
        }
        epic.setStatus(status);
    }

    // Получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Приоритезация задач
    private void add(Task task) {
        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();
        for (Map.Entry<LocalDateTime, Task> entry : priorTasks.entrySet()) {
            final Task t = entry.getValue();
            final LocalDateTime existStart = t.getStartTime();
            final LocalDateTime existEnd = t.getEndTime();
            if (!endTime.isAfter(existStart)) {
                continue;
            }
            if(!existEnd.isAfter(startTime)) {
                continue;
            }

            throw new TaskValidationException("Задача пересекается с id=" + t.getId() + " с " + existStart + " по " + existEnd);
        }

        priorTasks.put(startTime, task);
    }

    private void delete(Task task) {
        priorTasks.remove(task.getStartTime());
    }

    @Override
    public List<Task> getPriorTasks() {
        return new ArrayList<>(priorTasks.values());
    }

    private void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        updateEpicStatus(epicId);
        epicDuration(epic);
    }

    private void epicDuration(Epic epic) {
        List<Integer> subs = epic.getSubtasksIds();
        if (subtasks.isEmpty()) {
            epic.setDuration(0L);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (end.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getDuration();
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

}



