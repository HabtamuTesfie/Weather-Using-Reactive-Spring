package demo.com.weatherinformation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeatherDTO {
    private double latitude;
    private double longitude;
    private String timezone;
    private double elevation;
    private CurrentWeatherDTO current;
}
