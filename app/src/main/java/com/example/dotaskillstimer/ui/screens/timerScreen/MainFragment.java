package com.example.dotaskillstimer.ui.screens.timerScreen;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.Ability;
import com.example.dotaskillstimer.data.Hero;
import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.data.PrimaryAttributes;
import com.example.dotaskillstimer.ui.TimerImageView;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;


public class MainFragment extends Fragment implements HeroTimerView {
    private final String TAG = this.getClass().getSimpleName() + " ";
    private final String TagLifecycle = "Lifecycle ";
    @Inject
    HeroTimerPresenter presenter;
    private Context mContext;
    private ConstraintLayout mHeroLayout;
    private Guideline mGuideline_1;
    private Guideline mGuideline_2;
    private Guideline mGuideline_3;
    private Guideline mGuideline_4;
    private Guideline mGuideline_5;
    private Guideline mGuideline_6;
    private Button mButtonAddHero;
    private Button mButtonAddAbility;
    private Button mButtonNewHero;
    private Button mButtonAddHeroCloudStore;
    private Button mButtonGetHeroCloudStore;
    private Button mButtonGetAbilityCloudStore;
    private Button mButtonAddAbilityCloudStore;
    private int last_added_view_id;
    private int heroCount;
    private int leftConstraint = 0;
    private int rightConstraint = 0;
    private TimerImageView timerImageView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponentsHolder().getHeroTimerComponent().inject(this);
        if (getArguments() != null) {
            SparseArray sparseArray = getArguments().getSparseParcelableArray("enemyPick");
            for (int i = 0; i < sparseArray.size(); i++) {
                HeroWithAbility hero = (HeroWithAbility) sparseArray.get(i);
                presenter.addHero(hero);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.layout_five_heroes, container, false);
        findViews(fragmentView);
        Log.d(TAG, "presenter hash " + presenter.hashCode());
        setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
        postponeEnterTransition();
        mButtonAddHero.setOnClickListener(v -> {
                    Log.d(TAG, "add");
                    initStartHeroes();
                }
        );

        mButtonAddAbility.setOnClickListener(v ->
                createAbilities()
        );
        mButtonNewHero.setOnClickListener(v ->
                addNewHero());
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
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
        if (isRemoving()) {
            ((App) getActivity().getApplication()).getComponentsHolder().releaseHeroTimerComponent();
            Log.d(TAG, TagLifecycle + "onDestroyView" + " isRemoving");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, TagLifecycle + "onDestroy");

    }

    private void createView(HeroWithAbility hero) {
        setsConstrainsView();
        ImageView heroAvatarImageView = createHeroAvatarImageView(hero);
        mHeroLayout.addView(heroAvatarImageView);
        last_added_view_id = heroAvatarImageView.getId();
        setAvatarConstraints(heroAvatarImageView, mHeroLayout);
        for (Ability ability : hero.getAbilities()) {
            TimerImageView abilityImageView = createHeroAbilityImageView(ability);
            mHeroLayout.addView(abilityImageView);
            Log.d(TAG + new Object() {
            }.getClass().getEnclosingMethod().getName(), "Id of last view added: " + last_added_view_id);
            setAbilityConstraints(abilityImageView, mHeroLayout);

            last_added_view_id = abilityImageView.getId();
        }
        heroCount++;
        Log.d(TAG + new Object() {
        }.getClass().getEnclosingMethod().getName(), String.valueOf(hero.getName()));
    }

    @Override
    public void showHeroWithAbility(List<HeroWithAbility> heroes) {
        for (HeroWithAbility hero : heroes) {
            Log.d(TAG, hero.toString());
            createView(hero);
        }
        startPostponedEnterTransition();
    }

    private void setsConstrainsView() {
        switch (heroCount) {
            case 0: {
                leftConstraint = R.id.first_guideline;
                rightConstraint = R.id.second_guideline;
            }
            break;
            case 1: {
                leftConstraint = R.id.second_guideline;
                rightConstraint = R.id.third_guideline;
            }
            break;
            case 2: {
                leftConstraint = R.id.third_guideline;
                rightConstraint = R.id.fourth_guideline;
            }
            break;
            case 3: {
                leftConstraint = R.id.fourth_guideline;
                rightConstraint = R.id.fifth_guideline;
            }
            break;
            case 4: {
                leftConstraint = R.id.fifth_guideline;
                rightConstraint = R.id.sixth_guideline;
            }
            break;
        }
    }

    private void setAbilityConstraints(ImageView abilityImageView, ConstraintLayout layout) {
        ConstraintSet constraintSet = new ConstraintSet();
        int viewId = abilityImageView.getId();
        constraintSet.connect(viewId, ConstraintSet.TOP, last_added_view_id, ConstraintSet.BOTTOM);
        constraintSet.connect(viewId, ConstraintSet.LEFT, leftConstraint, ConstraintSet.LEFT);
        constraintSet.connect(viewId, ConstraintSet.RIGHT, rightConstraint, ConstraintSet.RIGHT);
        constraintSet.setDimensionRatio(viewId, "1:1");
        constraintSet.applyTo(layout);
    }

