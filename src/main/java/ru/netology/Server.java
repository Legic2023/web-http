package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    void serverGo() {

        Runnable connectionToServer = () -> {
            try (final ServerSocket serverSocket = new ServerSocket(9999)) {
                while (true) {
                    try (final var socket = serverSocket.accept();
                         final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         final var out = new BufferedOutputStream(socket.getOutputStream());) {
                        // read only request line for simplicity
                        // must be in form GET /path HTTP/1.1
                        final var requestLine = in.readLine();

                        if (requestLine == null) {
                            continue;
                        }

                        final var parts = requestLine.split(" ");

                        // just close socket
                        if (http1Check(parts)) {
                            continue;
                        }

                        final var path = parts[1];

                        if (pathCheck(path)) {
                            outWrite("HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n", out);
                            continue;
                        }

                        final var filePath = Path.of(".", "public", path);
                        final var mimeType = Files.probeContentType(filePath);

                        // special case for classic
                        if (path.equals("/classic.html")) {
                            final var template = Files.readString(filePath);
                            final var content = template.replace("{time}",
                                            LocalDateTime.now().toString())
                                    .getBytes();

                            outWrite("HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n", out, content);
                            continue;
                        }

                        final var length = Files.size(filePath);
                        outWrite(
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + mimeType + "\r\n" +
                                        "Content-Length: " + length + "\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n", out, filePath);
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

    boolean pathCheck(String path) {
        return (!validPaths.contains(path));
    }

    void outWrite(String text, BufferedOutputStream out) throws IOException {
        out.write((text).getBytes());
        out.flush();
    }

    void outWrite(String text, BufferedOutputStream out, byte[] content) throws IOException {
        out.write((text).getBytes());
        out.write(content);
        out.flush();
    }

    void outWrite(String text, BufferedOutputStream out, Path filePath) throws IOException {
        out.write((text).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

}