package com.xionces.youtubetomp3;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private NumberProgressBar bnp;
    private TextInputEditText mUrlText;
    private TextInputLayout mLayout;
    private AppCompatImageView mThumbNailImage;
    private AppCompatButton mConvert;
    private SwitchCompat mSwitch;
    private SwipeRefreshLayout mSwipeRefresh;
    private String mUrl,video_id,video_title;
    private AppCompatImageView ok;
    private Boolean success = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrl = mUrlText.getText().toString();
                if (!mUrl.equals("")) {
                    video_id = extractYTId(mUrl);
                    mSwipeRefresh.setRefreshing(true);
                    Picasso.with(MainActivity.this).load("http://img.youtube.com/vi/" + video_id + "/0.jpg").into(mThumbNailImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            mThumbNailImage.setImageResource(R.drawable.signs);
                        }
                    });
                    mSwipeRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.warning), Toast.LENGTH_SHORT).show();
                }
            }
        });


        mConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSwipeRefresh.setRefreshing(true);
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://www.youtubeinmp3.com/fetch/?format=JSON&video=https://www.youtube.com/watch?v="+video_id)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    video_title = obj.getString("title");
                    if (!mUrl.equals("")) {
                        new DownloadFileAsync().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    private void initVariables()
    {
        bnp = (NumberProgressBar)findViewById(R.id.number_progress_bar);
        mUrlText = (TextInputEditText) findViewById(R.id.url);
        mThumbNailImage = (AppCompatImageView) findViewById(R.id.thumbnail);
        mConvert = (AppCompatButton) findViewById(R.id.convert);
        mSwitch = (SwitchCompat) findViewById(R.id.check);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setEnabled(false);
        TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        mSwipeRefresh.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId) + 100);

        mSwipeRefresh.setColorSchemeResources(
                R.color.pink_500, R.color.indigo_500,
                R.color.orange_500, R.color.green_500);



        mLayout = (TextInputLayout) findViewById(R.id.view);
        ok = (AppCompatImageView) findViewById(R.id.ok);
    }



    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }






    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String ... aurl) {

            try {
                URL u = new URL("http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v="+video_id);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                int lenghtOfFile = c.getContentLength();
                File folder = new File(Environment.getExternalStorageDirectory() + "/YoutubeToMp3/");

                FileOutputStream f;

                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    f = new FileOutputStream("/sdcard/YoutubeToMp3/"+video_title+".mp3");
                } else {
                    f = new FileOutputStream("/sdcard/YoutubeToMp3/"+video_title+".mp3");
                }
                InputStream in = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;

                while ((len1 = in.read(buffer)) > 0) {
                    total += len1;
                    publishProgress("" + (int)((total*100)/lenghtOfFile));
                    f.write(buffer, 0, len1);
                }
                f.close();

            } catch (Exception e) {
                String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
                Log.e("TAYF",err);
            }

            return null;
        }

        protected void onProgressUpdate(String ... progress) {
            Log.d("TAYF", progress[0]);
            bnp.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            mSwipeRefresh.setRefreshing(false);
            if (mSwitch.isChecked())
            {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory() + "/YoutubeToMp3/" + video_title + ".mp3");
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
            else {
                String filepath = Environment.getExternalStorageDirectory() + "/YoutubeToMp3/" + video_title + ".mp3";
                Toast.makeText(getApplicationContext(), "Downloaded path is " + filepath, Toast.LENGTH_LONG).show();
            }
        }
    }
}
