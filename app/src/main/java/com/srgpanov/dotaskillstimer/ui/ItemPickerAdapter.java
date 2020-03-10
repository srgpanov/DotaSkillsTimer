package com.srgpanov.dotaskillstimer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemPickerAdapter extends RecyclerView.Adapter<ItemPickerAdapter.ItemHolder> {
    private Context context;
    private OnItemSelectListener itemSelectListener;
    List<Item> itemList=new ArrayList<>();

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.item_item_holder,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Item item=itemList.get(position);
        Glide.with(context)
                .load(item.getIcon())
                .placeholder(R.drawable.ic_image_24dp)
                .error(R.drawable.ic_broken_image_48px)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void setItemSelectListener(OnItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.item_image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectListener!=null) {
                        itemSelectListener.onItemSelect(itemList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
