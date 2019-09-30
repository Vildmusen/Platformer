package com.viktorvilmusenaho.platformer.levels;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Level extends LevelData {

    private static final String TAG = "LEVEL";
    private final SparseArray<String> _tileIdToSpriteName = new SparseArray<>();
    private ArrayList<String> _spriteCollection = new ArrayList<>();

    public Level(Context context, String fileName) {
        _tiles = readFromFile(context, fileName);
        for (int i = 0; i < _spriteCollection.size(); i++) {
            _tileIdToSpriteName.put(i, _spriteCollection.get(i));
        }
        updateLevelDimensions();
    }


    public int[][] readFromFile(Context context, String fileName) {
        ArrayList<char[]> lines = new ArrayList<>();
        fillCharArrayList(context, fileName, lines);
        return convertToInts(lines);
    }

    private void fillCharArrayList(final Context context, final String fileName, ArrayList<char[]> lines) {

        int resID = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        InputStream stream = context.getResources().openRawResource(resID);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buffReader = new BufferedReader(reader);

        try {
            String line = "";
            while (!((line = buffReader.readLine()).equalsIgnoreCase("-"))) {
                String[] split = line.split(":");
                _entityTypes.add(split);
                _spriteCollection.add(split[0]);
            }
            while ((line = buffReader.readLine()) != null) {
                lines.add(line.toCharArray());
            }
            buffReader.close();
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] convertToInts(ArrayList<char[]> lines) {

        int[][] spriteData = new int[lines.size()][lines.get(0).length];
        char[] row;
        char tile;

        for (int y = 0; y < spriteData.length; y++) {
            row = lines.get(y);
            for (int x = 0; x < row.length; x++) {
                tile = row[x];
                try{
                    spriteData[y][x] = Character.getNumericValue(tile);
                } catch (Exception e) {
                    Log.d(TAG, "Could not read sprite value: " + tile);
                    spriteData[y][x] = -1;
                }
            }
        }

        return spriteData;
    }

    @Override
    public String getSpriteName(int tileType) {
        final String fileName = _tileIdToSpriteName.get(tileType);
        if (fileName != null) {
            return fileName;
        }
        return NULLSPRITE;
    }
}
