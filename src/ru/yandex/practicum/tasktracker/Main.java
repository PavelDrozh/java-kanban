package ru.yandex.practicum.tasktracker;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        new Main().startApplication();
    }

    private void startApplication() {
        TaskManager manager = Managers.getDefault();
        addTasks(manager);
        checkHistory(manager);
        printStatistics(manager);
        updateTasks(manager);
        printStatistics(manager);
        deleteTasks(manager);
        printStatistics(manager);
        printHistory(manager);
    }

    private void checkHistory(TaskManager manager) {
        for (int i = 0; i <= 10; i++) {
            manager.getTaskOrNull(0);
        }
        printHistory(manager);
        manager.getTaskOrNull(3);
        printHistory(manager);
        manager.getTaskOrNull(2);
        printHistory(manager);
        manager.getTaskOrNull(5);
        printHistory(manager);
        manager.getTaskOrNull(1);
        printHistory(manager);
        manager.getTaskOrNull(2);
        printHistory(manager);
        manager.getTaskOrNull(4);
        printHistory(manager);
        manager.getTaskOrNull(3);
        printHistory(manager);
        manager.getTaskOrNull(6);
        printHistory(manager);
        manager.getTaskOrNull(7);
        printHistory(manager);
        manager.getTaskOrNull(6);
        printHistory(manager);
        manager.getTaskOrNull(4);
        printHistory(manager);
        manager.getTaskOrNull(0);
        printHistory(manager);
    }

    private void printHistory(TaskManager manager) {
        List<Task> history;
        history = manager.getHistory();

        System.out.print("История get-запросов: ");
        for (Task task : history) {
            System.out.printf("taskId = %s; ", task.getId());
        }
        System.out.println();
    }

    private void deleteTasks(TaskManager manager) {
        printDeleteResult(manager.deleteTaskOrNull(20));
        printDeleteResult(manager.deleteTaskOrNull(1));
        printHistory(manager);
        printDeleteResult(manager.deleteTaskOrNull(2));
        printHistory(manager);
        printDeleteResult(manager.deleteTaskOrNull(0));
        printHistory(manager);
    }

    private void printDeleteResult(Task deleted) {
        if (deleted != null) {
            System.out.println("Удаление прошло успешно");
        } else {
            System.out.println("Удалить не удалось");
        }
    }

    private void printStatistics(TaskManager manager) {
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtasksList());
        System.out.println(manager.getTasksByEpicId(6));
    }

    private void updateTasks(TaskManager manager) {
        Task task = new Task(0, "Уборка", "Помыть полы", 0);
        Task notCorrectTask = new Task(23, "Отдых", "Погулять в дождь", 0);
        Task taskTwo = new Task(1, "Программирование", "Написать программу по ТЗ 3 спринта", 1);
        Subtask sub = new Subtask(3,"Собрать вещи", "Сгруппировать по категориям вещи",
                1, 2);
        Subtask subTwo = new Subtask(4,"Упаковать вещи",
                "Сгруппированные вещи разложить по соответствующим коробкам", 0, 2);
        Subtask subThree = new Subtask(6, "Закупка материалов",
                "Заказать материалы на сайте с доставкой", 1,5);
        printUpdateResult(manager.updateTask(task));
        printUpdateResult(manager.updateTask(notCorrectTask));
        printUpdateResult(manager.updateTask(taskTwo));
        printUpdateResult(manager.updateTask(sub));
        printUpdateResult(manager.updateTask(subTwo));
        printUpdateResult(manager.updateTask(subThree));
    }

    private void printUpdateResult(boolean result) {
        if (result) {
            System.out.println("Успешное обновление");
        } else {
            System.out.println("Обновить информацию не удалось");
        }
    }

    private void addTasks(TaskManager manager) {
        Task task = new Task("Уборка","Помыть полы");
        Task taskTwo = new Task("Программирование","Написать программу по ТЗ 3 спринта");
        manager.createTask(task);
        manager.createTask(taskTwo);
        Epic epic = new Epic("Переезд", "Переезд из старой квартиры в новую");
        manager.createTask(epic);
        Subtask sub = new Subtask("Собрать вещи", "Сгруппировать по категориям вещи", epic.getId());
        Subtask subTwo = new Subtask("Упаковать вещи",
                "Сгруппированные вещи разложить по соответствующим коробкам", epic.getId());
        Subtask subThree = new Subtask("Загрузить коробки в машину",
                "Перенести из старой квартиры коробки в машину", epic.getId());
        manager.createTask(sub);
        manager.createTask(subTwo);
        manager.createTask(subThree);
        Epic epicTwo = new Epic("Дача", "Подготовка к строительству дома");
        manager.createTask(epicTwo);
        Subtask subFour = new Subtask("Закупка материалов",
                "Заказать материалы на сайте с доставкой", epicTwo.getId());
        manager.createTask(subFour);
        Task taskThree = new Task("Установить сетку","Установить москитную сетку на окно");
        manager.createTask(taskThree);
    }
}
