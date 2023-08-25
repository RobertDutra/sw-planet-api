package com.domain.swplanetapi.service;

import com.domain.swplanetapi.domain.Planet;
import com.domain.swplanetapi.domain.QueryBuilder;
import com.domain.swplanetapi.repository.PlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {

    @Autowired
    private PlanetRepository repository;

    public Planet save(Planet planet) {
        return repository.save(planet);
    }

    public Optional<Planet> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Planet> findByName(String name) {
        return repository.findByName(name);
    }

    public List<Planet> list(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(climate, terrain));
        return repository.findAll(query);
    }

    public void remove(Long id) {
        repository.deleteById(id);
    }
}
