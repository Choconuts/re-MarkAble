package com.example.choconut.re_markable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.AppCompatButton;

import com.example.choconut.re_markable.R;

public class candidateButton extends android.support.v7.widget.AppCompatButton {
    private static String TAG="CandidateButton";
    private boolean isright=false;
    public String groupId;
    private boolean isChecked=false;
    private static boolean isDragging=false;
    public boolean isChecked() {
        return isChecked;
    }
    public void SetChecked(){
        this.isChecked=true;
    }
    public candidateButton(Context context, AttributeSet attrs){
        super(context,attrs,R.style.Widget_AppCompat_Button_Small_mybutton);
        setFocusable(true);
        setClickable(true);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!isPressed()){
                            setPressed(true);
                            isDragging=true;
                        }else {
                            setPressed(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isPressed()){
                            setPressed(false);

                        }else {
                            setPressed(true);

                        }
                        break;
                }
                return true;
            }

        });
        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch(what){
                    case MotionEvent.ACTION_HOVER_ENTER:  //鼠标进入view
                        if(isDragging){
                            setPressed(true);

                        }
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:  //鼠标在view上
                        System.out.println("bottom ACTION_HOVER_MOVE");
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:  //鼠标离开view
                        if(isDragging){
                            setPressed(false);
                        }
                        break;
                }
                return false;
            }
        });


    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setIsright(boolean isright) {
        this.isright = isright;
    }
}
