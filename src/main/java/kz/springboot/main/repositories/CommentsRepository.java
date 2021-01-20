package kz.springboot.main.repositories;

import kz.springboot.main.entities.Comments;
import kz.springboot.main.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    List<Comments> findAllByItemIdOrderByAddedDateAsc(ShopItems item);

}
