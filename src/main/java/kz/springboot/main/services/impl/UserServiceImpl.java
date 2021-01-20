package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Roles;
import kz.springboot.main.entities.Users;
import kz.springboot.main.repositories.RolesRepository;
import kz.springboot.main.repositories.UserRepository;
import kz.springboot.main.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Users user = userRepository.findByEmail(s);

        if (user != null) {
            return new User(user.getEmail(), user.getPassword(), user.getRoles());
        }

        throw new UsernameNotFoundException("User not found");

    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users createUser(Users user) {
        Users checkUser = userRepository.findByEmail(user.getEmail());
        if (checkUser == null) {
            Roles role = rolesRepository.findByRole("ROLE_USER");
            if (role != null) {
                List<Roles> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    @Override
    public Users changePassword(Users user, String old_password, String new_password) {
        if(passwordEncoder.matches(old_password, user.getPassword())){
            user.setPassword(passwordEncoder.encode(new_password));
            userRepository.save(user);
            return user;
        }
        return null;
    }

    @Override
    public Users findById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public void save(Users users) {
        userRepository.save(users);
    }

    @Override
    public void delete(Users users) {
        userRepository.delete(users);
    }

    @Override
    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }


}
