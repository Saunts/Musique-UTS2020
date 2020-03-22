package id.kuro.musique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.*;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.content.*;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.view.MenuItem;
import android.view.View;

import android.os.Bundle;
import android.widget.TextView;

import id.kuro.musique.MusicService.MusicBinder;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_WAKE_LOCK = 0;
    private ArrayList<music> listmusic;
    private LinearLayout layout;
    private ListView musicview;
    private MusicService musicsvc;
    private Intent playIntent;
    private musicadapter musicadapt = null;
    private boolean musicBound=false, paused=false, playbackpaused=false;
    public Controller controller;
    private androidx.appcompat.widget.Toolbar sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.linear);
        sub = (Toolbar) findViewById(R.id.toolbar);
        sub.setTitle("More Option");
        setSupportActionBar(sub);
        sub.setBackgroundColor(Color.parseColor("#FFFFFF"));

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant

            return;
        }

        //check for write external storage permission
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant

            return;
        }

        //check for wake-lock permission
        if (checkSelfPermission(Manifest.permission.WAKE_LOCK)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                Manifest.permission.WAKE_LOCK)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.WAKE_LOCK},
                MY_PERMISSIONS_REQUEST_WAKE_LOCK);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant

            return;
        }

        musicview = (ListView) findViewById(R.id.songlist);
        listmusic = new ArrayList<music>();

        getMusicList();

        Collections.sort(listmusic, new Comparator<music>() {
            @Override
            public int compare(music o1, music o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


        musicadapt = new musicadapter(this, listmusic);
        musicview.setAdapter(musicadapt);

        setController();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu submenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submenu, submenu);
        return true;
    }

    @Override
    protected void onDestroy(){
        stopService(playIntent);
        musicsvc = null;
        super.onDestroy();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;

            musicsvc = binder.getService();

            musicsvc.setList(listmusic);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void getMusicList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor != null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do{
                long ID = musicCursor.getLong(idColumn);
                String Title = musicCursor.getString(titleColumn);
                listmusic.add(new music(ID, Title));
            }while(musicCursor.moveToNext());
        }
    }

    public void picked(View view){
        musicsvc.setmusic(Integer.parseInt(view.getTag().toString()));
        musicsvc.play();
        if(playbackpaused){
            setController();
            playbackpaused=false;
        }
        controller.show(0);
    }


    public void toProfile(MenuItem item) {
        Intent k = new Intent(this, profile.class);
        startActivity(k);
    }

    public void sortdesc(MenuItem item){
        Collections.sort(listmusic, new Comparator<music>(){
            @Override
            public int compare(music o1, music o2) {
                return o2.getTitle().compareTo(o1.getTitle());
            }
        });
        musicadapt.notifyDataSetChanged();
//        musicadapter musicadapt = new musicadapter(this, listmusic);
//        musicview.setAdapter(musicadapt);
    }

    public void sortasc(MenuItem item){
        Collections.sort(listmusic, new Comparator<music>() {
            @Override
            public int compare(music o1, music o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        musicadapt.notifyDataSetChanged();
//        musicadapter musicadapt = new musicadapter(this, listmusic);
//        musicview.setAdapter(musicadapt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.shuffle:
                break;
            case R.id.end:
                stopService(playIntent);
                musicsvc = null;
                System.exit(0);
                break;
            case R.id.profile:
                Intent k = new Intent(this, profile.class);
                startActivity(k);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void playNext(){
        musicsvc.playNext();
        if(playbackpaused){
            setController();
            playbackpaused=false;
        }
        controller.show(0);
    }

    private void playPrev(){
        musicsvc.playPrev();
        if(playbackpaused){
            setController();
            playbackpaused=false;
        }
        controller.show(0);
    }

    private void setController(){
        controller = new Controller(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.songlist));
        controller.setEnabled(true);
    }

    @Override
    public void start() {
        musicsvc.go();
    }

    @Override
    public void pause() {
        playbackpaused=true;
        musicsvc.pause();
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop(){
        controller.hide();
        super.onStop();
    }

    @Override
    public int getDuration() {
        if(musicsvc!=null && musicBound && musicsvc.isPlay()) return musicsvc.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicsvc!=null && musicBound && musicsvc.isPlay()) return musicsvc.getPos();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicsvc.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicsvc!=null && musicBound) return musicsvc.isPlay();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
