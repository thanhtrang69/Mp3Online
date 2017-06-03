package com.example.trang.mp3online.activity;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.trang.mp3online.R;
import com.example.trang.mp3online.adapter.viewpager.ShowFragmentPager;
import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.entity.NetworkUtil;
import com.example.trang.mp3online.util.LoadPhotoAlbum;
import com.example.trang.mp3online.util.LoadingPhoto;
import com.example.trang.mp3online.entity.Song;
import com.example.trang.mp3online.service.SongService;
import com.example.trang.mp3online.view.fragment.fragmentsong.ListSongHotFragment;
import com.example.trang.mp3online.view.fragment.fragmentsong.ListSongReleaseFragment;
import com.example.trang.mp3online.view.fragment.fragmentsong.ListSongTotalFragment;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.duration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListSongTotalFragment listSongTotalFragment;
    private ListSongHotFragment listSongHotFragment;
    private ListSongReleaseFragment listSongReleaseFragment;
    private LinearLayout linearLayoutAtc;
    private ImageButton mImgStop;
    private ImageButton mImgprevious;
    private ImageButton mImgPlay;
    private ImageButton mImgNext;
    private boolean isConnection;
    private SongService songService;
    private CircleImageView getcIAlbum;
    private TextView tvNameSong;
    private TextView tvAuhorSong;
    private Animation animation;
    private LoadPhotoAlbum loadPhotoAlbum;
    private int positionActionMain;
    private ServiceConnection serviceConnection;
    private LinearLayout getLinearLayoutAtc;
    private ArrayList<Song> songList;
    private boolean isInit;
    private int idAlbum;
    private BroadcastReceiver br;
    public static Context context;
    private boolean isState;
    private boolean mBroadcastIsRegistered;
    private int positionActionPlusMain;
    private int positionActionWeakMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionService();
        context = this;
        checkInternetConnection();
    }


    private boolean checkInternetConnection() {

        if (br == null) {

            br = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean status = NetworkUtil.getConnectivityStatusString(context);
                    if (status){
                        initView();
                    }
                    else {
                        getLinearLayoutAtc = (LinearLayout) findViewById(R.id.ll_boss_app);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Vui Lòng kiểm tra kết nối internet ?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getLinearLayoutAtc.setBackgroundResource(R.color.pink);
                                finish();
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }

                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver((BroadcastReceiver) br, intentFilter);
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        songService.hideMediaNotification();
        super.onDestroy();
    }

    private void initView() {

        tabLayout = (TabLayout) findViewById(R.id.tl_activity);
        viewPager = (ViewPager) findViewById(R.id.vp_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShowFragmentPager fragmentPager = new ShowFragmentPager(fragmentManager);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(fragmentPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(fragmentPager);

        linearLayoutAtc = (LinearLayout) findViewById(R.id.ll_show_strat_song_activity);
        linearLayoutAtc.setVisibility(View.GONE);


        mImgprevious = (ImageButton) findViewById(R.id.img_previous);
        mImgPlay = (ImageButton) findViewById(R.id.img_play_song);
        mImgStop = (ImageButton) findViewById(R.id.img_stop_song);
        mImgNext = (ImageButton) findViewById(R.id.img_next_song);

        loadPhotoAlbum = new LoadingPhoto();

        tvNameSong = (TextView) findViewById(R.id.tv_name_song_button);
        tvAuhorSong = (TextView) findViewById(R.id.tv_name_author_button);
        animation = AnimationUtils.loadAnimation(this, R.anim.runoval);
        getcIAlbum = (CircleImageView) findViewById(R.id.ci_album);

        mImgprevious.setOnClickListener(this);
        mImgPlay.setOnClickListener(this);
        mImgStop.setOnClickListener(this);
        mImgNext.setOnClickListener(this);
        linearLayoutAtc.setOnClickListener(this);


    }

    public void connectionService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isConnection = true;
                songService = ((SongService.BindService) service).getBindService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnection = false;
            }
        };
        Intent intent = new Intent(this, SongService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }
    public void showFragmentSong(int id, String urlImage, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(ListSongTotalFragment.ID_SONG, id);
        bundle.putString(ListSongTotalFragment.URL_IMAGE, urlImage);
        bundle.putString(ListSongTotalFragment.TITLE, title);
        listSongTotalFragment = ListSongTotalFragment.getInstance(bundle);
        getFragmentManager().beginTransaction().replace(android.R.id.content, listSongTotalFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(ListSongTotalFragment.class.getName())
                .commit();

    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getUI(intent);
        }
    };
    private void getUI(Intent intent) {
        isState = intent.getBooleanExtra(Key.STATE_SERVICE, false);
        if (!isState) {
            mImgPlay.setVisibility(View.VISIBLE);
            mImgStop.setVisibility(View.GONE);
            loadPhotoAlbum.onLoadinged(songList.get(positionActionMain).getUrlImage(), getcIAlbum);
        } else {
            mImgPlay.setVisibility(View.GONE);
            mImgStop.setVisibility(View.VISIBLE);
        }
    }


    public void showFragmentSongHot(int id, String urlImage, String title) {
        listSongHotFragment = new ListSongHotFragment(id, urlImage, title);
        getFragmentManager().beginTransaction().replace(android.R.id.content, listSongHotFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(ListSongHotFragment.class.getName())
                .commit();

    }

    public void showFragmentSongRelease(int id, String urlImage, String title) {
        listSongReleaseFragment = new ListSongReleaseFragment(id, urlImage, title);
        getFragmentManager().beginTransaction().replace(android.R.id.content, listSongReleaseFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(ListSongTotalFragment.class.getName())
                .commit();


    }

    public void setArrayList(ArrayList<Song> songArrayList, int positionAction, int idAlbum) {
        songList = new ArrayList<>();
        songList = songArrayList;
        this.idAlbum = idAlbum;
        Intent intent = new Intent(this, ShowActionPlaySongActivity.class);
        intent.putExtra(Key.POSITION, positionAction);
        intent.putExtra(Key.ID_SONG, idAlbum);
        Log.d("diiiiiiiiiiii", "setArrayList: "+positionAction);
        startActivityForResult(intent,123);
        isInit = true;
        registerReceiver(broadcastReceiver,new IntentFilter(SongService.ACTION_BROADCAST));
        mBroadcastIsRegistered=true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInit) {
            SharedPreferences sharedPreferences = getSharedPreferences("Status", Context.MODE_PRIVATE);
            if (sharedPreferences != null) {

                boolean state = sharedPreferences.getBoolean("state", false);
                if (!state) {
                    mImgStop.setVisibility(View.GONE);
                    mImgPlay.setVisibility(View.VISIBLE);
                    loadPhotoAlbum.onLoadinged(songList.get(positionActionMain).getUrlImage(), getcIAlbum);
                } else {
                    mImgStop.setVisibility(View.VISIBLE);
                    mImgPlay.setVisibility(View.GONE);
                    loadPhotoAlbum.onLoadingStartAnimation(songList.get(positionActionMain).getUrlImage(), getcIAlbum, animation);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                if (isConnection) {
                    onPrevious();
                }
                break;
            case R.id.img_stop_song:
                if (isConnection) {

                    onStopSong();
                }

                break;
            case R.id.img_next_song:
                if (isConnection) {
                    onNextSong();
                }
                break;
            case R.id.img_play_song:
                if (isConnection) {
                    onPlaySong();
                }
                break;
            case R.id.ll_show_strat_song_activity:
                if (isConnection) {
//                    Intent intent = new Intent(this, ShowActionPlaySongActivity.class);
//                    intent.putExtra(Key.POSITION, positionActionMain);
//                    intent.putExtra(Key.ID_SONG, idAlbum);
//                    startActivityForResult(intent,321);

                }
                break;
        }
    }

    private void onStopSong() {
        songService.onPause();
        mImgPlay.setVisibility(View.VISIBLE);
        mImgStop.setVisibility(View.GONE);
        loadPhotoAlbum.onLoadinged(songList.get(positionActionMain).getUrlImage(), getcIAlbum);
        songService.checkStatus(false);
    }

    private void onPlaySong() {
        mImgPlay.setVisibility(View.GONE);
        mImgStop.setVisibility(View.VISIBLE);
        loadPhotoAlbum.onLoadingStartAnimation(songList.get(positionActionMain).getUrlImage(), getcIAlbum, animation);
        songService.checkStatus(true);
    }

    private void onNextSong() {
        if (positionActionMain == (songList.size() - 1)) {
            Toast.makeText(songService, "islast", Toast.LENGTH_SHORT).show();
            songService.onStart(songList.get(0).getSource());
            tvNameSong.setText(songList.get(0).getTitel());
            tvAuhorSong.setText(songList.get(0).getArtist());
            loadPhotoAlbum.onLoadingStartAnimation(songList.get(0).getUrlImage(), getcIAlbum, animation);
            mImgPlay.setVisibility(View.GONE);
            mImgStop.setVisibility(View.VISIBLE);
        } else {
            positionActionMain++;
            songService.setSongList(songList);
            songService.selecteSong(positionActionMain, SongService.MEDIA_NOTIFICATION_ID);
            songService.nextSong(songList.get(positionActionMain).getSource());

            tvNameSong.setText(songList.get(positionActionMain).getTitel());
            tvAuhorSong.setText(songList.get(positionActionMain).getArtist());

            mImgPlay.setVisibility(View.GONE);
            mImgStop.setVisibility(View.VISIBLE);
            loadPhotoAlbum.onLoadingStartAnimation(songList.get(positionActionMain).getUrlImage(), getcIAlbum, animation);

        }
    }

    private void onPrevious() {
        if (positionActionMain == 0) {
            songService.onStart(songList.get(songList.size() - 1).getSource());
            tvNameSong.setText(songList.get(songList.size() - 1).getTitel());
            tvAuhorSong.setText(songList.get(songList.size() - 1).getArtist());
            loadPhotoAlbum.onLoading(songList.get(songList.size() - 1).getUrlImage(), getcIAlbum);
        } else {
            positionActionMain--;
            songService.setSongList(songList);
            songService.selecteSong(positionActionMain, SongService.MEDIA_NOTIFICATION_ID);
            songService.releaseSog(songList.get(positionActionMain).getSource());
            mImgPlay.setVisibility(View.GONE);
            mImgStop.setVisibility(View.VISIBLE);
            tvNameSong.setText(songList.get(positionActionMain).getTitel());
            tvAuhorSong.setText(songList.get(positionActionMain).getArtist());
            loadPhotoAlbum.onLoading(songList.get(positionActionMain).getUrlImage(), getcIAlbum);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode==123){
                positionActionMain = data.getIntExtra(Key.POSITION_SHOW_MAIN,0);


                tvNameSong.setText(songList.get(positionActionMain).getTitel());
                tvAuhorSong.setText(songList.get(positionActionMain).getArtist());
                loadPhotoAlbum.onLoadingStartAnimation(songList.get(positionActionMain).getUrlImage(), getcIAlbum, animation);

                if (String.valueOf(positionActionMain) == null) {
                    linearLayoutAtc.setVisibility(View.GONE);
                } else {
                    linearLayoutAtc.setVisibility(View.VISIBLE);

                }

                Log.d("veeeeeeeeee", "onActivityResult: "+positionActionMain);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
