package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.input.InputManager;

public class Player extends DynamicEntity {

    static final String TAG = "Player";
    static final float PLAYER_RUN_SPEED = 6.0f; // meter per seconds
    static final float PLAYER_JUMP_FORCE = -(GRAVITY/2);
    private final int LEFT = 1;
    private final int RIGHT = 1;
    private final int FRONT = 0;
    private int _facing = FRONT;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        transform.preScale(_facing, 1.0f);
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX = direction * PLAYER_RUN_SPEED;
        if(controls._isJumping && _isOnGround) {
            _velY = PLAYER_JUMP_FORCE;
            _isOnGround = false;
        }
        super.update(dt);
    }

    private void updateFacingDirection(final float controlDirection) {
        if(controlDirection < 0) {
            _facing = LEFT;
        } else if (controlDirection > 0) {
            _facing = RIGHT;
        } else {
            _facing = FRONT;
        }
    }
}
