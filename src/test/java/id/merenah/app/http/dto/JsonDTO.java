package id.merenah.app.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonDTO {

    @JsonProperty("slideshow")
    private JsonSlideShowDTO slideshow;

}
