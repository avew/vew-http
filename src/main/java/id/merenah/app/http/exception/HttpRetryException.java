package id.merenah.app.http.exception;

public class HttpRetryException extends RuntimeException{

    private final int retries;

    public HttpRetryException(String message, int retries) {
        super(message);
        this.retries = retries;
    }

    public HttpRetryException(String message, int retries, Throwable cause) {
        super(message, cause);
        this.retries = retries;
    }

    public int getRetries() {
        return retries;
    }
}
