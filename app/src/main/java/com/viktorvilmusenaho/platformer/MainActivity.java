package com.viktorvilmusenaho.platformer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.viktorvilmusenaho.platformer.input.InputManager;
import com.viktorvilmusenaho.platformer.input.TouchController;

public class MainActivity extends AppCompatActivity {

    Game _game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _game = findViewById(R.id.game);
        InputManager control = new TouchController(findViewById(R.id.touchControl));
        _game.setControls(control);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _game.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _game.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _game.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _game.onStop();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

    }
}
