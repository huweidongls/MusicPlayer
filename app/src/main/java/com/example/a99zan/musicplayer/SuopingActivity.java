package com.example.a99zan.musicplayer;

import android.os.Bundle;
import android.view.WindowManager;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SuopingActivity extends SwipeBackActivity {

    SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_suoping);

        swipeBackLayout = getSwipeBackLayout();

        // 可以调用该方法，设置是否允许滑动退出
        setSwipeBackEnable(true);
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        swipeBackLayout.setEdgeSize(400);

    }

    @Override
    public void onBackPressed() {
    }
}
