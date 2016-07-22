package eu.codlab.go.webservice;

import java.util.List;

import eu.codlab.go.models.Encounter;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by kevinleperf on 21/07/2016.
 */

public interface GoCodlabEu {

    @POST("/api/pokemon/encounter")
    Call<Response> postPokemonEncounter(@Body List<Encounter> encounters);
}
