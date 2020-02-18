package com.example.dotaskillstimer.ui.screens.heroListScreen;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.ui.AspectRatioImageView;
import com.example.dotaskillstimer.ui.CustomItemAnimator;
import com.example.dotaskillstimer.ui.HeroListAdapter;
import com.example.dotaskillstimer.ui.Navigatator;
import com.example.dotaskillstimer.ui.OnItemHeroClick;
import com.example.dotaskillstimer.ui.screens.MainActivity;
import com.example.dotaskillstimer.utils.LayoutConfig;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeroListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeroListFragment extends Fragment implements HeroListView {
    private final String TagLifecycle = "Lifecycle ";
    private final String TAG = "HeroListFragment";
    private final String transitionHeroCountKey = "enemyHero";
    @Inject
    HeroListPresenterImpl presenter;
    ConstraintSet set1Hero, set2Hero, set3Hero, set4Hero, set5Hero, set0Hero;
    private ConstraintLayout mainLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HeroListAdapter adapter;
    private ActionBar actionBar;
    private MainActivity mainActivity;
    private View bottomView;
    private AspectRatioImageView enemyPickHero1;
    private AspectRatioImageView enemyPickHero2;
    private AspectRatioImageView enemyPickHero3;
    private AspectRatioImageView enemyPickHero4;
    private AspectRatioImageView enemyPickHero5;
    private LinearLayout draftLayout;
    private Button startTimerFrag;

    private OnItemHeroClick heroClickListener;
    private View.OnClickListener draftClickListener;
    private View fragmentView;
    private boolean isRestoredView = false;
    private ChangeBounds changeBounds;
    private GridLayoutManager layoutManager;
    private int pickCount;
    private CustomItemAnimator customItemAnimator;
    private DefaultItemAnimator defaultItemAnimator;
    private Navigatator navigateCallback;


    public HeroListFragment() {
        // Required empty public constructor
    }

    //region LifeCycle

    // TODO: Rename and change types and number of parameters
    public static HeroListFragment newInstance() {
        return new HeroListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponentsHolder().getHeroListComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentView != null) {
            isRestoredView = true;
            return fragmentView;
        }
        fragmentView = inflater.inflate(R.layout.layout_list_fragment, container, false);
//        if (savedInstanceState != null) {
//            pickCount = savedInstanceState.getInt("pickCount");
//            logging("pickCount " + pickCount);
//            switch (pickCount) {
//                case 1:
//                    fragmentView = inflater.inflate(R.layout.one_hero, container, false);
//                    break;
//                case 2:
//                    fragmentView = inflater.inflate(R.layout.three_hero, container, false);
//                    break;
//                case 3:
//                    fragmentView = inflater.inflate(R.layout.three_hero, container, false);
//                    break;
//                case 4:
//                    fragmentView = inflater.inflate(R.layout.four_hero, container, false);
//                    break;
//                case 5:
//                    fragmentView = inflater.inflate(R.layout.layout_hero_list_fragment, container, false);
//                    break;
//            }
//        } else {
//            fragmentView = inflater.inflate(R.layout.empty_hero, container, false);
//
//        }
        findViews(fragmentView);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            actionBar = mainActivity.getSupportActionBar();
        }
        setupRecyclerView();
        setHasOptionsMenu(true);
        navigateCallback= (Navigatator) getActivity();
        Log.d(TAG, "presenter hash " + String.valueOf(presenter.hashCode()));
        isRestoredView = false;
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.exit_transition));
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
        if (getActivity().isFinishing() && isRemoving()) {
            Log.d(TAG, TagLifecycle + "onDestroyView" + " isRemoving");
            ((App) getActivity().getApplication()).getComponentsHolder().releaseHeroListComponent();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, TagLifecycle + "onDestroy");
        super.onDestroy();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // Операции для выбранного пункта меню
        if (id == R.id.action_settings) {
            if (navigateCallback != null) {
                navigateCallback.navigate(R.id.settingFragment, null, null);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
    private void findViews(View fragmentView) {
        mainLayout = fragmentView.findViewById(R.id.hero_list_container);
        toolbar = fragmentView.findViewById(R.id.toolbar_hero_list_frag);
        recyclerView = fragmentView.findViewById(R.id.rec_view_hero_list_frag);
        bottomView = fragmentView.findViewById(R.id.enemy_pick_panel);
        startTimerFrag = fragmentView.findViewById(R.id.button_start_timer_frag);
        draftLayout = fragmentView.findViewById(R.id.draft_view_container);

        startTimerFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEnemyPickReady();

            }
        });
        setDraftClickListener();
    }


    private void setupRecyclerView() {
        layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);
        heroClickListener = position -> {
            recyclerView.setItemAnimator(defaultItemAnimator);
            Log.d(TAG, "onHeroClick" + adapter.getItemAtPosition(position));
            presenter.onHeroPick(adapter.getItemAtPosition(position));
        };
        recyclerView.bringToFront();
        customItemAnimator = new CustomItemAnimator();
        defaultItemAnimator = new DefaultItemAnimator();
    }


    @Override
    public void showHeroesForPick(List<HeroWithAbility> heroes) {
        if(isRestoredView)return;
        adapter = new HeroListAdapter(heroes);
        adapter.setClickListener(heroClickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showHeroesEnemyDraft(List<HeroWithAbility> enemyPick) {
        if(isRestoredView)return;
        draftLayout.post(new Runnable() {
            @Override
            public void run() {
                for (HeroWithAbility hero:enemyPick){
                    AspectRatioImageView view=getDraftView(hero);
                    draftLayout.addView(view,draftLayout.getWidth() / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });

    }

    @Override
    public void moveHeroFromPickToDraft(HeroWithAbility hero) {
        int position = adapter.getPositionOfHero(hero);
        AspectRatioImageView view = (AspectRatioImageView) layoutManager.findViewByPosition(position);
        int leftMargin = view.getLeft();
        int topMargin = view.getTop();
        int width = view.getWidth();
        int height = view.getHeight();
        adapter.removeItem(hero);
        AspectRatioImageView draftImageView = getDraftView(hero);
        draftImageView.setVisibility(View.INVISIBLE);
        AutoTransition transition = new AutoTransition();
        transition.excludeTarget(draftImageView, true);
        TransitionManager.beginDelayedTransition(draftLayout, transition);
        draftLayout.addView(draftImageView, draftLayout.getWidth() / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        draftLayout.post(new Runnable() {
            @Override
            public void run() {
                float scaleX = width / (float) draftImageView.getWidth();
                float scaleY = height / (float) draftImageView.getHeight();
                float translationX = leftMargin - draftImageView.getLeft();
                float translationY = topMargin - recyclerView.getHeight();
                draftImageView.setTranslationX(translationX);
                draftImageView.setTranslationY(translationY);
                draftImageView.setPivotX(0);
                draftImageView.setPivotY(0);
                draftImageView.setScaleX(scaleX);
                draftImageView.setScaleY(scaleY);
                draftImageView.animate()
                        .translationY(0)
                        .translationX(0)
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(new DecelerateInterpolator(3.0f))
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                draftImageView.setVisibility(View.VISIBLE); //костылёк, чтобы вью не появлялась до того как ей не присвоенно смещение и скалирование
                            }
                        })
                        .start();
            }
        });
    }

    private AspectRatioImageView getDraftView(HeroWithAbility hero) {
        AspectRatioImageView view = new AspectRatioImageView(getContext());
        view.setAdjustViewBounds(true);
        view.setOnClickListener(draftClickListener);
        Glide.with(mainActivity).load(hero.getAvatar()).into(view);
        return view;
    }


    @Override
    public void moveHeroFromDraftToPick(HeroWithAbility hero, int pickCount) {
        logging("moveHeroFromDraftToPick " + pickCount);
        adapter.addHero(hero);
        TransitionManager.beginDelayedTransition(draftLayout, new ChangeBounds().setDuration(500));
        logging("index " + pickCount);
        draftLayout.removeViewAt(pickCount);
    }

    private void setDraftClickListener() {

        draftClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutConfig config;
                int topMargin = v.getTop() + recyclerView.getHeight();
                int leftMargin = v.getLeft();
                int width = v.getWidth();
                int height = v.getHeight();
                config = new LayoutConfig(leftMargin, topMargin, width, height);
                int index = draftLayout.indexOfChild(v);
                presenter.onHeroRemoveFromPick(index);
                customItemAnimator.setConfig(config);
                recyclerView.setItemAnimator(customItemAnimator);
            }
        };
    }

    @Override
    public void startTimerScreen(List<HeroWithAbility> enemyPick) {
        setTransitionNames();
        if (navigateCallback != null) {
            FragmentNavigator.Extras.Builder builder = new FragmentNavigator.Extras.Builder();
            //обернул код в Трай Кэтч, чтобы не городить миллион проверок на Нулл
            Bundle heroEnemyPick = new Bundle();
            int childCount = draftLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View sharedView = draftLayout.getChildAt(i);
                builder.addSharedElement(sharedView, sharedView.getTransitionName());
                heroEnemyPick.putParcelable("enemyPick" + i, enemyPick.get(i));
            }
            logging("starttimer" + enemyPick.size());
            FragmentNavigator.Extras extras = builder.build();
            navigateCallback.navigate(R.id.heroTimerFragment, heroEnemyPick, extras);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pickCount", pickCount);
    }

    private void setTransitionNames() {
        int[] transitionNames = {
                R.string.transition_name_hero_1,
                R.string.transition_name_hero_2,
                R.string.transition_name_hero_3,
                R.string.transition_name_hero_4,
                R.string.transition_name_hero_5};
        int childCount = draftLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            String transitionName = getResources().getString(transitionNames[i]);
            logging(transitionName);
            draftLayout.getChildAt(i).setTransitionName(transitionName);
        }
    }


    private void logging(String message) {
        Log.d(TAG, message);
    }


}
