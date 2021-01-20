package kz.springboot.main.controllers;

import kz.springboot.main.entities.*;
import kz.springboot.main.services.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Value("${file.item.viewPath}")
    private String viewPath;

    @Value("${file.item.uploadPath}")
    private String uploadPath;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BrandsService brandsService;

    @Autowired
    private CountriesService countriesService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private PicturesService picturesService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CommentsService commentsService;

    // ============================= ITEMS =============================

    @GetMapping(value = "/admin/items")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String adminItems(Model model) {
        model.addAttribute("current_user", getUserData());
        model.addAttribute("title", "Items");
        List<ShopItems> items = itemService.getAllItems();
        model.addAttribute("items", items);
        List<Brands> brands = brandsService.allBrands();
        model.addAttribute("brands", brands);
        return "admin/admin_items";
    }

    @PostMapping(value = "/addItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addTask(@RequestParam(name = "name", defaultValue = "Item") String name,
                          @RequestParam(name = "description", defaultValue = "Item without description") String description,
                          @RequestParam(name = "price", defaultValue = "500") double price,
                          @RequestParam(name = "stars") int stars,
                          @RequestParam(name = "small_picture", defaultValue = "https://media-exp1.licdn.com/dms/image/C4E0BAQGr4jMwOp7BBA/company-logo_200_200/0?e=215902440`0&v=beta&t=EYON0cF_iCIZWfmqOSGEYQrYM2TyaCysVHDNCSLiS6U") String small_picture,
                          @RequestParam(name = "large_picture", defaultValue = "https://media-exp1.licdn.com/dms/image/C4E0BAQGr4jMwOp7BBA/company-logo_200_200/0?e=2159024400&v=beta&t=EYON0cF_iCIZWfmqOSGEYQrYM2TyaCysVHDNCSLiS6U") String large_picture,
                          @RequestParam(name = "isTopPage", defaultValue = "false") boolean isTopPage,
                          @RequestParam(name = "brand") Long brand_id) {
        Date date = Date.valueOf(LocalDate.now());
        String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Brands brand = brandsService.findById(brand_id);
        ShopItems items = new ShopItems();
        items.setName(name);
        items.setDescription(description);
        items.setPrice(price);
        items.setStars(stars);
        items.setSmallPictureUrl(small_picture);
        items.setLargePictureUrl(large_picture);
        items.setAddedDate(Date.valueOf(modifiedDate));
        items.setInTopPage(isTopPage);
        items.setBrandId(brand);
        itemService.saveItem(items);
        return "redirect:/admin/items";
    }

    @PostMapping(value = "/assignCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignCategory(@RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "category_id") Long category_id) {
        Categories categories = categoriesService.findById(category_id);
        if (categories != null) {
            ShopItems items = itemService.getItem(item_id);
            if (item_id != null) {
                List<Categories> list = items.getCategories();
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(categories);
                itemService.saveItem(items);
            }
        }
        return "redirect:/admin/items/details/" + item_id;
    }

    @PostMapping(value = "/removeCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String removeCategory(@RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "category_id") Long category_id) {
        Categories categories = categoriesService.findById(category_id);
        if (categories != null) {
            ShopItems items = itemService.getItem(item_id);
            if (item_id != null) {
                List<Categories> list = items.getCategories();
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.remove(categories);
                itemService.saveItem(items);
            }
        }
        return "redirect:/admin/items/details/" + item_id;
    }

    @PostMapping(value = "/addImage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addImage(@RequestParam(name = "item_id") Long item_id,
                           @RequestParam(name = "image") MultipartFile file) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
            try {
                Pictures picture = new Pictures();
                picture = picturesService.save(picture);
                picture.setAdedDate(Date.valueOf(LocalDate.now()));
                byte[] bytes = file.getBytes();

                String pictureName = DigestUtils.sha1Hex("item_" + item_id + "_" + picture.getId() + "_!Picture");
                picture.setUrl(pictureName);
                picture.setItemId(itemService.getItem(item_id));
                picturesService.save(picture);

                Path path = Paths.get(uploadPath + pictureName + ".png");
                Files.write(path, bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "redirect:/admin/items/details/" + item_id;
    }

    @PostMapping(value = "/deleteImage/{image_url}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteImage(@RequestParam(name = "item_id") Long item_id,
                              @PathVariable(name = "image_url") String image_url) {
        try {
            picturesService.delete(picturesService.findByURL(image_url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/items/details/" + item_id;
    }

    @PostMapping(value = "/editItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String edit(Model model, @RequestParam(name = "id") Long id,
                       @RequestParam(name = "name", defaultValue = "Item") String name,
                       @RequestParam(name = "description", defaultValue = "Item without description") String description,
                       @RequestParam(name = "price", defaultValue = "500") double price,
                       @RequestParam(name = "stars") int stars,
                       @RequestParam(name = "small_picture") String small_picture,
                       @RequestParam(name = "large_picture") String large_picture,
                       @RequestParam(name = "isTopPage", defaultValue = "false") boolean isTopPage,
                       @RequestParam(name = "brand") Long brand_id) {
        Date date = Date.valueOf(LocalDate.now());
        String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Brands brand = brandsService.findById(brand_id);
        ShopItems items = itemService.getItem(id);
        items.setName(name);
        items.setDescription(description);
        items.setPrice(price);
        items.setStars(stars);
        items.setSmallPictureUrl(small_picture);
        items.setLargePictureUrl(large_picture);
        items.setAddedDate(Date.valueOf(modifiedDate));
        items.setInTopPage(isTopPage);
        items.setBrandId(brand);
        itemService.saveItem(items);
        return "redirect:/admin/items";
    }

    @GetMapping(value = "/deleteItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String delete(Model model, @RequestParam(name = "id") Long id) {
        ShopItems item = itemService.getItem(id);
        itemService.deleteItem(item);
        return "redirect:/admin/items";
    }

    @GetMapping(value = "/admin/items/details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String details(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        ShopItems item = itemService.getItem(id);
        model.addAttribute("item", item);
        List<Brands> brands = brandsService.allBrands();
        model.addAttribute("brands", brands);
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("title", item.getName());
        model.addAttribute("images", picturesService.itemsPictures(item));
        return "admin/detailsItem";
    }

    // ============================= COUNTRIES =============================

    @GetMapping(value = "/admin/countries")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String adminCountries(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Countries> countries = countriesService.findAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("title", "Countries");
        return "admin/admin_countries";
    }

    @PostMapping(value = "/addCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addCountry(@RequestParam(name = "name") String name,
                             @RequestParam(name = "code") String code) {
        Countries country = new Countries(null, name, code);
        countriesService.save(country);
        return "redirect:/admin/countries";
    }

    @PostMapping(value = "/editCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCountry(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "code") String code) {
        Countries country = countriesService.findById(id);
        country.setName(name);
        country.setCode(code);
        countriesService.save(country);
        return "redirect:/admin/countries";
    }

    @GetMapping(value = "/deleteCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteCountry(@RequestParam(name = "id") Long id) {
        Countries country = new Countries();
        country.setId(id);
        countriesService.delete(country);
        return "redirect:/admin/counties";
    }

    @GetMapping(value = "/admin/countries/details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsCountry(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        Countries country = countriesService.findById(id);
        model.addAttribute("country", country);
        model.addAttribute("title", country.getName());
        return "admin/detailsCountry";
    }

    // ============================= BRANDS =============================

    @GetMapping(value = "/admin/brands")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String adminBrands(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Brands> brands = brandsService.allBrands();
        model.addAttribute("brands", brands);
        List<Countries> countries = countriesService.findAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("title", "Brands");
        return "admin/admin_brands";
    }

    @PostMapping(value = "/addBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addBrand(@RequestParam(name = "name") String name,
                           @RequestParam(name = "country_id") Long id) {
        Brands brand = new Brands(null, name, countriesService.findById(id));
        brandsService.save(brand);
        return "redirect:/admin/brands";
    }

    @PostMapping(value = "/editBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editBrand(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "country_id") Long country_id) {
        Brands brand = brandsService.findById(id);
        brand.setName(name);
        brand.setCountryId(countriesService.findById(country_id));
        brandsService.save(brand);
        return "redirect:/admin/brands";
    }

    @GetMapping(value = "/deleteBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteBrand(@RequestParam(name = "id") Long id) {
        Brands brand = new Brands();
        brand.setId(id);
        brandsService.delete(brand);
        return "redirect:/admin/brands";
    }

    @GetMapping(value = "/admin/brands/details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsBrand(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        Brands brand = brandsService.findById(id);
        model.addAttribute("brand", brand);
        List<Countries> countries = countriesService.findAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("title", brand.getName());
        return "admin/detailsBrand";
    }

    // ============================= CATEGORIES =============================

    @GetMapping(value = "/admin/categories")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String adminCategories(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Categories");
        return "admin/admin_categories";
    }

    @PostMapping(value = "/addCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addCategory(@RequestParam(name = "name") String name,
                              @RequestParam(name = "logo") String logo) {
        Categories category = new Categories();
        category.setName(name);
        category.setLogoURL(logo);
        categoriesService.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping(value = "/editCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCategory(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "name") String name,
                               @RequestParam(name = "logo") String logo) {
        Categories category = categoriesService.findById(id);
        category.setName(name);
        category.setLogoURL(logo);
        categoriesService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping(value = "/deleteCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteCategory(@RequestParam(name = "id") Long id) {
        Categories category = new Categories();
        category.setId(id);
        categoriesService.delete(category);
        return "redirect:/admin/categories";
    }

    @GetMapping(value = "/admin/categories/details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String detailsCategory(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        Categories category = categoriesService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("title", category.getName());
        return "admin/detailsCategory";
    }

    // ============================= ROLES =============================

    @GetMapping(value = "/admin/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminRoles(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Roles> roles = rolesService.allRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("title", "Roles");
        return "admin/admin_roles";
    }

    @PostMapping(value = "/addRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addRole(@RequestParam(name = "role") String role,
                          @RequestParam(name = "description") String description) {
        Roles roles = new Roles();
        roles.setRole(role);
        roles.setDescription(description);
        rolesService.save(roles);
        return "redirect:/admin/roles";
    }

    @PostMapping(value = "/editRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editRole(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "role") String role,
                           @RequestParam(name = "description") String description) {
        Roles roles = rolesService.findById(id);
        roles.setRole(role);
        roles.setDescription(description);
        rolesService.save(roles);
        return "redirect:/admin/roles";
    }

    @GetMapping(value = "/deleteRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteRole(@RequestParam(name = "id") Long id) {
        Roles role = new Roles();
        role.setId(id);
        rolesService.delete(role);
        return "redirect:/admin/roles";
    }

    @GetMapping(value = "/admin/roles/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String detailsRole(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        Roles role = rolesService.findById(id);
        model.addAttribute("role", role);
        model.addAttribute("title", role.getRole());
        return "admin/detailsRole";
    }

    // ============================= USERS =============================

    @GetMapping(value = "/admin/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminUsers(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Users> users = usersService.findAllUsers();
        model.addAttribute("users", users);
        List<Roles> roles = rolesService.allRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("title", "Users");
        return "admin/admin_users";
    }

    @PostMapping(value = "/addUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUser(@RequestParam(name = "email") String email,
                          @RequestParam(name = "password") String password,
                          @RequestParam(name = "full_name") String full_name) {
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(full_name);
        if (usersService.createUser(user) != null) {
            Long id = usersService.getUserByEmail(email).getId();
            return "redirect:/admin/details/" + id;
        }
        usersService.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/editUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editUser(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "email") String email,
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "full_name") String full_name) {
        Users user = usersService.findById(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(full_name);
        usersService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/deleteUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        Users users = new Users();
        users.setId(id);
        usersService.delete(users);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/admin/users/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String detailsUser(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("current_user", getUserData());
        Users user = usersService.findById(id);
        model.addAttribute("user", user);
        List<Roles> roles = rolesService.allRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("title", user.getFullName());
        return "admin/detailsUser";
    }

    @PostMapping(value = "/assignRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String assignRole(@RequestParam(name = "user_id") Long user_id,
                             @RequestParam(name = "role_id") Long role_id) {
        Roles role = rolesService.findById(role_id);
        if (role != null) {
            Users user = usersService.findById(user_id);
            if (user_id != null) {
                List<Roles> list = user.getRoles();
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(role);
                usersService.save(user);
            }
        }
        return "redirect:/admin/users/details/" + user_id;
    }

    @PostMapping(value = "/removeRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String removeRole(@RequestParam(name = "user_id") Long user_id,
                             @RequestParam(name = "role_id") Long role_id) {
        Roles role = rolesService.findById(role_id);
        if (role != null) {
            Users user = usersService.findById(user_id);
            if (user_id != null) {
                List<Roles> list = user.getRoles();
                list.remove(role);
                usersService.save(user);
            }
        }
        return "redirect:/admin/users/details/" + user_id;
    }

    // ============================= Comments =============================

    @PostMapping(value = "/addComment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "comment") String text,
                             @RequestParam(name = "itemId") Long itemId) {
        Comments comment = new Comments();
        comment.setComment(text);
        comment.setItemId(itemService.getItem(itemId));
        comment.setAuthor(getUserData());
        comment.setAddedDate(new Timestamp(System.currentTimeMillis()));
        commentsService.save(comment);
        return "redirect:/details/" + itemId;
    }

    @GetMapping(value = "/deleteComment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable(name = "commentId") Long commentId) {
        Users user = getUserData();
        Comments comment = commentsService.findById(commentId);
        if (user != null && comment != null && user.getRoles() != null)
            if (user == comment.getAuthor() ||
                    user.getRoles().contains(rolesService.findById(2L)) ||
                    user.getRoles().contains(rolesService.findById(3L)))
                commentsService.delete(comment);
        return "redirect:/details/" + comment.getItemId().getId();
    }

    @PostMapping(value = "/editComment")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "comment") String text,
                              @RequestParam(name = "itemId") Long itemId) {
        Users user = getUserData();
        Comments comment = commentsService.findById(id);
        if (comment != null)
            if (user.getId().equals(comment.getAuthor().getId())) {
                comment.setComment(text);
                comment.setItemId(itemService.getItem(itemId));
                commentsService.save(comment);
            }
        return "redirect:/details/" + itemId;
    }

    // ============================= Orders =============================

    @GetMapping(value = "/admin/orders")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String adminOrders(Model model) {
        model.addAttribute("current_user", getUserData());
        List<Orders> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("title", "Orders");
        return "admin/admin_orders";
    }

    @GetMapping(value = "/viewPhoto/{url}", produces = {MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody
    byte[] viewPhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = "";
        InputStream in = null;
        if (url != null) {
            pictureURL = viewPath + url + ".png";

            try {

                ClassPathResource resource = new ClassPathResource(pictureURL);
                in = resource.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return IOUtils.toByteArray(in);
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
