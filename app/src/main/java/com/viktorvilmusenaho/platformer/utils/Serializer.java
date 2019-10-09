package com.viktorvilmusenaho.platformer.utils;

import com.viktorvilmusenaho.platformer.Game;
import com.viktorvilmusenaho.platformer.entities.DynamicEntity;
import com.viktorvilmusenaho.platformer.entities.Entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Serializer {

    private static final String SAVE_FILE = "data.ser";

    public String _path = "";
    public SaveObject _gameData = null;
    public Game _game = null;

    public Serializer(Game game) {
        _path = game.getContext().getFilesDir().getPath() + "/" + SAVE_FILE;
        _gameData = new SaveObject();
        _game = game;
    }

    public void save() {
        setLevelInformation();
        setEntitiesInformation();
        serializeData();
    }

    public void load() {
        deSerializeData();
    }

    private void setLevelInformation() {
        _gameData._currentLevel = _game.CURRENT_LEVEL;
        _gameData._currentTimeLeft = _game._timeLeft;
        _gameData._coinCount = _game._level._player._coinCount;
        _gameData._playerHealth = _game._level._player._health;
    }

    private void setEntitiesInformation() {
        _gameData._entityInfo = new ArrayList<>(); // reset list before each save
        for (Entity e : _game._level._entities) {
            if (e instanceof DynamicEntity) {
                DynamicEntityInformation entityInformation = new DynamicEntityInformation();
                entityInformation.spriteName = ((DynamicEntity) e)._bitmapName;
                entityInformation.x = e._x;
                entityInformation.y = e._y;
                entityInformation.velX = ((DynamicEntity) e)._velX;
                entityInformation.velY = ((DynamicEntity) e)._velY;
                entityInformation.animationTick = ((DynamicEntity) e)._animationTick;
                _gameData._entityInfo.add(entityInformation);
            }
        }
    }

    private void serializeData() {
        try {
            deleteFile(_path);
            FileOutputStream file = new FileOutputStream(_path);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(_gameData);
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    private void deSerializeData() {
        try {
            FileInputStream file = new FileInputStream(_path);
            ObjectInputStream in = new ObjectInputStream(file);
            _gameData = (SaveObject)in.readObject();
            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
