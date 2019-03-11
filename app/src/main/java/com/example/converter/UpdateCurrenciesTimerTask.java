package com.example.converter;

import android.os.Handler;
import android.widget.Toast;

import java.util.TimerTask;

import java.util.logging.LogRecord;

import static com.example.converter.UIController.isOnline;

public class UpdateCurrenciesTimerTask extends TimerTask {
    private MainActivity mainActivity;
    private CurrencyAsyncTask currencyAsyncTask;
    final Handler handler = new Handler();

    public UpdateCurrenciesTimerTask() {
        mainActivity = MainActivity.getInstance();
    }

    @Override
    public void run() {
        handler.post(new Runnable() {
            public void run() {

                if (isOnline(mainActivity)) {
                    currencyAsyncTask = new CurrencyAsyncTask();
                    currencyAsyncTask.execute();
                }
            }
        });

    }


}
