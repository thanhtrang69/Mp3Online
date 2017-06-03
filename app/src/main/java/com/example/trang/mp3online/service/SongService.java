package com.example.trang.mp3online.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.trang.mp3online.App;
import com.example.trang.mp3online.R;
import com.example.trang.mp3online.activity.MainActivity;
import com.example.trang.mp3online.activity.ShowActionPlaySongActivity;
import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.entity.Song;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Trang on 5/25/2017.
 */

public class SongService extends Service {
    public static final String ACTION_BROADCAST = "com.example.trang.mp3online.service.ACTION_BROADCAST";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Intent intentBroadcast;
    private int curronPosisson;
    private int duration;
    private ArrayList<Song> songArrayList;
    private String mUri;
    public int progressService;
    public static int MEDIA_NOTIFICATION_ID = 100;
    private static final String ACTION_START = "com.example.trang.mp3online.service.START";
    private static final String ACTION_NEXT = "com.example.trang.mp3online.service.NEXT";
    private static final String ACTION_PREVIOUS = "com.example.trang.mp3online.service.PREVIOUS";
    public static final int REQUEST_CODE_ACTION_PREVIOUS = 101;
    public static final int REQUEST_CODE_ACTION_START = 103;
    private int position = 0;
    private Notification mNotification;
    private Notification.Builder notifaBuilder;
    private RemoteViews remoteViews;
    private boolean isPlaying;
    private boolean checkFishnis=false;
    private int isStateAction;
    private int positionNext;
    private int positionPreate;
    public String nameSongUpdate;
    public String nameAuthorUpdate;
    public String nameAuthorFisnish;
    public String nameSongFisnish;

    @Override
    public IBinder onBind(Intent intent) {
        return new BindService();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        songArrayList = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        intentBroadcast = new Intent(ACTION_BROADCAST);
        notifaBuilder = new Notification.Builder(getApplicationContext());
        remoteViews = new RemoteViews(getPackageName(), R.layout.notifacation);
        setUpServiceBroadcast();
    }


    public void seekCompleteFake(int progress) {
        this.progressService = progress;
        mediaPlayer.seekTo(progress);
    }


    public class BindService extends Binder {
        public SongService getBindService() {
            return SongService.this;
        }
    }

    private void setUpServiceBroadcast() {
        handler.removeCallbacks(updateSendBroadCast);
        handler.postDelayed(updateSendBroadCast, 100);
    }

    private Runnable updateSendBroadCast = new Runnable() {
        @Override
        public void run() {
            logMediaService();
            handler.postDelayed(this, 1000);
        }
    };

