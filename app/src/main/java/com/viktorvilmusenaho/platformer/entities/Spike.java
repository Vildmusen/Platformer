package com.viktorvilmusenaho.platformer.entities;

public class Spike extends StaticEntity {

    public Spike(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    @Override
    public void onCollision(Entity that) {
        if(that instanceof Player) {
            // TODO ouchie wow wow
        }
    }

}
