package kz.springboot.main.repositories;

import kz.springboot.main.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

}
