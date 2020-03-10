package com.srgpanov.dotaskillstimer.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.ui.views.AspectRatioImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeroListAdapter extends RecyclerView.Adapter<HeroListAdapter.HeroItem> {
    private OnItemHeroClick clickListener;
    private List<HeroWithAbility> heroList;
    private Context context;

    public void setClickListener(OnItemHeroClick clickListener) {
        this.clickListener = clickListener;
    }



    public HeroListAdapter(List<HeroWithAbility> heroes) {
        heroList = new ArrayList<>(heroes);
    }

    public List<HeroWithAbility> getHeroList() {
        return heroList;
    }

    public HeroWithAbility getItemAtPosition(int position){
        return heroList.get(position);
    }
    public int getPositionOfHero(HeroWithAbility hero){
        return heroList.indexOf(hero);
    }
    public void addHeroes(List<HeroWithAbility>  heroes){
        heroList.addAll(heroes);
        notifyDataSetChanged();
    }
    public void addHero(HeroWithAbility  hero){
        heroList.add(hero);

        Log.d("log", heroList.indexOf(hero)+String.valueOf(hero));
        notifyItemInserted(heroList.indexOf(hero));
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
    public void removeItem(HeroWithAbility hero){
        notifyItemRemoved(heroList.indexOf(hero));
        heroList.remove(hero);
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
        holder.bind(position);
    }



    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public class HeroItem extends RecyclerView.ViewHolder implements View.OnClickListener{

        private AspectRatioImageView imageView;
        public HeroItem(@NonNull View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.item_hero_list);
        }
        void bind(int position){
            imageView.setOnClickListener(this);
            HeroWithAbility hero =heroList.get(position);
            Glide.with(context)
                    .load(hero.getAvatar())
                    .placeholder(R.drawable.ic_image_24dp)
                    .error(R.drawable.ic_broken_image_48px)
                    .into(imageView);
        }
        @Override
        public void onClick(View view) {
            if(clickListener !=null){
                clickListener.onItemClick(getAdapterPosition(),view);
            }
        }



    }

}
