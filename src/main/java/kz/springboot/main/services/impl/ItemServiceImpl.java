package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Brands;
import kz.springboot.main.entities.ShopItems;
import kz.springboot.main.repositories.ShopItemRepository;
import kz.springboot.main.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Override
    public ShopItems addItem(ShopItems item) {
        return shopItemRepository.save(item);
    }

    @Override
    public List<ShopItems> getAllItems() {
        return shopItemRepository.findAll();
    }

    @Override
    public List<ShopItems> getAllTopItems() {
        return shopItemRepository.findAllByInTopPageIsTrue();
    }

    @Override
    public ShopItems getItem(Long id) {
        return shopItemRepository.getOne(id);
    }

    @Override
    public void deleteItem(ShopItems item) {
        shopItemRepository.delete(item);
    }

    @Override
    public ShopItems saveItem(ShopItems item) {
        return shopItemRepository.save(item);
    }

    @Override
    public List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceAsc(String name, Brands brand) {
        return shopItemRepository.findAllByNameLikeAndBrandIdEqualsOrderByPriceAsc(name, brand);
    }

    @Override
    public List<ShopItems> findAllByNameLikeAndBrandIdEqualsOrderByPriceDesc(String name, Brands brand) {
        return shopItemRepository.findAllByNameLikeAndBrandIdEqualsOrderByPriceDesc(name, brand);
    }

    @Override
    public List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceAsc(String name, Brands brand, double price1, double price2) {
        return shopItemRepository.findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceAsc(name, brand, price1, price2);
    }

    @Override
    public List<ShopItems> findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceDesc(String name, Brands brand, double price1, double price2) {
        return shopItemRepository.findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceDesc(name, brand, price1, price2);
    }

    @Override
    public List<ShopItems> findAllByBrandEquals(Brands brand) {
        return shopItemRepository.findAllByBrandIdEquals(brand);
    }
}
