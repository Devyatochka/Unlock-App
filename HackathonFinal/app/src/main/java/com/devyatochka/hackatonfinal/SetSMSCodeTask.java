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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexbelogurow on 01.04.17.
 */

public class SetSMSCodeTask extends AsyncTask<String, Void, String> {
    private String server = "http://devyatochka.fvds.ru:8080/otp?otp=";

    public SetSMSCodeTask(String otpNumber) {
        this.server = server + otpNumber;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(server);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            //JSONObject body = new JSONObject();

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            //wr.write(body.toString());
            wr.flush();

            InputStream in = new BufferedInputStream(con.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
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

        Log.i("JSON_POST", result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            //JSONObject text = jsonObject.getJSONObject("state");

            if (jsonObject.getString("state").equals("ok")) {
                MainActivity.isLogin = jsonObject.getJSONObject("response").getBoolean("isLogin");

                //Log.i("bool", jsonObject.getJSONObject("response").getBoolean("isLogin") + "");
                //Log.i("string", jsonObject.getJSONObject("response").getString("isLogin"));
            }
            else {
                MainActivity.isLogin = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
