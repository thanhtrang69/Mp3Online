package com.example.trang.mp3online.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trang.mp3online.App;
import com.example.trang.mp3online.R;
import com.example.trang.mp3online.adapter.recycleradapter.songdapter.ShowActionPlayAdapter;
import com.example.trang.mp3online.api.Mp3API;
import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.entity.Song;
import com.example.trang.mp3online.service.SongService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ShowActionPlaySongActivity extends AppCompatActivity implements View.OnClickListener, Mp3API.OnResponseJsonCallback {
    boolean mBroadcastIsRegistered;
    private RecyclerView recyclerView;
    private TextView tvNameSong;
    private TextView tvNameAuthor;
    private TextView tvTimeFinish;
    private TextView tvTimeStart;
    private ImageButton imgClock;
    private ImageButton imgRandom;
    private ImageButton imgBack;
    private ImageButton imgPlay;
    private ImageButton tvEdit;
    private ImageButton imgStop;
    private ImageButton imgNext;
    private ImageButton imgRepeat;
    private SongService songService;
    private SeekBar seekBar;
    private ArrayList<Song> songArrayListShow;
    private int pos = -1;
    private int idSong;
    private Mp3API mp3API;
    private ServiceConnection serviceConnection;
    private ShowActionPlayAdapter showActionPlayAdapter;
    private boolean isConnected;
    private boolean isClickClock;
    private boolean isClickRepeat;
    private boolean isState;
    private boolean isClickRandom;
    private int duration;
    private int currenPosition;
    private String nameSongUpdate;
    private String nameAuthorUpdate;
    private String nameSongFisnish;
    private String nameAuthorFisnish;
    public static Context context;
    private int positionActionMain;
    private int positionActionPlusMain;
    private int positionActionWeakMain;
    private int state;
    private boolean checkfinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_action);
        context = this;
        connectionService();
        initView();
        mp3API = new Mp3API();
        mp3API.setOnResponseJsonCallback(this);
        mp3API.getSongList(Mp3API.makeRequestData(idSong, 200, mp3API.GET_PLAY_RELEASE_DATE));

    }

    public void connectionService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isConnected = true;
                songService = ((SongService.BindService) service).getBindService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
            }
        };
        Intent intent = new Intent(this, SongService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);

    }

    private void initView() {
        pos = getIntent().getIntExtra(Key.POSITION, 0);
        idSong = getIntent().getIntExtra(Key.ID_SONG, 0);
        Log.d("rrrrrrrrrrr", "onClickItem: " + idSong);
        positionActionMain = getIntent().getIntExtra(Key.POSITION, 0);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imgBack = (ImageButton) findViewById(R.id.img_previous_show);
        imgNext = (ImageButton) findViewById(R.id.img_next_song_show);
        imgPlay = (ImageButton) findViewById(R.id.img_play_song_show);
        imgRandom = (ImageButton) findViewById(R.id.img_random);
        imgRepeat = (ImageButton) findViewById(R.id.img_repeat_song);
        imgStop = (ImageButton) findViewById(R.id.img_stop_song_show);
        imgClock = (ImageButton) findViewById(R.id.img_bt_Clock);
        tvEdit = (ImageButton) findViewById(R.id.tv_edit);

        tvNameAuthor = (TextView) findViewById(R.id.tv_name_author_top);
        tvNameSong = (TextView) findViewById(R.id.tv_name_song_top);
        tvTimeFinish = (TextView) findViewById(R.id.tv_time_finish);
        tvTimeStart = (TextView) findViewById(R.id.tv_time_start);

        tvEdit.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgRandom.setOnClickListener(this);
        imgRepeat.setOnClickListener(this);
        imgStop.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgClock.setOnClickListener(this);


        seekBar = (SeekBar) findViewById(R.id.seekbar);

        seekBar.setClickable(false);
        seekBar.setClickable(false);

        if (pos == -1) {

            imgPlay.setVisibility(View.VISIBLE);
            imgStop.setVisibility(View.GONE);
        } else {

            imgPlay.setVisibility(View.GONE);
            imgStop.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResponse(JSONObject jsonObject) {
        songArrayListShow = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("docs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Song song = new Song();
                song.setId(object.getInt("song_id"));
                song.setTitel(object.getString("title"));
                song.setArtist(object.getString("artist"));
                song.setSort(object.getString("duration"));
                song.setUrlImage(object.getString("thumbnail"));

                JSONObject source = object.getJSONObject("source");
                String sourceString = source.getString("128");
                song.setSource(sourceString);
                songArrayListShow.add(song);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        showSongList(songArrayListShow);

    }

    private void showSongList(ArrayList<Song> songArrayList) {
        if (showActionPlayAdapter == null) {
            showActionPlayAdapter = new ShowActionPlayAdapter(songArrayList, App.getmContext());
            recyclerView.setAdapter(showActionPlayAdapter);
            tvNameSong.setText(songArrayList.get(pos).getTitel());
            tvNameAuthor.setText(songArrayList.get(pos).getArtist());
            showActionPlayAdapter.setListnerListSongTotal(new ShowActionPlayAdapter.OnClickSongListener() {
                @Override
                public void onClickSong(Song song, int position) {
                    onStratItemSong(song, position);

                }
            });
        } else {
            showActionPlayAdapter.addAlbum(songArrayList);
        }

    }

    public void onStratItemSong(Song song, int position) {
        pos = position;
        songService.onStart(song.getSource());
        imgPlay.setVisibility(View.GONE);
        imgStop.setVisibility(View.VISIBLE);
        tvNameSong.setText(songArrayListShow.get(pos).getTitel());
        tvNameAuthor.setText(songArrayListShow.get(pos).getArtist());
        songService.updateNotifacation(songArrayListShow.get(position).getTitel(), songArrayListShow.get(position).getArtist());

    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getUI(intent);
        }
    };

    private void getUI(Intent intent) {
        currenPosition = intent.getIntExtra(Key.CURRENTPOSITION, 0);
        duration = intent.getIntExtra(Key.DURATION, 0);

        positionActionPlusMain = intent.getIntExtra(Key.ACTION_NEXT, 0);
        positionActionWeakMain = intent.getIntExtra(Key.ACTION_PREAT, 0);

        nameAuthorFisnish = intent.getStringExtra(Key.NAMEAUTHORFISNISH);
        nameSongFisnish = intent.getStringExtra(Key.NAMESONGFISNISH);

        nameSongUpdate = intent.getStringExtra(Key.TITEL_MAIN);
        nameAuthorUpdate = intent.getStringExtra(Key.AUTHOR_MAIN);

        state = intent.getIntExtra(Key.STATE_ACTION_NOTIFACATION, 0);

        checkfinish = intent.getBooleanExtra(Key.CHECK_FINISH, false);

        if (checkfinish==true){
            tvNameSong.setText(nameSongFisnish);
            tvNameAuthor.setText(nameAuthorFisnish);
        }
        if (state == Key.NEXT) {
            this.positionActionMain = positionActionPlusMain;
            tvNameSong.setText(nameSongUpdate);
            tvNameAuthor.setText(nameAuthorUpdate);
        } else if (state == Key.PREAT) {
            this.positionActionMain = positionActionWeakMain;
            tvNameSong.setText(nameSongUpdate);
            tvNameAuthor.setText(nameAuthorUpdate);
        }

        Log.d("bbbbbbbbbb", "getUI: " + positionActionPlusMain);

        isState = intent.getBooleanExtra(Key.STATE_SERVICE, false);
        if (!isState) {
            imgPlay.setVisibility(View.VISIBLE);
            imgStop.setVisibility(View.GONE);
        } else {
            imgPlay.setVisibility(View.GONE);
            imgStop.setVisibility(View.VISIBLE);
        }
        Log.d("eeeeeeeeeeee", "getUI: " + pos);
        seekBar.setMax(duration);
        seekBar.setProgress(currenPosition);
        tvTimeFinish.setText(String.format("%d :%d",
                TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTimeStart.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(progress)
                        , TimeUnit.MILLISECONDS.toSeconds(progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))));
                seekBar.setProgress(progress);
                if (fromUser) {
                    songService.seekCompleteFake(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (duration == currenPosition) {
            tvNameSong.setText(songArrayListShow.get(positionActionMain++).getTitel());
            tvNameAuthor.setText(songArrayListShow.get(positionActionMain++).getArtist());
        }
    }

    @Override
    public void onResume() {
        if (!mBroadcastIsRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(SongService.ACTION_BROADCAST));
            mBroadcastIsRegistered = true;
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_play_song_show:
                if (isConnected) {
                    onPlaySong();
                }
                break;
            case R.id.img_stop_song_show:
                if (isConnected) {
                    onStopSong();
                }
                break;
            case R.id.img_next_song_show:
                if (isConnected) {
                    onNextSong();

                }

                break;
            case R.id.img_previous_show:
                if (isConnected) {

                    onPrevious();
                }
                break;
            case R.id.img_random:
                if (isConnected) {
                    onRandom();
                }
                break;
            case R.id.img_repeat_song:
                if (isConnected) {
                    onRepeat();
                }
                break;
            case R.id.img_bt_Clock:
                if (isConnected) {
                    onClock();
                }
                break;
            case R.id.tv_edit:

                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Key.POSITION_SHOW_MAIN, pos);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void onRandom() {
        if (isClickRandom) {
            imgRandom.setImageResource(R.mipmap.ic_random);
            isClickRandom = false;
        } else {
            songService.repeateSong(false);
            imgRandom.setImageResource(R.drawable.ic_transform_black_24dp);
            Collections.shuffle(songArrayListShow);
            Toast.makeText(songService, "Xáo Trộn", Toast.LENGTH_SHORT).show();
            isClickRandom = true;
        }
    }

    private void onRepeat() {
        if (isClickRepeat) {
            pos--;
            imgRepeat.setImageResource(R.drawable.ic_replay_black);
            songService.repeateSong(true);
            isClickRepeat = false;
        } else {
            songService.repeateSong(false);
            imgRepeat.setImageResource(R.drawable.ic_replay_on);
            isClickRepeat = true;
        }
    }

    private void onClock() {
        if (isClickClock) {
            imgClock.setImageResource(R.drawable.ic_alarm_black_24dp);
            isClickClock = false;

        } else {
            imgClock.setImageResource(R.drawable.ic_alarm_indigo_24dp);
            isClickClock = true;
        }
    }

    private void onStopSong() {
        imgPlay.setVisibility(View.VISIBLE);
        imgStop.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = getSharedPreferences("Status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", false);
        editor.apply();
        songService.checkStatus(false);

    }

    private void onPlaySong() {
        imgPlay.setVisibility(View.GONE);
        imgStop.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("Status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", true);
        editor.apply();
        tvTimeFinish.setText(String.format("%d :%d",
                TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        registerReceiver(broadcastReceiver, new IntentFilter(SongService.ACTION_BROADCAST));
        mBroadcastIsRegistered = true;
        songService.checkStatus(true);


    }

    private void onNextSong() {
        if (pos == (songArrayListShow.size() - 1)) {
            songService.onStart(songArrayListShow.get(0).getSource());
            tvNameSong.setText(songArrayListShow.get(0).getTitel());
            tvNameAuthor.setText(songArrayListShow.get(0).getArtist());
            imgPlay.setVisibility(View.GONE);
            imgStop.setVisibility(View.VISIBLE);
            tvTimeFinish.setText(String.format("%d :%d",
                    TimeUnit.MILLISECONDS.toMinutes(duration)
                    , TimeUnit.MILLISECONDS.toSeconds(duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        } else {
            pos++;
            songService.setSongList(songArrayListShow);
            songService.selecteSong(pos, SongService.MEDIA_NOTIFICATION_ID);
            songService.nextSong(songArrayListShow.get(pos).getSource());
            tvNameSong.setText(songArrayListShow.get(pos).getTitel());
            tvNameAuthor.setText(songArrayListShow.get(pos).getArtist());

            imgPlay.setVisibility(View.GONE);
            imgStop.setVisibility(View.VISIBLE);
            tvTimeFinish.setText(String.format("%d :%d",
                    TimeUnit.MILLISECONDS.toMinutes(duration)
                    , TimeUnit.MILLISECONDS.toSeconds(duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));

//                        positionActionPlusMain=positionActionMain+1;
        }
    }

    private void onPrevious() {
        if (songArrayListShow != null) {
            if (pos == 0) {
                songService.onStart(songArrayListShow.get(songArrayListShow.size() - 1).getSource());
                tvNameSong.setText(songArrayListShow.get(songArrayListShow.size() - 1).getTitel());
                tvNameAuthor.setText(songArrayListShow.get(songArrayListShow.size() - 1).getArtist());
                tvTimeFinish.setText(String.format("%d :%d",
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                        , TimeUnit.MILLISECONDS.toSeconds(duration)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
            } else {
                pos--;
                songService.setSongList(songArrayListShow);
                songService.selecteSong(pos, SongService.MEDIA_NOTIFICATION_ID);
                songService.releaseSog(songArrayListShow.get(pos).getSource());
                imgPlay.setVisibility(View.GONE);
                imgStop.setVisibility(View.VISIBLE);
                tvNameSong.setText(songArrayListShow.get(pos).getTitel());
                tvNameAuthor.setText(songArrayListShow.get(pos).getArtist());
                tvTimeFinish.setText(String.format("%d :%d",
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                        , TimeUnit.MILLISECONDS.toSeconds(duration)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
//                positionActionWeakMain=positionActionPlusMain-1;
            }
        }
    }

}
