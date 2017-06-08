package com.fpl.myapp.entity;

public class QueryResultInfo {
	private int ResultState;
	private int StudentItemID;
	private String LastTestTime;
	private String ItemCode;
	private int LastResult;
	private String StudentCode;

	public QueryResultInfo() {
		// TODO Auto-generated constructor stub
	}

	public QueryResultInfo(int resultState, int studentItemID, String lastTestTime, String itemCode, int lastResult,
			String studentCode) {
		super();
		ResultState = resultState;
		StudentItemID = studentItemID;
		LastTestTime = lastTestTime;
		ItemCode = itemCode;
		LastResult = lastResult;
		StudentCode = studentCode;
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

	@Override
	public String toString() {
		return "QueryResultInfo [ResultState=" + ResultState + ", StudentItemID=" + StudentItemID + ", LastTestTime="
				+ LastTestTime + ", ItemCode=" + ItemCode + ", LastResult=" + LastResult + ", StudentCode="
				+ StudentCode + "]";
	}

}
