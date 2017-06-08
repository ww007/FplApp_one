package com.fpl.myapp.entity;

public class ICInfo {

	private String projectTitle;
	private String projectValue;

	public ICInfo() {
	}

	public ICInfo(String projectTitle, String projectValue) {
		super();
		this.projectTitle = projectTitle;
		this.projectValue = projectValue;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getProjectValue() {
		return projectValue;
	}

	public void setProjectValue(String projectValue) {
		this.projectValue = projectValue;
	}

	@Override
	public String toString() {
		return "ICInfo [projectTitle=" + projectTitle + ", projectValue=" + projectValue + "]";
	}

}
