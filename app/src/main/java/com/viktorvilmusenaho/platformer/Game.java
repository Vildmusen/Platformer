package com.viktorvilmusenaho.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
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

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    static int STAGE_WIDTH = 1280;
    static int STAGE_HEIGHT = 720;
    private static final float METERS_TO_SHOW_X = 20f;
    private static final float METERS_TO_SHOW_Y = 0f;
    private final String LEVEL_1 = getResources().getString(R.string.level1);
    private final String LEVEL_2 = getResources().getString(R.string.level2);
    private final String LEVEL_3 = getResources().getString(R.string.level3);
    private String CURRENT_LEVEL = LEVEL_1;
    private static final int BG_COLOR = Color.rgb(135, 200, 240);
    public float TIME_LIMIT = 30;
    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;

    private SurfaceHolder _holder = null;
    private final Paint _paint = new Paint();
    private Canvas _canvas = null;
    private final Matrix _transform = new Matrix();

    public JukeBox _jukebox = null;

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
        loadLevel(LEVEL_1);
        _jukebox.play(JukeBox.BACKGROUND, -1, 3);
        Log.d(TAG, "Game created!");
    }

    private void loadLevel(final String levelName) {
        _level = new LevelManager(new Level(getContext(), levelName), _pool);
        _hud = new HUD(this);
        final RectF worldEdges = new RectF(0f, 0f, _level._levelWidth, _level._levelHeight);
        _camera.setBounds(worldEdges);
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _timeLeft = TIME_LIMIT;
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
        int pixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        return pixels;
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
            CURRENT_LEVEL =
                    (CURRENT_LEVEL.equalsIgnoreCase(LEVEL_1) ? LEVEL_2 :
                            (CURRENT_LEVEL.equalsIgnoreCase(LEVEL_2) ? LEVEL_3 : LEVEL_1));
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
        if (_gameOver) {
            gameOverUpdate();
            return;
        }
        _camera.lookAt(_level._player); // TODO ease it bruh
        _level.update(dt);
        _timeLeft -= 0.01f;
        checkGameOver();
        checkLevelCleared();
    }

    private void gameOverUpdate() {
        if (_controls._isJumping) {
            loadLevel(LEVEL_1);
            _gameOver = false;
        }
    }

    // TODO provide a viewport
    private static final Point _position = new Point();

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

//    Below here is executing on UI thread

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