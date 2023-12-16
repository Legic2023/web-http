package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static ru.netology.Server.outWrite;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", ".*", new MyHandler());

        // special case for classic
        server.addHandler("GET", "/classic.html", new MyHandler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace("{time}",
                        LocalDateTime.now().toString()).getBytes();

                outWrite("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n", responseStream, content);
            }
        });

        server.addHandler("GET", "/forms.html", new MyHandler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);

                System.out.println("Login: " + request.getQueryParam("login"));
                System.out.println("Password: " + request.getQueryParam("password"));

                outWrite(
                        "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n", responseStream, filePath);
            }
        });

        server.listenPort(9999);

    }
}

