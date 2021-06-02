package com.agndesarrollos.asteroidsnw.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OperacionesBDInterna extends SQLiteOpenHelper {

    private String DB_PATH;
    private static String DB_NAME = "dbasteroidsnw.db";
    private SQLiteDatabase DataBase;
    private final Context context;

    public OperacionesBDInterna(Context contx){
        super(contx, DB_NAME, null, 1);
        this.context = contx;
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            this.DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        } else {
            this.DB_PATH = contx.getApplicationInfo().dataDir + "/databases/";
        }
        //this.DB_PATH = contx.getApplicationInfo().dataDir + "/databases/";
    }

    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //La base de datos existe y no hacemos nada.
        }else{
            //Llamando a este método se crea la base de datos vacía en la ruta
            //por defecto del sistema de nuestra aplicación por lo que podremos
            //sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copiando Base de Datos");
            }
        }

    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el
     * fichero cada vez que se abra la aplicación.
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            //checkDB = SQLiteDatabase.openOrCreateDatabase(myPath,null);
        }catch(SQLiteException e){
            //si llegamos aqui es porque la base de datos no existe todavía.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a
     * ella.
     * Esto se hace con bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Abrimos el fichero de base de datos como entrada
        InputStream myInput = context.getAssets().open(DB_NAME);

        //Ruta a la base de datos vacía recién creada
        String outFileName = DB_PATH + DB_NAME;

        //Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(outFileName);

        //Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void open() throws SQLException {

        //Abre la base de datos
        try {
            createDataBase();
        } catch (IOException e) {
            throw new Error("Ha sido imposible crear la Base de Datos");
        }

        String myPath = DB_PATH + DB_NAME;
        DataBase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public boolean crear(SQLiteDatabase.CursorFactory factory){
        boolean creado = false;
        try{
            SQLiteDatabase db = DataBase.create(factory);
            creado = true;
        }catch (SQLiteException e){

        }
        return creado;
    }

    public boolean insertar(String TABLE_NAME, ContentValues values){
        //Por ahora sólo inserta un registro
        boolean exitoso = false;
        if(DataBase == null){
            open();
        }
        long res = DataBase.insert(TABLE_NAME, null, values);
        if(res != -1){
            exitoso = true;
        }
        return exitoso;
    }

    public boolean actualizar(String TABLE_NAME, ContentValues mapColumns, String whereCols, String[] whereValues){
        boolean exitoso = false;
        if(DataBase == null){
            open();
        }
        int res = DataBase.update(TABLE_NAME, mapColumns, whereCols, whereValues);
        if(res != 0){
            exitoso = true;
        }
        return exitoso;
    }

    public boolean eliminar(String TABLE_NAME, String whereCols, String [] whereValues){
        boolean exitoso = false;
        if(DataBase == null){
            open();
        }
        int res = DataBase.delete(TABLE_NAME, whereCols, whereValues);
        if(res != 0){
            exitoso = true;
        }
        return exitoso;
    }

    public Cursor query(boolean distinct,String table,String[] columns,String selection,String[] selectionArgs,
                         String groupBy,String having,String orderBy,String limit){
        if(DataBase == null){
            open();
        }
        Cursor cursor = DataBase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    public Cursor rawQuery (String sqlQuery, String[] whereV){
        if(DataBase == null){
            open();
        }
        Cursor cursorR = DataBase.rawQuery(sqlQuery,whereV);
        return cursorR;
    }

    public boolean queryNoData(String sql){
        boolean exitoso = false;
        if(DataBase == null){
            open();
        }
        try{
            DataBase.execSQL(sql);
            exitoso = true;
        } catch (SQLiteException e){

        }
        return exitoso;
    }

    @Override
    public synchronized void close() {
        if(DataBase != null)
            DataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}