package com.example.a99zan.musicplayer;

/**
 * Created by 99zan on 2018/1/11.
 */

public class MusicBean {

    private String music;
    private String name;

    public MusicBean() {
    }

    public MusicBean(String music, String name) {
        this.music = music;
        this.name = name;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
