package kz.springboot.main.controllers;

import kz.springboot.main.entities.*;
import kz.springboot.main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Relation;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BrandsService brandsService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PicturesService picturesService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RolesService roleService;

    private void mainModel(Model model, String title, int cart) {
        List<Brands> brands = brandsService.allBrands();
        model.addAttribute("brands", brands);
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("cart_count", cart);
        model.addAttribute("lng");
        model.addAttribute("title", title);
        model.addAttribute("current_user", getUserData());
    }

    @GetMapping(value = "/")
    public String index(Model model, @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        mainModel(model, "title.main", Integer.parseInt(count));
        List<ShopItems> items = itemService.getAllTopItems();
        model.addAttribute("items", items);
        return "index";
    }

    @GetMapping(value = "/allItems")
    public String allItems(Model model, @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        mainModel(model, "title.all_items", Integer.parseInt(count));
        List<ShopItems> items = itemService.getAllItems();
        model.addAttribute("items", items);
        return "allItems";
    }

    @GetMapping(value = "/details/{id}")
    public String details(Model model, @PathVariable(name = "id") Long id,
                          @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        ShopItems item = itemService.getItem(id);
        mainModel(model, item.getName(), Integer.parseInt(count));
        Roles role_admin = roleService.findById(2L);
        Roles role_moderator = roleService.findById(3L);
        model.addAttribute("item", item);
        model.addAttribute("role_admin", role_admin);
        model.addAttribute("role_moderator", role_moderator);
        model.addAttribute("pictures", picturesService.itemsPictures(item));
        model.addAttribute("comments", commentsService.findAllOfItem(item));
        return "details";
    }

    @GetMapping(value = "/search")
    public String search(Model model, @RequestParam(name = "name") String name,
                         @RequestParam(name = "brand") long brand_id,
                         @RequestParam(name = "priceFrom", required = false) Integer priceFrom,
                         @RequestParam(name = "priceTo", required = false) Integer priceTo,
                         @RequestParam(name = "order") String order,
                         @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        mainModel(model, name, Integer.parseInt(count));
        model.addAttribute("name", name);
        if (priceFrom != null) model.addAttribute("priceFrom", priceFrom);
        else model.addAttribute("priceFrom");
        if (priceFrom != null) model.addAttribute("priceTo", priceTo);
        else model.addAttribute("priceTo");
        model.addAttribute("order", order);
        List<ShopItems> items;
        Brands brand = brandsService.findById(brand_id);
        model.addAttribute("brand", brand);
        if (order.equals("asc")) {
            if (priceFrom != null && priceTo != null)
                items = itemService.findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceDesc("%" + name + "%", brand, priceFrom, priceTo);
            else items = itemService.findAllByNameLikeAndBrandIdEqualsOrderByPriceDesc("%" + name + "%", brand);
        } else {
            if (priceFrom != null && priceTo != null)
                items = itemService.findAllByNameLikeAndBrandIdEqualsAndPriceBetweenOrderByPriceAsc("%" + name + "%", brand, priceFrom, priceTo);
            else items = itemService.findAllByNameLikeAndBrandIdEqualsOrderByPriceAsc("%" + name + "%", brand);
        }
        model.addAttribute("items", items);
        return "search";
    }

    @GetMapping(value = "/cart")
    public String cart(Model model,
                       @CookieValue(value = "cart", defaultValue = "") String cart_s,
                       @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        mainModel(model, "title.cart", Integer.parseInt(count));
        List<Orders> orders = ordersService.parseToOrderList(cart_s);
        model.addAttribute("full_price", ordersService.getFullPrice(orders));
        model.addAttribute("orders", orders);
        return "cart";
    }

    @PostMapping(value = "/to_cart")
    public String to_cart(@CookieValue(value = "cart", defaultValue = "") String cart,
                          @CookieValue(value = "cart_count", defaultValue = "0") int cart_count,
                          HttpServletResponse response,
                          @RequestParam(name = "item_id") Long item_id,
                          @RequestParam(name = "item_count") int item_count) {
        List<Orders> orders = ordersService.parseToOrderList(cart);
        int count = cart_count + item_count;
        if (cart.contains("[" + item_id + "|")) {
            for (Orders order : orders) {
                if (order.getItemId().getId().equals(item_id)) {
                    if (item_count == -1 && order.getCount() == 1)
                        orders.remove(order);
                    else
                        order.setCount(order.getCount() + item_count);
                    break;
                }
            }
        } else {
            orders.add(new Orders(null, itemService.getItem(item_id), item_count, null, null));
        }
        cart = ordersService.parseToString(orders);
        Cookie cookie = new Cookie("cart", cart);
        Cookie cookie2 = new Cookie("cart_count", (count + ""));
        response.addCookie(cookie);
        response.addCookie(cookie2);
        return "redirect:/cart";
    }

    @GetMapping(value = "/buy")
    public String buy(HttpServletResponse response,
                      @CookieValue(value = "cart", defaultValue = "") String cart) {
        ordersService.parseToOrderList(cart);
        Users user = getUserData();
        if (user == null)
            ordersService.buy(cart, "Someone");
        else
            ordersService.buy(cart, user.getFullName());

        Cookie cookie = new Cookie("cart", "");
        Cookie cookie2 = new Cookie("cart_count", "0");
        cookie.setMaxAge(0);
        cookie2.setMaxAge(0);
        response.addCookie(cookie);
        response.addCookie(cookie2);
        return "redirect:/cart";
    }

    private Users getUserData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) auth.getPrincipal();
            return usersService.getUserByEmail(secUser.getUsername());
        }
        return null;
    }
}