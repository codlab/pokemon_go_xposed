package eu.codlab.go.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kevinleperf on 21/07/2016.
 */

@Table(database = PokemonTable.class)
public class Encounter extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    public long id;

    @Column
    public long encounter_id;

    @Column
    public long pkmn_id;

    @Column
    public double latitude;

    @Column
    public double longitude;


    public Encounter() {

    }
}
