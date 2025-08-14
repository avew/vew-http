package id.merenah.app.http.config;//package com.pajakku.http.client.config;
//
//import com.pajakku.http.client.exception.HttpRetryException;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
//@Slf4j
//public class HttpRetryOnGetInterceptor implements Interceptor {
//
//    private int maxRetries;
//    private int retryDelayMillis;
//    private List<Integer> retryStatusCodes;
//
//    public HttpRetryOnGetInterceptor(int maxRetries, int retryDelayMillis, List<Integer> retryStatusCodes) {
//        this.maxRetries = maxRetries;
//        this.retryDelayMillis = retryDelayMillis;
//        this.retryStatusCodes = retryStatusCodes;
//    }
//
//
//    public HttpRetryOnGetInterceptor() {
//        this(3, 2000, List.of(502, 503, 504));
//    }
//
//    @SneakyThrows
//    @NotNull
//    @Override
//    public Response intercept(@NotNull Chain chain) {
//        Request request = chain.request();
//        Response response;
//        int tryCount = 0;
//
//        while (tryCount < maxRetries) {
//            try {
//                response = chain.proceed(request);
//
//                if (response.isSuccessful() || !request.method().equals("GET") || !retryStatusCodes.contains(response.code())) {
//                    return response;
//                }
//
//                log.warn("RETRY URL: {}, TRY: {}, STATUS CODE: {}", request.url(), tryCount + 1, response.code());
//
//            } catch (IllegalStateException e) {
//                throw new HttpRetryException(String.format("Failed to complete request after %d attempts", maxRetries), maxRetries, e);
//            } catch (Exception e) {
//                if (tryCount >= maxRetries - 1) {
//                    throw new HttpRetryException(String.format("Failed to complete request after %d attempts", maxRetries), maxRetries, e);
//                }
//            }
//
//            tryCount++;
//            try {
//                Thread.sleep(retryDelayMillis);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // Restore interrupted status
//                throw new HttpRetryException("Retry interrupted after " + tryCount + " attempts", tryCount, e);
//            }
//        }
//
//        // If all retries fail, throw HttpRetryException
//        throw new HttpRetryException("Failed to complete request after " + maxRetries + " attempts", maxRetries);
//
//    }
//}
