package com.adithyaupadhya.moviemaniac.games;

import android.content.Context;
import android.media.MediaPlayer;

import com.adithyaupadhya.moviemaniac.R;

/**
 * Created by adithya.upadhya on 09-11-2016.
 */

class GameMediaPlayer {

    private static GameMediaPlayer instance = null;
    private MediaPlayer mediaPlayer;
    private boolean isMediaPlayerActive = false;

    private GameMediaPlayer() {
    }

    static GameMediaPlayer getInstance() {
        return instance != null ? instance : (instance = new GameMediaPlayer());
    }

    void startPlaying(Context context) {

        if (!isMediaPlayerActive || mediaPlayer == null) {
            isMediaPlayerActive = true;

            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.bell);

            final int maxVolume = 100, requiredVolume = 15;

            float finalVolume = 1 - (float) (Math.log(maxVolume - requiredVolume) / Math.log(maxVolume));

            mediaPlayer.setVolume(finalVolume, finalVolume);
        }

        mediaPlayer.seekTo(0);

        mediaPlayer.start();
    }

    void releaseResources() {

        if (mediaPlayer != null) {

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }
}