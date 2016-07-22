package eu.codlab.go.webservice;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by kevinleperf on 21/07/2016.
 */
public class WebServiceProvider {
    private static WebServiceProvider ourInstance = new WebServiceProvider();
    private GoCodlabEu mGoCodlabEu;

    public static WebServiceProvider getInstance() {
        return ourInstance;
    }

    private WebServiceProvider() {
    }

    public GoCodlabEu getGoCodlabEu() {
        if (mGoCodlabEu == null) {
            mGoCodlabEu = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://go.codlab.eu/")
                    .build()
                    .create(GoCodlabEu.class);
        }

        return mGoCodlabEu;
    }
}
