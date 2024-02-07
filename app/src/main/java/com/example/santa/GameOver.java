package com.example.santa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    TextView thHighest;
    SharedPreferences sharedPreferences;
    ImageView ivNewHighest;
    MediaPlayer loseEffect;
    MediaPlayer winEffect;
    MediaPlayer buttonClickEffect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loseEffect = MediaPlayer.create(this, R.raw.lose);
        winEffect = MediaPlayer.create(this, R.raw.win);
        buttonClickEffect = MediaPlayer.create(this, R.raw.key_button);
        setContentView(R.layout.game_over);
        tvPoints = findViewById(R.id.tvPoints);
        thHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText(String.valueOf(points));
        sharedPreferences = getSharedPreferences("my_pref", 0);
        int highest = sharedPreferences.getInt("highest", 0);
        if (points > highest){
            winEffect.start();
            winEffect.setOnCompletionListener(MediaPlayer::stop);
            ivNewHighest.setVisibility(View.VISIBLE);
            highest = points;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest", highest);
            editor.commit();
        }
        else {
            loseEffect.start();
            loseEffect.setOnCompletionListener(MediaPlayer::stop);
        }
        thHighest.setText(String.valueOf(highest));
    }

    public void restart(View view){
        buttonClickEffect.start();
        buttonClickEffect.setOnCompletionListener(MediaPlayer::stop);
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view){
        buttonClickEffect.start();
        buttonClickEffect.setOnCompletionListener(MediaPlayer::stop);
        finish();
    }
}
