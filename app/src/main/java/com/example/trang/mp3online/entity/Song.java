package com.example.trang.mp3online.entity;

/**
 * Created by Trang on 5/20/2017.
 */

public class Song {
    private int id;
    private String titel;
    private String artist;
    private String link;
    private String urlImage;
    private String sort;
    private String duration;
    private String source;

    public Song(int id, String titel, String duration, String source) {
        this.id = id;
        this.titel = titel;
        this.duration = duration;
        this.source = source;
    }

    public Song(int id, String titel, String artist, String link, String urlImage, String sort, String source) {
        this.id = id;
        this.titel = titel;
        this.artist = artist;
        this.link = link;
        this.urlImage = urlImage;
        this.sort = sort;
        this.source = source;
    }

    public Song(String titel, String artist, String link, String urlImage, String sort, String source) {
        this.titel = titel;
        this.artist = artist;
        this.link = link;
        this.urlImage = urlImage;
        this.sort = sort;
        this.source = source;
    }

    public Song() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
