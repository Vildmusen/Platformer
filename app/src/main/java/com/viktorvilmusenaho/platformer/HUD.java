package com.viktorvilmusenaho.platformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.entities.Player;

public class HUD {

    private static final String PLAYER_HEALTH_HALF = "lifehearth_half";
    private static final String PLAYER_HEALTH_EMPTY = "lifehearth_empty";

    private Player _player = null;
    private Bitmap _halfHearth = null;
    private Bitmap _emptyHearth = null;

    public HUD(Game game) {
        _player = game._level._player;

        _halfHearth = game._pool.createBitmap(PLAYER_HEALTH_HALF, 0,0);
        _emptyHearth = game._pool.createBitmap(PLAYER_HEALTH_EMPTY, 0,0);
    }

    public void renderHUD(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate(30, 20);
        transform.preScale(2, 2);

        for (int i = 0; i < _player._health; i++){
            canvas.drawBitmap(_halfHearth, transform, paint);
            if(i % 2 == 0){
                transform.preScale(-1, 1.0f);
            } else {
                transform.postTranslate(30, 0);
            }
        }

    }

}
