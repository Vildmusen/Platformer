package com.viktorvilmusenaho.platformer.entities;

import com.viktorvilmusenaho.platformer.utils.Utils;

public abstract class DynamicEntity extends StaticEntity {

    private static final float MAX_DELTA = 0.48f;
    static final float GRAVITY = 40f;
    public static final float DRAG = 0.98f;
    public float _velX = 0;
    public float _velY = 0;
    public float _gravity = GRAVITY;
    boolean _isOnGround = false;
    public int _animationTick = 1;

    public DynamicEntity(String spriteName, int xPos, int yPos) { super(spriteName, xPos, yPos); }

    @Override
    public void update(double dt) {
        _x += Utils.clamp((float) (_velX * dt), -MAX_DELTA, MAX_DELTA);

        if (!_isOnGround) {
            final float gravityThisTick = (float) (_gravity * dt);
            _velY += gravityThisTick;
        }

        _y += Utils.clamp((float) (_velY * dt), -MAX_DELTA, MAX_DELTA);
        if (_y > _game.getWorldHeight()) {
            _y = Utils.between(-4, 0f);
        }
        _isOnGround = false;
    }

    @Override
    public void onCollision(Entity that) {
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if (Entity.overlap.y != 0) {
            _velY = 0;
            if (Entity.overlap.y < 0f) { // we've hit out feet
                _isOnGround = true;
                continueAnimation();
            }
        }
        if (Entity.overlap.x != 0 && !(that instanceof EnemyStaticEntity)) {
            _velX = 0;
        }
    }

    public void continueAnimation() {
        _animationTick = 1;
    }

    public void freezeAnimation() {
        _animationTick = 0;
    }
}
