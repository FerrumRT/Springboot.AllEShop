package kz.springboot.main.services;

import kz.springboot.main.entities.Countries;

import java.util.List;

public interface CountriesService {
    Countries findById(Long id);
    void save(Countries countries);
    void delete(Countries countries);
    List<Countries> findAllCountries();
}
