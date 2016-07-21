package eu.codlab.go;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import eu.codlab.IPokemonGoService;
import eu.codlab.IPokemonInteraction;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public class PokemonGOService extends Service implements IPokemonInteraction{

    private final IPokemonGoService.Stub mBinder = new IPokemonGoService.Stub() {
        @Override
        public void onPokemonNearby(int id, float latitude, float longitude, float distance_meter) throws RemoteException {
            PokemonGOService.this.onPokemonNearby(id, latitude, longitude, distance_meter);
        }

        @Override
        public void onPokemonSpawn(long encounter_id, int id, float latitude, float longitude) throws RemoteException {
            PokemonGOService.this.onPokemonSpawn(encounter_id, id, latitude, longitude);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPokemonNearby(int id, float latitude, float longitude, float distance_meter) {

    }

    @Override
    public void onPokemonSpawn(long encounter_id, int id, float latitude, float longitude) {

    }
}
