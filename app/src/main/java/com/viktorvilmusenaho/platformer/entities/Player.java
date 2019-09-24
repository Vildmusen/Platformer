package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import com.viktorvilmusenaho.platformer.input.InputManager;
import com.viktorvilmusenaho.platformer.utils.Utils;

public class Player extends DynamicEntity {

    static final String TAG = "Player";
    static final float PLAYER_RUN_SPEED = 6.0f; // meter per seconds
    static final float PLAYER_JUMP_FORCE = -(GRAVITY / 2);
    static final float MIN_INPUT_TO_TURN = 0.05f; // 5% joystick input wont turn the character
    static final int DAMAGE_DURATION = 50;
    static final int PLAYER_HEALTH = 6;
    private final int LEFT = 1;
    private final int RIGHT = -1;
    private final int FRONT = 0;

    public int _health = 0;
    private int _facing = RIGHT;
    private int _damageCounter = 0;
    private boolean _isTakingDamage = false;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _width = DEFAULT_DIMENSION;
        _height = DEFAULT_DIMENSION;
        _health = PLAYER_HEALTH;
        loadBitmap(spriteName, xPos, yPos);
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        transform.preScale(_facing, 1.0f);
        if (_facing == RIGHT) {
            final float offset = _game.worldToScreenX(_width);
            transform.postTranslate(offset, 0);
        }
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(final double dt) {
        if (_damageCounter == 0) {
            _isTakingDamage = false;
            final InputManager controls = _game.getControls();
            final float direction = controls._horizontalFactor;
            _velX = direction * PLAYER_RUN_SPEED;
            updateFacingDirection(direction);
            if (controls._isJumping && _isOnGround) {
                _velY = PLAYER_JUMP_FORCE;
                _isOnGround = false;
            }
        } else {
            _damageCounter--;
        }
        super.update(dt);
    }

    private void updateFacingDirection(final float controlDirection) {
        if (Math.abs(controlDirection) < MIN_INPUT_TO_TURN) {
            return;
        }
        if (controlDirection < 0) {
            _facing = LEFT;
        } else if (controlDirection > 0) {
            _facing = RIGHT;
        }
    }

    @Override
    public void onCollision(Entity that) {
        super.onCollision(that);
        float intensity = 0f;
        if (that instanceof Lava) {
            intensity = 1.2f;
            _velY = PLAYER_JUMP_FORCE * intensity;
            reactToEntity(intensity);
            _health--;
        }
        if (that instanceof Spike) {
            intensity = 0.8f;
            _velY = PLAYER_JUMP_FORCE * intensity;
            reactToEntity(intensity);
            _health--;
        }
    }

    private void reactToEntity(float intensity){
        if (_facing == LEFT) {
            _facing = RIGHT;
        } else {
            _facing = LEFT;
        }
        _velX = -_velX;
        _velX = _velX * intensity;
        _isTakingDamage = true;
        _damageCounter = (int) (DAMAGE_DURATION * intensity);
    }
}
