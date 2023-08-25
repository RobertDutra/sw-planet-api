package com.domain.swplanetapi.domain;

import com.domain.swplanetapi.repository.PlanetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.domain.swplanetapi.common.PlanetConstants.PLANET;
import static com.domain.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    PlanetRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet(){
        Planet planet = repository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        Planet empyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        assertThatThrownBy(() -> repository.save(empyPlanet)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> repository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> optionalPlanet = repository.findById(planet.getId());

        assertThat(optionalPlanet).isNotEmpty();
        assertThat(optionalPlanet.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findById(1l);

        assertThat(optionalPlanet).isEmpty();
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> optionalPlanet = repository.findByName(planet.getName());

        assertThat(optionalPlanet).isNotEmpty();
        assertThat(optionalPlanet.get()).isEqualTo(planet);

    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() {
        Optional<Planet> optionalPlanet = repository.findByName("name");

        assertThat(optionalPlanet).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = repository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = repository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);

    }

    @Test
    public void listPlanets_ReturnsNotPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> response = repository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        repository.deleteById(planet.getId());

        Planet removePlanet = testEntityManager.find(Planet.class, planet.getId());
        assertThat(removePlanet).isNull();
    }

//    @Test
//    public void removePlanet_WithUnexistingId_ThrowsException() {
//        assertThatThrownBy( () -> repository.deleteById(2L)).isInstanceOf(EmptyResultDataAccessException.class);
//    }
}
