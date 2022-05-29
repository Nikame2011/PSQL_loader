package com.for_work.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener{

    private Database db;
    private int tableNumber=0;
    private boolean graphicMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Bundle arguments=getIntent().getExtras();
        if(arguments!=null) {
            db = new Database((String[])arguments.get("hostData"));
        }
        else {
            db = new Database();
        }
        Button tablePreviousB=findViewById(R.id.buttonTablePrevious);
        tablePreviousB.setOnClickListener(this);
        Button tableNextB=findViewById(R.id.buttonTableNext);
        tableNextB.setOnClickListener(this);
        ImageButton changeModeIB=findViewById(R.id.imageButtonChangeMode);
        changeModeIB.setOnClickListener(this);
        TextView connectionTextView=findViewById(R.id.textViewConnection);
        connectionTextView.setOnClickListener(this);
        String connection="%s\n%s";
        connectionTextView.setText(String.format(connection, DatabaseActivity.this.getString(
                R.string.TextViewConnectionInProgress),db.getUrl()));
        Thread thread = new Thread(() -> {
            db.connect();
            runOnUiThread(() -> {
                if (db.getTablesCount()>0){//за удачное подключение считается обнаружение таблиц
                    connectionTextView.setText(String.format(connection,
                            DatabaseActivity.this.getString(R.string.TextViewConnectionSuccessful),
                            db.getUrl()));
                   viewTableData();
                }
                else{
                    connectionTextView.setText(String.format(connection,
                            DatabaseActivity.this.getString(R.string.TextViewConnectionFailed),
                                    db.getUrl()));
                }
            });
        });
        thread.start();
    }

    private void viewTableData(){
        GraphView graph = findViewById(R.id.graphViewTableInfo);
        graph.setVisibility(View.INVISIBLE);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTable);
        //создаем адаптер
        recyclerView.setVisibility(View.VISIBLE);
        TableAdapter.OnRowClickListener rowClickListener = (row, position) -> {
            Intent intent = new Intent(DatabaseActivity.this,
                    MainActivity.class);
            intent.putExtra("columns",db.getColumnsNames(tableNumber));
            intent.putExtra("type",db.getColumnsDataType(tableNumber));
            intent.putExtra("row",db.getTableRows(tableNumber).get(position));
            startActivity(intent);
        };
        TableAdapter adapter = new TableAdapter(DatabaseActivity.this,
                db.getTableRows(tableNumber),rowClickListener);// устанавливаем для списка адаптер
        TextView tableNameTV = findViewById(R.id.textViewTableName);
        tableNameTV.setText(db.getTableName(tableNumber));
        tableNameTV.setVisibility(View.VISIBLE);
        Button tablePreviousB=findViewById(R.id.buttonTablePrevious);
        if (tableNumber>0)
            tablePreviousB.setVisibility(View.VISIBLE);
        else
            tablePreviousB.setVisibility(View.INVISIBLE);
        Button tableNextB=findViewById(R.id.buttonTableNext);
        if (tableNumber<db.getTablesCount()-1)
            tableNextB.setVisibility(View.VISIBLE);
        else
            tableNextB.setVisibility(View.INVISIBLE);
        int[] drawInfo=db.getTableDrawInfo(tableNumber);
        ImageButton changeModeIB=findViewById(R.id.imageButtonChangeMode);
        if (drawInfo[0]>=0 & drawInfo[1]>=0){
            changeModeIB.setImageResource(R.drawable.ic_action_graphic);
            changeModeIB.setVisibility(View.VISIBLE);
        }
        else {
            changeModeIB.setVisibility(View.INVISIBLE);
        }
        recyclerView.setAdapter(adapter);
    }

    private void graphTableData(){
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTable);
        recyclerView.setVisibility(View.INVISIBLE);
        GraphView graph = findViewById(R.id.graphViewTableInfo);
        graph.setVisibility(View.VISIBLE);
        int[] drawInfo=db.getTableDrawInfo(tableNumber);
        ArrayList<String[]>table=db.getTableRows(tableNumber);
        DataPoint[] points=new DataPoint[table.size()];
        for(int row=0;row<table.size();row++){
            points[row]=new DataPoint(Double.parseDouble(table.get(row)[drawInfo[0]]),
                    Double.parseDouble(table.get(row)[drawInfo[1]]));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        graph.addSeries(series);
        ImageButton changeModeIB=findViewById(R.id.imageButtonChangeMode);
        changeModeIB.setImageResource(R.drawable.ic_action_table);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.textViewConnection){
            Intent intent = new Intent(this, SetUrlActivity.class);
            String[] extra=new String[]{
                    db.getHost(),
                    db.getPort(),
                    db.getDatabase(),
                    db.getUser(),
                    db.getPass()
            };
            intent.putExtra("hostData",extra);
            startActivity(intent);
        }
        else if(view.getId()==R.id.buttonTablePrevious){
            if (tableNumber>0)tableNumber--;
            viewTableData();
        }
        else if(view.getId()==R.id.buttonTableNext){
            if (tableNumber<db.getTablesCount()-1)tableNumber++;
            viewTableData();
        }
        else if(view.getId()==R.id.imageButtonChangeMode){
            if (graphicMode){
                graphicMode=false;
                viewTableData();
            }
            else{
                graphicMode=true;
                graphTableData();
            }
        }
    }
}