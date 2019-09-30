package com.viktorvilmusenaho.platformer.levels;

import java.util.ArrayList;

public abstract class LevelData {

    public String NULLSPRITE = "nullsprite";
    public String BACKGROUND = "background";
    public String PLAYER = "player_0";
    public String SPEAR_LEFT = "spearsup_brown_left";
    public String SPEAR_RIGHT = "spearsup_brown_right";
    public String LAVA = "spearsup_brown_left";
    public String COIN = "coin";

    public ArrayList<String[]> _entityTypes = new ArrayList<>();
    public static final int NO_TILE = 0;
    int[][] _tiles;
    int _height = 0;
    int _width = 0;

    public int getTile(final int x, final int y){
        return _tiles[y][x];
    }

    int[] getRow(final int y){
        return _tiles[y];
    }

    protected void updateLevelDimensions(){
        _height = _tiles.length;
        _width = _tiles[0].length;
    }

    abstract public String getSpriteName(final int tileType);
}
