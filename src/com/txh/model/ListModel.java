package com.txh.model;

public class ListModel {
	
	private String id;
	private String name;
	private String content;
	private String phone;
	private int total;
	private boolean isMy;
	
	public void setTotal(int total){
		this.total = total;
	}
	
	public int getTotal(){
		return total;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return name + ": " + content;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPhone(String phone){
		this.phone = phone;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void isMy(boolean isMy){
		this.isMy = isMy;
	}
	
	public String getId(){
		return id;
	}
	
	public boolean isMy(){
		return isMy;
	}
	
	public ListModel(){
		
	}
}
