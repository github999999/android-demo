package com.my.ownpulltorefreshdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ThisView thisView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= (ListView) findViewById(R.id.list);
        List<String> list=new ArrayList<>();
        for (int i=0;i<20;i++){
            list.add("这是第"+i+"个Item哟");
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapter);
        thisView= (ThisView) findViewById(R.id.father);
        thisView.setOnRefreshListener(new ThisView.onRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        thisView.onRefreshCompleted();
                    }
                },3000);
            }
        });
    }
}
