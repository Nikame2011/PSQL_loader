package com.for_work.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {


    private static String host = "195.161.69.96";
    private static String database = "test_database";
    private static String port = "5432";
    private static String user = "test_user";
    private static String pass = "test_password";
    private String url;
    public ArrayList<Table> tables=new ArrayList<>();

    public Database()
    {
        url = "jdbc:postgresql://%s:%s/%s";
        url = String.format(url, host, port, database);
    }

    public Database(String[] hostParameters){
        host= hostParameters[0];
        port= hostParameters[1];
        database= hostParameters[2];
        user= hostParameters[3];
        pass= hostParameters[4];
        url = "jdbc:postgresql://%s:%s/%s";
        url = String.format(url, host, port, database);
    }

    public String getUrl() {
        return url;
    }
    public String getHost() {
        return host;
    }
    public String getDatabase() {
        return database;
    }
    public int getTablesCount() {
        return tables.size();
    }
    public String getTableName(int tableNumber) {
        return tables.get(tableNumber).tableName;
    }
    public ArrayList<String[]> getTableRows(int tableNumber) {
        return tables.get(tableNumber).rows;
    }

    public String[] getColumnsNames(int tableNumber) {
        return tables.get(tableNumber).columnsName;
    }
    public String[] getColumnsDataType(int tableNumber) {
        return tables.get(tableNumber).columnsDataType;
    }
    public int[] getTableDrawInfo(int tableNumber) {
        return tables.get(tableNumber).drawInfo;
    }

    public String getUser() {
        return user;
    }
    public String getPass() {
        return pass;
    }
    public String getPort() {
        return port;
    }

    public void connect()
    {
        try
        {
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES");
            //загружаем информацию о имеющихся таблицах
            ArrayList<String> tables_name=new ArrayList<>();
            while(rs.next()) {
                if (rs.getString(2).equals("public")){
                    tables_name.add(rs.getString(3));//выбираем пользовательские таблицы
                }
            }
            for (String name:tables_name){
                rs = st.executeQuery("SELECT * FROM "+name);
                ResultSetMetaData mData = rs.getMetaData();
                int columns = mData.getColumnCount();
                String[] columnsName = new String[columns];
                String[] columnsType = new String[columns];
                for (int column=1;column<=columns;column++){
                    columnsName[column-1]=mData.getColumnName(column);//сохраняем названия столбцов
                    columnsType[column-1]=mData.getColumnTypeName(column);
                    //isAutoIncrement
                }
                ArrayList<String[]> rows=new ArrayList<>();
                while (rs.next()) {
                    String[] row = new String[columns];
                    for (int column = 1; column <= columns; column++) {
                        row[column - 1] = rs.getString(column);//сохраняем строки
                    }
                    rows.add(row);
                }
                tables.add(new Table(name,columnsName,columnsType,rows));
            }
            rs.close();
            st.close();
            connection.close();
        }
        catch (Exception e)
        {
            //status = false;
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
    }

    private static class Table{
        String tableName;
        String[] columnsName;
        String[] columnsDataType;
        ArrayList<String[]> rows;

        int[] drawInfo;

        private Table(String tableName, String[]columnsName,String[]columnsDataType, ArrayList<String[]> rows){
            this.tableName=tableName;
            this.columnsName=columnsName;
            this.columnsDataType=columnsDataType;
            this.rows=rows;
            drawInfo=new int[]{-1,-1};
            for(int column=0;column<columnsDataType.length;column++){
                if(columnsDataType[column].equals("bigserial") & drawInfo[0]==-1){
                    drawInfo[0]=column;
                }
                if(columnsDataType[column].equals("int4") & drawInfo[1]==-1){
                    drawInfo[1]=column;
                }
            }
        }


    }
}