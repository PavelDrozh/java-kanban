package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.managers.HttpTaskManager;
import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.servers.HttpTaskServer;
import ru.yandex.practicum.tasktracker.servers.KVServer;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Main().startApplication();
    }

    public void startApplication() throws IOException {
        System.out.println("StartApp");
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskManager manager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(manager);

        kvServer.stop(200);
        taskServer.stop(200);
    }
}
