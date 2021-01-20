package kz.springboot.main.repositories;

import kz.springboot.main.entities.Brands;
import kz.springboot.main.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ShopItemRepository extends JpaRepository<ShopItems, Long> {

    List<ShopItems> findAllByInTopPageIsTrue();
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceAsc(String name, Brands brand);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceDesc(String name, Brands brand);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceAsc(String name, Brands brand, double price1, double price2);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceDesc(String name, Brands brand, double price1, double price2);
    List<ShopItems> findAllByBrandIdEquals(Brands brand);
}
