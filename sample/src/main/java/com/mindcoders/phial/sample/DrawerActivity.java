package com.mindcoders.phial.sample;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * Created by rost on 11/8/17.
 */

public abstract class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.main_content);
        final FrameLayout content = findViewById(R.id.content_frame);
        LayoutInflater.from(this).inflate(layoutResID, content, true);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();

        switch (item.getItemId()) {
            case R.id.main:
                navigateTo(SampleActivity.class);
                return true;
            case R.id.auto_fill:
                navigateTo(LoginActivity.class);
                return true;
            default:
                return false;
        }
    }

    private void navigateTo(Class<? extends Activity> clazz) {
        if (!getClass().equals(clazz)) {
            final Intent intent = new Intent(this, clazz);
            startActivity(intent);
        }
    }
}
