package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyHandler {
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        if ("/".equals(request.path)) {
            return;
        }
        final var filePath = Path.of(".", "public", request.path);
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);
        Server.outWrite(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n", responseStream, filePath);

    }
}
