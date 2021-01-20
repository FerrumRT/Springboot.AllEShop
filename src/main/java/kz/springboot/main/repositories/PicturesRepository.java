package kz.springboot.main.repositories;

import kz.springboot.main.entities.Pictures;
import kz.springboot.main.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PicturesRepository extends JpaRepository<Pictures, Long> {
    List<Pictures> findAllByItemId(ShopItems items);
    Pictures findByUrl(String url);
}
