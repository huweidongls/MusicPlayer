package com.example.a99zan.musicplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdataActivity extends AppCompatActivity {

    @BindView(R.id.listview)
    ListView listView;
    MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata);

        ButterKnife.bind(this);
        
        init();

    }

    private void init() {

        adapter = new MusicAdapter(this, getMusicData());
        listView.setAdapter(adapter);

    }

    private List<MusicBean> getMusicData (){

        String[] ext = { ".mp3" };
        File file = new File(Environment.getExternalStorageDirectory(), "/com/99zan/download");
        List<String> music = new ArrayList<>();
        List<MusicBean> mu = new ArrayList<>();
        music = Utils.searchMp3Infos(file, ext);
        for(int i = 0; i<music.size(); i++){
            String name = music.get(i).substring(music.get(i).lastIndexOf("/") + 1);
            String name1 = name.substring(0, name.indexOf("."));
            mu.add(new MusicBean(music.get(i), name1));
        }
        return mu;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
