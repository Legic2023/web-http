package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Request {
    String method, path, httpVersion;
    List<NameValuePair> queryParams;

    public Request(String method, String path, String httpVersion, List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static List<NameValuePair> getQueryParams(String urlString) throws URISyntaxException {
        URI uri = new URI(urlString);
        String query = uri.getQuery();
        return URLEncodedUtils.parse(query, null);
    }

    public static String getQueryParam(List<NameValuePair> queryParams, String name) {
        NameValuePair queryParam = null;
        for (NameValuePair param : queryParams) {
            if (name.equals(param.getName())) {
                queryParam = param;
            }
        }
        if (queryParam == null) {
            return null;
        } else {
            return queryParam.getValue();
        }
    }


}
