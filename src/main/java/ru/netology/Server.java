package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Server {
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public static Map<String, Map<String, MyHandler>> myHandlers = new ConcurrentHashMap<String, Map<String, MyHandler>>();

    public void addHandler(String method, String path, MyHandler myHandler) {
        Map<String, MyHandler> methodHandlers = myHandlers.get(method);
        if (methodHandlers == null) {
            methodHandlers = new HashMap<String, MyHandler>();
            myHandlers.put(method, methodHandlers);
        }
        methodHandlers.put(path, myHandler);
    }

    void listenPort(int port) {

        Runnable connectionToServer = () -> {
            try (final ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try (final var socket = serverSocket.accept();
                         final var requestStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         final var responseStream = new BufferedOutputStream(socket.getOutputStream());) {
                        // read only request line for simplicity
                        // must be in form GET /path HTTP/1.1
                        final var requestLine = requestStream.readLine();

                        if (requestLine == null) {
                            continue;
                        }

                        final var parts = requestLine.split(" ");

                        // just close socket
                        if (http1Check(parts)) {
                            continue;
                        }

                        final var method = parts[0];
                        final var path = parts[1];
                        final var httpVersion = parts[2];

                        // Создаем объект Request
                        Request request = new Request(method, path, httpVersion);

                        //Находим нужный хендлер
                        var methodHandlers = myHandlers.get(method);
                        for (String s : methodHandlers.keySet()) {
                            if (Pattern.matches(s, request.path)) {
                                methodHandlers.get(s).handle(request, responseStream);
                            } else if (noValidPathsCheck(request.path)) {
                                outWrite("HTTP/1.1 404 Not Found\r\n" +
                                        "Content-Length: 0\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n", responseStream);

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // пул на 64 потока
        final ExecutorService threadPool = Executors.newFixedThreadPool(64);
        threadPool.submit(connectionToServer);
        threadPool.shutdown();

    }

    boolean http1Check(String[] parts) {
        return (parts.length != 3);
    }

    boolean noValidPathsCheck(String path) {
        return (!validPaths.contains(path));
    }

    static void outWrite(String text, BufferedOutputStream out) throws IOException {
        out.write((text).getBytes());
        out.flush();
    }

    static void outWrite(String text, BufferedOutputStream out, byte[] content) throws IOException {
        out.write((text).getBytes());
        out.write(content);
        out.flush();
    }

    static void outWrite(String text, BufferedOutputStream out, Path filePath) throws IOException {
        out.write((text).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}