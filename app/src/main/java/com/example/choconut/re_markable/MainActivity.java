package com.example.choconut.re_markable;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;



public class MainActivity extends AppCompatActivity {
    UserHelper uh;
    Button login;
    Button sign;
    Button sub;
    EditText username;
    EditText pw;
    EditText email;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handl=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                popupWindow.dismiss();
                if(msg.what==1)
                {
                    new AlertDialog.Builder(MainActivity.this).setMessage(msg.obj.toString()).setPositiveButton("yes",null).show();
                    String notice=UserHelper.getMsg(msg);
                    String success="登录成功";
                    new AlertDialog.Builder(MainActivity.this).setMessage(notice).setPositiveButton("yes",null).show();
                    if(notice.equals(success)){
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this,Gallery.class);
                        intent.putExtra("token",UserHelper.getToken(msg));
                        intent.putExtra("username",username.getText().toString());
                        MainActivity.this.startActivity(intent);
                    }

                }
                else{
                    new AlertDialog.Builder(MainActivity.this).setMessage("登录超时，请检查网络状态").setPositiveButton("yes",null).show();

                }
                super.handleMessage(msg);
                }
            };


        uh = new UserHelper(handl);
        login=findViewById(R.id.loginIn);
        sign=findViewById(R.id.signIn);
        sub=findViewById(R.id.subsign);
        username=findViewById(R.id.UseName);
        pw=findViewById(R.id.password);
        email=findViewById(R.id.Email);
        username.clearFocus();
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email.setVisibility(View.VISIBLE);
                sub.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                sign.setVisibility(View.GONE);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=username.getText().toString();
                String b=pw.getText().toString();
                makebar();
                uh.signIn(a,b);
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=username.getText().toString();
                String b=pw.getText().toString();
                String c=email.getText().toString();
                makebar();
                uh.login(a,b,c);
            }
        });
    }

    private void enterin(){
        Intent intent = new Intent(this,MainInterface.class);
        startActivity(intent);
    }
    private void makebar(){
        popupWindow = new PopupWindow();
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        View view = LayoutInflater.from(this).inflate(R.layout.progressbar,null);
        popupWindow.setContentView(view);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);

    }

}
