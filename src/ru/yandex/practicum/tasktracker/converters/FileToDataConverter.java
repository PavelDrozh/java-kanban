package ru.yandex.practicum.tasktracker.converters;

import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.enums.CSVColumns;
import ru.yandex.practicum.tasktracker.enums.TaskStatus;
import ru.yandex.practicum.tasktracker.enums.TaskTypes;
import ru.yandex.practicum.tasktracker.exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class FileToDataConverter {

    private static final String COMMA_DELIMITER = ",";
    private static final String MESSAGE = "Ошибка чтения задач из файла";

    private FileToDataConverter() { }

    public static List<Task> getTasksFromFile(File file) {
        List<Task> tasks = new ArrayList<>();
        List<String> tasksLines = readTaskLines(file);
        for (String line : tasksLines) {
            try {
                Task task = fromString(line);
                tasks.add(task);
            } catch (Exception e) {
                throw new ManagerSaveException(MESSAGE);
            }
        }
        return tasks;
    }

    private static List<String> readTaskLines(File file) {
        List<String> tasksLines = new ArrayList<>();
        try (FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);) {
            if (br.ready()) {
                br.readLine(); // skip first line
            }
            while (br.ready()) {
                String line = br.readLine();
                if (line.isBlank() || line.isEmpty()) {
                    break;
                }
                tasksLines.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(MESSAGE);
        }
        return tasksLines;
    }

    private static Task fromString(String value) {
        Task result;
        String[] values = value.split(COMMA_DELIMITER);
        int id = Integer.parseInt(values[CSVColumns.ID.getId()]);
        String type = values[CSVColumns.TYPE.getId()];
        String name = values[CSVColumns.NAME.getId()];
        int status = TaskStatus.NEW.getId();
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (values[CSVColumns.STATUS.getId()].equalsIgnoreCase(taskStatus.name())) {
                status = taskStatus.getId();
                break;
            }
        }
        String description = values[CSVColumns.DESCRIPTION.getId()];
        LocalDateTime startTime = LocalDateTime.parse(values[CSVColumns.START_TIME.getId()]);
        LocalDateTime endTime = LocalDateTime.parse(values[CSVColumns.END_TIME.getId()]);
        Duration duration = Duration.between(startTime, endTime);
        if (type.equalsIgnoreCase(TaskTypes.SUBTASK.name())) {
            int epicID = Integer.parseInt(values[CSVColumns.EPIC.getId()]);
            result = new Subtask(id, name, description, status, epicID, startTime, duration);
        } else if (type.equalsIgnoreCase(TaskTypes.EPIC.name())) {
            result = new Epic(id, name, description);
        } else if (type.equalsIgnoreCase(TaskTypes.TASK.name())) {
            result = new Task(id, name, description, status, startTime, duration);
        } else {
            throw new ManagerSaveException(MESSAGE);
        }
        return result;
    }

    public static List<Integer> getHistoryFromFile(File file) {
        List<Integer> history = new ArrayList<>();
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader);) {
            while (br.ready()) {
                String line = br.readLine();
                if ((line.isBlank() || line.isEmpty()) && br.ready()) {
                    history = historyFromString(br.readLine());
                }
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка чтения истории из файла");
        }
        return history;
    }

    private static List<Integer> historyFromString(String value) {
        String[] values = value.split(COMMA_DELIMITER);
        List<Integer> history = new ArrayList<>();
        for (String element : values) {
            history.add(Integer.parseInt(element));
        }
        return history;
    }
}
