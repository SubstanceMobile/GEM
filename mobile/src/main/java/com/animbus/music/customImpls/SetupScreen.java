package com.animbus.music.customImpls;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.paolorotolo.appintro.IndicatorController;
import com.github.paolorotolo.appintro.ProgressIndicatorController;
import com.github.paolorotolo.appintro.R.*;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Adrian on 8/4/2015.
 */
public abstract class SetupScreen extends FragmentActivity {

    private PagerAdapter mPagerAdapter;
    private ViewPager pager;
    private List<Fragment> fragments = new Vector();
    private List<ImageView> dots;
    private int slidesNumber;
    private Vibrator mVibrator;
    private IndicatorController mController;
    private boolean isVibrateOn = false;
    private int vibrateIntensity = 20;
    private boolean showDone = true;
    private boolean showSkip = false;
    private boolean showIndicator = true;

    public SetupScreen() {
    }

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);*/
        this.setContentView(layout.intro_layout2);
        final ImageView nextButton = (ImageView) this.findViewById(id.next);
        final ImageView doneButton = (ImageView) this.findViewById(id.done);
        final Button skipButton = (Button) this.findViewById(id.skip);
        this.mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(@NonNull View v) {
                if (SetupScreen.this.isVibrateOn) {
                    SetupScreen.this.mVibrator.vibrate((long) SetupScreen.this.vibrateIntensity);
                }

                SetupScreen.this.pager.setCurrentItem(SetupScreen.this.pager.getCurrentItem() + 1);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(@NonNull View v) {
                if (SetupScreen.this.isVibrateOn) {
                    SetupScreen.this.mVibrator.vibrate((long) SetupScreen.this.vibrateIntensity);
                }

                SetupScreen.this.onDonePressed();
            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(@NonNull View v) {
                if (SetupScreen.this.isVibrateOn) {
                    SetupScreen.this.mVibrator.vibrate((long) SetupScreen.this.vibrateIntensity);
                }

                SetupScreen.this.onSkipPressed();
            }
        });
        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), this.fragments);
        this.pager = (ViewPager) this.findViewById(id.view_pager);
        this.pager.setAdapter(this.mPagerAdapter);
        this.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                SetupScreen.this.mController.selectPosition(position);
                if (position == SetupScreen.this.slidesNumber - 1) {
                    nextButton.setVisibility(View.GONE);
                    if (SetupScreen.this.showDone) {
                        doneButton.setVisibility(View.VISIBLE);
                    } else {
                        doneButton.setVisibility(View.GONE);
                    }
                    if (SetupScreen.this.showSkip) {
                        skipButton.setVisibility(View.INVISIBLE);
                    } else {
                        skipButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    doneButton.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                    if (SetupScreen.this.showSkip) {
                        skipButton.setVisibility(View.VISIBLE);
                    } else {
                        skipButton.setVisibility(View.INVISIBLE);
                    }
                }

            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.init(savedInstanceState);
        this.slidesNumber = this.fragments.size();
        if (this.slidesNumber == 1) {
            nextButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.INVISIBLE);
        }

        this.initController();
    }

    private void initController() {
        if (this.mController == null) {
            this.mController = new DefaultIndicatorController();
        }

        FrameLayout indicatorContainer = (FrameLayout) this.findViewById(id.indicator_container);
        indicatorContainer.addView(this.mController.newInstance(this));
        this.mController.initialize(this.slidesNumber);
    }

    public void addSlide(@NonNull Fragment fragment) {
        this.fragments.add(fragment);
        this.mPagerAdapter.notifyDataSetChanged();
    }

    @NonNull
    public List<Fragment> getSlides() {
        return this.mPagerAdapter.getFragments();
    }

    public void showDoneButton(boolean showDone) {
        this.showDone = showDone;
        ImageView done = (ImageView) this.findViewById(id.done);
        if (!showDone) {
            done.setVisibility(View.INVISIBLE);
        }
    }

    public void showSkipButton(boolean showSkip) {
        this.showSkip = showSkip;
        Button skip = (Button) this.findViewById(id.skip);
        if (!showSkip) {
            skip.setVisibility(View.INVISIBLE);
        } else {
            skip.setVisibility(View.VISIBLE);
        }
    }

    public void setVibrate(boolean vibrate) {
        this.isVibrateOn = vibrate;
    }

    public void setVibrateIntensity(int intensity) {
        this.vibrateIntensity = intensity;
    }

    public void setFadeAnimation() {
        this.pager.setPageTransformer(true, new FadePageTransformer());
    }

    public void setCustomTransformer(@Nullable ViewPager.PageTransformer transformer) {
        this.pager.setPageTransformer(true, transformer);
    }

    public void setOffScreenPageLimit(int limit) {
        this.pager.setOffscreenPageLimit(limit);
    }

    public void setProgressIndicator() {
        this.mController = new ProgressIndicatorController();
    }

    public void setCustomIndicator(@NonNull IndicatorController controller) {
        this.mController = controller;
    }

    public void showIndicator(boolean showIndicator){
        this.showIndicator = false;
        if (!showIndicator) {
            findViewById(id.indicator_container).setVisibility(View.INVISIBLE);
        } else {
            findViewById(id.indicator_container).setVisibility(View.VISIBLE);
        }
    }

    public abstract void init(@Nullable Bundle var1);

    public abstract void onDonePressed();

    public abstract void onSkipPressed();

    public boolean onKeyDown(int code, KeyEvent kevent) {
        if (code != 66 && code != 96) {
            return super.onKeyDown(code, kevent);
        } else {
            ViewPager vp = (ViewPager) this.findViewById(id.view_pager);
            if (vp.getCurrentItem() == vp.getAdapter().getCount() - 1) {
                this.onDonePressed();
            } else {
                vp.setCurrentItem(vp.getCurrentItem() + 1);
            }

            return false;
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        public Fragment getItem(int position) {
            return (Fragment)this.fragments.get(position);
        }

        public int getCount() {
            return this.fragments.size();
        }

        @NonNull
        public List<Fragment> getFragments() {
            return this.fragments;
        }
    }

    class FadePageTransformer implements ViewPager.PageTransformer {
        FadePageTransformer() {
        }

        public void transformPage(View view, float position) {
            ViewHelper.setTranslationX(view, (float) view.getWidth() * -position);
            if(position > -1.0F && position < 1.0F) {
                if(position == 0.0F) {
                    ViewHelper.setAlpha(view, 1.0F);
                    view.setClickable(true);
                } else {
                    ViewHelper.setAlpha(view, 1.0F - Math.abs(position));
                }
            } else {
                ViewHelper.setAlpha(view, 0.0F);
                view.setClickable(false);
            }

        }
    }

    class DefaultIndicatorController implements IndicatorController {
        private Context mContext;
        private LinearLayout mDotLayout;
        private List<ImageView> mDots;
        private int mSlideCount;
        private static final int FIRST_PAGE_NUM = 0;

        DefaultIndicatorController() {
        }

        public View newInstance(@NonNull Context context) {
            this.mContext = context;
            this.mDotLayout = (LinearLayout)View.inflate(context, layout.default_indicator, (ViewGroup)null);
            return this.mDotLayout;
        }

        public void initialize(int slideCount) {
            this.mDots = new ArrayList();
            this.mSlideCount = slideCount;

            for(int i = 0; i < slideCount; ++i) {
                ImageView dot = new ImageView(this.mContext);
                dot.setImageDrawable(this.getDrawable(drawable.indicator_dot_grey));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
                this.mDotLayout.addView(dot, params);
                this.mDots.add(dot);
            }

            this.selectPosition(0);
        }

        public void selectPosition(int index) {
            for(int i = 0; i < this.mSlideCount; ++i) {
                int drawableId = i == index?drawable.indicator_dot_white:drawable.indicator_dot_grey;
                Drawable drawable = this.getDrawable(drawableId);
                this.mDots.get(i).setImageDrawable(drawable);
            }

        }

        private Drawable getDrawable(@DrawableRes int drawableId) {
            if (Build.VERSION.SDK_INT >= 21) {
                return this.mContext.getDrawable(drawableId);
            } else {
                return this.mContext.getResources().getDrawable(drawableId);
            }
        }
    }


}

