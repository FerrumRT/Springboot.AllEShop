package kz.springboot.main.controllers;

import kz.springboot.main.entities.Users;
import kz.springboot.main.services.UsersService;
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

@Controller
public class AuthController {

    @Autowired
    private UsersService usersService;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPic}")
    private String defaultPic;

    @GetMapping(value = "/403")
    public String accessForbidden(Model model) {
        model.addAttribute("current_user", getUserData());
        model.addAttribute("title", "403 - Access Forbidden");
        return "auth/403";
    }

    @GetMapping(value = "/login")
    public String login(Model model, @RequestParam(name = "error", defaultValue = "false") boolean error,
                        @RequestParam(name = "success", defaultValue = "false") boolean success) {
        if (error) {
            model.addAttribute("error", "Login or password is incorrect");
            model.addAttribute("success", "");
        } else if (success) {
            model.addAttribute("error", "");
            model.addAttribute("success", "Registration complete!");
        } else {
            model.addAttribute("error", "");
            model.addAttribute("success", "");
        }
        model.addAttribute("current_user", getUserData());
        model.addAttribute("title", "title.login");
        return "auth/login";
    }

    @GetMapping(value = "/register")
    @PreAuthorize("isAnonymous()")
    public String register(Model model, @RequestParam(name = "error", defaultValue = "-1") int error) {
        if (error == 0)
            model.addAttribute("error", "Passwords doesn't match");
        else if (error == 1)
            model.addAttribute("error", "This email is already taken");
        else
            model.addAttribute("error", "");

        model.addAttribute("title", "title.register");
        return "auth/register";
    }

    @PostMapping(value = "/register")
    public String toRegister(@RequestParam(name = "user_email") String user_email,
                             @RequestParam(name = "user_password") String user_password,
                             @RequestParam(name = "re_user_password") String re_user_password,
                             @RequestParam(name = "user_full_name") String user_full_name) {

        if (user_password.equals(re_user_password)) {
            Users user = new Users();
            user.setEmail(user_email);
            user.setPassword(user_password);
            user.setFullName(user_full_name);
            if (usersService.createUser(user) != null) {
                return "redirect:/login?success=true";
            }
            return "redirect:/register?error=1";
        }

        return "redirect:/register?error=0";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, @CookieValue(value = "cart_count", defaultValue = "0") String count) {
        model.addAttribute("current_user", getUserData());
        model.addAttribute("cart_count", Integer.parseInt(count));
        model.addAttribute("title", getUserData().getFullName());

        return "authenticated/profile";
    }

    @PostMapping(value = "/change_profile")
    @PreAuthorize("isAuthenticated()")
    public String change_profile(@RequestParam(name = "full_name") String full_name) {
        Users user = getUserData();
        if (user != null) {
            user.setFullName(full_name);
            usersService.save(user);
            return "redirect:/profile";
        }
        return "redirect:/profile?error";
    }

    @PostMapping(value = "/change_password")
    @PreAuthorize("isAuthenticated()")
    public String change_password(@RequestParam(name = "old_password") String old_password,
                                  @RequestParam(name = "new_password") String new_password,
                                  @RequestParam(name = "re_new_password") String re_new_password) {
        if (new_password.equals(re_new_password))
            if (usersService.changePassword(getUserData(), old_password, new_password) != null)
                return "redirect:/profile?success";
        return "redirect:/profile?error";
    }

    @PostMapping(value = "/change_avatar")
    @PreAuthorize("isAuthenticated()")
    public String change_avatar(@RequestParam(name = "user_ava") MultipartFile file) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
            try {
                Users currentUser = getUserData();

                byte[] bytes = file.getBytes();

                String pictureName = DigestUtils.sha1Hex("avatar_" + currentUser.getId() + "_!Picture");

                Path path = Paths.get(uploadPath + pictureName + ".png");
                Files.write(path, bytes);

                currentUser.setPictureURL(pictureName);
                usersService.save(currentUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "redirect:/profile";
    }

    @GetMapping(value = "/viewAvatar/{url}", produces = {MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody
    byte[] viewPhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPic;
        if (url != null) {
            pictureURL = viewPath + url + ".png";
        }

        InputStream in;

        try {

            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();

        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPic);
            in = resource.getInputStream();
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
