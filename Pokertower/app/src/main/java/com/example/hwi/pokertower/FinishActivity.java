package com.example.hwi.pokertower;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

import static com.example.hwi.pokertower.GameRun.wave;

class DBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "MyRank.db";
    public static final String SCORES_COLUMN_NAME = "name";
    public static final String SCORES_COLUMN_SCORE = "score";
    public static final String SCORES_COLUMN_WAVE = "wave";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table rankings (_id integer primary key autoincrement, name text, score integer, wave integer);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists rankings");
        onCreate(db);
    }

    public boolean insert(String name, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("score", score);
        contentValues.put("wave", wave);

        db.insert("rankings",null,contentValues);
        return true;
    }

    public ArrayList getAllScores(){
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from rankings order by score desc",null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add("ID : "+res.getString(res.getColumnIndex(SCORES_COLUMN_NAME))+"      SCORE : "+res.getString(res.getColumnIndex(SCORES_COLUMN_SCORE))+"      WAVE : "+res.getString(res.getColumnIndex(SCORES_COLUMN_WAVE)));
            res.moveToNext();
        }
        return array_list;
    }
}
public class FinishActivity extends AppCompatActivity {
    private int currentApiVersion; //네비바 없애기
    private ListView myListView;
    DBHelper mydb;
    ArrayAdapter mAdapter;
    EditText et;
    LinearLayout gameoverlay;
    RelativeLayout scorelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish);
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

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllScores();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        myListView = (ListView) findViewById(R.id.rankingmenu);
        myListView.setAdapter(mAdapter);

        et = (EditText) findViewById(R.id.scorename);
        gameoverlay = (LinearLayout) findViewById(R.id.gameoverlay);
        scorelay = (RelativeLayout) findViewById(R.id.scorelay);

        if (GameRun.clear == true) {
            gameoverlay.setBackgroundResource(R.drawable.clear);
        }
    }

    //폰트 액티비티 전체에 설정해주기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public void insert(View view) {
        String name = et.getText().toString();
        int score = GameRun.score;
        mydb.insert(name, score);
        mAdapter.clear();
        mAdapter.addAll(mydb.getAllScores());
        mAdapter.notifyDataSetChanged();
        et.setText("");

    }

    public void gameover(View view) {
        scorelay.setVisibility(View.VISIBLE);
    }
}
