package id.kuro.musique;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private MediaPlayer player;
    private ArrayList<music> musics;
    private int musicpos;
    private final IBinder musicbind = new MusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicbind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onCreate(){
        super.onCreate();
        musicpos = 0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
            PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<music> themusic){
        musics = themusic;
    }

    public class MusicBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void setmusic(int pos){
        musicpos = pos;
    }

    public void play(){
        player.reset();
        music play = musics.get(musicpos);
        long current = play.getId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void playPrev(){
        musicpos--;
        if(musicpos < 0) musicpos=musics.size()-1;
        play();
    }

    public void playNext() {
        musicpos++;
        if (musicpos > musics.size() - 1) musicpos = 0;
        play();
    }

    public int getPos(){return player.getCurrentPosition();}
    public int getDur(){return player.getDuration();}
    public boolean isPlay(){return player.isPlaying();}
    public void pause(){player.pause();}
    public void seek(int pos){player.seekTo(pos);}
    public void go(){player.start();}
}
