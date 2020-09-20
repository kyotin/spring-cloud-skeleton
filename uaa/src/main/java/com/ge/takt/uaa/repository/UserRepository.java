package com.ge.takt.uaa.repository;


import com.ge.takt.uaa.model.Role;
import com.ge.takt.uaa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	User findUserById(Long id);

	User findUserByUserName(String userName);

	@Query(value = "SELECT u FROM User u "
			+ "WHERE (:factoryId is null or (u.factoryId=:factoryId)) "
			+ "AND u.active=true")
	List<User> findByActiveTrue(@Param("factoryId") Long factoryId);

	@Query(value="SELECT u FROM User u "
			+ "WHERE lower(u.userName) like lower(:userName) "
			+ "AND (:factoryId is null or (u.factoryId=:factoryId)) "
			+ "AND u.active=true")
	Page<User> filterUser(@Param("factoryId") Long factoryId, @Param("userName") String userName, Pageable pageable);

	@Query(value = "SELECT u FROM User u WHERE u.role.id =:roleId")
	User findUserByRoleId(@Param("roleId") Long roleId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE User u "
					+ "SET u.role=:role, u.email=:mail, u.factoryId=:factoryId, "
					+ "u.firstName=:firstName, u.lastName=:lastName, u.sso=:sso "
					+ "WHERE u.userName=:userName")
	void updateUserWithOutPwd(@Param("role") Role role, @Param("mail") String mail, @Param("factoryId")Long factoryId, @Param("firstName") String firstName,
							  @Param("lastName") String lastName, @Param("sso") String sso, @Param("userName") String userName);
}