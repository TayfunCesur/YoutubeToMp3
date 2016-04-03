package com.infxios.ferhat.http_request;

import android.app.Activity;
import android.os.Bundle;

import com.infxios.ferhat.httprequestlib.HttpRequestFactory;
import com.infxios.ferhat.httprequestlib.RIO;
import com.infxios.ferhat.httprequestlib.RequestType;

import org.json.JSONObject;

/**
 * Created by ferhat on 06.01.2016.
 */
public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        HttpRequestFactory factory = new HttpRequestFactory(getApplicationContext()) {
            @Override
            public void Happens(RIO rio) {

            }
        };

        factory.createRequest(RequestType.POST,"https://httpbin.org/post")
                .secure(true)
                .setBody("{\"test\":\"object\"}")
                .execute();


    }
}
