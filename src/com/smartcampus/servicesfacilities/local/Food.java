package com.smartcampus.servicesfacilities.local;

public class Food {
	public String label;
	public int quantity;
	public String unit;
	public FoodList foods;

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String aLabel) {
		this.label = aLabel;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int aQuantity) {
		this.quantity = aQuantity;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String aUnit) {
		this.unit = aUnit;
	}
}