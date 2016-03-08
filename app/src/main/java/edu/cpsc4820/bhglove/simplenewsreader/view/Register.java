package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.cpsc4820.bhglove.simplenewsreader.R;

public class Register extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirm;
    private EditText mFirstName;
    private EditText mLastName;
    private Button mRegisterButton;

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
        mRegisterButton = (Button) findViewById(R.id.registerButton);
        //Set the email to the user's input email and set the password field as the next input
        String email = getIntent().getExtras().getString("Email");
        if(setEmail(email)) {
            mPassword.requestFocus();
        }
    }

    public void register(View v){

        Toast.makeText(getApplicationContext(), "Register!", Toast.LENGTH_SHORT).show();
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
}
