package com.example.projectclass;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ClassAdapter.OnNextBtnClickListener {
    FloatingActionButton fab_main;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        fab_main = findViewById(R.id.fab_main);
        fab_main.setOnClickListener(v -> showDialog());
        loadData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        classAdapter.setOnNextBtnClickListener(this);
        registerForContextMenu(recyclerView);
    }

    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();
        classItems.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DbHelper.C_ID);
                int classNameIndex = cursor.getColumnIndex(DbHelper.CLASS_NAME_KEY);
                int subjectNameIndex = cursor.getColumnIndex(DbHelper.SUBJECT_NAME_KEY);

                if (idIndex >= 0 && classNameIndex >= 0 && subjectNameIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String className = cursor.getString(classNameIndex);
                    String subjectName = cursor.getString(subjectNameIndex);
                    classItems.add(new ClassItem(id, className, subjectName));
                } else {
                    Log.e("MainActivity", "Invalid column indices: idIndex=" + idIndex + ", classNameIndex=" + classNameIndex + ", subjectNameIndex=" + subjectNameIndex);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    private void showDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className, subjectName) -> addClass(className, subjectName));
    }

    private void addClass(String className, String subjectName) {
        long cidLong = dbHelper.addClass(className, subjectName);
        int cid = (int) cidLong; // Convert long to int
        ClassItem classItem = new ClassItem(cid, className, subjectName);
        classItems.add(classItem);
        classAdapter.notifyDataSetChanged();
    }


    @Override
    public void onNextBtnClick(int position) {
        ClassItem clickedItem = classItems.get(position);
        String message = "Next button clicked for: " + clickedItem.getClassName() + " - " + clickedItem.getSubjectName();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                ShowUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);

    }

    private void ShowUpdateDialog(int position) {
        MyDialog dialog =new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener( (className, subjectName) -> updateClass(position,className,subjectName));
    }
    private void updateClass(int position, String className, String subjectName) {
        dbHelper.updateClass(classItems.get(position).getCid(),className,subjectName);
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }


    private void deleteClass(int position) {
        long id = classItems.get(position).getCid();
        dbHelper.deleteClass(id);
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
        classAdapter.notifyItemRangeChanged(position, classItems.size());
    }
}