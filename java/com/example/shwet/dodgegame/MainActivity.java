package com.example.shwet.dodgegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    GameSurface gameSurface;
    SensorManager sensorManager;
    double xxx, y, z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xxx = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    public class GameSurface extends SurfaceView implements Runnable {

        Thread thread;
        SurfaceHolder surfaceHolder;
        Bitmap character, background, enemy,secondImage,state;
        Paint paint;
        Canvas canvas;
        volatile boolean moving = false;
        int firstEnemy, secondEnemy;
        Enemy enemy1,enemy2;
        ConstraintLayout layout;
        Boolean tapped = false;
        Boolean hit=false;
        Boolean firstHit=false,secondHit=false;
        Boolean hit1=false,hit2=false;
        Boolean end = false,play=false;
        int width;
        int height;
        SoundPool soundPool;
        int soundPooll;
        MediaPlayer player;
        int score;
        int seconds;

        public GameSurface(Context context) {
            super(context);
            surfaceHolder=getHolder();
            character = BitmapFactory.decodeResource(getResources(),R.drawable.giants);
            enemy = BitmapFactory.decodeResource(getResources(),R.drawable.browns);
            background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
            secondImage = BitmapFactory.decodeResource(getResources(),R.drawable.obj);
            layout = findViewById(R.id.layout);
            state = BitmapFactory.decodeResource(getResources(),R.drawable.giants);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width=size.x;
            height=size.y;

            paint= new Paint();
            paint.setTextSize(110);
            paint.setColor(Color.RED);

            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC,0);
            soundPooll = soundPool.load(MainActivity.this,R.raw.scream,1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                }
            });
            player = MediaPlayer.create(MainActivity.this,R.raw.music);
            player.start();
            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    seconds =(int) millisUntilFinished/1000;
                }

                public void onFinish() {
                    end = true;
                }
            }.start();

        }

        @Override
        public void run() {
            int value = 300;
            enemy1 = new Enemy();
            enemy2 = new Enemy();
            firstEnemy = -100;
            secondEnemy = -100;
            enemy1.setSpeed(9);
            enemy2.setSpeed(9);
            gameSurface.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    tapped = !tapped;
                    if (tapped) {
                        enemy1.setSpeed(18);
                        enemy2.setSpeed(18);
                    }
                    else {
                        enemy1.setSpeed(9);
                        enemy2.setSpeed(9);
                    }
                    if(end){
                        end=!end;
                        score=0;
                        new CountDownTimer(30000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                seconds =(int) millisUntilFinished/1000;
                            }
                            public void onFinish() {
                                end = true;
                            }
                        }.start();
                        tapped=false;
                    }
                }
            });

            while (moving) {
                double x;
                if (!surfaceHolder.getSurface().isValid()) //forgot the exclamation point
                    continue;
                if (!end) {
                    x = -xxx;
                    if(Math.abs(x)<Math.abs(0.1)){
                        x=0;
                    }
                    value += (3*x);
                    if (firstEnemy==secondEnemy && secondEnemy == -100) {
                        enemy1.setX(enemy1.randomX1());
                        enemy2.setX(enemy2.randomX2());
                    }

                    firstEnemy += enemy1.getSpeed();
                    secondEnemy += enemy2.getSpeed();
                    if (value < -23) {
                        value = -23;
                    }
                    if (value > 650) {
                        value = 650;
                    }
                    Rect person = new Rect(value, 800, value + 100, 950);
                    Rect en1 = new Rect(enemy1.getX(), firstEnemy, enemy1.getX() + 128, firstEnemy + 128);
                    Rect en2 = new Rect(enemy2.getX(), secondEnemy, enemy2.getX() + 128, secondEnemy + 128);
                    if (person.intersect(en1)) {
                        hit = true;
                        hit1 = true;
                        firstHit = true; }
                    if (person.intersect(en2)) {
                        hit = true;
                        hit2 = true;
                        secondHit = true;
                    }
                    if (person.intersect(en1) || person.intersect(en2)) {
                        hit = true;
                    } else {
                        hit1 = false;
                        hit2 = false;
                        hit = false;
                    }
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawRGB(100,100,100);
                    canvas.drawBitmap(background, 0, 0, null);
                    if (hit) {
                        state = secondImage;
                        if(!play){
                            soundPool.play(soundPooll,2,2,0,0,1);
                            play=true;

                        }
                    }
                    canvas.drawText(String.valueOf(seconds -1), 550, 100, paint);
                    canvas.drawBitmap(state, value, 830, null);
                    canvas.drawBitmap(enemy, enemy1.getX(), firstEnemy, null);
                    canvas.drawBitmap(enemy, enemy2.getX(), secondEnemy, null);
                    canvas.drawText(String.valueOf(score), 100, 100, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    if (firstEnemy > 1260) {
                        firstEnemy = -100;
                        secondEnemy = -100;
                        score += 2;
                        if (firstHit) {
                            score -= 1;
                        }
                        if (secondHit) {
                            score -= 1;
                        }
                        firstHit = false;
                        secondHit = false;
                        state = character;
                        play=false;
                    }
                }
                if(end){
                    canvas=surfaceHolder.lockCanvas();
                    canvas.drawRGB(100,100,100);
                    canvas.drawText("Score: "+score,100,595,paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        public void pause() {
            moving = false;
            while (true) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        public void resume(){
            moving=true;
            thread=new Thread(this);
            thread.start();
        }

    }
}