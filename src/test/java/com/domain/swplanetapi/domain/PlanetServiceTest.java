package com.domain.swplanetapi.domain;

import com.domain.swplanetapi.repository.PlanetRepository;
import com.domain.swplanetapi.service.PlanetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.domain.swplanetapi.common.PlanetConstants.INVALID_PLANET;
import static com.domain.swplanetapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @InjectMocks
    private PlanetService service;

    @Mock
    private PlanetRepository repository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Mockito.when(repository.save(PLANET)).thenReturn(PLANET);

        Planet sut = service.save(PLANET);

        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        Mockito.when(repository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> service.save(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void  getPlanet_ByExistingId_ReturnsPlanet() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(PLANET));

        Optional<Planet> planet = service.findById(1L);

        assertThat(planet).isNotEmpty();
        assertThat(planet.get()).isEqualTo(PLANET);
    }

    @Test
    public void  getPlanet_ByUnexistingId_ReturnsEmpty() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Planet> planet = service.findById(1L);

        assertThat(planet).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        Mockito.when(repository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        Optional<Planet> planet = service.findByName(PLANET.getName());

        assertThat(planet).isNotEmpty();
        assertThat(planet.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() {
        final String name = "Unexisting name";
        Mockito.when(repository.findByName(name)).thenReturn(Optional.empty());

        Optional<Planet> planet = service.findByName(name);

        assertThat(planet).isEmpty();
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>() {{
            add(PLANET);
        }};
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));

        Mockito.when(repository.findAll(query)).thenReturn(planets);

        List<Planet> list = service.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo(PLANET);
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() {
        Mockito.when(repository.findAll((Example<Planet>) any())).thenReturn(Collections.emptyList());

        List<Planet> list = service.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(list).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> service.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanet_WithExistingId_ThrowAnyException() {
        doThrow(new RuntimeException()).when(repository).deleteById(99L);

        assertThatThrownBy( () -> service.remove(99L)).isInstanceOf(RuntimeException.class);
    }
}
