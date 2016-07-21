package eu.codlab.go.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by kevinleperf on 21/07/2016.
 */

@Database(name = PokemonTable.NAME, version = PokemonTable.VERSION)
public class PokemonTable {
    public final static String NAME = "PokemonTable";

    public final static int VERSION = 1;
}
