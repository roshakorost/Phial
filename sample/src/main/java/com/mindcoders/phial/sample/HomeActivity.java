package com.mindcoders.phial.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.mindcoders.phial.keyvalue.Phial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/8/17.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ShareElementManager {
    private static final SparseArray<Class<? extends Fragment>> FRAGMENTS = new SparseArray<>();
    private static final int DEFAULT_FRAGMENT = R.id.main;
    private List<Pair<View, String>> sharedElements;

    static {
        FRAGMENTS.put(R.id.main, MainFragment.class);
        FRAGMENTS.put(R.id.auto_fill, AutoFillFragment.class);
    }

    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedElements = new ArrayList<>();
        super.onCreate(savedInstanceState);
        Phial.setKey("currentActivity", getClass().getSimpleName());
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            showFragmentById(DEFAULT_FRAGMENT);
            navigationView.setCheckedItem(DEFAULT_FRAGMENT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedElements = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();
        showFragmentById(item.getItemId());
        return true;
    }

    private void showFragmentById(int id) {
        final Class<? extends Fragment> target = FRAGMENTS.get(id);

        final Fragment fragment = Fragment.instantiate(this, target.getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupFragmentTransition(fragment);
        }

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment);

        for (Pair<View, String> sharedElement : sharedElements) {
            transaction.addSharedElement(sharedElement.first, sharedElement.second);
        }

        sharedElements = new ArrayList<>();

        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupFragmentTransition(Fragment fragment) {
        final Transition move = TransitionInflater.from(this).inflateTransition(android.R.transition.move);
        fragment.setSharedElementEnterTransition(move);
        fragment.setExitTransition(new Slide(Gravity.END));
    }

    @Override
    public void addSharedElement(View item, @StringRes int stringNme) {
        final String transitionName = getString(stringNme);
        sharedElements.add(Pair.create(item, transitionName));
        ViewCompat.setTransitionName(item, transitionName);
    }
}
