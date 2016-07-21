package eu.codlab.go.models;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public final class ModelFactory {

    private ModelFactory() {

    }

    public static Encounter createEncounter(long pkmn_id, double latitude, double longitude, long encounter_id) {
        Encounter encounter = new Encounter();

        encounter.encounter_id = encounter_id;
        encounter.latitude = latitude;
        encounter.longitude = longitude;
        encounter.pkmn_id = pkmn_id;

        return encounter;
    }

    public static Nearby createNearby(long pkmn_id, double latitude, double longitude, float distance_meter) {
        Nearby nearby = new Nearby();

        nearby.distance_meter = distance_meter;
        nearby.latitude = latitude;
        nearby.longitude = longitude;
        nearby.pkmn_id = pkmn_id;

        return nearby;
    }

    public static void saveEncounter(Encounter encounter) {
        List<Encounter> list = null;
        if (encounter.encounter_id >= 0) {
            list = new Select()
                    .from(Encounter.class)
                    .where(ConditionGroup.clause()
                            .and(Encounter_Table.encounter_id.eq(encounter.encounter_id)))
                    .queryList();
        }

        if (list == null || list.size() == 0) {
            encounter.save();
        }
    }

    public static void saveNearBy(Nearby nearby) {
        Log.d("PokemonGO", "saveNearBy " + nearby);
        nearby.save();
    }
}
