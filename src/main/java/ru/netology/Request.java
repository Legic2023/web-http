package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Request {
    private String method, params, path;
    private List<NameValuePair> queryParams;

    public Request(String method, String params, String path) throws URISyntaxException {
        this.method = method;
        this.params = params;
        this.path = path;
        this.queryParams = URLEncodedUtils.parse(new URI(params).getQuery(), null);
    }

    public String getPath() {
        return this.path;
    }

    public List<NameValuePair> getQueryParams(){
        return  this.queryParams;
    }

    public String getQueryParam(String name) {
        NameValuePair queryParam = null;
        for (NameValuePair param : this.queryParams) {
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
