package demo.com.weatherinformation;

import demo.com.weatherinformation.model.LocationInfo;
import demo.com.weatherinformation.model.WeatherDTO;
import demo.com.weatherinformation.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class WeatherControllerIntegrationTest {
    @Value("${openweathermap.uri}")
    private String openweatherUri;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GeocodingService geocodingService;

    @Test
    public void testGetCurrentWeather() {
        LocationInfo locationInfo = new LocationInfo(0.0, 0.0);
        when(geocodingService.extractLocationInfo(anyString(), anyString(), anyString()))
            .thenReturn(Mono.just(locationInfo));

        webTestClient.get()
            .uri("/api/weather/current?location=myTestLocation")
            .exchange()
            .expectStatus().isOk()
            .expectBody(WeatherDTO.class)
            .consumeWith(result -> {
                WeatherDTO weatherDTO = result.getResponseBody();
                assertNotNull(weatherDTO);
                assertTrue(weatherDTO.getCurrent().getTemperature_2m() >= 0);
                assertTrue(weatherDTO.getCurrent().getInterval() >= 0);
                assertTrue(weatherDTO.getCurrent().getRelative_humidity_2m() >= 0);
                assertTrue(weatherDTO.getCurrent().getWind_speed_10m() >= 0);
                assertTrue(weatherDTO.getLatitude() >= 0);
                assertTrue(weatherDTO.getLongitude() >= 0);
                assertNotNull(weatherDTO.getCurrent().getTime());
            });
        verify(geocodingService).extractLocationInfo(eq(openweatherUri), eq("myTestLocation"), eq(apiKey));
    }
}
