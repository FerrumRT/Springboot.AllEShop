package kz.springboot.main.services;

import kz.springboot.main.entities.Categories;

import java.util.List;

public interface CategoriesService {
    Categories findById(Long id);
    void save(Categories category);
    void delete(Categories category);
    List<Categories> findAll();
}
