package com.viktorvilmusenaho.platformer.entities;

public class Spike extends EnemyStaticEntity {

    public Spike(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _intensity = 0.7f;
    }

    @Override
    public void onCollision(Entity that) {
        if(that instanceof Player) {
            // TODO ouchie wow wow
        }
    }

}
