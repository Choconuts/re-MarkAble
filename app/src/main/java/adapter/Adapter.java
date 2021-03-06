package adapter;
import android.content.Context;
import android.content.Entity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.choconut.re_markable.MainInterface;
import com.example.choconut.re_markable.R;
import com.example.choconut.re_markable.Relation;
import com.example.choconut.re_markable.Utils;
import com.example.choconut.re_markable.SlidingButton;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private static final String TAG = Adapter.class.getSimpleName();

    //图标数组
    private int[] icons = {
            R.drawable.icon1, R.drawable.icon2

    };

    private Context lContext;   //上下文

    //图标集合
    private List<Integer> listIcon = new ArrayList<Integer>();

    //信息集合
    private LinkedList<String> infos;

    //MyViewHolder集合
    private List<MyViewHolder> sbViews = new ArrayList<MyViewHolder>();

    private int type;

    public Adapter(Context context, LinkedList<String> contents,int type) {
        this.type=type;
        lContext = context;
        //设置菜单行数与行内图标、名称、信息
        for (int i = 0; i < 2; i++) {
            listIcon.add(icons[i]);
        }
        infos=contents;
    }


    @Override
    public int getItemCount() {
        return infos.size();
    }

    /**
     * 设置列表菜单中子项所显示的内容
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        sbViews.add(holder);
        Log.d(TAG,"onBindViewHolder====================");
        //设置图标
        if(type==0){
            holder.img.setBackgroundResource(listIcon.get(0));
        }
        else {
            holder.img.setBackgroundResource(listIcon.get(1));
        }


        //设置信息
        holder.info.setText(infos.get(position));
        //设置内容布局的宽为屏幕宽度
        holder.layout_content.getLayoutParams().width = Utils.getScreenWidth(lContext);

        //删除按钮的事件，单击后删除整行
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();     //获取要删除行的位置
                removeData(n);                          //删除列表中的子项
            }
        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();     //获取要高亮行的位置
                heilightthis(n);                          //删除列表中的子项
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();     //获取要高亮行的位置
                heilightthis(n);                          //删除列表中的子项
            }
        });

        holder.sbView.smoothScrollTo(0,0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        Log.d(TAG,"onCreateViewHolder====================");
        //获取列表中，每行的布局文件
        View view=LayoutInflater.from(lContext).inflate(R.layout.layout_item, arg0, false);
        if(type==0){
        }
        else {
            view = LayoutInflater.from(lContext).inflate(R.layout.layout_item2, arg0, false);
        }

        MyViewHolder holder = new MyViewHolder(view);           //

        return holder;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        public SlidingButton sbView;
        public TextView btn_Delete;
        public TextView info;
        public ImageView img;
        public ViewGroup layout_content;

        //获取相关控件
        public MyViewHolder(View itemView) {
            super(itemView);
            sbView = (SlidingButton) itemView.findViewById(R.id.sb_view);
            btn_Delete = (TextView) itemView.findViewById(R.id.tv_delete);
            info = (TextView) itemView.findViewById(R.id.info);
            img = (ImageView) itemView.findViewById(R.id.img);
            layout_content = (ViewGroup) itemView.findViewById(R.id.layout_content);
            Log.d(TAG,"MyViewHolder====================");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void removeData(int position) {
        infos.remove(position);
        notifyItemRemoved(position);    //删除列表
        if(type==0){
            MainInterface mi=(MainInterface)lContext;
            mi.removeEntity(position);
        }
        else{
            Relation mi=(Relation) lContext;
            mi.removeTriple(position);
        }

    }
    public void heilightthis(int position){

        if(type==0){
            MainInterface mi=(MainInterface)lContext;
            mi.hightlightentity(position);
        }
        else{
            Relation mi=(Relation) lContext;
            mi.hightlightTriple(position);
        }


    }

}
