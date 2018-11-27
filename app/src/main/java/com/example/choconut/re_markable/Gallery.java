package com.example.choconut.re_markable;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Gallery extends AppCompatActivity {
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }

        Intent intent= getIntent();
        token= intent.getStringExtra("token");
        String username=intent.getStringExtra("username");
        bt1=findViewById(R.id.entity);
        bt2=findViewById(R.id.relation);
        bt3=findViewById(R.id.loginOut);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Gallery.this);
                builder.setMessage("要怎么开始标注？");
                builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(Gallery.this,MainInterface.class);
                        intent.putExtra("token",token);
                        intent.putExtra("username",username);
                        intent.putExtra("type",1);
                        intent.putExtra("start",1);
                        Gallery.this.startActivity(intent);
                    }
                });
                builder.setNegativeButton("如果有存档，继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(Gallery.this,MainInterface.class);
                        intent.putExtra("token",token);
                        intent.putExtra("username",username);
                        intent.putExtra("type",1);
                        intent.putExtra("start",0);
                        Gallery.this.startActivity(intent);
                    }
                });
                builder.show();
            }

        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Gallery.this);
                builder.setMessage("要怎么开始标注？");
                builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(Gallery.this,Relation.class);
                        intent.putExtra("token",token);
                        intent.putExtra("username",username);
                        intent.putExtra("type",1);
                        intent.putExtra("start",1);
                        Gallery.this.startActivity(intent);
                    }
                });
                builder.setNegativeButton("如果有存档，继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(Gallery.this,Relation.class);
                        intent.putExtra("token",token);
                        intent.putExtra("username",username);
                        intent.putExtra("type",1);
                        intent.putExtra("start",0);
                        Gallery.this.startActivity(intent);
                    }
                });
                builder.show();

            }
        });


        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.setClass(Gallery.this,SetDialog.class);
                startActivity(intent1);
            }
        });

    }
}
