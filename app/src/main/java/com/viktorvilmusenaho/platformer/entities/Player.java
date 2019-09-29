package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.input.InputManager;
import com.viktorvilmusenaho.platformer.sound.JukeBox;
import com.viktorvilmusenaho.platformer.utils.Utils;

import java.util.ArrayList;

public class Player extends DynamicEntity {

    static final String TAG = "Player";
    private static final float PLAYER_RUN_SPEED = 5.0f; // meter per seconds
    private static final float PLAYER_JUMP_FORCE = -(GRAVITY * 0.4f);
    private static final float PLAYER_DASH_FORCE = 3f;
    private static final int PLAYER_DASH_DURATION = 7;
    private static final float PLAYER_MAX_SPEED = 13f;
    private static final float MIN_INPUT_TO_TURN = 0.05f; // 5% joystick input wont turn the character
    private static final int DAMAGE_DURATION = 50;
    private static final int PLAYER_HEALTH = 6;
    private final int LEFT = 1;
    private final int RIGHT = -1;

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
        if (Math.abs(_velX) > 0.5f) {
            _bitmap = _game._pool.getBitmap("player_1_1.0_1.0");
        } else {
            _bitmap = _game._pool.getBitmap("player_0_1.0_1.0");
        }
        transform.preScale(_facing, 1);
        if (_facing == RIGHT) {
            transform.postTranslate(_game.worldToScreenX(_width), 0);
        }
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(final double dt) {
        if(_isOnGround) {
            _velX *= (DRAG / 1.5);
        } else {
            _velX *= DRAG;
        }
        if (_damageCounter == 0) {
            _isTakingDamage = false;
        } else {
            _damageCounter--;
        }
        manageInput();
        super.update(dt);
    }

    private void manageInput() {
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX += direction * PLAYER_RUN_SPEED * 0.5;
        _velX = Utils.clamp(_velX, -PLAYER_MAX_SPEED, PLAYER_MAX_SPEED);
        updateFacingDirection(direction);
        if (controls._isJumping && _isOnGround) {
            jump();
        }
        if (controls._isDashing && _dashFrames > 0) {
            if (_dashFrames == PLAYER_DASH_DURATION) {
                _game._jukebox.play(JukeBox.DASH, 0);
            }
            dash(direction);
        } else if (_isOnGround) {
            _dashFrames = PLAYER_DASH_DURATION;
        }
    }

    private void jump() {
        _game._jukebox.play(JukeBox.JUMP, 0);
        _velY = PLAYER_JUMP_FORCE;
        _isOnGround = false;
    }

    private void dash(final float direction) {
        _velY = -(GRAVITY * 0.2f);
        _velX += PLAYER_DASH_FORCE * direction;
        _isOnGround = false;
        _dashFrames--;
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
                reactToEntity(intensity);
            }
        } else {
            _coinCount++;
            _game._jukebox.play(JukeBox.COIN_PICKUP, 0);
        }
    }

    private void reactToEntity(float intensity) {
        if (_facing == LEFT) {
            _facing = RIGHT;
        } else {
            _facing = LEFT;
        }
        _velY = PLAYER_JUMP_FORCE * intensity;
        if (!_isTakingDamage) {
            _health--;
            _isTakingDamage = true;
            _game._jukebox.play(JukeBox.DAMAGE, 0);
        }
        _damageCounter = (int) (DAMAGE_DURATION * intensity);
    }
}
