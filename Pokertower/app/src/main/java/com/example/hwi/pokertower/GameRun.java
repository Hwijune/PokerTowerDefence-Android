package com.example.hwi.pokertower;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static com.example.hwi.pokertower.GameRunActivity.soundflag;
import static com.example.hwi.pokertower.GameRunActivity.v;

public class GameRun extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread m_GameThread;
    int gamespeed = 10; // 스레드시간
    Context mcontex; // 컨텍스트
    static boolean  threadrun = true;

    private CTowerMgr m_cTowerMgr = new CTowerMgr();
    private CMopMgr m_cMopMgr = new CMopMgr();
    //맵 배열
    int map[][] =
            {
                    {10, 11, 10, 10, 10, 10, 2, 1, 6, 10, 11, 10, 10, 10, 10, 11, 10, 10, 2, 1, 6, 10, 10, 10, 10, 11, 10, 10, 10, 10, 10, 10},
                    {10, 3, 4, 4, 4, 4, 15, 1, 6, 10, 10, 10, 10, 10, 10, 10, 10, 10, 2, 1, 12, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 10},
                    {10, 2, 1, 1, 1, 1, 1, 1, 6, 10, 10, 10, 11, 10, 10, 10, 11, 10, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6, 11},
                    {10, 2, 1, 13, 8, 8, 8, 8, 7, 10, 3, 4, 4, 4, 4, 4, 5, 10, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 14, 1, 6, 10},
                    {10, 2, 1, 6, 10, 10, 10, 10, 10, 10, 2, 1, 1, 1, 1, 1, 6, 11, 10, 10, 10, 10, 10, 10, 10, 11, 10, 10, 2, 1, 6, 11},
                    {10, 2, 1, 6, 10, 11, 10, 3, 4, 4, 15, 1, 13, 8, 14, 1, 6, 10, 10, 11, 10, 10, 10, 10, 10, 3, 4, 4, 15, 1, 6, 10},
                    {11, 2, 1, 6, 10, 10, 11, 2, 1, 1, 1, 1, 6, 10, 2, 1, 6, 10, 10, 10, 10, 11, 10, 10, 10, 2, 1, 1, 1, 1, 6, 10},
                    {10, 2, 1, 12, 4, 4, 4, 15, 1, 13, 8, 8, 7, 10, 2, 1, 6, 10, 10, 10, 10, 10, 10, 10, 10, 2, 1, 13, 8, 8, 7, 10},
                    {10, 2, 1, 1, 1, 1, 1, 1, 1, 6, 10, 10, 10, 10, 2, 1, 12, 4, 4, 4, 4, 4, 4, 4, 4, 15, 1, 6, 10, 10, 10, 10},
                    {10, 9, 8, 8, 8, 8, 8, 8, 8, 7, 10, 10, 10, 11, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6, 10, 11, 10, 10},
                    {10, 11, 10, 10, 10, 10, 11, 10, 10, 10, 10, 10, 10, 10, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 10, 10, 10, 10},
                    {10, 10, 10, 11, 10, 10, 10, 10, 10, 10, 10, 10, 11, 10, 10, 10, 10, 10, 10, 10, 10, 11, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
            };


    private Bitmap[] tile = new Bitmap[15]; // 타일 15종류
    private Bitmap menubar; //메뉴바
    private Bitmap statusbar; //상태바
    private Bitmap[] trees = new Bitmap[3];
    private Bitmap testmop[] = new Bitmap[6];
    private Bitmap wave2mop[] = new Bitmap[6];
    private Bitmap wave3mop[] = new Bitmap[6];
    private Bitmap missile1;
    private Bitmap missile2;
    private Bitmap missile3;
    boolean mopstart = false;

    public static boolean clear = false;
    public static int score = 0;
    public int time = 25;
    public static int wave = 1;
    private int life = 10;
    public static int money = 450;
    private int gmax;

    AudioAttributes audio; //오디오어트리뷰트 > 사운드풀(사운드아이디)
    SoundPool soundpool;
    int soundId;


    class CMop {
        public int m_nX;
        public int m_nY;
        public long m_lBeforTime;        // 이전에 움직였던 밀리세컨드
        public long m_nSleep;            // 이전과 다음이동사이의 밀리세컨드
        public int m_nSpeed;            // 한 이동 단위에 움직일 pixel 숫자
        public int m_nMoveArea;        // 이동할 구역
        public int m_nMopHealth;        // 몹체력
        public boolean m_bUsed;            // 생성되었나
        public boolean m_bDie;            // 몹이 죽었나 - 재생되지 않음
        public int m_nDirection;        // 이전 몹의 방향
        public boolean hit = false;

        // x, y, 방향  동1 남2 서3 북4
        int moprange = 50; // x,y좌표 대비해서 좌표 빼기
        public int m_aMovePos[][] =
                {
                        {600 - moprange, 240 - moprange, 2},    // 시작점임 남쪽 으로
                        {600 - moprange, 430 - moprange, 3},    // 서쪽 으로
                        {215 - moprange, 430 - moprange, 2},    // 남쪽 으로
                        {215 - moprange, 915 - moprange, 1},    // 동쪽 으로
                        {675 - moprange, 915 - moprange, 4},    // 북쪽 으로
                        {675 - moprange, 765 - moprange, 1},    // 동쪽 으로
                        {935 - moprange, 765 - moprange, 4},    // 북쪽 으로
                        {935 - moprange, 615 - moprange, 1},    // 동쪽 으로
                        {1235 - moprange, 615 - moprange, 2},    // 남쪽 으로
                        {1235 - moprange, 995 - moprange, 1},    // 동쪽 으로
                        {2125 - moprange, 995 - moprange, 4},    // 북쪽 으로
                        {2200 - moprange, 775 - moprange, 1},   //동쪽으로
                        {2350 - moprange, 775 - moprange, 4},    // 북쪽 으로
                        {2350 - moprange, 425 - moprange, 3},       // 서쪽 으로
                        {1575 - moprange, 425 - moprange, 4},        // 북쪽으로
                        {1575 - moprange, 240 - moprange, 0}     //종착지
                };

        CMop() {
            m_nX = 600 - moprange;
            m_nY = 240 - moprange;
            m_lBeforTime = System.currentTimeMillis();    // 현재 시간을 설정한다
            m_nSleep = 10;    // 10 밀리세컨드마다 움직인다.
            m_nSpeed = 6;    // 2pixel 씩 움직인다
            m_nMoveArea = 1;    // 처음 시작은 위 중간즈음이다
            m_bUsed = false;// 생성되지 않은 상태임
            m_nMopHealth = 100;    // 몹체력은 100으로 초기화
            m_bDie = false;// 몹은 살아있는걸로 초기화됨
            m_nDirection = -1;
        }

        // 다음 포지션으로 이동
        public void MovePosition() {
            // 이전 움직인 시간 이후 m_nSleep 이상 흘렀다면 움직인다.
            if ((System.currentTimeMillis() - m_lBeforTime) > m_nSleep) {
                m_lBeforTime = System.currentTimeMillis();
            } else {
                return;
            }

            // 방향 전환 인가
            int nDir = m_aMovePos[m_nMoveArea - 1][2];
            if (m_nDirection != nDir) {
                m_nDirection = nDir;
            }

            // 마지막 위치면 몹에대해 특별처리함
            if (m_nMoveArea > 15) {
                m_nDirection = -1;
                m_nX = 600 - moprange;
                m_nY = 240 - moprange;
                m_cMopMgr.m_nOverCount += 1;    // 오버 카운트를 1 증가 시킨다
                life--; //목숨 한개씩 줄인다
                m_nMoveArea = 1;
                return;
            }

            // 방향에 따라 X,Y 좌표를 각각 이동 시킨다
            if (nDir == 1) {
                m_nX += m_nSpeed;
                if (m_aMovePos[m_nMoveArea][0] < m_nX) m_nMoveArea++;
            } else if (nDir == 2) {
                m_nY += m_nSpeed;
                if (m_aMovePos[m_nMoveArea][1] < m_nY) m_nMoveArea++;
            } else if (nDir == 3) {
                m_nX -= m_nSpeed;
                if (m_aMovePos[m_nMoveArea][0] > m_nX) m_nMoveArea++;
            } else if (nDir == 4) {
                m_nY -= m_nSpeed;
                if (m_aMovePos[m_nMoveArea][1] > m_nY) m_nMoveArea++;
            }
        }
    }

    class CMopMgr {
        public static final int m_nMopCnt = 60;                    // 몹 개수
        public CMop mop[] = new CMop[m_nMopCnt];    // 몹 Count
        public int m_nUsedMopCnt = 0;                    // 생성되었던  몹 Count
        public int m_nDieMopCnt = 0;                    // 죽은 몹 count
        public long m_lRegen = 1500;                    // 다음몹 생성되는 시간(1초)
        public int m_nOverCount = 0;                    // 몹이 한바퀴 도달하면 1 증가
        public long m_lBeforRegen = System.currentTimeMillis();
        public int wavemop = 20;

        CMopMgr() {
            for (int n = 0; n < m_nMopCnt; n++) {
                mop[n] = new CMop();
                //웨이브 당 체력 +100
                if(n>=20 && n<40)
                    mop[n].m_nMopHealth+=100;
                else if(n>=40 && n<60)
                    mop[n].m_nMopHealth+=200;
            }
        }

        // 신규몹 추가 - m_bStart 플래그만 true 로 설정
        public void AddMop() {
            // 리젠 시간에 도달하면 몹 추가를 시도함
            if(m_nUsedMopCnt<wavemop) {
                if ((System.currentTimeMillis() - m_lBeforRegen) > m_lRegen) {
                    m_lBeforRegen = System.currentTimeMillis();
                } else return;

                if (m_nUsedMopCnt >= (m_nMopCnt - 1)) return;
                mop[m_nUsedMopCnt].m_bUsed = true;

                m_nUsedMopCnt++;
            }
            //시간다되면 웨이브 +20해주고 시간되돌리기
            if(time==0)
            {
                wavemop+=20;
                wave+=1;
                time=25;
            }
        }

        // 이동 가능한 모든 몹들을 이동시킨다
        public void MoveMop() {
            for (int n = 0; n < m_nMopCnt; n++) {
                if (mop[n].m_bUsed == true) {
                    mop[n].MovePosition();
                }
            }
        }

        public int FindMop(int nTowerIndex) {
            for (int n = 0; n < m_nMopCnt; n++) {

                // 죽었거나 생성되지 않은 몹이면 continue..
                if (mop[n].m_bUsed == false) {
                    continue;
                }

                if (mop[n].m_bDie == true) {
                    continue;
                }

                int nX = m_cTowerMgr.tower[nTowerIndex].m_nX - mop[n].m_nX;
                int nY = m_cTowerMgr.tower[nTowerIndex].m_nY - mop[n].m_nY;

                int nDistance = nX * nX + nY * nY;
                if (nDistance < 50000) return n;
            }
            return -1;
        }
    }

    class CTower {
        public int rename=0;
        public int m_nX = 0;        // 타워 X좌표
        public int m_nY = 0;        // 타워 Y좌표
        public int towerpower = 0;
        public int m_nMopIndex;                // 공격 몹의 Index
        public long m_lAttackTime = 0;        // 공격시간
        public long m_lAttackSleep = 1500;     // 공격 간격 1.5초
        public boolean m_bUsed = false;    // 사용되는 타워여부

        public boolean m_bUsedMissale = false;    // 미사일이 발사 되었나
        public int m_nMissalePos = 0;        // 미사일 진행 거리(0~5)
        public long m_lMissaleSleep = 8;        // 미사일 1틱 간격(0.2초)
        public long m_lMissaleTime = 0;        // 이전 미사일 진행했던 시간

        public int m_nMissaleX = 0;        // 미사일 X좌표
        public int m_nMissaleY = 0;        // 미사일 Y좌표

        void AttackMop(int nMopIndex) {
            // 사용중인 타워가 아니라면 return;
            if (m_bUsed == false) return;

            // 공격 간격이 안되었다면 return;
            if ((System.currentTimeMillis() - m_lAttackTime) > m_lAttackSleep) {
                m_lAttackTime = System.currentTimeMillis();
            } else return;

            // 미사일이 발사된 상태면 return
            if (m_bUsedMissale == true) return;

            // 미사일 발사
            m_bUsedMissale = true;

            // 타겟몹 설정
            m_nMopIndex = nMopIndex;
        }

        void MoveMissale() {
            // 사용중인 타워가 아니면 return
            if (m_bUsed == false) return;

            // 미사일이 발사되지 않았으면 return
            if (m_bUsedMissale == false) return;

            // 미사일 진행 간격이 아니면 return
            if ((System.currentTimeMillis() - m_lMissaleTime) > m_lMissaleSleep) {
                m_lMissaleTime = System.currentTimeMillis();
            } else return;

            // 미사일에 맞았다
            if (m_nMissalePos > 6) {

                // 미사일은 사용되지 않은 상태다
                m_nMissalePos = 0;
                m_bUsedMissale = false;

                // 이미 죽어있다.
                if (m_cMopMgr.mop[m_nMopIndex].m_nMopHealth < 0) {
                    return;
                }
                m_cMopMgr.mop[m_nMopIndex].m_nMopHealth -= towerpower; //타워 공격력만큼 체력 줄이기
                m_cMopMgr.mop[m_nMopIndex].hit = true; //몹이 맞았다.
                if (m_cMopMgr.mop[m_nMopIndex].m_nMopHealth <= 0) {
                    // 몹은 죽었다
                    m_cMopMgr.mop[m_nMopIndex].m_bUsed = false;
                    m_cMopMgr.m_nDieMopCnt += 1;
                    score += 10; //죽을때마다 점수가 오른다
                    money += 10; //죽을때마다 돈이 오른다
                }
                return;
            }

            int nMopX = m_cMopMgr.mop[m_nMopIndex].m_nX;
            int nMopY = m_cMopMgr.mop[m_nMopIndex].m_nY;

            // 몹과의 거리
            int nWidth = m_nX - nMopX;
            int nHeight = m_nY - nMopY;

            // 현재 타워에서 몹까지의 비율을 나눠서 구해 더한다
            m_nMissaleX = m_nX - (nWidth / (8 - m_nMissalePos));
            m_nMissaleY = m_nY - (nHeight / (8 - m_nMissalePos));

            m_nMissalePos++;
        }
    }

    class CTowerMgr {
        public CTower tower[] = new CTower[20];  // 타워 객체
        public int m_nUsedTowerCnt = 0;                        // 사용되는 타워 Count

        CTowerMgr() {
            for (int n = 0; n < 20; n++) {
                tower[n] = new CTower();
            }
        }

        // 신규타워 추가 - m_bUsed 플래그만 true로 설정
        public void AddTower(int x, int y, int max, int towerpower,int name) {
            if (m_nUsedTowerCnt < max) {
                if (m_nUsedTowerCnt >= 20) return;
                tower[m_nUsedTowerCnt].rename = name;
                tower[m_nUsedTowerCnt].m_bUsed = true;
                tower[m_nUsedTowerCnt].m_nX = x;
                tower[m_nUsedTowerCnt].m_nY = y;
                tower[m_nUsedTowerCnt].towerpower = towerpower;
                m_nUsedTowerCnt++;
            }
        }

        public int GetTowerCount() {
            return m_nUsedTowerCnt;
        }
    }


    public GameRun(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mcontex = context;

        m_GameThread = new GameThread(holder, context);
        //타격음 오디오어트리뷰트사용용
        audio = new AudioAttributes.Builder()
                . setContentType ( AudioAttributes . CONTENT_TYPE_MUSIC )
                . setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                . setUsage(AudioAttributes.USAGE_GAME)
                .build();
        soundpool = new SoundPool.Builder().setAudioAttributes(audio).setMaxStreams(20).build(); // 소리중첩갯수 타워가 최대 20개까지임
        soundId = soundpool.load(context, R.raw.hiiteffect, 1);


        //테스트미사일
        missile1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.testmissale);
        missile2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bom1);
        missile3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bom2);

        //괴물 wave1
        testmop[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.monmove1);
        testmop[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.monmove2);
        testmop[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.monmove3);
        testmop[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.monmove4);
        testmop[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.monmove5);
        testmop[5] = BitmapFactory.decodeResource(context.getResources(),R.drawable.monhit);

        //괴물 wave2
        wave2mop[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon2move1);
        wave2mop[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon2move2);
        wave2mop[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon2move3);
        wave2mop[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon2move4);
        wave2mop[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon2move5);
        wave2mop[5] = BitmapFactory.decodeResource(context.getResources(),R.drawable.mon2hit);

        //괴물 wave3
        wave3mop[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon3move1);
        wave3mop[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon3move2);
        wave3mop[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon3move3);
        wave3mop[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon3move4);
        wave3mop[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.mon3move5);
        wave3mop[5] = BitmapFactory.decodeResource(context.getResources(),R.drawable.mon3hit);

        //타일 15종류 넣기
        tile[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile00);
        tile[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile01);
        tile[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile02);
        tile[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile03);
        tile[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile04);
        tile[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile05);
        tile[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile06);
        tile[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile07);
        tile[8] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile08);
        tile[9] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile09);
        tile[10] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile10);
        tile[11] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile11);
        tile[12] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile12);
        tile[13] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile13);
        tile[14] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile14);

        //나무 배열
        for (int i = 0; i < trees.length; i++) {
            trees[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree1 + i);
        }

        //타일 사이즈 조절
        for (int i = 0; i < 15; i++) {
            tile[i] = Bitmap.createScaledBitmap(tile[i], 80, 80, true);
        }

        //메뉴바 상태바 넣기
        menubar = BitmapFactory.decodeResource(context.getResources(), R.drawable.menubar);
        statusbar = BitmapFactory.decodeResource(context.getResources(), R.drawable.statusbar);
        menubar = Bitmap.createScaledBitmap(menubar, 2560, 240, true);
        statusbar = Bitmap.createScaledBitmap(statusbar, 2560, 240, true);

    }

    class GameThread extends Thread {
        private SurfaceHolder m_SurfaceHolder;
        //괴물 동작 카운트
        int moncount = 0;
        //스레드 전체시간
        int montime = 0;
        int hitcount = 0; //타격 카운트세서 스레드 n번 회전동안만 보여주기

        public GameThread(SurfaceHolder surfaceholder, Context context) {
            m_SurfaceHolder = surfaceholder;
        }

        public void drawMap1(Canvas canvas) {
            //메뉴바 상태바 그리기
            canvas.drawBitmap(menubar, 0, 0, null);
            canvas.drawBitmap(statusbar, 0, 1200, null);

            //맵 그리기
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    switch (map[i][j]) {
                        case 1:
                            canvas.drawBitmap(tile[0], 80 * j, 80 * i + 240, null);
                            break;
                        case 2:
                            canvas.drawBitmap(tile[1], 80 * j, 80 * i + 240, null);
                            break;
                        case 3:
                            canvas.drawBitmap(tile[2], 80 * j, 80 * i + 240, null);
                            break;
                        case 4:
                            canvas.drawBitmap(tile[3], 80 * j, 80 * i + 240, null);
                            break;
                        case 5:
                            canvas.drawBitmap(tile[4], 80 * j, 80 * i + 240, null);
                            break;
                        case 6:
                            canvas.drawBitmap(tile[5], 80 * j, 80 * i + 240, null);
                            break;
                        case 7:
                            canvas.drawBitmap(tile[6], 80 * j, 80 * i + 240, null);
                            break;
                        case 8:
                            canvas.drawBitmap(tile[7], 80 * j, 80 * i + 240, null);
                            break;
                        case 9:
                            canvas.drawBitmap(tile[8], 80 * j, 80 * i + 240, null);
                            break;
                        case 10:
                            canvas.drawBitmap(tile[9], 80 * j, 80 * i + 240, null);
                            break;
                        case 11:
                            canvas.drawBitmap(tile[10], 80 * j, 80 * i + 240, null);
                            break;
                        case 12:
                            canvas.drawBitmap(tile[11], 80 * j, 80 * i + 240, null);
                            break;
                        case 13:
                            canvas.drawBitmap(tile[12], 80 * j, 80 * i + 240, null);
                            break;
                        case 14:
                            canvas.drawBitmap(tile[13], 80 * j, 80 * i + 240, null);
                            break;
                        case 15:
                            canvas.drawBitmap(tile[14], 80 * j, 80 * i + 240, null);
                            break;
                    }
                }
            }

            //나무 그리기
            canvas.drawBitmap(trees[0], 30, 215, null);
            canvas.drawBitmap(trees[0], 750, 270, null);
            canvas.drawBitmap(trees[0], 1980, 230, null);
            canvas.drawBitmap(trees[0], 1040, 1040, null);
            canvas.drawBitmap(trees[1], 350, 560, null);
            canvas.drawBitmap(trees[1], 850, 920, null);
            canvas.drawBitmap(trees[1], 1730, 620, null);
            canvas.drawBitmap(trees[2], 2300, 860, null);
            canvas.drawBitmap(trees[2], 1600, 550, null);
            canvas.drawBitmap(trees[2], 1400, 750, null);
            canvas.drawBitmap(trees[2], 850, 270, null);
            canvas.drawBitmap(trees[2], 300, 1000, null);
        } //맵그리기
        public void drawTower(Canvas canvas){
            //                        노 원 투 트리플 포커 - 폭탄
//                        로얄스트레이트프러쉬 스트레이트플러쉬 - 빨강
//                        스트레이트 플러쉬 풀하우스 - 분홍
            for (int n = 0; n < 20; n++) {
                if (m_cTowerMgr.tower[n].m_bUsed == true && mopstart) { //타워있는지 && 몬스터가 움직이는 중인지
                    if (m_cTowerMgr.tower[n].m_bUsedMissale) {  //미사일그리기
                        switch (m_cTowerMgr.tower[n].rename) { //타워별로 미사일 다르게하기
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 8:
                                canvas.drawBitmap(missile1, m_cTowerMgr.tower[n].m_nMissaleX, m_cTowerMgr.tower[n].m_nMissaleY, null);
                                break;
                            case 9:
                            case 10:
                                canvas.drawBitmap(missile3, m_cTowerMgr.tower[n].m_nMissaleX, m_cTowerMgr.tower[n].m_nMissaleY, null);
                                break;
                            case 5:
                            case 6:
                            case 7:
                                canvas.drawBitmap(missile2, m_cTowerMgr.tower[n].m_nMissaleX, m_cTowerMgr.tower[n].m_nMissaleY, null);
                                break;
                        }
//                                    paint.setColor(Color.YELLOW);
//                                    canvas.drawRect( m_cTowerMgr.tower[n].m_nX, m_cTowerMgr.tower[n].m_nY, m_cTowerMgr.tower[n].m_nX + 120, m_cTowerMgr.tower[n].m_nY + 120, paint);
                    } else {   // 근처의 몹을 검색하여 공격한다
                        int nMop = m_cMopMgr.FindMop(n);
                        if (nMop >= 0) {
                            m_cTowerMgr.tower[n].AttackMop(nMop);
                        }
                    }
                    m_cTowerMgr.tower[n].MoveMissale();
                }
            }
        } //타워관련그리기
        public void drawMop(Canvas canvas) {
            //스레드 시간 재서 몹 동작바꾸기
            if(mopstart) {
                if (montime % 50 == 0) {
                    moncount++;
                }
                if (moncount == 5) {
                    moncount = 0;
                }
            }
            Paint paint=new Paint();
            // 몹을 그린다
            for (int n = 0; n < m_cMopMgr.m_nMopCnt; n++) {
                if (m_cMopMgr.mop[n].m_bUsed) {
                    //맞았을 때 hit로 이미지 바꾸기
                    if(m_cMopMgr.mop[n].hit) {
                        if(hitcount == 0) {
                            if(soundflag){ //사운드 onoff에 따른 소리와 진동유무
                            int streamId = soundpool.play(soundId, 0.6F, 0.6F, 1, 0, 1.0F); //soundId, leftVolum, rightVolum, priority, loop(0이면반복x,-1무한반복), rate(재생속도) 타격음설정
                            v.vibrate(100);
                            }
                        }
                        //0~20 웨이브1 21~40 웨이브2 41~60 웨이브3
                        if(n<20) {
                            canvas.drawBitmap(testmop[5], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                        else if(n<40)
                        {
                            canvas.drawBitmap(wave2mop[5], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                        else
                        {
                            canvas.drawBitmap(wave3mop[5], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                        hitcount++;
                        if(hitcount==10) //스레드 속도가 20 0.2초동안 맞은이미지로
                        {
                            m_cMopMgr.mop[n].hit = false;
                            hitcount=0;
                        }
                    }
                    else
                    {
                        if(n<20) {
                            canvas.drawBitmap(testmop[moncount], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                        else if(n<40)
                        {
                            canvas.drawBitmap(wave2mop[moncount], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                        else
                        {
                            canvas.drawBitmap(wave3mop[moncount], m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY, null);
                        }
                    }

                    //체력바 그리기기
                    paint.setColor(Color.RED);
                    canvas.drawRect(m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY - 15, m_cMopMgr.mop[n].m_nX + 100, m_cMopMgr.mop[n].m_nY - 35, paint);
                    paint.setColor(Color.GREEN);
                    if(n<20)
                        canvas.drawRect(m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY - 15, m_cMopMgr.mop[n].m_nX + m_cMopMgr.mop[n].m_nMopHealth, m_cMopMgr.mop[n].m_nY - 35, paint);
                    else if(n<40)
                        canvas.drawRect(m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY - 15, m_cMopMgr.mop[n].m_nX + (m_cMopMgr.mop[n].m_nMopHealth)*1/2, m_cMopMgr.mop[n].m_nY - 35, paint);
                    else
                        canvas.drawRect(m_cMopMgr.mop[n].m_nX, m_cMopMgr.mop[n].m_nY - 15, m_cMopMgr.mop[n].m_nX + (m_cMopMgr.mop[n].m_nMopHealth)*1/3, m_cMopMgr.mop[n].m_nY - 35, paint);
                }
            }
        } //몹관련 그리기
        public void drawInfo(Canvas canvas){
            Paint paint = new Paint();
            paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/PressStart2P.ttf"));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);  //화면 캔버스를 하얀색으로 표현한다.
            paint.setTextSize(55);
            canvas.drawText("SCORE=" + score, 110, 150, paint);
            canvas.drawText(" TIME=" + time, 545, 150, paint);
            canvas.drawText(" WAVE=" + wave, 1005, 150, paint);
            canvas.drawText(" LIFE=" + life, 1410, 150, paint);
            canvas.drawText(" MONEY=" + money, 1875, 150, paint);

            //게임오버 뜨고 1.5초 뒤 종료
            if(life <= 0) {
                threadrun = false;
                ((GameRunActivity) mcontex).gameover();
            }

            //클리어
            if(m_cMopMgr.m_nDieMopCnt == 60){
                threadrun = false;
                ((GameRunActivity) mcontex).gameover();
                clear = true;
            }
        } //상황정보그리기
        public void mainValue() {
            //타워가 지어졌을 때 값들 받아오기
            if (((GameRunActivity) mcontex).Toweron()) {
                int MX = ((GameRunActivity) mcontex).gettowerx();
                int MY = ((GameRunActivity) mcontex).gettowery();
                int towerpower = ((GameRunActivity) mcontex).getTowerPower();
                int rename = ((GameRunActivity) mcontex).getResultname();
                m_cTowerMgr.AddTower(MX, MY, gmax, towerpower,rename);
            }
            if (montime % 1000 == 0) {
                mopstart = ((GameRunActivity) mcontex).startflag();
                gmax = ((GameRunActivity) mcontex).getMaxtower();
            }
            if (mopstart) {    //스타트버튼이 눌려져 있으면
                m_cMopMgr.MoveMop();
                m_cMopMgr.AddMop();
                if(montime%500 ==0){
                    time--;
                }
            }
        } //메인액티비티에서 가져오는 값


        public void run() {
            while (threadrun) {
                Canvas canvas = null;
                try {
                    canvas = m_SurfaceHolder.lockCanvas(null);
                    synchronized (m_SurfaceHolder) {
                        montime += gamespeed; // montime으로 스레드 전체시간 세기

                        //gamespeed가 10이기때문에 1초뒤에 canvas그리기
                        if(montime>=500) {
                            mainValue(); // 메인액티비티에서 서피스뷰로 값받아오기
                            drawMap1(canvas); //맵그리기
                            drawTower(canvas); //타워그리기
                            drawMop(canvas); //몹그리기
                            drawInfo(canvas); //정보그리기
                        }
                        sleep(20);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        m_SurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

        m_GameThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        //이미지 환원
        for(int i=0; i<tile.length; i++)
        {
            tile[i].recycle();
            tile[i]=null;
        }
        menubar.recycle();
        menubar=null;
        statusbar.recycle();
        statusbar=null;
        for(int i=0; i<trees.length;i++)
        {
            trees[i].recycle();
            trees[i]=null;
        }
        destroyDrawingCache();
        for(int i=0;i<testmop.length;i++)
        {
            testmop[i].recycle();
            testmop[i]=null;
        }
        missile1.recycle();
        missile1=null;
        missile2.recycle();
        missile2=null;
        missile3.recycle();
        missile3=null;
    }
}