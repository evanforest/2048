package com.my2048.linzg.lin2048;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by asus on 2017/8/4.
 */

public class GameActivity extends Activity {
    private Game2048View gameView = null;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        preferences = getSharedPreferences("gameData", Context.MODE_PRIVATE);
        this.gameView = new Game2048View(this);
        View view = gameView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            layout.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorMyPrimary));
            layout.setFitsSystemWindows(true);
            layout.addView(gameView,new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            view = layout;
        }
        if (savedInstanceState != null && savedInstanceState.getBoolean("isSave",false)){
            load();
        }
        setContentView(view);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isSave",true);
        save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }
    private void load(){
        if (gameView == null)
            return;
        gameView.load(preferences);
    }
    private void save(){
        if (gameView == null)
            return;
        gameView.save(preferences);
    }
}
