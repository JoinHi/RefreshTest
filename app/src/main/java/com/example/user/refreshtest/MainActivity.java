package com.example.user.refreshtest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RefreshView mRefreshView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mListView = (ListView) findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        while (true) {
            count++;
            list.add(count + "");
            if (count == 50)
                break;
        }
        TextView textView = new TextView(this);
        textView.setText("hahah");
        textView.setTextColor(Color.RED);
        textView.setPadding(0,100,0,0);
        textView.setBackgroundColor(Color.WHITE);
        mRefreshView.setHeader(textView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1);
        arrayAdapter.addAll(list);
        mListView.setAdapter(arrayAdapter);
    }
}
