package com.example.hwi.pokertower;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

public class GameRunActivity extends AppCompatActivity {

    private int currentApiVersion; //네비바 없애기
    public static Vibrator v;
    int touchx; //파랑블록의 x,y좌표
    int touchy;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 음악 서비스 시작하기
        intent = new Intent(getApplicationContext(),//현재제어권자
                MusicService.class); // 이동할 컴포넌트
        startService(intent);

        //가로화면
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //네비게이션바 없애기
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    //폰트 액티비티 전체에 설정해주기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    ////////// 이벤트
    int card[] = new int[8]; //입력받을 카드
    pokerSet pokerset = new pokerSet(); // 포커패 클래스
    boolean start = false;
    int TowerX = 0;
    int TowerY = 0;
    int maxtower = 0;
    boolean toweron = false;
    int towerPower[] = new int[20]; //타워공격력
    String resultname[] = new String[20];
    static boolean soundflag = true; //사운드플래그
    ArrayList<ImageButton> towerarr = new ArrayList<ImageButton>();

    /////////////////버튼
    //인포레이아웃닫기
    public void closeInfo(View view){
        GridLayout infolay = (GridLayout)findViewById(R.id.infolay); //게임정보레이아웃
        infolay.setVisibility(View.INVISIBLE);
    }
    //카드 값 받아서 이미지뷰에 이미지 바꾸기
    public void cardsetting() {
        card = pokerset.pokerdraw();

        for (int i = 0; i < 7; i++) {
            card[i] = getResources().getIdentifier("c" + card[i], "drawable", getPackageName());
        }

        ImageView card1 = (ImageView) findViewById(R.id.card1);
        card1.setImageResource(card[0]);
        ImageView card2 = (ImageView) findViewById(R.id.card2);
        card2.setImageResource(card[1]);
        ImageView card3 = (ImageView) findViewById(R.id.card3);
        card3.setImageResource(card[2]);
        ImageView card4 = (ImageView) findViewById(R.id.card4);
        card4.setImageResource(card[3]);
        ImageView card5 = (ImageView) findViewById(R.id.card5);
        card5.setImageResource(card[4]);
        ImageView card6 = (ImageView) findViewById(R.id.card6);
        card6.setImageResource(card[5]);
        ImageView card7 = (ImageView) findViewById(R.id.card7);
        card7.setImageResource(card[6]);

    }

    public void closebutton(View view) {//포커패 그리드
        GridLayout pokergrid = (GridLayout) findViewById(R.id.pokergrid);
        pokergrid.setVisibility(View.GONE);
    }

    public void info(View view){ //인포메이션 열기
        GridLayout infolay = (GridLayout)findViewById(R.id.infolay);
        infolay.setVisibility(View.VISIBLE);
    }

    public void infoclose(View view){ //인포메이션 닫기
        GridLayout infolay = (GridLayout)findViewById(R.id.infolay);
        infolay.setVisibility(View.GONE);
    }

    public void menu(View view){ //메뉴 열기
        LinearLayout menulay = (LinearLayout) findViewById(R.id.menulay);
        menulay.setVisibility(View.VISIBLE);
    }

    public void menuclose(View view){ //메뉴 닫기
        LinearLayout menulay = (LinearLayout) findViewById(R.id.menulay);
        menulay.setVisibility(View.GONE);
    }

    public void mainreturn(View view){ //메인으로 가기
        GameRun.threadrun = false;
        finish();
    }

    public void buildbutton(View view) {
        GridLayout pokergrid = (GridLayout) findViewById(R.id.pokergrid);
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.buttonlayout);
        Button closebutton = (Button) findViewById(R.id.gridclosebutton);

