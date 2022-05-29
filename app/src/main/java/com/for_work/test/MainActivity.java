package com.for_work.test;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle arguments=getIntent().getExtras();
        TextView rowInfoTV=findViewById(R.id.textViewRowInfo);
        if(arguments!=null) {
            //если при переключении с DatabaseActivity была передана информация, выводим её на экран
            String[] row = (String[]) arguments.get("row");
            String[] name = (String[]) arguments.get("columns");
            String[] type = (String[]) arguments.get("type");
            StringBuilder summary= new StringBuilder();
            for(int column=0;column<name.length;column++){
                summary.append(name[column]).append("(").append(type[column]).append("): ")
                        .append(row[column]).append("\n");
            }
            rowInfoTV.setGravity(Gravity.START);//устанавливаем выравнивание текста по левому краю
            rowInfoTV.setText(summary.toString());
        }
        else {
            //при запуске приложения информация не передаётся, выводим стартовое сообщение
            rowInfoTV.setGravity(Gravity.CENTER);//устанавливаем выравнивание текста по центру
            rowInfoTV.setText(R.string.TextViewConnectToDb);
        }
        Button dbConnectionB=findViewById(R.id.buttonDbConnection);
        dbConnectionB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.buttonDbConnection){
            //если нажата кнопка, переходим в другую activity для подключения к базе данных
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        }
    }
}