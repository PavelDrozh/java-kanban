package ru.yandex.practicum.tasktracker.converters;

import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.enums.CSVColumns;
import ru.yandex.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasktracker.managers.HistoryManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DataToFileConverter {

    public static final String COMMA_DELIMITER = ",";
    public static final String RESOURCE = "resources/taskstrack.csv";

    private DataToFileConverter() { }

    public static void writeFile(List<Task> tasks, HistoryManager manager) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESOURCE))) {

            String firstLine = String.join(COMMA_DELIMITER, Arrays.toString(CSVColumns.values()));
            bufferedWriter.write(firstLine);
            bufferedWriter.newLine();
            for (Task task : tasks) {
                bufferedWriter.write(task.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
            String history = historyToString(manager);
            bufferedWriter.write(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи задач в файл");
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<String> tasksIdInHistory = new ArrayList<>();
        List<Task> taskList = manager.getHistory();
        for (Task task: taskList) {
            tasksIdInHistory.add(String.valueOf(task.getId()));
        }
        return String.join(COMMA_DELIMITER, tasksIdInHistory);
    }
}
