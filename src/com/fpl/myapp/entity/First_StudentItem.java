package com.fpl.myapp.entity;

import java.util.List;

public class First_StudentItem {

	private int totalCount; // 总记录条数
	private int totalPage; // 总页码
	private int pageSize; // 页面大小
	private int pageNo; // 当前第几页
	private List<PH_StudentItem> result; // 返回学生项目结果
	private int startRow; // 开始行
	private int endRow; // 结束行

	public First_StudentItem() {
		// TODO Auto-generated constructor stub
	}

	public First_StudentItem(int totalCount, int totalPage, int pageSize, int pageNo, List<PH_StudentItem> result,
			int startRow, int endRow) {
		super();
		this.totalCount = totalCount;
		this.totalPage = totalPage;
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.result = result;
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<PH_StudentItem> getResult() {
		return result;
	}

	public void setResult(List<PH_StudentItem> result) {
		this.result = result;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	@Override
	public String toString() {
		return "First_StudentItem [totalCount=" + totalCount + ", totalPage=" + totalPage + ", pageSize=" + pageSize
				+ ", pageNo=" + pageNo + ", result=" + result + ", startRow=" + startRow + ", endRow=" + endRow + "]";
	}

}
