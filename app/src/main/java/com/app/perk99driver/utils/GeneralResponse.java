package com.app.perk99driver.utils;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;


public class GeneralResponse {

    public static final String TAG = GeneralResponse.class.getSimpleName();
    private JSONObject response;
    ResponseBody responseBody;
    JSONArray response_array;
    JSONObject response_object;

    int status_code;

    public GeneralResponse(Response<ResponseBody> rb) {

        status_code=rb.code();
        this.responseBody = (rb.body() != null) ? rb.body() : rb.errorBody();

        try {
            this.response = new JSONObject(responseBody.string());
            Log.e("ddd","ddd "+response);
        } catch (Exception je) {

        }
    }


    public String getStringValue(String Key) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());

        String val = response.getString(Key);
        return val;
    }

    public boolean checkStatus() throws JSONException {

        if(status_code==200&&(response.getString("status").equals("true") ||response.getString("status").equals("200"))){

            return true;
        }else{
            return false;
        }
    }

    public GeneralResponse(ResponseBody rb) {
        this.responseBody = rb;

        try {
            this.response = new JSONObject(responseBody.string());
        } catch (Exception je) {

        }
    }

    public boolean getResponseObject(Activity activity) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());


        if (response.getString("status").equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean getResponseObject() throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());

        return  response.getBoolean("status");

    }
    public JSONObject getResponse_object() throws JSONException {
        if (response == null)
            response = new JSONObject(responseBody.toString());
        return  response;
    }





    public GeneralResponse(JSONArray response) {
        try {
            this.response_array = response;
        } catch (Exception je) {

        }
    }



    public GeneralResponse(JSONObject response) {
        try {
            this.response_object = response;
        } catch (Exception je) {

        }
    }

    JsonObject jsonObject;

    public GeneralResponse(JsonObject body) {
        this.jsonObject = body;
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject_ = new JSONObject(jsonObject.toString());
        return jsonObject_;
    }




    public boolean checkResponse() throws JSONException {
        JSONObject respnseCheck = new JSONObject(response.toString());
        if (respnseCheck.getBoolean("status")) {
            return true;
        } else {
            return false;
        }
    }

    public JSONObject getResponse() {
        return response;
    }

    public <T> List<T> getDataAsList(String key, Class<T> classOFT) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        JSONArray ja = response.getJSONArray(key);
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }
/*

    public <T> List<T> getDataAsObjectList(String key, Class<T> classOFT) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        JSONObject ja = response.getJSONObject(key);
        Gson gson = new Gson();
        List<T> list = new ArrayList<>();
        Iterator<?> keys = ja.keys();


        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }
*/


    public <T> List<T> getDataAsListDecoded(JSONObject jsonObject, String key, Class<T> classOFT) throws JSONException {
       // if (response == null) response = new JSONObject(responseBody.toString());
        JSONArray ja = jsonObject.getJSONArray(key);
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }




    public JSONArray getResponse_array(String key) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        JSONArray ja = response.getJSONArray(key);
        return ja;
    }

    public <T> List<T> getJSONArrayAsList(Class<T> classOFT) throws JSONException {
        JSONArray ja = response_array;
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }

    public <T> T getJSONObjectAs(Class<T> classOFT) throws JSONException {
        JSONObject ja = response_object;
        Gson gson = new Gson();
        return gson.fromJson(ja.toString(), (Type) classOFT);

    }

    public <T> List<T> getDataAs(String key, Class<T> classOFT) throws JSONException {
        if (response == null) response =response_object;
        JSONArray ja = response.getJSONArray(key);
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }







    public <T> T getData(String key, Class<T> dataOFSET) throws JSONException {

        if (response == null) response = new JSONObject(responseBody.toString());


        Gson gson = new Gson();

        return gson.fromJson(response.getString(key), (Type) dataOFSET);
    }

    public <T> T getData(String key, int posJson, String key2, Class<T> dataOFSET) throws JSONException {

        if (response == null) response = new JSONObject(responseBody.toString());


        Gson gson = new Gson();

        return gson.fromJson(response.getJSONArray(key).getJSONObject(posJson).getString(key2), (Type) dataOFSET);
    }


    public JSONObject getResponseObject(String s) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        return response.getJSONObject(s);
    }

    public <T> List<T> getJSonArray(JSONArray ja, Class<T> dataOFSET) throws JSONException {
        Gson gson = new Gson();
        int len = ja.length();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), dataOFSET));
        }
        return list;
    }


}
