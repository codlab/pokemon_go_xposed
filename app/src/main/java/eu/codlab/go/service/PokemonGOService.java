package eu.codlab.go.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import eu.codlab.IPokemonGoService;
import eu.codlab.IPokemonInteraction;
import eu.codlab.go.models.Encounter;
import eu.codlab.go.models.ModelFactory;
import eu.codlab.go.models.Nearby;
import eu.codlab.go.webservice.GoCodlabEu;
import eu.codlab.go.webservice.WebServiceProvider;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public class PokemonGOService extends Service implements IPokemonInteraction {

    private enum State {
        WAITING,
        RUNNING
    }

    private IPokemonGoService.Stub mBinder;
    private ReentrantLock mReentrantLock;
    private Runnable mRunnable;
    private Handler mHandler;
    private State mState;
    private long mCurrentTimeout;

    public PokemonGOService() {
        super();

        mBinder = new IPokemonGoService.Stub() {
            @Override
            public void onPokemonNearby(int id, double latitude, double longitude, float distance_meter) throws RemoteException {
                PokemonGOService.this.onPokemonNearby(id, latitude, longitude, distance_meter);
            }

            @Override
            public void onPokemonSpawn(long encounter_id, int id, double latitude, double longitude) throws RemoteException {
                PokemonGOService.this.onPokemonSpawn(encounter_id, id, latitude, longitude);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mReentrantLock = new ReentrantLock();
        mState = State.WAITING;
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mReentrantLock.lock();
                if (State.WAITING.equals(mState)) {
                    mState = State.RUNNING;
                    mReentrantLock.unlock();
                    sendPokemonEncounter();
                } else {
                    mReentrantLock.unlock();
                }
            }
        };

        resendInformation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPokemonNearby(int id, double latitude, double longitude, float distance_meter) {
        Nearby nearby = ModelFactory.createNearby(id, latitude, longitude, distance_meter);

        ModelFactory.saveNearBy(nearby);
    }

    @Override
    public void onPokemonSpawn(long encounter_id, int id, double latitude, double longitude) {
        Encounter encounter = ModelFactory.createEncounter(id, latitude, longitude, encounter_id);

        ModelFactory.saveEncounter(encounter);

        resendInformation();
    }

    private void checkResendInformation(boolean was_success) {
        if (was_success) {
            mCurrentTimeout = 5;
        } else {
            mCurrentTimeout *= 2;
            if (mCurrentTimeout >= 400) mCurrentTimeout = 400;
        }

        mHandler.postDelayed(mRunnable, mCurrentTimeout * 1000);
    }

    private void resendInformation() {
        mHandler.post(mRunnable);
    }

    private void sendPokemonEncounter() {
        final List<Encounter> encounters = ModelFactory.getWaitingEncounters();

        if (encounters.size() > 0) {
            GoCodlabEu service = WebServiceProvider.getInstance()
                    .getGoCodlabEu();

            Call<Response> response = service.postPokemonEncounter(encounters);

            response.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Response<Response> response, Retrofit retrofit) {
                    boolean success = false;
                    if (response.isSuccess()) {
                        success = true;
                        for (Encounter encounter : encounters) {
                            encounter.delete();
                        }
                    }

                    checkResendInformation(success);
                }

                @Override
                public void onFailure(Throwable t) {
                    checkResendInformation(false);
                }
            });
        } else {
            mReentrantLock.lock();
            mState = State.WAITING;
            mReentrantLock.unlock();
        }
    }
}
