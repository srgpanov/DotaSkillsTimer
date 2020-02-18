package com.example.dotaskillstimer.ui.screens.timerScreen;

import android.util.Log;

import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.data.Ability;
import com.example.dotaskillstimer.data.CloudFireStore;
import com.example.dotaskillstimer.data.Hero;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.data.Item;
import com.example.dotaskillstimer.data.PrimaryAttributes;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.dotaskillstimer.utils.Constants.autocast;
import static com.example.dotaskillstimer.utils.Constants.fifth;
import static com.example.dotaskillstimer.utils.Constants.first;
import static com.example.dotaskillstimer.utils.Constants.fourth;
import static com.example.dotaskillstimer.utils.Constants.passive;
import static com.example.dotaskillstimer.utils.Constants.second;
import static com.example.dotaskillstimer.utils.Constants.third;
import static com.example.dotaskillstimer.utils.Constants.ultimate;
import static com.example.dotaskillstimer.utils.Constants.uniqueAbilityOrScepterUpgrade;
import static com.example.dotaskillstimer.utils.Constants.withoutCalldown;

public class HeroTimerPresenter implements HeroTimerPresenterI {
    private final String TAG = "HeroTimerPresenter";
    @Inject
    HeroRepository mRepository;
    @Inject
    CloudFireStore cloudFireStore;
    private List<HeroWithAbility> heroes = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Item> allAvailableItems = new ArrayList<>();
    private HeroTimerView heroTimerView;

    public HeroTimerPresenter() {
        App.getInstance().getComponentsHolder().getAppComponent().injectHeroTimerPresenter(this);
    }


    @Override
    public void attachView(HeroTimerView view) {
        heroTimerView = view;
        Log.d(TAG, "attachView" + view.hashCode());
        if (heroes.isEmpty()) {
            Disposable getHeroesDis = mRepository.getAllHeroesWithAbilities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<HeroWithAbility>>() {
                        @Override
                        public void accept(List<HeroWithAbility> hero) throws Exception {
                            heroes.clear();
                            heroes.addAll(hero);
                            heroTimerView.showHeroWithAbility(heroes);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d(TAG, throwable.getMessage());
                        }
                    });

            disposable.add(getHeroesDis);
        } else {
            heroTimerView.showHeroWithAbility(heroes);
        }
        Disposable allItemsDisposable = mRepository.getAllItems()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Item>>() {
                    @Override
                    public void accept(List<Item> items) throws Exception {
                        if (items.size() > allAvailableItems.size()) {
                            allAvailableItems.clear();
                            allAvailableItems.addAll(items);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logged(throwable.getMessage());
                    }
                });
        disposable.add(allItemsDisposable);
    }


    @Override
    public void detachView() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