        if (GameRun.money >= 100) {
            pokergrid.setVisibility(View.VISIBLE);
            cardsetting();
            //파이널 해줘야 getx,gety가능
            final ImageButton tower = new ImageButton(this);
            final ImageButton statustowerimg = new ImageButton(this);
            final ImageButton upgradebt = new ImageButton(this);
            switch (card[7]) {
                case 1:
                    resultname[maxtower] = "No pair";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.nopairtower);
                    statustowerimg.setBackgroundResource(R.drawable.nopairtower);
                    towerPower[maxtower] = 20;
                    break;
                case 2:
                    resultname[maxtower] = "One pair";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.onepairtower);
                    statustowerimg.setBackgroundResource(R.drawable.onepairtower);
                    towerPower[maxtower] = 26;
                    break;
                case 3:
                    resultname[maxtower] = "Two pair";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.twopairtower);
                    statustowerimg.setBackgroundResource(R.drawable.twopairtower);
                    towerPower[maxtower] = 32;
                    break;
                case 4:
                    resultname[maxtower] = "Triple";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.threepletower);
                    statustowerimg.setBackgroundResource(R.drawable.threepletower);
                    towerPower[maxtower] = 38;
                    break;
                case 5:
                    resultname[maxtower] = "Straight";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.straighttower);
                    statustowerimg.setBackgroundResource(R.drawable.straighttower);
                    towerPower[maxtower] = 44;
                    break;
                case 6:
                    resultname[maxtower] = "Flush";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.flush);
                    statustowerimg.setBackgroundResource(R.drawable.flush);
                    towerPower[maxtower] = 50;
                    break;
                case 7:
                    resultname[maxtower] = "Full house";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.fullhousetower);
                    statustowerimg.setBackgroundResource(R.drawable.fullhousetower);
                    towerPower[maxtower] = 56;
                    break;
                case 8:
                    resultname[maxtower] = "Four card";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.pokertower);
                    statustowerimg.setBackgroundResource(R.drawable.pokertower);
                    towerPower[maxtower] = 62;
                    break;
                case 9:
                    resultname[maxtower] = "Straight Flush";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.straightflushtower);
                    statustowerimg.setBackgroundResource(R.drawable.straightflushtower);
                    towerPower[maxtower] = 68;
                    break;
                case 10:
                    resultname[maxtower] = "Royal Straight Flush";
                    closebutton.setText(resultname[maxtower]);
                    tower.setBackgroundResource(R.drawable.royal);
                    statustowerimg.setBackgroundResource(R.drawable.royal);
                    towerPower[maxtower] = 74;
                    break;
            }
            //arrlist에 타워추가
            towerarr.add(tower);
            //타워 설명
            tower.setScaleType(ImageView.ScaleType.FIT_XY);
            tower.setMaxWidth(120);
            tower.setMaxHeight(120);
            tower.setX(touchx - 105);
            tower.setY(touchy - 300);
            //상황바 타워 그림 그려주기
            statustowerimg.setX(120);
            statustowerimg.setScaleType(ImageView.ScaleType.FIT_XY);
            statustowerimg.setY(1100);
            statustowerimg.setVisibility(View.INVISIBLE);
            statustowerimg.setMaxHeight(120);
            statustowerimg.setMaxWidth(120);
            //업그레이드 버튼
            upgradebt.setX(350);
            upgradebt.setY(1230);
            upgradebt.setScaleType(ImageView.ScaleType.FIT_XY);
            upgradebt.setMaxWidth(180);
            upgradebt.setMaxHeight(180);
            upgradebt.setVisibility(View.INVISIBLE);
            upgradebt.setBackgroundResource(R.drawable.up);
            toweron = true;
            maxtower++;

            TowerX = (int) tower.getX() + 40;
            TowerY = (int) tower.getY() + 190;

            //타워 업그레이드버튼 비용-100 공격력*1.1
            upgradebt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (GameRun.money > 100) {
                        GameRun.money -= 100;
                        towerPower[maxtower-1] *= 1.1;
                    }
                }
            });
            //클릭해서 범위보기
            tower.setOnClickListener(new View.OnClickListener() {
                ImageView range = (ImageView) findViewById(R.id.range);
                TextView statusinfo = (TextView) findViewById(R.id.statusinfo);
                boolean rangeonoff = true;
                //타워의 인덱스를 확인하기
                int towerindex = towerarr.indexOf(tower);

                public void onClick(View v) {
                    if (rangeonoff) {
                        statustowerimg.setVisibility(View.VISIBLE);
                        statusinfo.setText(resultname[towerindex] + "\nAttack " + towerPower[towerindex] + "\nCost100 A+10%");
                        statusinfo.setVisibility(View.VISIBLE);
                        range.setVisibility(View.VISIBLE);
                        upgradebt.setVisibility(View.VISIBLE);
                        range.setX(tower.getX() - 180);
                        range.setY(tower.getY());
                        rangeonoff = false;
                    } else {
                        upgradebt.setVisibility(View.INVISIBLE);
                        statustowerimg.setVisibility(View.INVISIBLE);
                        statusinfo.setVisibility(View.GONE);
                        range.setVisibility(View.GONE);
                        rangeonoff = true;
                    }
                }
            });
            lay.addView(tower);
            lay.addView(statustowerimg);
            lay.addView(upgradebt);
            GameRun.money -= 100;
        }
    }
    //기본 바닥타일
    public boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();
        touchx = (int) event.getX();
        touchy = (int) event.getY();
        TextView checktile = (TextView) findViewById(R.id.checktile);
        switch (keyAction) {
            case MotionEvent.ACTION_DOWN:
                checktile.setVisibility(View.VISIBLE);
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                checktile.bringToFront(); //타일이 최상위로 오게만들기
                checktile.setX(touchx - 100);
                checktile.setY(touchy - 100);
                break;
        }
        // 함수 override 해서 사용하게 되면  return  값이  super.onTouchEvent(event) 되므로
        // MOVE, UP 관련 이벤트가 연이어 발생하게 할려면 true 를 반환해주어야 한다.
        return true;
    }

    ////////////// 값 넘기기 /////////////////
    public boolean Toweron() {
        return toweron;
    }

    public int getMaxtower() {
        return maxtower;
    }

    public int gettowerx() {
        return TowerX;
    }

    public int gettowery() {
        return TowerY;
    }

    //카드 종류보내주기
    public int getResultname() { return card[7]; }
    //공격력 넘겨주기
    public int getTowerPower() {
        return towerPower[maxtower-1];
    }

    public void startclick(View view) {
        start = true;
    }

    public void pauseclick(View view) {
        start = false;
    }

    public boolean startflag() {
        return start;
    }

    //게임오버 액티비티 종료
    public void gameover(){
        Intent intent = new Intent(GameRunActivity.this, com.example.hwi.pokertower.FinishActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        //음악끄기
        stopService(intent);
        super.onStop();
    }

    public void soundonoff(View view)
    {
        Button soundb = (Button)findViewById(R.id.soundbutton);
        if(soundflag)
        {
            //음악끄기
            stopService(intent);
            super.onStop();
            soundflag = false;
            soundb.setText("SOUND ON");
        }
        else
        {
            startService(intent);
            soundflag = true;
            soundb.setText("SOUND OFF");
        }
    }
}
