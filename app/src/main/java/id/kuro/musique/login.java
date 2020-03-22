package id.kuro.musique;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class login extends Activity {
    private EditText username, password;
    private Button submit;
    private TextView failed;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        submit = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        failed = (TextView)findViewById(R.id.fail);
    }

    public void login(View view){
        System.out.println("login cuk");
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if(user.equals("musique") && pass.equals("musique123")){
            showPopup();
            Intent k = new Intent(this, MainActivity.class);
            startActivity(k);
        }
        else{
            failed.setVisibility(View.VISIBLE);
        }
    }

    public void showPopup(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.popup, null, false), 100, 100, true);
        pw.showAtLocation(findViewById(R.id.login), Gravity.CENTER, 0, 0);
    }

}
