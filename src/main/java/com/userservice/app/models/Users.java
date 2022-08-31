package com.userservice.app.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//@Data
@Entity
//@AllArgsConstructor
//@NoArgsConstructor
@Table(name = "AUTH_USER", uniqueConstraints = { @UniqueConstraint(columnNames = "USERNAME"),
		@UniqueConstraint(columnNames = "EMAIL") })
public class Users implements UserDetails,Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="AUTH_USER_ID")
	private Long id;

//	@NotBlank
	@Size(max = 20)
	@Column(name="USERNAME")
	private String username;

//	@NotBlank
	@Size(max = 50)
	@Email
	@Column(name="EMAIL")
	private String email;

//	@NotBlank
	@Size(max = 120)
	@Column(name="PASSWORD")
	private String password;

	@ManyToMany(cascade = CascadeType.ALL /* fetch = FetchType.LAZY */)
	@JoinTable(name = "AUTH_USER_ROLES", joinColumns = @JoinColumn(name = "AUTH_USER_ID"), 
	           inverseJoinColumns = @JoinColumn(name = "AUTH_ROLE_ID"))
	private Set<Role> roles = new HashSet<>();

//	@NotBlank
	@Size(max = 10)
	@Column(name="GENDER")
	private String gender;

//	@NotBlank
	@Size(max = 120)
	@Column(name="FIRST_NAME")
	private String firstName;

	@Size(max = 120)
	@Column(name="MIDDLE_NAME")
	private String middleName;

//	@NotBlank
	@Column(name="LAST_NAME")
	@Size(max = 120)
	private String lastName;

//	@NotBlank
	@Size(max = 120)
	@Column(name="CONTACT_NUMBER")
	private String contactNumber;

//	@NotBlank
	@Size(max = 120)
	@Column(name="DATE_OF_BIRTH")
	private String dateOfBirth;
	
	@Size(max = 120)
	@Column(name="VERIFICATION_CODE", length = 64)
	private String verificationCode;
	
	@Column(name="IS_EMAIL_VERIFIED")
	private boolean isEmailVerified;

//	@NotBlank
//	@Size(max = 120)
//	@Column(name="CURRENT_LOCATION")
//	private String currentLocation;
//
////	@Column(nullable = false)
////	private MultipartFile file;
//

//	@NotBlank
	@Column(name="CAPTCHA")
	private String captcha;

	@Column(name="STATUS")
	private String status;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private LocalDateTime modifiedDate;

	public Users() {
	}

	public Users(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}



}
