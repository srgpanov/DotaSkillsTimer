package com.example.dotaskillstimer.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.HeroWithAbility;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeroListAdapter extends RecyclerView.Adapter<HeroListAdapter.HeroItem> {
    private onItemHeroClick clickListener;
    private List<HeroWithAbility> heroList;
    private Context context;

    public void setClickListener(onItemHeroClick clickListener) {
        this.clickListener = clickListener;
    }



    public HeroListAdapter(List<HeroWithAbility> heroes) {
        heroList = heroes;
    }

    public List<HeroWithAbility> getHeroList() {
        return heroList;
    }

    public HeroWithAbility getItemAtPosition(int position){
        Log.d("adapter",heroList.get(position).getName());
        return heroList.get(position);
    }
    public int getPositionOfHero(HeroWithAbility hero){
        return heroList.indexOf(hero);
    }
    public void addHeroes(List<HeroWithAbility>  heroes){
        heroList.addAll(heroes);
        notifyDataSetChanged();
    }
    public void clearData(){
        heroList.clear();
        notifyDataSetChanged();
    }
    public void removeItem(int position){
        if(heroList.size()<position)return;
        heroList.remove(position);
        notifyItemRemoved(position);
    }


    @NonNull
    @Override
    public HeroItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_hero_list_frag,parent,false);
        return new HeroItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroItem holder, int position) {
        HeroWithAbility hero =heroList.get(position);
        Glide.with(context)
                .load(hero.getAvatar())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    class HeroItem extends RecyclerView.ViewHolder implements View.OnClickListener{

        private AspectRatioImageView imageView;
        public HeroItem(@NonNull View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.item_hero_list);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                clickListener.onHeroItemClick(getAdapterPosition());
            }
        }


    }
    public interface onItemHeroClick{
        void onHeroItemClick(int position);
    }
}
