# VewHttp

A robust Java HTTP client library built on top of OkHttp3 and Retrofit2, providing simplified configuration and enhanced features for HTTP communication.

## Features

- 🚀 **Easy Configuration**: Simple builder pattern for HTTP client setup
- 🔒 **Proxy Support**: HTTP/HTTPS/SOCKS proxy configuration with authentication
- ⏱️ **Timeout Management**: Configurable connection, read, and write timeouts
- 🔧 **Connection Pooling**: Optimized connection management
- 📝 **Comprehensive Logging**: Customizable HTTP request/response logging with masking
- 🛡️ **SSL/TLS Support**: Flexible SSL configuration including certificate pinning
- 🔄 **Retry Mechanism**: Built-in retry logic for failed requests
- 📡 **Simple Proxy Server**: Built-in HTTP proxy server implementation

## Requirements

- Java 21+
- Maven 3.6+

## Dependencies

- OkHttp3 4.12.0
- Retrofit2 2.9.0
- Lombok 1.18.30
- Jackson/Gson for JSON serialization
- Logback for logging

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>id.merenah.app.http</groupId>
    <artifactId>vew-http</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

## Quick Start

### Basic Usage

```java
import id.merenah.app.http.VewHttp;
import id.merenah.app.http.config.HttpConfig;

// Create HTTP configuration
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .build();

// Initialize VewHttp client
VewHttp vewHttp = new VewHttp();
vewHttp.setConfig(config);

// Build Retrofit instance
Retrofit retrofit = vewHttp.builder()
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

### Configuration Options

#### Proxy Configuration

```java
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .proxy(HttpProxy.builder()
        .proxy(true)
        .type(ProxyType.HTTP)
        .host("proxy.example.com")
        .port(8080)
        .auth(true)
        .username("proxyuser")
        .password("proxypass")
        .build())
    .build();
```

#### Timeout Configuration

```java
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .timeout(HttpTimeout.builder()
        .connectTimeout(30)
        .readTimeout(60)
        .writeTimeout(60)
        .callTimeout(120)
        .build())
    .build();
```

#### Connection Pool Configuration

```java
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .connectionPool(HttpConnectionPool.builder()
        .maxIdleConnections(10)
        .keepAliveDuration(5)
        .build())
    .build();
```

#### SSL/TLS Configuration

```java
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .tls(HttpTls.TLS_1_2)
    .hostnameVerifier(false) // Disable hostname verification
    .trustAllCerts(true)     // Trust all certificates (use with caution)
    .build();
```

### Logging Configuration

```java
HttpConfig config = HttpConfig.builder()
    .url("https://api.example.com")
    .logging(HttpLoggingHttpInterceptor.builder()
        .level(HttpLoggingInterceptor.Level.BODY)
        .masking(true)
        .maskingFields(Set.of("password", "token", "secret"))
        .build())
    .build();
```

### Using the Simple Proxy Server

```java
// Start a simple HTTP proxy server on port 9999
SimpleProxyServer proxy = new SimpleProxyServer(9999);
proxy.listen();
```

## API Examples

### Creating a Retrofit Service

```java
public interface ApiService {
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);
    
    @POST("users")
    Call<User> createUser(@Body User user);
}

// Usage
ApiService service = retrofit.create(ApiService.class);
Response<User> response = service.getUser(123).execute();
```

### With RxJava Support

```java
Retrofit retrofit = vewHttp.builder()
    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

## Configuration Classes

### HttpConfig
Main configuration class that accepts:
- `url`: Base URL for the HTTP client
- `proxy`: Proxy configuration
- `timeout`: Timeout settings
- `connectionPool`: Connection pool settings
- `headers`: Default headers
- `tls`: SSL/TLS configuration
- `logging`: Logging configuration
- `retryConnectionFailure`: Enable/disable retry on connection failure

### HttpProxy
Proxy configuration supporting:
- HTTP, HTTPS, and SOCKS proxies
- Authentication with username/password
- Custom proxy selectors

### HttpTimeout
Timeout configuration for:
- Connection timeout
- Read timeout
- Write timeout
- Call timeout

## Testing

Run the test suite:

```bash
mvn test
```

The test suite includes examples of:
- Basic HTTP requests
- Proxy configuration
- JSON serialization/deserialization
- Error handling

## Building

Build the project:

```bash
mvn clean compile
```

Create JAR file:

```bash
mvn clean package
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Asep Rojali**
- Email: aseprojali@gmail.com
- Website: https://merenah.id

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support and questions, please open an issue on the GitHub repository.
