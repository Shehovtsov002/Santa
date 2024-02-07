package com.example.santa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Bitmap background, ground, santa;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healtPaint = new Paint();
    float TEXT_SIZE = 120;
    int points = 0;
    int life = 3;
    static int dWidth, dHeight;
    Random random;
    float santaX, santaY;
    float oldX;
    float oldSantaX;
    ArrayList<Spike> spikes;
    ArrayList<Explosion> explosions;
    MediaPlayer mainTheme;
    MediaPlayer explosionSound;
    MediaPlayer laughEffect;

    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        santa = BitmapFactory.decodeResource(getResources(), R.drawable.santa);

        mainTheme = MediaPlayer.create(context, R.raw.main_theme);
        mainTheme.start();

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.kenney_blocks));
        healtPaint.setColor(Color.GREEN);
        random = new Random();

        santaX = dWidth / 2 - santa.getWidth() / 2;
        santaY = dHeight - ground.getHeight() - santa.getHeight();
        spikes = new ArrayList<>();
        explosions = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            Spike spike = new Spike(context);
            spikes.add(spike);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(santa, santaX, santaY, null);

        for (int i = 0; i < spikes.size(); i++){
            canvas.drawBitmap(spikes.get(i).getSpike(spikes.get(i).spikeFrame),
                    spikes.get(i).spikeX, spikes.get(i).spikeY, null);
            spikes.get(i).spikeFrame++;

            if (spikes.get(i).spikeFrame > 2){
                spikes.get(i).spikeFrame = 0;
            }
            spikes.get(i).spikeY += spikes.get(i).spikeVelocity;
            // Grounded
            if (spikes.get(i).spikeY + spikes.get(i).getSpikeHeight() >= dHeight - ground.getHeight()){
                explosionSound = MediaPlayer.create(context, R.raw.explosion);
                explosionSound.start();
                explosionSound.setOnCompletionListener(MediaPlayer::stop);
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = spikes.get(i).spikeX;
                explosion.explosionY = spikes.get(i).spikeY;
                explosions.add(explosion);
                spikes.get(i).resetPosition();
            }
        }

        for (int i = 0; i < spikes.size(); i++){
            // collision
            if (spikes.get(i).spikeX + spikes.get(i).getSpikeWidth() >= santaX &&
                spikes.get(i).spikeX <= santaX + santa.getWidth() &&
                spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() >= santaY &&
                spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() <= santaY + santa.getHeight()){

                laughEffect = MediaPlayer.create(context, R.raw.laugh);
                laughEffect.start();
                laughEffect.setOnCompletionListener(MediaPlayer::stop);

                life--;
                spikes.get(i).resetPosition();
                if (life == 0){
                    mainTheme.stop();
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }

        for (int i = 0; i < explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX,
                    explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 3){
                explosions.remove(i);
            }
        }

        if (life == 2){
            healtPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healtPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healtPaint);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        canvas.drawText("       " + life, 20, TEXT_SIZE, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= santaY){
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldSantaX = santaX;
            }
            if (action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newSantaX = oldSantaX - shift;
                if (newSantaX <= 0)
                    santaX = 0;
                else if (newSantaX >= dWidth - santa.getWidth())
                    santaX = dWidth - santa.getWidth();
                else
                    santaX = newSantaX;
            }
        }
        return true;
    }
}
