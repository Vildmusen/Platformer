package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Coin extends DynamicEntity {

    public Coin(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        transform.preScale(0.7f,0.7f);
        transform.postTranslate((_height - (_height * 0.7f)), (_width - (_width/2)));
        super.render(canvas, transform, paint);
    }

    @Override
    public void onCollision(Entity that) {
        if (that instanceof Player) {
            _game._level.removeEntity(this);
        } else {
            super.onCollision(that);
        }
    }
}
