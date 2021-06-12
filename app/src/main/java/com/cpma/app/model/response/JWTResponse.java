package com.cpma.app.model.response;

import java.util.List;

public class JWTResponse {

	private String accessToken;

	private String tokenType;

	private Long id;

	private String name;

	private String surname;

	private String phone;

	private String username;

	private String email;

	private List<String> roles;

	public JWTResponse(String accessToken, String tokenType, Long id, String name, String surname, String username, String email, String phone, List<String> roles) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.roles = roles;
	}

	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() { return tokenType; }
	public void setTokenType(String tokenType) { this.tokenType = tokenType; }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public void setUsername(String username) {
		this.username = username;
	}
	public void setRoles(List<String> roles) { this.roles = roles; }
	public List<String> getRoles() {
		return roles;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
