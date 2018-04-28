package com.example.root.albumbytime.models;

/**
 * Created by jack
 * On 18-3-6:下午5:27
 * Desc:
 */

public class TimeItem {
    private Album album;
    private int position;

    public Album getAlbum() {
        return album;
    }

    public int getPosition() {
        return position;
    }

    public TimeItem(Album album, int position) {
        this.album = album;
        this.position = position;
    }
}
