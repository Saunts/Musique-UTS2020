package id.kuro.musique;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

public class profile extends Activity {
    private ImageView profilepic;
    private androidx.appcompat.widget.Toolbar sub;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

    }

    public void backtomain(){
        Intent k = new Intent(this, MainActivity.class);
        startActivity(k);
    }
}
