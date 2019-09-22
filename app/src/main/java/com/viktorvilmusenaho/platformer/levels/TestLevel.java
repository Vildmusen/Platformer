package com.viktorvilmusenaho.platformer.levels;

import android.util.SparseArray;

public class TestLevel extends LevelData {

    private final SparseArray<String> _tileIdToSpriteName = new SparseArray<>();


    public TestLevel(){
        _tileIdToSpriteName.put(0, "background");
        _tileIdToSpriteName.put(1, PLAYER);
        _tileIdToSpriteName.put(2, "ground");
        _tileIdToSpriteName.put(3, "ground_left");
        _tileIdToSpriteName.put(4, "ground_right");
        _tileIdToSpriteName.put(5, "ground_background_full");

        _tiles = new int[][]{
                {5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5},
                {5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5},
                {5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5},
                {5,0,3,2,4,0,1,0,0,0,0,0,0,0,3,4,0,5},
                {5,0,5,5,5,0,0,0,0,0,0,0,0,0,5,5,0,5},
                {5,0,3,2,2,2,2,2,2,2,4,0,0,0,5,5,0,5},
                {5,0,5,5,5,5,5,5,5,5,5,0,0,0,5,5,0,5},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,5},
                {5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5},
                {5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5},
        };
        updateLevelDimensions();
    }

    @Override
    public String getSpriteName(int tileType) {
        final String fileName = _tileIdToSpriteName.get(tileType);
        if(fileName != null){
            return fileName;
        }
        return NULLSPRITE;
    }
}
