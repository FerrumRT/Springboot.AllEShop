package kz.springboot.main.services;

import kz.springboot.main.entities.Orders;
import kz.springboot.main.entities.ShopItems;

import java.util.List;

public interface OrdersService {

    List<Orders> getAllOrders();

    boolean buy(String cart, String buyer_name);
    boolean buy(List<Orders> orders, String buyer_name);

    List<Orders> parseToOrderList(String cart);
    int getFullPrice(List<Orders> orders);
    String parseToString(List<Orders> orders);
}
