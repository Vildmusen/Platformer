package com.viktorvilmusenaho.platformer.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.viktorvilmusenaho.platformer.Game;

import java.util.*;

public class BitmapPool {
    private static final String TAG = "BitmapPool";
    private final HashMap _bitmaps = new HashMap<>();
    private Bitmap _nullsprite = null;
    private Game _game;

    public BitmapPool(final Game game) {
        Objects.requireNonNull(game, "BitmapPool requires a valid Game instance");
        _game = game;
        _nullsprite = createBitmap("nullsprite", 0.5f, 0.5f); //the nullsprite, in case loading fails later
        Objects.requireNonNull(_nullsprite, "BitmapPool: unable to create nullsprite!");
    }

    public Bitmap createBitmap(final String sprite, float widthMeters, float heightMeters) {
        final String key = makeKey(sprite, widthMeters, heightMeters);
        Bitmap bmp = getBitmap(key);
        if (bmp != null) {
            return bmp;
        }
        try {
            bmp = BitmapUtils.loadScaledBitmap(_game.getContext(), sprite, (int) _game.worldToScreenX(widthMeters), (int) _game.worldToScreenY(heightMeters));
            put(key, bmp);
        } catch (final OutOfMemoryError e) {
            //this is very very bad! Ideally you have some reference counted assets and can start unloading as needed
            Log.w(TAG, "Out of Memory!", e);
        } finally {
            if (bmp == null) {
                bmp = _nullsprite;
            }
        }
        return bmp;
    }

    public int size() {
        return _bitmaps.size();
    }

    public String makeKey(final String name, final float widthMeters, final float heightMeters) {
        return name + "_" + widthMeters + "_" + heightMeters;
    }

    public void put(final String key, final Bitmap bmp) {
        if (_bitmaps.containsKey(key)) {
            return;
        }
        _bitmaps.put(key, bmp);
    }

    public boolean contains(final String key) {
        return _bitmaps.containsKey(key);
    }

    public boolean contains(final Bitmap bmp) {
        return _bitmaps.containsValue(bmp);
    }

    public Bitmap getBitmap(final String key) {
        return (Bitmap) _bitmaps.get(key);
    }

    private String getKey(final Bitmap bmp) {
        if (bmp != null) {
            for (Object entry : _bitmaps.entrySet()) {
                HashMap.Entry<String, Bitmap> pair = (HashMap.Entry<String, Bitmap>) entry;
                if (bmp == pair.getValue()) {
                    return pair.getKey();
                }
            }
        }
        return "";
    }

    private void remove(final String key) {
        Bitmap tmp = (Bitmap) _bitmaps.get(key);
        if (tmp != null) {
            _bitmaps.remove(key);
            tmp.recycle();
        }
    }

    public void remove(Bitmap bmp) {
        if (bmp == null) {
            return;
        }
        remove(getKey(bmp));
    }

    public void empty() {
        for (final Object entry : _bitmaps.entrySet()) {
            HashMap.Entry<String, Bitmap> temp = (HashMap.Entry<String, Bitmap>) entry;
            temp.getValue().recycle();
        }
        _bitmaps.clear();
    }
}