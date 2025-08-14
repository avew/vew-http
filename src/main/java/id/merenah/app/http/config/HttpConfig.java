package id.merenah.app.http.config;

import id.merenah.app.http.enumeration.HttpTls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Interceptor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpConfig {

    private String url;

    @Builder.Default
    private HttpProxy proxy = new HttpProxy();

    @Builder.Default
    private String agent = null;

    @Builder.Default
    private HttpTimeout timeout = new HttpTimeout();

//    @Builder.Default
//    private boolean masking = true;

    @Builder.Default
    private boolean retryConnectionFailure = false;

    //    @Builder.Default
//    private boolean logBody = false;
//
//    @Builder.Default
//    private boolean logHeader = false;
//
//    @Builder.Default
//    private List<String> excludeHeaders = new ArrayList<>();

    @Builder.Default
    private HttpTls TLS = HttpTls.TLS1_2;

    @Builder.Default
    private HttpConnectionPool connectionPool = new HttpConnectionPool();

    @Builder.Default
    private List<HttpHeader> headers = new ArrayList<>();

    @Builder.Default
    private List<Interceptor> networkInterceptors = new ArrayList<>();

    @Builder.Default
    private List<Interceptor> interceptors = new ArrayList<>();


    public static String mask(String value) {
        if (value == null) {
            return null;
        }

        char[] chs = new char[value.length()];
        for (int i = 1; i < chs.length; i++) {
            chs[i] = '*';
        }
        chs[0] = value.charAt(0);
        chs[chs.length - 1] = value.charAt(value.length() - 1);
        return new String(chs);
    }


}
