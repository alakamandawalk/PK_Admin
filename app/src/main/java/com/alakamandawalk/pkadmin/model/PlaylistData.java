package com.alakamandawalk.pkadmin.model;

public class PlaylistData {

    private String playlistId, playlistName, playlistImage, playlistCategory;

    public PlaylistData() {
    }

    public PlaylistData(String playlistId, String playlistName, String playlistImage, String playlistCategory) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistImage = playlistImage;
        this.playlistCategory = playlistCategory;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        this.playlistImage = playlistImage;
    }

    public String getPlaylistCategory() {
        return playlistCategory;
    }

    public void setPlaylistCategory(String playlistCategory) {
        this.playlistCategory = playlistCategory;
    }
}
