package com.fpl.myapp.entity;

public class PH_Student {
	private String StudentCode; // 学号
	private String StudentName; // 姓名
	private int Sex; // 性别
	private String ClassCode; // 班级编号 --班级id
	private String GradeCode; // 年级代码 --年级id
	private String IDCardNo; // 身份证号
	private String ICCardNo; // IC卡号
	private String DownloadTime; // 下载时间

	public PH_Student() {
		// TODO Auto-generated constructor stub
	}

	public PH_Student(String studentCode, String studentName, int sex, String classCode, String gradeCode,
			String iDCardNo, String iCCardNo, String downloadTime) {
		super();
		StudentCode = studentCode;
		StudentName = studentName;
		Sex = sex;
		ClassCode = classCode;
		GradeCode = gradeCode;
		IDCardNo = iDCardNo;
		ICCardNo = iCCardNo;
		DownloadTime = downloadTime;
	}

	public String getStudentCode() {
		return StudentCode;
	}

	public void setStudentCode(String studentCode) {
		StudentCode = studentCode;
	}

	public String getStudentName() {
		return StudentName;
	}

	public void setStudentName(String studentName) {
		StudentName = studentName;
	}

	public int getSex() {
		return Sex;
	}

	public void setSex(int sex) {
		Sex = sex;
	}

	public String getClassCode() {
		return ClassCode;
	}

	public void setClassCode(String classCode) {
		ClassCode = classCode;
	}

	public String getGradeCode() {
		return GradeCode;
	}

	public void setGradeCode(String gradeCode) {
		GradeCode = gradeCode;
	}

	public String getIDCardNo() {
		return IDCardNo;
	}

	public void setIDCardNo(String iDCardNo) {
		IDCardNo = iDCardNo;
	}

	public String getICCardNo() {
		return ICCardNo;
	}

	public void setICCardNo(String iCCardNo) {
		ICCardNo = iCCardNo;
	}

	public String getDownloadTime() {
		return DownloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		DownloadTime = downloadTime;
	}

	@Override
	public String toString() {
		return "PH_Student [StudentCode=" + StudentCode + ", StudentName=" + StudentName + ", Sex=" + Sex
				+ ", ClassCode=" + ClassCode + ", GradeCode=" + GradeCode + ", IDCardNo=" + IDCardNo + ", ICCardNo="
				+ ICCardNo + ", DownloadTime=" + DownloadTime + "]";
	}

}
