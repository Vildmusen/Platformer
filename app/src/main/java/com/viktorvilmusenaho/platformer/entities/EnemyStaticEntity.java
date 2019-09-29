package com.viktorvilmusenaho.platformer.entities;

public abstract class EnemyStaticEntity extends StaticEntity {

    public float _intensity = 0;

    public EnemyStaticEntity(String spriteName, int xPos, int yPos) {
        super(spriteName, xPos, yPos);
    }
}
