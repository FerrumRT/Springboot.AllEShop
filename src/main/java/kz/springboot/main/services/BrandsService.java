package kz.springboot.main.services;

import kz.springboot.main.entities.Brands;

import java.util.List;

public interface BrandsService {

    Brands findById(Long id);
    void save(Brands brands);
    void delete(Brands brands);
    List<Brands> allBrands();

}
