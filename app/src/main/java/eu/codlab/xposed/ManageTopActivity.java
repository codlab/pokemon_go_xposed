package eu.codlab.xposed;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by kevinleperf on 20/07/2016.
 */

public class ManageTopActivity {

    private Activity activity;
    private TouchView _touch_view;

    public ManageTopActivity() {

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        _touch_view = new TouchView(activity);

        ViewGroup view = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Log.d("PokemonGO", "view := " + view.getClass().getName());
        int count = view.getChildCount();

        LinearLayout parent = null;

        for (int i = 0; i < count; i++) {
            View child = view.getChildAt(i);
            Log.d("PokemonGO", "view ? " + child.getClass().getSimpleName());

            if (child instanceof LinearLayout) {
                parent = (LinearLayout) child;
            }
        }

        if (parent != null) {
            view.removeView(parent);
            FrameLayout frame_layout = new FrameLayout(activity);
            frame_layout.addView(parent);
            view.addView(frame_layout);

            view.addView(_touch_view);
        }
    }

    public void onCreate() {
        Log.d("PokemonGO", "onCreate");
    }

    public void onResume() {
        Log.d("PokemonGO", "onResume");
        if (_touch_view != null)
            _touch_view.onResume();
    }

    public void onPause() {
        Log.d("PokemonGO", "onPause");
        if (_touch_view != null)
            _touch_view.onPause();
    }
}
