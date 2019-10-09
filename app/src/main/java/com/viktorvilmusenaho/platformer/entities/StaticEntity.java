package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class StaticEntity extends Entity {

    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xPos, final int yPos) {
        _x = xPos;
        _y = yPos;
        _bitmapName = spriteName;
        loadBitmap(spriteName, xPos, yPos);
    }

    public void loadBitmap(final String spriteName, final int xPos, final int ypos) {
        _bitmap = _game._pool.createBitmap(spriteName, _width, _height);
    }

    @Override
    public void render(final Canvas canvas, final Matrix transform, final Paint paint) {
        canvas.drawBitmap(_bitmap, transform, paint);
    }

    @Override
    public void destroy() {}
}
