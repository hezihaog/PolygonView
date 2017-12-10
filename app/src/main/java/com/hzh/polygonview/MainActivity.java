package com.hzh.polygonview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzh.polygonview.widget.PolygonView;

public class MainActivity extends AppCompatActivity {
    //分值数组
    private int[] scoreValueArr = new int[]{60, 80, 20, 40};//,40

    private PolygonView polygonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        onBindContent();
    }


    private void initView() {
        polygonView = (PolygonView) findViewById(R.id.polygonView);
    }

    private void onBindContent() {
        polygonView.setScoreValueArr(scoreValueArr);
    }
}