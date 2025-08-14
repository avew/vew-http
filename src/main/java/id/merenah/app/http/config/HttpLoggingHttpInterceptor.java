package id.merenah.app.http.config;

import com.bartoszwesolowski.okhttp3.logging.CustomizableHttpLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class HttpLoggingHttpInterceptor {

    private Set<String> masking = new HashSet<>();


    public HttpLoggingHttpInterceptor() {
        masking.add("password");
        masking.add("token");
        masking.add("secret");
        masking.add("key");
    }

    public Set<String> getMasking() {
        return masking;
    }

    public void setMasking(Set<String> masking) {
        this.masking = masking;
    }

    public CustomizableHttpLoggingInterceptor init() {
        CustomizableHttpLoggingInterceptor logging = new CustomizableHttpLoggingInterceptor(
                (String msg) -> {
                    String maskingLog = maskingLog(masking, msg);
                    if (StringUtils.isNotBlank(maskingLog))
                        log.info("HTTP {}", maskingLog(masking, msg));
                },
                true,
                true);
        logging.redactHeader("Authorization");
        logging.redactHeader("Cookie");
        masking.forEach(logging::redactHeader);
        return logging;
    }

    public String maskingLog(Set<String> words, String msg) {
        StringBuilder sb = new StringBuilder(msg);
        List<String> maskPatterns = words.stream().map(s -> "\"([" + s + "\"]+)\"\\s*:\\s*\"([^\"]+)\",?").collect(Collectors.toList());
        Pattern multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
                }
            });
        }
        return sb.toString();
    }

}

