package com.security.appsecurity;

import java.util.List;
import java.util.Set;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.security.appsecurity.Persistence.Entity.PermissionEntity;
import com.security.appsecurity.Persistence.Entity.RoleEntity;
import com.security.appsecurity.Persistence.Entity.RoleEnum;
import com.security.appsecurity.Persistence.Entity.UseEntity;
import com.security.appsecurity.Persistence.Repository.UserRepository;
@EnableScheduling
@SpringBootApplication
public class AppsecurityApplication {

	/**
	 * @param args
	 */ 
	public static void main(String[] args) {SpringApplication.run(AppsecurityApplication.class, args);}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return arg ->{

			// Creacion de permisos
			PermissionEntity createPermission = PermissionEntity.builder()
			.name("CREATE")
			.build();

			PermissionEntity readPermission = PermissionEntity.builder()
			.name("READ")
			.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
			.name("UPDATE")
			.build();

			PermissionEntity deletedPermission = PermissionEntity.builder()
			.name("DELETED")
			.build();

			PermissionEntity refactorPermission = PermissionEntity.builder()
			.name("SEARCH")
			.build();


			//CREATE ROLES*
			RoleEntity roleAdmin = RoleEntity.builder()
			.roleEnum(RoleEnum.STUDENT)
			.permissionsList(Set.of(createPermission,readPermission,updatePermission,deletedPermission))
			.build();
			
			RoleEntity roleUser = RoleEntity.builder()
			.roleEnum(RoleEnum.USER)
			.permissionsList(Set.of(createPermission,readPermission))
			.build();

			RoleEntity roleInvited = RoleEntity.builder()
			.roleEnum(RoleEnum.USER)
			.permissionsList(Set.of(readPermission))
			.build();

			RoleEntity roleDeveloper = RoleEntity.builder()
			.roleEnum(RoleEnum.TEACHER)
			.permissionsList(Set.of(createPermission,readPermission,updatePermission,deletedPermission,refactorPermission))
			.build();

			//CREATE USERS

			UseEntity userSantiago = UseEntity.builder()
			.username("santiago")
			.password("$2a$10$QwmE1jgjWBLpZjdlvzyxVuONcWzPqLIPnu16isUWTvLc8DyXfhB9S")
			.isEnable(true)
			.accountNoEspired(true)
			.accountNoLocked(true)
			.credentialAccount(true)
			.roles(Set.of(roleAdmin))
			.build();

			UseEntity userDaniel = UseEntity.builder()
			.username("daniel")
			.password("$2a$10$QwmE1jgjWBLpZjdlvzyxVuONcWzPqLIPnu16isUWTvLc8DyXfhB9S")
			.isEnable(true)
			.accountNoEspired(true)
			.accountNoLocked(true)
			.credentialAccount(true)
			.roles(Set.of(roleUser))
			.build();

			UseEntity userAndrea = UseEntity.builder()
			.username("Andrea")
			.password("$2a$10$QwmE1jgjWBLpZjdlvzyxVuONcWzPqLIPnu16isUWTvLc8DyXfhB9S")
			.isEnable(true)
			.accountNoEspired(true)
			.accountNoLocked(true)
			.credentialAccount(true)
			.roles(Set.of(roleInvited))
			.build();

			UseEntity userAngie = UseEntity.builder()
			.username("jovannisdmendoza@unicesar.edu.co")
			.password("$2a$10$QwmE1jgjWBLpZjdlvzyxVuONcWzPqLIPnu16isUWTvLc8DyXfhB9S")
			.isEnable(false)
			.accountNoEspired(true)
			.accountNoLocked(true)
			.credentialAccount(true)
			.roles(Set.of(roleDeveloper))
			.build();

			userRepository.saveAll(List.of(userAndrea,userAngie,userDaniel,userSantiago));

			// System.out.println("Contraseña");
			// System.out.println(new BCryptPasswordEncoder().encode("1234"));
		};
	}
}
