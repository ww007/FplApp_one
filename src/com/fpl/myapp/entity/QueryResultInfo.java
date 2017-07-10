package com.fpl.myapp.entity;

public class QueryResultInfo {
	private int ResultState;
	private int StudentItemID;
	private String LastTestTime;
	private String ItemCode;
	private int LastResult;
	private String StudentCode;
	private String stuYear; // 学年 例如：2014-2015学年
	private String isCurYear; // 是否当前学年 0为否，1为是

	public QueryResultInfo() {
		// TODO Auto-generated constructor stub
	}

	public QueryResultInfo(int resultState, int studentItemID, String lastTestTime, String itemCode, int lastResult,
			String studentCode, String stuYear, String isCurYear) {
		super();
		ResultState = resultState;
		StudentItemID = studentItemID;
		LastTestTime = lastTestTime;
		ItemCode = itemCode;
		LastResult = lastResult;
		StudentCode = studentCode;
		this.stuYear = stuYear;
		this.isCurYear = isCurYear;
	}

	public int getResultState() {
		return ResultState;
	}

	public void setResultState(int resultState) {
		ResultState = resultState;
	}

	public int getStudentItemID() {
		return StudentItemID;
	}

	public void setStudentItemID(int studentItemID) {
		StudentItemID = studentItemID;
	}

	public String getLastTestTime() {
		return LastTestTime;
	}

	public void setLastTestTime(String lastTestTime) {
		LastTestTime = lastTestTime;
	}

	public String getItemCode() {
		return ItemCode;
	}

	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}

	public int getLastResult() {
		return LastResult;
	}

	public void setLastResult(int lastResult) {
		LastResult = lastResult;
	}

	public String getStudentCode() {
		return StudentCode;
	}

	public void setStudentCode(String studentCode) {
		StudentCode = studentCode;
	}

	public String getStuYear() {
		return stuYear;
	}

	public void setStuYear(String stuYear) {
		this.stuYear = stuYear;
	}

	public String getIsCurYear() {
		return isCurYear;
	}

	public void setIsCurYear(String isCurYear) {
		this.isCurYear = isCurYear;
	}

	@Override
	public String toString() {
		return "QueryResultInfo [ResultState=" + ResultState + ", StudentItemID=" + StudentItemID + ", LastTestTime="
				+ LastTestTime + ", ItemCode=" + ItemCode + ", LastResult=" + LastResult + ", StudentCode="
				+ StudentCode + ", stuYear=" + stuYear + ", isCurYear=" + isCurYear + "]";
	}

}
