package kz.springboot.main.services;

import kz.springboot.main.entities.Roles;

import java.util.List;

public interface RolesService {

    Roles findById(Long id);
    void save(Roles roles);
    void delete(Roles roles);
    List<Roles> allRoles();

}
