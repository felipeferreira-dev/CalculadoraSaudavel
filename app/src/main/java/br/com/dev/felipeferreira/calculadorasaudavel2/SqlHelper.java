package br.com.dev.felipeferreira.calculadorasaudavel2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SqlHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fitness_tracker.db";
    private static final int DB_VERSION = 1;
    private static SqlHelper INSTANCE;

    //Padrão SINGLETON = Um único objeto existente em toda a execução do aplicativo
    static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new SqlHelper(context);
        return INSTANCE;
    }

    //Construtor = Contexto, nome do banco, factory e versão
    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Criando a tabela
        db.execSQL("CREATE TABLE calc (id INTEGER primary key, type_calc TEXT, res DECIMAL, created_date DATETIME, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // para executar o onUpgrade, é necessário atualizar a versão (DB_VERSION) do banco
       // db.execSQL("ALTER TABLE calc ADD COLUMN name TEXT");
    }

    List<Register> getRegisterBy(String type) {
        List<Register> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        // O Cursor percorre todas as linhas do banco de dados, em busca das informações que preciso
        Cursor cursor = db.rawQuery("SELECT * FROM calc WHERE type_calc = ?", new String[]{ type });

        try {

            if (cursor.moveToFirst()) {
                do {
                    Register register = new Register();

                    register.id = cursor.getInt(cursor.getColumnIndex("id"));
                    register.name = cursor.getString(cursor.getColumnIndex("name"));
                    register.type = cursor.getString(cursor.getColumnIndex("type_calc"));
                    register.response = cursor.getDouble(cursor.getColumnIndex("res"));
                    register.createdDate = cursor.getString(cursor.getColumnIndex("created_date"));

                    registers.add(register);
                } while (cursor.moveToNext());

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    long addItem(String type, double response, String name) {

        SQLiteDatabase db = getWritableDatabase(); //Escrevendo os dados no bd

        long calcId = 0;

        try {
            db.beginTransaction(); //Abrindo conexão com bd

            ContentValues values = new ContentValues();
            values.put("Type_calc", type);
            values.put("res", response);

            //Definindo um padrão de formatação de data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", new Locale("pt", "BR"));
            String now = dateFormat.format(new Date());
            values.put("created_date", now);
            values.put("name", name);

            calcId = db.insertOrThrow("calc", null, values); // INSERT INTO calc VALUES ()

            db.setTransactionSuccessful(); // Efetivando a escrita no banco

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction(); // Fechando conexão com bd
        }
        return calcId;
    }

    long updateItem(String type, double response, String name, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long calcId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("type_calc", type);
            values.put("res", response);
            values.put("name", name);

            //Definindo um padrão de formatação de data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", new Locale("pt", "BR"));
            String now = dateFormat.format(new Date());
            values.put("created_date", now);

            calcId = db.update("calc", values, "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLiteUpdate",e.getMessage(), e);
        } finally {
            if(db.isOpen())
            db.endTransaction();
        }
        return calcId;
    }

    long deleteItem(String type, int id) {
        SQLiteDatabase bd = getWritableDatabase();
        long calcId = 0;

        try {
            bd.beginTransaction();

            calcId = bd.delete("calc", "id = ? and type_calc = ?", new String[]{String.valueOf(id), type});

            bd.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLiteDelete", e.getMessage(), e);
        } finally {
            if(bd.isOpen())
            bd.endTransaction();
        }

        return calcId;
    }
}
