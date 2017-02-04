package com.codepath.simpletoto.sqlite;

import java.util.Date;

/**
 * Model class for TABLE_ITEM
 * Created by lin1000 on 2017/2/5.
 */

public class Item {
    public Integer ITEM_ID;
    public String ITEM_PRIORITY;
    public String ITEM_CONTENT;
    public String ITEM_START_DATE;
    public String ITEM_DUE_DATE;
    public String ITEM_IS_COMPLETE;

    @Override
    public String toString(){
        return ITEM_CONTENT;
    }

    public Item(String ITEM_CONTENT, String ITEM_PRIORITY, String ITEM_START_DATE, String ITEM_DUE_DATE, String ITEM_IS_COMPLETE){
        this.ITEM_CONTENT = ITEM_CONTENT;
        this.ITEM_PRIORITY = ITEM_PRIORITY;
        this.ITEM_START_DATE = ITEM_START_DATE;
        this.ITEM_DUE_DATE = ITEM_DUE_DATE;
        this.ITEM_IS_COMPLETE = ITEM_IS_COMPLETE;
    }

    public Item(String ITEM_CONTENT){
        this(ITEM_CONTENT,"M", "","","N");
    }

    public Item(){}

}
