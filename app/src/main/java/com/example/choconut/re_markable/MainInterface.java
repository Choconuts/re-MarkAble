package com.example.choconut.re_markable;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.Window;
import android.widget.Toast;
import adapter.Adapter;

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
    Button discard;
    Button seperate;
    String token;
    LinkedList<Entity> entitylist;
    LinkedList<String> infolist;

    private RecyclerView lRecyclerView;     //列表控件
    private RecyclerView.Adapter lAdapter;                //适配器

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        entitylist=new LinkedList<Entity>();
        Intent intent= getIntent();
        token= intent.getStringExtra("token");
        username=intent.getStringExtra("username");
        int start=intent.getIntExtra("start",0);
        fl=(FlowLayout) findViewById(R.id.CandidateList);
        fl.setPadding(10,0,10,0);
        discard=findViewById(R.id.discard);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fl.removeAllViews();
                uh.getEntities(token);
            }
        });
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



                            initView();
                            setAdapter();
                        }

                        break;
                }

                super.handleMessage(msg);
            }
        };
//        hd.hasMessages()
        uh=new UserHelper(hd);
        FileHelper fileHelper = new FileHelper();
        String dir = this.getFilesDir().getPath()+"/"+username;
        String now=dir+"/entity-save.json";
        Object su=fileHelper.read(now);
        if(null!=su&&start==0){
            try{
                mt=MarkTable.load(now);
                LinkedList<String> wordList;
                wordList=mt.getArticle();
                for (int i=0;i<wordList.size();i++){
                    String idn=wordList.get(i);
                    addbutton(mt.getString(idn),idn);
                }
                initView();
                setAdapter();

            }
            catch (Exception e){
                Toast.makeText(MainInterface.this, "存档出错", Toast.LENGTH_SHORT).show();
                uh.getEntities(token);
            }
        }
        else{
            uh.getEntities(token);
        }

        seperate=findViewById(R.id.separate);
        seperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup vg=findViewById(R.id.widget_1);


            }
        });




    }


    /**
     * 初始化控件方法
     */
    private void initView(){
        //获取列表控件
        lRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    /**
     * 设置适配器方法
     */
    private void setAdapter(){
        //设置列表布局管理
        lRecyclerView.setLayoutManager(new LinearLayoutManager(MainInterface.this));
        //设置适配器
        infolist=new LinkedList<>();
        entitylist=mt.getEntities();
        String now;
        for(int i=0;i<entitylist.size();i++){
            now=entitylist.get(i).entityName+"    ";
            now=now+entitylist.get(i).nerTag;
            infolist.add(now);
        }

        lRecyclerView.setAdapter(lAdapter = new Adapter(MainInterface.this,infolist));
        //设置列表中子项的动画
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());


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
        if(left!=-1){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainInterface.this);
            builder.setIcon(R.drawable.icon1);
            builder.setTitle("创建实体");
            builder.setMessage("这是一个什么实体？");
            builder.setPositiveButton("人名", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
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
                        mt.addEntity(ButtonList.get(left).groupID,"PERSON");
                        infolist.clear();
                        String now;
                        entitylist=mt.getEntities();
                        for(int i=0;i<entitylist.size();i++){
                            now=entitylist.get(i).entityName+"    ";
                            now=now+entitylist.get(i).nerTag;
                            infolist.add(now);
                        }
                        lAdapter.notifyDataSetChanged();
                        left=-1;
                        right=-1;
                    }
                    else if(left!=-1){
                        mt.addEntity(ButtonList.get(left).groupID,"PERSON");
                        infolist.clear();
                        String now;
                        entitylist=mt.getEntities();
                        for(int i=0;i<entitylist.size();i++){
                            now=entitylist.get(i).entityName+"    ";
                            now=now+entitylist.get(i).nerTag;
                            infolist.add(now);
                        }
                        lAdapter.notifyDataSetChanged();
                        ButtonList.get(left).bt.setTouch();
                        left=-1;
                        right=-1;
                    }
                }
            });


            builder.setNegativeButton("职位", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
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
                        mt.addEntity(ButtonList.get(left).groupID,"TITLE");
                        infolist.clear();
                        String now;
                        entitylist=mt.getEntities();
                        for(int i=0;i<entitylist.size();i++){
                            now=entitylist.get(i).entityName+"    ";
                            now=now+entitylist.get(i).nerTag;
                            infolist.add(now);
                        }
                        lAdapter.notifyDataSetChanged();
                        left=-1;
                        right=-1;
                    }
                    else if(left!=-1){
                        mt.addEntity(ButtonList.get(left).groupID,"TITLE");
                        infolist.clear();
                        String now;
                        entitylist=mt.getEntities();
                        for(int i=0;i<entitylist.size();i++){
                            now=entitylist.get(i).entityName+"    ";
                            now=now+entitylist.get(i).nerTag;
                            infolist.add(now);
                        }
                        lAdapter.notifyDataSetChanged();
                        ButtonList.get(left).bt.setTouch();
                        left=-1;
                        right=-1;
                    }
                }
            });

            builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(MainInterface.this, "neutral: " + which, Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();



        }

    }

    @Override
    protected void onDestroy() {
        String dir = this.getFilesDir().getPath()+"/"+username;
        FileHelper fp = new FileHelper();
        if (fp.mkdir(dir)){
            String url=dir+"/entity-save.json";
            boolean su=mt.save(url);
        }

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
public void removeEntity(int pos){
        entitylist=mt.getEntities();
        mt.deleteEntity(entitylist.get(pos).groupId);
        entitylist=mt.getEntities();
        entitylist=mt.getEntities();
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