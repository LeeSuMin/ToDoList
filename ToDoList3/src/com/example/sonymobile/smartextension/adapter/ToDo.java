package com.example.sonymobile.smartextension.adapter;

// 할 일을 저장할 객체를 정의하는 클래스
public class ToDo {
	private String id;
	private String todo;
	private String calendar;
	private boolean check;
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	public boolean getCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
}