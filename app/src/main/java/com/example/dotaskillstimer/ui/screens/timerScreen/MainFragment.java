package com.example.dotaskillstimer.ui.screens.timerScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.Ability;
import com.example.dotaskillstimer.data.Hero;
import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.data.Item;
import com.example.dotaskillstimer.data.PrimaryAttributes;
import com.example.dotaskillstimer.ui.AspectRatioImageView;
import com.example.dotaskillstimer.ui.GlyphTimerView;
import com.example.dotaskillstimer.ui.ItemSelectDialogFragment;
import com.example.dotaskillstimer.ui.RoshanTimerView;
import com.example.dotaskillstimer.ui.TimeGameView;
import com.example.dotaskillstimer.ui.TimerImageView;
import com.example.dotaskillstimer.utils.IOnBackPressed;
import com.example.dotaskillstimer.utils.TimerState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;


public class MainFragment extends Fragment implements HeroTimerView, IOnBackPressed {
    public static final int ITEM_SELECT_REQUEST_CODE = 200;
    private static final String LIST_STATE_KEY = "recycler_list_state";
    private final String TAG = this.getClass().getSimpleName() + " ";
    private final String TagLifecycle = "Lifecycle ";
    @Inject
    HeroTimerPresenter presenter;
    private Context mContext;
    private ConstraintLayout mHeroLayout;
    private AspectRatioImageView firstHeroImageView, secondHeroImageView, thirdHeroImageView, fourthHeroImageView, fifthHeroImageView;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5;
    private LinearLayout linearLayoutsArray[];
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5;
    private ImageButton imageButtonsArray[];
    private TimerImageView buybackTimer1, buybackTimer2, buybackTimer3, buybackTimer4, buybackTimer5;
    private TimerImageView buybackTimerArray[];

    private RoshanTimerView roshanView;
    private GlyphTimerView glyphView;
    private TimeGameView timeGameView;
    private SharedPreferences sharedPreferences;
    private int abilityMinCallDownForShow;
    private Button addToCs, loadFromCs;


