package com.example.a99zan.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 99zan on 2018/1/9.
 */

public class MusicService extends Service {

    /**
     * 用于播放音乐等媒体资源
     */
    private MediaPlayer mediaPlayer;
    /**
     * 标志判断播放歌曲是否是停止之后重新播放，还是继续播放
     */
    private boolean isStop = true;
    /**
     * 判断是否暂停
     */
    private boolean isPause = false;

    /**
     * 当前是否在播放
     */
    boolean isPlay = false;

    private MusicServiceBroadcastReceiver receiver;

    private static final int REQUEST_CODE = 300;
    private static final String ACTION_PLAY = "play";

    Notification notification;
    private MediaCompletionListener listener = new MediaCompletionListener();

    /**
     * 歌曲链接数组
     */
    ArrayList<String> musicList = new ArrayList<>();

    /**
     * 当前播放的歌曲index
     */
    private int currentIndex = 0;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    //在此方法中服务被创建
    @Override
    public void onCreate() {
        super.onCreate();

        addNotification();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            //为播放器添加播放完成时的监听器
        }
        mediaPlayer.setOnCompletionListener(listener);

        receiver = new MusicServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_MUSIC_SERVICE_RECEIVER);
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, intentFilter);

    }

    private class MediaCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            try {
                nextMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNotification() {

        RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                R.layout.notification_layout);// 获取remoteViews（参数一：包名；参数二：布局资源）
        remoteViews.setImageViewResource(R.id.notification_play, R.mipmap.ic_play_bar_btn_pause);
        remoteViews.setTextViewText(R.id.notification_name, "红豆");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(remoteViews);
        notification = builder.build();// 获取构建好的通知--.build()最低要求在

        Intent intent = new Intent(ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext()
                , REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play, pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);

    }


    private class MusicServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent();
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    if (isPlay) {
                        isPlay = false;
                        notification.contentView.setImageViewResource(R.id.notification_play, R.mipmap.ic_play_bar_btn_play);
                        notificationManager.notify(2, notification);
                        intent1.setAction(MainActivity.ACTION_MUSIC_ACTIVITY_RECEIVER);
                        intent1.putExtra("type", MainActivity.PLAT_MUSIC);
                        sendBroadcast(intent1);
                        pause();
                    } else {
                        isPlay = true;
                        notification.contentView.setImageViewResource(R.id.notification_play, R.mipmap.ic_play_bar_btn_pause);
                        notificationManager.notify(2, notification);
                        intent1.setAction(MainActivity.ACTION_MUSIC_ACTIVITY_RECEIVER);
                        intent1.putExtra("type", MainActivity.PAUSE_MUSIC);
                        sendBroadcast(intent1);
                        play(currentIndex);
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    Log.e("111", "收到锁屏广播");
                    Intent lockscreen = new Intent(MusicService.this, SuopingActivity.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockscreen);
                    break;
                case MainActivity.ACTION_MUSIC_SERVICE_RECEIVER:
                    if (intent.getIntExtra("type", -1) == MainActivity.NEXT_MUSIC) {
                        try {
                            nextMusic();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (intent.getIntExtra("type", -1) == MainActivity.UPDATA_MUSIC) {
                        Log.e("111", "开始下载!");
                        final NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context);
                        builder1.setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("通知")
                                .setContentText("正在下载...");
                        final Notification notification1 = builder1.build();
                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        DownloadUtil.get().download(musicList.get(currentIndex), "/com/99zan/download", new DownloadUtil.OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess() {
//                                builder1.setProgress(0, 0, false);
                                builder1.setContentText("下载成功！");
                                manager.notify(3, builder1.build());
                                Log.e("111", "下载成功！");
                            }

                            @Override
                            public void onDownloading(int progress) {
                                builder1.setProgress(100, progress, false);
                                manager.notify(3, builder1.build());
                            }

                            @Override
                            public void onDownloadFailed() {
                                Log.e("111", "下载失败！");
                                builder1.setContentText("下载失败！");
                                manager.notify(3, builder1.build());
                            }
                        });
                    }
                    break;
            }

            String s = intent.getAction();
            Log.e("111", s + "1");
        }
    }

    /**
     * 在此方法中，可以执行相关逻辑，如耗时操作
     *
     * @param intent  :由Activity传递给service的信息，存在intent中
     * @param flags   ：规定的额外信息
     * @param startId ：开启服务时，如果有规定id，则传入startid
     * @return 返回值规定此startservice是哪种类型，粘性的还是非粘性的
     * START_STICKY:粘性的，遇到异常停止后重新启动，并且intent=null
     * START_NOT_STICKY:非粘性，遇到异常停止不会重启
     * START_REDELIVER_INTENT:粘性的，重新启动，并且将Context传递的信息intent传递
     * 此方法是唯一的可以执行很多次的方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getIntExtra("type", -1)) {
            case MainActivity.PLAT_MUSIC:
                musicList = intent.getStringArrayListExtra("musicList");
                play(intent.getIntExtra("currentIndex", 0));
                break;
            case MainActivity.PAUSE_MUSIC:
                //播放器不为空，并且正在播放
                pause();
                break;
            case MainActivity.STOP_MUSIC:
                stop();
                break;
            case MainActivity.SEEK_TO_MUSIC:
                int progress = intent.getIntExtra("progress", -1);
                mediaPlayer.seekTo(progress);
                break;
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Thread currentTime = new Thread(new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            for (; ; ) {
                try {
                    Thread.sleep(1000);
                    int max = mediaPlayer.getDuration();
                    int current = mediaPlayer.getCurrentPosition();
                    intent.setAction(MainActivity.ACTION_MUSIC_ACTIVITY_RECEIVER);
                    intent.putExtra("playerTimeMax", max);
                    intent.putExtra("playerCurrentTime", current);
                    sendBroadcast(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    //http://abv.cn/music/红豆.mp3
    public void play(int currentIndex) {
        if (isPause) {
            mediaPlayer.start();
        } else {
            if (isStop) {
                //重置mediaplayer
                mediaPlayer.reset();
                //将需要播放的资源与之绑定
                mediaPlayer = MediaPlayer.create(this, Uri.parse(musicList.get(currentIndex)));
                //开始播放
                mediaPlayer.start();
                //是否循环播放
                mediaPlayer.setLooping(false);
                currentTime.start();
                isStop = false;
            } else if (!isStop && mediaPlayer.isPlaying() && mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }

    private void nextMusic() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            if (currentIndex == musicList.size() - 1) {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(musicList.get(0)));
                currentIndex = 0;
            } else {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(musicList.get(currentIndex + 1)));
                currentIndex++;
            }
            mediaPlayer.start();
        }
    }

    private void preMusic() {
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            //停止之后要开始播放音乐
            mediaPlayer.stop();
            isStop = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentTime = null;
        mediaPlayer.release();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        stopSelf();
        unregisterReceiver(receiver);
    }

//    private void resetAudioPlayer() {
//        mRestartPaus.setImageResource(R.drawable.ic_player_start);
//        audioPlayer.stop();
//        audioPlayer.reset();
//        seek.setProgress(0);
//    }

    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {

                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    private void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

}
