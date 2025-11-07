package com.romanm.smsserver;

import android.app.ProgressDialog;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.romanm.smsserver.Const.Constants;
import com.romanm.smsserver.email.SendMailTask;
import com.romanm.smsserver.email.Sendmail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmaiActivity extends AppCompatActivity {

    private EditText emailText;
    private TextView description;
    private Button btnSendEmail;

    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        ViewsInit();
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void ViewsInit() {
        emailText = (EditText)findViewById(R.id.edEmail);
        btnSendEmail = (Button)findViewById(R.id.btnSendEmail);
        description = findViewById(R.id.textDescription);

        description.setText(getString(R.string.info_source_code));


        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");

                List<String> rec = new ArrayList<String>();
                if (!emailText.getText().toString().isEmpty()) {
                    if (checkEmail(emailText.getText().toString())) {
                        rec.add(emailText.getText().toString());
                        rec.add("rom3889@yandex.ru");
                        new SendMailTask(EmaiActivity.this).execute("transporter-app@yandex.ru",
                                "9laermAA$", rec, getString(R.string.email_subject), Constants.EMAIL_BODY);
                    } else {
                        showToast(getString(R.string.wrong_email_format));
                    }
                } else {
                    showToast(getString(R.string.empty_email_address));
                }
            }
        });
    }

}
