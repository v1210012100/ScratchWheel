package com.example.zangdonglai.scratchwheel;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
    ScratchWheel scratchWheel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        scratchWheel= (ScratchWheel) findViewById(R.id.scratchWheel);
        scratchWheel.setRotateState(true);

    }


}
