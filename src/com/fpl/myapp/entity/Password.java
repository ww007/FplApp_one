package com.fpl.myapp.entity;

public class Password {

	private String password;
	public Password() {
		// TODO Auto-generated constructor stub
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Password(String password) {
		super();
		this.password = password;
	}

	@Override
	public String toString() {
		return "Password [password=" + password + "]";
	}
}
