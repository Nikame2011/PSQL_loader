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
import com.jjoe64.graphview.GridLabelRenderer;
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
            //если при переключении activity была передана информация, используем её для подключения
            db = new Database((String[])arguments.get("hostData"));
        }
        else {
            //если информация не передавалась, используем предустановленные параметры
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
        Thread thread = new Thread(() -> {//в отдельном потоке осуществляем подключение к базе
            db.connect();
            runOnUiThread(() -> {
                //когда попытка подключения завершена, выводим сообщение и таблицу в UI потоке
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
        graph.setVisibility(View.INVISIBLE); //скрываем поле графического вывода данных
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTable);
        recyclerView.setVisibility(View.VISIBLE); //отображаем поле для табличного вывода данных
        TableAdapter.OnRowClickListener rowClickListener = (row, position) -> {
            //обявляем слушатель для адаптера. При нажатии на строку в поле вывода будет осуществлён
                //переход в MainActivity с передачей информации из данной строки
            Intent intent = new Intent(DatabaseActivity.this,
                    MainActivity.class);
            intent.putExtra("columns",db.getColumnsNames(tableNumber));
            intent.putExtra("type",db.getColumnsDataType(tableNumber));
            intent.putExtra("row",db.getTableRows(tableNumber).get(position));
            startActivity(intent);
        };
        TableAdapter adapter = new TableAdapter(DatabaseActivity.this, //создаем адаптер
                db.getTableRows(tableNumber),rowClickListener); // устанавливаем для списка адаптер
        TextView tableNameTV = findViewById(R.id.textViewTableName); //отображаем название таблицы
        tableNameTV.setText(db.getTableName(tableNumber));
        tableNameTV.setVisibility(View.VISIBLE);
        Button tablePreviousB=findViewById(R.id.buttonTablePrevious); //если таблиц больше одной,
            //отображаем кнопки навигации между таблицами
        if (tableNumber>0)
            tablePreviousB.setVisibility(View.VISIBLE);
        else
            tablePreviousB.setVisibility(View.INVISIBLE);
        Button tableNextB=findViewById(R.id.buttonTableNext);
        if (tableNumber<db.getTablesCount()-1)
            tableNextB.setVisibility(View.VISIBLE);
        else
            tableNextB.setVisibility(View.INVISIBLE);
        int[] drawInfo=db.getTableDrawInfo(tableNumber); //если получены указатели на строки,
            //подходящие для графического вывода, отображаем кнопку переключения режима
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
            //скрываем поле текстового вывода данных
        recyclerView.setVisibility(View.INVISIBLE);
        GraphView graph = findViewById(R.id.graphViewTableInfo);
            //отображаем поле графического вывода данных
        graph.setVisibility(View.VISIBLE);
        int[] drawInfo=db.getTableDrawInfo(tableNumber);
        ArrayList<String[]>table=db.getTableRows(tableNumber);
        DataPoint[] points=new DataPoint[table.size()];
        for(int row=0;row<table.size();row++){//подготавливаем точки для графического отображения
            points[row]=new DataPoint(Double.parseDouble(table.get(row)[drawInfo[0]]),
                    Double.parseDouble(table.get(row)[drawInfo[1]]));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);//создаём гистограмму
        graph.addSeries(series);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle(db.getColumnsNames(tableNumber)[drawInfo[0]]);
        gridLabel.setVerticalAxisTitle(db.getColumnsNames(tableNumber)[drawInfo[1]]);
        ImageButton changeModeIB=findViewById(R.id.imageButtonChangeMode);
        //изменяем иконку на кнопке переключения режимов
        changeModeIB.setImageResource(R.drawable.ic_action_table);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.textViewConnection){//если сработало нажатие на строку с адресом
            //подключения, переходим в SetUrlActivity для редактирования параметров переключения
                //и передаём текущие параметры
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
        else if(view.getId()==R.id.buttonTablePrevious){//если нажата кнопка "-", уменьшаем номер
            // отображаемой таблицы и отображаем вновь выбранную
            if (tableNumber>0)tableNumber--;
            viewTableData();
        }
        else if(view.getId()==R.id.buttonTableNext){//если нажата кнопка "+", увеличиваем номер
            // отображаемой таблицы и отображаем вновь выбранную
            if (tableNumber<db.getTablesCount()-1)tableNumber++;
            viewTableData();
        }
        else if(view.getId()==R.id.imageButtonChangeMode){//если нажата кнопка переключения режима,
            // переключаем режим вывода информации
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