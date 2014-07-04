package com.smartcampus.servicesfacilities.local;

public class ServicesAndFacilitiesService implements ServicesAndFacilitiesSystem {

	public FoodList getFoodStocks() {
		FoodList fl = new FoodList();
		Food[] foods = new Food[2];
		
		foods[0] = new Food();
		foods[0].setLabel("Water");
		foods[0].setQuantity(200);
		foods[0].setUnit("liters");
		
		foods[1] = new Food();
		foods[1].setLabel("Sandwich");
		foods[1].setQuantity(250);
		foods[1].setUnit("pieces");
		
		fl.setFoods(foods);
		return fl;
	}

	public boolean placeFoodOrder(FoodOrder aStockOrder) {
		return true;
	}

	public boolean setCleaningFrequency(int aFrequency) {
		return true;
	}
}