    private void setAvatarConstraints(ImageView imageView, ConstraintLayout layout) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(imageView.getId(), ConstraintSet.LEFT, leftConstraint, ConstraintSet.LEFT);
        constraintSet.connect(imageView.getId(), ConstraintSet.RIGHT, rightConstraint, ConstraintSet.RIGHT);
        constraintSet.setDimensionRatio(imageView.getId(), "16:9");
        constraintSet.applyTo(layout);
    }

    private ImageView createHeroAvatarImageView(HeroWithAbility hero) {
        ImageView heroImageView = new ImageView(mContext);
        Glide.with(this)
                .load(hero.getAvatar())
                .apply(new RequestOptions().dontTransform())
                .into(heroImageView);
        heroImageView.setId(View.generateViewId());
        heroImageView.setTransitionName(getResources().getString(R.string.transition_name) + (heroCount + 1));
        Log.d(TAG, "transition name " + heroImageView.getTransitionName());
        ConstraintLayout.LayoutParams heroLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        heroImageView.setLayoutParams(heroLayoutParams);
        return heroImageView;
    }

    private TimerImageView createHeroAbilityImageView(Ability ability) {
        TimerImageView abilityImageView = new TimerImageView(mContext);
        abilityImageView.setId(View.generateViewId());
        Glide.with(this).load(ability.getIcon()).into(abilityImageView);
        abilityImageView.setTimer(ability.getCallDown());
        abilityImageView.setAbilityName(ability.getName());
        abilityImageView.setOnClickListener(v -> {
            abilityImageView.startTimer();

            Log.d(TAG, "click");
            Log.d(TAG, "isRunning" + abilityImageView.isTimerIsRunning());
            Log.d(TAG, String.valueOf(abilityImageView.getCountTimerValueStarted()));
        });
        ConstraintLayout.LayoutParams abilityLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD);
        abilityImageView.setLayoutParams(abilityLayoutParams);
        return abilityImageView;
    }

    private void findViews(View fragmentView) {
        mHeroLayout = fragmentView.findViewById(R.id.heroes_layout);
        mGuideline_1 = fragmentView.findViewById(R.id.first_guideline);
        mGuideline_2 = fragmentView.findViewById(R.id.second_guideline);
        mGuideline_3 = fragmentView.findViewById(R.id.third_guideline);
        mGuideline_4 = fragmentView.findViewById(R.id.fourth_guideline);
        mGuideline_5 = fragmentView.findViewById(R.id.fifth_guideline);
        mGuideline_6 = fragmentView.findViewById(R.id.sixth_guideline);
        mButtonAddHero = fragmentView.findViewById(R.id.button_add_hero);
        mButtonAddAbility = fragmentView.findViewById(R.id.button_add_ability);
        mButtonNewHero = fragmentView.findViewById(R.id.button_add_new_hero);
        mButtonAddHeroCloudStore = fragmentView.findViewById(R.id.button_add_hero_cloudstore);
        mButtonGetHeroCloudStore = fragmentView.findViewById(R.id.button_get_hero_cloudstore);
        mButtonAddAbilityCloudStore = fragmentView.findViewById(R.id.button_set_ability_cloudstore);
        mButtonGetAbilityCloudStore = fragmentView.findViewById(R.id.button_get_ability_cloudstore);
    }

    private void logged(String message) {
        Log.d(TAG, message);
    }
//        mMainLayout.addView(heroAvatarImageView);
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(mMainLayout);
//        constraintSet.clear(heroAvatarImageView.getId());
//        constraintSet.connect(heroAvatarImageView.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
//        if (heroViewIdMap.size()==0) {
//            constraintSet.connect(heroAvatarImageView.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT);
//        }else {
//            int lastViewId = heroViewIdMap.get(mlastHeroAdded);
//            constraintSet.connect(heroAvatarImageView.getId(),ConstraintSet.LEFT,lastViewId,ConstraintSet.RIGHT);
//        }
//
//
//        constraintSet.setDimensionRatio(heroAvatarImageView.getId(),"16:9");
//        constraintSet.constrainPercentWidth(heroAvatarImageView.getId(),0.2f);
//        constraintSet.applyTo(mMainLayout);
//        heroViewIdMap.put(hero.getName(),heroAvatarImageView.getId());
//        mlastHeroAdded = hero.getName();
//        Log.d("onResume",String.valueOf(mMainLayout.indexOfChild(heroAvatarImageView)));
//
//
//
//    }
//    private ImageView createHeroImageView (Hero hero){
//        ImageView heroImageView =new ImageView(mContext);
//        heroImageView.setId(View.generateViewId());
//        heroImageView.setImageResource(hero.getAvatar());
//        ConstraintLayout.LayoutParams heroLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD);
//        heroImageView.setLayoutParams(heroLayoutParams);
//        return heroImageView;
//    }

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


    //endregion
}
