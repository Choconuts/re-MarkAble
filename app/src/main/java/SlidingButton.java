import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.example.choconut.re_markable.R;

public class SlidingButton extends HorizontalScrollView {

    private TextView lTextView_Delete;      //删除按钮
    private int lScrollWidth;               //横向滑动的范围
    private Boolean first = false;            //标记第一次进入获取删除按钮控件

    //记录是否显示删除按钮
    public Boolean isShowDelButton = false;


    public SlidingButton(Context context) {
        this(context, null);
    }

    public SlidingButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //第一次进入获取删除按钮控件
        if(!first){
            lTextView_Delete = (TextView) findViewById(R.id.tv_delete);
            first = true;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //默认隐藏删除按钮
        if(changed){
            this.scrollTo(0,0);
            //获取水平滚动条可以滑动的范围，即右侧按钮的宽度
            lScrollWidth = lTextView_Delete.getWidth();
        }

    }

    /**
     *滑动手指抬起时的手势判断
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeScrollx();            //根据滑动距离判断是否显示删除按钮
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }



    /**
     * 根据滑动距离判断是否显示删除按钮
     */
    public void changeScrollx(){
        //触摸滑动的距离大于删除按钮宽度的一半
        if(getScrollX() >= (lScrollWidth/2)){
            //显示删除按钮
            this.smoothScrollTo(lScrollWidth, 0);
            isShowDelButton = true;
        }else{
            //隐藏删除按钮
            this.smoothScrollTo(0, 0);
            isShowDelButton = false;
        }
    }

    /**
     * 恢复原状
     */
    public void reset(){
        //隐藏删除按钮
        this.smoothScrollTo(0, 0);
    }


}
