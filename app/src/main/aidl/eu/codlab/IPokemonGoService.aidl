// IPokemonGoService.aidl
package eu.codlab;

// Declare any non-default types here with import statements

interface IPokemonGoService {

    void onPokemonNearby(int id, float latitude, float longitude, float distance_meter);

    void onPokemonSpawn(long encounter_id, int id, float latitude, float longitude);
}
