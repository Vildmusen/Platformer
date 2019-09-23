package com.viktorvilmusenaho.platformer.levels;

public abstract class LevelData {

    public static final String NULLSPRITE = "nullsprite";
    public static final String PLAYER_FRONT = "red_front1";
    public static final String PLAYER_SIDE = "red_left1";
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