    private void logMediaService() {
        curronPosisson = mediaPlayer.getCurrentPosition();
        duration = mediaPlayer.getDuration();
        intentBroadcast.putExtra(Key.CURRENTPOSITION, curronPosisson);
        intentBroadcast.putExtra(Key.DURATION, duration);
        intentBroadcast.putExtra(Key.STATE_SERVICE, isPlaying);
        intentBroadcast.putExtra(Key.TITEL_MAIN, nameSongUpdate);
        intentBroadcast.putExtra(Key.AUTHOR_MAIN, nameAuthorUpdate);
        intentBroadcast.putExtra(Key.STATE_ACTION_NOTIFACATION, isStateAction);
        intentBroadcast.putExtra(Key.ACTION_NEXT, positionNext);
        intentBroadcast.putExtra(Key.ACTION_PREAT, positionPreate);
        intentBroadcast.putExtra(Key.NAMESONGFISNISH, nameSongFisnish);
        intentBroadcast.putExtra(Key.NAMEAUTHORFISNISH, nameAuthorFisnish);
        intentBroadcast.putExtra(Key.CHECK_FINISH, checkFishnis);
        sendBroadcast(intentBroadcast);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START:
                    if (!isPlaying) {
                        remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.pause);
                        isPlaying = true;
                        onSratNow();
                        updateNotifacation();
                    } else {
                        remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.playbutton);
                        isPlaying = false;
                        onPause();
                        updateNotifacation();
                    }
                    break;
                case ACTION_NEXT:
                    if (position < songArrayList.size() - 1) {
                        onStop();
                        positionNext = position + 1;
                        remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.pause);
                        onStart(songArrayList.get(positionNext).getSource());
                        updateNotifacation(songArrayList.get(positionNext).getTitel(), songArrayList.get(positionNext).getArtist());
                        position = positionNext;
                        nameSongUpdate = songArrayList.get(positionNext).getTitel();
                        nameAuthorUpdate = songArrayList.get(positionNext).getArtist();
                        isStateAction = Key.NEXT;
                        isPlaying = true;
                    }
                    break;
                case ACTION_PREVIOUS:
                    positionPreate = position - 1;
                    if (positionPreate > 0) {
                        onStop();
                        remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.pause);
                        onStart(songArrayList.get(positionPreate).getSource());
                        updateNotifacation(songArrayList.get(positionPreate).getTitel(), songArrayList.get(positionPreate).getArtist());
                        position = positionPreate;
                        nameSongUpdate = songArrayList.get(positionPreate).getTitel();
                        nameAuthorUpdate = songArrayList.get(positionPreate).getArtist();
                        isStateAction = Key.PREAT;
                        isPlaying = true;

                    }
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void checkStatus(boolean isStatus) {
        isPlaying = isStatus;
        if (!isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.playbutton);
            onPause();
        } else {
            remoteViews.setImageViewResource(R.id.img_play_song_notifacation, R.drawable.pause);
            onSratNow();
        }
        updateNotifacation();
    }

    public void hideMediaNotification() {
        NotificationManager notificationManager = (NotificationManager) App.getmContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MEDIA_NOTIFICATION_ID);
    }

    public void showNotifavation() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews.setTextViewText(R.id.tv_name_song_button_notifacation, songArrayList.get(position).getTitel());
        remoteViews.setTextViewText(R.id.tv_name_author_button_notifacation, songArrayList.get(position).getArtist());
        remoteViews.setImageViewResource(R.id.ci_album_notifacation, R.drawable.ic_music);

        Intent intent = new Intent(ACTION_PREVIOUS);
        PendingIntent pendingIntent = PendingIntent.getService(this, REQUEST_CODE_ACTION_PREVIOUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_previous_notifacation, pendingIntent);

        Intent startIntent = new Intent(ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getService(this, REQUEST_CODE_ACTION_START, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_play_song_notifacation, startPendingIntent);

        Intent actLock = new Intent();
        actLock.setClass(this, SongService.class);
        actLock.setAction(ACTION_NEXT);
        PendingIntent pLock = PendingIntent.getService(this, 0, actLock, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_next_song_notifacation, pLock);

        Intent onClickViewIntent = new Intent(this, MainActivity.class);
        onClickViewIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mNotification = notifaBuilder
                .setSmallIcon(R.drawable.ic_music).setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, onClickViewIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContent(remoteViews)
                .setDefaults(Notification.FLAG_NO_CLEAR)
                .build();
        notificationManager.notify(MEDIA_NOTIFICATION_ID, mNotification);
    }

    public void onStart(final String path) {

        mediaPlayer.reset();
        try {

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateNotifacation(songArrayList.get(position).getTitel(), songArrayList.get(position).getArtist());
    }

    public void onStop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();

        }
    }


    public void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void onSratNow() {
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onNext();
            }
        });
    }


    public void nextSong(String path) {
        onStart(path);
    }

    public void onNext() {
        position++;
        onStart(songArrayList.get(position).getSource());
        nameSongFisnish = songArrayList.get(position).getTitel();
        nameAuthorFisnish = songArrayList.get(position).getArtist();
        checkFishnis = true;
    }

    public void releaseSog(String path) {
        onStart(path);
    }

    public void repeateSong(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);

    }

    public void selecteSong(int pos, int notifacationId) {
        this.position = pos;
        MEDIA_NOTIFICATION_ID = notifacationId;
        setmUri(songArrayList.get(pos).getSource());
        showNotifavation();
        updateNotifacation();
        onStart(songArrayList.get(pos).getSource());
        isPlaying = true;
    }

    public void setmUri(String uri) {
        this.mUri = uri;
    }

    public void setSongList(ArrayList<Song> listSong) {
        this.songArrayList = listSong;
    }

    public void updateNotifacation(String songName, String author) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification.contentView.setTextViewText(R.id.tv_name_song_button_notifacation, songName);
        mNotification.contentView.setTextViewText(R.id.tv_name_author_button_notifacation, author);
        notificationManager.notify(MEDIA_NOTIFICATION_ID, mNotification);
    }

    public void updateNotifacation() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MEDIA_NOTIFICATION_ID, mNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSendBroadCast);
    }
}
