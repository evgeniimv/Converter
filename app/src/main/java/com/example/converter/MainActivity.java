package com.example.converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private UIController uiController;

    public MainActivity() {
        instance = this;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new UIController();
        uiController.setOnCreateInterface();
    }

    public void onConvertButtonClick(View v) {
        uiController.onConvertClick(v);
    }

    public void onSwitchButtonClick(View v) { uiController.onSwitchClick(v); }


    public static MainActivity getInstance() {
        return instance;
    }
}
