package com.horizon.trailer;

import android.os.Bundle;
import android.view.View;

import com.horizon.trailer.view.SlidingMenu;


public class MainActivity extends BaseFragmentActivity {
    SlidingMenu slide_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slide_menu = (SlidingMenu)findViewById(R.id.slide_menu);
    }
    public void toggle(View v){
        slide_menu.toggle();
    }
}
