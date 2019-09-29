package com.viktorvilmusenaho.platformer.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.io.IOException;

public class JukeBox {
    private static final int MAX_STREAMS = 3;
    static int BACKGROUND = 0;
    static int JUMP = 0;
    static int DASH = 0;
    static int COIN_PICKUP = 0;
    static int DAMAGE = 0;

    private SoundPool _soundPool;

    JukeBox(final Context context) {
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
            descriptor = assetManager.openFd("crash.wav");
            BACKGROUND = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("game_over.wav");
            JUMP = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("game_start.ogg");
            DASH = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("laser_pew.wav");
            COIN_PICKUP = _soundPool.load(descriptor, 2);
            descriptor = assetManager.openFd("game_start.ogg");
            DAMAGE = _soundPool.load(descriptor, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void play(final int soundID) {
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final int priority = 1;
        final int loop = 0;
        final float rate = 1.0f;

        if (soundID > 0) {
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    void destroy() {
        _soundPool.release();
        _soundPool = null;
    }
}
