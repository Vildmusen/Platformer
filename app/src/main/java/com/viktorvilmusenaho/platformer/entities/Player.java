package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.input.InputManager;

public class Player extends DynamicEntity {

    static final String TAG = "Player";
    private static final float PLAYER_RUN_SPEED = 6.0f; // meter per seconds
    private static final float PLAYER_JUMP_FORCE = -(GRAVITY / 2);
    private static final float MIN_INPUT_TO_TURN = 0.05f; // 5% joystick input wont turn the character
    private static final int DAMAGE_DURATION = 50;
    private static final int PLAYER_HEALTH = 6;
    private static final float PLAYER_DASH_FORCE = GRAVITY * 2f;
    private static final int PLAYER_DASH_DURATION = 10;
    private final int LEFT = 1;
    private final int RIGHT = -1;
    private final int FRONT = 0;

    public int _health = 0;
    private int _facing = RIGHT;
    private int _damageCounter = 0;
    private boolean _isTakingDamage = false;
    public int _coinCount = 0;
    private int _dashFrames = PLAYER_DASH_DURATION;

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
        } else {
            _damageCounter--;
        }
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX = direction * PLAYER_RUN_SPEED;
        updateFacingDirection(direction);
        if (controls._isJumping && _isOnGround) {
            _velY = PLAYER_JUMP_FORCE;
            _isOnGround = false;
        }
        if (controls._isDashing && _dashFrames > 0) {
            _velY = -(GRAVITY * 0.2f);
            _velX = PLAYER_DASH_FORCE * direction;
            _isOnGround = false;
            _dashFrames--;
        } else {
            if (_isOnGround) {
                _dashFrames = PLAYER_DASH_DURATION;
            }
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
        if (!(that instanceof Coin)) {
            super.onCollision(that);
            if (that instanceof EnemyStaticEntity) {
                final float intensity = ((EnemyStaticEntity) that)._intensity;
                _velY = PLAYER_JUMP_FORCE * intensity;
                reactToEntity(intensity);
            }
        } else {
            _coinCount++;
        }
    }

    private void reactToEntity(float intensity) {
        if (_facing == LEFT) {
            _facing = RIGHT;
        } else {
            _facing = LEFT;
        }
        _velX = -(_velX * intensity);
        if (!_isTakingDamage) {
            _health--;
            _isTakingDamage = true;
        }
        _damageCounter = (int) (DAMAGE_DURATION * intensity);
    }
}
