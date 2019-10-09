package com.viktorvilmusenaho.platformer.entities;

public class Lava extends EnemyStaticEntity {

    public static final float LAVA_INTENSITY = 0.9f;

    public Lava(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        _intensity = LAVA_INTENSITY;
    }
}
