package com.smartcampus.servicesfacilities;

public interface ServicesAndFacilitiesSystem {

	public FoodList getFoodStocks();

	public boolean placeFoodOrder(FoodOrder aStockOrder);

	public boolean setCleaningFrequency(int aFrequency);
}