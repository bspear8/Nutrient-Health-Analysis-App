package edu.gatech.ihi.nhaa.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;

@Entity
@Table(name="User")
@Access(AccessType.FIELD)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "userId", updatable = false, nullable = false)
	private long userId;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "accountLocked", nullable = false)
	private boolean accountLocked;

	@Column(name = "accountDisabled", nullable = false)
	private boolean accountDisabled;

	@Column(name = "passwordExpirationDate")
	private ZonedDateTime passwordExpirationDate;

	@Column(name = "firstName", nullable = false)
	private String firstName;

	@Column(name = "lastName", nullable = false)
	private String lastName;
	
	@Column(name = "patientId", nullable = false, unique = true)
	private String patientId;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
	private Collection<Role> roles;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return passwordExpirationDate == null || passwordExpirationDate.isAfter(ZonedDateTime.now());
	}

	@Override
	public boolean isEnabled() {
		return !accountDisabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String[] roles = this.roles.stream().map(Role::getName).toArray(String[]::new);
		return AuthorityUtils.createAuthorityList(roles);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isAccountDisabled() {
		return accountDisabled;
	}

	public void setAccountDisabled(boolean accountDisabled) {
		this.accountDisabled = accountDisabled;
	}

	public ZonedDateTime getPasswordExpirationDate() {
		return passwordExpirationDate;
	}

	public void setPasswordExpirationDate(ZonedDateTime passwordExpirationDate) {
		this.passwordExpirationDate = passwordExpirationDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (username == null ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final User user = (User) obj;
		if (!username.equals(user.username)) {
			return false;
		}
		return true;
	}
}
