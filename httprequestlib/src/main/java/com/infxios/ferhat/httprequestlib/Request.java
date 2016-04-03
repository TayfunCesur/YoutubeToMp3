package com.infxios.ferhat.httprequestlib;

import java.util.Hashtable;

/**
 * Created by ferhat on 05.01.2016.
 */
public abstract class Request{
    protected String _url = null;
    protected String _body = null;
    protected Hashtable<String,String> _requestHeaders = new Hashtable<String,String>();
    private boolean _isInvoked = false;
    private boolean _keepHeaders = false;
    protected HttpRequestFactory _requestFactory = null;
    protected RequestType requestType = RequestType.GET;
    protected volatile boolean _isBusy = false;
    protected boolean _isSecure = false;

    protected boolean _timeout_on = false;
    protected int timeOut = 100;

    protected void setHttpRequestFactory(HttpRequestFactory factory){
        _requestFactory = factory;
    }



    public void execute(){

        _isInvoked = true;

        if(!_keepHeaders){
            _requestHeaders.clear();
        }
    }

    public void execute(String url){
        _url = url;
        execute();
    }

    public void execute(String url, String body){
        _url = url;
        _body = body;
        execute();
    }

    public Request secure(boolean isSecure){
        _isSecure = isSecure;
        return this;
    }

    public Request keepHeaders(boolean keepIfTrue){
        _keepHeaders = keepIfTrue;
        return this;
    }

    public Request addHeaderValue(String key, String value){
        _requestHeaders.put(key,value);
        return this;
    }

    public boolean isInvoked(){
        return _isInvoked;
    }


    public Request setTimeOut(int timeoutMilis){

        _timeout_on = timeoutMilis > 0;
        timeOut = timeoutMilis;
        return this;

    }

    public String getUrl(){
        return _url;
    }

    public void setUrl(String url){
        _url = url;
    }

    public String getBody(){
        return _body;
    }

    public Request setBody(String body){
        _body = body;
        return this;
    }

    public RequestType getRequestType(){
        return requestType;
    }

    public boolean isBusy(){
        return _isBusy;
    }
}
