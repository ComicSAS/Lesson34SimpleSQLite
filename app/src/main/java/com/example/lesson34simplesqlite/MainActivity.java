package com.example.lesson34simplesqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lesson34simplesqlite.utils.DBHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG = "Log";

    Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText etName, etEmail, etID;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnUpd = findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(this);

        btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etID = findViewById(R.id.etID);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {
        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String id = etID.getText().toString();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put("name", name);
                cv.put("email", email);

                // вставляем запись и получаем ее ID
                long rowID = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                break;

            case R.id.btnRead:
                Log.d(LOG_TAG, "--- Rows in mytable: ---");
                // делаем запрос всех данных из таблицы mytable, получаем Cursor
                Cursor cursor = db.query("mytable", null, null, null, null, null, null);
                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (cursor.moveToFirst()) {
                    // определяем номера столбцов по имени в выборке
                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int emailIndex = cursor.getColumnIndex("email");

                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "ID = " + cursor.getInt(idIndex) +
                                        ", name = " + cursor.getString(nameIndex) +
                                        ", email = " + cursor.getString(emailIndex));
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false - выходим из цикла
                    } while (cursor.moveToNext());
                } else
                    Log.d(LOG_TAG, "0 rows");
                cursor.close();
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                int clearCount = db.delete("mytable", null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                break;
            case R.id.btnUpd:
                if (id.equalsIgnoreCase(""))
                    break;
                Log.d(LOG_TAG, "--- Update mytable: ---");
                // подготовим значения для обновления
                cv.put("name", name);
                cv.put("email", email);
                // обновляем по id
                int updateCount = db.update("mytable", cv, "id = ?", new String[]{id});
                Log.d(LOG_TAG, "updated rows count = " + updateCount);
                break;
            case R.id.btnDel:
                if (id.equalsIgnoreCase(""))
                    break;
                Log.d(LOG_TAG, "--- Delete from mytable: ---");
                // удаляем по id
                int delCount = db.delete("mytable", "id = " + id, null);
                Log.d(LOG_TAG, "deleted rows count = " + delCount);
                break;
        }
        // закрываем подключение к БД
        dbHelper.close();
    }
}
