package eu.codlab.go;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import eu.codlab.IPokemonGoService;
import eu.codlab.IPokemonInteraction;
import eu.codlab.go.models.Encounter;
import eu.codlab.go.models.ModelFactory;
import eu.codlab.go.models.Nearby;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public class PokemonGOService extends Service implements IPokemonInteraction {

    private final IPokemonGoService.Stub mBinder = new IPokemonGoService.Stub() {
        @Override
        public void onPokemonNearby(int id, double latitude, double longitude, float distance_meter) throws RemoteException {
            PokemonGOService.this.onPokemonNearby(id, latitude, longitude, distance_meter);
        }

        @Override
        public void onPokemonSpawn(long encounter_id, int id, double latitude, double longitude) throws RemoteException {
            PokemonGOService.this.onPokemonSpawn(encounter_id, id, latitude, longitude);
        }
    };
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();
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
    }

    private void sendPokemonEncounter(Encounter encounter) {

    }
}
