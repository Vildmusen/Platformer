package com.viktorvilmusenaho.platformer.entities;

public class Spike extends EnemyStaticEntity {

    public static final float SPIKE_INTENSITY = 0.7f;

    public Spike(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _intensity = SPIKE_INTENSITY;
    }

}
