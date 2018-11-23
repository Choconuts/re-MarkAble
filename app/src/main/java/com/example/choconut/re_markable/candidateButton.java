package com.example.choconut.re_markable;

import android.content.ClipDescription;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
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
    private static int firstPressedId=-1;
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
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setPressed(true);

                        break;
                    case MotionEvent.ACTION_UP:
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
}
