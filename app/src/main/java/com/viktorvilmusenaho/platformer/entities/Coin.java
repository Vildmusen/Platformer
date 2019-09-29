package com.viktorvilmusenaho.platformer.entities;

import com.viktorvilmusenaho.platformer.utils.Utils;

public class Coin extends DynamicEntity {

    public Coin(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    public void spawn() {
        _velX = Utils.nextInt(12);
        _velY = Utils.nextInt(10);
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
