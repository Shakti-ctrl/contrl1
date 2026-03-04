package com.family.parentalcontrol.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TripleTapDetector {
    private static final String TAG = "TripleTapDetector";
    private static final long TRIPLE_TAP_WINDOW = 1000; // 1 second window for 3 taps
    private static final int REQUIRED_TAPS = 3;

    private List<Long> tapTimes;
    private Handler handler;
    private OnTripleTapListener listener;

    public interface OnTripleTapListener {
        void onTripleTapDetected();
    }

    public TripleTapDetector(OnTripleTapListener listener) {
        this.tapTimes = new ArrayList<>();
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void recordTap() {
        long currentTime = System.currentTimeMillis();
        tapTimes.add(currentTime);

        Log.d(TAG, "Tap recorded. Total taps: " + tapTimes.size());

        // Remove taps outside the time window
        tapTimes.removeIf(time -> (currentTime - time) > TRIPLE_TAP_WINDOW);

        if (tapTimes.size() == REQUIRED_TAPS) {
            Log.d(TAG, "TRIPLE TAP DETECTED! Activating SOS Mode!");
            if (listener != null) {
                listener.onTripleTapDetected();
            }
            tapTimes.clear();
        }

        // Reset after window expires
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            if (!tapTimes.isEmpty()) {
                Log.d(TAG, "Tap window expired, resetting counter");
                tapTimes.clear();
            }
        }, TRIPLE_TAP_WINDOW);
    }

    public void reset() {
        tapTimes.clear();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Tap detector reset");
    }
}
