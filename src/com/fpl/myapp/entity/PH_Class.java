package com.fpl.myapp.entity;

public class PH_Class {
	private String GradeCode; // 年级代码 --年级id
	private String ClassCode; // 班级编号
	private String ClassName; // 班级名称

	public PH_Class() {
		// TODO Auto-generated constructor stub
	}

	public PH_Class(String gradeCode, String classCode, String className) {
		super();
		GradeCode = gradeCode;
		ClassCode = classCode;
		ClassName = className;
	}

	public String getGradeCode() {
		return GradeCode;
	}

	public void setGradeCode(String gradeCode) {
		GradeCode = gradeCode;
	}

	public String getClassCode() {
		return ClassCode;
	}

	public void setClassCode(String classCode) {
		ClassCode = classCode;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	@Override
	public String toString() {
		return "PH_Class [GradeCode=" + GradeCode + ", ClassCode=" + ClassCode + ", ClassName=" + ClassName + "]";
	}

}
