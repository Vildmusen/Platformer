package com.viktorvilmusenaho.platformer.entities;

public class Lava extends EnemyStaticEntity {

    public Lava(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _intensity = 0.9f;
    }

    @Override
    public void onCollision(Entity that) {
        if(that instanceof Player) {
            // TODO splash
        }
    }
}
