package com.example.dotaskillstimer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.Item;
import com.example.dotaskillstimer.ui.screens.timerScreen.MainFragment;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

public class ItemSelectDialogFragment extends DialogFragment {
    private final String TAG =this.getClass().getSimpleName();
    private RecyclerView recyclerView;
    private ItemPickerAdapter adapter;
    private View fragmentView;
    @Inject
    HeroRepository repository;
    Disposable disposable;
    private int positionOfHero;
    List<Item> itemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponentsHolder().getAppComponent().injectItemPickerDialogFragment(this);
        if (getArguments() != null) {
            positionOfHero=getArguments().getInt("positionOfHero",-1);
            itemList=getArguments().getParcelableArrayList("items");
        }

    }

    public ItemSelectDialogFragment() {
    }
    public static ItemSelectDialogFragment newInstance(Bundle arguments) {
        ItemSelectDialogFragment fragment = new ItemSelectDialogFragment();
        fragment.setArguments(arguments);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        fragmentView=getActivity().getLayoutInflater().inflate(R.layout.layout_item_picker_fragment, null);
        recyclerView=fragmentView.findViewById(R.id.item_picker_rv);
        adapter=new ItemPickerAdapter();
        logged(String.valueOf(repository==null));
        if (itemList!=null){
            adapter.setItemList(itemList);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4  ));
        recyclerView.setAdapter(adapter);
        adapter.setItemSelectListener(new OnItemSelectListener() {
            @Override
            public void onItemSelect(Item item) {
                Intent intent=new Intent();
                intent.putExtra("item",item);
                getTargetFragment().onActivityResult(positionOfHero+MainFragment.ITEM_SELECT_REQUEST_CODE, Activity.RESULT_OK,intent);
                dismiss();
            }
        });

        return new AlertDialog.Builder(getContext())
                .setView(fragmentView)
                .setTitle("Select item")
                .create();
    }
    private void logged(String message){
        Log.d(TAG,message);
    }
}
