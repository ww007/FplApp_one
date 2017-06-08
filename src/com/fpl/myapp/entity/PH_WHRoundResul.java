package com.fpl.myapp.entity;

public class PH_WHRoundResul {
	private String stuCode; // 学号
	private String gradeCode; // 年级代码 可以传null
	private int sex; // 性别 1--男，2--女 可以传null
	private String hitemCode; // 身高项目代码
	private String witemCode; // 体重项目代码
	private int hresult; // 身高成绩
	private int wresult; // 体重成绩
	private int roundNo; // 轮次
	private String testTime; // 测试时间
	private int resultState; // 成绩状态 0正常，-1犯规，-2免跳，-3退出
	private String mac; // 上传设备MAC

	public PH_WHRoundResul() {
		// TODO Auto-generated constructor stub
	}

	public PH_WHRoundResul(String stuCode, String gradeCode, int sex, String hitemCode, String witemCode, int hresult,
			int wresult, int roundNo, String testTime, int resultState, String mac) {
		super();
		this.stuCode = stuCode;
		this.gradeCode = gradeCode;
		this.sex = sex;
		this.hitemCode = hitemCode;
		this.witemCode = witemCode;
		this.hresult = hresult;
		this.wresult = wresult;
		this.roundNo = roundNo;
		this.testTime = testTime;
		this.resultState = resultState;
		this.mac = mac;
	}

	public String getStuCode() {
		return stuCode;
	}

	public void setStuCode(String stuCode) {
		this.stuCode = stuCode;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getHitemCode() {
		return hitemCode;
	}

	public void setHitemCode(String hitemCode) {
		this.hitemCode = hitemCode;
	}

	public String getWitemCode() {
		return witemCode;
	}

	public void setWitemCode(String witemCode) {
		this.witemCode = witemCode;
	}

	public int getHresult() {
		return hresult;
	}

	public void setHresult(int hresult) {
		this.hresult = hresult;
	}

	public int getWresult() {
		return wresult;
	}

	public void setWresult(int wresult) {
		this.wresult = wresult;
	}

	public int getRoundNo() {
		return roundNo;
	}

	public void setRoundNo(int roundNo) {
		this.roundNo = roundNo;
	}

	public String getTestTime() {
		return testTime;
	}

	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}

	public int getResultState() {
		return resultState;
	}

	public void setResultState(int resultState) {
		this.resultState = resultState;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public String toString() {
		return "PH_WHRoundResul [stuCode=" + stuCode + ", gradeCode=" + gradeCode + ", sex=" + sex + ", hitemCode="
				+ hitemCode + ", witemCode=" + witemCode + ", hresult=" + hresult + ", wresult=" + wresult
				+ ", roundNo=" + roundNo + ", testTime=" + testTime + ", resultState=" + resultState + ", mac=" + mac
				+ "]";
	}

}
