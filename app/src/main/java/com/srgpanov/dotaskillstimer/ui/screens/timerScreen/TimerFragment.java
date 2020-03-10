package com.srgpanov.dotaskillstimer.ui.screens.timerScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;
import com.srgpanov.dotaskillstimer.databinding.LayoutFiveHeroesBinding;
import com.srgpanov.dotaskillstimer.ui.views.AspectRatioImageView;
import com.srgpanov.dotaskillstimer.ui.views.GlyphTimerView;
import com.srgpanov.dotaskillstimer.ui.ItemSelectDialogFragment;
import com.srgpanov.dotaskillstimer.ui.views.RoshanTimerView;
import com.srgpanov.dotaskillstimer.ui.views.TimeGameView;
import com.srgpanov.dotaskillstimer.ui.views.TimerImageView;
import com.srgpanov.dotaskillstimer.utils.IOnBackPressed;
import com.srgpanov.dotaskillstimer.utils.TimerState;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import io.reactivex.Completable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subjects.CompletableSubject;
import kotlin.Unit;

public class TimerFragment extends Fragment implements HeroTimerView, IOnBackPressed {
    public static final int ITEM_SELECT_REQUEST_CODE = 200;
    private static final String LIST_STATE_KEY = "recycler_list_state";
    private final String TAG = this.getClass().getSimpleName() + " ";
    private final String TagLifecycle = "Lifecycle ";
    @Inject
    HeroTimerPresenterI presenter;
    Transition.TransitionListener sharedElementListener;
    private LayoutFiveHeroesBinding binding;
    private Context mContext;
    private ConstraintLayout mHeroLayout;
    private AspectRatioImageView firstHeroImageView, secondHeroImageView, thirdHeroImageView, fourthHeroImageView, fifthHeroImageView;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5;
    private LinearLayout linearLayoutsArray[];
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5;
    private ImageButton imageButtonsArray[];
    private TimerImageView buybackTimer1, buybackTimer2, buybackTimer3, buybackTimer4, buybackTimer5;
    private TimerImageView buybackTimerArray[];
    private NestedScrollView scrollViewsArray[];
    private RoshanTimerView roshanView;
    private GlyphTimerView glyphView;
    private TimeGameView timeGameView;
    private SharedPreferences sharedPreferences;
    private int abilityMinCallDownForShow;
    private boolean animationIsOn = true;
    private int heroesCount;
    private int translationYValue = -216;
    private boolean isConfigChange;
    private View.OnLongClickListener longClickListener;

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    //region Lifecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        logged(TagLifecycle + "onAttach");
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String abilityMinTimePref = sharedPreferences.getString(getString(R.string.abilityMinCallDownForShow), String.valueOf(15));
        animationIsOn = sharedPreferences.getBoolean(getString(R.string.animation), true);
        if (abilityMinTimePref != null) {
            abilityMinCallDownForShow = Integer.parseInt(abilityMinTimePref);
        }
        if (animationIsOn) {
            setupAnimations();
        }
    }

    private void setupAnimations() {
        DisplayMetrics metrics = requireActivity().getResources().getDisplayMetrics();
        translationYValue = -metrics.widthPixels / 5;
        setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.exit_transition));
        setupSharedElementTransition();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutFiveHeroesBinding.inflate(getLayoutInflater());
        View fragmentView = binding.getRoot();
        setupViews(fragmentView);
        logged(TagLifecycle + "onCreateView");
        isConfigChange = savedInstanceState == null;
        Log.d(TAG, "presenter hash " + presenter.hashCode());
        presenter.attachView(this);
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
        binding = null;
        presenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TagLifecycle + "onDestroy");

    }

    //endregion
    @Override
    public void showHeroWithAbility(List<HeroWithAbility> heroes) {
        logged("heroes" + heroes.size());
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mHeroLayout);
        heroesCount = heroes.size();
        switch (heroes.size()) {
            case 1:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(firstHeroImageView);
                initViews(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.forty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 2:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(secondHeroImageView);
                initViews(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.thirty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 3:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(thirdHeroImageView);
                initViews(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.twenty_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 4:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(thirdHeroImageView);
                Glide.with(mContext).load(heroes.get(3).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(fourthHeroImageView);
                initViews(heroes);
                constraintSet.connect(firstHeroImageView.getId(), ConstraintSet.LEFT, R.id.ten_percent_guideline, ConstraintSet.RIGHT);
                constraintSet.applyTo(mHeroLayout);
                break;
            case 5:
                Glide.with(mContext).load(heroes.get(0).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(firstHeroImageView);
                Glide.with(mContext).load(heroes.get(1).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(secondHeroImageView);
                Glide.with(mContext).load(heroes.get(2).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(thirdHeroImageView);
                Glide.with(mContext).load(heroes.get(3).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(fourthHeroImageView);
                Glide.with(mContext).load(heroes.get(4).getAvatar()).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(fifthHeroImageView);
                initViews(heroes);
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
        fragment.setTargetFragment(TimerFragment.this, positionOfHero + ITEM_SELECT_REQUEST_CODE);
        fragment.show(getParentFragmentManager(), "fragment_item_pick");
    }

    private void initViews(List<HeroWithAbility> heroes) {
        logged("anim is on" + animationIsOn);
        for (int i = 0; i < heroes.size(); i++) {
            int position = i;
            if (!animationIsOn || !isConfigChange) {
                RxView.preDraws(mHeroLayout, () -> true).firstOrError().subscribe(new DisposableSingleObserver<Unit>() {
                    @Override
                    public void onSuccess(Unit unit) {
                        buybackTimerArray[position].setTranslationY(0);
                        imageButtonsArray[position].setTranslationY(0);
                        buybackTimerArray[position].setVisibility(View.VISIBLE);
                        imageButtonsArray[position].setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
            }
            createAbilityView(heroes, i);
            createItemView(heroes, i);
        }
    }

    private void createItemView(List<HeroWithAbility> heroes, int position) {
        for (int i = 0; i < heroes.get(position).getItemList().size(); i++) {
            Item item = heroes.get(position).getItemList().get(i);
            TimerImageView imageView = createTimerImageView(item.getIcon());
            imageView.setTag(item.getName());
            int childCount = linearLayoutsArray[position].getChildCount();
            linearLayoutsArray[position].addView(imageView, childCount);
            imageView.setTimerDuration(item.getCallDownWithReduction());

        }
    }

    private void createAbilityView(List<HeroWithAbility> heroes, int position) {
        sortAbilityByPosition(heroes.get(position));
        for (int i = 0; i < heroes.get(position).getAbilities().size(); i++) {
            Ability ability = heroes.get(position).getAbilities().get(i);
            if (ability.getCallDown() >= abilityMinCallDownForShow) {
                TimerImageView imageView = createTimerImageView(ability.getIcon());
                imageView.setTag(ability.getName());
                linearLayoutsArray[position].addView(imageView);
                imageView.setTimerDuration(ability.getCallDownWithReduction());
                if (animationIsOn && isConfigChange) {
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setTranslationY(translationYValue);
                }
            }
        }
    }

    private void sortAbilityByPosition(HeroWithAbility heroWithAbility) {
        Collections.sort(heroWithAbility.getAbilities(), new Comparator<Ability>() {
            @Override
            public int compare(Ability o1, Ability o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        });
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
            linearLayoutsArray[positionOfHero].addView(imageView);
            imageView.setTimerDuration(item.getCallDownWithReduction());
            RxView.globalLayouts(imageView).firstOrError().subscribe(new DisposableSingleObserver<Unit>() {
                @Override
                public void onSuccess(Unit unit) {
                    scrollViewsArray[positionOfHero].smoothScrollTo(0, scrollViewsArray[positionOfHero].getBottom());
                }

                @Override
                public void onError(Throwable e) {

                }
            });
        }
    }

    private TimerImageView createTimerImageView(String uri) {
        TimerImageView imageView = new TimerImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getTimerDuration() == 0) {
                    showMessage(getString(R.string.passive_or_charges));
                } else {
                    imageView.startTimer();
                }
            }
        });
        imageView.setOnLongClickListener(longClickListener);
        Glide.with(mContext).load(uri).placeholder(R.drawable.ic_image_24dp).error(R.drawable.ic_broken_image_48px).into(imageView);
        return imageView;
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
        presenter.onItemRemoved(String.valueOf(view.getTag()));
    }

    private void setupViews(View fragmentView) {
        mHeroLayout = binding.heroesLayout;
        firstHeroImageView = binding.imageViewHeroes1;
        secondHeroImageView = binding.imageViewHeroes2;
        thirdHeroImageView = binding.imageViewHeroes3;
        fourthHeroImageView = binding.imageViewHeroes4;
        fifthHeroImageView = binding.imageViewHeroes5;
        linearLayout1 = binding.linearLayout1;
        linearLayout2 = binding.linearLayout2;
        linearLayout3 = binding.linearLayout3;
        linearLayout4 = binding.linearLayout4;
        linearLayout5 = binding.linearLayout5;
        linearLayoutsArray = new LinearLayout[]{
                linearLayout1,
                linearLayout2,
                linearLayout3,
                linearLayout4,
                linearLayout5
        };
        scrollViewsArray = new NestedScrollView[]{
                binding.nestedScroll1,
                binding.nestedScroll2,
                binding.nestedScroll3,
                binding.nestedScroll4,
                binding.nestedScroll5,
        };
        imageButton1 = binding.addImageButton1;
        imageButton2 = binding.addImageButton2;
        imageButton3 = binding.addImageButton3;
        imageButton4 = binding.addImageButton4;
        imageButton5 = binding.addImageButton5;
        imageButtonsArray = new ImageButton[]{
                imageButton1,
                imageButton2,
                imageButton3,
                imageButton4,
                imageButton5
        };
        buybackTimer1 = binding.buybackImage1;
        buybackTimer2 = binding.buybackImage2;
        buybackTimer3 = binding.buybackImage3;
        buybackTimer4 = binding.buybackImage4;
        buybackTimer5 = binding.buybackImage5;
        buybackTimerArray = new TimerImageView[]{
                buybackTimer1,
                buybackTimer2,
                buybackTimer3,
                buybackTimer4,
                buybackTimer5
        };
        longClickListener = new View.OnLongClickListener() {
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
                        if (item.getItemId() == R.id.hero_menu_hide) {
                            hideView(view);
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return true;
            }
        };
        for (TimerImageView view : buybackTimerArray) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.startTimer();
                }
            });
            view.setOnLongClickListener(longClickListener);
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
            public void onClick(View v) {
                logged("fragments " + getParentFragmentManager().getFragments());
            }
        });
    }

    private void addViewWithAnimation() {
        List<Completable> completableList0 = new ArrayList<>();
        List<Completable> completableList1 = new ArrayList<>();
        List<Completable> completableList2 = new ArrayList<>();
        List<Completable> completableList3 = new ArrayList<>();
        List<Completable> completableList4 = new ArrayList<>();
        List<List<Completable>> lists = new ArrayList<>();
        lists.add(completableList0);
        lists.add(completableList1);
        lists.add(completableList2);
        lists.add(completableList3);
        lists.add(completableList4);
        for (int i = 0; i < heroesCount; i++) {
            lists.get(i).add(slideAnimation(buybackTimerArray[i], 300));
            for (int j = 0; j < linearLayoutsArray[i].getChildCount(); j++) {
                View view = linearLayoutsArray[i].getChildAt(j);
                lists.get(i).add(slideAnimation(view, 300));
            }
            lists.get(i).add(fadeAnimation(imageButtonsArray[i], 300));
        }

        Completable.concat(completableList0)
                .mergeWith(Completable.concat(completableList1))
                .mergeWith(Completable.concat(completableList2))
                .mergeWith(Completable.concat(completableList3))
                .mergeWith(Completable.concat(completableList4))
                .subscribe();

    }

    private Completable slideAnimation(View view, long duration) {
        CompletableSubject animationSubject = CompletableSubject.create();
        return animationSubject.doOnSubscribe(disposable -> {
            view.setZ(-1);
            view.setVisibility(View.VISIBLE);
            ViewCompat.animate(view)
                    .setDuration(duration)
                    .translationY(0)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .z(0)
                    .withEndAction(() -> {
                        animationSubject.onComplete();
                    });
        });
    }
    private Completable fadeAnimation(View view, long duration) {
        CompletableSubject animationSubject = CompletableSubject.create();
        return animationSubject.doOnSubscribe(disposable -> {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            ViewCompat.animate(view)
                    .setDuration(duration)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .alpha(1f)
                    .withEndAction(() -> {
                        animationSubject.onComplete();
                    });
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

    private void setupSharedElementTransition() {
        setSharedElementEnterTransition(TransitionInflater.from(mContext).inflateTransition(R.transition.change_image_transform_enter));
        setSharedElementReturnTransition(TransitionInflater.from(mContext).inflateTransition(R.transition.change_image_transform_enter));
        Transition sharedElementTransition = (Transition) this.getSharedElementEnterTransition();
        sharedElementListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                logged("sharedElement transition start");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                logged("sharedElement transition end");
                addViewWithAnimation();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };

        sharedElementTransition.addListener(sharedElementListener);
    }

    @Override
    public void onBackPressed() {
        App.getInstance().getComponentsHolder().releaseHeroTimerComponent();
    }

    private void logged(String message) {
        Log.d(TAG, message);
    }
}
