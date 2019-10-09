package com.viktorvilmusenaho.platformer.utils;

import com.viktorvilmusenaho.platformer.entities.DynamicEntityInformation;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveObject implements Serializable {

    public int _currentLevel = 1;
    public float _currentTimeLeft = 30;
    public int _coinCount = 0;
    public int _playerHealth = 0;
    public ArrayList<DynamicEntityInformation> _entityInfo = new ArrayList<>();

}
