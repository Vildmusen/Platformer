package com.viktorvilmusenaho.platformer.entities;

import java.io.Serializable;

public class DynamicEntityInformation implements Serializable {

    public String spriteName = "";
    public float x = 0;
    public float y = 0;
    public float velX = 0;
    public float velY = 0;
    public int animationTick = 0;

    public DynamicEntityInformation() {}

}
