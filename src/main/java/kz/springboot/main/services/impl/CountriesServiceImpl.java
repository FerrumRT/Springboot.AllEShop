package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Countries;
import kz.springboot.main.repositories.CountriesRepository;
import kz.springboot.main.services.CountriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountriesServiceImpl implements CountriesService {

    @Autowired
    private CountriesRepository countriesRepository;

    @Override
    public Countries findById(Long id) {
        return countriesRepository.getOne(id);
    }

    @Override
    public void save(Countries countries) {
        countriesRepository.save(countries);
    }

    @Override
    public void delete(Countries countries) {
        countriesRepository.delete(countries);
    }

    @Override
    public List<Countries> findAllCountries() {
        return countriesRepository.findAll();
    }
}
