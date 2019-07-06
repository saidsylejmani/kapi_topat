package com.example.kapi_topat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    //Frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    //Image
    private ImageView gloves, serbia,kosovo, usa;
    private Drawable imageGloves;

    //Size
    private int glovesSize;

    //Position
    private float glovesX, glovesY;
    private float serbiaX, serbiaY;
    private float usaX, usaY;
    private float kosovoX, kosovoY;

    //Score
    private TextView scoreLabel , highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;


    //Class
    private Timer timer;
    private Handler handler =new Handler();

    //Status
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean kosovo_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        gloves = findViewById(R.id.gloves);
        serbia = findViewById(R.id.serbia);
        usa = findViewById(R.id.usa);
        kosovo = findViewById(R.id.kosovo);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        imageGloves = getResources().getDrawable(R.drawable.gloves);

//High Score(Rezultati me i larte)
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);
    }
    public void changePos() {

        //Add timeCount(Shto numeriesin e kohes)
        timeCount += 20;

        //usa ball
        usaY += 12;

        float usaCenterX = usaX + usa.getWidth() / 2;
        float usaCenterY = usaY + usa.getHeight() / 2;

        if(hitCheck(usaCenterX, usaCenterY)){
            usaY = frameHeight + 100;
            score += 10;
        }

        if(usaY > frameHeight){
            usaY = -100;
            usaX = (float) Math.floor(Math.random() * (frameWidth - usa.getWidth()));
        }
        usa.setX(usaX);
        usa.setY(usaY);

        //kosovo ball
        if(!kosovo_flg && timeCount % 10000 == 0) {
            kosovo_flg = true;
            kosovoY = -20;
            kosovoX = (float) Math.floor(Math.random() * (frameWidth - kosovo.getWidth()));
        }

        if(kosovo_flg){
            kosovoY += 20;

            float kosovoCenterX = kosovoX + kosovo.getWidth() / 2;
            float kosovoCenterY = kosovoY + kosovo.getWidth() / 2;

            if(hitCheck(kosovoCenterX, kosovoCenterY)){
                kosovoY = frameHeight + 30;
                score +=30;
                //Ndrysho FrameWidth(Gjeresine e kornizes)
                if (initialFrameWidth > frameWidth * 110 / 100){
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }

            }

            if (kosovoY > frameHeight) kosovo_flg = false;
            kosovo.setX(kosovoX);
            kosovo.setY(kosovoY);
        }

        //serbia ball
        serbiaY += 18;

        float serbiaCenterX = serbiaX + serbia.getWidth() / 2;
        float serbiaCenterY = serbiaY + serbia.getHeight() / 2;

        if(hitCheck(serbiaCenterX, serbiaCenterY)){
            serbiaY = frameHeight + 100;

            //Ndrysho FrameWidth(Gjeresine e kornizes)
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);

            if (frameWidth <= glovesSize) {
                gameOver();
            }

        }

        if (serbiaY > frameHeight) {
            serbiaY = -100;
            serbiaX = (float) Math.floor(Math.random() * (frameWidth - serbia.getWidth()));
        }

        serbia.setX(serbiaX);
        serbia.setY(serbiaY);

        //Levize dorezen
        if (action_flg) {
            //Touching
            glovesX += 14;
            gloves.setImageDrawable(imageGloves);
        } else {
            //leshimi
            glovesX -= 14;
            gloves.setImageDrawable(imageGloves);
        }

        //kontrollo poziten e dorezes.
        if (glovesX < 0) {
            glovesX = 0;
            gloves.setImageDrawable(imageGloves);
        }
        if (frameWidth - glovesSize < glovesX) {
            glovesX = frameWidth - glovesSize;
            gloves.setImageDrawable(imageGloves);
        }

        gloves.setX(glovesX);

        scoreLabel.setText("Score : " +score);
    }

    public boolean hitCheck(float x, float y){
        if (glovesX <= x && x <= glovesX + glovesSize && glovesY <= y && y <= frameHeight){
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    public void gameOver() {
        //Ndalo timerin,
        timer.cancel();
        timer = null;
        start_flg = false;

        //Para shfaqjes se startLayout , pauzo 1 sekonde
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        gloves.setVisibility(View.INVISIBLE);
        serbia.setVisibility(View.INVISIBLE);
        kosovo.setVisibility(View.INVISIBLE);
        usa.setVisibility(View.INVISIBLE);

        //perditesimi i rezultatit me te mire
        if(score > highScore){
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;

    }

    public void startGame(View view){
        start_flg = true;
        startLayout.setVisibility(view.INVISIBLE);

        if(frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            glovesSize = gloves.getHeight();
            glovesX = gloves.getX();
            glovesY = gloves.getY();
        }

        frameWidth =  initialFrameWidth;

        gloves.setX(0.0f);
        serbia.setY(3000.0f);
        usa.setY(3000.0f);
        kosovo.setY(3000.0f);

        serbiaY = serbia.getY();
        usaY = usa.getY();
        kosovoY = kosovo.getY();

        gloves.setVisibility(view.VISIBLE);
        serbia.setVisibility(view.VISIBLE);
        usa.setVisibility(view.VISIBLE);
        kosovo.setVisibility(view.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0");


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public  void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();


                        }
                    });
                }
            }
        },0 ,20);



    }
    public void quitGame(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }

    } }

