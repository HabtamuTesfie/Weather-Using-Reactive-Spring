package demo.com.weatherinformation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CurrentWeatherDTO {
    private LocalDateTime time;
    private int interval;
    private double temperature_2m;
    private double wind_speed_10m;
    private int relative_humidity_2m;
}
