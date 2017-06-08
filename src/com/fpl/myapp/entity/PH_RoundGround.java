package com.fpl.myapp.entity;

public class PH_RoundGround {

	private String studentCode; // 学号
	private String itemCode; // 项目代码
	private String result; // 成绩
	private int roundNo; // 轮次
	private String testTime; // 测试时间
	private int resultState; // 成绩状态 0正常，-1犯规，-2免跳，-3退出
	private int isLastResult; // 是否为最终成绩 该参数传：0
	private String mac; // mac地址

	public PH_RoundGround() {
		// TODO Auto-generated constructor stub
	}

	public PH_RoundGround(String studentCode, String itemCode, String result, int roundNo, String testTime,
			int resultState, int isLastResult, String mac) {
		super();
		this.studentCode = studentCode;
		this.itemCode = itemCode;
		this.result = result;
		this.roundNo = roundNo;
		this.testTime = testTime;
		this.resultState = resultState;
		this.isLastResult = isLastResult;
		this.mac = mac;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

	public int getIsLastResult() {
		return isLastResult;
	}

	public void setIsLastResult(int isLastResult) {
		this.isLastResult = isLastResult;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public String toString() {
		return "{studentCode:" + studentCode + ", itemCode:" + itemCode + ", result:" + result + ", roundNo:" + roundNo
				+ ", testTime:" + testTime + ", resultState:" + resultState + ", isLastResult:" + isLastResult
				+ ", mac:" + mac + "}";
	}

}
