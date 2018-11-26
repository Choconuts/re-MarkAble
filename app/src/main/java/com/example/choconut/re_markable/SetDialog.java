package com.example.choconut.re_markable;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class SetDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除这个Activity的标题栏
        setContentView(R.layout.activity_set_dialog);

        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的1.0---去除titlebar高度
        p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的0.8
        p.alpha = 1.0f;      //设置本身透明度
        p.dimAmount = 0.0f;      //设置黑暗度
        p.gravity=Gravity.BOTTOM;
        getWindow().setAttributes(p);


        super.onCreate(savedInstanceState);

    }
}
