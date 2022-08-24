package com.okayjava.html.model;

import java.util.ArrayList;
import java.util.List;

public class User {

	private List<String> ingredient;
	private static List<String> copyValues = new ArrayList<String>();

	public List<String> getIngredients() {
		return ingredient;
	}

	public void setIngredients(List<String> ingredient) {
		this.ingredient = ingredient;
	}

	public static List<String> getCopyValues() {
		return copyValues;
	}

	public static void setCopyValues(List<String> anotherValues) {
		copyValues = anotherValues;
	}

	@Override
	public String toString() {
		return "User [ingredient=" + ingredient + "]";
	}
}