//        for (HeroWithAbility hero:heroes){
//            hero.getItemList().clear();
//        }
        //TODO: сделать очистку листа предметов после выхода из фрагмента
        Log.d(TAG, "detachView" + heroTimerView.hashCode());

        this.heroTimerView = null;
    }

    public void addHero(HeroWithAbility hero) {
        if (!heroes.contains(hero)) {
            logged("items" + hero.hashCode() + " " + hero.getItemList());
            heroes.add(hero);
        }

    }

    private void logged(String message) {
        Log.d(TAG, message);
    }

    public void applyCallDawnReduction(int heroPosition, float percent) {
        heroes.get(heroPosition - 1).applyCallDownReduction(percent);
        List<Ability> abilities = heroes.get(heroPosition - 1).getAbilities();
        for (Ability ability : abilities) {
            logged("callDown" + ability.toString());
            ;
        }
        heroTimerView.refreshAbilityCallDown(heroes);
    }

    public void addItemToDb() {
        Item item = new Item("BKB", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/72/Black_King_Bar_icon.png?version=689da2e28f12053e51be1f53f149055d");
        Item item1 = new Item("Refresher Orb", 195000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e2/Refresher_Orb_icon.png?version=28aa8b9be28a13aeca9b356dee6ac0d7");
        Item item2 = new Item("Heaven's Halberd", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c6/Heaven%27s_Halberd_icon.png?version=74f935051ad4995ad651acefb03bc4fa");
        Item item3 = new Item("Satanic", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/dd/Satanic_icon.png?version=e4d371668282cf372db9f8c3f941aa01");
        Item item4 = new Item("Abyssal Blade", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3b/Abyssal_Blade_icon.png?version=8a56b54a3d4187648832bd4fec25ed2c");
        Item item5 = new Item("Bloodthorn", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f4/Bloodthorn_icon.png?version=aefdea8e86edce2d6449441b6aed537b");
        Item item6 = new Item("Meteor Hammer", 28000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/54/Meteor_Hammer_icon.png?version=4f41f71816e30a4750c4f070f0500be9");
        Item item7 = new Item("Bloodstone", 250000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5a/Bloodstone_icon.png?version=f0f095ffb77bbc88967240f8726fa20a");
        Item item8 = new Item("Shiva's Guard", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b6/Shiva%27s_Guard_icon.png?version=99a42baaacd7c0fc09301572bebb395c");
        Item item9 = new Item("Manta Style", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/84/Manta_Style_icon.png?version=ad9b698df4b7c03fedf9859d602a5fae");
        Item item10 = new Item("Hurricane Pike", 23000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/13/Hurricane_Pike_icon.png?version=84ba0c434d81e408a2c32035c89fe761");
        Item item11 = new Item("Aeon Disk", 115000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2b/Aeon_Disk_icon.png?version=f99e607615bf99d0cc43395c42fdd616");
        Item item12 = new Item("Scythe of Vyse", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/54/Scythe_of_Vyse_icon.png?version=1623b34aadcac8eb849ac2b445228aa9");
        Item item13 = new Item("Eul's Scepter of Divinity", 23000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/80/Eul%27s_Scepter_of_Divinity_icon.png?version=70e20f9a86cc3f09a99999b1ccdce2e8");
        Item item14 = new Item("Necronomicon 3", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Necronomicon_3_icon.png?version=9e08b8f87697b8973bb57065d79ed640");
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        items.add(item9);
        items.add(item10);
        items.add(item11);
        items.add(item12);
        items.add(item13);
        items.add(item14);
        Disposable disposable =
                Observable.fromIterable(items)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Item>() {
                            @Override
                            public void accept(Item item) throws Exception {
                                mRepository.addItem(item);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                logged(throwable.getMessage());
                            }
                        });
    }

    public void addHeroToDB() {

        Hero abbadon = new Hero(
                "Abaddon",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/2/26/Abaddon_icon.png?version=1b9f117778a2c6daaf4a28cc7920a547",
                PrimaryAttributes.Strength);
        Hero alchemist = new Hero(
                "Alchemist",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Alchemist_icon.png?version=a7c13bea85d68e59c5400c1bd99d1cfb",
                PrimaryAttributes.Strength);
        Hero axe = new Hero(
                "Axe",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/2/23/Axe_icon.png?version=e684255614f57796dc1a65df620c5191",
                PrimaryAttributes.Strength);
        Hero beastmaster = new Hero(
                "Beastmaster",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d9/Beastmaster_icon.png?version=5aa86ba5bbaca098fe5f9e86bbe132f4",
                PrimaryAttributes.Strength);
        Hero brewmaster = new Hero(
                "Brewmaster",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1e/Brewmaster_icon.png?version=c0906f71595d5e6917c14b54cd1821d3",
                PrimaryAttributes.Strength);
        Hero bristleback = new Hero(
                "Bristleback",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Bristleback_icon.png?version=77ce3fefc668df38a8c90268dc5bcd8b",
                PrimaryAttributes.Strength);
        Hero centaur_Warrunner = new Hero(
                "Centaur Warrunner",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Centaur_Warrunner_icon.png?version=0ebbf51ef4b45ddd509450ac454a0dfa",
                PrimaryAttributes.Strength);
        Hero chaos_Knight = new Hero(
                "Chaos Knight",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Chaos_Knight_icon.png?version=b321a84d401423e904159e6a380abe12",
                PrimaryAttributes.Strength);
        Hero clockwerk = new Hero(
                "Clockwerk",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d8/Clockwerk_icon.png?version=079dcb0e7d50a1aba6c833753da60bcf",
                PrimaryAttributes.Strength);
        Hero doom = new Hero(
                "Doom",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/4/40/Doom_icon.png?version=2240f491fb558e9ea1b0619b53517c82",
                PrimaryAttributes.Strength);
        Hero dragon_Knight = new Hero(
                "Dragon Knight",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/5/59/Dragon_Knight_icon.png?version=8f081d5debb0efc6f95282c7a1c52c68",
                PrimaryAttributes.Strength);
        Hero earth_Spirit = new Hero(
                "Earth Spirit",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/b/be/Earth_Spirit_icon.png?version=6358ee2b1e31c856ff16cb7fb5e23f46",
                PrimaryAttributes.Strength);
        Hero earthshaker = new Hero(
                "Earthshaker",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a5/Earthshaker_icon.png?version=7f16a63e5ed6a7ec7a3889dd4aea354d",
                PrimaryAttributes.Strength);
        Hero elder_Titan = new Hero(
                "Elder Titan",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1a/Elder_Titan_icon.png?version=e2649f981d5f6dcc1d16bbaefadc2324",
                PrimaryAttributes.Strength);
        Hero huskar = new Hero(
                "Huskar",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d3/Huskar_icon.png?version=310e94592950b90783d934a87591e05d",
                PrimaryAttributes.Strength);
        Hero io = new Hero(
                "Io",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8d/Io_icon.png?version=94a7476345faa617842c26f446a26ec4",
                PrimaryAttributes.Strength);
        Hero kunkka = new Hero(
                "Kunkka",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Kunkka_icon.png?version=f64791660d48f854ce9ce85eb233af3b",
                PrimaryAttributes.Strength);
        Hero legion_Commander = new Hero(
                "Legion Commander",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a2/Legion_Commander_icon.png?version=82c151a5cd968eba51cfb442831f94af",
                PrimaryAttributes.Strength);
        Hero lifestealer = new Hero(
                "Lifestealer",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2b/Lifestealer_icon.png?version=2fd62b82d2c618c9b6f1b69ba6b8d509",
                PrimaryAttributes.Strength);
        Hero lycan = new Hero(
                "Lycan",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d6/Lycan_icon.png?version=8174f47b42eaebb44f401927252765cb",
                PrimaryAttributes.Strength);
        Hero magnus = new Hero(
                "Magnus",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/b/ba/Magnus_icon.png?version=a59de522b23eddce524eec08e6598a9b",
                PrimaryAttributes.Strength);
        Hero mars = new Hero(
                "Mars",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9d/Mars_icon.png?version=52c12b93be5c87fe1345fdb20b24fb5d",
                PrimaryAttributes.Strength);
        Hero night_Stalker = new Hero(
                "Night Stalker",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/15/Night_Stalker_icon.png?version=89360dbfb748a26ff50bcb475feb9d5b",
                PrimaryAttributes.Strength);
        Hero omniknight = new Hero(
                "Omniknight",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e2/Omniknight_icon.png?version=69fb42b317ef5d5495fd566090baec8f",
                PrimaryAttributes.Strength);
        Hero phoenix
                = new Hero(
                "Phoenix",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/14/Phoenix_icon.png?version=db8f4ec5840022a97ce532d138af8f13",
                PrimaryAttributes.Strength);
        Hero pudge
                = new Hero(
                "Pudge",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Pudge_icon.png?version=fee1223645079c94aacce19357e9a416",
                PrimaryAttributes.Strength);
        Hero sand_King = new Hero(
                "Sand King",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/7/79/Sand_King_icon.png?version=eda0d31413598822d2190b790f56a0a5",
                PrimaryAttributes.Strength);
        Hero slardar = new Hero(
                "Slardar",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7e/Slardar_icon.png?version=a47d98fe72c0976d67582f3bc223c069",
                PrimaryAttributes.Strength);
        Hero snapfire
                = new Hero(
                "Snapfire",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7a/Snapfire_icon.png?version=75f9424c41df97a94ac316d0cc274bf0",
                PrimaryAttributes.Strength);
        Hero spirit_Breaker
                = new Hero(
                "Spirit Breaker",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Spirit_Breaker_icon.png?version=166d65dd74ef0676fd3a12d1a1504e5d",
                PrimaryAttributes.Strength);
        Hero sven
                = new Hero(
                "Sven",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1b/Sven_icon.png?version=6afeb3577b8d3e602f7b9cc769cd0be6",
                PrimaryAttributes.Strength);
        Hero tidehunter = new Hero(
                "Tidehunter",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d5/Tidehunter_icon.png?version=72d689d15e1011f9668c546af5f19f00",
                PrimaryAttributes.Strength);
        Hero timbersaw
                = new Hero(
                "Timbersaw",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9a/Timbersaw_icon.png?version=8cdae5ee791dccd7b4dd8cf5db327bdd",
                PrimaryAttributes.Strength);
        Hero tiny
                = new Hero(
                "Tiny",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/5/55/Tiny_icon.png?version=042adfa9c45fb68093cc6fb4cbe06ea8",
                PrimaryAttributes.Strength);
        Hero treant_Protector
                = new Hero(
                "Treant Protector",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3f/Treant_Protector_icon.png?version=d4d85450f31c83aea8266dfd419b70d7",
                PrimaryAttributes.Strength);
        Hero tusk = new Hero(
                "Tusk",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ce/Tusk_icon.png?version=147fb449a87e1067c85f21f186132092",
                PrimaryAttributes.Strength);
        Hero underlord
                = new Hero(
                "Underlord",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/18/Underlord_icon.png?version=608e265d917a1cb3ecbdb479ca17cfe9",
                PrimaryAttributes.Strength);
        Hero undying
                = new Hero(
                "Undying",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/6/61/Undying_icon.png?version=60d8f84491097a3128b7bb2a3de26bf7",
                PrimaryAttributes.Strength);
        Hero wraith_King
                = new Hero(
                "Wraith King",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1e/Wraith_King_icon.png?version=52212107e602b6a17dfc1d7515086216",
                PrimaryAttributes.Strength);


        mRepository.addHero(abbadon);
        mRepository.addHero(axe);
        mRepository.addHero(alchemist);
        mRepository.addHero(beastmaster);
        mRepository.addHero(brewmaster);
        mRepository.addHero(bristleback);
        mRepository.addHero(centaur_Warrunner);
        mRepository.addHero(chaos_Knight);
        mRepository.addHero(clockwerk);
        mRepository.addHero(doom);
        mRepository.addHero(dragon_Knight);
        mRepository.addHero(earth_Spirit);
        mRepository.addHero(earthshaker);
        mRepository.addHero(elder_Titan);
        mRepository.addHero(huskar);
        mRepository.addHero(io);
        mRepository.addHero(kunkka);
        mRepository.addHero(legion_Commander);
        mRepository.addHero(lifestealer);
        mRepository.addHero(lycan);
        mRepository.addHero(magnus);
        mRepository.addHero(mars);
        mRepository.addHero(night_Stalker);
        mRepository.addHero(omniknight);
        mRepository.addHero(phoenix);
        mRepository.addHero(pudge);
        mRepository.addHero(sand_King);
        mRepository.addHero(slardar);
        mRepository.addHero(snapfire);
        mRepository.addHero(spirit_Breaker);
        mRepository.addHero(sven);
        mRepository.addHero(tidehunter);
        mRepository.addHero(timbersaw);
        mRepository.addHero(tiny);
        mRepository.addHero(treant_Protector);
        mRepository.addHero(tusk);
        mRepository.addHero(underlord);
        mRepository.addHero(undying);
        mRepository.addHero(wraith_King);

        Hero antimage = new Hero(
                "Anti-Mage",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Anti-Mage_icon.png?version=f55b1d31df2c842ebd3e6121f4dc513d",
                PrimaryAttributes.Agility);
        Hero arc_warden = new Hero(
                "Arc Warden",
                "https://gamepedia.cursecdn.com/dota2_gamepedia/0/07/Arc_Warden_icon.png?version=7ef22642fb6a42d09bc39601f19818ab",
                PrimaryAttributes.Agility);

        mRepository.addHero(antimage);
        mRepository.addHero(arc_warden);

        mRepository.addHero(new Hero("Bloodseeker", "https://gamepedia.cursecdn.com/dota2_gamepedia/5/56/Bloodseeker_icon.png?version=13fa81ed7d1bf313049de902c91def21", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Bounty Hunter", "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a6/Bounty_Hunter_icon.png?version=4a8be8d7f90be0936164013d63f17670", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Broodmother", "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Broodmother_icon.png?version=e5fc8a51578011186b3517e1b64cc72a", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Clinkz", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cb/Clinkz_icon.png?version=1764f0e7f6b084058f8e417891e3135e", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Drow Ranger", "https://gamepedia.cursecdn.com/dota2_gamepedia/8/80/Drow_Ranger_icon.png?version=53f9bde6e8f3c953a752c1358175309a", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Ember Spirit", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/91/Ember_Spirit_icon.png?version=d457d8fe38d1aae31d81f6e177d5f802", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Faceless Void", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/73/Faceless_Void_icon.png?version=af7f98a0de5e29bc73b662a27670aa9e", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Gyrocopter", "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4f/Gyrocopter_icon.png?version=f24686d2d018218824e5dc17c3e4dca1", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Juggernaut", "https://gamepedia.cursecdn.com/dota2_gamepedia/0/03/Juggernaut_icon.png?version=728e72d86473fed502ade52c7326d544", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Lone Druid", "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5d/Lone_Druid_icon.png?version=f9e25298afe665f5bcb1cbd3f22b828c", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Luna", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Luna_icon.png?version=b1d1b20a5c17defc097201e30e83ff98", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Medusa", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cc/Medusa_icon.png?version=3d9ae4d87d7dd18473b0077c6d51d3d3", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Meepo", "https://gamepedia.cursecdn.com/dota2_gamepedia/8/85/Meepo_icon.png?version=06970686c2620e4899376b9ee7e17d47", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Mirana", "https://gamepedia.cursecdn.com/dota2_gamepedia/1/12/Mirana_icon.png?version=a80affcf57d37d4fe5d898cd7e3f4e0a", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Monkey King", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7b/Monkey_King_icon.png?version=abe10431bbc2211c3e4f931534bd3d27", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Morphling", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7b/Morphling_icon.png?version=d1015dea93a88152e3fef62e9b64ad6e", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Naga Siren", "https://gamepedia.cursecdn.com/dota2_gamepedia/6/60/Naga_Siren_icon.png?version=a64bc31b30d125ba993f6498b68f9a0b", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Nyx Assassin", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fa/Nyx_Assassin_icon.png?version=82c807740fa60d9ab486e0bf0e4153c2", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Pangolier", "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4e/Pangolier_icon.png?version=0ca9284b8283c2fbd8550b2be5fd9c3a", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Phantom Assassin", "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Phantom_Assassin_icon.png?version=fcf1444e78efa7c35c74f98e934c271d", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Phantom Lancer", "https://gamepedia.cursecdn.com/dota2_gamepedia/8/81/Phantom_Lancer_icon.png?version=a78af53d50a56dff0aede90622eb3511", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Razor", "https://gamepedia.cursecdn.com/dota2_gamepedia/6/66/Razor_icon.png?version=0146a218be9881bb9d75b19ed14d35be", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Riki", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Riki_icon.png?version=85a963fecc5bdfc702aa22d6314eb728", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Shadow Fiend", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/36/Shadow_Fiend_icon.png?version=aead4589c97b4c486ca808225004b114", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Slark", "https://gamepedia.cursecdn.com/dota2_gamepedia/a/aa/Slark_icon.png?version=18b68c91671452beab47ca1813e1366e", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Sniper", "https://gamepedia.cursecdn.com/dota2_gamepedia/5/51/Sniper_icon.png?version=6429ecb9ebbc59abed6b846ac0af6f0d", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Spectre", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/ff/Spectre_icon.png?version=8af2c21961605d25cfa1be31725d7422", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Templar Assassin", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9c/Templar_Assassin_icon.png?version=e531fd05354743719c1b8dd48dc108d5", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Terrorblade", "https://gamepedia.cursecdn.com/dota2_gamepedia/5/52/Terrorblade_icon.png?version=77a3768fe6b0ad17bb407b12d6479d1d", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Troll Warlord", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f0/Troll_Warlord_icon.png?version=43c7aadede7bd18591b0c6dbb7dbd027", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Ursa", "https://gamepedia.cursecdn.com/dota2_gamepedia/4/40/Ursa_icon.png?version=6a015dc9739ae708a62230a0cea9ffa6", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Vengeful Spirit", "https://gamepedia.cursecdn.com/dota2_gamepedia/2/20/Vengeful_Spirit_icon.png?version=f7b24acba1c0bf726248ca1dd4620c7c", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Venomancer", "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Venomancer_icon.png?version=52d85a454dde46581873c15b7a072237", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Viper", "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5f/Viper_icon.png?version=2a8187ea6747e352093e1e54bc2606ff", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Weaver", "https://gamepedia.cursecdn.com/dota2_gamepedia/0/09/Weaver_icon.png?version=09f9240a75d1045d5d403385c61db35c", PrimaryAttributes.Agility));
        mRepository.addHero(new Hero("Ancient Apparition", "https://gamepedia.cursecdn.com/dota2_gamepedia/6/67/Ancient_Apparition_icon.png?version=4ca8ab4fcec9d410c544a9938b521d50", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Bane", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c3/Bane_icon.png?version=377fefe5af50310f8cff4fe94a093261", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Batrider", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f2/Batrider_icon.png?version=9bcaca9957e859c2aa8656b2dad2ef1c", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Chen", "https://gamepedia.cursecdn.com/dota2_gamepedia/6/61/Chen_icon.png?version=0f827938b63d647d266ce8f613c832ed", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Crystal Maiden", "https://gamepedia.cursecdn.com/dota2_gamepedia/2/27/Crystal_Maiden_icon.png?version=1fe64eb25bb59c8f5f9b916a8ee5f378", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Dark Seer", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c5/Dark_Seer_icon.png?version=9b81b657518099522d6a36b5cdd51e7e", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Dark Willow", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3c/Dark_Willow_icon.png?version=a4ef3ff9c27f1f5510f20e8dec1f15a9", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Dazzle", "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e6/Dazzle_icon.png?version=12e839c1ea458185e87dcf7a7c02c0e6", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Death Prophet", "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d7/Death_Prophet_icon.png?version=4010d86b2e5761cac55c24b259696f34", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Disruptor", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/97/Disruptor_icon.png?version=075011a1621b2d1750e4919e09c25b0e", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Enchantress", "https://gamepedia.cursecdn.com/dota2_gamepedia/4/41/Enchantress_icon.png?version=098b08695e2d972fb667dd4509e74ed5", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Enigma", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f7/Enigma_icon.png?version=cd786d109fe3b9bbf88f82ad48c8723b", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Grimstroke", "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d7/Grimstroke_icon.png?version=615a597828869dbf570f7993c3f927bc", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Invoker", "https://gamepedia.cursecdn.com/dota2_gamepedia/0/00/Invoker_icon.png?version=fb7516f0b780535fa8acfa9e8cb99266", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Jakiro", "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2f/Jakiro_icon.png?version=c4f2df2b9c444557b75aee31f289f8cf", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Keeper of the Light", "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b9/Keeper_of_the_Light_icon.png?version=134e16d0dd565e2763fd2111fe83f439", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Leshrac", "https://gamepedia.cursecdn.com/dota2_gamepedia/2/26/Leshrac_icon.png?version=fa8d18a43365db435910db186eb985d5", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Lich", "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bb/Lich_icon.png?version=7cb34de5d2bb15610cda9474d6c1b306", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Lina", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/35/Lina_icon.png?version=2bc137c161b17cd86674d29d0a0ed261", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Lion", "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b8/Lion_icon.png?version=7d730dc2fc5c3b86db8f84f9e09d1de2", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Nature's Prophet", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c4/Nature%27s_Prophet_icon.png?version=3d5a7a42e57a7e43f058e8d0874cda03", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Necrophos", "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a6/Necrophos_icon.png?version=ddc8b2eab62f4e578045623bfc74bd0b", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Ogre Magi", "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e0/Ogre_Magi_icon.png?version=3fa5ac15939733f3cfddc00aba633795", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Oracle", "https://gamepedia.cursecdn.com/dota2_gamepedia/7/72/Oracle_icon.png?version=50cfbd6e98b026808a9f8ad253605cfa", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Outworld Devourer", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/99/Outworld_Devourer_icon.png?version=bb656970d69f3027d26943bc24b03610", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Puck", "https://gamepedia.cursecdn.com/dota2_gamepedia/1/13/Puck_icon.png?version=07290c7498433bde0a87c330a51081c1", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Pugna", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cd/Pugna_icon.png?version=6e8e11c6c2013546664a8251d52e0e97", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Queen of Pain", "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a1/Queen_of_Pain_icon.png?version=d223991a573549d25a93703234869960", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Rubick", "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Rubick_icon.png?version=069f44c919f07acd1dec09aa71330568", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Shadow Demon", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f3/Shadow_Demon_icon.png?version=d4f43f79f2752f89c5d1177dae20c2d2", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Shadow Shaman", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/96/Shadow_Shaman_icon.png?version=412ef19dc4b14ec00e5be48a2f3ffa58", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Silencer", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9f/Silencer_icon.png?version=987250b53e9d354e7385f2b30263730d", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Skywrath Mage", "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bf/Skywrath_Mage_icon.png?version=e80bcf3140a08fd474e4ae959c68b5bd", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Storm Spirit", "https://gamepedia.cursecdn.com/dota2_gamepedia/1/13/Storm_Spirit_icon.png?version=c9eb4d9152a61c7663cefaa20bc1a8e8", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Techies", "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fa/Techies_icon.png?version=156b828f72a516c4327906e03e0c3a0e", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Tinker", "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d1/Tinker_icon.png?version=e904d8e318ae448d4639a6ca85303fe6", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Visage", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Visage_icon.png?version=737c2387430a431a85ffd08b7252fb6e", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Void Spirit", "https://gamepedia.cursecdn.com/dota2_gamepedia/9/99/Void_Spirit_icon.png?version=3f3635445f030d72d1e96310fc0816bb", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Warlock", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3f/Warlock_icon.png?version=553c91bae27fe106872f09f7c5da3878", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Windranger", "https://gamepedia.cursecdn.com/dota2_gamepedia/6/60/Windranger_icon.png?version=85593ee15f958babcf2f2d1ceb7a3940", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Winter Wyvern", "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4a/Winter_Wyvern_icon.png?version=708c1aac73eef23b9b9183d47484e7b2", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Witch Doctor", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/33/Witch_Doctor_icon.png?version=73b2ab9afd2990f49faf54f4e8bd66aa", PrimaryAttributes.Intellects));
        mRepository.addHero(new Hero("Zeus", "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3f/Zeus_icon.png?version=f80d75c5a638f7d875e49dc179b5143b", PrimaryAttributes.Intellects));
    }

    public void addAbilityToDB() {

        mRepository.addAbility(new Ability("Mist Coil", 5500, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ce/Mist_Coil_icon.png?version=40879d4ec75feb9c1a1a5eb9dbe5b74d", first, "Abaddon"));
        mRepository.addAbility(new Ability("Aphotic Shield", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b1/Aphotic_Shield_icon.png?version=3bc42248f1910086219c446d27cd5ac9", second, "Abaddon"));
        mRepository.addAbility(new Ability("Curse of Avernus", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Curse_of_Avernus_icon.png?version=87f3cab829e59844e9a9161d6f0ec050", third, "Abaddon"));
        mRepository.addAbility(new Ability("Borrowed Time", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/78/Borrowed_Time_icon.png?version=892a864f5a94d0d9860cfdffa9de4c7a", ultimate, "Abaddon"));

        mRepository.addAbility(new Ability("Acid Spray", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6b/Acid_Spray_icon.png?version=cee52fb70ac4852e313dc6b13ecdcb6b", first, "Alchemist"));
        mRepository.addAbility(new Ability("Unstable Concoction", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/96/Unstable_Concoction_icon.png?version=8155ae48d0d5be01ac6c5e54c0e8d85b", second, "Alchemist"));
        mRepository.addAbility(new Ability("Greevil's Greed", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/94/Greevil%27s_Greed_icon.png?version=7f63e88c698157cac11caec241b03760", third, "Alchemist"));
        mRepository.addAbility(new Ability("Chemical Rage", 55000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Chemical_Rage_icon.png?version=067bfe8dcde0b80aabb3a0c1e4cf868c", ultimate, "Alchemist"));

        mRepository.addAbility(new Ability("Berserker's Call", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d1/Berserker%27s_Call_icon.png?version=3130a674d60afe1fd4bfef191ae6649b", first, "Axe"));
        mRepository.addAbility(new Ability("Battle Hunger", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/00/Battle_Hunger_icon.png?version=de2fbb202be509b4f176758a992d3e13", second, "AxeAxe"));
        mRepository.addAbility(new Ability("Counter Helix", 300, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/14/Counter_Helix_icon.png?version=737865749b2a6efeac8d1bc137081391", third, "Axe"));
        mRepository.addAbility(new Ability("Culling Blade", 55000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/30/Culling_Blade_icon.png?version=d03d8a9f1aa4c926e024ace4d584b454", ultimate, "Axe"));

        mRepository.addAbility(new Ability("Wild Axes", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/27/Wild_Axes_icon.png?version=eb4cba4b2d33e55254b2caafbc033ea1", first, "Beastmaster"));
        mRepository.addAbility(new Ability("Call of the Wild Boar", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fa/Call_of_the_Wild_Boar_icon.png?version=e5c8fdf4f77c93070f705ec8b38d9368", second, "Beastmaster"));
        mRepository.addAbility(new Ability("Call of the Wild Hawk", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fc/Call_of_the_Wild_Hawk_icon.png?version=007000acceae4ef61bf5ebca6bde7159", third, "Beastmaster"));
        mRepository.addAbility(new Ability("Inner Beast", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1d/Inner_Beast_icon.png?version=b781758bd69d458fe1bfc0fa549b35ec", fourth, "Beastmaster"));
        mRepository.addAbility(new Ability("Primal Roar", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/68/Primal_Roar_icon.png?version=385e0a881eeffb4a9a57cf7b8b814fa4", ultimate, "Beastmaster"));

        mRepository.addAbility(new Ability("Thunder Clap", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b3/Thunder_Clap_icon.png?version=725d4e332ef6b40933bf3e2ff22815d4", first, "Brewmaster"));
        mRepository.addAbility(new Ability("Cinder Brew", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cf/Cinder_Brew_icon.png?version=d7e582dded19d83f2c30c968fe569652", second, "Brewmaster"));
        mRepository.addAbility(new Ability("Drunken Brawler", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d9/Drunken_Brawler_icon.png?version=1a0302e7abfb939edda812f7919140cc", third, "Brewmaster"));
        mRepository.addAbility(new Ability("Primal Split", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1f/Primal_Split_icon.png?version=1eed47663b15eee0ba162d4fd16ce18a", ultimate, "Brewmaster"));

        mRepository.addAbility(new Ability("Viscous Nasal Goo", 1500, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Viscous_Nasal_Goo_icon.png?version=967acf692391e4bbe03a53645b0f1e77", first, "Bristleback"));
        mRepository.addAbility(new Ability("Quill Spray", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/15/Quill_Spray_icon.png?version=f156738db0bd1eddda5b813bdb8c5ca5", second, "Bristleback"));
        mRepository.addAbility(new Ability("Bristleback", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Bristleback_ability_icon.png?version=00f4a50dc9b838f152af100020cbd355", third, "Bristleback"));
        mRepository.addAbility(new Ability("Warpath", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Warpath_icon.png?version=ab11670118b2ab14ff77c0f959130bd0", ultimate, "Bristleback"));

        mRepository.addAbility(new Ability("Hoof Stomp", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/95/Hoof_Stomp_icon.png?version=f3744b85d64fdca34ef2dbb262011fb9", first, "Centaur Warrunner"));
        mRepository.addAbility(new Ability("Double Edge", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3f/Double_Edge_icon.png?version=5901ded8c162929b9cd1f1ff62b2503e", second, "Centaur Warrunner"));
        mRepository.addAbility(new Ability("Retaliate", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f0/Retaliate_icon.png?version=01ab5eb8a94e0955fecece3c9614afd0", third, "Centaur Warrunner"));
        mRepository.addAbility(new Ability("Stampede", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5b/Stampede_icon.png?version=bf1e1fdc4f86a6aeaed63730f72f5dd5", ultimate, "Centaur Warrunner"));

        mRepository.addAbility(new Ability("Chaos Bolt", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Chaos_Bolt_icon.png?version=e09e3d1e06b0e27a6369af4fd16ed544", first, "Chaos Knight"));
        mRepository.addAbility(new Ability("Reality Rift", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/66/Reality_Rift_icon.png?version=945b4ecce2e3bf3d0e481b29d4c27418", second, "Chaos Knight"));
        mRepository.addAbility(new Ability("Chaos Strike", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f8/Chaos_Strike_icon.png?version=329263d4c4a310bb1d08a72ea49bb2e3", third, "Chaos Knight"));
        mRepository.addAbility(new Ability("Phantasm", 145000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3c/Phantasm_icon.png?version=68c7f94905e0075ca74bbe156ee45722", ultimate, "Chaos Knight"));

        mRepository.addAbility(new Ability("Battery Assault", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8f/Battery_Assault_icon.png?version=9effaa863be4d51a3b5795c764a48e44", first, "Clockwerk"));
        mRepository.addAbility(new Ability("Power Cogs", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/60/Power_Cogs_icon.png?version=ba26226beff7c70f371acafa1b08d935", second, "Clockwerk"));
        mRepository.addAbility(new Ability("Rocket Flare", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d8/Rocket_Flare_icon.png?version=0bad741b43a5e43f0efdb34c38e946b0", third, "Clockwerk"));
        mRepository.addAbility(new Ability("Hookshot", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d9/Hookshot_icon.png?version=b8dba415b9ba376011953ef203746a55", ultimate, "Clockwerk"));
        mRepository.addAbility(new Ability("Overclocking", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3f/Overclocking_icon.png?version=dd7e1372ef73855608e22ad4bb265ba9", uniqueAbilityOrScepterUpgrade, "Clockwerk"));

        mRepository.addAbility(new Ability("Devour", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e0/Devour_icon.png?version=1ddd8f15c204db18220f48c5be96d242", first, "Doom"));
        mRepository.addAbility(new Ability("Scorched Earth", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7e/Scorched_Earth_icon.png?version=96ffa1216a6f635b9284292d4969b2a5", second, "Doom"));
        mRepository.addAbility(new Ability("Infernal Blade", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e2/Infernal_Blade_icon.png?version=0eb4399f10b886938eb82c42d2e7725f", third, "Doom"));
        mRepository.addAbility(new Ability("Doom", 145000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/76/Doom_ability_icon.png?version=67a96dd4b53d7652a89931eafb99f8a7", ultimate, "Doom"));

        mRepository.addAbility(new Ability("Breathe Fire", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f8/Breathe_Fire_icon.png?version=dfd174d11f097e3640c4cdb4a1e16cd9", first, "Dragon Knight"));
        mRepository.addAbility(new Ability("Dragon Tail", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6f/Dragon_Tail_icon.png?version=5326f92047c74c2936523e4071f006b5", second, "Dragon Knight"));
        mRepository.addAbility(new Ability("Dragon Blood", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e6/Dragon_Blood_icon.png?version=b1a3c731a0cb7317ed46e473adeb9a6c", third, "Dragon Knight"));
        mRepository.addAbility(new Ability("Elder Dragon Form", 115000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/33/Elder_Dragon_Form_icon.png?version=e60d089d3ad6dabae0883ac67e85c16c", ultimate, "Dragon Knight"));

        mRepository.addAbility(new Ability("Boulder Smash", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/54/Boulder_Smash_icon.png?version=dcf4e3968783897cfbcf39c2a3048a59", first, "Earth Spirit"));
        mRepository.addAbility(new Ability("Rolling Boulder", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/09/Rolling_Boulder_icon.png?version=1e2e61cc0d9c997297f79e519f699222", second, "Earth Spirit"));
        mRepository.addAbility(new Ability("Geomagnetic Grip", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e8/Geomagnetic_Grip_icon.png?version=0df749eee5e307a8edd10abb7242d318", third, "Earth Spirit"));
        mRepository.addAbility(new Ability("Magnetize", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/82/Magnetize_icon.png?version=7f81eaee87ea99f2c89855989891b397", ultimate, "Earth Spirit"));
        mRepository.addAbility(new Ability("Enchant Remnant", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/74/Enchant_Remnant_icon.png?version=255c909084cecd71b58f985a423e298d", uniqueAbilityOrScepterUpgrade, "Earth Spirit"));

        mRepository.addAbility(new Ability("Fissure", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Fissure_icon.png?version=f85994f076e9fc403383b029b4d39cd6", first, "Earthshaker"));
        mRepository.addAbility(new Ability("Enchant Totem", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8f/Enchant_Totem_icon.png?version=9336f19de3d5f460020d2825ecd693c6", second, "Earthshaker"));
        mRepository.addAbility(new Ability("Aftershock", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f1/Aftershock_icon.png?version=d3e3b316390187ab0541d7ab236427e2", third, "Earthshaker"));
        mRepository.addAbility(new Ability("Echo Slam", 110000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/01/Echo_Slam_icon.png?version=a59a6b8e46e3b16cbcfe7dc463b8abda", ultimate, "Earthshaker"));

        mRepository.addAbility(new Ability("Echo Stomp", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e5/Echo_Stomp_icon.png?version=43fd6869efcdc9ffae1e5ec533b5b869", first, "Elder Titan"));
        mRepository.addAbility(new Ability("Astral Spirit", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/90/Astral_Spirit_icon.png?version=f50ad203504b9c7a96a98f847f6e18cf", second, "Elder Titan"));
        mRepository.addAbility(new Ability("Natural Order", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Natural_Order_icon.png?version=2cf740b16deb671c4fb4915b55ddef1c", third, "Elder Titan"));
        mRepository.addAbility(new Ability("Earth Splitter", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/88/Earth_Splitter_icon.png?version=b326851f6f91b695135b5764e3c456d7", ultimate, "Elder Titan"));

        mRepository.addAbility(new Ability("Inner Fire", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/af/Inner_Fire_icon.png?version=ec96a67aa09f3cadffb455e64f685d9b", first, "Huskar"));
        mRepository.addAbility(new Ability("Burning Spear", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/27/Burning_Spear_icon.png?version=66f48053afb20ab726a6600543bc446d", second, "Huskar"));
        mRepository.addAbility(new Ability("Berserker's Blood", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6a/Berserker%27s_Blood_icon.png?version=4b51d187c26987fa046ea2bb0c120d76", third, "Huskar"));
        mRepository.addAbility(new Ability("Life Break", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d8/Life_Break_icon.png?version=3451efe37a95172bf803ea486fb1f0ae", ultimate, "Huskar"));

        mRepository.addAbility(new Ability("Tether", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/02/Tether_icon.png?version=35ab6e7cec1f4f31004ee816be68e0aa", first, "Io"));
        mRepository.addAbility(new Ability("Spirits", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/af/Spirits_icon.png?version=7f271223aa6ffd69f5cf9ea214ce7c8d", second, "Io"));
        mRepository.addAbility(new Ability("Overcharge", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c3/Overcharge_icon.png?version=451e6462318e4b8feb953e5001b538d5", third, "Io"));
        mRepository.addAbility(new Ability("Relocate", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1e/Relocate_icon.png?version=ec5b652c2583ea790744cc20d047cf45", ultimate, "Io"));

        mRepository.addAbility(new Ability("Torrent", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/08/Torrent_icon.png?version=56300cf80952fe08af67bf44a34b9b0f", first, "Kunkka"));
        mRepository.addAbility(new Ability("Tidebringer", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/03/Tidebringer_icon.png?version=bcd992e5b4fade757bb2cba43913b064", second, "Kunkka"));
        mRepository.addAbility(new Ability("X Marks the Spot", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e2/X_Marks_the_Spot_icon.png?version=975f74e3018a79dbe4acfd6e9025e807", third, "Kunkka"));
        mRepository.addAbility(new Ability("Ghostship", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/12/Ghostship_icon.png?version=edcf424cb934af415cc5e0768181f4f7", ultimate, "Kunkka"));
        mRepository.addAbility(new Ability("Torrent Storm", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ad/Torrent_Storm_icon.png?version=8d110eb7dc6e14bfa3eca85cd4f6d88a", uniqueAbilityOrScepterUpgrade, "Kunkka"));

        mRepository.addAbility(new Ability("Overwhelming Odds", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/49/Overwhelming_Odds_icon.png?version=8580342a6aaf42256bfae35ead858704", first, "Legion Commander"));
        mRepository.addAbility(new Ability("Press the Attack", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Press_the_Attack_icon.png?version=05a32a56508d692fb75dc2ec9c185a47", second, "Legion Commander"));
        mRepository.addAbility(new Ability("Moment of Courage", 800, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bd/Moment_of_Courage_icon.png?version=19138d0b02769b37e79515c7f3ca9d50", third, "Legion Commander"));
        mRepository.addAbility(new Ability("Duel", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/ba/Duel_icon.png?version=321f541061f0d6571636ffc5c81587dd", ultimate, "Legion Commander"));

        mRepository.addAbility(new Ability("Rage", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/53/Rage_icon.png?version=ac516d89d3404cfad06901df2fa58665", first, "Lifestealer"));
        mRepository.addAbility(new Ability("Feast", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/da/Feast_icon.png?version=e1ad6099c4e14915ed60f56bb922cb18", second, "Lifestealer"));
        mRepository.addAbility(new Ability("Open Wounds", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/69/Open_Wounds_icon.png?version=8c181fce7ad44d6e3cb0f42e48a00747", third, "Lifestealer"));
        mRepository.addAbility(new Ability("Infest", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/06/Infest_icon.png?version=5c97c95ca6fdcef29089b3a5b3a7c361", ultimate, "Lifestealer"));

        mRepository.addAbility(new Ability("Summon Wolves", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/32/Summon_Wolves_icon.png?version=4aafb295e99dc14be5e1a2620a91b234", first, "Lycan"));
        mRepository.addAbility(new Ability("Howl", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d1/Howl_icon.png?version=c0869df5d01a907301f36478caa1ce22", second, "Lycan"));
        mRepository.addAbility(new Ability("Feral Impulse", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/04/Feral_Impulse_icon.png?version=c8f91871bd41d945a3308b617a6776b5", third, "Lycan"));
        mRepository.addAbility(new Ability("Shapeshift", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Shapeshift_icon.png?version=ccb2d55d3cfdce0c65c06dc93b362169", ultimate, "Lycan"));
        mRepository.addAbility(new Ability("Wolf Bite", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8b/Wolf_Bite_icon.png?version=a610e1220108552f944450becf639f33", uniqueAbilityOrScepterUpgrade, "Lycan"));

        mRepository.addAbility(new Ability("Shockwave", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Shockwave_icon.png?version=59952de2e54897df3818456cba5c40b3", first, "Magnus"));
        mRepository.addAbility(new Ability("Empower", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Empower_icon.png?version=2a94040487ef9c5f4f18ae01248faa26", second, "Magnus"));
        mRepository.addAbility(new Ability("Skewer", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/87/Skewer_icon.png?version=53ea1b482ba6ea7775e3aaebe367c786", third, "Magnus"));
        mRepository.addAbility(new Ability("Reverse Polarity", 130000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/ff/Reverse_Polarity_icon.png?version=a4b55a953e809cd2b52b14830ca4d24f", ultimate, "Magnus"));

        mRepository.addAbility(new Ability("Spear of Mars", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Spear_of_Mars_icon.png?version=4c3a834094d181734664356b3ffd0418", first, "Mars"));
        mRepository.addAbility(new Ability("God's Rebuke", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/29/God%27s_Rebuke_icon.png?version=38d618bcaf3bce9576941f3fdf55638a", second, "Mars"));
        mRepository.addAbility(new Ability("Bulwark", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b3/Bulwark_icon.png?version=4251ff894875b131bf54c24e5afa39b0", third, "Mars"));
        mRepository.addAbility(new Ability("Arena Of Blood", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8c/Arena_Of_Blood_icon.png?version=e9868b378d36eeedacc7def62b321b0a", ultimate, "Mars"));

        mRepository.addAbility(new Ability("Void", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/29/Void_icon.png?version=8ea720d9f39ed64a74ca12f2c744457a", first, "Night Stalker"));
        mRepository.addAbility(new Ability("Crippling Fear", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Crippling_Fear_icon.png?version=ae5ed9f3734383858e6a4f1909df357f", second, "Night Stalker"));
        mRepository.addAbility(new Ability("Hunter in the Night", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/54/Hunter_in_the_Night_icon.png?version=a3a1db13e51f113317f5b266df0129dd", third, "Night Stalker"));
        mRepository.addAbility(new Ability("Dark Ascension", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5e/Dark_Ascension_icon.png?version=cc3fa2e044ec044f873c789a234d760e", ultimate, "Night Stalker"));

        mRepository.addAbility(new Ability("Purification", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/64/Purification_icon.png?version=c5913935912fe94451ae4e50f75e7b73", first, "Omniknight"));
        mRepository.addAbility(new Ability("Heavenly Grace", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fa/Heavenly_Grace_icon.png?version=ac0e388790f2f7a6541da1683b10ead5", second, "Omniknight"));
        mRepository.addAbility(new Ability("Degen Aura", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Degen_Aura_icon.png?version=2ccd23658b65fdbf90e3dc846fc9ede4", third, "Omniknight"));
        mRepository.addAbility(new Ability("Guardian Angel", 140000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5d/Guardian_Angel_icon.png?version=95342d03f9d12dcebbe0c24a48b95857", ultimate, "Omniknight"));

        mRepository.addAbility(new Ability("Icarus Dive", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/00/Icarus_Dive_icon.png?version=8a779d18373c2d84bff34a81547d04da", first, "Phoenix"));
        mRepository.addAbility(new Ability("Fire Spirits", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cf/Fire_Spirits_icon.png?version=7787c90368071700245a527967db7272", second, "Phoenix"));
        mRepository.addAbility(new Ability("Sun Ray", 26000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Sun_Ray_icon.png?version=ea4bf2f5d2d2ee73a241ee121c995094", third, "Phoenix"));
        mRepository.addAbility(new Ability("Supernova", 110000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/aa/Supernova_icon.png?version=e6e78a7125e55fe6dac5f57f19866734", ultimate, "Phoenix"));

        mRepository.addAbility(new Ability("Meat Hook", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9c/Meat_Hook_icon.png?version=4562c3f4f9a1dc2bcfe35bc801177400", first, "Pudge"));
        mRepository.addAbility(new Ability("Rot", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/76/Rot_icon.png?version=42676989dbf3719ff3d0a1bdee3dc9f2", second, "Pudge"));
        mRepository.addAbility(new Ability("Flesh Heap", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bf/Flesh_Heap_icon.png?version=06769a178baf2df4cedc042b21ee64d6", third, "Pudge"));
        mRepository.addAbility(new Ability("Dismember", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/87/Dismember_icon.png?version=874ca416cc7aaef12c6306e480911668", ultimate, "Pudge"));

        mRepository.addAbility(new Ability("Burrowstrike", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bb/Burrowstrike_icon.png?version=2de80316ad2da7b1090fb74f8730bf51", first, "Sand King"));
        mRepository.addAbility(new Ability("Sand Storm", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/51/Sand_Storm_icon.png?version=5de8f38ab10ce5f8560b8f0cfff1c19b", second, "Sand King"));
        mRepository.addAbility(new Ability("Caustic Finale", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c5/Caustic_Finale_icon.png?version=37d78b382a4924e325216d4dd051ee49", third, "Sand King"));
        mRepository.addAbility(new Ability("Epicenter", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Epicenter_icon.png?version=054777b18553f3f48df75f7de86b6791", ultimate, "Sand King"));

        mRepository.addAbility(new Ability("Guardian Sprint", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1c/Guardian_Sprint_icon.png?version=4b08c72156d76501ca2a15961482493d", first, "Slardar"));
        mRepository.addAbility(new Ability("Slithereen Crush", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/15/Slithereen_Crush_icon.png?version=93ce319db612f4d6f52ee4ce47956a09", second, "Slardar"));
        mRepository.addAbility(new Ability("Bash of the Deep", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Bash_of_the_Deep_icon.png?version=3d51cb1531bd7a17aeb37e143c070ce0", third, "Slardar"));
        mRepository.addAbility(new Ability("Corrosive Haze", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cb/Corrosive_Haze_icon.png?version=6e53970408b39fbe3a60f1b443a297e3", ultimate, "Slardar"));

        mRepository.addAbility(new Ability("Scatterblast", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5b/Scatterblast_icon.png?version=76c765724632df46d303883b31312302", first, "Snapfire"));
        mRepository.addAbility(new Ability("Firesnap Cookie", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ad/Firesnap_Cookie_icon.png?version=9ac7efcef3b46573326b40e4facbffb7", second, "Snapfire"));
        mRepository.addAbility(new Ability("Lil' Shredder", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/89/Lil%27_Shredder_icon.png?version=ab728c9f5e74816df6d155c80b7bca7e", third, "Snapfire"));
        mRepository.addAbility(new Ability("Mortimer Kisses", 110000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0c/Mortimer_Kisses_icon.png?version=642b569460ef0e7d689fa64219347af8", ultimate, "Snapfire"));

        mRepository.addAbility(new Ability("Charge of Darkness", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Charge_of_Darkness_icon.png?version=4bfa87f7454e2f3b53ca84801e3a7148", first, "Spirit Breaker"));
        mRepository.addAbility(new Ability("Bulldoze", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/01/Bulldoze_icon.png?version=3bcb18d2b32f3e38799e4443f72e2371", second, "Spirit Breaker"));
        mRepository.addAbility(new Ability("Greater Bash", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/90/Greater_Bash_icon.png?version=132b7b15a4ede1ea430bfd6afabd54c6", third, "Spirit Breaker"));
        mRepository.addAbility(new Ability("Nether Strike", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/79/Nether_Strike_icon.png?version=1063305ca16f72a2d38ced4d2217cf6e", ultimate, "Spirit Breaker"));

        mRepository.addAbility(new Ability("Storm Hammer", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c3/Storm_Hammer_icon.png?version=8bac2f02b777dfa19be6e7197b48f56f", first, "Sven"));
        mRepository.addAbility(new Ability("Great Cleave", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9a/Great_Cleave_icon.png?version=d7b19f8fa5a9b0f4ccb9c15a9f9cb4b7", second, "Sven"));
        mRepository.addAbility(new Ability("Warcry", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/69/Warcry_icon.png?version=366ebdb395887384ee0d1acdc3604887", third, "Sven"));
        mRepository.addAbility(new Ability("God's Strength", 110000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cd/God%27s_Strength_icon.png?version=127d61017a9accfd04c4c60558b135dd", ultimate, "Sven"));

        mRepository.addAbility(new Ability("Gush", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/37/Gush_icon.png?version=c581f78b8cbbce28b64c7f8a0eb93f87", first, "Tidehunter"));
        mRepository.addAbility(new Ability("Kraken Shell", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/44/Kraken_Shell_icon.png?version=51ab7799e239533304cc53c44690035d", second, "Tidehunter"));
        mRepository.addAbility(new Ability("Anchor Smash", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/63/Anchor_Smash_icon.png?version=5d5a309520617d3b55755d25d8d8b251", third, "Tidehunter"));
        mRepository.addAbility(new Ability("Ravage", 150000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/17/Ravage_icon.png?version=aae12d67ee48deeb04f124a2e6897516", ultimate, "Tidehunter"));

        mRepository.addAbility(new Ability("Whirling Death", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f5/Whirling_Death_icon.png?version=c79820a743f808b72e1eca51f25fc99a", first, "Timbersaw"));
        mRepository.addAbility(new Ability("Timber Chain", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4e/Timber_Chain_icon.png?version=6e4ba29a10cde48e8f355749da9559c6", second, "Timbersaw"));
        mRepository.addAbility(new Ability("Reactive Armor", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e5/Reactive_Armor_icon.png?version=48d435492271c5a96390dcdcf1cf4ddc", third, "Timbersaw"));
        mRepository.addAbility(new Ability("Chakram", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c3/Chakram_icon.png?version=acdbb33a8f6d0ac5c5682825b70a6931", ultimate, "Timbersaw"));

        mRepository.addAbility(new Ability("Avalanche", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ef/Avalanche_icon.png?version=4fde1bf7fabff87d315918de4e43db76", first, "Tiny"));
        mRepository.addAbility(new Ability("Toss", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/54/Toss_icon.png?version=31b5ee8bdf5f3d056938a216951cdeca", second, "Tiny"));
        mRepository.addAbility(new Ability("Tree Grab", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4f/Tree_Grab_icon.png?version=fa6439dccebaa31b30288db67be949ea", third, "Tiny"));
        mRepository.addAbility(new Ability("Grow", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/db/Grow_icon.png?version=26b90e405c31cbc40cc8aae3081713b3", ultimate, "Tiny"));
        mRepository.addAbility(new Ability("Tree Volley", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/10/Tree_Volley_icon.png?version=c36417e33a4420a444d0c2e9b8af1f11", uniqueAbilityOrScepterUpgrade, "Tiny"));

        mRepository.addAbility(new Ability("Nature's Grasp", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/34/Nature%27s_Grasp_icon.png?version=2caa27869c8ca5f43a431068a4b45c7a", first, "Treant Protector"));
        mRepository.addAbility(new Ability("Leech Seed", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4e/Leech_Seed_icon.png?version=4204a23cbe6acc5e76748cf0c9eb516f", second, "Treant Protector"));
        mRepository.addAbility(new Ability("Living Armor", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b8/Living_Armor_icon.png?version=c40a1866d99303a09584000243c18857", third, "Treant Protector"));
        mRepository.addAbility(new Ability("Overgrowth", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a3/Overgrowth_icon.png?version=e23fd1c88026c42ae3609fa4921617d1", ultimate, "Treant Protector"));
        mRepository.addAbility(new Ability("Eyes In The Forest", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d4/Eyes_in_the_Forest_icon.png?version=9f30a2fe41a869c6f92e7b25d1002404", uniqueAbilityOrScepterUpgrade, "Treant Protector"));

        mRepository.addAbility(new Ability("Ice Shards", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/35/Ice_Shards_icon.png?version=c58c1f49aac7cdea9c246a997b8a40a6", first, "Tusk"));
        mRepository.addAbility(new Ability("Snowball", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/41/Snowball_icon.png?version=7d69a799b9a548b2dd978baa1bc22736", second, "Tusk"));
        mRepository.addAbility(new Ability("Tag Team", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Tag_Team_icon.png?version=daf4024560aadc751814de7bc290a7fb", third, "Tusk"));
        mRepository.addAbility(new Ability("Walrus PUNCH!", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/40/Walrus_Punch_icon.png?version=d1bbd8ccea87a7a1c385f3bdaa9c1643", ultimate, "Tusk"));
        mRepository.addAbility(new Ability("Walrus Kick", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/63/Walrus_Kick_icon.png?version=dcf9b8a2685ff9983f9ff95e3f2bc564", uniqueAbilityOrScepterUpgrade, "Tusk"));

        mRepository.addAbility(new Ability("Firestorm", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/97/Firestorm_icon.png?version=5c08fb1a77cc175e68ff6c21be831886", first, "Underlord"));
        mRepository.addAbility(new Ability("Pit of Malice", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/12/Pit_of_Malice_icon.png?version=ef7f9ce1bc587fca5aa71748edbb6c32", second, "Underlord"));
        mRepository.addAbility(new Ability("Atrophy Aura", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/69/Atrophy_Aura_icon.png?version=aab3068ef8e1f9914d77ca12309e5c87", third, "Underlord"));
        mRepository.addAbility(new Ability("Dark Rift", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/57/Dark_Rift_icon.png?version=f58b3610eafc45d94dac1649be807141", ultimate, "Underlord"));

        mRepository.addAbility(new Ability("Decay", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/28/Decay_icon.png?version=bf2909b1e3b9414ea0396e867869d127", first, "Undying"));
        mRepository.addAbility(new Ability("Soul Rip", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4c/Soul_Rip_icon.png?version=8be3c310286d1b4b106ccab2d5ef7728", second, "Undying"));
        mRepository.addAbility(new Ability("Tombstone", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6c/Tombstone_icon.png?version=0aa939f06905eb49e17d8bc86d1d85b7", third, "Undying"));
        mRepository.addAbility(new Ability("Flesh Golem", 125000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/46/Flesh_Golem_icon.png?version=f801913602da4b8c77fe159f397f4f35", ultimate, "Undying"));
        mRepository.addAbility(new Ability("Reincarnation", 250000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Aegis_of_the_Immortal_ability_icon.png?version=ba98bf2d3db47a7b470c05c031daf0b0", uniqueAbilityOrScepterUpgrade, "Undying"));

        mRepository.addAbility(new Ability("Wraithfire Blast", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3c/Wraithfire_Blast_icon.png?version=556ceffd091774ee4585f994794baf8e", first, "Wraith King"));
        mRepository.addAbility(new Ability("Vampiric Aura", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e4/Vampiric_Aura_icon.png?version=84426ac92f7f61841cf379de3c6aafcb", second, "Wraith King"));
        mRepository.addAbility(new Ability("Mortal Strike", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f2/Mortal_Strike_icon.png?version=627d0a0163abb3911122d2c26bdd2da4", third, "Wraith King"));
        mRepository.addAbility(new Ability("Reincarnation", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b4/Reincarnation_icon.png?version=935eed949112b782807db6979d716faf", ultimate, "Wraith King"));

        mRepository.addAbility(new Ability("Mana Break", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c1/Mana_Break_icon.png?version=3a030100029453e8532750495bde6db6", first, "Anti-Mage"));
        mRepository.addAbility(new Ability("Blink", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ce/Blink_%28Anti-Mage%29_icon.png?version=004bc9712d7f71db5f6d0d576ba58b16", second, "Anti-Mage"));
        mRepository.addAbility(new Ability("Counterspell", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/90/Counterspell_icon.png?version=ed81bed3061fb4bfec6b286170ddfa0a", third, "Anti-Mage"));
        mRepository.addAbility(new Ability("Mana Void", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Mana_Void_icon.png?version=4048b461564657beda74e49ecd2e3d21", ultimate, "Anti-Mage"));

        mRepository.addAbility(new Ability("Flux", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/03/Flux_icon.png?version=f47a65a2a7503d4a89304f72a3ff859b", first, "Arc Warden"));
        mRepository.addAbility(new Ability("Magnetic Field", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/db/Magnetic_Field_icon.png?version=d261ee42e61f37658a8ee0ad19bd14fe", second, "Arc Warden"));
        mRepository.addAbility(new Ability("Spark Wraith", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/eb/Spark_Wraith_icon.png?version=4544dd795607317a5baa0494a01cf8f7", third, "Arc Warden"));
        mRepository.addAbility(new Ability("Tempest Double", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d2/Tempest_Double_icon.png?version=bb698f17090538509315b0604f767fd6", ultimate, "Arc Warden"));
        mRepository.addAbility(new Ability("Rune Forge", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b9/Rune_Forge_icon.png?version=77ffef9a3ab4f7aa171ceadbdb361895", uniqueAbilityOrScepterUpgrade, "Arc Warden"));

        mRepository.addAbility(new Ability("Bloodrage", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Bloodrage_icon.png?version=a7e03d103a8aea0be39d5ce455e603b1", first, "Bloodseeker"));
        mRepository.addAbility(new Ability("Blood Rite", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/83/Blood_Rite_icon.png?version=0c8f2b8ed9e4e83b1ad16de79168e4ff", second, "Bloodseeker"));
        mRepository.addAbility(new Ability("Thirst", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Thirst_icon.png?version=692cfd095dc9619718f3bea2ab4be97b", third, "Bloodseeker"));
        mRepository.addAbility(new Ability("Rupture", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f4/Rupture_icon.png?version=fa323fe5f9565a24216ad4027b3cb13f", ultimate, "Bloodseeker"));

        mRepository.addAbility(new Ability("Shuriken Toss", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0d/Shuriken_Toss_icon.png?version=c8bfa96f940ec015f8e3f76b7cb76049", first, "Bounty Hunter"));
        mRepository.addAbility(new Ability("Jinada", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/99/Jinada_icon.png?version=4ff5711bb1165d8c8948d30962d2e9ec", second, "Bounty Hunter"));
        mRepository.addAbility(new Ability("Shadow Walk", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/78/Shadow_Walk_icon.png?version=043593de47f083aeb22b18267d74eeb2", third, "Bounty Hunter"));
        mRepository.addAbility(new Ability("Track", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6e/Track_icon.png?version=ce0384a323002cd37eceb04756eb7797", ultimate, "Bounty Hunter"));

        mRepository.addAbility(new Ability("Spawn Spiderlings", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d5/Spawn_Spiderlings_icon.png?version=dfb2ae04a126bc43d3a022ef992123d9", first, "Broodmother"));
        mRepository.addAbility(new Ability("Spin Web", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Spin_Web_icon.png?version=5becc049b8ccd37d787aefb9194019e1", second, "Broodmother"));
        mRepository.addAbility(new Ability("Incapacitating Bite", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/84/Incapacitating_Bite_icon.png?version=05adbd83c567dbd1dd7919ed16377f0b", third, "Broodmother"));
        mRepository.addAbility(new Ability("Insatiable Hunger", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/be/Insatiable_Hunger_icon.png?version=db5452e56c64f988214873bb04717c06", ultimate, "Broodmother"));

        mRepository.addAbility(new Ability("Death Pact", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b4/Death_Pact_icon.png?version=920fc1596cd4d88804bf151c29b874e8", first, "Clinkz"));
        mRepository.addAbility(new Ability("Searing Arrows", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e9/Searing_Arrows_icon.png?version=f3b91675a25080890e352059e867e6d4", second, "Clinkz"));
        mRepository.addAbility(new Ability("Skeleton Walk", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b5/Skeleton_Walk_icon.png?version=d87bf07bd7f8edfa5aa3468f2aaf2f36", third, "Clinkz"));
        mRepository.addAbility(new Ability("Burning Army", 110000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/60/Burning_Army_icon.png?version=c26a07963347df7d5de966fc46a50144", ultimate, "Clinkz"));

        mRepository.addAbility(new Ability("Frost Arrows", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8f/Frost_Arrows_icon.png?version=bf2615dc818ca8ce269958bfab9843f2", first, "Drow Ranger"));
        mRepository.addAbility(new Ability("Gust", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5b/Gust_icon.png?version=469ec351ae2c1af1823cdfdd9c13e84c", second, "Drow Ranger"));
        mRepository.addAbility(new Ability("Multishot", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4b/Multishot_icon.png?version=0a66dbcbcf5df39562b1a4e04e36f08c", third, "Drow Ranger"));
        mRepository.addAbility(new Ability("Marksmanship", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/af/Marksmanship_icon.png?version=717fa3deb9d5ffa2579b172e868cf8fa", ultimate, "Drow Ranger"));

        mRepository.addAbility(new Ability("Searing Chains", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ad/Searing_Chains_icon.png?version=9e87d51e6c581fbabd32d3ef5c7446a6", first, "Ember Spirit"));
        mRepository.addAbility(new Ability("Sleight of Fist", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/64/Sleight_of_Fist_icon.png?version=fe5d4d22eb1e19b6d62db4b8f732bafd", second, "Ember Spirit"));
        mRepository.addAbility(new Ability("Flame Guard", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/11/Flame_Guard_icon.png?version=b6ccf69330fbb5451f65916a8f37eb02", third, "Ember Spirit"));
        mRepository.addAbility(new Ability("Fire Remnant", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a8/Fire_Remnant_icon.png?version=59d8b9f999afab20be734a0d98998887", ultimate, "Ember Spirit"));

        mRepository.addAbility(new Ability("Time Walk", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1d/Time_Walk_icon.png?version=1c5973beb0a6e84f654a8505a820cfd8", first, "Faceless Void"));
        mRepository.addAbility(new Ability("Time Dilation", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bc/Time_Dilation_icon.png?version=bef4ffe5db746a37ecfa15f5e7ff7e3b", second, "Faceless Void"));
        mRepository.addAbility(new Ability("Time Lock", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b7/Time_Lock_icon.png?version=f9b703bd8ab8fe5d4eb974edb23c7750", third, "Faceless Void"));
        mRepository.addAbility(new Ability("Chronosphere", 160000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bf/Chronosphere_icon.png?version=43bf6aee79f3de4708a398cba665fee8", ultimate, "Faceless Void"));

        mRepository.addAbility(new Ability("Rocket Barrage", 5500, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5a/Rocket_Barrage_icon.png?version=4623ae9320bac0b55e1a892cf242f793", first, "Gyrocopter"));
        mRepository.addAbility(new Ability("Homing Missile", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3a/Homing_Missile_icon.png?version=5f04a67bef9a1923c1c476d841feede6", second, "Gyrocopter"));
        mRepository.addAbility(new Ability("Flak Cannon", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2b/Flak_Cannon_icon.png?version=5cb99f218d68fedbac276486fedca931", third, "Gyrocopter"));
        mRepository.addAbility(new Ability("Call Down", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ad/Call_Down_icon.png?version=4d1cc86759a5154071e39b5015482fd2", ultimate, "Gyrocopter"));

        mRepository.addAbility(new Ability("Blade Fury", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4c/Blade_Fury_icon.png?version=72e31f35e89b6c96066cd8f18743187e", first, "Juggernaut"));
        mRepository.addAbility(new Ability("Healing Ward", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/58/Healing_Ward_icon.png?version=3af1336a77e1d418e556462a2922987d", second, "Juggernaut"));
        mRepository.addAbility(new Ability("Blade Dance", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/83/Blade_Dance_icon.png?version=98dccd8db20dac51417e2e60805831f4", third, "Juggernaut"));
        mRepository.addAbility(new Ability("Omnislash", 140000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/39/Omnislash_icon.png?version=da2b7212f7af40d9ef35b0aead456603", ultimate, "Juggernaut"));

        mRepository.addAbility(new Ability("Summon Spirit Bear", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5d/Summon_Spirit_Bear_icon.png?version=f0510716cffd80c303e011893ebb6281", first, "Lone Druid"));
        mRepository.addAbility(new Ability("Spirit Link", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/42/Spirit_Link_icon.png?version=44298666d46deab1513d3f3a665b7899", second, "Lone Druid"));
        mRepository.addAbility(new Ability("Savage Roar", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/31/Savage_Roar_icon.png?version=7eb19b7777ddf77627bb65104ef04e00", third, "Lone Druid"));
        mRepository.addAbility(new Ability("True Form", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/64/True_Form_icon.png?version=e50f92434b21ec31c486d260b683e517", ultimate, "Lone Druid"));
        mRepository.addAbility(new Ability("Return", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b8/Return_%28Spirit_Bear%29_icon.png?version=a4004eac86f8edfcd2cb3f4e0c10c1aa", uniqueAbilityOrScepterUpgrade, "Lone Druid"));

        mRepository.addAbility(new Ability("Lucent Beam", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f0/Lucent_Beam_icon.png?version=3a08231d3f0c491d88bf424b28940b72", first, "Luna"));
        mRepository.addAbility(new Ability("Moon Glaives", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a8/Moon_Glaives_icon.png?version=0a09526d112807b4ec5ad7ba10171a77", second, "Luna"));
        mRepository.addAbility(new Ability("Lunar Blessing", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/49/Lunar_Blessing_icon.png?version=5b66e6b8289f4185adc630fc89f6f09c", third, "Luna"));
        mRepository.addAbility(new Ability("Eclipse", 140000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2c/Eclipse_icon.png?version=3a72d57b45d7876c7b2810ab7c191028", ultimate, "Luna"));

        mRepository.addAbility(new Ability("Split Shot", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/17/Split_Shot_icon.png?version=f7be56db92c7d8506c74e144cc129387", first, "Medusa"));
        mRepository.addAbility(new Ability("Mystic Snake", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7e/Mystic_Snake_icon.png?version=0f161148d72ee14b7d997299307b027d", second, "Medusa"));
        mRepository.addAbility(new Ability("Mana Shield", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Mana_Shield_icon.png?version=eb5c82dda178746efbe723a0fe266683", third, "Medusa"));
        mRepository.addAbility(new Ability("Stone Gaze", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6c/Stone_Gaze_icon.png?version=53d7989a3b1d361360d28b4cf82e5642", ultimate, "Medusa"));

        mRepository.addAbility(new Ability("Earthbind", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/36/Earthbind_icon.png?version=710fd37f3130efe9ef08e55378e49015", first, "Meepo"));
        mRepository.addAbility(new Ability("Poof", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/17/Poof_icon.png?version=61f988a0162697ce2c64ac5913adcdcf", second, "Meepo"));
        mRepository.addAbility(new Ability("Ransack", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/51/Ransack_icon.png?version=c18520a9014f2899215f6b57e5d60636", third, "Meepo"));
        mRepository.addAbility(new Ability("Divided We Stand", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/07/Divided_We_Stand_icon.png?version=5136563eebbfee7e754e4f0dc3b6773d", ultimate, "Meepo"));

        mRepository.addAbility(new Ability("Starstorm", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Starstorm_icon.png?version=53264fb4e9a415e888819d69b7fac0f7", first, "Mirana"));
        mRepository.addAbility(new Ability("Sacred Arrow", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/49/Sacred_Arrow_icon.png?version=259a5fea041829c81674f6a98c157bca", second, "Mirana"));
        mRepository.addAbility(new Ability("Leap", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/88/Leap_icon.png?version=58dcd728d2403e393f8d6f637fa28bad", third, "Mirana"));
        mRepository.addAbility(new Ability("Moonlight Shadow", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6b/Moonlight_Shadow_icon.png?version=046a832bfc96d3a5d293fb0051469582", ultimate, "Mirana"));

        mRepository.addAbility(new Ability("Boundless Strike", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/76/Boundless_Strike_icon.png?version=ed56986b26b6773c96ce6aa8c59bafe7", first, "Monkey King"));
        mRepository.addAbility(new Ability("Primal Spring", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/96/Primal_Spring_icon.png?version=cedee16479eebfbd7d4e099cd0483f5d", second, "Monkey King"));
        mRepository.addAbility(new Ability("Jingu Mastery", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7e/Jingu_Mastery_icon.png?version=8f859d6b1414a0fba13850a90fb90c44", third, "Monkey King"));
        mRepository.addAbility(new Ability("Wukong's Command", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4a/Wukong%27s_Command_icon.png?version=70bd3d82cbab1fa6246eae5c4f768c23", ultimate, "Monkey King"));
        mRepository.addAbility(new Ability("Mischief", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/80/Mischief_icon.png?version=f48bd775e1746e11a4c764feee1a54c0", uniqueAbilityOrScepterUpgrade, "Monkey King"));

        mRepository.addAbility(new Ability("Waveform", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/97/Waveform_icon.png?version=9e4020b8788d69b610eaa11b9542b813", first, "Morphling"));
        mRepository.addAbility(new Ability("Adaptive Strike (Agility)", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Adaptive_Strike_%28Agility%29_icon.png?version=3ef416cde8fff6dd761788b040dddaaa", second, "Morphling"));
        mRepository.addAbility(new Ability("Adaptive Strike (Strength)", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/78/Adaptive_Strike_%28Strength%29_icon.png?version=a8116ba8aa4768417ce4401a719681c0", third, "Morphling"));
        mRepository.addAbility(new Ability("Attribute Shift (Agility Gain)", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a0/Attribute_Shift_%28Agility_Gain%29_icon.png?version=7ae5aa8d16ce7d5bed32b15c1d0ffc7f", fourth, "Morphling"));
        mRepository.addAbility(new Ability("Attribute Shift (Strength Gain)", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/27/Attribute_Shift_%28Strength_Gain%29_icon.png?version=a3940ea04d6c246ae98129ca1f5f664f", fifth, "Morphling"));
        mRepository.addAbility(new Ability("Morph", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8f/Morph_icon.png?version=decee8d1e97ce2bdb1d5f6063a6cf2d5", ultimate, "Morphling"));

        mRepository.addAbility(new Ability("Mirror Image", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6d/Mirror_Image_icon.png?version=62bd833b640d994e4d93e6a8bfd86120", first, "Naga Siren"));
        mRepository.addAbility(new Ability("Ensnare", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/31/Ensnare_icon.png?version=8cc1629619b6089b38a5e7d1212589d8", second, "Naga Siren"));
        mRepository.addAbility(new Ability("Rip Tide", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/21/Rip_Tide_icon.png?version=12968f3bee2e76abb6c9c8600742edf8", third, "Naga Siren"));
        mRepository.addAbility(new Ability("Song of the Siren", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Song_of_the_Siren_icon.png?version=6810a707a9ad8e211db4df4e522af52f", ultimate, "Naga Siren"));

        mRepository.addAbility(new Ability("Impale", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2f/Impale_icon.png?version=e4f6bff37d8584786d97e6d54aaebc28", first, "Nyx Assassin"));
        mRepository.addAbility(new Ability("Mana Burn", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b6/Mana_Burn_icon.png?version=977c8bca8eb5d196cab86446c6de6e9b", second, "Nyx Assassin"));
        mRepository.addAbility(new Ability("Spiked Carapace", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/28/Spiked_Carapace_icon.png?version=a1124e2227be8f77cfbd14545b0c8299", third, "Nyx Assassin"));
        mRepository.addAbility(new Ability("Vendetta", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/40/Vendetta_icon.png?version=0c5e5cc225ab22964b02e895d1491abe", ultimate, "Nyx Assassin"));
        mRepository.addAbility(new Ability("Burrow", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b8/Burrow_icon.png?version=7baab232897212066629ecc77f1b2c98", uniqueAbilityOrScepterUpgrade, "Nyx Assassin"));

        mRepository.addAbility(new Ability("Swashbuckle", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/93/Swashbuckle_icon.png?version=fa400e3e7428092e5dab78e761411593", first, "Pangolier"));
        mRepository.addAbility(new Ability("Shield Crash", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/10/Shield_Crash_icon.png?version=8af25916baa424a0045af0d7ff7c187c", second, "Pangolier"));
        mRepository.addAbility(new Ability("Lucky Shot", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fd/Lucky_Shot_icon.png?version=831686f2be641ab117adca43b6299e23", third, "Pangolier"));
        mRepository.addAbility(new Ability("Rolling Thunder", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/17/Rolling_Thunder_icon.png?version=98550ee94de41304944883a415ff0f48", ultimate, "Pangolier"));

        mRepository.addAbility(new Ability("Stifling Dagger", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1c/Stifling_Dagger_icon.png?version=1ba4e745c156ed444913b50171484641", first, "Phantom Assassin"));
        mRepository.addAbility(new Ability("Phantom Strike", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Phantom_Strike_icon.png?version=49e966c74170357b23d3c0068a287d03", second, "Phantom Assassin"));
        mRepository.addAbility(new Ability("Blur", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ef/Blur_icon.png?version=08697c0ef3527bacae00a5861f013353", third, "Phantom Assassin"));
        mRepository.addAbility(new Ability("Coup de Grace", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0f/Coup_de_Grace_icon.png?version=46361fccdb71c79386c6b0fa7e6b048b", ultimate, "Phantom Assassin"));

        mRepository.addAbility(new Ability("Spirit Lance", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fd/Spirit_Lance_icon.png?version=9cecc2de7d3b62dc3f3365b4d9976611", first, "Phantom Lancer"));
        mRepository.addAbility(new Ability("Doppelganger", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e2/Doppelganger_icon.png?version=784651df10d8f765475bc3e9313bc35e", second, "Phantom Lancer"));
        mRepository.addAbility(new Ability("Phantom Rush", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Phantom_Rush_icon.png?version=3ae306ab763482b17c0e39d4a74e01cc", third, "Phantom Lancer"));
        mRepository.addAbility(new Ability("Juxtapose", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e4/Juxtapose_icon.png?version=11e073dee3f4a14ec8c65787eee47be2", ultimate, "Phantom Lancer"));

        mRepository.addAbility(new Ability("Plasma Field", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c9/Plasma_Field_icon.png?version=ff8876bf37163b8a163ff6606c2b6035", first, "Razor"));
        mRepository.addAbility(new Ability("Static Link", 25000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/01/Static_Link_icon.png?version=28e5c4f3145c35ddfbe3f2e3a528149e", second, "Razor"));
        mRepository.addAbility(new Ability("Storm Surge", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c3/Storm_Surge_icon.png?version=142e922c2a84a1b457b50162df729bc3", third, "Razor"));
        mRepository.addAbility(new Ability("Eye of the Storm", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3c/Eye_of_the_Storm_icon.png?version=6cd5b2cde568cd80adc36e04de9b9f4f", ultimate, "Razor"));

        mRepository.addAbility(new Ability("Smoke Screen", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7a/Smoke_Screen_icon.png?version=e2631241cc457ce232e19e7f77e4c6b4", first, "Riki"));
        mRepository.addAbility(new Ability("Blink Strike", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/30/Blink_Strike_icon.png?version=039fd01a49a7552a7a664a0bda562ff6", second, "Riki"));
        mRepository.addAbility(new Ability("Tricks of the Trade", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Tricks_of_the_Trade_icon.png?version=8ed0e5fa4108ab9239ccb78c740a4065", third, "Riki"));
        mRepository.addAbility(new Ability("Cloak and Dagger", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f0/Cloak_and_Dagger_icon.png?version=e70acb1850720f96e8c37b8ead585877", ultimate, "Riki"));

        mRepository.addAbility(new Ability("Shadowraze (Near)", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7c/Shadowraze_%28Near%29_icon.png?version=2a9b7b5206dc974b672214772c5cbffd", first, "Shadow Fiend"));
        mRepository.addAbility(new Ability("Shadowraze (Medium)", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c1/Shadowraze_%28Medium%29_icon.png?version=c3375b9feff72334a6af3dcca5b8a568", second, "Shadow Fiend"));
        mRepository.addAbility(new Ability("Shadowraze (Far)", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a6/Shadowraze_%28Far%29_icon.png?version=080b788c62e7f3093feedb071cc3ec08", third, "Shadow Fiend"));
        mRepository.addAbility(new Ability("Necromastery", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9d/Necromastery_icon.png?version=4f15bb7d98f1972e19539458dfe20ae3", fourth, "Shadow Fiend"));
        mRepository.addAbility(new Ability("Presence of the Dark Lord", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6e/Presence_of_the_Dark_Lord_icon.png?version=914b59e6604c65548d0647c763bef842", fifth, "Shadow Fiend"));
        mRepository.addAbility(new Ability("Requiem of Souls", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a8/Requiem_of_Souls_icon.png?version=97649c7549c8e8b1c1fc578b84082725", ultimate, "Shadow Fiend"));

        mRepository.addAbility(new Ability("Dark Pact", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8d/Dark_Pact_icon.png?version=6c4786148cc839e6eba331dc76fdb629", first, "Slark"));
        mRepository.addAbility(new Ability("Pounce", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/25/Pounce_icon.png?version=bb90753af465496138f41db19f4ede65", second, "Slark"));
        mRepository.addAbility(new Ability("Essence Shift", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6e/Essence_Shift_icon.png?version=f5fb0fdf0cd5fdf51bc8e74ae6e4abf2", third, "Slark"));
        mRepository.addAbility(new Ability("Shadow Dance", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2c/Shadow_Dance_icon.png?version=9720a4ca5c65884e2df0a11581fa22a9", ultimate, "Slark"));

        mRepository.addAbility(new Ability("Shrapnel", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/05/Shrapnel_icon.png?version=681ede25e20d11d91ca2836063cf1299", first, "Sniper"));
        mRepository.addAbility(new Ability("Headshot", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e5/Headshot_icon.png?version=964ae3cfe9258fea4b5a045fe3de6e8e", second, "Sniper"));
        mRepository.addAbility(new Ability("Take Aim", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/64/Take_Aim_icon.png?version=163a10cc7efc67f637b495e280bba16c", third, "Sniper"));
        mRepository.addAbility(new Ability("Assassinate", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/16/Assassinate_icon.png?version=733caba66f99661121b170a927e64e76", ultimate, "Sniper"));

        mRepository.addAbility(new Ability("Spectral Dagger", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/db/Spectral_Dagger_icon.png?version=44a34f4b87eabc632fa8940e9006c44c", first, "Spectre"));
        mRepository.addAbility(new Ability("Desolate", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/06/Desolate_icon.png?version=cb083cf80f6fea1f668d3754ac931447", second, "Spectre"));
        mRepository.addAbility(new Ability("Dispersion", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a4/Dispersion_icon.png?version=743312404cf239420e6f1ec5cb162b4b", third, "Spectre"));
        mRepository.addAbility(new Ability("Haunt", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b4/Haunt_icon.png?version=196bb31069fa8ae1aaab1b3d709e706f", ultimate, "Spectre"));
        mRepository.addAbility(new Ability("Shadow Step", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ee/Shadow_Step_icon.png?version=679798fb062b450eca86231679ab3249", uniqueAbilityOrScepterUpgrade, "Spectre"));

        mRepository.addAbility(new Ability("Refraction", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f9/Refraction_icon.png?version=5428feb903868199590508e2b50fc6d1", first, "Templar Assassin"));
        mRepository.addAbility(new Ability("Meld", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Meld_icon.png?version=37d8605571ea7e0e5f97c69ad1f6ceb2", second, "Templar Assassin"));
        mRepository.addAbility(new Ability("Psi Blades", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/78/Psi_Blades_icon.png?version=e45831497493d03f0457021aef41d684", third, "Templar Assassin"));
        mRepository.addAbility(new Ability("Psionic Trap", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a7/Psionic_Trap_icon.png?version=d662309c2a1f91b4ccaf66ff401be064", ultimate, "Templar Assassin"));

        mRepository.addAbility(new Ability("Reflection", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8b/Reflection_icon.png?version=335b97b4b6321e79729199dd848b7e40", first, "Terrorblade"));
        mRepository.addAbility(new Ability("Conjure Image", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9b/Conjure_Image_icon.png?version=3f5c42bc779341e68ce48ad8b4288193", second, "Terrorblade"));
        mRepository.addAbility(new Ability("Metamorphosis", 155000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/85/Metamorphosis_icon.png?version=7f7270a5bf27c19b4a5d7a9bd9421330", third, "Terrorblade"));
        mRepository.addAbility(new Ability("Sunder", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/15/Sunder_icon.png?version=b951cbe436fc9d96cbdcc66ff39f3d53", ultimate, "Terrorblade"));

        mRepository.addAbility(new Ability("Berserker's Rage", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/79/Berserker%27s_Rage_icon.png?version=9098d3afff99e0b8ad42ddc37b8cac8e", first, "Troll Warlord"));
        mRepository.addAbility(new Ability("Whirling Axes (Ranged)", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/00/Whirling_Axes_%28Ranged%29_icon.png?version=4bc225930ddc390239e989200994a1e1", second, "Troll Warlord"));
        mRepository.addAbility(new Ability("Whirling Axes (Melee)", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/68/Whirling_Axes_%28Melee%29_icon.png?version=09c0dfcb00d667245b43b62c920aaab3", third, "Troll Warlord"));
        mRepository.addAbility(new Ability("Fervor", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c4/Fervor_icon.png?version=2eb26c85fb1b97f98afbcf44d88d6ca5", fourth, "Troll Warlord"));
        mRepository.addAbility(new Ability("Battle Trance", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/70/Battle_Trance_icon.png?version=81e80a8b84e99a4b2a23dee390811e1a", ultimate, "Troll Warlord"));

        mRepository.addAbility(new Ability("Earthshock", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3a/Earthshock_icon.png?version=417dfc7acd2ecac1d87804cf19661756", first, "Ursa"));
        mRepository.addAbility(new Ability("Overpower", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fd/Overpower_icon.png?version=28d9a23863509451b90c1aaf608b9729", second, "Ursa"));
        mRepository.addAbility(new Ability("Fury Swipes", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2f/Fury_Swipes_icon.png?version=eec391db187006cfb8098ebe8b30bda1", third, "Ursa"));
        mRepository.addAbility(new Ability("Enrage", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/52/Enrage_icon.png?version=c51c2c490681934f36858f3a40f84f06", ultimate, "Ursa"));

        mRepository.addAbility(new Ability("Magic Missile", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Magic_Missile_icon.png?version=93f2a8141c6344fcbcbc13d3457f91d0", first, "Vengeful Spirit"));
        mRepository.addAbility(new Ability("Wave of Terror", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/34/Wave_of_Terror_icon.png?version=ac96ca1e3b6bebcbff01a06d88363bb5", second, "Vengeful Spirit"));
        mRepository.addAbility(new Ability("Vengeance Aura", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/28/Vengeance_Aura_icon.png?version=7c513b20e397c6e87070b696a1c09bd7", third, "Vengeful Spirit"));
        mRepository.addAbility(new Ability("Nether Swap", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/11/Nether_Swap_icon.png?version=aa47e7487c368a0537dfb6eb9d9a5597", ultimate, "Vengeful Spirit"));

        mRepository.addAbility(new Ability("Venomous Gale", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0c/Venomous_Gale_icon.png?version=207a88b8499e79378c6595c81d22fc17", first, "Venomancer"));
        mRepository.addAbility(new Ability("Poison Sting", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/69/Poison_Sting_icon.png?version=58e4b45c7b498c949afa4dc1e8a4b095", second, "Venomancer"));
        mRepository.addAbility(new Ability("Plague Ward", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/67/Plague_Ward_icon.png?version=6fe2f429ed7f08e03a39fed73bff1970", third, "Venomancer"));
        mRepository.addAbility(new Ability("Poison Nova", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0a/Poison_Nova_icon.png?version=f888997bda5f555de8f792a231ef065c", ultimate, "Venomancer"));

        mRepository.addAbility(new Ability("Poison Attack", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1b/Poison_Attack_icon.png?version=7dd2ed3c4eb3d68b4cc146f3b12f7f5e", first, "Viper"));
        mRepository.addAbility(new Ability("Nethertoxin", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/95/Nethertoxin_icon.png?version=b69567670ed56a2966cce9f544d15f56", second, "Viper"));
        mRepository.addAbility(new Ability("Corrosive Skin", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ea/Corrosive_Skin_icon.png?version=2836b8a3ae7ddc0b09944639fca268a3", third, "Viper"));
        mRepository.addAbility(new Ability("Viper Strike", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9b/Viper_Strike_icon.png?version=5893579ee4afe96c2867541c704404da", ultimate, "Viper"));

        mRepository.addAbility(new Ability("The Swarm", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7e/The_Swarm_icon.png?version=4569daf1cf202701d9007a461a9fdb9e", first, "Weaver"));
        mRepository.addAbility(new Ability("Shukuchi", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/24/Shukuchi_icon.png?version=8554d05245ee48eddba6fb0a21ae43f9", second, "Weaver"));
        mRepository.addAbility(new Ability("Geminate Attack", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2d/Geminate_Attack_icon.png?version=8dbab7de4c451e07804f24f8a086f0ba", third, "Weaver"));
        mRepository.addAbility(new Ability("Time Lapse", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/16/Time_Lapse_icon.png?version=6556395c736d2979ea6c1df989b224af", ultimate, "Weaver"));

        mRepository.addAbility(new Ability("Cold Feet", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/52/Cold_Feet_icon.png?version=c4c400d8cce0245d71ec1487407fe3f1", first, "Ancient Apparition"));
        mRepository.addAbility(new Ability("Ice Vortex", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Ice_Vortex_icon.png?version=129270e30a4800ad159b55d33681fa25", second, "Ancient Apparition"));
        mRepository.addAbility(new Ability("Chilling Touch", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/71/Chilling_Touch_icon.png?version=0c83fb181ccd032ef698d19afecb1652", third, "Ancient Apparition"));
        mRepository.addAbility(new Ability("Ice Blast", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f4/Ice_Blast_icon.png?version=143eb912ee878d7b10d42da68e3ac072", ultimate, "Ancient Apparition"));

        mRepository.addAbility(new Ability("Nightmare", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6e/Nightmare_icon.png?version=acbf849b29b9ea3b7b7052e49c83554d", first, "Bane"));
        mRepository.addAbility(new Ability("Brain Sap", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0c/Brain_Sap_icon.png?version=f25536011f8f519e24be88d47dea86c5", second, "Bane"));
        mRepository.addAbility(new Ability("Enfeeble", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/40/Enfeeble_icon.png?version=2471cbdc53491309183f7256c987360e", third, "Bane"));
        mRepository.addAbility(new Ability("Fiend's Grip", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7b/Fiend%27s_Grip_icon.png?version=57c37da3399cb4f8e464162e376b7571", ultimate, "Bane"));

        mRepository.addAbility(new Ability("Sticky Napalm", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/76/Sticky_Napalm_icon.png?version=9d23a249875d25a79b402096fc5ead30", first, "Batrider"));
        mRepository.addAbility(new Ability("Flamebreak", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/04/Flamebreak_icon.png?version=7170f09bc6d5dfca25ec941e831590e7", second, "Batrider"));
        mRepository.addAbility(new Ability("Firefly", 34000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2c/Firefly_icon.png?version=a13d796a5258c096e95f0fa55e171cb4", third, "Batrider"));
        mRepository.addAbility(new Ability("Flaming Lasso", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Flaming_Lasso_icon.png?version=d24bb7458b7a1d38fea8a4365ae09365", ultimate, "Batrider"));

        mRepository.addAbility(new Ability("Penitence", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9e/Penitence_icon.png?version=4967cead22302b46557af4a693be3432", first, "Chen"));
        mRepository.addAbility(new Ability("Holy Persuasion", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/43/Holy_Persuasion_icon.png?version=12de269afb18cc958e6fcd407600556b", second, "Chen"));
        mRepository.addAbility(new Ability("Divine Favor", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/14/Divine_Favor_icon.png?version=03e76c0dc2e38344ad8779d681c7df21", third, "Chen"));
        mRepository.addAbility(new Ability("Hand of God", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c9/Hand_of_God_icon.png?version=c0176c99982c4f79a0b91227ecf1b2dc", ultimate, "Chen"));

        mRepository.addAbility(new Ability("Crystal Nova", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/72/Crystal_Nova_icon.png?version=fd67c308b83f6ef52a46fe5a7e79e508", first, "Crystal Maiden"));
        mRepository.addAbility(new Ability("Frostbite", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d6/Frostbite_icon.png?version=8ecc5d6dcc12eaa01b4135f5d06edd2f", second, "Crystal Maiden"));
        mRepository.addAbility(new Ability("Arcane Aura", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5a/Arcane_Aura_icon.png?version=6f9e4cad7576371647d40a154c446375", third, "Crystal Maiden"));
        mRepository.addAbility(new Ability("Freezing Field", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/de/Freezing_Field_icon.png?version=6c9349f169899479766e28e76604309a", ultimate, "Crystal Maiden"));

        mRepository.addAbility(new Ability("Vacuum", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e3/Vacuum_icon.png?version=4a28c99ffe765f0e55d57a4e45f8988c", first, "Dark Seer"));
        mRepository.addAbility(new Ability("Ion Shell", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b5/Ion_Shell_icon.png?version=ff7976fb39fa4eca1d66ec789716fb69", second, "Dark Seer"));
        mRepository.addAbility(new Ability("Surge", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0f/Surge_icon.png?version=a62d371089914fca286ba164be08ac45", third, "Dark Seer"));
        mRepository.addAbility(new Ability("Wall of Replica", 100000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c6/Wall_of_Replica_icon.png?version=e127e0cd9fc6f15eb30eb95aac557b5d", ultimate, "Dark Seer"));

        mRepository.addAbility(new Ability("Bramble Maze", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/21/Bramble_Maze_icon.png?version=a013daeefa263d5f7f85906beafc6e0a", first, "Dark Willow"));
        mRepository.addAbility(new Ability("Shadow Realm", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5e/Shadow_Realm_icon.png?version=60c1762f0485cc12c5ecc73e1e53f6f4", second, "Dark Willow"));
        mRepository.addAbility(new Ability("Cursed Crown", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5e/Cursed_Crown_icon.png?version=d52362374e93f352ec7e30ec70caacb3", third, "Dark Willow"));
        mRepository.addAbility(new Ability("Bedlam", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b1/Bedlam_icon.png?version=1e9e18996f198424ec64a03a68202b2d", ultimate, "Dark Willow"));
        mRepository.addAbility(new Ability("Terrorize", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/be/Terrorize_icon.png?version=29cf772e5559f3eae13eaa1b2406cfa9", ultimate + 1, "Dark Willow"));

        mRepository.addAbility(new Ability("Poison Touch", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/09/Poison_Touch_icon.png?version=7790e7fc061c614e43f707b314b015bd", first, "Dazzle"));
        mRepository.addAbility(new Ability("Shallow Grave", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/69/Shallow_Grave_icon.png?version=332d139e7fbee526979554e0c610311c", second, "Dazzle"));
        mRepository.addAbility(new Ability("Shadow Wave", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/12/Shadow_Wave_icon.png?version=e39da430b53aa696b28ae9b1a18bbb8b", third, "Dazzle"));
        mRepository.addAbility(new Ability("Bad Juju", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Bad_Juju_icon.png?version=d1c0055129aa4246b5236a5f2596647a", ultimate, "Dazzle"));

        mRepository.addAbility(new Ability("Crypt Swarm", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bc/Crypt_Swarm_icon.png?version=779d38a1c6d6b62d312bbb8f292f5ce1", first, "Death Prophet"));
        mRepository.addAbility(new Ability("Silence", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/49/Silence_icon.png?version=f3ff87c9ca72dd71ee43a0a64b2ead73", second, "Death Prophet"));
        mRepository.addAbility(new Ability("Spirit Siphon", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/83/Spirit_Siphon_icon.png?version=e71a827067ecafa99d4117354a109024", third, "Death Prophet"));
        mRepository.addAbility(new Ability("Exorcism", 145000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/71/Exorcism_icon.png?version=3b8e8f4c4e3cd4c0cd302f7a2918faf3", ultimate, "Death Prophet"));

        mRepository.addAbility(new Ability("Thunder Strike", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5e/Thunder_Strike_icon.png?version=2e0aa3d43ec6f8d390a574c81b7d6b64", first, "Disruptor"));
        mRepository.addAbility(new Ability("Glimpse", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ed/Glimpse_icon.png?version=d4cb1a45b6042e13d98db3e2c44f75ea", second, "Disruptor"));
        mRepository.addAbility(new Ability("Kinetic Field", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7f/Kinetic_Field_icon.png?version=441fc350e8d9395a77ab717ae16f3d4f", third, "Disruptor"));
        mRepository.addAbility(new Ability("Static Storm", 70000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Static_Storm_icon.png?version=1dc126afabcccc0b3982e8e127ddd396", ultimate, "Disruptor"));

        mRepository.addAbility(new Ability("Impetus", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c9/Impetus_icon.png?version=01f46d5d3c27a0bcbe0fb550d9025757", first, "Enchantress"));
        mRepository.addAbility(new Ability("Enchant", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3a/Enchant_icon.png?version=031c7febe9d08931e1e0f720347ad5a7", second, "Enchantress"));
        mRepository.addAbility(new Ability("Nature's Attendants", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/50/Nature%27s_Attendants_icon.png?version=770eba9edf69b06cea7c7eb5e1b5b2fc", third, "Enchantress"));
        mRepository.addAbility(new Ability("Untouchable", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0d/Untouchable_icon.png?version=45811629b698b0d089c7b28af5126e22", ultimate, "Enchantress"));
        mRepository.addAbility(new Ability("Sproink", 3000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/42/Sproink_icon.png?version=91f899b3e9e98506f8f47307dee099b8", uniqueAbilityOrScepterUpgrade, "Enchantress"));

        mRepository.addAbility(new Ability("Malefice", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c1/Malefice_icon.png?version=0263ba45afb5ea374101e2e4cad473c8", first, "Enigma"));
        mRepository.addAbility(new Ability("Demonic Conversion", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d5/Demonic_Conversion_icon.png?version=a839866f44f6e8fc100362edbd06a8d2", second, "Enigma"));
        mRepository.addAbility(new Ability("Midnight Pulse", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/09/Midnight_Pulse_icon.png?version=99dc1f25c2970542fdeb70117b2e46e0", third, "Enigma"));
        mRepository.addAbility(new Ability("Black Hole", 160000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b8/Black_Hole_icon.png?version=f50f650c6a5855adeaa376c45878244e", ultimate, "Enigma"));

        mRepository.addAbility(new Ability("Stroke of Fate", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a7/Stroke_of_Fate_icon.png?version=8052b0b261593c2c728cde8199d2210c", first, "Grimstroke"));
        mRepository.addAbility(new Ability("Phantom's Embrace", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c6/Phantom%27s_Embrace_icon.png?version=3231215bba31c6f655320c2529b9e7a9", second, "Grimstroke"));
        mRepository.addAbility(new Ability("Ink Swell", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d7/Ink_Swell_icon.png?version=b589bc8ce8144ca2ae66e5a29a7beec5", third, "Grimstroke"));
        mRepository.addAbility(new Ability("Soulbind", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f9/Soulbind_icon.png?version=12dd3d07d271e3f3abb5cbecd28bff43", ultimate, "Grimstroke"));
        mRepository.addAbility(new Ability("Dark Portrait", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Dark_Portrait_icon.png?version=ead84e6a6874dc36888359703f1313e1", uniqueAbilityOrScepterUpgrade, "Grimstroke"));

        mRepository.addAbility(new Ability("Cold Snap", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fb/Cold_Snap_icon.png?version=6802868170229493de0c7690bf8081d8", 1, "Invoker"));
        mRepository.addAbility(new Ability("Ghost Walk", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c8/Ghost_Walk_icon.png?version=4587b45bd735ddf458a127fe2582236f", 2, "Invoker"));
        mRepository.addAbility(new Ability("Ice Wall", 25000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2d/Ice_Wall_icon.png?version=40d31e65cf7a9873c5f41d252e99ec35", 3, "Invoker"));
        mRepository.addAbility(new Ability("EMP", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/97/EMP_icon.png?version=f9bbd9894f1b9bcccad34c8de062b795", 4, "Invoker"));
        mRepository.addAbility(new Ability("Tornado", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e1/Tornado_icon.png?version=046d4227391f333810470e092879bfb7", 5, "Invoker"));
        mRepository.addAbility(new Ability("Alacrity", 17000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1b/Alacrity_icon.png?version=e1c1a1ee8dcb10ae76e7c4aea8fff33a", 6, "Invoker"));
        mRepository.addAbility(new Ability("Sun Strike", 25000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/83/Sun_Strike_icon.png?version=395feddf4f877eb8db60678f4d53cdf6", 7, "Invoker"));
        mRepository.addAbility(new Ability("Forge Spirit", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cb/Forge_Spirit_icon.png?version=53452b75ee6d8d9e6f69967adb60e8f1", 8, "Invoker"));
        mRepository.addAbility(new Ability("Chaos Meteor", 55000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/76/Chaos_Meteor_icon.png?version=7d80cecc5233fd4fdd554a93b472acbf", 9, "Invoker"));
        mRepository.addAbility(new Ability("Deafening Blast", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/dd/Deafening_Blast_icon.png?version=a30971b48a7fdcd593d0c8025f7ff3a2", 10, "Invoker"));

        mRepository.addAbility(new Ability("Dual Breath", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/af/Dual_Breath_icon.png?version=0aa16febde10e1e1b7e31c6f82605970", first, "Jakiro"));
        mRepository.addAbility(new Ability("Ice Path", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fe/Ice_Path_icon.png?version=667bf9261bc96ea5a5992078dfb45722", second, "Jakiro"));
        mRepository.addAbility(new Ability("Liquid Fire", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/67/Liquid_Fire_icon.png?version=4b12dfb1b02ed2875b206dfd915dbaa4", third, "Jakiro"));
        mRepository.addAbility(new Ability("Macropyre", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bd/Macropyre_icon.png?version=ca48ebe15241a9e4bf248459ef47166e", ultimate, "Jakiro"));

        mRepository.addAbility(new Ability("Illuminate", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bd/Illuminate_icon.png?version=9f6af711b4e3c49cc2d1756c4b34c704", first, "Keeper of the Light"));
        mRepository.addAbility(new Ability("Blinding Light", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/df/Blinding_Light_icon.png?version=1537a641d2448df08964fee4244f8e98", second, "Keeper of the Light"));
        mRepository.addAbility(new Ability("Chakra Magic", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Chakra_Magic_icon.png?version=b7a1f6a8448b11624b415b3c4556d19d", third, "Keeper of the Light"));
        mRepository.addAbility(new Ability("Will-O-Wisp", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d9/Will-O-Wisp_icon.png?version=d0d14b5692e2e585e68966cc7199494e", ultimate, "Keeper of the Light"));

        mRepository.addAbility(new Ability("Split Earth", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1a/Split_Earth_icon.png?version=8bf7966a5842c842ee19226bc012ba69", first, "Leshrac"));
        mRepository.addAbility(new Ability("Diabolic Edict", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/49/Diabolic_Edict_icon.png?version=3e738ba96d5156f45ba7e81979ac3058", second, "Leshrac"));
        mRepository.addAbility(new Ability("Lightning Storm", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b0/Lightning_Storm_icon.png?version=2d0ff7c5dcad1ab7afa2216b63a93947", third, "Leshrac"));
        mRepository.addAbility(new Ability("Pulse Nova", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/ca/Pulse_Nova_icon.png?version=bb69dfdebdaeff01e919ef26b7d36bb0", ultimate, "Leshrac"));

        mRepository.addAbility(new Ability("Frost Blast", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6c/Frost_Blast_icon.png?version=5bbff701f43c833400372d9af0dea19f", first, "Lich"));
        mRepository.addAbility(new Ability("Frost Shield", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1b/Frost_Shield_icon.png?version=7168686495bdfb4f5adf44a586e1dfbd", second, "Lich"));
        mRepository.addAbility(new Ability("Sinister Gaze", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1f/Sinister_Gaze_icon.png?version=0e711df7b8b4f9ba7bff90a2ed2ab637", third, "Lich"));
        mRepository.addAbility(new Ability("Chain Frost", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/77/Chain_Frost_icon.png?version=59373c16ce40da8f075cb2a7dff07d4a", ultimate, "Lich"));

        mRepository.addAbility(new Ability("Dragon Slave", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/93/Dragon_Slave_icon.png?version=d02f4f09162b7548dde0b1208f03eea6", first, "Lina"));
        mRepository.addAbility(new Ability("Light Strike Array", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a1/Light_Strike_Array_icon.png?version=67adc3ca8bf0d671a928d5ae4e2fb141", second, "Lina"));
        mRepository.addAbility(new Ability("Fiery Soul", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/be/Fiery_Soul_icon.png?version=92d0d544ac96153ef514f341639716c5", third, "Lina"));
        mRepository.addAbility(new Ability("Laguna Blade", 50000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/83/Laguna_Blade_icon.png?version=fab88196b2430c1e84be59c3b22cafeb", ultimate, "Lina"));

        mRepository.addAbility(new Ability("Earth Spike", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/03/Earth_Spike_icon.png?version=487ca39740ccb5c7539dbf19b8d16762", first, "Lion"));
        mRepository.addAbility(new Ability("Hex", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2c/Hex_%28Lion%29_icon.png?version=2697c2965749b333b4183cf6b1f98129", second, "Lion"));
        mRepository.addAbility(new Ability("Mana Drain", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1a/Mana_Drain_icon.png?version=405741e76b07cd1134a9bf950251eeb2", third, "Lion"));
        mRepository.addAbility(new Ability("Finger of Death", 40000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d4/Finger_of_Death_icon.png?version=a16ec2bc43688260b122dda540ca11d9", ultimate, "Lion"));

        mRepository.addAbility(new Ability("Sprout", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/50/Sprout_icon.png?version=430b67c6a273e11e7d58dde4ebb11919", first, "Nature's Prophet"));
        mRepository.addAbility(new Ability("Teleportation", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/59/Teleportation_icon.png?version=dd7492b7dddf069d05899ac3cb95f49e", second, "Nature's Prophet"));
        mRepository.addAbility(new Ability("Nature's Call", 37000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2f/Nature%27s_Call_icon.png?version=c85b9982ea811c5e644083b258a99511", third, "Nature's Prophet"));
        mRepository.addAbility(new Ability("Wrath of Nature", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/be/Wrath_of_Nature_icon.png?version=08d783bbc8c3dd1e5afbeacb7c1c6d75", ultimate, "Nature's Prophet"));

        mRepository.addAbility(new Ability("Death Pulse", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/32/Death_Pulse_icon.png?version=7ffb796a5c7d96e45b0e317e70d1fd32", first, "Necrophos"));
        mRepository.addAbility(new Ability("Ghost Shroud", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1f/Ghost_Shroud_icon.png?version=305d69894cd37fdf9c8c9e3125c66525", second, "Necrophos"));
        mRepository.addAbility(new Ability("Heartstopper Aura", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/17/Heartstopper_Aura_icon.png?version=70918257350df6c0aa043848920a03ca", third, "Necrophos"));
        mRepository.addAbility(new Ability("Reaper's Scythe", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/de/Reaper%27s_Scythe_icon.png?version=dc400cf26ed44a1bc5f931061ec1414b", ultimate, "Necrophos"));

        mRepository.addAbility(new Ability("Fireblast", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/30/Fireblast_icon.png?version=0a01480acd6d68061ccc1f35ef3bdb88", first, "Ogre Magi"));
        mRepository.addAbility(new Ability("Ignite", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e4/Ignite_icon.png?version=283713d4a05b59b15920d4a98fe3755d", second, "Ogre Magi"));
        mRepository.addAbility(new Ability("Bloodlust", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b4/Bloodlust_icon.png?version=cc36b148e7a1e15c0a98253a679a6804", third, "Ogre Magi"));
        mRepository.addAbility(new Ability("Multicast", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/87/Multicast_icon.png?version=d4c9f4a737f62af3e521041fd22d170d", ultimate, "Ogre Magi"));
        mRepository.addAbility(new Ability("Unrefined Fireblast", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/64/Unrefined_Fireblast_icon.png?version=bd862ca1207842016fbe84bd049cd035", uniqueAbilityOrScepterUpgrade, "Ogre Magi"));

        mRepository.addAbility(new Ability("Fortune's End", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/38/Fortune%27s_End_icon.png?version=678b02665bd90a8c1182677cd7f99eab", first, "Oracle"));
        mRepository.addAbility(new Ability("Fate's Edict", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/db/Fate%27s_Edict_icon.png?version=888d6ae0c84d05403c2a1bc729b549aa", second, "Oracle"));
        mRepository.addAbility(new Ability("Purifying Flames", 2250, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3e/Purifying_Flames_icon.png?version=05097c1358ede840f34668933859170a", third, "Oracle"));
        mRepository.addAbility(new Ability("False Promise", 45000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f0/False_Promise_icon.png?version=28c6fb26b531b88933f9c0e2294173dd", ultimate, "Oracle"));

        mRepository.addAbility(new Ability("Arcane Orb", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/68/Arcane_Orb_icon.png?version=881532622593bc0775c2dcc76e24e915", first, "Outworld Devourer"));
        mRepository.addAbility(new Ability("Astral Imprisonment", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/09/Astral_Imprisonment_icon.png?version=7894cd952601502f9c86e2a54432517f", second, "Outworld Devourer"));
        mRepository.addAbility(new Ability("Essence Flux", 25000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1a/Essence_Flux_icon.png?version=12ec9cace3367d8d148098e79b808fa7", third, "Outworld Devourer"));
        mRepository.addAbility(new Ability("Sanity's Eclipse", 160000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9f/Sanity%27s_Eclipse_icon.png?version=2c619f7e0fb4ec0d62088c3913378b83", ultimate, "Outworld Devourer"));

        mRepository.addAbility(new Ability("Illusory Orb", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Illusory_Orb_icon.png?version=d5131f9d283b92bc22c1134302731240", first, "Puck"));
        mRepository.addAbility(new Ability("Waning Rift", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/28/Waning_Rift_icon.png?version=a5c26cb2b758df72f3a70a8a458d6d9e", second, "Puck"));
        mRepository.addAbility(new Ability("Phase Shift", 3250, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/79/Phase_Shift_icon.png?version=8f00a187ca67f33977e93a6adedb1340", third, "Puck"));
        mRepository.addAbility(new Ability("Dream Coil", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/90/Dream_Coil_icon.png?version=b2c849084254dba17fb34101b2953c84", ultimate, "Puck"));

        mRepository.addAbility(new Ability("Nether Blast", 5000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/12/Nether_Blast_icon.png?version=670e3bfd038c0e3a742fd30f3fe4df8f", first, "Pugna"));
        mRepository.addAbility(new Ability("Decrepify", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c9/Decrepify_icon.png?version=86a916ebd7d5e16cc9b61009b06e5011", second, "Pugna"));
        mRepository.addAbility(new Ability("Nether Ward", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/fa/Nether_Ward_icon.png?version=257f56774b2804ba679c5e32f95a7843", third, "Pugna"));
        mRepository.addAbility(new Ability("Life Drain", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a7/Life_Drain_icon.png?version=e1d6eaee5a8d4df6c82d1fd9eccaa5d7", ultimate, "Pugna"));

        mRepository.addAbility(new Ability("Shadow Strike", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/a6/Shadow_Strike_icon.png?version=63dc6e752785c780dc2e9b2f16b4e1f8", first, "Queen of Pain"));
        mRepository.addAbility(new Ability("Blink", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/45/Blink_%28Queen_of_Pain%29_icon.png?version=870b3a74fc7dff7e95f93cd87e8d4a20", second, "Queen of Pain"));
        mRepository.addAbility(new Ability("Scream of Pain", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c8/Scream_of_Pain_icon.png?version=2aaba6b3c9f609a93955e0e32b99bf50", third, "Queen of Pain"));
        mRepository.addAbility(new Ability("Sonic Wave", 135000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b5/Sonic_Wave_icon.png?version=7977fef08a5a3aaab0561196482f89d3", ultimate, "Queen of Pain"));

        mRepository.addAbility(new Ability("Telekinesis", 22000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/0e/Telekinesis_icon.png?version=d1a0b371645303ea8c9b00cf152a0a8a", first, "Rubick"));
        mRepository.addAbility(new Ability("Fade Bolt", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/35/Fade_Bolt_icon.png?version=7d2e2f4ab481d4119f936a3e8e44ec63", second, "Rubick"));
        mRepository.addAbility(new Ability("Arcane Supremacy", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ee/Arcane_Supremacy_icon.png?version=995bdd7bad30ddf46524079dad6473c9", third, "Rubick"));
        mRepository.addAbility(new Ability("Spell Steal", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f4/Spell_Steal_icon.png?version=1cd94d21de0eafac0f07c621b3f7119f", ultimate, "Rubick"));

        mRepository.addAbility(new Ability("Disruption", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b6/Disruption_icon.png?version=0abdd0083a6c4d1047b05d26d653ef56", first, "Shadow Demon"));
        mRepository.addAbility(new Ability("Soul Catcher", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e8/Soul_Catcher_icon.png?version=cca32bb20d80fedcafe23d1693403eb8", second, "Shadow Demon"));
        mRepository.addAbility(new Ability("Shadow Poison", 2500, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/32/Shadow_Poison_icon.png?version=62f9b86799735a4cb221cd54c266c370", third, "Shadow Demon"));
        mRepository.addAbility(new Ability("Demonic Purge", 60000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4a/Demonic_Purge_icon.png?version=ef86f1e3798597011a1995fbec80ce92", ultimate, "Shadow Demon"));

        mRepository.addAbility(new Ability("Ether Shock", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ea/Ether_Shock_icon.png?version=cf733c475fc5ff853712c378097fcfa7", first, "Shadow Shaman"));
        mRepository.addAbility(new Ability("Hex", 13000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/66/Hex_%28Shadow_Shaman%29_icon.png?version=60cb5b5efa3dfc574a0961521b9ca144", second, "Shadow Shaman"));
        mRepository.addAbility(new Ability("Shackles", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/62/Shackles_icon.png?version=533893ab9ff2d4a7f157e805ae11185e", third, "Shadow Shaman"));
        mRepository.addAbility(new Ability("Mass Serpent Ward", 120000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/5e/Mass_Serpent_Ward_icon.png?version=827822cad6e47c6828bf2f9f69407eb1", ultimate, "Shadow Shaman"));

        mRepository.addAbility(new Ability("Arcane Curse", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/da/Arcane_Curse_icon.png?version=e934b732786c0b8932d0453a64c7df90", first, "Silencer"));
        mRepository.addAbility(new Ability("Glaives of Wisdom", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b3/Glaives_of_Wisdom_icon.png?version=0ed6d5e4c4d97607c2b3d817bf609d80", second, "Silencer"));
        mRepository.addAbility(new Ability("Last Word", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ae/Last_Word_icon.png?version=105e3e8ba0a260c77711162a298e53c0", third, "Silencer"));
        mRepository.addAbility(new Ability("Global Silence", 130000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2d/Global_Silence_icon.png?version=6b2025d9eedd803228bb5450541261d8", ultimate, "Silencer"));

        mRepository.addAbility(new Ability("Arcane Bolt", 2000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3a/Arcane_Bolt_icon.png?version=6fc8fc77b29bbeed62db097ed1a75946", first, "Skywrath Mage"));
        mRepository.addAbility(new Ability("Concussive Shot", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/88/Concussive_Shot_icon.png?version=6a94df0c3110070011a10c8a7ac44c46", second, "Skywrath Mage"));
        mRepository.addAbility(new Ability("Ancient Seal", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/06/Ancient_Seal_icon.png?version=a20cdd69e7b3770fba76ea0c10d3fd36", third, "Skywrath Mage"));
        mRepository.addAbility(new Ability("Mystic Flare", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/23/Mystic_Flare_icon.png?version=46e0ab91e06ec6e02992a945d68751a3", ultimate, "Skywrath Mage"));

        mRepository.addAbility(new Ability("Static Remnant", 3500, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/d6/Static_Remnant_icon.png?version=314775510c7ef45b08b5c68aca25e000", first, "Storm Spirit"));
        mRepository.addAbility(new Ability("Electric Vortex", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8e/Electric_Vortex_icon.png?version=1044850b3a58dfcd812f2954d16c835b", second, "Storm Spirit"));
        mRepository.addAbility(new Ability("Overload", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9d/Overload_icon.png?version=c28612964a8918baa1a4228e33718753", third, "Storm Spirit"));
        mRepository.addAbility(new Ability("Ball Lightning", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/6/6e/Ball_Lightning_icon.png?version=c7a18f862bc052260e727cb6649089ac", ultimate, "Storm Spirit"));

        mRepository.addAbility(new Ability("Proximity Mines", 23000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/bc/Proximity_Mines_icon.png?version=026da039409c5d9c6f8c1e8f3f03a24e", first, "Techies"));
        mRepository.addAbility(new Ability("Stasis Trap", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/de/Stasis_Trap_icon.png?version=28dc698fc9c0ea1388a3b6753ca503fc", second, "Techies"));
        mRepository.addAbility(new Ability("Blast Off!", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/50/Blast_Off%21_icon.png?version=e4075e34b163c0bb42b7ad24ebd13737", third, "Techies"));
        mRepository.addAbility(new Ability("Remote Mines", 8000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/e1/Remote_Mines_icon.png?version=e6de7e31f8c98b780ac668d7b86657b3", ultimate, "Techies"));
        mRepository.addAbility(new Ability("Minefield Sign", 360000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8d/Minefield_Sign_icon.png?version=714ed980c7c0f176b1591c4ec03d17f7", uniqueAbilityOrScepterUpgrade, "Techies"));

        mRepository.addAbility(new Ability("Laser", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/59/Laser_icon.png?version=361a2b1a1ec204aa09b614b80bc6e578", first, "Tinker"));
        mRepository.addAbility(new Ability("Heat-Seeking Missile", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/9/9c/Heat-Seeking_Missile_icon.png?version=370dc4bb2cd6650ea48dbc1cc2375b6b", second, "Tinker"));
        mRepository.addAbility(new Ability("March of the Machines", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/f/f5/March_of_the_Machines_icon.png?version=c4d07a22f552468a88e3fd767bf6d2ce", third, "Tinker"));
        mRepository.addAbility(new Ability("Rearm", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cd/Rearm_icon.png?version=c920867aa29eb6ff6fcd3fc2d9a7f1a5", ultimate, "Tinker"));

        mRepository.addAbility(new Ability("Grave Chill", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c2/Grave_Chill_icon.png?version=06c3a28536737c7c5fd0b45b2f785ef1", first, "Visage"));
        mRepository.addAbility(new Ability("Soul Assumption", 4000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Soul_Assumption_icon.png?version=b049c88e9fa5c8a646abc0067fa1c01e", second, "Visage"));
        mRepository.addAbility(new Ability("Gravekeeper's Cloak", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c2/Gravekeeper%27s_Cloak_icon.png?version=f8f9affabfafad6c3b0a6ea44a554476", third, "Visage"));
        mRepository.addAbility(new Ability("Summon Familiars", 130000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/39/Summon_Familiars_icon.png?version=089da747f2e8626121d4c8606e99d57a", ultimate, "Visage"));

        mRepository.addAbility(new Ability("Aether Remnant", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/thumb/5/52/Aether_Remnant_icon.png/128px-Aether_Remnant_icon.png?version=355b808e56eff109bb8717268f50ae13", first, "Void Spirit"));
        mRepository.addAbility(new Ability("Dissimilate", 11000, "https://gamepedia.cursecdn.com/dota2_gamepedia/thumb/6/60/Dissimilate_icon.png/128px-Dissimilate_icon.png?version=24f531c2576497450ec4687f58de48d0", second, "Void Spirit"));
        mRepository.addAbility(new Ability("Resonant Pulse", 16000, "https://gamepedia.cursecdn.com/dota2_gamepedia/thumb/9/96/Resonant_Pulse_icon.png/128px-Resonant_Pulse_icon.png?version=f2008d50619af74676fdbafcd12e3324", third, "Void Spirit"));
        mRepository.addAbility(new Ability("Astral Step", withoutCalldown, "https://gamepedia.cursecdn.com/dota2_gamepedia/thumb/b/b0/Astral_Step_icon.png/128px-Astral_Step_icon.png?version=56d87b30ae00b6c3070afa951c6fb8e1", ultimate, "Void Spirit"));

        mRepository.addAbility(new Ability("Fatal Bonds", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8b/Fatal_Bonds_icon.png?version=79fdd02120497f9c5bef70eb654f3cc9", first, "Warlock"));
        mRepository.addAbility(new Ability("Shadow Word", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b1/Shadow_Word_icon.png?version=1d33cc1503e05fd14efbf4d12721c09a", second, "Warlock"));
        mRepository.addAbility(new Ability("Upheaval", 38000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ea/Upheaval_icon.png?version=980315d6106783ee1dbffea319007ad6", third, "Warlock"));
        mRepository.addAbility(new Ability("Chaotic Offering", 170000, "https://gamepedia.cursecdn.com/dota2_gamepedia/7/7d/Chaotic_Offering_icon.png?version=a6e8e103f7831cf0ba78c75827e11f4a", ultimate, "Warlock"));

        mRepository.addAbility(new Ability("Shackleshot", 10000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/3e/Shackleshot_icon.png?version=c28c8b5f0f7e02eefa05ecbb403f5455", first, "Windranger"));
        mRepository.addAbility(new Ability("Powershot", 9000, "https://gamepedia.cursecdn.com/dota2_gamepedia/2/2a/Powershot_icon.png?version=a111eff55f7883bc6b54828d901f08f7", second, "Windranger"));
        mRepository.addAbility(new Ability("Windrun", 12000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/88/Windrun_icon.png?version=ec9efb816f751dedaf258226d771d185", third, "Windranger"));
        mRepository.addAbility(new Ability("Focus Fire", 30000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/4d/Focus_Fire_icon.png?version=dce675bd2765ea0efbe1b6dc98db469f", ultimate, "Windranger"));

        mRepository.addAbility(new Ability("Arctic Burn", 20000, "https://gamepedia.cursecdn.com/dota2_gamepedia/0/01/Arctic_Burn_icon.png?version=e49d4a09c7e870fd8119b4f88ace48cb", first, "Winter Wyvern"));
        mRepository.addAbility(new Ability("Splinter Blast", 7000, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ea/Splinter_Blast_icon.png?version=02e4c4125ef741e16be47177e201f5c3", second, "Winter Wyvern"));
        mRepository.addAbility(new Ability("Cold Embrace", 15000, "https://gamepedia.cursecdn.com/dota2_gamepedia/8/8a/Cold_Embrace_icon.png?version=58b91fd658ca13f4b25a704040f76a91", third, "Winter Wyvern"));
        mRepository.addAbility(new Ability("Winter's Curse", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/b/b2/Winter%27s_Curse_icon.png?version=f3de0736895bc19fac7046dba2ab33e4", ultimate, "Winter Wyvern"));

        mRepository.addAbility(new Ability("Paralyzing Cask", 14000, "https://gamepedia.cursecdn.com/dota2_gamepedia/d/dd/Paralyzing_Cask_icon.png?version=64bfb068bb0a581978df566bd1c378cc", first, "Witch Doctor"));
        mRepository.addAbility(new Ability("Voodoo Restoration", autocast, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/11/Voodoo_Restoration_icon.png?version=3967f196cb284481278646acd770b83c", second, "Witch Doctor"));
        mRepository.addAbility(new Ability("Maledict", 18000, "https://gamepedia.cursecdn.com/dota2_gamepedia/4/42/Maledict_icon.png?version=58f0d93536bd4c316f9e4501e16f1cdd", third, "Witch Doctor"));
        mRepository.addAbility(new Ability("Death Ward", 80000, "https://gamepedia.cursecdn.com/dota2_gamepedia/c/cf/Death_Ward_icon.png?version=b04bcbb3f2203916b6979c08e7deed34", ultimate, "Witch Doctor"));

        mRepository.addAbility(new Ability("Arc Lightning", 1600, "https://gamepedia.cursecdn.com/dota2_gamepedia/e/ef/Arc_Lightning_icon.png?version=0169f14502319430857ff598b8154574", first, "Zeus"));
        mRepository.addAbility(new Ability("Lightning Bolt", 6000, "https://gamepedia.cursecdn.com/dota2_gamepedia/3/34/Lightning_Bolt_icon.png?version=954c190410d2ae65342f05ec4ad3e2ec", second, "Zeus"));
        mRepository.addAbility(new Ability("Static Field", passive, "https://gamepedia.cursecdn.com/dota2_gamepedia/a/ae/Static_Field_icon.png?version=345041b019240da158828c88ae6f1c26", third, "Zeus"));
        mRepository.addAbility(new Ability("Thundergod's Wrath", 90000, "https://gamepedia.cursecdn.com/dota2_gamepedia/1/1c/Thundergod%27s_Wrath_icon.png?version=e226eda7587a84b09e0946542d8d705f", ultimate, "Zeus"));
        mRepository.addAbility(new Ability("Nimbus", 35000, "https://gamepedia.cursecdn.com/dota2_gamepedia/5/50/Nimbus_icon.png?version=269a962565297f23ab819cc46b77ee03", uniqueAbilityOrScepterUpgrade, "Zeus"));
    }

    @Override
    public void onItemAddClick(int positionOfHero) {
        if (heroes.get(positionOfHero).getItemList().size() >= 6) {
            heroTimerView.showMessage("Hero cannot have more than 6 items");
        } else {
            List<Item> heroItems = heroes.get(positionOfHero).getItemList();
            List<Item> itemListNotOnHero = new ArrayList<>(allAvailableItems);
            for (Item item : heroItems) {
                itemListNotOnHero.remove(item);
            }
            heroTimerView.showItemList(positionOfHero, itemListNotOnHero);
        }
    }


    @Override
    public void onSelectItem(int positionOfHero, Item item) {
        heroes.get(positionOfHero).getItemList().add(item);
    }

    @Override
    public void onItemRemoved(String tag) {
        for (HeroWithAbility hero : heroes) {
            for (Ability ability : hero.getAbilities()) {
                if (ability.getName().equals(tag)) {
                    hero.getAbilities().remove(ability);
                    logged("ability " + ability + " was removed");
                    return;
                }
            }
            for (Item item : hero.getItemList()) {
                if (item.getName().equals(tag)) {
                    hero.getItemList().remove(item);
                    logged("item " + item + " was removed");
                    return;
                }
            }
        }
    }

    public void loadFromCs() {

    }

    public void addToCs() {
        Disposable disposable1 = mRepository.getAllHeroes()
                .concatMap(new Function<List<Hero>, Publisher<Hero>>() {
                    @Override
                    public Publisher<Hero> apply(List<Hero> heroList) throws Exception {
                        return Flowable.fromIterable(heroList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Hero>() {
                    @Override
                    public void accept(Hero hero) throws Exception {
                        cloudFireStore.addHero(hero);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logged(throwable.getMessage());
                    }
                });
        Disposable disposable2 = mRepository.getAllAbilities()
                .concatMap((Function<List<Ability>, Publisher<Ability>>) Flowable::fromIterable)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Ability>() {
                    @Override
                    public void accept(Ability ability) throws Exception {
                        cloudFireStore.addAbility(ability);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logged(throwable.getMessage());
                    }
                });

    }
}

