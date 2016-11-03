package com.labs.vdesouza.doodleprototype;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    // slide out tools menu adapted from Navigation Drawer tutorial on
    // http://blog.teamtreehouse.com/add-navigation-drawer-android

    // ListView for tools menu
    private ListView mDrawerList;
    // ArrayAdapter that holds Strings of the tool names
    private ArrayAdapter<String> mAdapter;
    // Toggle for ActionBar that enables the tools menu
    private ActionBarDrawerToggle mDrawerToggle;
    // container for the slide out menu
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private DoodleView mDoodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDoodleView = (DoodleView) findViewById(R.id.doodle_view);

        // sets up tools menu view and items
        mDrawerList = (ListView) findViewById(R.id.toolList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        // sets up menu button on Action Bar for slide out menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // syncs tool menu animation with action bar text
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handles actions for selecting tools
        switch (item.getItemId()) {
            case R.id.action_undo:
                mDoodleView.onClickUndo();
                break;
            case R.id.action_redo:
                mDoodleView.onClickRedo();
                break;
        }

        // activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDrawerItems() {
        // creates array of items to put on ListView that contains the tools
        String[] toolsArray = { "Line Tool", "Color"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toolsArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.tools_menu_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }
}
