package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Orders;
import kz.springboot.main.entities.ShopItems;
import kz.springboot.main.repositories.OrderRepository;
import kz.springboot.main.services.ItemService;
import kz.springboot.main.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public boolean buy(String cart, String buyer_name) {
        List<Orders> orders = parseToOrderList(cart);
        return buy(orders, buyer_name);
    }

    @Override
    public boolean buy(List<Orders> orders, String buyer_name) {
        for (Orders order : orders) {
            order.setBoughtDate(Timestamp.valueOf(LocalDateTime.now()));
            order.setBuyerName(buyer_name);
            orderRepository.save(order);
        }
        return true;
    }

    @Override
    public List<Orders> parseToOrderList(String cart) {
        List<Orders> orders = new ArrayList<>();
        Long item_id;
        int item_count;
        while (cart.contains("[")) {
            item_id = Long.parseLong(cart.substring(cart.indexOf("[") + 1, cart.indexOf("|")));
            item_count = Integer.parseInt(cart.substring(cart.indexOf("|") + 1, cart.indexOf("]")));
            cart = cart.substring(cart.indexOf("]") + 1);
            orders.add(new Orders(null, itemService.getItem(item_id), item_count, null, null));
        }
        return orders;
    }

    @Override
    public int getFullPrice(List<Orders> orders) {
        int price = 0;
        for (Orders order : orders) {
            price += order.getCount() * order.getItemId().getPrice();
        }
        return price;
    }

    @Override
    public String parseToString(List<Orders> orders) {
        StringBuilder cart = new StringBuilder();
        for (Orders order : orders) {
            cart.append("[").append(order.getItemId().getId()).append("|").append(order.getCount()).append("]");
        }
        return cart.toString();
    }
}
