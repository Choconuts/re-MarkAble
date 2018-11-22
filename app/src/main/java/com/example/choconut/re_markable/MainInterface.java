package com.example.choconut.re_markable;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import com.example.choconut.re_markable.MarkTable;
import com.example.choconut.re_markable.UserHelper;
//import com.example.choconut.re_markable.qcloud.Utilities.Json.JSONObject;
import org.json.JSONObject;


import org.apmem.tools.layouts.FlowLayout;

public class MainInterface extends AppCompatActivity {
    private FlowLayout fl;
    private MarkTable mt;
    private UserHelper uh;
    private Handler hd;
    private JSONObject jo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);

        Intent intent= getIntent();
        String token= intent.getStringExtra("token");
        fl=(FlowLayout) findViewById(R.id.CandidateList);
        hd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                new AlertDialog.Builder(MainInterface.this).setMessage(msg.toString()).setPositiveButton("yes",null).show();
               switch (msg.what){
                   case 3:
                       jo=UserHelper.getJson(msg);
                       break;
               }

                super.handleMessage(msg);
            }
        };
//        hd.hasMessages()
        uh=new UserHelper(hd);
        uh.getEntities(token);
        uh.geDividedtWords(jo);
        //mt=new MarkTable(MarkTable.MarkType.ENTITY);

    }
    private void addbutton(CharSequence text,String groupId){
        candidateButton btn =new candidateButton(new ContextThemeWrapper(this,R.style.Widget_AppCompat_Button_Small_mybutton),null);
        btn.setText(text);
        btn.setGroupId(groupId);
        fl.addView(btn);
    }
}
