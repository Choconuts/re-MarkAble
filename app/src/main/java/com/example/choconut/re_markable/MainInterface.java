package com.example.choconut.re_markable;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.ViewGroup;

import com.example.choconut.re_markable.MarkTable;
import com.example.choconut.re_markable.UserHelper;
//import com.example.choconut.re_markable.qcloud.Utilities.Json.JSONObject;
import org.json.JSONObject;


import org.apmem.tools.layouts.FlowLayout;

import java.util.LinkedList;

public class MainInterface extends AppCompatActivity {
    private FlowLayout fl;
    private MarkTable mt;
    private UserHelper uh;
    private Handler hd;
    private JSONObject jo;
    private JSONObject tx;
    private boolean isCalled;
    private LinkedList<String> wordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
        isCalled=false;
        Intent intent= getIntent();
        String token= intent.getStringExtra("token");
        fl=(FlowLayout) findViewById(R.id.CandidateList);
        fl.setPadding(10,0,10,0);

        hd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                new AlertDialog.Builder(MainInterface.this).setMessage(msg.toString()).setPositiveButton("yes",null).show();
               switch (msg.what){
                   case 3:
                       if(!isCalled){
                           jo=UserHelper.getJson(msg);
                           uh.geDividedtWords(jo);
                           isCalled=true;
                       }
                       else{
                           tx=UserHelper.getJson(msg);
                           mt=new MarkTable(MarkTable.MarkType.ENTITY,jo,tx);
                           wordList=mt.getArticle();
                           for (int i=0;i<wordList.size();i++){
                               String idn=wordList.get(i);
                               addbutton(mt.getString(idn),idn);
                           }
                       }

                       break;
               }

                super.handleMessage(msg);
            }
        };
//        hd.hasMessages()
        uh=new UserHelper(hd);
        uh.getEntities(token);



    }

    private void addbutton(String text,String groupId){
        candidateButton btn =new candidateButton(new ContextThemeWrapper(this,R.style.Widget_AppCompat_Button_Small_mybutton),null);
        btn.setText(text);
        btn.setTextSize(20);

        btn.setGroupId(groupId);

        fl.addView(btn);
    }
}