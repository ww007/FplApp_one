package com.fpl.myapp.entity;

public class PH_School {
	private String SchoolName; // 学校名称
	private String SchoolYear; // 学年

	public PH_School() {
		// TODO Auto-generated constructor stub
	}

	public PH_School(String schoolName, String schoolYear) {
		super();
		SchoolName = schoolName;
		SchoolYear = schoolYear;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getSchoolYear() {
		return SchoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		SchoolYear = schoolYear;
	}

	@Override
	public String toString() {
		return "PH_School [SchoolName=" + SchoolName + ", SchoolYear=" + SchoolYear + "]";
	}

}
