package com.example.qlsv;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtmalop, edttenlop, edtsiso;
    Button btninsert, btndelete, btnupdate, btnquery;
    ListView LV;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        edtmalop = findViewById(R.id.edtmalop);
        edttenlop = findViewById(R.id.edttenlop);
        edtsiso = findViewById(R.id.edtsiso);
        btninsert = findViewById(R.id.btnthem);
        btndelete = findViewById(R.id.btnxoa);
        btnupdate = findViewById(R.id.btnsua);
        btnquery = findViewById(R.id.btntimkiem);

        // Setup ListView
        LV = findViewById(R.id.lv);
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        LV.setAdapter(myAdapter);

        // Create and open SQLite database
        myDatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Create table if it doesn't exist
        try {
            String sql = "CREATE TABLE IF NOT EXISTS tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            myDatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table already exists");
        }

        // Fetch and display data from the database
        fetchDataAndDisplay();

        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        btnquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchDataAndDisplay();
            }
        });
    }

    private void fetchDataAndDisplay() {
        myList.clear();
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM tbllop", null);
        if (cursor.moveToFirst()) {
            do {
                String malop = cursor.getString(0);
                String tenlop = cursor.getString(1);
                int siso = cursor.getInt(2);
                myList.add("Mã lớp: " + malop + ", Tên lớp: " + tenlop + ", Sĩ số: " + siso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        myAdapter.notifyDataSetChanged();
    }

    private void insertData() {
        String malop = edtmalop.getText().toString();
        String tenlop = edttenlop.getText().toString();
        int siso;
        try {
            siso = Integer.parseInt(edtsiso.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Sĩ số phải là một số nguyên.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues myValue = new ContentValues();
        myValue.put("malop", malop);
        myValue.put("tenlop", tenlop);
        myValue.put("siso", siso);
        String msg;
        if (myDatabase.insert("tbllop", null, myValue) == -1) {
            msg = "Thêm bị lỗi! Hãy thử lại.";
        } else {
            msg = "Thêm thành công";
            fetchDataAndDisplay(); // Update the ListView after inserting new data
        }
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void deleteData() {
        String malop = edtmalop.getText().toString();
        int n = myDatabase.delete("tbllop", "malop = ?", new String[]{malop});
        String msg;
        if (n == 0) {
            msg = "Xóa không thành công";
        } else {
            msg = n + " xóa thành công";
            fetchDataAndDisplay(); // Update the ListView after deleting data
        }
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void updateData() {
        int siso;
        try {
            siso = Integer.parseInt(edtsiso.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Sĩ số phải là một số nguyên.", Toast.LENGTH_SHORT).show();
            return;
        }

        String malop = edtmalop.getText().toString();
        ContentValues myValue = new ContentValues();
        myValue.put("siso", siso);
        int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{malop});
        String msg;
        if (n == 0) {
            msg = "Chỉnh sửa không thành công";
        } else {
            msg = n + " chỉnh sửa thành công";
            fetchDataAndDisplay(); // Update the ListView after updating data
        }
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}