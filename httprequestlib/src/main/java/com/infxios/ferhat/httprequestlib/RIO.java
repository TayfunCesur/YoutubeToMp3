package com.infxios.ferhat.httprequestlib;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ferhat on 06.01.2016.
 */
public class RIO {
    public HttpResponseType result = HttpResponseType.HTTP_FORBIDDEN;
    public JSONObject response_json = null;
    public String response_str = null;
    public List<Exception> exceptions = null;
    public String responseUrl = null;
    public Request request = null;

}
