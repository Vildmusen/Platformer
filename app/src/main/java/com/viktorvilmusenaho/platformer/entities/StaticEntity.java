package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.utils.BitmapUtils;

public class StaticEntity extends Entity {
    // TODO bitmap pool
    static final float DEFAULT_DIMENSION = 1.0f; //in meters
    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xpos, final int ypos) {
        _width = DEFAULT_DIMENSION;
        _height = DEFAULT_DIMENSION;
        _x = xpos;
        _y = ypos;
        loadBitmap(spriteName, xpos, ypos);
    }

    void loadBitmap(final String spriteName, final int xpos, final int ypos) {
        destroy();
        final int widthPixels = _game.worldToScreenX(_width);
        final int heightPixels = _game.worldToScreenY(_height);
        try{
            _bitmap = BitmapUtils.loadScaledBitmap(_game.getContext(), spriteName, widthPixels, heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(final Canvas canvas, final Matrix transform, final Paint paint) {
        canvas.drawBitmap(_bitmap, transform, paint);
    }

    @Override
    public void destroy() {
        if (_bitmap != null) {
            _bitmap.recycle();
            _bitmap = null;
        }
    }
}
