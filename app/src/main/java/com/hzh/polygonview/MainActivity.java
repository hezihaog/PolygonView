package com.hzh.polygonview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzh.polygonview.widget.PolygonView;

public class MainActivity extends AppCompatActivity {
    //分值数组
    private int[] scoreValueArr = new int[]{60, 80, 20, 40};
    //设置种类文字
    private String[] categoryTextArr = (new String[]{
            "综合",
            "财运",
            "工作",
            "爱情"});
    //设置种类分值文字的颜色
    private int[] scoreValueColorArr = new int[]{
            Color.parseColor("#8943C9"),
            Color.parseColor("#DBA700"),
            Color.parseColor("#2EC9FF"),
            Color.parseColor("#FF6A91")
    };

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
        polygonView.setDebug(false);
        polygonView.config(categoryTextArr, scoreValueArr, scoreValueColorArr);
    }
}