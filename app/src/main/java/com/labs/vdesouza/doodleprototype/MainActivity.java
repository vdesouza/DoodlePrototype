package com.labs.vdesouza.doodleprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // slide out tools menu adapted from Navigation Drawer tutorial on
    // http://blog.teamtreehouse.com/add-navigation-drawer-android

    // currently selected color shown as was adapted from
    // https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202

    // save image to gallery from tutorial:
    // https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328

    private String TAG = "Doodle Prototype";
    // ListView for tools menu
    private ListView mDrawerList;
    // ArrayAdapter that holds Strings of the tool names
    private ArrayAdapter<String> mAdapter;
    // Toggle for ActionBar that enables the tools menu
    private ActionBarDrawerToggle mDrawerToggle;

    // Relative layout for color grid
    private RelativeLayout mColorGrid;

    // Relative layout for brush sizes
    private RelativeLayout mBrushSizes;
    private SeekBar mSizeSeekBar;
    private TextView mSeekBarTextView;

    // Relative layout for brush opacity
    private RelativeLayout mBrushOpacity;
    private SeekBar mOpacitySeekBar;
    private TextView mOpacityTextView;

    private RelativeLayout mBrushPreview;
    private ImageView mBrushPreviewImageView;

    // container for the slide out menu
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    // current color - used to highlight currently selected color
    private ImageButton currPaint;

    private Toast mToast;

    // IDs for menu items
    private static final int MENU_CLEAR_ALL = Menu.FIRST;
    private static final int MENU_SAVE = Menu.FIRST + 1;

    private DoodleView mDoodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDoodleView = (DoodleView) findViewById(R.id.doodle_view);

        // sets up tools menu view and items
        mDrawerList = (ListView) findViewById(R.id.tool_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mColorGrid = (RelativeLayout) findViewById(R.id.color_grid);
        mColorGrid = (RelativeLayout) getLayoutInflater().inflate(R.layout.color_grid, null);
        mBrushSizes = (RelativeLayout) findViewById(R.id.brush_seeker);
        mBrushSizes = (RelativeLayout) getLayoutInflater().inflate(R.layout.brush_seeker, null);
        mBrushOpacity = (RelativeLayout) findViewById(R.id.opacity_seeker);
        mBrushOpacity = (RelativeLayout) getLayoutInflater().inflate(R.layout.opacity_seeker, null);
        mBrushPreview = (RelativeLayout) findViewById(R.id.brush_preview);
        mBrushPreview = (RelativeLayout) getLayoutInflater().inflate(R.layout.brush_preview, null);
        mSizeSeekBar = (SeekBar) mBrushSizes.findViewById(R.id.seek_bar);
        mOpacitySeekBar = (SeekBar) mBrushOpacity.findViewById(R.id.opacity_seek_bar);
        mSeekBarTextView = (TextView) mBrushSizes.findViewById(R.id.seek_bar_text_view);
        mOpacityTextView = (TextView) mBrushOpacity.findViewById(R.id.opacity_text_view);
        mBrushPreviewImageView = (ImageView) mBrushPreview.findViewById(R.id.brush_preview_image);

        mSeekBarTextView.setText("Brush Size");
        mOpacityTextView.setText("Brush Opacity");

        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        // toast to show messages - this is so toasts can be dismissed
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        // set the default background color
        mDoodleView.setDrawingCacheEnabled(true);
        mDoodleView.setBackgroundColor(Color.parseColor("#fafafa"));
        mDoodleView.setDrawingCacheBackgroundColor(Color.parseColor("#fafafa"));

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

    // creates the options on the action bar for undo, redo, and dropdown for clear, save, load
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        menu.add(Menu.NONE, MENU_CLEAR_ALL, Menu.NONE, getResources().getString(R.string.clear_all));
        menu.add(Menu.NONE, MENU_SAVE, Menu.NONE, getResources().getString(R.string.save));
        return true;
    }

    // performs actions of options clicked for action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // handles actions for selecting tools
        switch (item.getItemId()) {
            case R.id.action_undo:
                mDoodleView.onClickUndo();
                break;
            case R.id.action_redo:
                mDoodleView.onClickRedo();
                break;
            case MENU_CLEAR_ALL:
                showClearAllAlert();
                break;
            case MENU_SAVE:
                saveAlert();
                break;
        }

        // activate the tools drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    private void addDrawerItems() {
        // creates array of items to put on ListView that contains the tools
        String[] toolsArray = { "Brush Tool", "Eraser","Fill with Current Color"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toolsArray);
        // add color grid and brush size, make them hidden until tool is clicked
        mDrawerList.addFooterView(mBrushSizes);
        mDrawerList.addFooterView(mBrushOpacity);
        mDrawerList.addFooterView(mColorGrid);
        mDrawerList.addFooterView(mBrushPreview);
        mDrawerList.setFooterDividersEnabled(false);
        mDrawerList.setAdapter(mAdapter);

        // hide tools until clicked on
        mBrushSizes.setVisibility(View.GONE);
        mBrushSizes.setPadding(0, -1*mBrushSizes.getHeight(), 0, 0);
        mBrushOpacity.setVisibility(View.GONE);
        mBrushOpacity.setPadding(0, -1*mBrushOpacity.getHeight(), 0, 0);
        mColorGrid.setVisibility(View.GONE);
        mColorGrid.setPadding(0, -1*mColorGrid.getHeight(), 0, 0);
        mBrushPreview.setVisibility(View.GONE);
        mBrushPreview.setPadding(0, -1*mBrushPreview.getHeight(), 0, 0);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        // set tools visible
                        mBrushSizes.setVisibility(View.VISIBLE);
                        mBrushSizes.setPadding(0, 0, 0, 0);
                        mBrushOpacity.setVisibility(View.VISIBLE);
                        mBrushOpacity.setPadding(0, 0, 0, 0);
                        mColorGrid.setVisibility(View.VISIBLE);
                        mColorGrid.setPadding(0, 0, 0, 0);
                        mBrushPreview.setVisibility(View.VISIBLE);
                        mBrushPreview.setPadding(0, 0, 0, 0);
                        // brush tool - change brush size, color, and opacity
                        mBrushPreviewImageView.setColorFilter(mDoodleView.getCurrentColor());
                        mBrushPreviewImageView.getLayoutParams().height = mDoodleView.getCurrentBrushSize() * 2;
                        mBrushPreviewImageView.getLayoutParams().width = mDoodleView.getCurrentBrushSize() * 2;
                        mBrushPreviewImageView.setImageAlpha(mDoodleView.getCurrentAlpha());
                        mBrushPreviewImageView.requestLayout();
                        // highlight the currently selected color
                        LinearLayout currPaintRow = (LinearLayout) mColorGrid.getChildAt(0);
                        currPaint = (ImageButton) currPaintRow.getChildAt(3);
                        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
                        break;
                    case 1:
                        // hide tools not used by eraser
                        mBrushSizes.setVisibility(View.VISIBLE);
                        mBrushSizes.setPadding(0, 0, 0, 0);
                        mBrushOpacity.setVisibility(View.GONE);
                        mBrushOpacity.setPadding(0, -1*mBrushOpacity.getHeight(), 0, 0);
                        mColorGrid.setVisibility(View.GONE);
                        mColorGrid.setPadding(0, -1*mColorGrid.getHeight(), 0, 0);
                        mBrushPreview.setVisibility(View.VISIBLE);
                        mBrushPreview.setPadding(0, 0, 0, 0);
                        // eraser - change size of eraser
                        mBrushPreviewImageView.getLayoutParams().height = mDoodleView.getCurrentBrushSize() * 2;
                        mBrushPreviewImageView.getLayoutParams().width = mDoodleView.getCurrentBrushSize() * 2;
                        mBrushPreviewImageView.setColorFilter(mDoodleView.getDrawingCacheBackgroundColor());
                        mBrushPreviewImageView.setImageAlpha(255);
                        mBrushPreviewImageView.requestLayout();
                        mDoodleView.setPaintColor(mDoodleView.getDrawingCacheBackgroundColor());
                        mDoodleView.setBrushOpacity(255);
                        // highlight the currently selected color
                        currPaintRow = (LinearLayout) mColorGrid.getChildAt(0);
                        currPaint = (ImageButton) currPaintRow.getChildAt(3);
                        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
                        break;
                    case 2:
                        // set tools visible
                        mBrushSizes.setVisibility(View.GONE);
                        mBrushSizes.setPadding(0, -1*mBrushSizes.getHeight(), 0, 0);
                        mBrushOpacity.setVisibility(View.GONE);
                        mBrushOpacity.setPadding(0, -1*mBrushOpacity.getHeight(), 0, 0);
                        mColorGrid.setVisibility(View.GONE);
                        mColorGrid.setPadding(0, -1*mBrushOpacity.getHeight(), 0, 0);
                        mBrushPreview.setVisibility(View.GONE);
                        mBrushPreview.setPadding(0, -1*mBrushPreview.getHeight(), 0, 0);

                        showFillAllAlert();
                        break;
                }
            }
        });

        // behaviors for size seekbar
        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                mBrushPreviewImageView.getLayoutParams().height = (int) (progress * 2);
                mBrushPreviewImageView.getLayoutParams().width = (int) (progress * 2);
                mBrushPreviewImageView.requestLayout();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mBrushPreviewImageView.getLayoutParams().height = (int) (progress * 2);
                mBrushPreviewImageView.getLayoutParams().width = (int) (progress * 2);
                mBrushPreviewImageView.requestLayout();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDoodleView.setBrushSize(progress);
            }
        });

        // behaviors for opacity seekbar
        mOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                mBrushPreviewImageView.setImageAlpha((int) progress);
                mBrushPreviewImageView.requestLayout();

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mBrushPreviewImageView.setImageAlpha((int) progress);
                mBrushPreviewImageView.requestLayout();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDoodleView.setBrushOpacity((int) progress);
            }
        });
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

    // Alert dialog for confirming clear all
    private void showClearAllAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(getResources().getString(R.string.call_all_alert_message));
        alertDialog.setTitle(getResources().getString(R.string.call_all_alert_title));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_yes),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mDoodleView.setBackgroundColor(Color.parseColor("#fafafa"));
                    mDoodleView.setDrawingCacheBackgroundColor(Color.parseColor("#fafafa"));
                    mDoodleView.onClickClearAll();
                    // toast message to confirm cleared all
                    Toast.makeText(MainActivity.this, R.string.cleared_all_toast, Toast.LENGTH_SHORT).show();
                }
            });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_no),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        alertDialog.show();
    }

    // Alert dialog for confirming clear all
    private void showFillAllAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage("Fill background with currently selected color?");
        alertDialog.setTitle("Fill Background");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int colorPicked = mDoodleView.getCurrentColor();
                        mDoodleView.setBackgroundColor(colorPicked);
                        mDoodleView.setDrawingCacheBackgroundColor(colorPicked);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    // Save Dialog
    private void saveAlert() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                fixMediaDir();
                String imgSaved = MediaStore.Images.Media.insertImage(
                        MainActivity.this.getContentResolver(), mDoodleView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                mDoodleView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    // listener for paint color - changes the current paint color and updates the ui
    public void paintClicked(View view){
        ImageButton imgView = (ImageButton) view;
        String color = imgView.getTag().toString();
        mBrushPreviewImageView.setColorFilter(Color.parseColor(color));
        mBrushPreviewImageView.requestLayout();

        imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
        currPaint= imgView;

        mDoodleView.setPaintColor(Color.parseColor(color));
        // brush tool
        mToast.setText("Color set to: " + color);
        mToast.show();
        //mDrawerLayout.closeDrawers();
    }

    // fix found on http://stackoverflow.com/a/33069416 to save image to gallery app on emulator
    void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }
    }
}
