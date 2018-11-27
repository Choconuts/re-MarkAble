package com.example.choconut.re_markable;

import android.content.ClipDescription;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.AppCompatButton;

import com.example.choconut.re_markable.R;

public class candidate_Button extends android.support.v7.widget.AppCompatButton {
    private static String TAG="CandidateButton";
    private boolean isright=false;
    private boolean isfocus=false;
    public String groupId;
    private boolean isChecked=false;
    private static int firstPressedId=-1;
    private Relation mi;

    public boolean isIsfocus() {
        return isfocus;
    }

    public void setIsfocus(boolean isfocus) {
        this.isfocus = isfocus;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void SetChecked(){
        this.isChecked=true;
    }
    public String getGroupId(){return groupId;}
    public void setActivity(Relation mi){
        this.mi=mi;
    }
    public candidate_Button(Context context, AttributeSet attrs){
        super(context,attrs,R.style.Widget_AppCompat_Button_Small_mybutton);
        setFocusable(true);
        setClickable(true);
        setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setPressed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(mi.isInMark){
                            if(mi.lEntiy==-1){
                                mi.lEntiy=mi.getNumByID(groupId);
                                setSelected(true);
                            }
                            else if(mi.rEntiy==-1){
                                mi.rEntiy=mi.getNumByID(groupId);
                                setEnabled(false);
                                mi.addTriple();
                            }
                        }
                        else{
                            boolean flag=false;
                            if(isPressed()){
                                if(mi.left==-1){
                                    setTouch();
                                    mi.setLeft(mi.getNumByID(groupId));
                                    flag=true;
                                }
                                else
                                if(mi.right==-1){
                                    mi.setRight(mi.getNumByID(groupId));

                                    mi.connectlr();
                                }
                                else{
                                    mi.connectlr();
                                    mi.setRight(mi.getNumByID(groupId));
                                    mi.connectlr();
                                }

                                if(mi.left==mi.getNumByID(groupId)&&!flag){
                                    setTouch();
                                    mi.setLeft(-1);
                                    mi.setRight(-1);
                                }
                            }
                        }
                        break;

                    case  MotionEvent.ACTION_OUTSIDE:
                        setPressed(false);
                        break;
                }
                return false;
            }

        });
        setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED: // 拖拽开始
                        return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                    case DragEvent.ACTION_DRAG_ENTERED: // 被拖拽View进入目标区域
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION: // 被拖拽View在目标区域移动
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED: // 被拖拽View离开目标区域
                        return true;
                    case DragEvent.ACTION_DROP: // 放开被拖拽View
                        String content = event.getClipData().getItemAt(0).getText().toString(); //接收数据
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED: // 拖拽完成
                        return true;
                }
                return false;
            }
        });




    }

    private boolean inRangeOfView(View view, MotionEvent ev){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return !(ev.getX() < x) && !(ev.getX() > (x + view.getWidth())) && !(ev.getY() < y) && !(ev.getY() > (y + view.getHeight()));
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setIsright(boolean isright) {
        this.isright = isright;
    }



    public void setTouch(){
        if(isIsfocus()){
            setBackgroundResource(R.drawable.buttombg);
            setIsfocus(false);
        }
        else {
            setBackgroundResource(R.drawable.buttonbg1);
            setIsfocus(true);
        }
    }

}
