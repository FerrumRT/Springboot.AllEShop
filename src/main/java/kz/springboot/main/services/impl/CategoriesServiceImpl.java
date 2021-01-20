package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Categories;
import kz.springboot.main.repositories.CategoriesRepository;
import kz.springboot.main.services.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public Categories findById(Long id) {
        return categoriesRepository.getOne(id);
    }

    @Override
    public void save(Categories category) {
        categoriesRepository.save(category);
    }

    @Override
    public void delete(Categories category) {
        categoriesRepository.delete(category);
    }

    @Override
    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }
}
