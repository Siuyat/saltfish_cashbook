package com.example.ruanx.cashbook.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruanx.cashbook.Model.Item;
import com.example.ruanx.cashbook.R;

import java.util.List;

/**
 * Created by SIRIUS on 2017/12/22.
 */

public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private List<Item> mItemList;
    private Context context;
    private RecyclerViewOnItemClickListener onItemClickListener;
    private RecyclerViewOnItemLongClickListener onItemLongClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView labelTV;
        TextView sutypeTV;
        TextView moneyTV;
        TextView itemdateTV;
        TextView itemcommentTV;
        ImageView itemImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            labelTV = (TextView) view.findViewById(R.id.label_tv);
            moneyTV = (TextView) view.findViewById(R.id.money_tv);
            itemdateTV = (TextView)view.findViewById(R.id.itemdate_tv);
            sutypeTV=(TextView)view.findViewById(R.id.subtyp_tv);
            itemcommentTV=(TextView)view.findViewById(R.id.itemcomment_tv);
            itemImage=(ImageView)view.findViewById(R.id.main_item_image);
        }
    }

    public ItemAdapter(List<Item> itemList) {
        mItemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        ViewHolder holder = new ViewHolder(view);
        //设置点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item i = mItemList.get(position);
        holder.labelTV.setText(i.getLabel());
        holder.moneyTV.setText(Double.toString(i.getMoney()));
        holder.itemdateTV.setText(""+i.getYear()+"-"+i.getMonth()+"-"+i.getDay());
        holder.sutypeTV.setText(i.getSubtype());
        holder.itemcommentTV.setText(""+i.getComments());
        Log.d("sutypeTV", "onBindViewHolder: "+i.getSubtype());
        if(i.getLabel().equals("居家生活")){
            holder.itemImage.setImageResource(R.drawable.ic_action_sun);
        }else if (i.getLabel().equals("餐饮酒水")){
            holder.itemImage.setImageResource(R.drawable.ic_action_restaurant);
        }else if (i.getLabel().equals("交通出行")){
            holder.itemImage.setImageResource(R.drawable.ic_action_car);
        }else if (i.getLabel().equals("休闲娱乐")){
            holder.itemImage.setImageResource(R.drawable.ic_action_fun);
        }else if (i.getLabel().equals("健康医疗")){
            holder.itemImage.setImageResource(R.drawable.ic_action_health);
        }else if (i.getLabel().equals("收入")){
            holder.itemImage.setImageResource(R.drawable.ic_action_money);
        }else if (i.getLabel().equals("文化教育")){
            holder.itemImage.setImageResource(R.drawable.ic_action_book);
        }else if (i.getLabel().equals("其他支出")){
            holder.itemImage.setImageResource(R.drawable.ic_action_sun);
        }else if (i.getLabel().equals("购物消费")){
            Log.d("购物消费", "onBindViewHolder: "+i.getSubtype());
            holder.itemImage.setImageResource(R.drawable.ic_action_shop);
        }
        holder.mView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //!!用getTag方法获取数据
            onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return onItemLongClickListener != null && onItemLongClickListener.onItemLongClickListener(v, (Integer) v.getTag());
    }

    /*  删除条目 */
    public void removeItem(int position) {
        mItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mItemList.size()-position);
    }

    /* 设置点击事件 */
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    /* 设置长按事件 */
    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    //定义接口
    public interface RecyclerViewOnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public interface RecyclerViewOnItemLongClickListener {
        boolean onItemLongClickListener(View view, int position);
    }


}
