package com.example.trang.mp3online.api;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Trang on 5/24/2017.
 */

public class Mp3API {
    private static final String API_KEY = "b319bd16be6d049fdb66c0752298ca30",
            SCHEME = "http",
            HOST = "api.mp3.zing.vn",
            ROOT_PATH = "api/mobile/playlist";

    public static final String GET_PLAY_LIST_API = "getplaylistbygenre",
            GET_SONG_LIST_API = "getsonglist", GET_PLAY_TOTAL_PLAY = "total_play",
            GET_PLAY_HOT = "hot", GET_PLAY_RELEASE_DATE = "release_date";


    private OnResponseJsonCallback onResponseJsonCallback;

    public void getAlbumList(String param) {
        new RequestTask(GET_PLAY_LIST_API, param).execute();
    }

    public void setOnResponseJsonCallback(OnResponseJsonCallback onResponseJsonCallback) {
        this.onResponseJsonCallback = onResponseJsonCallback;
    }

    public void getSongList(String param) {
        new RequestTask(GET_SONG_LIST_API, param).execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, String> {

        private final String api;
        private final String param;

        public RequestTask(String api, String param) {
            this.api = api;
            this.param = param;
        }

        @Override
        protected String doInBackground(Void... params) {
            delay(1000);
            return request(api, param);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (onResponseJsonCallback != null) {
                onResponseJsonCallback.onResponse(jsonObject);
            }
        }
    }

    private void delay(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String request(String api, String param) {
        String response = "";
        HttpURLConnection httpURLConnection = null;

        Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .appendEncodedPath(ROOT_PATH)
                .appendPath(api)
                .appendQueryParameter("keycode", API_KEY)
                .appendQueryParameter("fromvn", "true")
                .appendQueryParameter("requestdata", param);

        try {
            httpURLConnection = (HttpURLConnection) new URL(builder.build().toString()).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type:", "application/json;charset=utf-8");
            httpURLConnection.setConnectTimeout(6000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while (!TextUtils.isEmpty(line = reader.readLine())) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                response = stringBuilder.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }

    public static String makeRequestData(int id, int length, String type) {
        return makeRequestData(id, 0, length, type);
    }

    public static String makeRequestData(int startAt, String type) {
        return makeRequestData(9, startAt, 16, type);
    }

    public static String makeRequestData(int id, int startAt, int length, String type) {
        JSONObject jsonRequestData = new JSONObject();
        try {
            jsonRequestData.put("length", length);
            jsonRequestData.put("id", id);
            jsonRequestData.put("start", startAt);
            jsonRequestData.put("sort", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequestData.toString();
    }

    public interface OnResponseJsonCallback {
        void onResponse(JSONObject jsonObject);
    }

}
