package com.example.dotaskillstimer.ui.screens.heroListScreen;


import android.content.res.Resources;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bumptech.glide.Glide;
import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.ui.AspectRatioImageView;
import com.example.dotaskillstimer.ui.HeroListAdapter;
import com.example.dotaskillstimer.ui.Navigatator;
import com.example.dotaskillstimer.ui.screens.MainActivity;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
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
    HeroRepository repository;
    @Inject
    HeroListPresenterImpl presenter;
    private RelativeLayout mainLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HeroListAdapter adapter;
    private ActionBar actionBar;
    private MainActivity mainActivity;
    private View bottomView;
    private View centerHorizontalView;
    private int enemyPickHeroCounter;
    private AspectRatioImageView enemyPickHero1;
    private AspectRatioImageView enemyPickHero2;
    private AspectRatioImageView enemyPickHero3;
    private AspectRatioImageView enemyPickHero4;
    private AspectRatioImageView enemyPickHero5;
    private int wrapContent;
    private int widthEnemyPickHeroView;
    private Button startTimerFrag;
    private Bundle heroEnemyPick = new Bundle();
    private HeroListAdapter.onItemHeroClick heroClickListener;
    private int enemyPickMaxHero = 5;
    private View fragmentView;
    private LayoutParams firstPickParams;
    private LayoutParams secondPickParams;
    private LayoutParams thirdPickParams;
    private LayoutParams fourthPickParams;
    private LayoutParams fifthPickParams;
    private boolean isRestoredView = false;


    public HeroListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HeroListFragment newInstance() {
        return new HeroListFragment();
    }

    //region LifeCycle

    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
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
        fragmentView = inflater.inflate(R.layout.fragment_hero_list, container, false);
        findViews(fragmentView);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            actionBar = mainActivity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        setupRecyclerView();
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

    //endregion
    private void findViews(View fragmentView) {
        mainLayout = fragmentView.findViewById(R.id.hero_list_container);
        toolbar = fragmentView.findViewById(R.id.toolbar_hero_list_frag);
        recyclerView = fragmentView.findViewById(R.id.rec_view_hero_list_frag);
        bottomView = fragmentView.findViewById(R.id.enemy_pick_panel);
        centerHorizontalView = fragmentView.findViewById(R.id.center_horizontal_line);
        startTimerFrag = fragmentView.findViewById(R.id.button_start_timer_frag);
        startTimerFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEnemyPickReady();
            }
        });
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);
        heroClickListener = position -> {
            Log.d(TAG, "onHeroClick" + adapter.getItemAtPosition(position));
            presenter.onHeroPick(adapter.getItemAtPosition(position));
        };
    }

    private LayoutParams applyDefaultParams(LayoutParams newParams) {
        widthEnemyPickHeroView = getScreenWidth() / enemyPickMaxHero;
        newParams.setMargins(0,0,0,0);
        newParams.width = widthEnemyPickHeroView;
        newParams.addRule(RelativeLayout.ALIGN_TOP, bottomView.getId());
        return newParams;
    }
    private LayoutParams applyDefaultParams() {
        widthEnemyPickHeroView = getScreenWidth() / enemyPickMaxHero;
        LayoutParams params = new LayoutParams(wrapContent, wrapContent);
        params.width = widthEnemyPickHeroView;
        params.addRule(RelativeLayout.ALIGN_TOP, bottomView.getId());
        return params;
    }

    private void logging(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void showHeroListForPick(List<HeroWithAbility> heroes) {
        adapter = new HeroListAdapter(heroes);
        adapter.setClickListener(heroClickListener);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void showHeroesEnemyPick(SparseArray<HeroWithAbility> enemyPick) {
        if (isRestoredView) return;
        int heroCount = enemyPick.size();
        if (heroCount == 0) return;
        enemyPickHero1 = createEnemyPickView(enemyPick.get(0), 1);
        if (enemyPick.size() == 1) {
            setupEnemyPickLayoutParams(heroCount);
            return;
        }
        enemyPickHero2 = createEnemyPickView(enemyPick.get(1), 2);
        if (enemyPick.size() == 2) {
            setupEnemyPickLayoutParams(heroCount);
            return;
        }
        enemyPickHero3 = createEnemyPickView(enemyPick.get(2), 3);
        if (enemyPick.size() == 3) {
            setupEnemyPickLayoutParams(heroCount);
            return;
        }
        enemyPickHero4 = createEnemyPickView(enemyPick.get(3), 4);
        if (enemyPick.size() == 4) {
            setupEnemyPickLayoutParams(heroCount);
            return;
        }
        enemyPickHero5 = createEnemyPickView(enemyPick.get(4), 5);
        setupEnemyPickLayoutParams(heroCount);
    }

    @Override
    public void removeHeroesFromPick(int position, int pickCount) {
        AspectRatioImageView imageView = (AspectRatioImageView) recyclerView.getChildAt(position);
        int imageCoordinateX = (int) imageView.getX();
        int imageCoordinateY = (int) imageView.getY();
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        imageView.setOnClickListener(null);
        LayoutParams startParams = new LayoutParams(wrapContent, wrapContent);
        startParams.setMargins(imageCoordinateX, imageCoordinateY + toolbar.getHeight(), 0, 0);
        AspectRatioImageView heroBottomImage = saveImageToReference(imageView,pickCount);
        saveParamsToReference(startParams,pickCount);
        heroBottomImage.setLayoutParams(startParams);
        mainLayout.addView(heroBottomImage);
        applyDefaultParams(startParams);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(1000);
        TransitionManager.beginDelayedTransition(mainLayout,changeBounds);
    }

    private void saveParamsToReference(LayoutParams startParams, int pickCount) {
        switch (pickCount){
            case 1:
                firstPickParams=startParams;
                break;
            case 2:
                secondPickParams=startParams;
                break;
            case 3:
                thirdPickParams=startParams;
                break;
            case 4:
                fourthPickParams=startParams;
                break;
            case 5:
                fifthPickParams=startParams;
                break;
        }
    }

    private AspectRatioImageView saveImageToReference(AspectRatioImageView imageView,int pickCount) {
        switch (pickCount-1) {
            case 0:
                enemyPickHero1 = imageView;
                enemyPickHero1.setId(R.id.enemyPickHero1);
                enemyPickHero1.setTransitionName(getResources().getString(R.string.transition_name) + 1);
                return enemyPickHero1;
            case 1:
                enemyPickHero2 = imageView;
                enemyPickHero2.setId(R.id.enemyPickHero2);
                enemyPickHero2.setTransitionName(getResources().getString(R.string.transition_name) + 2);
                return enemyPickHero2;
            case 2:
                enemyPickHero3 = imageView;
                enemyPickHero3.setId(R.id.enemyPickHero3);
                enemyPickHero3.setTransitionName(getResources().getString(R.string.transition_name) + 3);
                return enemyPickHero3;
            case 3:
                enemyPickHero4 = imageView;
                enemyPickHero4.setId(R.id.enemyPickHero4);
                enemyPickHero4.setTransitionName(getResources().getString(R.string.transition_name) + 4);
                return enemyPickHero4;
            case 4:
                enemyPickHero5 = imageView;
                enemyPickHero5.setId(R.id.enemyPickHero5);
                enemyPickHero5.setTransitionName(getResources().getString(R.string.transition_name) + 5);
                return enemyPickHero5;
            default:
                throw new IllegalStateException();
        }
    }

    private AspectRatioImageView createEnemyPickView(HeroWithAbility hero, int pickCount) {
        AspectRatioImageView imageView = new AspectRatioImageView(getActivity());
        LayoutParams params = applyDefaultParams();
        imageView.setTransitionName(getResources().getString(R.string.transition_name) + pickCount);
        imageView.setLayoutParams(params);
        Glide.with(getActivity()).load(hero.getAvatar()).into(imageView);
        mainLayout.addView(imageView);
        switch (pickCount) {
            case 1:
                imageView.setId(R.id.enemyPickHero1);
                firstPickParams = params;
                break;
            case 2:
                imageView.setId(R.id.enemyPickHero2);
                secondPickParams = params;
                break;
            case 3:
                imageView.setId(R.id.enemyPickHero3);
                thirdPickParams = params;
                break;
            case 4:
                imageView.setId(R.id.enemyPickHero4);
                fourthPickParams = params;
                break;
            case 5:
                imageView.setId(R.id.enemyPickHero5);
                fifthPickParams = params;
                break;
        }
        return imageView;
    }

    private void setupEnemyPickLayoutParams(int pickCount) {
        switch (pickCount) {
            case 1:
                firstPickParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 2:
                firstPickParams.addRule(RelativeLayout.LEFT_OF, centerHorizontalView.getId());
                secondPickParams.addRule(RelativeLayout.RIGHT_OF, centerHorizontalView.getId());
                break;
            case 3:
                secondPickParams.removeRule(RelativeLayout.RIGHT_OF);

                secondPickParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                firstPickParams.addRule(RelativeLayout.LEFT_OF, enemyPickHero2.getId());
                thirdPickParams.addRule(RelativeLayout.RIGHT_OF, enemyPickHero2.getId());
                break;
            case 4:
                firstPickParams.addRule(RelativeLayout.LEFT_OF, enemyPickHero2.getId());
                secondPickParams.addRule(RelativeLayout.LEFT_OF, centerHorizontalView.getId());
                thirdPickParams.addRule(RelativeLayout.RIGHT_OF, centerHorizontalView.getId());
                fourthPickParams.addRule(RelativeLayout.RIGHT_OF, enemyPickHero3.getId());
                break;
            case 5:
                thirdPickParams.removeRule(RelativeLayout.RIGHT_OF);

                firstPickParams.addRule(RelativeLayout.LEFT_OF, enemyPickHero2.getId());
                secondPickParams.addRule(RelativeLayout.LEFT_OF, enemyPickHero3.getId());
                thirdPickParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                fourthPickParams.addRule(RelativeLayout.RIGHT_OF, enemyPickHero3.getId());
                fifthPickParams.addRule(RelativeLayout.RIGHT_OF, enemyPickHero4.getId());
                break;
        }
    }

    @Override
    public void addHeroesEnemyPick(HeroWithAbility hero, int pickCount) {
        switch (pickCount) {
            case 1:
                if (enemyPickHero1==null) {
                    enemyPickHero1 = createEnemyPickView(hero, pickCount);
                }

                setupEnemyPickLayoutParams(pickCount);
                break;
            case 2:
                if (enemyPickHero2==null) {
                    enemyPickHero2 = createEnemyPickView(hero, pickCount);
                }

                setupEnemyPickLayoutParams(pickCount);
                break;
            case 3:
                if (enemyPickHero3==null) {
                    enemyPickHero3 = createEnemyPickView(hero, pickCount);
                }
                setupEnemyPickLayoutParams(pickCount);
                break;
            case 4:
                if (enemyPickHero4==null) {
                    enemyPickHero4 = createEnemyPickView(hero, pickCount);
                }
                setupEnemyPickLayoutParams(pickCount);
                break;
            case 5:
                if (enemyPickHero5==null) {
                    enemyPickHero5 = createEnemyPickView(hero, pickCount);
                }
                setupEnemyPickLayoutParams(pickCount);
                break;
        }
    }

    @Override
    public void startTimerScreen(SparseArray<HeroWithAbility> enemyPick) {
        Navigatator navigateCallback = (Navigatator) getActivity();
        if (navigateCallback != null) {
            FragmentNavigator.Extras.Builder builder = new FragmentNavigator.Extras.Builder();
            //обернул код в Трай Кэтч, чтобы не городить миллион проверок на Нулл
            try {
                builder.addSharedElement(enemyPickHero1, enemyPickHero1.getTransitionName());
                builder.addSharedElement(enemyPickHero2, enemyPickHero2.getTransitionName());
                builder.addSharedElement(enemyPickHero3, enemyPickHero3.getTransitionName());
                builder.addSharedElement(enemyPickHero4, enemyPickHero4.getTransitionName());
                builder.addSharedElement(enemyPickHero5, enemyPickHero5.getTransitionName());
            } catch (NullPointerException ignored) {

            }
            logging("starttimer" + enemyPick.size());
            FragmentNavigator.Extras extras = builder.build();
            heroEnemyPick.putSparseParcelableArray("enemyPick", enemyPick);
            navigateCallback.navigate(R.id.heroTimerFragment, heroEnemyPick, extras);
        }
    }

    private int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


}
