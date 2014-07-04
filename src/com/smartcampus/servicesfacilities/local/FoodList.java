package com.smartcampus.servicesfacilities.local;

public class FoodList {
	public Food[] foods;
	public FoodOrder foodList;

	public void setFoods(Food[] aFoods) {
		this.foods = aFoods;
	}

	public Food[] getFoods() {
		return this.foods;
	}
}