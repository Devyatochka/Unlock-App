package com.devyatochka.hackatonfinal;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexbelogurow on 01.04.17.
 */

public class GetSMSTask extends AsyncTask<String, Void, String> {
    private String server = "http://devyatochka.fvds.ru:8080/otp?";

    public GetSMSTask(String number) {
        this.server = server + "number=" + number;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(server);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            return sb.toString();
        } catch (MalformedURLException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.i("JSON_GET", result);


        // To do in parse JSON //
        //this.activity.parseJson(result);
    }
}
