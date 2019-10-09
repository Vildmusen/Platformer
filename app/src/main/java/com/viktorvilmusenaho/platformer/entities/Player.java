package com.viktorvilmusenaho.platformer.entities;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.viktorvilmusenaho.platformer.input.InputManager;
import com.viktorvilmusenaho.platformer.sound.JukeBox;
import com.viktorvilmusenaho.platformer.utils.Utils;

public class Player extends DynamicEntity {

    static final String TAG = "Player";
    private static final float PLAYER_RUN_SPEED = 5.0f; // meter per seconds
    private static final float PLAYER_JUMP_FORCE = -(GRAVITY * 0.4f);
    private static final float PLAYER_DASH_FORCE = 7f;
    private static final int PLAYER_DASH_DURATION = 7;
    private static final float PLAYER_MAX_SPEED = 13f;
    private static final float DEAD_ZONE = 0.05f; // 5% joystick input wont turn the character
    private static final int DAMAGE_DURATION = 50;
    private static final int PLAYER_HEALTH = 6;
    private static final int MIN_ANIMATION_SPEED = 15;
    private static final int MAX_ANIMATION_SPEED = 60;
    private static final float MAX_RUN_TILT = 10;
    private static final String PLAYER_FRONT = "player_0";
    private static final String PLAYER_SIDE_1 = "player_1";
    private static final String PLAYER_SIDE_2 = "player_2";
    private static final String PLAYER_SIDE_3 = "player_3";
    private final int LEFT = 1;
    private final int RIGHT = -1;

    private int _animationCount = 0;
    private float _animationTotalLength = 20;
    public int _health = PLAYER_HEALTH;
    private int _facing = RIGHT;
    private int _damageCounter = 0;
    private boolean _isTakingDamage = false;
    public int _coinCount = 0;
    private int _dashFrames = PLAYER_DASH_DURATION;
    private float _tilt;
    private ColorMatrixColorFilter _NoSaturationFilter = null;
    private ColorFilter _SaturationFilter = null;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _width = DEFAULT_DIMENSION;
        _height = DEFAULT_DIMENSION;
        loadBitmap(spriteName, xPos, yPos);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0f);
        _NoSaturationFilter = new ColorMatrixColorFilter(saturation);
        _SaturationFilter = new ColorFilter();
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        animateDamage(paint);
        _tilt = Math.abs(_velX) / PLAYER_MAX_SPEED * MAX_RUN_TILT;
        transform.preRotate(-(_tilt * _facing));
        transform.preScale(_facing, 1);
        if (_facing == RIGHT) { transform.postTranslate(_game.worldToScreenX(_width), 0); }
        super.render(canvas, transform, paint);
    }

    @Override
    public void update(final double dt) {
        updateDrag();
        updateDamageCounter();
        if(_damageCounter < DAMAGE_DURATION / 2) {
            manageInput();
        }
        updateMovement();
        super.update(dt);
    }

    private void updateMovement() {
        if (Math.abs(_velX) > 0.5f) {
            walk();
        } else {
            loadBitmap(PLAYER_FRONT, (int) _x, (int) _y);
            _animationCount = 0;
        }
    }

    private void updateDamageCounter() {
        if (_damageCounter == 0) {
            _isTakingDamage = false;
        } else {
            _damageCounter--;
        }
    }

    private void updateDrag() {
        if (_isOnGround) {
            _velX *= (DRAG / 1.5);
        } else {
            _velX *= DRAG;
        }
    }

    private void animateDamage(Paint paint) {
        if (_damageCounter > 0) {
            paint.setColorFilter(_NoSaturationFilter);
        } else {
            paint.setColorFilter(_SaturationFilter);
        }
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
                _game._jukebox.play(JukeBox.DASH, 0, 2);
            }
            dash(direction);
        } else if (_isOnGround) {
            _dashFrames = PLAYER_DASH_DURATION;
        }
    }

    private void walk() {
        _animationTotalLength = MAX_ANIMATION_SPEED - (MAX_ANIMATION_SPEED * (Math.abs(_velX) / PLAYER_MAX_SPEED));
        _animationTotalLength = Utils.clamp(_animationTotalLength, MIN_ANIMATION_SPEED, MAX_ANIMATION_SPEED);
        if (_animationCount > 0 && _animationCount < (_animationTotalLength * 0.25)) {
            loadBitmap(PLAYER_SIDE_1, (int) _x, (int) _y);
        }
        if (_animationCount > (_animationTotalLength * 0.25) && _animationCount < (_animationTotalLength * 0.5)) {
            loadBitmap(PLAYER_SIDE_2, (int) _x, (int) _y);
        }
        if (_animationCount > (_animationTotalLength * 0.5) && _animationCount < (_animationTotalLength * 0.75)) {
            loadBitmap(PLAYER_SIDE_1, (int) _x, (int) _y);
        }
        if (_animationCount > (_animationTotalLength * 0.75) && _animationCount < _animationTotalLength) {
            loadBitmap(PLAYER_SIDE_3, (int) _x, (int) _y);
        }
        int nextTick = _animationCount + _animationTick;
        _animationCount = (nextTick >= _animationTotalLength ? 0 : nextTick);
    }

    private void jump() {
        freezeAnimation();
        _game._jukebox.play(JukeBox.JUMP, 0, 2);
        _velY = PLAYER_JUMP_FORCE;
        _isOnGround = false;
    }

    private void dash(final float direction) {
        freezeAnimation();
        _velY = -(GRAVITY * 0.2f);
        _velX += PLAYER_DASH_FORCE * direction;
        _isOnGround = false;
        _dashFrames--;
    }

    private void updateFacingDirection(final float controlDirection) {
        if (Math.abs(controlDirection) < DEAD_ZONE) {
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
            if (that instanceof EnemyStaticEntity && !_isTakingDamage) {
                final float intensity = ((EnemyStaticEntity) that)._intensity;
                reactToEntity(intensity);
            }
        } else {
            _coinCount++;
            _game._jukebox.play(JukeBox.COIN_PICKUP, 0, 2);
        }
    }

    private void reactToEntity(float intensity) {
        _facing = _facing == LEFT ? RIGHT : LEFT;
        _velX = -(_velX) * intensity;
        _velY = PLAYER_JUMP_FORCE * intensity;
        if (!_isTakingDamage) {
            _health--;
            _isTakingDamage = true;
            _game._jukebox.play(JukeBox.DAMAGE, 0, 2);
        }
        _damageCounter = (int) (DAMAGE_DURATION * intensity);
    }
}
