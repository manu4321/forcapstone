package main.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import main.bean.user.User;
import main.dao.UserDao;
import main.security.SecurityConfiguration;

@RestController
@RequestMapping("/spring")
public class MainController {
	@RequestMapping("/create")
	@ResponseBody
	public String create(String email, String name) {
		String userId = "";
		try {
			User user = new User(email, name, userId, true);
			userDao.save(user);
			userId = String.valueOf(user.getId());
		} catch (Exception ex) {
			return "Error creating the user: " + ex.toString();
		}
		return "User succesfully created with id = " + userId;
	}
	
	@RequestMapping("/register")
	@ResponseBody
	public String register(String username, String password, String email) {
		String encryptedPassword = new BCryptPasswordEncoder().encode(password);
		if(encryptedPassword == null){
			encryptedPassword = password;
		}
		User user = new User(username, encryptedPassword, email,  true);
		main.bean.user.UserRole userRole = new main.bean.user.UserRole(user, "ROLE_USER");
		user.getUserRole().add(userRole);
		userDao.save(user);
		UserDetails userDetails = new main.bean.user.MyUserDetailsService(userDao).loadUserByUsername(username);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
				password, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return "it was registered!";

	}

	/**
	 * GET /delete --> Delete the user having the passed id.
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public String delete(long id) {
		try {
			User user = new User(id);
			userDao.delete(user);
		} catch (Exception ex) {
			return "Error deleting the user:" + ex.toString();
		}
		return "User succesfully deleted!";
	}

	/**
	 * GET /get-by-email --> Return the id for the user having the passed email.
	 */
	@RequestMapping("/get-by-email")
	@ResponseBody
	public String getByEmail(String email) {
		String userId = "";
		try {
			User user = userDao.findsomething(email);
			userId = String.valueOf(user.getId());
		} catch (Exception ex) {
			return "User not found" + ex.toString();
		}
		return "The user id is: " + userId;
	}

	/**
	 * GET /get-by-email --> Return the id for the user having the passed email.
	 */
	@RequestMapping("/findAll")
	@ResponseBody
	public ArrayList<User> getAll() {
		ArrayList<User> users;
		try {
			users = userDao.findAll();
		} catch (Exception ex) {
			return null;
		}
		return users;
	}

	/**
	 * GET /update --> Update the email and the name for the user in the
	 * database having the passed id.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ResponseBody
	public String updateUser(long id, String email, String name) {
		try {
			User user = userDao.findOne(id);
			user.setEmail(email);
			user.setUsername(name);
			userDao.save(user);
		} catch (Exception ex) {
			return "Error updating the user: " + ex.toString();
		}
		return "User succesfully updated!";
	}
	
	/**
	 * GET /update --> Update the email and the name for the user in the
	 * database having the passed id.
	 */
    @CrossOrigin
	@RequestMapping(value = "/updateSave", method = RequestMethod.PUT)
	@ResponseBody
	public String updateUser(@RequestBody User user) {
		try {
			userDao.save(user);
		} catch (Exception ex) {
			return "Error updating the user: " + ex.toString();
		}
		return "User succesfully updated!";
	}
	
	@RequestMapping(value = "/practicing", method = RequestMethod.GET)
	@ResponseBody
	public String practice() {

		return "practice.html";
	}
	
/*	  @RequestMapping(value="/login" ,method = RequestMethod.POST)
	  public String login(@RequestParam("u") String username,
	    @RequestParam("p") String password,
	    HttpServletRequest req) throws Exception {
		logger.info("LOGIN");
		logger.info("User: " + username + " password " + password + " encryped " );
 
		User user = userDao.findByUserName(username);
		logger.info("user is " + user);

	     if(user != null){
	 		logger.info("user is " + user.toString());
	 		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	 		if(encoder.matches(password, user.getPassword())){
		 		
		 	    // Force session creation so it's available to Spring Security post processor filter
		 	    req.getSession(true);
		 	    // Authenticate using AuthenticationManager configured on SecurityContext
		 	    AuthenticationManager authMgr = securityConfig.authenticationManagerBean();
		 	    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, user.getPassword());
		 	    authReq.setDetails(authenticationDetailsSource.buildDetails(req));
		 	    Authentication authResp = authMgr.authenticate(authReq);
		 	     
		 	    // If successful add the authentication response to context so the post processor filter
		 	    // can save it to session
		 	    SecurityContextHolder.getContext().setAuthentication(authResp);
		 	     
		 	    return "Authentication successful";
	 		}

	     }
	     
    	 return "failed authentication";

	  }*/
	  
	// Private fields

	@Autowired
	private SecurityConfiguration securityConfig;
	private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

	@Autowired
	private UserDao userDao;
	final static Logger logger = Logger.getLogger(MainController.class);

}
