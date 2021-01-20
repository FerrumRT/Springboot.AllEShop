package kz.springboot.main.repositories;

import kz.springboot.main.entities.Orders;
import kz.springboot.main.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Orders findByItemId(ShopItems items);
}
