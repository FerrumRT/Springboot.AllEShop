package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Brands;
import kz.springboot.main.repositories.BrandsRepository;
import kz.springboot.main.services.BrandsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandsService {

    @Autowired
    private BrandsRepository brandsRepository;

    @Override
    public Brands findById(Long id) {
        return brandsRepository.getOne(id);
    }

    @Override
    public void save(Brands brands) {
        brandsRepository.save(brands);
    }

    @Override
    public void delete(Brands brands) {
        brandsRepository.delete(brands);
    }

    @Override
    public List<Brands> allBrands() {
        return brandsRepository.findAll();
    }
}
