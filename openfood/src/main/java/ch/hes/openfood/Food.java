package ch.hes.openfood;

import java.util.Map;

public class Food {

	private String id;
	private String name;
	private String ingredients_translations;
	private int quantity;
	private String unit;
	private int portion_quantity;
	private String portion_unit;
	private Map<String, Nutrient> nutrients;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIngredients_translations() {
		return ingredients_translations;
	}

	public void setIngredients_translations(String ingredients_translations) {
		this.ingredients_translations = ingredients_translations;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getPortion_quantity() {
		return portion_quantity;
	}

	public void setPortion_quantity(int portion_quantity) {
		this.portion_quantity = portion_quantity;
	}

	public String getPortion_unit() {
		return portion_unit;
	}

	public void setPortion_unit(String portion_unit) {
		this.portion_unit = portion_unit;
	}

	public Map<String, Nutrient> getNutrients() {
		return nutrients;
	}

	public void setNutrients(Map<String, Nutrient> nutrients) {
		this.nutrients = nutrients;
	}
}
