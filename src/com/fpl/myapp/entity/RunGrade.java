package com.fpl.myapp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RunGrade implements Serializable {

	private int xuhao;
	private String time;
	private String name;
	private String stuCode;
	private int sex;

	public RunGrade() {
	}

	public RunGrade(int xuhao, String time, String name, String stuCode, int sex) {
		super();
		this.xuhao = xuhao;
		this.time = time;
		this.name = name;
		this.stuCode = stuCode;
		this.sex = sex;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getStuCode() {
		return stuCode;
	}

	public void setStuCode(String stuCode) {
		this.stuCode = stuCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getXuhao() {
		return xuhao;
	}

	public void setXuhao(int xuhao) {
		this.xuhao = xuhao;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "RunGrade [xuhao=" + xuhao + ", time=" + time + ", name=" + name + ", stuCode=" + stuCode + ", sex="
				+ sex + "]";
	}

}
