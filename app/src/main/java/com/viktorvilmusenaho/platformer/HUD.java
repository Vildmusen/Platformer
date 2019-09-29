package com.viktorvilmusenaho.platformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.entities.Player;

public class HUD {

    private static final int HUD_EDGE_MARGIN = 30;
    private static final String PLAYER_HEALTH_HALF = "lifehearth_half";
    private static final String COIN = "coin";

    private Player _player = null;
    private Bitmap _halfHearth = null;
    private Bitmap _coin = null;

    public HUD(Game game) {
        _player = game._level._player;
        _halfHearth = game._pool.createBitmap(PLAYER_HEALTH_HALF, 0, 0);

        if ((_coin = game._pool.getBitmap(COIN)) == null) {
            _coin = game._pool.createBitmap(COIN, 0, 0);
        }
    }

    public void renderHUD(Canvas canvas, Matrix transform, Paint paint) {
        renderCoinCount(canvas, transform, paint);
        renderHearts(canvas, transform, paint);
    }

    private void renderHearts(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate((HUD_EDGE_MARGIN + _halfHearth.getWidth()), HUD_EDGE_MARGIN);
        transform.preScale(2, 2);

        for (int i = 0; i < _player._health; i++) {
            canvas.drawBitmap(_halfHearth, transform, paint);
            if (i % 2 == 0) {
                transform.preScale(-1, 1.0f); // if first half of heart, mirror it.
            } else {
                transform.postTranslate(30, 0); // if last half of heart, move it.
            }
        }
    }

    private void renderCoinCount(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate(Game.STAGE_WIDTH - (HUD_EDGE_MARGIN + _coin.getWidth()), HUD_EDGE_MARGIN);
        canvas.drawBitmap(_coin, transform, paint);
        paint.setColor(Color.WHITE);
        int textSize = _coin.getHeight();
        paint.setTextSize(textSize);
        canvas.drawText(String.format("%d", _player._coinCount), Game.STAGE_WIDTH - ((HUD_EDGE_MARGIN) + _coin.getWidth() * 2), HUD_EDGE_MARGIN + textSize, paint);

    }

}
