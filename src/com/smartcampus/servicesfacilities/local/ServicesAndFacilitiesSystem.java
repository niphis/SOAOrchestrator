package com.smartcampus.servicesfacilities.local;

public interface ServicesAndFacilitiesSystem {

	public FoodList getFoodStocks();

	public boolean placeFoodOrder(FoodOrder aStockOrder);

	public boolean setCleaningFrequency(int aFrequency);
}