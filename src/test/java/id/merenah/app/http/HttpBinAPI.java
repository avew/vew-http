package id.merenah.app.http;

import id.merenah.app.http.dto.JsonDTO;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface HttpBinAPI {

    @GET("/json")
    Observable<Response<JsonDTO>> getJson();

    @GET("/status/{codes}")
    Observable<Response<Void>> getStatus(@Path("codes") int code);

}
