package ru.yandex.practicum.tasktracker.servers;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.managers.HttpTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private HttpServer httpServer;

    public HttpTaskServer(HttpTaskManager manager) {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        httpServer.stop(0);
    }

    public void stop(int delay) {
        httpServer.stop(delay);
    }
}
