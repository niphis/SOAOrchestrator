package com.smartcampus.servicesfacilities.local;

import java.util.Date;

public class FoodOrder {
	private Date timestamp;
	private FoodList foodList;

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date aTimestamp) {
		this.timestamp = aTimestamp;
	}

	public FoodList getFoodList() {
		return this.foodList;
	}

	public void setFoodList(FoodList aFoodList) {
		this.foodList = aFoodList;
	}
}
