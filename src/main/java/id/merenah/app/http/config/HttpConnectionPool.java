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
public class HttpConnectionPool {


    @Builder.Default
    private final int maxIdle = 5;
    @Builder.Default
    private final int keepAlive = 5;
    @Builder.Default
    private TimeUnit keepAliveDuration = TimeUnit.MINUTES;
}
