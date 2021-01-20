package kz.springboot.main.services;

import kz.springboot.main.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService extends UserDetailsService {

    Users getUserByEmail(String email);
    Users createUser(Users user);
    Users changePassword(Users user, String old_password, String new_password);

    Users findById(Long id);
    void save(Users users);
    void delete(Users users);
    List<Users> findAllUsers();
}