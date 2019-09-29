package com.viktorvilmusenaho.platformer.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.io.IOException;

public class JukeBox {
    private static final int MAX_STREAMS = 3;
    public static int BACKGROUND = 0;
    public static int JUMP = 0;
    public static int DASH = 0;
    public static int COIN_PICKUP = 0;
    public static int DAMAGE = 0;

    private SoundPool _soundPool;

    public JukeBox(final Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(MAX_STREAMS)
                .build();
        loadSounds(context);
    }

    private void loadSounds(final Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("background.wav");
            BACKGROUND = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("jump.wav");
            JUMP = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("dash.wav");
            DASH = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("coin_pickup.wav");
            COIN_PICKUP = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("player_damage.wav");
            DAMAGE = _soundPool.load(descriptor, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(final int soundID, final int loop) {
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final int priority = 1;
        final float rate = 1.0f;

        if (soundID > 0) {
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    public void onPause(){
        _soundPool.autoPause();
    }

    public void onResume(){
        _soundPool.autoResume();
    }

    public void destroy() {
        _soundPool.release();
        _soundPool = null;
    }
}
