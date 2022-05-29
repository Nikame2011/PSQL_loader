package com.for_work.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetUrlActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments=getIntent().getExtras();
        String[] extra= (String[]) arguments.get("hostData");
        //получаем параметры, считая, что запуск SetUrlActivity без передачи параметров невозможен.
        //полученные параметры выводим на экран
        setContentView(R.layout.activity_set_url);
        EditText hostET=findViewById(R.id.editTextHost);
        hostET.setText(extra[0]);
        EditText portET=findViewById(R.id.editTextPort);
        portET.setText(extra[1]);
        EditText dbET=findViewById(R.id.editTextDatabase);
        dbET.setText(extra[2]);
        EditText userET=findViewById(R.id.editTextUser);
        userET.setText(extra[3]);
        EditText passET=findViewById(R.id.editTextPassword);
        passET.setText(extra[4]);
        Button selectB=findViewById(R.id.buttonSelect);
        Button closeB=findViewById(R.id.buttonClose);
        selectB.setOnClickListener(this);
        closeB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, DatabaseActivity.class);
        if(view.getId()==R.id.buttonSelect){ //если нажата кнопка Принять, считываем с полей новые
            //параметры и передаём их, запуская DatabaseActivity
            String[] extra=new String[6];
            EditText hostET=findViewById(R.id.editTextHost);
            extra[0]= String.valueOf(hostET.getText());
            EditText portET=findViewById(R.id.editTextPort);
            extra[1]= String.valueOf(portET.getText());
            EditText dbET=findViewById(R.id.editTextDatabase);
            extra[2]= String.valueOf(dbET.getText());
            EditText userET=findViewById(R.id.editTextUser);
            extra[3]= String.valueOf(userET.getText());
            EditText passET=findViewById(R.id.editTextPassword);
            extra[4]= String.valueOf(passET.getText());
            intent.putExtra("hostData",extra);
            startActivity(intent);
        }
        else if(view.getId()==R.id.buttonClose){//если нажата кнопка Отмена, запускаем
            //DatabaseActivity, не передавая параметров
            startActivity(intent);
        }
    }
}