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
    private boolean isSelected=false;
    private boolean isChecked=false;
    public boolean isChecked() {
        return isChecked;
    }
    public boolean isSelected() {
        return isSelected;
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
                        if(!isChecked){
                            setPressed(true);
                            isChecked=true;
                        }else {
                            setPressed(false);
                            isChecked=false;
                        }
                        break;
                }
                return true;
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
