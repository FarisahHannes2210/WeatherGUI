reminder: if image cant be load , change path for every image based on your device.

#major changes : use JSON-simple 4.0.1 jar instead of 1.1.1 jar

#Changes in WeatherApp.java

-Library Changes:
Replaced JSONObject with JsonObject.
Replaced JSONArray with JsonArray.

-Parsing Changes:
Parsing JSON is now done with Jsoner.deserialize.

-Data Extraction:
Replaced doubleValue conversions from BigDecimal (used in latitude, longitude, etc.) for better precision handling.

-Return Type Adjustments:
getWeatherData now consistently uses Optional<JsonObject> instead of the previous mix of Object and Optional.

-Optional Enhancements:
Improved use of Optional to avoid null handling and make code safer.

-Potential Impacts:
The new JSON library requires explicit handling of BigDecimal for numbers and String casting.

#Changes in WeatherAppGui.java

-Library Changes:
Updated to use JsonObject for weatherData.

-update Gui elements
Updated asset paths and dynamically changed images for weather conditions.
update text and value to automatically updated the fields

-Data Handling:
GUI now accesses weather data using Optional<JsonObject>.

-Switch Statement for Weather Conditions:
Used a switch-case structure to dynamically update weather icons based on conditions (Clear, Cloudy, etc.).
