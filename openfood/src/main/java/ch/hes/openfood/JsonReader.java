package ch.hes.openfood;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

	// JSON DATA
	private static String data;

	// JSON Keys
	private static final String KEY_DATA = "data";
	private static final String KEY_NAME_TRANSLATIONS = "name_translations";
	private static final String KEY_INGREDIENTS_TRANSLATIONS = "ingredients_translations";
	private static final String KEY_QUANTITY = "quantity";
	private static final String KEY_UNIT = "unit";
	private static final String KEY_PORTION_QUANTITY = "portion_quantity";
	private static final String KEY_PORTION_UNIT = "portion_unit";
	private static final String KEY_NUTRIENTS = "nutrients";
	private static final String KEY_PER_HUNDRED = "per_hundred";
	private static final String KEY_PER_PORTION = "per_portion";
	private static final String KEY_PER_DAY = "per_day";
	private static final String KEY_FR = "fr";
	private static final String KEY_ID = "id";

	// Exclude
	private static final String EXCLUDE = "2Cbarcode%2Cdisplay_name_translations%2Corigin_translations%2Cstatus%2Calcohol_by_volume%2Cimages%2Ccategories%2Cthumb%2Cmedium%2Clarge%2Cxlarge%2Ccategories%2Ccreated_at%2Cupdated_at";

	// JSON Values
	private static List<Food> listFood = new ArrayList<Food>();
	private static Food food;

	// JSON OBJECT
	private static JSONObject wholeObject;
	private static JSONObject eachObject;
	private static JSONObject ingredientTranslationObject;
	private static JSONObject nameTranslationObject;
	private static JSONObject nutrientObject;
	private static JSONObject elementNutrientObject;

	// JSON ARRAY
	private static JSONArray dataArray;

	// ITERATOR
	private static Iterator<String> keys;

	// Client
	private static Client client;

	// Client Config
	private static ClientConfig config;

	// WebTarget
	private static WebTarget target;

	public static void main(String[] args) throws JSONException, JsonProcessingException {

		data = getAllProductsFromOpenFood(getOpenFoodURI());

		getAllFrenchInformation();

		postData(getApiURI());

	}

	public static void postData(URI uri) throws JsonProcessingException {
		initializeConnection(uri);

		for (Food food : listFood) {
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(food);

			// POST method
			Invocation.Builder invocationBuilder = target.path("food").request();
			invocationBuilder.post(Entity.entity(jsonInString, MediaType.APPLICATION_JSON));
		}

	}

	private static void initializeConnection(URI uri) {
		config = new ClientConfig();

		client = ClientBuilder.newClient(config);

		target = client.target(uri);
	}


	private static String getAllProductsFromOpenFood(URI uri) {

		initializeConnection(uri);

		String response = target.path("api").path("v3").path("products").queryParam("excludes", EXCLUDE)
				.queryParam("page%5Bsize%5D", "200").request().accept(MediaType.TEXT_PLAIN)
				.header("Accept", "application/json")
				.header("Authorization", "Token token=14d42ef195b015e67f164213c1758918").get(String.class);

		System.out.println(response);

		return response;
	}

	private static URI getOpenFoodURI() {
		return UriBuilder.fromUri("https://www.openfood.ch/").build();
	}
	
	private static URI getApiURI() {
		return UriBuilder.fromUri("http://localhost:8080/").build();
	}

	private static void getAllFrenchInformation() throws JSONException {
		
		wholeObject = new JSONObject(data);

		dataArray = wholeObject.getJSONArray(KEY_DATA);

		for (int i = 0; i < dataArray.length(); i++) {

			eachObject = dataArray.getJSONObject(i);

			// food creation
			food = new Food();
			System.out.println(eachObject);

			int id = eachObject.getInt(KEY_ID);
			food.setId(String.valueOf(id));

			setNameTranslation();

			setIngredientTranslation();

			food.setUnit(eachObject.getString(KEY_UNIT));
			food.setPortion_unit(eachObject.getString(KEY_PORTION_UNIT));
			food.setQuantity(eachObject.getInt(KEY_QUANTITY));
			food.setPortion_quantity(eachObject.getInt(KEY_PORTION_QUANTITY));

			// add nutrients to food
			food.setNutrients(getNutrient());

			listFood.add(food);

			System.out.println(food);

		}
	}

	private static Map<String, Nutrient> getNutrient() throws JSONException {
		// nutrient creation

		// element creation
		nutrientObject = eachObject.getJSONObject(KEY_NUTRIENTS);

		keys = nutrientObject.keys();

		Map<String, Nutrient> res = new HashMap<String, Nutrient>();

		while (keys.hasNext()) {

			String currentKey = keys.next().toString();
			elementNutrientObject = nutrientObject.getJSONObject(currentKey);
			Nutrient element = new Nutrient();

			if (elementNutrientObject.get(KEY_PER_HUNDRED).equals(null)) {
				element.setPer_hundred(0.0);
			} else {
				element.setPer_hundred(elementNutrientObject.getDouble(KEY_PER_HUNDRED));
			}

			element.setUnit(elementNutrientObject.getString(KEY_UNIT));

			if (elementNutrientObject.get(KEY_PER_PORTION).equals(null)) {
				element.setPer_portion(0.0);
			} else {
				element.setPer_portion(elementNutrientObject.getDouble(KEY_PER_PORTION));
			}

			if (elementNutrientObject.get(KEY_PER_DAY).equals(null)) {
				element.setPer_day(0.0);
			} else {
				element.setPer_day(elementNutrientObject.getDouble(KEY_PER_DAY));
			}

			nameTranslationObject = elementNutrientObject.getJSONObject(KEY_NAME_TRANSLATIONS);
			element.setName(nameTranslationObject.getString(KEY_FR));
			res.put(currentKey, element);
		}
		return res;
	}

	private static void setNameTranslation() throws JSONException {

		// get the name translation object
		nameTranslationObject = eachObject.getJSONObject(KEY_NAME_TRANSLATIONS);

		keys = nameTranslationObject.keys();

		if (nameTranslationObject.has(KEY_FR)) {
			food.setName(nameTranslationObject.getString(KEY_FR));
		} else {
			food.setName(nameTranslationObject.getString((String) keys.next()));
		}
	}

	private static void setIngredientTranslation() throws JSONException {

		// get the ingredient translation object
		ingredientTranslationObject = eachObject.getJSONObject(KEY_INGREDIENTS_TRANSLATIONS);

		System.out.println(ingredientTranslationObject.length());

		Iterator<String> keys = nameTranslationObject.keys();

		if (ingredientTranslationObject.length() > 0) {
			if (ingredientTranslationObject.has(KEY_FR)) {
				food.setIngredients_translations(ingredientTranslationObject.getString(KEY_FR));
			} else {
				System.out.println(ingredientTranslationObject.keys().next());
				System.out.println(nameTranslationObject);
				food.setIngredients_translations(nameTranslationObject.getString(keys.next()));
			}
		}

	}
}
