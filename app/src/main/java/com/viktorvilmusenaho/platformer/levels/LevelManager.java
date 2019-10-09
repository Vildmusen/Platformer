package com.viktorvilmusenaho.platformer.levels;

import android.util.Log;

import com.viktorvilmusenaho.platformer.entities.Coin;
import com.viktorvilmusenaho.platformer.entities.DynamicEntity;
import com.viktorvilmusenaho.platformer.utils.DynamicEntityInformation;
import com.viktorvilmusenaho.platformer.entities.Entity;
import com.viktorvilmusenaho.platformer.entities.Lava;
import com.viktorvilmusenaho.platformer.entities.Player;
import com.viktorvilmusenaho.platformer.entities.Spike;
import com.viktorvilmusenaho.platformer.entities.StaticEntity;
import com.viktorvilmusenaho.platformer.utils.BitmapPool;

import java.io.Serializable;
import java.util.ArrayList;

public class LevelManager implements Serializable {

    public static final String TAG = "LEVEL_MANAGER";

    public int _levelHeight = 0;
    public int _levelWidth = 0;

    public final ArrayList<Entity> _entities = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToAdd = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToRemove = new ArrayList<>();

    private LevelData _data = null;
    public Player _player = null;
    private BitmapPool _pool = null;
    public int _coinCount = 0;

    public LevelManager(final LevelData map, BitmapPool pool) {
        _pool = pool;
        _data = map;
        loadEntityTypes();
        loadMapAssets();
        _coinCount = numberOfCoins();
    }

    private void loadEntityTypes() {
        for (String[] keyTypePair : _data._entityTypes) {
            int key = -1;
            try {
                key = Integer.parseInt(keyTypePair[1]);
            } catch (Exception e) {
                Log.d(TAG, String.format("could not read entity type: %s, %s ", keyTypePair[0], keyTypePair[1]));
            }
            switch (key) {
                case 0:
                    _data.BACKGROUND = keyTypePair[0];
                    break;
                case 1:
                    _data.PLAYER = keyTypePair[0];
                    break;
                case 2:
                    _data.LAVA = keyTypePair[0];
                    break;
                case 3:
                    _data.SPEAR_LEFT = keyTypePair[0];
                    break;
                case 4:
                    _data.SPEAR_RIGHT = keyTypePair[0];
                    break;
                case 5:
                    _data.COIN = keyTypePair[0];
                    break;
            }
        }
    }

    public void update(final double dt) {
        for (Entity e : _entities) {
            e.update(dt);
        }
        checkCollisions();
        addAndRemoveEntities();
    }

    private void checkCollisions() {
        final int count = _entities.size();
        Entity a, b;
        for (int i = 0; i < count - 1; i++) {
            a = _entities.get(i);
            for (int j = i + 1; j < count; j++) {
                b = _entities.get(j);
                if (a.isColliding(b)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }

    private void loadMapAssets() {
        cleanUp();
        _levelHeight = _data._height;
        _levelWidth = _data._width;

        for (int y = 0; y < _levelHeight; y++) {
            final int[] row = _data.getRow(y);
            for (int x = 0; x < row.length; x++) {
                final int tileID = row[x];
                if (tileID == LevelData.NO_TILE) {
                    continue;
                }
                final String spriteName = _data.getSpriteName(tileID);
                createEntity(spriteName, x, y);
            }
        }
    }

    private void createEntity(final String spriteName, final int xPos, final int yPos) {
        Entity e = initEntity(spriteName, xPos, yPos);
        addEntity(e);
    }

    private void createEntity(final String spriteName, final int xPos, final int yPos, final float velX, final float velY) {
        Entity e = initEntity(spriteName, xPos, yPos);
        ((DynamicEntity) e)._velX = velX;
        ((DynamicEntity) e)._velY = velY;
        addEntity(e);
    }

    private Entity initEntity(final String spriteName, final int xPos, final int yPos) {
        Entity e = null;
        if (spriteName.equalsIgnoreCase(_data.PLAYER)) {
            e = new Player(spriteName, xPos, yPos);
            _player = (Player) e;
        } else if (spriteName.equalsIgnoreCase(_data.LAVA)) {
            e = new Lava(spriteName, xPos, yPos);
        } else if (spriteName.equalsIgnoreCase(_data.SPEAR_LEFT) || spriteName.equalsIgnoreCase(_data.SPEAR_RIGHT)) {
            e = new Spike(spriteName, xPos, yPos);
        } else if (spriteName.equalsIgnoreCase(_data.COIN)) {
            e = new Coin(spriteName, xPos, yPos);
        } else {
            e = new StaticEntity(spriteName, xPos, yPos);
        }
        return e;
    }

    public void addAndRemoveEntities() {
        for (Entity e : _entitiesToRemove) {
            _entities.remove(e);
        }
        for (Entity e : _entitiesToAdd) {
            _entities.add(e);
        }
        _entitiesToRemove.clear();
        _entitiesToAdd.clear();
    }

    private void addEntity(final Entity e) {
        if (e != null) {
            _entitiesToAdd.add(e);
        }
    }

    public void removeEntity(final Entity e) {
        if (e != null) {
            _entitiesToRemove.add(e);
        }
    }

    public void addUpdatedDynamicEntities(ArrayList<DynamicEntityInformation> entityInfo) {
        for (DynamicEntityInformation e : entityInfo) {
            createEntity(e.spriteName, (int) e.x, (int) e.y, e.velX, e.velY);
        }
    }

    public void removeDynamicEntities() {
        removeFromList(_entities);
        removeFromList(_entitiesToAdd);
    }

    private void removeFromList(ArrayList<Entity> entities) {
        ArrayList<Entity> toRemove = new ArrayList<>();
        for(Entity e : entities) {
            if (e instanceof DynamicEntity) {
                toRemove.add(e);
            }
        }
        for (Entity e : toRemove) {
            entities.remove(e);
        }
    }

    public int numberOfCoins() {
        int count = 0;
        for (Entity e : _entitiesToAdd) {
            if (e instanceof Coin) {
                count++;
            }
        }
        return count;
    }

    private void cleanUp() {
        addAndRemoveEntities();
        for (Entity e : _entities) {
            e.destroy();
        }
        _entities.clear();
        _player = null;
        _pool.empty();
    }

    public void destroy() {
        cleanUp();
    }

}