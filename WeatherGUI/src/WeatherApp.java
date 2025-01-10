//use to retrieve data from API - this is the backend
//fetch data from the external API and display it in our GUI

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

public class WeatherApp {

    public static Object getWeatherData(String locationName){

        JsonArray locationData = (JsonArray) getLocationData(locationName);

        if(locationData == null || locationData.isEmpty()){
            System.out.println("Error: no location data found");
            return Optional.empty();
        }
        
        //extract latitude and longitude data
        JsonObject location = (JsonObject) locationData.get(0);
        double latitude = ((BigDecimal)location.get("latitude")).doubleValue();
        double longitude = ((BigDecimal)location.get("longitude")).doubleValue();


        //build API request URL with location coordinates
        String URLString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude +
                "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code," +
                "wind_speed_10m&timezone=Asia%2FSingapore";

        try{

            //call API and get response
            HttpURLConnection conn = fetchAPIResponse(URLString);

            //check for response status (200- success)

            assert conn != null;
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return Optional.empty();

            }else{
                StringBuilder resultsJSON = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our stringbuilder
                while(scanner.hasNext()){
                    resultsJSON.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse & serialize the json string into json object
                JsonObject resultsJsonObj = (JsonObject) Jsoner.deserialize(resultsJSON.toString());

                //get the list of hourly data teh API generated from the location name
                JsonObject hourly = (JsonObject) resultsJsonObj.get("hourly");

                JsonArray time = (JsonArray) hourly.get("time");
                int index = findIndexOfCurrentTime(time);

                //get temperature
                JsonArray temperatureData = (JsonArray) hourly.get("temperature_2m");
                double temperature = ((BigDecimal)temperatureData.get(index)).doubleValue();

                //get weather code
                JsonArray weatherCode = (JsonArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode(((BigDecimal) weatherCode.get(index)).longValue());

                //get humidity
                JsonArray humidityData = (JsonArray) hourly.get("relative_humidity_2m");
                long humidity = ((BigDecimal) humidityData.get(index)).longValue();

                //get windspeed
                JsonArray windSpeedData = (JsonArray) hourly.get("wind_speed_10m");
                double windspeed = ((BigDecimal) windSpeedData.get(index)).doubleValue();

                //build the weather JSON data object that we are going to access in our frontend
                JsonObject weatherData = new JsonObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

                return Optional.of(weatherData);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        return Optional.empty();
    }



    // retrieve geographic coordinates for given location name
    public static Object getLocationData(String locationName){

        //replace any whitespace to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        //build API URL with location parameters

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                            + locationName + "&count=10&language=en&format=json";

        try{
            //call API and get a response
            HttpURLConnection URLConn = fetchAPIResponse(urlString);

            if(URLConn == null || URLConn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return Optional.empty();
            }else{
                //store the API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(URLConn.getInputStream());

                //read and store the resulting json data into our stringbuilder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                URLConn.disconnect();

                //parse the json string into json object

                JsonObject resultsJsonObj = (JsonObject) Jsoner.deserialize(resultJson.toString());
                //JsonObject resultsJsonObj = (JsonObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data teh API generated from the location name
                JsonArray locationData = (JsonArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        //couldnt find location
        return Optional.empty();
    }

    // fetch api response based on url string
    private static HttpURLConnection fetchAPIResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our app aka API
            conn.connect();
            return conn;

        }catch(IOException e){
            e.printStackTrace();;
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JsonArray timeList){

        String currentTime = getCurrentTime();

        for(int i = 0; i < timeList.size(); i++){
            //String time = (String) timeList.get(i);

            if(timeList.get(i).toString().equalsIgnoreCase(currentTime)){

                //return the index
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        //get current datetime
        LocalDateTime currentDateTime = LocalDateTime.now();

        //change it into yyyy-mm-ddThh:mm so it matches the API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H':00'");

        //format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    //convert weather code into something more readable
    private static String convertWeatherCode(long weathercode) {

        String weatherCondition = " ";

        if(weathercode == 0L){
            weatherCondition = "Clear";

        }else if(weathercode <= 3L && weathercode > 0L){
            weatherCondition = "Cloudy";

        }else if((weathercode >= 51L && weathercode <= 67L ) || (weathercode >= 80L && weathercode <= 99L)){
            weatherCondition = "Rain";

        }else if(weathercode >= 71L && weathercode <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;

    }



}
