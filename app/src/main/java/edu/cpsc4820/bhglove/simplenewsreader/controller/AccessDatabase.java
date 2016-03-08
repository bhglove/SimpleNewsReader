package edu.cpsc4820.bhglove.simplenewsreader.controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
            String urlParameters = "email='" + email + "'&password='" +
                    URLEncoder.encode(password, "UTF-8") + "'";
            HttpURLConnection connection;
            URL url = new URL(dbUrl + "login.php");
            Log.d("Access", urlParameters);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Language", "en-US");
            Log.d("Access", urlParameters);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());

            wr.writeUTF(urlParameters);
            wr.flush();
            wr.close();
            Log.d("Access", connection.getURL().toString());
            Log.d("Access", connection.getRequestMethod());

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
           Log.d("Access", "Something funky happend" + e.getLocalizedMessage());
            e.printStackTrace();


        } finally {
            return retVal;
        }
    }
}
