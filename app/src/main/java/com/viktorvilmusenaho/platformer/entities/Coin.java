package com.viktorvilmusenaho.platformer.entities;

public class Coin extends DynamicEntity {

    public Coin(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
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
