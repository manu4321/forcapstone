package main.dao;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import main.bean.user.User;

@Transactional
public interface UserDao extends JpaRepository<User, Long> {

	/**
	 * This method will find an User instance in the database by its email. Note
	 * that this method is not implemented and its working code will be
	 * automagically generated from its signature by Spring Data JPA.
	 */
	// @Query("SELECT u FROM USERS u WHERE email = :email")
	public User findsomething(@Param("email") String email);

	public ArrayList<User> findAll();

	public User findByUserName(@Param("username") String username);

	public User findUser(@Param("username") String username, @Param("password") String password);

}
