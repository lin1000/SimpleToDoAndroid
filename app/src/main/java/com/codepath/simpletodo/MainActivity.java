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

import com.codepath.simpletoto.sqlite.Item;
import com.codepath.simpletoto.sqlite.TodoDatabaseHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> itemArrayList;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    //singleton instance
    TodoDatabaseHelper todoDatabaseHelper = TodoDatabaseHelper.getInstance(this);

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        // Create sample data
//        Item sampleItem = new Item();
//        sampleItem.ITEM_PRIORITY = "H";
//        sampleItem.ITEM_CONTENT = "Do the homework!";
//        sampleItem.ITEM_START_DATE = "2017-01-26";
//        sampleItem.ITEM_DUE_DATE = "2017-02-06";
//        sampleItem.ITEM_IS_COMPLETE = "False";

//        // Add sample post to the database
//        todoDatabaseHelper.addItem(sampleItem);



        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        //itemArrayList = new ArrayList<>();
        //readItems();
        readItemsFromDB();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemArrayList);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        System.out.println(itemText);
        Item tempItem = new Item(itemText);
        insertItem(tempItem);
        itemsAdapter.add(itemText);
        itemsAdapter.notifyDataSetChanged();
        etNewItem.setText("");


    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new android.widget.AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id){
                    deleteItemByItemContent(adapter.getItemAtPosition(pos).toString());
                    itemArrayList.remove(pos);
                    itemsAdapter.notifyDataSetChanged();
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

    //read from file storage
    private void readItems(){
        System.out.println("readItems()");
        File filesDir = getFilesDir();
        System.out.println("filesDir.toString()="+ filesDir.toString());
        File todoFile = new File(filesDir,"todo.txt");
        try{
            itemArrayList =  new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch(IOException e){
            itemArrayList =  new ArrayList<String>();
        }
        System.out.println("itemArrayList.size()="+ itemArrayList.size());
    }

    //write to file storage
    private void writeItems(){
        System.out.println("writeItems()");
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try{
            FileUtils.writeLines(todoFile,itemArrayList);
        }catch(IOException e){
            e.printStackTrace();
            itemArrayList =  new ArrayList<String>();
        }
    }

    //Query from db storage
    private void readItemsFromDB(){

        itemArrayList = new ArrayList<>();

        // Get all posts from database
        List<Item> items = todoDatabaseHelper.getAllItems();
        System.out.println("items.size()=" + items.size());

        for (Item item : items) {
            itemArrayList.add(String.valueOf(item));
            System.out.println("===========================================");
            System.out.println("ITEM_ID="+ item.ITEM_ID);
            System.out.println("ITEM_PRIORITY="+ item.ITEM_PRIORITY);
            System.out.println("ITEM_CONTENT="+ item.ITEM_CONTENT);
            System.out.println("ITEM_START_DATE="+ item.ITEM_START_DATE);
            System.out.println("ITEM_DUE_DATE="+ item.ITEM_DUE_DATE);
            System.out.println("ITEM_IS_COMPLETE="+ item.ITEM_IS_COMPLETE);
        }
    }

    //Delete from db storage
    private void deleteItemByItemContent(String itemContent){
        todoDatabaseHelper.deleteItemByItemContent(itemContent);
    }

    //Insert into db storage
    private void insertItem(Item newItem){
        todoDatabaseHelper.addItem(newItem);
    }

    //Update into db storage
    private void updateItem(String itemContent, String itemContentOriginal){
        Item tobeItem = new Item(itemContent);
        Item asisItem = new Item(itemContentOriginal);
        todoDatabaseHelper.updateItem(tobeItem,asisItem);
    }





    //To launch EditItem Activity ans apss item text (and relevant info) for edit/save purpose
    public void launchEditView( String itemText, int pos) {
        // first parameter is the context, second is the class of the activity to launch
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        // put "extras" into the bundle for access in the second activity
        intent.putExtra("itemText", itemText);
        intent.putExtra("itemTextOriginal", itemText);
        intent.putExtra("pos", pos);
        //startActivity(i); // brings up the second activity
        startActivityForResult(intent,REQUEST_CODE);
    }

    // ActivityOne.java, time to handle the call back from EditItem Activity
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
            String itemTextOriginal = intent.getExtras().getString("itemTextOriginal");
            int pos = intent.getExtras().getInt("pos", 0);
            System.out.println("itemArrayList="+itemArrayList);
            System.out.println("itemsAdapter="+itemsAdapter);
            System.out.println("itemText="+itemText);
            System.out.println("itemTextOriginal="+itemTextOriginal);
            System.out.println("pos="+pos);
            updateItem(itemText,itemTextOriginal);
            itemArrayList.set(pos, itemText);
            itemsAdapter.notifyDataSetChanged();

        }
    }
}
