package com.srgpanov.dotaskillstimer.ui.screens.heroListScreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.ui.views.AspectRatioImageView;
import com.srgpanov.dotaskillstimer.ui.HeroListAdapter;
import com.srgpanov.dotaskillstimer.ui.MyNavigator;
import com.srgpanov.dotaskillstimer.ui.OnItemHeroClick;
import com.srgpanov.dotaskillstimer.ui.screens.MainActivity;
import com.srgpanov.dotaskillstimer.utils.HeroAddAnimationListener;
import com.srgpanov.dotaskillstimer.utils.LayoutConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding3.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import kotlin.Unit;

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
    HeroListPresenter presenter;
    private ConstraintLayout constraintLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HeroListAdapter adapter;
    private ActionBar actionBar;
    private MainActivity mainActivity;

    private LinearLayout draftLayout;
    private FloatingActionButton startTimerFrag;

    private OnItemHeroClick heroClickListener;
    private View.OnClickListener draftClickListener;
    private View fragmentView;
    private GridLayoutManager layoutManager;
    private int pickCount;
    private CustomItemAnimator customItemAnimator;
    private MyNavigator navigateCallback;
    private int[] transitionNames;
    private int heroesInDraft = 5;
    private int heroesInRow = 4;
    private Single<Unit> draftContainerInflatedSingle;
    private SharedPreferences sharedPreferences;
    private boolean animationIsOn = true;
    private ProgressBar progressBar;

    public HeroListFragment() {
        // Required empty public constructor
    }

    //region LifeCycle

    public static HeroListFragment newInstance() {
        return new HeroListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponentsHolder().getHeroListComponent().inject(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setupAnimation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_list_fragment, container, false);
        findViews(fragmentView);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            actionBar = mainActivity.getSupportActionBar();
        }
        setupRecyclerView();
        setHasOptionsMenu(true);
        navigateCallback = (MyNavigator) getActivity();
        logging("presenter hash " + String.valueOf(presenter.hashCode()));
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        draftContainerInflatedSingle = RxView.globalLayouts(draftLayout).firstOrError();
        draftContainerInflatedSingle.subscribe(new DisposableSingleObserver<Unit>() {
            @Override
            public void onSuccess(Unit unit) {
                ViewGroup.LayoutParams params = draftLayout.getLayoutParams();
                int heightHeroView = (int) (draftLayout.getWidth() / heroesInDraft / 16f * 9f);
                params.height = heightHeroView;
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        logging(TagLifecycle + "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        logging(TagLifecycle + "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        logging(TagLifecycle + "onStart");
        animationIsOn = sharedPreferences.getBoolean(getString(R.string.animation), true);
        if (!animationIsOn) {
            recyclerView.setItemAnimator(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        logging(TagLifecycle + "onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logging(TagLifecycle + "onDestroyView");
        logging("fragmentView==null" + (fragmentView == null));
        presenter.detachView();
        if (getActivity().isFinishing() && isRemoving()) {
            logging(TagLifecycle + "onDestroyView" + " isRemoving");
            ((App) getActivity().getApplication()).getComponentsHolder().releaseHeroListComponent();
        }
    }

    @Override
    public void onDestroy() {
        logging(TagLifecycle + "onDestroy");
        super.onDestroy();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
        constraintLayout = fragmentView.findViewById(R.id.hero_list_container);
        toolbar = fragmentView.findViewById(R.id.toolbar_hero_list_frag);
        recyclerView = fragmentView.findViewById(R.id.rec_view_hero_list_frag);
        startTimerFrag = fragmentView.findViewById(R.id.button_start_timer_frag);
        draftLayout = fragmentView.findViewById(R.id.draft_view_container);
        progressBar =fragmentView.findViewById(R.id.progress_bar);

        startTimerFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEnemyPickReady();
            }
        });
        setDraftClickListener();
    }
    private void setupRecyclerView() {
        layoutManager = new GridLayoutManager(getActivity(), heroesInRow);
        recyclerView.setLayoutManager(layoutManager);
        heroClickListener = new OnItemHeroClick() {
            @Override
            public void onItemClick(int position, View view) {
                view.setOnClickListener(null);//убираем клик листенер, чтобы на убранную из списка, но ещё отрисованную вью нельзя было кликать
                presenter.onHeroPick(adapter.getItemAtPosition(position));
            }
        };
        if (animationIsOn) {
            HeroAddAnimationListener animationListener = new HeroAddAnimationListener() {
                float translationZ;

                @Override
                public void animationStart() {
                    translationZ = draftLayout.getTranslationZ();
                    draftLayout.setElevation(0);
                    logging("draft layout " + translationZ);
                }

                @Override
                public void animationStop() {
                    logging("draft layout " + translationZ);
                    draftLayout.setElevation(translationZ);
                }
            };
            customItemAnimator = new CustomItemAnimator(animationListener);
            recyclerView.setItemAnimator(customItemAnimator);
        } else {
            recyclerView.setItemAnimator(null);
        }
    }

    @Override
    public void showHeroesForPick(List<HeroWithAbility> heroes) {
        adapter = new HeroListAdapter(heroes);
        adapter.setClickListener(heroClickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showHeroesEnemyDraft(List<HeroWithAbility> enemyPick) {
        DisplayMetrics metrics = mainActivity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        for (int i = 0; i < enemyPick.size(); i++) {
            HeroWithAbility hero = enemyPick.get(i);
            AspectRatioImageView view = getDraftView(hero);
            String transitionName = getResources().getString(transitionNames[i]);
            view.setTransitionName(transitionName);
            draftLayout.addView(view, width / heroesInDraft, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void moveHeroFromPickToDraft(HeroWithAbility hero) {
        int position = adapter.getPositionOfHero(hero);
        AspectRatioImageView view = (AspectRatioImageView) layoutManager.findViewByPosition(position);
        int leftMargin = view.getLeft();
        int topMargin = view.getTop();
        int width = view.getWidth();
        int height = view.getHeight();
        logging("moveHeroFromPickToDraft");
        adapter.removeItem(hero);
        AspectRatioImageView draftImageView = getDraftView(hero);
        if (animationIsOn) {
            draftImageView.setVisibility(View.INVISIBLE);
            AutoTransition transition = new AutoTransition();
            transition.excludeTarget(draftImageView, true);
            TransitionManager.beginDelayedTransition(draftLayout, transition);
        }
        draftLayout.addView(draftImageView, draftLayout.getWidth() / heroesInDraft, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (animationIsOn){
            RxView.globalLayouts(draftImageView).firstOrError().subscribe(new DisposableSingleObserver<Unit>() {
                @Override
                public void onSuccess(Unit unit) {
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

                @Override
                public void onError(Throwable e) {

                }
            });
        }

    }

    private AspectRatioImageView getDraftView(HeroWithAbility hero) {
        AspectRatioImageView view = new AspectRatioImageView(getContext());
        view.setAdjustViewBounds(true);
        view.setOnClickListener(draftClickListener);
        Glide.with(mainActivity).load(hero.getAvatar()).error(R.drawable.ic_broken_image_48px).into(view);
        return view;
    }

    @Override
    public void moveHeroFromDraftToPick(HeroWithAbility hero, int pickCount) {
        adapter.addHero(hero);
        View view = draftLayout.getChildAt(pickCount);
        LayoutConfig config = new LayoutConfig(view);
        if (animationIsOn) {
            TransitionManager.beginDelayedTransition(draftLayout, new ChangeBounds().setDuration(500));
        }
        draftLayout.removeView(view);
        if (animationIsOn) {
            logging("lastItemIsVisible " + lastItemIsVisible());
            if (lastItemIsVisible()) {
                config.topMargin = config.topMargin + recyclerView.getHeight();
                customItemAnimator.setConfig(config);
            } else {
                AspectRatioImageView imageView = setupViewForAnimate(hero, config);
                constraintLayout.addView(imageView, config.width, config.height);
                draftLayout.setElevation(0);
                imageView.setElevation(1);
                ViewCompat.animate(imageView)
                        .translationY(imageView.getTranslationY() - config.height)
                        .withEndAction(() -> {
                            draftLayout.setElevation(1);
                            imageView.setElevation(0);
                            ViewCompat.animate(imageView)
                                    .translationY(imageView.getTranslationY() + imageView.getHeight())
                                    .withEndAction(() -> {
                                        constraintLayout.removeView(imageView);
                                    });
                        });
            }
        }

    }

    @NotNull
    private AspectRatioImageView setupViewForAnimate(HeroWithAbility hero, LayoutConfig config) {
        config.topMargin = config.topMargin + constraintLayout.getHeight() - draftLayout.getHeight();
        AspectRatioImageView imageView = getDraftView(hero);
        imageView.setOnClickListener(null);
        imageView.setTranslationX(config.leftMargin);
        imageView.setTranslationY(config.topMargin);
        return imageView;
    }

    private boolean lastItemIsVisible() {
        logging("findLastVisibleItemPosition " + layoutManager.findLastVisibleItemPosition());
        logging("adapter.getItemCount()-1 " + (adapter.getItemCount() - 1));
        logging("delta " + (adapter.getItemCount() - 1 - layoutManager.findLastVisibleItemPosition()));
        return (adapter.getItemCount() - 1 - layoutManager.findLastVisibleItemPosition()) == 1;
    }

    private void setDraftClickListener() {

        draftClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);
                int index = draftLayout.indexOfChild(v);
                presenter.onHeroRemoveFromPick(index);
            }
        };
    }

    @Override
    public void startTimerScreen(List<HeroWithAbility> enemyPick) {
        if(animationIsOn){
        setTransitionNames();
        if (navigateCallback != null) {
            FragmentNavigator.Extras.Builder builder = new FragmentNavigator.Extras.Builder();
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
        }}else {
            if (navigateCallback != null) {
                Bundle heroEnemyPick = new Bundle();
                int childCount = draftLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View sharedView = draftLayout.getChildAt(i);
                    heroEnemyPick.putParcelable("enemyPick" + i, enemyPick.get(i));
                }
                logging("starttimer" + enemyPick.size());
                navigateCallback.navigate(R.id.heroTimerFragment, heroEnemyPick, null);
        }}

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pickCount", pickCount);
    }

    private void setTransitionNames() {
        int childCount = draftLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            String transitionName = getResources().getString(transitionNames[i]);
            logging(transitionName);
            draftLayout.getChildAt(i).setTransitionName(transitionName);
        }
    }

    private void setupAnimation() {
        transitionNames = new int[]{
                R.string.transition_name_hero_1,
                R.string.transition_name_hero_2,
                R.string.transition_name_hero_3,
                R.string.transition_name_hero_4,
                R.string.transition_name_hero_5};
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.exit_transition));
    }

    private void logging(String message) {
        Log.d(TAG, message);
    }

}
