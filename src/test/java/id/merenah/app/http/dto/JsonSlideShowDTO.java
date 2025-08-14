package id.merenah.app.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonSlideShowDTO {

    @JsonProperty("author")
    private String author;
    @JsonProperty("date")
    private String date;
    @JsonProperty("slides")
    private List<JsonSlidesDTO> slides;
    @JsonProperty("title")
    private String title;
}
