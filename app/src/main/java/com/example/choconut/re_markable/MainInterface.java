package com.example.choconut.re_markable;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.Window;
import android.widget.Toast;


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
    private LinkedList<ButtonData> ButtonList;
    public int left;
    public int right;
    private String username;
    private boolean isComplete;
    Button combine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
//       getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        isCalled=false;
        isComplete=false;
        left=-1;
        right=-1;
        Intent intent= getIntent();
        String token= intent.getStringExtra("token");
        username=intent.getStringExtra("username");
        fl=(FlowLayout) findViewById(R.id.CandidateList);
        fl.setPadding(10,0,10,0);
        combine=findViewById(R.id.combine);
        combine.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                CombineGroup();
            }
        });
        ButtonList=new LinkedList<>();

        hd=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                            LinkedList<String> wordList;
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
        FileHelper fileHelper = new FileHelper();
        String now=username;
        now=now.concat("+entity.json");
        Object su=fileHelper.read(now);
        if(null!=su){
            try{
                mt=MarkTable.load(now);
            }
            catch (Exception e){
                Toast.makeText(MainInterface.this, "存档出错", Toast.LENGTH_SHORT).show();
                uh.getEntities(token);
            }
        }
        else{
            uh.getEntities(token);
        }





    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addbutton(String text, String groupId){
        candidateButton btn =new candidateButton(new ContextThemeWrapper(this,R.style.Widget_AppCompat_Button_Small_mybutton),null);
        btn.setActivity(this);
        btn.setText(text);
        btn.setTextSize(20);
        btn.setLetterSpacing(0.4f);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(3,8,8,3);
        btn.setLayoutParams(lp);
        btn.setGroupId(groupId);

        fl.addView(btn);
        ButtonData bdn=new ButtonData(groupId,btn,0,0,text);
        ButtonList.add(bdn);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addbutton(String text, String groupId,int index){
        candidateButton btn =new candidateButton(new ContextThemeWrapper(this,R.style.Widget_AppCompat_Button_Small_mybutton),null);
        btn.setActivity(this);
        btn.setText(text);
        btn.setTextSize(20);
        btn.setLetterSpacing(0.4f);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(3,8,8,3);
        btn.setLayoutParams(lp);
        btn.setGroupId(groupId);

        fl.addView(btn,index);
        ButtonData bdn=new ButtonData(groupId,btn,0,0,text);
        ButtonList.add(index,bdn);
    }



    public int getNumByID(String groupID){
        int result=-1;
        for(int i=0;i<ButtonList.size();i++){
            if(groupID.equals(ButtonList.get(i).groupID))result=i;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void CombineGroup(){
        if(left!=-1&&right!=-1){
            if(left>right){
                int temp=left;
                left=right;
                right=temp;
            }
            String id2= mt.combine(ButtonList.get(left).groupID,ButtonList.get(right).groupID);

            for(int i=left;i<=right;i++){
                fl.removeView(ButtonList.get(i).bt);
            }
            for(int i=left;i<=right;i++){
                ButtonList.remove(left);
            }

            addbutton(mt.getString(id2),id2,left);
            left=-1;
            right=-1;
        }
    }

    @Override
    protected void onDestroy() {
        String url=this.getFilesDir().getPath()+"/"+username+"/entity-save.json";
        boolean su=mt.save(url);
        super.onDestroy();
    }

    public void setLeft(int left) {
        this.left = left;
    }
    public void setRight(int right) {
        this.right = right;
    }

    public void connectlr(){
        boolean flag=false;
        if(left!=-1&&right!=-1) {
            if (left > right) {
                int temp = left;
                left = right;
                right = temp;
                ButtonList.get(left).bt.setTouch();
                ButtonList.get(right).bt.setTouch();
                flag=true;
            }
            for (int i = left + 1; i <= right; i++) {
                ButtonList.get(i).bt.setTouch();
            }
            if(flag){
                int temp2 = left;
                left = right;
                right = temp2;
            }
        }
    }


}

class ButtonData{
    public String groupID;
    public candidateButton bt;
    public int pos1;
    public int pos2;
    public String word;
    public ButtonData(String groupID, candidateButton bt, int pos1, int pos2, String word) {
        this.groupID = groupID;
        this.bt = bt;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.word = word;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public candidateButton getBt() {
        return bt;
    }

    public void setBt(candidateButton bt) {
        this.bt = bt;
    }

    public int getPos1() {
        return pos1;
    }

    public void setPos1(int pos1) {
        this.pos1 = pos1;
    }

    public int getPos2() {
        return pos2;
    }

    public void setPos2(int pos2) {
        this.pos2 = pos2;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}