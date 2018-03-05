package com.example.hwi.pokertower;

import android.app.Application;
import com.tsengvn.typekit.Typekit;

/**
 * Created by hwi on 2017-06-05.
 */

public class applicationbase extends Application {
    @Override public void onCreate() {
        super.onCreate();
        // 폰트 정의
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/PressStart2P.ttf"));
    }
}
