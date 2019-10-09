package com.viktorvilmusenaho.platformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.entities.Player;

public class HUD {

    private static final String START_MESSAGE = "Collect all the coins before the timer runs out!";
    private static final String GAME_OVER_MESSAGE_1 = "GAME OVER";
    private static final String GAME_OVER_MESSAGE_2 = "Press A to try again!";
    private static final int HUD_EDGE_MARGIN = 30;
    private static final String PLAYER_HEALTH_HALF = "lifehearth_half";
    private static final String COIN = "coin";

    private Game _game = null;
    public Player _player = null;
    private Bitmap _halfHearth = null;
    private Bitmap _coin = null;
    private Bitmap _refresh = null;
    public int _coinCount = 0;
    private int _textSize = 30;

    public HUD(Game game) {
        _game = game;
        _player = _game._level._player;
        _halfHearth = _game._pool.createBitmap(PLAYER_HEALTH_HALF, 0, 0);
        if ((_coin = _game._pool.getBitmap(COIN)) == null) {
            _coin = _game._pool.createBitmap(COIN, 1, 1);
        }
        _textSize = _coin.getHeight() / 2;
        _coinCount =  _game._level._coinCount;
    }

    public void renderHUD(Canvas canvas, Matrix transform, Paint paint) {
        renderCoinCount(canvas, transform, paint);
        renderHearts(canvas, transform, paint);
        renderText(canvas, paint);
    }

    private void renderHearts(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate((HUD_EDGE_MARGIN + _halfHearth.getWidth()), HUD_EDGE_MARGIN);
        transform.preScale(1, 1);

        for (int i = 0; i < _player._health; i++) {
            canvas.drawBitmap(_halfHearth, transform, paint);
            if (i % 2 == 0) {
                transform.preScale(-1, 1.0f); // if first half of heart, flip it.
            } else {
                transform.postTranslate(30, 0); // if last half of heart, move it.
            }
        }
    }

    private void renderCoinCount(Canvas canvas, Matrix transform, Paint paint) {
        transform.reset();
        transform.postTranslate(_game.STAGE_WIDTH - (HUD_EDGE_MARGIN + _coin.getWidth()), HUD_EDGE_MARGIN);
        canvas.drawBitmap(_coin, transform, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(_textSize);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("%d / %d", _player._coinCount, _coinCount),
                _game.STAGE_WIDTH - ((HUD_EDGE_MARGIN) + _coin.getWidth() * 1.5f),
                HUD_EDGE_MARGIN - metrics.ascent, paint);
    }

    private void renderText(Canvas canvas, Paint paint) {
        final float timeLeft = _game._timeLeft;
        renderTimer(canvas, paint, timeLeft);
        if(timeLeft > _game.CURRENT_LEVEL_TIME_LIMIT * 0.95) {
            renderStartMessage(canvas, paint);
        }
    }

    private void renderStartMessage(Canvas canvas, Paint paint) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (_textSize * 1.5));
        String text = START_MESSAGE;
        canvas.drawText(text, _game.STAGE_WIDTH / 2, _game.STAGE_HEIGHT * 0.67f, paint);
    }

    private void renderTimer(Canvas canvas, Paint paint, final float timeLeft) {
        String text = String.format("Time left: %s", (int) (timeLeft * 10));
        paint.setTextAlign(Paint.Align.CENTER);
        if (timeLeft < _game.CURRENT_LEVEL_TIME_LIMIT * 0.314) {
            animateTextSize(paint, timeLeft * 5);
        }
        canvas.drawText(text, _game.STAGE_WIDTH / 2, paint.getTextSize() + HUD_EDGE_MARGIN, paint);
    }

    private void animateTextSize(Paint paint, final float timeLeft) {
        double size = _textSize + (5 * Math.sin(timeLeft));
        paint.setColor(Color.RED);
        paint.setTextSize((int) size);
    }

    public void renderGameOver(Canvas canvas, Paint paint){
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (_textSize * 1.5));
        canvas.drawText(GAME_OVER_MESSAGE_1, _game.STAGE_WIDTH / 2, _game.STAGE_HEIGHT * 0.4f, paint);
        canvas.drawText(GAME_OVER_MESSAGE_2, _game.STAGE_WIDTH / 2, _game.STAGE_HEIGHT * 0.5f, paint);
    }
}
