package id.merenah.app.http;

import com.bartoszwesolowski.okhttp3.logging.CustomizableHttpLoggingInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import id.merenah.app.http.config.HttpConfig;
import id.merenah.app.http.config.HttpLoggingHttpInterceptor;
import id.merenah.app.http.config.HttpProxy;
import id.merenah.app.http.dto.JsonDTO;
import id.merenah.app.http.enumeration.ProxyType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Set;

@Slf4j
public class OkHttpTest {

    private Retrofit retrofit;
    private final Set<String> maskingLog = Set.of("user", "password", "name", "email", "first_name");

    private ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        mapper.coercionConfigFor(LogicalType.POJO)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Before
    public void init() {
        HttpConfig httpConfig = HttpConfig.builder()
                .url("https://httpbin.org")
                .retryConnectionFailure(false)
                .proxy(HttpProxy.builder()
                        .type(ProxyType.HTTP)
                        .proxy(false)
                        .auth(false)
                        .host("127.0.0.1")
                        .port(9999)
                        .build())
                .build();
        HttpLoggingHttpInterceptor httpLoggingHttpInterceptor = new HttpLoggingHttpInterceptor();
        httpLoggingHttpInterceptor.setMasking(maskingLog);
        CustomizableHttpLoggingInterceptor customizableHttpLoggingInterceptor = httpLoggingHttpInterceptor.init();
        httpConfig.setInterceptors(List.of(customizableHttpLoggingInterceptor));
        VewHttp vewHttp = new VewHttp();

        vewHttp.setConfig(httpConfig);
        retrofit = vewHttp
                .builder()
                .addConverterFactory(JacksonConverterFactory.create(mapper()))
                .build();
    }

    @Test
    public void testGet() {
        Response<JsonDTO> response = retrofit.create(HttpBinAPI.class)
                .getJson()
                .toBlocking().single();
        Assert.assertEquals(200, response.code());

    }
    @Test
    public void testGetStatus() {
        Response<Void> response = retrofit.create(HttpBinAPI.class)
                .getStatus(200)
                .toBlocking().single();
        Assert.assertEquals(200, response.code());

    }


}
