package com.example.user.refreshtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

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
    }
}
