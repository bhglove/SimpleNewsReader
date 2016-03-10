package edu.cpsc4820.bhglove.simplenewsreader.controller;

import android.content.ContentValues;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Benjamin Glover on 3/7/2016.
 *
 * Resources:
 * POST HttpUrlConnection
 * http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
 */
public class AccessDatabase {
    private static AccessDatabase access = null;
    private static final String dbUrl =
            "http://people.cs.clemson.edu/~bhglove/CPSC482/Assignments/Assignment4Portal/";

    private  AccessDatabase(){

    }

    public static AccessDatabase getInstance(){
        if(access == null) access = new AccessDatabase();
        return access;
    }

    private String getFirstName(String email){
        String retVal = " ";
        try {
            String variables = "email=" + email;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "getFromEmail.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Name", "Result: " + builder.toString());
            retVal = jObject.getString("fname");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }

    private String getLastName(String email){
        String retVal = " ";
        try {
            String variables = "email=" + email;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "getFromEmail.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Name", "Result: " + builder.toString());
            retVal = jObject.getString("lname");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }


    public int executeRegisterUser(String email, String password, String fname, String lname){
        int retVal = 0;
        try {
            String variables = "email=" + email + "&password=" + password + "&fname=" + fname +
                    "&lname=" +lname;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "register.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            Log.d("Register", "Result: " + builder.toString());
            retVal = jObject.getInt("result");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }
    /**
     * Connects to an authentication script via method post
     * @param email
     * @param password
     * @return 1 - for successful 0 - for wrong password -1 - for the account does not exist.
     */
    public int executeLogin(String email, String password){
        int retVal = 0;
        try {
            String variables = "email=" + email + "&password=" + password;
            byte[] postData = variables.getBytes();
            URL url = new URL(dbUrl + "login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line);
            }
            reader.close();
            JSONObject jObject = new JSONObject(builder.toString());
            retVal = jObject.getInt("result");
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("URL", "Malformed Url: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IO", "IO Exception: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON", "JSON ERROR: " + e.getLocalizedMessage());
        }
        return retVal;
    }
}
