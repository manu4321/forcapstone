package main.bean.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@NamedQueries({ @NamedQuery(name = "User.findsomething", query = "SELECT u FROM User u WHERE u.email = :email"),
		@NamedQuery(name = "User.findAll", query = "FROM User"),
		@NamedQuery(name = "User.findByUserName", query = "FROM User WHERE username = :username"),
		@NamedQuery(name = "User.findUser", query = "FROM User WHERE username = :username AND password = :password")
})
@Table(name = "users")
public class User {


	@Id
	@Column(name = "username", nullable = false, unique = true)
	private String username;
	
	@Column(name = "email", nullable = false)
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "password", nullable = false, columnDefinition = "TEXT")
	private String password;

	@JsonIgnore
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user" , cascade = CascadeType.ALL)
	private Set<UserRole> userRole = new HashSet<UserRole>(0);

	// Public methods

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

	public User(String username, String password, String email, boolean enabled) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	public boolean isEnabled() {
		return enabled;
	}
	@JsonIgnore
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@JsonIgnore
	public Set<UserRole> getUserRole() {
		return userRole;
	}
	@JsonIgnore
	public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}

}
