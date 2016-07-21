package eu.codlab;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public interface IPokemonInteraction {
    void onPokemonNearby(int id, float latitude, float longitude, float distance_meter);

    void onPokemonSpawn(long encounter_id, int id, float latitude, float longitude);
}
