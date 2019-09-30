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
    private static final String REFRESH = "repeat";
    private static final String COIN = "coin";

    private Game _game = null;
    private Player _player = null;
    private Bitmap _halfHearth = null;
    private Bitmap _coin = null;
    private Bitmap _refresh = null;
    private int _coinCount = 0;
    private int _textSize = 30;

    public HUD(Game game) {
        _game = game;
        _player = game._level._player;
        _halfHearth = game._pool.createBitmap(PLAYER_HEALTH_HALF, 0, 0);
        _refresh = game._pool.createBitmap(REFRESH, 2, 2);
        if ((_coin = game._pool.getBitmap(COIN)) == null) {
            _coin = game._pool.createBitmap(COIN, 1, 1);
        }
        _textSize = _coin.getHeight() / 2;
        _coinCount =  _game._level._coinCount;
    }

    public void renderHUD(Canvas canvas, Matrix transform, Paint paint) {
        renderCoinCount(canvas, transform, paint);
        renderHearts(canvas, transform, paint);
        renderText(canvas, transform, paint);
    }

    private void renderHearts(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate((HUD_EDGE_MARGIN + _halfHearth.getWidth()), HUD_EDGE_MARGIN);
        transform.preScale(1, 1);

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
        paint.setTextSize(_textSize);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("%d / %d", _player._coinCount, _coinCount),
                Game.STAGE_WIDTH - ((HUD_EDGE_MARGIN) + _coin.getWidth() * 1.5f),
                HUD_EDGE_MARGIN - metrics.ascent, paint);
    }

    private void renderText(Canvas canvas, Matrix transform, Paint paint) {
        final float timeLeft = _game._timeLeft;
        renderTimer(canvas, paint, timeLeft);
        if(timeLeft > _game.TIME_LIMIT * 0.95) {
            renderStartMessage(canvas, paint);
        }
        if(_player._health <= 0 || _game._timeLeft <= 0) {
            renderGameOver(canvas, transform, paint);
        }
    }

    private void renderStartMessage(Canvas canvas, Paint paint) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (_textSize * 1.5));
        String text = "Collect all the coins before the timer runs out!";
        canvas.drawText(text, Game.STAGE_WIDTH / 2, Game.STAGE_HEIGHT * 0.67f, paint);
    }

    private void renderTimer(Canvas canvas, Paint paint, final float timeLeft) {
        String text = String.format("Time left: %s", (int) (timeLeft * 10));
        paint.setTextAlign(Paint.Align.CENTER);
        if (timeLeft < _game.TIME_LIMIT * 0.314) {
            animateTextSize(paint, timeLeft * 5);
        }
        canvas.drawText(text, Game.STAGE_WIDTH / 2, paint.getTextSize() + HUD_EDGE_MARGIN, paint);
    }

    private void animateTextSize(Paint paint, final float timeLeft) {
        double size = _textSize + (5 * Math.sin(timeLeft));
        paint.setColor(Color.RED);
        paint.setTextSize((int) size);
    }

    public void renderGameOver(Canvas canvas, Matrix transform, Paint paint){
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (_textSize * 1.5));
        String text1 = "GAME OVER";
        String text2 = "Press here to try again!";
        canvas.drawText(text1, Game.STAGE_WIDTH / 2, Game.STAGE_HEIGHT * 0.4f, paint);
        canvas.drawText(text2, Game.STAGE_WIDTH / 2, Game.STAGE_HEIGHT * 0.5f, paint);
        transform.reset();
        transform.postTranslate((Game.STAGE_WIDTH / 2) - (_refresh.getWidth() / 2), Game.STAGE_HEIGHT * 0.6f);
        canvas.drawBitmap(_refresh, transform, paint);
    }
}
