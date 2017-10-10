package net.chris.ui.slick;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import net.chris.ui.slick.databinding.ActivityMainBinding;
import net.chris.ui.slick.model.HomeMenuItem;
import net.chris.ui.slick.model.inject.HomeComponent;
import net.chris.ui.slick.ui.HomeMenuAdapter;
import net.chris.ui.slick.ui.model.CallbackType;
import net.chris.ui.slick.ui.model.DimensionHelper;
import net.chris.ui.slick.ui.model.HomeObservables;
import net.chris.ui.slick.ui.model.HomeViewModel;
import net.chris.ui.slick.ui.model.MoveDirection;
import net.chris.ui.slick.ui.widget.HomeRecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

import static net.chris.ui.slick.Constants.KEY_CONTENT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_HEIGHT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_IMAGE_HEIGHT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_RADIUS;
import static net.chris.ui.slick.Constants.KEY_DIALOG_WIDTH;
import static net.chris.ui.slick.ui.model.Command.CommandType.ACTION_UP;
import static net.chris.ui.slick.ui.model.Command.CommandType.CLICK_ITEM;
import static net.chris.ui.slick.ui.model.Command.CommandType.PULL_BACKGROUND;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    HomeViewModel viewModel;

    @BindView(R.id.sliding_home_background)
    ImageView background;
    @BindView(R.id.sliding_home_recycler)
    HomeRecyclerView recycler;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.sliding_home_title)
    TextView title;
    @BindView(R.id.sliding_home_sub_title)
    TextView subTitle;


    private int lastY;
    private MoveDirection direction = MoveDirection.UNKNOW;

    private HomeMenuAdapter adapter;

    private Unbinder unbinder;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeComponent.getInstance().inject(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        HomeObservables homeObservables = new HomeObservables();
        viewModel.setHomeObservables(homeObservables);
        binding.setValues(homeObservables);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        appBar.bringToFront();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        viewModel.registerCallback(CallbackType.UPDATE_ADAPTER, new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (((ObservableBoolean) sender).get()) {
                    handler.post(() -> adapter.notifyDataSetChanged());
                }
            }
        });
        viewModel.registerCallback(CallbackType.SHOW_DIALOG, new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                try {
                    Pair<View, HomeMenuItem> pair = ((ObservableField<Pair<View, HomeMenuItem>>) sender).get();
                    if (pair != null) {
                        final String sharedElementName = "transition_menu_item_layout";
                        final int sharedElementRes = R.id.item_menu_item_layout;
                        DimensionHelper dimension = DimensionHelper.getInstance();
                        startActivity(new Intent(MainActivity.this, MenuItemDetailActivity.class)
                                        .putExtra(KEY_CONTENT, pair.second)
                                        .putExtra(KEY_DIALOG_WIDTH, dimension.getDialogWidth())
                                        .putExtra(KEY_DIALOG_HEIGHT, dimension.getDialogHeight())
                                        .putExtra(KEY_DIALOG_RADIUS, dimension.getDialogRadius())
                                        .putExtra(KEY_DIALOG_IMAGE_HEIGHT, dimension.getDialogImageHeight()),
                                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pair.first.findViewById(sharedElementRes), sharedElementName).toBundle());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "show dialog failed", e);
                }
            }
        });
        initViews(homeObservables);
        title.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                DimensionHelper.getInstance().adjustTitle(title.getWidth(), title.getHeight());
                viewModel.adjustTitle();
            }
        });
        subTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (DimensionHelper.getInstance().isTitleAdjusted()) {
                    subTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    DimensionHelper.getInstance().adjustSubTitle(subTitle.getWidth(), subTitle.getHeight());
                    viewModel.adjustSubTitle();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.removeOnPropertyChangedCallbacks();
        HomeComponent.uninject();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnTouch(R.id.sliding_home_background)
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getY() - lastY;
                viewModel.order(PULL_BACKGROUND, background.getHeight(), deltaY);
                direction = deltaY > 0 ? MoveDirection.DOWN : MoveDirection.UP;
                break;
            case MotionEvent.ACTION_UP:
                viewModel.order(ACTION_UP, background.getHeight(), direction);
                direction = MoveDirection.UNKNOW;
                break;
        }
        lastY = (int) event.getY();
        return true;
    }

    private void initViews(@NonNull final HomeObservables homeObservables) {
        DimensionHelper dimension = DimensionHelper.getInstance();
        dimension.init(this,
                R.dimen.home_menu_recycler_margin_top,
                R.dimen.home_menu_recycler_margin_bottom,
                R.dimen.home_menu_recycler_radius,
                android.support.design.R.dimen.design_fab_size_normal,
                R.dimen.fab_margin,
                R.dimen.home_dialog_margin);
        viewModel.init(R.drawable.placeholder);
        adapter = new HomeMenuAdapter(viewModel.getItems(),
                dimension.getRecyclerItemWidth(),
                dimension.getRecyclerItemHeight(),
                dimension.getRecyclerItemImageHeight(),
                dimension.getRecyclerItemRadius(),
                homeObservables.recyclerItemOffset);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int interval = getResources().getDimensionPixelOffset(R.dimen.home_menu_recycler_interval);
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = interval;
                }
                outRect.right = interval;
            }
        });
        recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            private GestureDetectorCompat gestureDetector = new GestureDetectorCompat(recycler.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            View childView = recycler.findChildViewUnder(e.getX(), e.getY());
                            if (childView != null) {
                                viewModel.order(CLICK_ITEM, childView, recycler.getChildAdapterPosition(childView));
                            }
                            return true;
                        }
                    });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });

    }

}
