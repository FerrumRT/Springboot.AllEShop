package kz.springboot.main.services;

import kz.springboot.main.entities.Brands;
import kz.springboot.main.entities.ShopItems;

import java.util.List;

public interface ItemService {

    ShopItems addItem(ShopItems item);
    List<ShopItems> getAllItems();
    List<ShopItems> getAllTopItems();
    ShopItems getItem(Long id);
    void deleteItem(ShopItems item);
    ShopItems saveItem(ShopItems item);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceAsc(String name, Brands brand);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceDesc(String name, Brands brand);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceAsc(String name, Brands brand, double price1, double price2);
    List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceDesc(String name, Brands brand, double price1, double price2);
    List<ShopItems> findAllByBrandEquals(Brands brand);
}
