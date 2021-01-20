package kz.springboot.main.services.impl;

import kz.springboot.main.entities.Roles;
import kz.springboot.main.repositories.RolesRepository;
import kz.springboot.main.services.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Roles findById(Long id) {
        return rolesRepository.getOne(id);
    }

    @Override
    public void save(Roles roles) {
        rolesRepository.save(roles);
    }

    @Override
    public void delete(Roles roles) {
        rolesRepository.delete(roles);
    }

    @Override
    public List<Roles> allRoles() {
        return rolesRepository.findAll();
    }
}
