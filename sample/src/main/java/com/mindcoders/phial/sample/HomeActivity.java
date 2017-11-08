package com.mindcoders.phial.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;

import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.keyvalue.Phial;

/**
 * Created by rost on 11/8/17.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final SparseArray<Class<? extends Fragment>> FRAGMENTS = new SparseArray<>();
    private static final int DEFAULT_FRAGMENT = R.id.main;

    static {
        FRAGMENTS.put(R.id.main, MainFragment.class);
        FRAGMENTS.put(R.id.auto_fill, AutoFillFragment.class);
    }

    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();
        showFragmentById(item.getItemId());
        return true;
    }

    private void showFragmentById(int id) {
        final Class<? extends Fragment> target = FRAGMENTS.get(id);
        Precondition.notNull(target, "unexpected id " + id);

        final Fragment fragment = Fragment.instantiate(this, target.getName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
