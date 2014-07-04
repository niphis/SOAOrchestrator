package com.smartcampus.servicesfacilities.local;

public class FoodOrder {
	public long timestamp;
	public FoodList foodList;

	public void setTimestamp(long aTimestamp) {
		this.timestamp = aTimestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setFoodList(FoodList aFoodList) {
		this.foodList = aFoodList;
	}

	public FoodList getFoodList() {
		return this.foodList;
	}
}