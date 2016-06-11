package henhkok.pianoandrain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MyService extends Service {
    public MyService() {
    }
    NotificationManager nm;
    private MediaPlayer player;
    private MediaPlayer player2;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        initializeMediaPlayer();
        initializeMediaPlayer2();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotif();
        startPlaying();
        startPlaying2();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopPlaying();
        stopPlaying2();
        nm.cancel(56248);
    }

    void sendNotif() {
        Notification notif = new Notification(R.mipmap.ic_launcher, "Text in status bar", System.currentTimeMillis());

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Now playing Piano & Rain")
                .setContentText("Classic Piano + Rain")
                .setContentTitle("Piano & Rain");
        if (Build.VERSION.SDK_INT < 16) {
            notif = builder.getNotification();
        } else {
            notif = builder.build();
        }

        // ставим флаг, чтобы уведомление пропало после нажатия
        //notif.flags |= Notification.FLAG_AUTO_CANCEL;
        // ставим флаг, чтобы уведомление работало пока работает сам поток
        notif.flags |= Notification.FLAG_ONGOING_EVENT;

        // отправляем
        nm.notify(56248, notif);
    }

    private void startPlaying() {
        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });
    }

    private void startPlaying2() {
        player2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                player2.start();
            }
        });
        player2.prepareAsync();
    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }
    }

    private void stopPlaying2(){
        if (player2.isPlaying()) {
            player2.stop();
            initializeMediaPlayer2();
        }
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource("http://pianosolo.streamguys.net/live");
            //player.setDataSource("http://streaming502.radionomy.com/1000HITSClassicalMusic");
            //player2.setDataSource("https://dl.dropboxusercontent.com/u/8520971/RainyMood.mp3");
            //http://7oom.ru/audio/15-new.mp3 звук дождя
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeMediaPlayer2() {
        player2 = new MediaPlayer();
        try {
            player2.setDataSource("https://dl.dropboxusercontent.com/u/8520971/RainyMood.mp3");
            //http://7oom.ru/audio/15-new.mp3 звук дождя
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
