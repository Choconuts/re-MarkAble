package com.example.choconut.re_markable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class MainActivity extends AppCompatActivity {

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取布局文件中的 Spinner 组件
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] arr = {"红楼梦","西游记","三国演义","水浒传"}; //创建 ArrayAdapter 对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, arr); //为 Spinner 设置 Adapter
        spinner.setAdapter(adapter);

    }
}
