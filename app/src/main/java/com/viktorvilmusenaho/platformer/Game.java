package com.viktorvilmusenaho.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.viktorvilmusenaho.platformer.entities.Entity;
import com.viktorvilmusenaho.platformer.input.InputManager;
import com.viktorvilmusenaho.platformer.levels.LevelManager;
import com.viktorvilmusenaho.platformer.levels.Level;
import com.viktorvilmusenaho.platformer.sound.JukeBox;
import com.viktorvilmusenaho.platformer.utils.BitmapPool;
import com.viktorvilmusenaho.platformer.utils.SaveObject;
import com.viktorvilmusenaho.platformer.utils.Serializer;

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    public int STAGE_WIDTH = getResources().getInteger(R.integer.screen_standard_stage_width);
    public int STAGE_HEIGHT = getResources().getInteger(R.integer.screen_standard_stage_height);
    private final float METERS_TO_SHOW_X = getResources().getInteger(R.integer.meters_to_show_x);
    private final float METERS_TO_SHOW_Y = getResources().getInteger(R.integer.meters_to_show_y);
    private final String LEVEL_1 = getResources().getString(R.string.level1);
    private final String LEVEL_2 = getResources().getString(R.string.level2);
    private final String LEVEL_3 = getResources().getString(R.string.level3);
    public float TIME_LIMIT_LEVEL_1 = getResources().getInteger(R.integer.level1_time);
    public float TIME_LIMIT_LEVEL_2 = getResources().getInteger(R.integer.level2_time);
    public float TIME_LIMIT_LEVEL_3 = getResources().getInteger(R.integer.level3_time);
    public int CURRENT_LEVEL = 1;
    public float CURRENT_LEVEL_TIME_LIMIT = TIME_LIMIT_LEVEL_1;
    private final int BG_COLOR = getResources().getInteger(R.integer.backgroundColor);
    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;

    private SurfaceHolder _holder = null;
    private final Paint _paint = new Paint();
    private Canvas _canvas = null;
    private final Matrix _transform = new Matrix();
    private Serializer _serializer = null;
    public JukeBox _jukebox = null;

    private boolean _saveDataExists = false;
    private SaveObject _saveData = null;
    private static final Point _position = new Point();
    public LevelManager _level = null;
    private InputManager _controls = new InputManager();
    private HUD _hud = null;
    private Viewport _camera = null;
    public final ArrayList<Entity> _visibleEntities = new ArrayList<>();
    public BitmapPool _pool = null;
    public float _timeLeft = 0;
    private boolean _gameOver = false;

    public Game(final Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        final int TARGET_HEIGHT = 360;
        final int actualHeight = getScreenHeight();
        final float ratio = (TARGET_HEIGHT >= actualHeight ? 1 : (float) TARGET_HEIGHT / actualHeight);
        STAGE_WIDTH = (int) (ratio * getScreenWidth());
        STAGE_HEIGHT = TARGET_HEIGHT;
        Entity._game = this;
        _camera = new Viewport(STAGE_WIDTH, STAGE_HEIGHT, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        _jukebox = new JukeBox(context);
        _pool = new BitmapPool(this);
        loadLevel(1);
        _jukebox.play(JukeBox.BACKGROUND, -1, 3);
        _serializer = new Serializer(this);
        Log.d(TAG, "Game created!");
    }

    private void loadLevel(final int level) {
        _level = new LevelManager(new Level(getContext(), setLevel(level)), _pool);
        _hud = new HUD(this);
        final RectF worldEdges = new RectF(0f, 0f, _level._levelWidth, _level._levelHeight);
        _camera.setBounds(worldEdges);
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _timeLeft = CURRENT_LEVEL_TIME_LIMIT;
    }

    private String setLevel(final int level) {
        switch (level) {
            case 1:
                CURRENT_LEVEL_TIME_LIMIT = TIME_LIMIT_LEVEL_1;
                CURRENT_LEVEL = 1;
                return LEVEL_1;
            case 2:
                CURRENT_LEVEL_TIME_LIMIT = TIME_LIMIT_LEVEL_2;
                CURRENT_LEVEL = 2;
                return LEVEL_2;
            case 3:
                CURRENT_LEVEL_TIME_LIMIT = TIME_LIMIT_LEVEL_3;
                CURRENT_LEVEL = 3;
                return LEVEL_3;
        }
        return LEVEL_1;
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public InputManager getControls() {
        return _controls;
    }

    public void setControls(final InputManager controls) {
        _controls.onPause();
        _controls.onStop();
        _controls = controls;
    }

    public float getWorldHeight() {
        return _level._levelHeight;
    }

    public float getWorldWidth() {
        return _level._levelWidth;
    }

    public int worldToScreenX(float worldDistance) {
        return (int) worldDistance * _camera.getPixelsPerMeterX();
    }

    public int worldToScreenY(float worldDistance) {
        return (int) worldDistance * _camera.getPixelsPerMeterY();
    }

    public float screenToWorldX(float pixelDistance) {
        return (float) pixelDistance / _camera.getPixelsPerMeterX();
    }

    public float screenToWorldY(float pixelDistance) {
        return (float) pixelDistance / _camera.getPixelsPerMeterY();
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        while (_isRunning) {
            final double deltaTime = (System.nanoTime() - lastFrame) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            update(deltaTime);
            buildVisibleSet();
            render(_camera, _visibleEntities);
        }
    }

    private void checkGameOver() {
        if (_timeLeft <= 0 || _level._player._health <= 0) {
            _gameOver = true;
        }
    }

    private void checkLevelCleared() {
        if (_level._player._coinCount == _level._coinCount) {
            CURRENT_LEVEL = (CURRENT_LEVEL == 1 ? 2 : (CURRENT_LEVEL == 2 ? 3 : 1));
            loadLevel(CURRENT_LEVEL);
        }
    }

    private void buildVisibleSet() {
        _visibleEntities.clear();
        for (final Entity e : _level._entities) {
            if (_camera.inView(e)) {
                _visibleEntities.add(e);
            }
        }
    }

    private void update(final double dt) {
        if (_saveDataExists) {
            setupGameData();
        }
        if (_gameOver) {
            gameOverUpdate();
            return;
        }
        _camera.lookAt(_level._player);
        _level.update(dt);
        _timeLeft -= 0.01f;
        checkGameOver();
        checkLevelCleared();
    }

    private void gameOverUpdate() {
        if (_controls._isJumping) { // isJumping == user pressed "A" to restart
            loadLevel(1);
            _gameOver = false;
        }
    }

    private void render(final Viewport camera, final ArrayList<Entity> visibleEntities) {
        if (!acquireAndLockCanvas()) {
            return;
        }
        try {
            _canvas.drawColor(BG_COLOR);
            if(!_gameOver) {
                for (final Entity e : visibleEntities) {
                    _transform.reset();
                    camera.worldToScreen(e, _position);
                    _transform.postTranslate(_position.x, _position.y);
                    e.render(_canvas, _transform, _paint);
                }
            } else {
                _hud.renderGameOver(_canvas, _paint);
            }
            _hud.renderHUD(_canvas, _transform, _paint);
        } finally {
            _holder.unlockCanvasAndPost(_canvas);
        }
    }

    private boolean acquireAndLockCanvas() {
        if (!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }

    private void setupGameData() {
        if (_saveData != null) {
            loadLevel(_saveData._currentLevel);
            _timeLeft = _saveData._currentTimeLeft;
            if (_saveData._entityInfo != null) {
                _level.removeDynamicEntities();
                _level.addUpdatedDynamicEntities(_saveData._entityInfo);
                _level.addAndRemoveEntities();
                _hud._player = _level._player;
                _hud._coinCount = _level._coinCount;
                _level._player._health = _saveData._playerHealth;
                _level._player._coinCount = _saveData._coinCount;
            }
        }
        _saveDataExists = false;
    }

//    Below here is executing on UI thread

    protected void onStart() {
        Log.d(TAG, "onStart");
        _serializer.load();
        SaveObject data =  _serializer._gameData;
        if (data != null) {
            _saveDataExists = true;
            _saveData = data;
        }
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _controls.onResume();
        _jukebox.onResume();
        _gameThread = new Thread(this);
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        _controls.onPause();
        _jukebox.onPause();
        while (true) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    protected void onStop() {
        Log.d(TAG, "onStop");
        _serializer.save();
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;

        if (_level != null) {
            _level.destroy();
            _level = null;
        }
        _controls = null;
        _jukebox.destroy();
        Entity._game = null;
        if (_pool != null) {
            _pool.empty(); // safe but redundant, the LevelManager empties the pool as well.
        }
        _holder.removeCallback(this);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated!");
    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int format, final int width, final int height) {
        Log.d(TAG, "surfaceChanged!");
        Log.d(TAG, "\t Width: " + width + " Height: " + height);
        if (_gameThread != null && _isRunning) {
            Log.d(TAG, "GameThread started!");
            _gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed!");
    }
}