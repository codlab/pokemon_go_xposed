// IPokemonGoService.aidl
package eu.codlab;

// Declare any non-default types here with import statements

interface IPokemonGoService {

    void onPokemonNearby(int id, double latitude, double longitude, float distance_meter);

    void onPokemonSpawn(long encounter_id, int id, double latitude, double longitude);
}
