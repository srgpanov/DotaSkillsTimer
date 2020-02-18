package com.example.dotaskillstimer.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.Ability;
import com.example.dotaskillstimer.data.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.AbilityHolder> {
    private List<Ability> abilityList= new ArrayList<>();
    private List<Item> itemList = new ArrayList<>();
    private OnItemHeroClick clickListener;
    private Context context;

    @NonNull
    @Override
    public AbilityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_ability_holder,parent,false);
        return new AbilityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbilityHolder holder, int position) {
        Ability ability =abilityList.get(position);
        Glide.with(context)
                .load(ability.getIcon())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return abilityList.size();
    }
    public void addAbilities(List<Ability>  abilities){
        abilityList.addAll(abilities);
        notifyDataSetChanged();
    }
    public void setClickListener(OnItemHeroClick clickListener) {
        this.clickListener = clickListener;
    }
     class AbilityHolder extends RecyclerView.ViewHolder {
        private TimerImageView imageView;
        public AbilityHolder(@NonNull View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.item_ability);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                    imageView.setTimerDuration(abilityList.get(getAdapterPosition()).getCallDownWithReduction());
                    imageView.startTimer();
                }
            });

        }

     }
    private class ItemHolder extends RecyclerView.ViewHolder{

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
