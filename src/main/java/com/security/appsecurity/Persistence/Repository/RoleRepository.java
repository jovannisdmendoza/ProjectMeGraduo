package com.security.appsecurity.Persistence.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.security.appsecurity.Persistence.Entity.RoleEntity;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Long>{
    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleNames) throws IllegalArgumentException;
}
