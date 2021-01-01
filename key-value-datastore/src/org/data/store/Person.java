package org.data.store;

public class Person {

	private String name;
	private String id;
	private int age;
	private int timeToLive;
	private String createdDate;
	
	public Person () {
		
	}
	
    public Person (String name,String id,int age,String createdDate,int timeToLive) {
		this.name = name;
		this.age = age;
		this.id = id;
		this.setTimeToLive(timeToLive);
		this.setCreatedDate(createdDate);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	public boolean hasTimetoLive() {
		if(timeToLive > 0) {
			return true;
		}else {
			return false;
		}
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
