package com.example.a99zan.musicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    /**
     * 规定开始音乐、暂停音乐、结束音乐的标志
     */
    public static final int PLAT_MUSIC = 1;
    public static final int PAUSE_MUSIC = 2;
    public static final int STOP_MUSIC = 3;
    public static final int SEEK_TO_MUSIC = 4;
    public static final int NEXT_MUSIC = 5;
    public static final int PRE_MUSIC = 6;
    public static final int UPDATA_MUSIC = 7;

    public static final String ACTION_MUSIC_ACTIVITY_RECEIVER = "100";
    public static final String ACTION_MUSIC_SERVICE_RECEIVER = "200";

    /**
     * 当前是否在播放
     */
    boolean isPlay = false;

    @BindView(R.id.currenttime)
    TextView playTime;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.play_or_pause)
    Button play;
    @BindView(R.id.shangyiqu)
    Button shangyiqu;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.wodexiazai)
    Button wodexiazai;
    @BindView(R.id.updata)
    Button upData;

    private MusicBroadCastReceiver receiver;
    /**
     * 歌曲链接列表
     */
    ArrayList<String> musicList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        verifyStoragePermissions(this);

        musicList.add("http://abv.cn/music/红豆.mp3");
        musicList.add("http://abv.cn/music/千千阙歌.mp3");
        musicList.add("http://abv.cn/music/光辉岁月.mp3");

        receiver = new MusicBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MUSIC_ACTIVITY_RECEIVER);
        registerReceiver(receiver, intentFilter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress(), SEEK_TO_MUSIC);
            }
        });

    }

    @OnClick({R.id.updata, R.id.wodexiazai, R.id.shangyiqu, R.id.play_or_pause, R.id.next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.updata:
                showNormalDialog();
                break;
            case R.id.wodexiazai:
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, UpdataActivity.class);
                startActivity(intent1);
                break;
            case R.id.shangyiqu:

                break;
            case R.id.play_or_pause:
                Drawable drawable = null;
                if(isPlay){
                    isPlay = false;
                    playingmusic(PAUSE_MUSIC);
                    drawable = getResources().getDrawable(R.mipmap.ic_play_bar_btn_play);
                    play.setBackground(drawable);
                }else{
                    isPlay = true;
                    playingmusic(PLAT_MUSIC);
                    drawable = getResources().getDrawable(R.mipmap.ic_play_bar_btn_pause);
                    play.setBackground(drawable);
                }
                break;
            case R.id.next:
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_MUSIC_SERVICE_RECEIVER);
                intent.putExtra("type", NEXT_MUSIC);
                sendBroadcast(intent);
                break;
        }
    }

    public class MusicBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Drawable drawable = null;
            switch (intent.getIntExtra("type", -1)){
                case PLAT_MUSIC:
                    drawable = getResources().getDrawable(R.mipmap.ic_play_bar_btn_play);
                    play.setBackground(drawable);
                    break;
                case PAUSE_MUSIC:
                    drawable = getResources().getDrawable(R.mipmap.ic_play_bar_btn_pause);
                    play.setBackground(drawable);
                    break;
            }
            int max = intent.getIntExtra("playerTimeMax", -1);
            int current = intent.getIntExtra("playerCurrentTime", -1);
            seekBar.setMax(max);
            seekBar.setProgress(current);
            playTime.setText(Utils.getCurrentTime(current));
            if("wancheng".equals(intent.getStringExtra("bofangwancheng"))){
                isPlay = false;
                drawable = getResources().getDrawable(R.mipmap.ic_play_bar_btn_play);
                play.setBackground(drawable);
                seekBar.setProgress(0);
            }
        }
    }

    private void seekTo (int progress, int type){
        Intent intent=new Intent(this, MusicService.class);
        intent.putExtra("type",type);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    private void playingmusic(int type) {
        //启动服务，播放音乐

        Intent intent = new Intent(this, MusicService.class);
        intent.putStringArrayListExtra("musicList", musicList);
        intent.putExtra("type",type);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent intent = new Intent(this, MusicService.class);
//        stopService(intent);
        unregisterReceiver(receiver);
    }

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否下载当前音频");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_MUSIC_SERVICE_RECEIVER);
                        intent.putExtra("type", UPDATA_MUSIC);
                        sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

}
