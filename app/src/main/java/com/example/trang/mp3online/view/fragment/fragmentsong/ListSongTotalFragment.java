package com.example.trang.mp3online.view.fragment.fragmentsong;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.trang.mp3online.App;
import com.example.trang.mp3online.R;
import com.example.trang.mp3online.activity.MainActivity;
import com.example.trang.mp3online.adapter.recycleradapter.songdapter.SongAdapter;
import com.example.trang.mp3online.util.LoadPhotoAlbum;
import com.example.trang.mp3online.util.LoadingPhoto;
import com.example.trang.mp3online.api.Mp3API;
import com.example.trang.mp3online.entity.Song;
import com.example.trang.mp3online.service.SongService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Trang on 5/26/2017.
 */

public class ListSongTotalFragment extends Fragment implements Mp3API.OnResponseJsonCallback, View.OnClickListener {
    private static ListSongTotalFragment instance;
    public static final String ID_SONG = "id_song";
    public static final String URL_IMAGE = "url_image";
    public static final String TITLE = "title";

    private String title;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private Mp3API mp3API;
    private ServiceConnection serviceConnection;
    private ArrayList<Song> songList;
    private SongService songService;
    private int positionAction = -1;
    private ImageView imgAlbum;
    private Toolbar toolbar;
    private LoadPhotoAlbum loadPhotoAlbum;
    private View view;
    private int idAlbum;
    private String urlAlbum;
    private ImageButton mImgBtBack;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_list_song, container, false);
        idAlbum = getArguments().getInt(ID_SONG);
        urlAlbum = getArguments().getString(URL_IMAGE);
        title = getArguments().getString(TITLE);
        initView();
        setUpToolbar();
        connectionService();
        return view;
    }

    public ListSongTotalFragment() {

    }

    public static ListSongTotalFragment getInstance(Bundle bundle) {
        instance = new ListSongTotalFragment();
        if (bundle != null) {
            instance.setArguments(bundle);
        }
        return instance;
    }

    public ListSongTotalFragment(int id, String urlImage, String title) {
        this.idAlbum = id;
        this.urlAlbum = urlImage;
        this.title = title;
    }

    private void initView() {

        loadPhotoAlbum = new LoadingPhoto();
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_item_song);
        mImgBtBack = (ImageButton)view. findViewById(R.id.img_bt_back_activity);
        mImgBtBack.setOnClickListener(this);
        imgAlbum = (ImageView) view.findViewById(R.id.img_song_listitem_avt);
        loadPhotoAlbum.onLoading(urlAlbum, imgAlbum);
        imgAlbum.setClickable(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getmContext()));
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(recyclerView.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
        mp3API = new Mp3API();
        mp3API.setOnResponseJsonCallback(this);
        mp3API.getSongList(Mp3API.makeRequestData(idAlbum, 200, mp3API.GET_PLAY_RELEASE_DATE));

    }
    private void setUpToolbar() {
        ((MainActivity) getActivity()). setSupportActionBar(toolbar);
        ((MainActivity) getActivity()). getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((MainActivity) getActivity()). getSupportActionBar().setTitle(title);


    }

    public void connectionService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                songService = ((SongService.BindService) service).getBindService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(getActivity(), SongService.class);
        getActivity().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }


    private void showSongList(ArrayList<Song> songArrayList) {
        if (songAdapter == null) {
            songAdapter = new SongAdapter(songArrayList, App.getmContext());
            recyclerView.setAdapter(songAdapter);
            songAdapter.setListnerListSongTotal(new SongAdapter.OnClickSongListener() {
                @Override
                public void onClickSong(Song song, int position) {
                    songService.setSongList(songList);
                    songService.selecteSong(position, SongService.MEDIA_NOTIFICATION_ID);
                    onStratItemSong(song, position);
                }
            });
            progressDialog.dismiss();
        } else {
            songAdapter.addAlbum(songArrayList);
        }
    }

    public void onStratItemSong(Song song, int position) {
        this.positionAction = position;
        songService.onStart(song.getSource());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", true);
        editor.apply();
        ((MainActivity) getActivity()).setArrayList(songList, positionAction, idAlbum);


    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        songList = new ArrayList<>();
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
                songList.add(song);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showSongList(songList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_bt_back_activity:
                getActivity().onBackPressed();
                break;
        }
    }
}