    private Parcelable listState;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    //region Lifecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logged(TagLifecycle + "onCreate");
        App.getInstance().getComponentsHolder().getHeroTimerComponent().inject(this);
        if (getArguments() != null) {
            for (int i = 0; i < getArguments().size(); i++) {
                HeroWithAbility hero = getArguments().getParcelable("enemyPick" + i);
                presenter.addHero(hero);
            }
        }
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String abilityMinTimePref=sharedPreferences.getString(getString(R.string.abilityMinCallDownForShow), String.valueOf(0));
        if (abilityMinTimePref != null) {
            abilityMinCallDownForShow=Integer.parseInt(abilityMinTimePref);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.layout_five_heroes, container, false);
        findViews(fragmentView);
        logged(TagLifecycle + "onCreateView");
        logged("savedInstanceState == null " + (savedInstanceState == null));
        Log.d(TAG, "presenter hash " + presenter.hashCode());
        setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
        presenter.attachView(this);
        logged("return view");
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logged(TagLifecycle + "onViewCreated");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, TagLifecycle + "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, TagLifecycle + "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TagLifecycle + "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, TagLifecycle + "onResume");


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, TagLifecycle + "onDestroyView");
        presenter.detachView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TagLifecycle + "onDestroy");
        ((App) getActivity().getApplication()).getComponentsHolder().releaseHeroTimerComponent();
        Log.d(TAG, TagLifecycle + "onDestroyView" + " isRemoving");
    }
    //endregion


    @Override
    public void showHeroWithAbility(List<HeroWithAbility> heroes) {
        logged("heroes" + heroes.size());
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mHeroLayout);
        switch (heroes.size()) {
            case 1:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).into(firstHeroImageView);
                initAbilityView(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.forty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 2:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).into(secondHeroImageView);
                initAbilityView(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.thirty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 3:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).into(thirdHeroImageView);
                initAbilityView(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.twenty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 4:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).into(thirdHeroImageView);
                Glide.with(mContext).load(heroes.get(3).getAvatar()).into(fourthHeroImageView);
                initAbilityView(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.ten_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 5:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).into(thirdHeroImageView);
                Glide.with(mContext).load(heroes.get(3).getAvatar()).into(fourthHeroImageView);
                Glide.with(mContext).load(heroes.get(4).getAvatar()).into(fifthHeroImageView);
                initAbilityView(heroes);
                break;
        }

    }

    @Override
    public void refreshAbilityCallDown(List<HeroWithAbility> heroes) {
        //     initAbilityView(heroes);

    }

    @Override
    public void showItemList(int positionOfHero, List<Item> itemListNotOnHero) {
        Bundle bundle = new Bundle();
        if (itemListNotOnHero instanceof ArrayList) {
            bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) itemListNotOnHero);
        }
        bundle.putInt("positionOfHero", positionOfHero);
        logged(positionOfHero + "  " + bundle);
        startItemSelectDialogFragment(positionOfHero, bundle);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    private void startItemSelectDialogFragment(int positionOfHero, Bundle bundle) {
        ItemSelectDialogFragment fragment = ItemSelectDialogFragment.newInstance(bundle);
        fragment.setTargetFragment(MainFragment.this, positionOfHero + ITEM_SELECT_REQUEST_CODE);
        fragment.show(getFragmentManager(), "fragment_item_pick");
    }


    private void initAbilityView(List<HeroWithAbility> heroes) {
        for (int i = 0; i < heroes.size(); i++) {
            createAbilityView(heroes, i);
            createItemView(heroes, i);
            imageButtonsArray[i].setVisibility(View.VISIBLE);
            buybackTimerArray[i].setVisibility(View.VISIBLE);
        }
    }

    private void createItemView(List<HeroWithAbility> heroes, int position) {
        for (int i = 0; i < heroes.get(position).getItemList().size(); i++) {
            logged("createItemView");
            Item item = heroes.get(position).getItemList().get(i);
            TimerImageView imageView = createTimerImageView(item.getIcon());
            imageView.setTag(item.getName());
            int childCount = linearLayoutsArray[position].getChildCount();
            linearLayoutsArray[position].addView(imageView, childCount - 1);
            imageView.setTimerDuration(item.getCallDownWithReduction());
        }
    }

    private void createAbilityView(List<HeroWithAbility> heroes, int position) {
        for (int i = 0; i < heroes.get(position).getAbilities().size(); i++) {
            Ability ability = heroes.get(position).getAbilities().get(i);
            if(ability.getCallDown()>=abilityMinCallDownForShow){
                TimerImageView imageView = createTimerImageView(ability.getIcon());
                imageView.setTag(ability.getName());
                int childCount = linearLayoutsArray[position].getChildCount();
                linearLayoutsArray[position].addView(imageView, childCount - 1);
                imageView.setTimerDuration(ability.getCallDownWithReduction());
            }
        }
    }

    private void setAbilityTimer(List<TimerImageView> abilityImageList, List<Ability> abilities) {
        for (int i = 0; i < abilities.size(); i++) {
            abilityImageList.get(i).setTimerDuration(abilities.get(i).getCallDownWithReduction());
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedState) {
        logged(TagLifecycle + "onViewStateRestored");
        super.onViewStateRestored(savedState);
        if (savedState != null) {
            for (int i = 0; i < linearLayoutsArray.length; i++) {
                for (int j = 0; j < linearLayoutsArray[i].getChildCount(); j++) {
                    if (linearLayoutsArray[i].getChildAt(j) instanceof TimerImageView) {
                        TimerImageView view = (TimerImageView) linearLayoutsArray[i].getChildAt(j);
                        TimerState timerState = savedState.getParcelable("TimerImageView_" + i + "_" + j);
                        if (timerState != null) {
                            view.setTimerState(timerState);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Item item = data.getParcelableExtra("item");
        ;
        switch (requestCode) {
            case ITEM_SELECT_REQUEST_CODE:
                presenter.onSelectItem(0, item);
                addItemToHero(0, item);
                break;
            case ITEM_SELECT_REQUEST_CODE + 1:
                presenter.onSelectItem(1, item);
                addItemToHero(1, item);
                break;
            case ITEM_SELECT_REQUEST_CODE + 2:
                presenter.onSelectItem(2, item);
                addItemToHero(2, item);
                break;
            case ITEM_SELECT_REQUEST_CODE + 3:
                presenter.onSelectItem(3, item);
                addItemToHero(3, item);
                break;
            case ITEM_SELECT_REQUEST_CODE + 4:
                presenter.onSelectItem(4, item);
                addItemToHero(4, item);
                break;
        }
    }

    private void addItemToHero(int positionOfHero, @Nullable Item item) {
        if (item != null) {
            TimerImageView imageView = createTimerImageView(item.getIcon());
            int childCount = linearLayoutsArray[positionOfHero].getChildCount();
            imageView.setTag(item.getName());
            linearLayoutsArray[positionOfHero].addView(imageView, childCount - 1);
            imageView.setTimerDuration(item.getCallDownWithReduction());
        }
    }

    private TimerImageView createTimerImageView(String uri) {
        TimerImageView imageView = new TimerImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startTimer();
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, view);
                //inflating menu from xml resource
                popup.inflate(R.menu.hero_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.hero_menu_hide:
                                removeView(view);
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return true;
            }
        });
        Glide.with(mContext).load(uri).into(imageView);
        return imageView;
    }

    private void removeView(View view) {
        ViewParent parentView =view.getParent();
        if (!(parentView instanceof ViewGroup)){
            throw new IllegalStateException("View does not have a parent, it cannot be rootview!");
        }else {
            ViewGroup parent = (ViewGroup) parentView;
            logged(String.valueOf(view.getTag()));
            parent.removeView(view);
            presenter.onItemRemoved(view.getTag().toString());
        }
    }

    private void findViews(View fragmentView) {
        mHeroLayout = fragmentView.findViewById(R.id.heroes_layout);
        firstHeroImageView = fragmentView.findViewById(R.id.image_view_heroes_1);
        secondHeroImageView = fragmentView.findViewById(R.id.image_view_heroes_2);
        thirdHeroImageView = fragmentView.findViewById(R.id.image_view_heroes_3);
        fourthHeroImageView = fragmentView.findViewById(R.id.image_view_heroes_4);
        fifthHeroImageView = fragmentView.findViewById(R.id.image_view_heroes_5);

        linearLayout1 = fragmentView.findViewById(R.id.linear_layout_1);
        linearLayout2 = fragmentView.findViewById(R.id.linear_layout_2);
        linearLayout3 = fragmentView.findViewById(R.id.linear_layout_3);
        linearLayout4 = fragmentView.findViewById(R.id.linear_layout_4);
        linearLayout5 = fragmentView.findViewById(R.id.linear_layout_5);
        linearLayoutsArray = new LinearLayout[]{
                linearLayout1,
                linearLayout2,
                linearLayout3,
                linearLayout4,
                linearLayout5
        };
        imageButton1 = fragmentView.findViewById(R.id.add_image_button_1);
        imageButton2 = fragmentView.findViewById(R.id.add_image_button_2);
        imageButton3 = fragmentView.findViewById(R.id.add_image_button_3);
        imageButton4 = fragmentView.findViewById(R.id.add_image_button_4);
        imageButton5 = fragmentView.findViewById(R.id.add_image_button_5);
        imageButtonsArray = new ImageButton[]{
                imageButton1,
                imageButton2,
                imageButton3,
                imageButton4,
                imageButton5
        };
        buybackTimer1 = fragmentView.findViewById(R.id.buyback_image_1);
        buybackTimer2 = fragmentView.findViewById(R.id.buyback_image_2);
        buybackTimer3 = fragmentView.findViewById(R.id.buyback_image_3);
        buybackTimer4 = fragmentView.findViewById(R.id.buyback_image_4);
        buybackTimer5 = fragmentView.findViewById(R.id.buyback_image_5);
        buybackTimerArray=new TimerImageView[]{
                 buybackTimer1,
                 buybackTimer2,
                 buybackTimer3,
                 buybackTimer4,
                 buybackTimer5
        };
        for (TimerImageView view:buybackTimerArray){

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.startTimer();
                }
            });
        }


        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemAddClick(0);
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemAddClick(1);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemAddClick(2);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemAddClick(3);
            }
        });
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemAddClick(4);
            }
        });
        roshanView = fragmentView.findViewById(R.id.roshan_view);
        roshanView.setOnClickListener(v ->
        {
            Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
            roshanView.startTimer();
        });
        glyphView = fragmentView.findViewById(R.id.glyph_view);
        glyphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glyphView.startTimer();
            }
        });

        timeGameView = fragmentView.findViewById(R.id.time_game_view);
        timeGameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                presenter.addItemToDb();
