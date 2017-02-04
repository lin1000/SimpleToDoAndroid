package com.codepath.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import static com.codepath.simpletodo.R.id.etNewItem;

public class EditItemActivity extends AppCompatActivity {

    String itemText;
    String itemTextOriginal;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemText = getIntent().getStringExtra("itemText");
        itemTextOriginal = getIntent().getStringExtra("itemTextOriginal");
        pos = getIntent().getIntExtra("pos",0);

        EditText editingItem = (EditText) findViewById(R.id.editingItem);
        editingItem.setText(itemText);
        editingItem.setSelection(editingItem.getText().length());

    }

    // ActivityTwo.java
    public void onSave(View v) {

        EditText editingItem = (EditText) findViewById(R.id.editingItem);
        itemText = editingItem.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("itemText", itemText);
        intent.putExtra("itemTextOriginal",itemTextOriginal);
        intent.putExtra("pos", pos);

        setResult(RESULT_OK, intent); // set result code and bundle data for response
        // closes the activity and returns to first screen
        this.finish();
    }
}
