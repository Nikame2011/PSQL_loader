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
            String[] row = (String[]) arguments.get("row");
            String[] name = (String[]) arguments.get("columns");
            String[] type = (String[]) arguments.get("type");
            StringBuilder summary= new StringBuilder();
            for(int column=0;column<name.length;column++){
                summary.append(name[column]).append("(").append(type[column]).append("): ").append(row[column]).append("\n");
            }
            rowInfoTV.setGravity(Gravity.START);
            rowInfoTV.setText(summary.toString());
        }
        else {
            rowInfoTV.setGravity(Gravity.CENTER);
            rowInfoTV.setText(R.string.TextViewConnectToDb);
        }
        Button dbConnectionB=findViewById(R.id.buttonDbConnection);
        dbConnectionB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.buttonDbConnection){
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        }
    }
}