//                presenter.addHeroToDB();
//                presenter.addAbilityToDB();

            }
        });
        firstHeroImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.applyCallDawnReduction(1, 0.25f);
                Toast.makeText(getContext(), "applyCallDawnReduction", Toast.LENGTH_SHORT).show();
            }
        });

        addToCs=fragmentView.findViewById(R.id.add_to_cloudstore_button);
        loadFromCs=fragmentView.findViewById(R.id.load_from_cs_button);
        addToCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  presenter.addToCs();
                presenter.addHeroToDB();
            }
        });
        loadFromCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

   //             presenter.loadFromCs();
                presenter.addAbilityToDB();
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        for (int i = 0; i < linearLayoutsArray.length; i++) {
            for (int j = 0; j < linearLayoutsArray[i].getChildCount(); j++) {
                if (linearLayoutsArray[i].getChildAt(j) instanceof TimerImageView) {
                    TimerImageView view = (TimerImageView) linearLayoutsArray[i].getChildAt(j);
                    outState.putParcelable("TimerImageView_" + i + "_" + j, view.getTimerState());
                }
            }
        }

    }

    private void logged(String message) {
        Log.d(TAG, message);
    }


//region ADD HEROES

    private void initStartHeroes() {
        Hero abbadon = new Hero(
                "Abaddon",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/2/26/Abaddon_icon.png?version=1b9f117778a2c6daaf4a28cc7920a547",
                PrimaryAttributes.Strength);
        Hero alchemist = new Hero(
                "Alchemist",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Alchemist_icon.png?version=a7c13bea85d68e59c5400c1bd99d1cfb",
                PrimaryAttributes.Strength);
        Hero brewmaster = new Hero(
                "Brewmaster",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1e/Brewmaster_icon.png?version=c0906f71595d5e6917c14b54cd1821d3",
                PrimaryAttributes.Strength);
        Hero bristleback = new Hero(
                "Bristleback",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Bristleback_icon.png?version=77ce3fefc668df38a8c90268dc5bcd8b",
                PrimaryAttributes.Strength);
//        heroRepository.addHero(bristleback);
        Hero antimage = new Hero(
                "Anti-Mage",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Anti-Mage_icon.png?version=f55b1d31df2c842ebd3e6121f4dc513d",
                PrimaryAttributes.Agility);
//        heroRepository.addHero(antimage);
//        Hero arc_warden = new Hero(
//                "Arc Warden",
//                R.drawable.arc_warden_icon,
//                PrimaryAttributes.Agility);

//        mRepository.addHero(abbadon);
//        mRepository.addHero(alchemist);
//        mRepository.addHero(brewmaster);
//        mRepository.addHero(arc_warden);


    }

    private void addNewHero() {
        Hero arc_warden = new Hero(
                "Arc Warden",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/0/07/Arc_Warden_icon.png?version=7ef22642fb6a42d09bc39601f19818ab",
                PrimaryAttributes.Agility);
//        mRepository.addHero(arc_warden);
//        mRepository.addAbility(new Ability("Flux", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/03/Flux_icon.png?version=f47a65a2a7503d4a89304f72a3ff859b", "Arc Warden"));
//        mRepository.addAbility(new Ability("Magnetic Field", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/db/Magnetic_Field_icon.png?version=d261ee42e61f37658a8ee0ad19bd14fe", "Arc Warden"));
//        mRepository.addAbility(new Ability("Spark Wraith", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/eb/Spark_Wraith_icon.png?version=4544dd795607317a5baa0494a01cf8f7", "Arc Warden"));
//        mRepository.addAbility(new Ability("Tempest Double", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Tempest_Double_icon.png?version=bb698f17090538509315b0604f767fd6", "Arc Warden"));
    }

    private void createAbilities() {

//        mRepository.addAbility(new Ability("Mist Coil", 4500, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ce/Mist_Coil_icon.png?version=40879d4ec75feb9c1a1a5eb9dbe5b74d", "Abaddon"));
//        mRepository.addAbility(new Ability("Aphotic Shield", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b1/Aphotic_Shield_icon.png?version=3bc42248f1910086219c446d27cd5ac9", "Abaddon"));
//        mRepository.addAbility(new Ability("Curse of Avernus", 0, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Curse_of_Avernus_icon.png?version=87f3cab829e59844e9a9161d6f0ec050", "Abaddon"));
//        mRepository.addAbility(new Ability("Borrowed Time", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/78/Borrowed_Time_icon.png?version=892a864f5a94d0d9860cfdffa9de4c7a", "Abaddon"));

//        mRepository.addAbility(new Ability("Acid Spray", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6b/Acid_Spray_icon.png?version=cee52fb70ac4852e313dc6b13ecdcb6b", "Alchemist"));
//        mRepository.addAbility(new Ability("Unstable Concoction", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/96/Unstable_Concoction_icon.png?version=8155ae48d0d5be01ac6c5e54c0e8d85b", "Alchemist"));
//        mRepository.addAbility(new Ability("Grevil's Greed", 0, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/94/Greevil%27s_Greed_icon.png?version=7f63e88c698157cac11caec241b03760", "Alchemist"));
//        mRepository.addAbility(new Ability("Chemical Rage", 55000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Chemical_Rage_icon.png?version=067bfe8dcde0b80aabb3a0c1e4cf868c", "Alchemist"));

//        mRepository.addAbility(new Ability("Thunder Clap", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b3/Thunder_Clap_icon.png?version=725d4e332ef6b40933bf3e2ff22815d4", "Brewmaster"));
//        mRepository.addAbility(new Ability("Cinder Brew", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cf/Cinder_Brew_icon.png?version=d7e582dded19d83f2c30c968fe569652", "Brewmaster"));
//        mRepository.addAbility(new Ability("Drunken Brawler", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d9/Drunken_Brawler_icon.png?version=1a0302e7abfb939edda812f7919140cc", "Brewmaster"));
//        mRepository.addAbility(new Ability("Primal Split", 55000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1f/Primal_Split_icon.png?version=1eed47663b15eee0ba162d4fd16ce18a", "Brewmaster"));

//        heroRepository.addAbility(new Ability("Viscous Nasal Goo", 1500, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Viscous_Nasal_Goo_icon.png?version=967acf692391e4bbe03a53645b0f1e77", "Bristleback"));
//        heroRepository.addAbility(new Ability("Quill Spray", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/15/Quill_Spray_icon.png?version=f156738db0bd1eddda5b813bdb8c5ca5", "Bristleback"));
//        heroRepository.addAbility(new Ability("Bristleback", 0, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Bristleback_ability_icon.png?version=00f4a50dc9b838f152af100020cbd355", "Bristleback"));
//        heroRepository.addAbility(new Ability("Warpath", 0, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Warpath_icon.png?version=ab11670118b2ab14ff77c0f959130bd0", "Bristleback"));
//
//        heroRepository.addAbility(new Ability("Mana Break", 0, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c1/Mana_Break_icon.png?version=3a030100029453e8532750495bde6db6", "Anti-Mage"));
//        heroRepository.addAbility(new Ability("Blink", 3500, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ce/Blink_%28Anti-Mage%29_icon.png?version=004bc9712d7f71db5f6d0d576ba58b16", "Anti-Mage"));
//        heroRepository.addAbility(new Ability("Counterspell", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/90/Counterspell_icon.png?version=ed81bed3061fb4bfec6b286170ddfa0a", "Anti-Mage"));
//        heroRepository.addAbility(new Ability("Mana Void", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Mana_Void_icon.png?version=4048b461564657beda74e49ecd2e3d21", "Anti-Mage"));

//        mRepository.addAbility( new Ability("Flux", 16000, R.drawable.flux_icon, "Arc Warden"));
//        mRepository.addAbility( new Ability("Magnetic Field", 20000, R.drawable.magnetic_field_icon, "Arc Warden"));
//        mRepository.addAbility( new Ability("Spark Wraith", 4000, R.drawable.spark_wraith_icon, "Arc Warden"));
//        mRepository.addAbility( new Ability("Tempest Double", 40000, R.drawable.tempest_double_icon, "Arc Warden"));
    }

    @Override
    public void onBackPressed() {
        presenter.applyCallDawnReduction(1, 0);

    }


    //endregion
}
