package com.example.santa;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    MediaPlayer buttonClickEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        buttonClickEffect = MediaPlayer.create(this, R.raw.key_button);
    }

    public void startGame(View view) {
        buttonClickEffect.start();
        buttonClickEffect.setOnCompletionListener(MediaPlayer::stop);
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }
}