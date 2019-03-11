package com.example.converter;

import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileOperations {
    private String FILENAME = "file.txt";

    public void writeFile(String response) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    MainActivity.getInstance().openFileOutput(FILENAME,
                            MainActivity.getInstance().MODE_PRIVATE)));
//             пишем данные
            bw.write(response);
//             закрываем поток
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readFile() {
        StringBuilder fileContent = new StringBuilder();

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    MainActivity.getInstance().openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                fileContent.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }


    public void setDateFromFile() {
        TextView lastRefreshingDate = (TextView) MainActivity.getInstance().findViewById(R.id.textView);
        if (readFile().length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(readFile());
                String jsonDate = jsonObject.getString("Timestamp");
                lastRefreshingDate.setText(jsonDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            lastRefreshingDate.setText("Будет доступно после подключения интернета");
        }

    }
}
