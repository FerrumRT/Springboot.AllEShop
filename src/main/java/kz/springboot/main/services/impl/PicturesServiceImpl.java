package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Pictures;
import kz.springboot.main.entities.ShopItems;
import kz.springboot.main.repositories.PicturesRepository;
import kz.springboot.main.services.PicturesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PicturesServiceImpl implements PicturesService {

    @Autowired
    private PicturesRepository picturesRepository;

    @Override
    public List<Pictures> itemsPictures(ShopItems item) {
        return picturesRepository.findAllByItemId(item);
    }

    @Override
    public Pictures findById(Long id) {
        return picturesRepository.getOne(id);
    }

    @Override
    public Pictures findByURL(String url) {
        return picturesRepository.findByUrl(url);
    }

    @Override
    public Pictures save(Pictures picture) {
        return picturesRepository.save(picture);
    }

    @Override
    public void delete(Pictures picture) {
        picturesRepository.delete(picture);
    }

    @Override
    public List<Pictures> allPictures() {
        return picturesRepository.findAll();
    }
}
