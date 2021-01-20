package kz.springboot.main.services;

import kz.springboot.main.entities.Pictures;
import kz.springboot.main.entities.ShopItems;

import java.util.List;

public interface PicturesService {

    List<Pictures> itemsPictures(ShopItems item);

    Pictures findById(Long id);
    Pictures findByURL(String url);
    Pictures save(Pictures pictures);
    void delete(Pictures pictures);
    List<Pictures> allPictures();

}
