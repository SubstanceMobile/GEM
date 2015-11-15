package com.animbus.music.ui.activity.albumDetails;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.ui.custom.activity.ThemableActivity;
import com.animbus.music.ui.list.ListAdapter;
import com.animbus.music.media.Library;
import com.animbus.music.media.stable.PlaybackManager;
import com.animbus.music.media.objects.Album;
import com.animbus.music.ui.activity.nowPlaying.NowPlaying;
import com.animbus.music.ui.activity.settings.Settings;
import com.animbus.music.util.IconManager;
import com.animbus.music.ui.activity.theme.Theme;

public class AlbumDetails extends ThemableActivity {
    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbar;
    RecyclerView mList;
    FloatingActionButton mFAB;
    Album mAlbum;
    TextView mTitle, mArtist;
    LinearLayout mDetailsRoot;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_album_details);
        configureTransition();
        mAlbum = Library.findAlbumById(getIntent().getLongExtra("album_id", -1));
    }

    private void configureTransition() {
        ViewCompat.setTransitionName(findViewById(R.id.album_details_album_art), "art");
        ViewCompat.setTransitionName(findViewById(R.id.album_details_info_toolbar), "info");
        ViewCompat.setTransitionName(findViewById(R.id.album_details_toolbar), "appbar");
        ViewCompat.setTransitionName(findViewById(R.id.album_details_toolbar_text_protection), "appbar_text_protextion");
        ViewCompat.setTransitionName(findViewById(R.id.album_details_recycler), "list");
    }

    @Override
    protected void setVariables() {
        mToolbar = (Toolbar) findViewById(R.id.album_details_toolbar);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.album_details_collapsing_toolbar);
        mList = (RecyclerView) findViewById(R.id.album_details_recycler);
        mFAB = (FloatingActionButton) findViewById(R.id.album_details_fab);
        mDetailsRoot = (LinearLayout) findViewById(R.id.album_details_info_toolbar);
        mTitle = (TextView) findViewById(R.id.album_details_info_toolbar_title);
        mArtist = (TextView) findViewById(R.id.album_details_info_toolbar_artist);
    }

    @Override
    protected void setUp() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        configureFab();
        configureRecyclerView();
        configureUI();
    }

    private void configureRecyclerView() {
        mList.setAdapter(new ListAdapter(ListAdapter.TYPE_ALBUM_DETAILS, mAlbum.getSongs(), this));
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void configureFab() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionNowPlaying();
            }
        });
        mFAB.setAlpha(0.0f);
        mFAB.setScaleX(0.0f);
        mFAB.setScaleY(0.0f);
        mFAB.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(200).setStartDelay(500).start();
    }

    private void configureUIColors() {
        FabHelper.setFabBackground(mFAB, mAlbum.getAccentColor());
        FabHelper.setFabTintedIcon(mFAB, getResources().getDrawable(R.drawable.ic_play_arrow_black_48dp), mAlbum.getAccentIconColor());
        mDetailsRoot.setBackgroundColor(mAlbum.getBackgroundColor());
        mTitle.setTextColor(mAlbum.getTitleTextColor());
        mArtist.setTextColor(mAlbum.getSubtitleTextColor());
        mCollapsingToolbar.setContentScrimColor(mAlbum.getBackgroundColor());
        mCollapsingToolbar.setStatusBarScrimColor(mAlbum.getBackgroundColor());

        //Sets Window description in Multitasking menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IconManager iconM = IconManager.get().setContext(this);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), iconM.getDrawable(iconM.getOverviewIcon(iconM.getIcon()).getId()));
            setTaskDescription(new ActivityManager.TaskDescription(mAlbum.getAlbumTitle(), bm, mAlbum.getBackgroundColor()));
            bm.recycle();
        }
    }

    private void configureUI() {
        mAlbum.requestArt(this, (ImageView) findViewById(R.id.album_details_album_art));
        mTitle.setText(mAlbum.getAlbumTitle());
        mCollapsingToolbar.setTitle(mAlbum.getAlbumTitle());
        mArtist.setText(mAlbum.getAlbumArtistName());
        configureUIColors();
    }

    private void transitionNowPlaying() {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<View, String>(findViewById(R.id.album_details_transition_reveal_part), "controls"),
                new Pair<View, String>(findViewById(R.id.album_details_toolbar), "appbar"),
                new Pair<View, String>(findViewById(R.id.album_details_toolbar_text_protection), "appbar_text_protection"),
                new Pair<View, String>(findViewById(R.id.album_details_album_art), "art")

        );

        final View overlay = findViewById(R.id.album_details_transition_reveal_part);

        //Original values:
        final float fabOriginalX = mFAB.getX();
        final float fabOriginalY = mFAB.getY();
        final float fabOriginalElev = ViewCompat.getElevation(mFAB);

        //Processed positions:
        float fabFinalX = (overlay.getWidth() / 2f) - (mFAB.getWidth() / 2f);
        float fabFinalY = (mList.getTop() - (overlay.getHeight() / 2f)) - (mFAB.getHeight() / 2f);
        float fabMiddleX = (mFAB.getX() + fabFinalX) / 2f;
        float fabMiddleY = (mList.getTop() + (overlay.getHeight() / 2f)) - (mFAB.getHeight() * 2.5f);

        Path path = new Path();
        path.moveTo(fabOriginalX, fabOriginalY);
        path.quadTo(fabMiddleX, fabMiddleY, fabFinalX, fabFinalY);
        ValueAnimator curve = FabHelper.getAnimatorAlong(mFAB, path);
        curve.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Circular reveal
                overlay.setBackgroundColor(mAlbum.getAccentColor());
                overlay.setAlpha(1f);
                ViewCompat.setElevation(mFAB, 0f);
                Animator reveal = FabHelper.getRevealAnim(mFAB, overlay);

                reveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFAB.setX(fabOriginalX);
                        mFAB.setY(fabOriginalY);
                        ViewCompat.setElevation(mFAB, fabOriginalElev);
                        ActivityCompat.startActivity(AlbumDetails.this, new Intent(AlbumDetails.this, NowPlaying.class), options.toBundle());
                        PlaybackManager.get().play(mAlbum.getSongs(), 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                //Fab Collapsing
                mFAB.setScaleX(1.0f);
                mFAB.setScaleY(1.0f);
                mFAB.setAlpha(1.0f);
                ViewPropertyAnimator fabCollapsing = mFAB.animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(200).setStartDelay(100);
                fabCollapsing.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFAB.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                fabCollapsing.start();
                reveal.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        curve.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFAB.show();
        findViewById(R.id.album_details_transition_reveal_part).animate().alpha(0f).start();
    }

    @Override
    protected void setUpTheme(Theme theme) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mFAB.setAlpha(1.0f);
        mFAB.setScaleX(1.0f);
        mFAB.setScaleY(1.0f);
        //Not using hide because that has a duration of 200
        mFAB.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setDuration(100).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AlbumDetails.super.onBackPressed();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
