package com.animbus.music.customImpls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.animbus.music.R;
import com.animbus.music.media.PlaybackManager;
import com.animbus.music.media.objects.Song;

/**
 * Created by Adrian on 9/24/2015.
 */
public class MusicControlsView extends FrameLayout implements PlaybackManager.OnChangedListener {
    private static final int SHOW_PROGRESS = 2;
    private final Context mContext;
    AudioManager mAudioManager;
    private boolean mDragging;
    private SeekBar mProgress, mVolume;
    private ImageView mPlayButton, mPauseButton, mNextButton, mPrevButton;
    private ImageView mVolumeNoneIcon, mVolumeFullIcon;
    private View mPlayButtonRoot, mPauseButtonRoot;
    private TextView mEndTime, mCurrentTime;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_PROGRESS) {
                if (!mDragging && PlaybackManager.get().isPlaying()) {
                    setProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessage(msg);
                }
            }
        }
    };
    private MediaControllerCompat mController;
    SeekBar.OnSeekBarChangeListener mVolumeSeekListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mController.setVolumeTo(progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    private MediaControllerCompat.TransportControls mTransportControls;
    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTransportControls.pause();
        }
    };
    private final View.OnClickListener mPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTransportControls.play();
        }
    };
    private final View.OnClickListener mNextListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mTransportControls.skipToNext();
        }
    };
    private final View.OnClickListener mPrevListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mTransportControls.skipToPrevious();
        }
    };
    /**
     * There are two scenarios that can trigger the seekbar listener to trigger:
     * <p/>
     * The first is the user using the touchpad to adjust the posititon of the
     * seekbar's thumb. In this case onStartTrackingTouch is called followed by
     * a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
     * We're setting the field "mDragging" to true for the duration of the dragging
     * session to avoid jumps in the position in case of ongoing playback.
     * <p/>
     * The second scenario involves the user operating the scroll ball, in this
     * case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
     * we will simply apply the updated position without suspending regular updates.
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (fromuser) {
                mTransportControls.seekTo((long) progress);
            }

            updatePausePlay();
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime(progress));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    public MusicControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public MusicControlsView(Context context) {
        super(context);
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setController(MediaControllerCompat controller) {
        mController = controller;
        mTransportControls = controller.getTransportControls();
        mVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     */
    public View initView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRoot = inflate.inflate(R.layout.now_playing_controls_view, null);
        addView(mRoot);

        initControllerView(mRoot);

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        return mRoot;
    }

    private void initControllerView(View v) {
        mPlayButtonRoot = v.findViewById(R.id.controls_play_button_root);
        mPauseButtonRoot = v.findViewById(R.id.controls_pause_button_root);

        mPlayButton = (ImageView) v.findViewById(R.id.controls_play_button);
        mPlayButton.setOnClickListener(mPlayListener);

        mPauseButton = (ImageView) v.findViewById(R.id.controls_pause_button);
        mPauseButton.setOnClickListener(mPauseListener);

        mNextButton = (ImageView) v.findViewById(R.id.controls_next_button);
        mNextButton.setOnClickListener(mNextListener);

        mPrevButton = (ImageView) v.findViewById(R.id.controls_prev_button);
        mPrevButton.setOnClickListener(mPrevListener);

        mProgress = (SeekBar) v.findViewById(R.id.control_seek_bar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setProgressDrawable(null);

        mVolume = (SeekBar) v.findViewById(R.id.controls_volume_seekbar);
        mVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mVolume.setOnSeekBarChangeListener(mVolumeSeekListener);

        mVolumeNoneIcon = (ImageView) v.findViewById(R.id.controls_volume_icon_empty);
        mVolumeFullIcon = (ImageView) v.findViewById(R.id.controls_volume_icon_full);

        mEndTime = (TextView) v.findViewById(R.id.controls_total_pos);
        mCurrentTime = (TextView) v.findViewById(R.id.controls_current_pos);

        setProgress();
        PlaybackManager.get().registerListener(this);
        updatePausePlay();
    }

    public void setUIColors(int iconColor, int iconSecondaryColor, int volumeSeekerColor, int mainSeekbarColor) {
        Drawable playIcon = mContext.getResources().getDrawable(R.drawable.ic_play_arrow_black_48dp);
        Drawable pauseIcon = mContext.getResources().getDrawable(R.drawable.ic_pause_black_48dp);
        Drawable nextIcon = mContext.getResources().getDrawable(R.drawable.ic_skip_next_black_48dp);
        Drawable prevIcon = mContext.getResources().getDrawable(R.drawable.ic_skip_previous_black_48dp);
        Drawable volumeNone = mContext.getResources().getDrawable(R.drawable.ic_volume_none);
        Drawable volumeAll = mContext.getResources().getDrawable(R.drawable.ic_volume_up);
        DrawableCompat.setTint(playIcon, iconColor);
        DrawableCompat.setTint(pauseIcon, iconColor);
        DrawableCompat.setTint(nextIcon, iconColor);
        DrawableCompat.setTint(prevIcon, iconColor);
        DrawableCompat.setTint(volumeNone, iconSecondaryColor);
        DrawableCompat.setTint(volumeAll, iconSecondaryColor);

        mPlayButton.setImageDrawable(playIcon);
        mPauseButton.setImageDrawable(pauseIcon);
        mNextButton.setImageDrawable(nextIcon);
        mPrevButton.setImageDrawable(prevIcon);
        mVolumeNoneIcon.setImageDrawable(volumeNone);
        mVolumeFullIcon.setImageDrawable(volumeAll);

        DrawableCompat.setTint(mVolume.getThumb(), volumeSeekerColor);
        DrawableCompat.setTint(mVolume.getProgressDrawable(), volumeSeekerColor);

        DrawableCompat.setTint(mProgress.getThumb(), mainSeekbarColor);
    }

    private int setProgress() {
        if (mDragging) {
            return 0;
        }

        int position = PlaybackManager.get().getCurrentPosInSong();
        int duration = (int) PlaybackManager.get().getCurrentSong().getSongDuration();

            if (mProgress != null) {
                mProgress.setProgress(position);
                mProgress.setMax(duration);
            }

        if (mEndTime != null)
            mEndTime.setText(PlaybackManager.get().getCurrentSong().getSongDurString());
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private void updatePausePlay() {
        if (PlaybackManager.get().isPlaying()) {
            mPlayButtonRoot.setVisibility(View.GONE);
            mPauseButtonRoot.setVisibility(View.VISIBLE);
        } else {
            mPlayButtonRoot.setVisibility(View.VISIBLE);
            mPauseButtonRoot.setVisibility(View.GONE);
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public void onSongChanged(Song song) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        updatePausePlay();
    }

    public boolean onKeyEvent(KeyEvent event){
        int vol;
        boolean handled = false;
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_VOLUME_UP:
                vol = mVolume.getProgress() + 1;
                if (vol > mVolume.getMax()) vol = mVolume.getMax();
                mVolume.setProgress(vol);
                handled = true;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                vol = mVolume.getProgress() - 1;
                if (vol < 0) vol = 0;
                mVolume.setProgress(vol);
                handled = true;
                break;
        }
        return handled;
    }

}
