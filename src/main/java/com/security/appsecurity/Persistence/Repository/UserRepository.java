package com.security.appsecurity.Persistence.Repository;

import java.util.Optional;

// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.security.appsecurity.Persistence.Entity.UseEntity;

@Repository
public interface UserRepository extends CrudRepository<UseEntity,Long>{
    //Busca usando QueryMethod "Query" es decir busca por el username
    //porque lo busca a partir del nombre del metodo findUserEntityByUsername
    Optional<UseEntity> findUserEntityByUsername(String username)  throws UsernameNotFoundException;

    // Crea la Sentencia con la notacion Query
    // @Query("SELECT u FROM UseEntity u WHERE u.username = ?")
    // Optional<UseEntity> findUser();

}
