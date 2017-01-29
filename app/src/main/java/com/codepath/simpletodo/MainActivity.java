package com.codepath.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        //items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        System.out.println(itemText);
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new android.widget.AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id){
                    items.remove(pos);
                    itemsAdapter.notifyDataSetChanged();
                    writeItems();
                    return true;
                }
        });

        lvItems.setOnItemClickListener(
                new android.widget.AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id){
                        String itemText= adapter.getItemAtPosition(pos).toString();
                        System.out.println("itemText="+ adapter.getItemAtPosition(pos).toString() );
                        System.out.println("pos="+ pos);
                        System.out.println("id="+ id);
                        launchEditView(itemText,pos);
                    }
                });
    }

    private void readItems(){
        File filesDir = getFilesDir();
        System.out.println("filesDir.toString()="+ filesDir.toString());
        File todoFile = new File(filesDir,"todo.txt");
        try{
            items =  new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch(IOException e){
            items =  new ArrayList<String>();
        }
        System.out.println("items.size()="+ items.size());
    }

    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            FileUtils.writeLines(todoFile,items);
        }catch(IOException e){
            items =  new ArrayList<String>();
        }
    }

    public void launchEditView( String itemText, int pos) {
        // first parameter is the context, second is the class of the activity to launch
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        // put "extras" into the bundle for access in the second activity
        intent.putExtra("itemText", itemText);
        intent.putExtra("pos", pos);
        //startActivity(i); // brings up the second activity
        startActivityForResult(intent,REQUEST_CODE);
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // REQUEST_CODE is defined above
        System.out.println("RESULT_OK="+RESULT_OK);
        System.out.println("REQUEST_CODE="+REQUEST_CODE);
        System.out.println("resultCode="+resultCode);
        System.out.println("requestCode="+requestCode);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String itemText = intent.getExtras().getString("itemText");
            int pos = intent.getExtras().getInt("pos", 0);
            System.out.println("items="+items);
            System.out.println("itemsAdapter="+itemsAdapter);
            System.out.println("itemText="+itemText);
            System.out.println("pos="+pos);
            items.set(pos, itemText);
            itemsAdapter.notifyDataSetChanged();

        }
    }
}
