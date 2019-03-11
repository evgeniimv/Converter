package com.example.converter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UIController {
    private String[] data = {"AUD", "AZN", "GBP", "AMD", "BYN", "BGN", "BRL", "HUF", "HKD", "DKK", "USD", "EUR", "INR",
            "KZT", "CAD", "KGS", "CNY", "MDL", "NOK", "PLN", "RON", "XDR", "SGD", "TJS", "TRY", "TMT",
            "UZS", "UAH", "CZK", "SEK", "CHF", "ZAR", "KRW", "JPY", "RUB"};
    private Spinner spinner1;
    private Spinner spinner2;
    private TextView userPrintedText;
    private TextView availblConvertCoef;
    private Float calculatedCurrency;
    private TextView convertationResult;
    private static ArrayAdapter<String> adapter;
    private FileOperations fileOperations;
    private MainActivity mainActivity;
    private CurrencyAsyncTask currencyAsyncTask;

    public UIController() { mainActivity = MainActivity.getInstance(); }


    public void setOnCreateInterface() {
        mainActivity.setContentView(R.layout.activity_main);
        userPrintedText = (TextView) mainActivity.findViewById(R.id.editText);
        convertationResult = (TextView) mainActivity.findViewById(R.id.textView2);
        availblConvertCoef = (TextView) mainActivity.findViewById(R.id.textView3);
        Arrays.sort(data);
        // подключаем адаптер со списком стран
        adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1 = (Spinner) mainActivity.findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter);
        // заголовок
        spinner1.setPrompt("Список валют");
        // выделяем элемент
        spinner1.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinner2 = (Spinner) mainActivity.findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter);
        // заголовок
        spinner2.setPrompt("Список валют");
        // выделяем элемент
        spinner2.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        fileOperations = new FileOperations();
        fileOperations.setDateFromFile();
        callCurrencyAsyncTask();
    }

    public void onButtonClick(View v) {
        fileOperations = new FileOperations();

        //проверяем содержит ли файл "file.txt" какие-либо данные
        if (fileOperations.readFile().length() == 0) {
            Toast.makeText(mainActivity, "Необходимо обновить " +
                    "курсы валют", Toast.LENGTH_LONG).show();
        } else {
            try {
                Float userPrintedValue = Float.valueOf(userPrintedText.getText().toString());

                //Вычисляем коэффициент преобразования числа "userPrintedValue"
                //введенного пользователем
                JSONObject jsonObject = new JSONObject(fileOperations.readFile());
                String chosenItemSpinner1 = (String)spinner1.getSelectedItem() ;
                String chosenItemSpinner2 = (String)spinner2.getSelectedItem() ;
                calculatedCurrency = CalcCurrencyFromJson(chosenItemSpinner1,chosenItemSpinner2,jsonObject);

                // Выводим соответствующий коэффициент конвертации "calculatedCurrency"
                // и результат конвертации
                availblConvertCoef.setText("Актуальный курс " + chosenItemSpinner1 + " : " +
                        spinner2.getSelectedItem() + " = " + calculatedCurrency);
                convertationResult.setText("" + new BigDecimal(userPrintedValue *
                        calculatedCurrency).setScale(4, RoundingMode.UP).floatValue());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                Toast.makeText(mainActivity, "Введите конвертируемое число", Toast.LENGTH_LONG).show();
            }
        }
    }

    public float CalcCurrencyFromJson(String itemSpinner1, String itemSpinner2, JSONObject jsonObject) throws JSONException {
        JSONObject allValutes = jsonObject.getJSONObject("Valute");
        if(itemSpinner1=="RUB"){
            JSONObject convertTo = allValutes.getJSONObject(itemSpinner2);
            return 1/Float.valueOf(convertTo.getString("Value"));
        }
        if(itemSpinner2=="RUB"){
            JSONObject convertFrom = allValutes.getJSONObject(itemSpinner1);
            return Float.valueOf(convertFrom.getString("Value"));
        }
        JSONObject convertTo = allValutes.getJSONObject(itemSpinner2);
        JSONObject convertFrom = allValutes.getJSONObject(itemSpinner1);
        Float valueTo = Float.valueOf(convertTo.getString("Value"));
        Float valueFrom = Float.valueOf(convertFrom.getString("Value"));
        return valueFrom / valueTo;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void callCurrencyAsyncTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doCurrencyAsynTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (isOnline(mainActivity)) {
                            CurrencyAsyncTask currencyAsyncTask = new CurrencyAsyncTask();
                            // CurrencyAsyncTask this class is the class that extends AsynchTask
                            currencyAsyncTask.execute();
                            }else {
                                Toast.makeText(mainActivity, "Необходимо поключение " +
                                        "к интернету", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doCurrencyAsynTask, 0, 3600000); //execute in every 1 hour
    }




}
