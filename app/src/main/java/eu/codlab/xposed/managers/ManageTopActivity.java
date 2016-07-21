package eu.codlab.xposed.managers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import eu.codlab.IPokemonGoService;
import eu.codlab.IPokemonInteraction;
import eu.codlab.go.PokemonGOService;
import eu.codlab.xposed.ScreenBrightnessView;

/**
 * Created by kevinleperf on 20/07/2016.
 */

public class ManageTopActivity implements IPokemonInteraction {

    private Activity activity;
    private ScreenBrightnessView _touch_view;

    private IPokemonGoService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("PokemonGO", "onServiceConnected");

            mService = IPokemonGoService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d("PokemonGO", "onServiceDisconnected");
        }
    };

    public ManageTopActivity() {

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        _touch_view = new ScreenBrightnessView(activity);

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
        activity.startService(getServiceIntent());
        Log.d("PokemonGO", "onCreate");
    }

    public void onResume() {
        Log.d("PokemonGO", "onResume");
        if (_touch_view != null)
            _touch_view.onResume();

        activity.bindService(getServiceIntent(), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onPause() {
        try {
            activity.unbindService(mServiceConnection);
        } catch (Exception e) {

        }
        Log.d("PokemonGO", "onPause");
        if (_touch_view != null)
            _touch_view.onPause();
    }

    private Intent getServiceIntent() {
        Intent intent = new Intent("eu.codlab.go.PokemonGOService");
        intent.setClassName("eu.codlab.go", PokemonGOService.class.getName());
        return intent;
    }

    @Override
    public void onPokemonNearby(int id, double latitude, double longitude, float distance_meter) {
        try {
            if (mService != null)
                mService.onPokemonNearby(id, latitude, longitude, distance_meter);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPokemonSpawn(long encounter_id, int id, double latitude, double longitude) {
        try {
            if (mService != null)
                mService.onPokemonSpawn(encounter_id, id, latitude, longitude);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
