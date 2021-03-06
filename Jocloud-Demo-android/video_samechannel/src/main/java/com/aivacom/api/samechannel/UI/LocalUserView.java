package com.aivacom.api.samechannel.UI;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aivacom.api.rtc.FacadeRtcManager;
import com.aivacom.api.samechannel.R;
import com.aivacom.api.samechannel.OnRemoteListener;
import com.thunder.livesdk.video.ThunderPreviewView;

/**
 *
 * Custom Local video view with some state
 * 自定义本地视频视图，增加一些状态
 */
public class LocalUserView extends RelativeLayout implements OnRemoteListener {

    private ThunderPreviewView mThunderPlayerView;
    private TextView tvNetStatus;
    private TextView tvUID;
    private ImageView ivAudio;

    private String uid;
    private boolean isInRoom = false;

    public boolean isInRoom() {
        return isInRoom;
    }

    public LocalUserView(Context context) {
        super(context);
        ini(context, null, 0);
    }

    public LocalUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini(context, attrs, 0);
    }

    public LocalUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ini(context, attrs, defStyleAttr);
    }

    private void ini(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_local_view, this);

        mThunderPlayerView = findViewById(R.id.view);
        tvNetStatus = findViewById(R.id.tvNetStatus);
        ivAudio = findViewById(R.id.ivAudio);
        tvUID = findViewById(R.id.tvUID);
    }

    public ThunderPreviewView getThunderPlayerView() {
        return mThunderPlayerView;
    }

    /**
     * Set up network poor display
     * 设置网络差显示
     */
    public void changeLowNetStatus(boolean isShown) {
        if (isShown) {
            tvNetStatus.setVisibility(VISIBLE);
        } else {
            tvNetStatus.setVisibility(GONE);
        }
    }

    /**
     * Join the room
     * 加入房间
     */
    public void joinRoom(String uid) {
        if (TextUtils.equals(this.uid, uid)) {
            return;
        }

        this.uid = uid;
        tvUID.setText(uid);
        tvNetStatus.setVisibility(GONE);
        FacadeRtcManager.getInstance().setLocalVideoCanvas(mThunderPlayerView, uid);
        isInRoom = true;
    }

    /**
     * Leave the room
     * 离开房间
     */
    public void leaveRoom() {
        this.uid = null;
        tvUID.setText("");
        tvNetStatus.setVisibility(GONE);

        mThunderPlayerView.clearViews();
        mThunderPlayerView.addViews(mThunderPlayerView.getSurfaceView());
        isInRoom = false;
    }

    /**
     * Don't keep the last frame
     * 不要保留最后一帧
     */
    public void resetView() {
        mThunderPlayerView.clearViews();
        mThunderPlayerView.addViews(mThunderPlayerView.getSurfaceView());
    }

    /**
     * Mute the audio
     * 静音
     */
    public void setAudioMute() {
        ivAudio.setVisibility(View.VISIBLE);
        ivAudio.setImageResource(R.drawable.img_audiostop3x);
    }

    /**
     * Set volume display
     * 设置音量显示
     */
    public void setAudioVolume(Integer micVolume) {
        if (micVolume == null) {
            ivAudio.setVisibility(View.GONE);
        } else {
            ivAudio.setVisibility(View.VISIBLE);
            if (micVolume <= 30) {
                ivAudio.setImageResource(R.drawable.img_audiovolume_level1);
            } else if (micVolume <= 60) {
                ivAudio.setImageResource(R.drawable.img_audiovolume_level2);
            } else {
                ivAudio.setImageResource(R.drawable.img_audiovolume_level3);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VideoSameChannelActivity.mRemoteListeners.add(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VideoSameChannelActivity.mRemoteListeners.remove(this);
    }

    @Override
    public void onJoinRoom(String uid) {

    }

    @Override
    public void onLeaveRoom(String uid) {
        if (TextUtils.equals(this.uid, uid)) {
            leaveRoom();
        }
    }
}
