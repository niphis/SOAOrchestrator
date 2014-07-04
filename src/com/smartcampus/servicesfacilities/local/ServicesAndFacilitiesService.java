package com.smartcampus.servicesfacilities.local;

public class ServicesAndFacilitiesService implements ServicesAndFacilitiesSystem {

	public FoodList getFoodStocks() {
		
		if (failed())
			return null;
		
		FoodList fl = new FoodList();
		Food[] foods = new Food[2];
		
		foods[0] = new Food();
		foods[0].setLabel("Drinks");
		foods[0].setQuantity((int) (Math.random() * 300));
		foods[0].setUnit("liters");
		
		foods[1] = new Food();
		foods[1].setLabel("Meals");
		foods[1].setQuantity((int) (Math.random() * 200));
		foods[1].setUnit("pieces");
		
		fl.setFoods(foods);
		return fl;
	}

	public boolean placeFoodOrder(FoodOrder aStockOrder) {
		if (failed())
			return false;
		return true;
	}

	public boolean setCleaningFrequency(int aFrequency) {
		if (failed())
			return false;
		return true;
	}
	
	private boolean failed() { return Math.random() < 0.01; }
}
