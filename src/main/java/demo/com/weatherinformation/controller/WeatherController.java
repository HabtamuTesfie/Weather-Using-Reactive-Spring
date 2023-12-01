package demo.com.weatherinformation.controller;

import demo.com.weatherinformation.model.WeatherDTO;
import demo.com.weatherinformation.service.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/api/weather")
public class WeatherController {

    private final GeocodingService geocodingService;
    private final WebClient webClient;

    @Value("${weather.uri}")
    private String baseUri;

    @Value("${openweathermap.uri}")
    private String openweatherUri;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherController(WebClient.Builder webClientBuilder,
                             GeocodingService geocodingService) {
        this.webClient = webClientBuilder.baseUrl(baseUri).build();
        this.geocodingService = geocodingService;
    }

    @GetMapping("/current")
    public Mono<WeatherDTO> getCurrentWeather(@RequestParam(name = "location") String location) {

        return geocodingService.extractLocationInfo(openweatherUri, location, apiKey)
            .flatMap(locationInfo -> {
                URI uri = UriComponentsBuilder.fromUriString(baseUri)
                    .queryParam("latitude", locationInfo.getLat())
                    .queryParam("longitude", locationInfo.getLon())
                    .queryParam("current", "temperature_2m,wind_speed_10m,relative_humidity_2m")
                    .build()
                    .toUri();

                return webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WeatherDTO.class)
                    .onErrorMap(ex -> {
                        log.error("Error fetching weather data for location: {}", location, ex);
                        return new RuntimeException("Failed to fetch weather data");
                    });
            })
            .onErrorMap(ex -> {
                log.error("Error extracting location information for location: {}", location, ex);
                return new RuntimeException("Failed to extract location information");
            });
    }

}
