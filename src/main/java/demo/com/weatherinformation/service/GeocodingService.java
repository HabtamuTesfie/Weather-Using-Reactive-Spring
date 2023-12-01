package demo.com.weatherinformation.service;

import demo.com.weatherinformation.model.LocationInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class GeocodingService {
    private final WebClient webClient;

    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient    = webClientBuilder.build();
    }

    public Mono<LocationInfo> extractLocationInfo(String apiUrl, String location, String apiKey) {
        String uri = buildUrl(location, apiUrl, apiKey);

        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(LocationInfo[].class)
            .mapNotNull(locationInfoArray -> {
                if (locationInfoArray != null && locationInfoArray.length > 0) {
                    LocationInfo locationInfo = locationInfoArray[0];
                    return new LocationInfo(locationInfo.getName(), locationInfo.getLat(), locationInfo.getLon());
                }
                return null;
            });
    }

    private String buildUrl(String location, String uri, String apiKey) {
        return UriComponentsBuilder.fromUriString(uri)
            .queryParam("q", location)
            .queryParam("limit", 1)
            .queryParam("appid", apiKey)
            .build()
            .toUriString();
    }
}

