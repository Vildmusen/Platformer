package com.viktorvilmusenaho.platformer.levels;

public abstract class LevelData {

    public static final String NULLSPRITE = "nullsprite";
    public static final String PLAYER__SIDE_1 = "player_1";
    public static final String PLAYER__SIDE_2 = "player_2";
    public static final String PLAYER__SIDE_3 = "player_3";
    public static final String PLAYER_FRONT = "player_0";

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
