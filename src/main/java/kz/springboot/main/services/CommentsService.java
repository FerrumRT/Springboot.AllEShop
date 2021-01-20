package kz.springboot.main.services;


import kz.springboot.main.entities.Comments;
import kz.springboot.main.entities.ShopItems;

import java.util.List;

public interface CommentsService {

    List<Comments> findAllOfItem(ShopItems item);

    Comments findById(Long id);
    void save(Comments comment);
    void delete(Comments comment);
    List<Comments> findAll();
}
