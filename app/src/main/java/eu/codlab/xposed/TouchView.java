package eu.codlab.xposed;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by kevinleperf on 20/07/2016.
 */

public class TouchView extends View {

    private enum State {
        LIGHT,
        DARK
    }

    private PowerManager.WakeLock lockStatic = null;
    private State _current_state = State.DARK;
    private Handler _handler;
    private long _timeout;
    private long DELAY = 5;

    private Runnable _runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("PokemonGO", "timeout ? " + _timeout + " " + _current_state);

            if (State.LIGHT.equals(_current_state)) {
                if (_timeout <= 0) {
                    setDarkMode();
                } else {
                    //we have a small delay when this method is called
                    //but we will assume that lim deltaT -> 0
                    _timeout -= DELAY;
                    _handler.postDelayed(_runnable, DELAY * 1000);
                }
            }
        }
    };

    public TouchView(Context context) {
        super(context);

        _handler = new Handler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("PokemonGO", "onTOuch event");

        resetMode();
        return super.onTouchEvent(event);
    }

    public void onResume() {
        resetMode();
    }

    public void onPause() {
        _current_state = State.DARK;
        _handler.removeCallbacks(_runnable);
        _timeout = 0;
    }

    private void resetMode() {
        if (State.DARK.equals(_current_state)) {
            Log.d("PokemonGO", "resetMode");
            _current_state = State.LIGHT;
            _handler.post(_runnable);

            try {
                getLock(getContext()).acquire();
            } catch (Exception e) {

            }
        }
        _timeout = 30;
        setLuminosity(255 / 2);
    }

    private void setDarkMode() {
        Log.d("PokemonGO", "setDarkMode");
        _current_state = State.DARK;
        _handler.removeCallbacks(_runnable);
        try {
            getLock(getContext()).release();
        } catch (Exception e) {

        }
        setLuminosity(10);
    }

    private PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "TouchView");
            lockStatic.setReferenceCounted(true);
        }

        return lockStatic;
    }

    private void setLuminosity(int brightness) {
        Activity activity = (Activity) getContext();
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = (brightness * 1.f / 255.0f);
        activity.getWindow().setAttributes(lp);
    }
}
