package edu.cpsc4820.bhglove.simplenewsreader.controller;

import android.content.ContentValues;
import android.util.JsonReader;
import android.util.Log;

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
import java.util.List;

/**
 * Created by Benjamin Glover on 3/7/2016.
 */
public class AccessDatabase {
    private static final String dbUrl =
            "http://people.cs.clemson.edu/~bhglove/CPSC482/Assignments/Assignment4Portal/";

    public AccessDatabase(){

    }

    public int executeLogin(String email, String password){
        int retVal = 0;
        try {
            //Create connection
            String urlParameters = "email" + URLEncoder.encode(email, "UTF-8") + "password" +
                    URLEncoder.encode(password, "UTF-8");
            HttpURLConnection connection;
            URL url = new URL(dbUrl + "login.php");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            Log.d("Access", "Response equals: " + response.toString());
            connection.disconnect();
            return -2;

        } catch (Exception e) {
           Log.d("Access", "Something funky happend");
            e.printStackTrace();


        } finally {
            return retVal;
        }
    }
}
