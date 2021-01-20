package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Comments;
import kz.springboot.main.entities.ShopItems;
import kz.springboot.main.repositories.CommentsRepository;
import kz.springboot.main.services.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentsRepository commentsRepository;

    @Override
    public List<Comments> findAllOfItem(ShopItems item) {
        return commentsRepository.findAllByItemIdOrderByAddedDateAsc(item);
    }

    @Override
    public Comments findById(Long id) {
        return commentsRepository.getOne(id);
    }

    @Override
    public void save(Comments comment) {
        commentsRepository.save(comment);
    }

    @Override
    public void delete(Comments comment) {
        commentsRepository.delete(comment);
    }

    @Override
    public List<Comments> findAll() {
        return commentsRepository.findAll();
    }
}
