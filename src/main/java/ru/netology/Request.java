package ru.netology;

import com.sun.net.httpserver.HttpExchange;

public class Request {
    String method, path, httpVersion;

    public Request(String method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

}
