package id.merenah.app.http.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpTimeout {

    @Builder.Default
    private long connectTimeout = 5L;
    @Builder.Default
    private TimeUnit connectTimeoutUnit = TimeUnit.MINUTES;

    @Builder.Default
    private long readTimeout = 5L;
    @Builder.Default
    private TimeUnit readTimeoutUnit = TimeUnit.MINUTES;

    @Builder.Default
    private long writeTimeout = 5L;
    @Builder.Default
    private TimeUnit writeTimeoutUnit = TimeUnit.MINUTES;
}
