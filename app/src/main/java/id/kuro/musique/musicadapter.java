package id.kuro.musique;

import android.text.Layout;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import android.content.Context;

public class musicadapter extends BaseAdapter {
    private ArrayList<music> musics;
    private LayoutInflater musicInf;

    public musicadapter(){

    }

    public musicadapter(Context c, ArrayList<music> musics){
        this.musics = musics;
        this.musicInf = LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout musicLay = (LinearLayout)musicInf.inflate(R.layout.music, parent, false);
        TextView musicView = musicLay.findViewById(R.id.music_title);

        music currentmusic = musics.get(position);
        musicView.setText(currentmusic.getTitle());

        musicLay.setTag(position);
//        musicLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return musicLay;
    }
}
