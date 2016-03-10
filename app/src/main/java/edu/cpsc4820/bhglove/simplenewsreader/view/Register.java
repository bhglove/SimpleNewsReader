package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.AccessDatabase;

public class Register extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirm;
    private EditText mFirstName;
    private EditText mLastName;

    private String email;
    private String password;
    private String fname;
    private String lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Initialize variables
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirm = (EditText) findViewById(R.id.passwordConfirm);
        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.lastName);

        //Set the email to the user's input email and set the password field as the next input
        String email = getIntent().getExtras().getString("Email");
        if(setEmail(email)) {
            mPassword.requestFocus();
        }

        progressBar = new ProgressBar(getApplicationContext());
    }

    private void showProgress(boolean value){
        if(value){
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    public void register(View v){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        fname = mFirstName.getText().toString();
        lname = mLastName.getText().toString();

        if(!TextUtils.equals(mPassword.getText(), mConfirm.getText())){
            mConfirm.setError("Passwords do not match.");
            mConfirm.requestFocus();
        }
        else {
            RegisterUserTask register = new RegisterUserTask();
            register.execute();
        }
    }

    private boolean setEmail(String email){
        boolean retVal = false;
        if(email != null) {
            mEmail.setText(email);
            mEmail.setSelection(email.length());
            retVal = true;
        }
        return retVal;
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected void onPreExecute(){
            showProgress(true);
        }
        @Override
        protected void onPostExecute(Integer value){
           showProgress(false);
           if(value == 1){
               finish();
               Intent intent = new Intent(Register.this, NewsFeed.class);
               startActivity(intent);
           }
           else {
               Log.d("Thread", "value is: " + value);
           }
        }
        @Override
        protected Integer doInBackground(Void ...params) {
            Integer retVal =  0;
            try{
                AccessDatabase access = AccessDatabase.getInstance();
                retVal = access.executeRegisterUser(email, password, fname, lname);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return retVal;
        }
    }
}
