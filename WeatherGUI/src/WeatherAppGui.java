
import com.github.cliftonlabs.json_simple.JsonObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;


public class WeatherAppGui extends JFrame {

    private JsonObject weatherData;

    public WeatherAppGui(JsonObject weatherData) throws HeadlessException {
        this.weatherData = weatherData;
    }

    public Optional<JsonObject> getWeatherData() {
        return Optional.ofNullable(weatherData);
    }

    public void setWeatherData(JsonObject weatherData) {
        this.weatherData = weatherData;
    }

    public WeatherAppGui() {


        super("Weather App"); //setting title for the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //set to exit program when closed
        setSize(450, 650); //set frame size
        setLocationRelativeTo(null); //set location to middle of the computer
        setLayout(null); //make layout manager manually set on middle of the frame
        setResizable(false); // set cant resize the frame

        addGuiComponents();
    }


    private void addGuiComponents() {

        //search text field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(10, 13, 350, 45);
        searchTextField.setFont(new Font("Roboto", Font.PLAIN, 24));
        add(searchTextField);

        JLabel builtByLabel = new JLabel("Built by Icha");
        builtByLabel.setBounds(10, 50, 200, 30); // Adjust position and size as needed
        builtByLabel.setForeground(Color.GRAY); // Optional: Set color to distinguish it visually
        add(builtByLabel);


        //weather condition image
        JLabel weatherConditionImage = new JLabel(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/cloudy.png"));//mybe just use name
        weatherConditionImage.setBounds(0, 60, 450, 300);
        add(weatherConditionImage);

        //temperature label
        JLabel temperatureLabel = new JLabel("24°C");
        temperatureLabel.setBounds(-10, 300, 450, 54);
        temperatureLabel.setFont(new Font("Roboto", Font.BOLD, 40));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureLabel);

        //weather condition description label
        JLabel weatherConditionDescriptionLabel = new JLabel("Cloudy");
        weatherConditionDescriptionLabel.setBounds(-10, 360, 450, 54);
        weatherConditionDescriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 24));
        weatherConditionDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDescriptionLabel);

        //humidity image
        JLabel humidityLabel = new JLabel(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/humidity.png"));
        humidityLabel.setBounds(10, 500, 74, 66);
        add(humidityLabel);

        //humidity label
        JLabel humidityValueLabel = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityValueLabel.setBounds(80, 510, 100, 55);
        humidityValueLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(humidityValueLabel);

        //windspeed image
        JLabel windSpeedLabel = new JLabel(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/windspeed.png"));
        windSpeedLabel.setBounds(220, 500, 74, 66);

        add(windSpeedLabel);

        //windspeed label
        JLabel windSpeedValueLabel = new JLabel("<html><b>Wind Speed</b> 100% </html>");
        windSpeedValueLabel.setBounds(300, 510, 100, 55);
        windSpeedValueLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(windSpeedValueLabel);

        //search button
        JButton searchButton = new JButton(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/search.png"));

        //change cursor to ahand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText().trim();


                //validate input - remove whitespace to ensure non-empty text
                if (userInput.isEmpty()) {
                    JOptionPane.showMessageDialog(WeatherAppGui.this, "Could not retrieve weather data." +
                            " Please check the location.", "Error", JOptionPane.ERROR_MESSAGE);

                    return;
                }

                //retrieve weather data
                Optional<JsonObject> weatherData = (Optional<JsonObject>) WeatherApp.getWeatherData(userInput);
                if (!weatherData.isPresent()) {
                    JOptionPane.showMessageDialog(WeatherAppGui.this, "Could not retrieve weather data. Please check the location.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //update gui components

                //assert weatherData != null;
                JsonObject data = weatherData.get(); // Safe extraction after validation.
                String weatherCondition = (String) data.get("weather_condition");
                weatherConditionDescriptionLabel.setText(weatherCondition);

                // humidity = (int) Math.round((Double) data.get("humidity")); // Define humidity
                humidityValueLabel.setText("<html><b>Humidity</b> " + data.get("humidity") + "%</html>");

                windSpeedValueLabel.setText("<html><b>Wind Speed</b> " + data.get("windspeed") + " km/h</html>");
                //weatherConditionImage.setIcon(loadImage("src/assets/" + weatherCondition.toLowerCase() + ".png"));

//                System.out.println("weatherData: " + weatherData);

                //depending on the condition, we will update the image based on the weather condition
                //weatherCondition = weatherCondition.toLowerCase();
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/cloudy.png"));

                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("D:/Backend Projects/WeatherAppGUI/WeatherGUI/src/assets/snow.png"));
                        break;

                }

                //update temperature text
                Object tempObj = data.get("temperature");
                if (tempObj instanceof Double) {
                    double temperature = (Double) tempObj;
                    temperatureLabel.setText(String.format("%.1f°C", temperature));

                    //update humidity
                    Object humidityObj = data.get("humidity");
                    if (humidityObj instanceof Double) {
                        long humidity = Math.round((Double) humidityObj);
                        System.out.println("Updated Humidity: " + humidity + "%"); // Debugging
                        humidityValueLabel.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                        //update windspeed
                        Object windSpeedObj = data.get("windspeed");
                        if (windSpeedObj instanceof Double) {
                            double windspeed = (Double) windSpeedObj;
                            System.out.println("Updated Wind Speed: " + windspeed + " km/h");
                            windSpeedValueLabel.setText("<html><b>Wind Speed</b> " + windspeed + " km/h</html>");
                        }
                    }
                }

            }
        });

        add(searchButton);
    }

      //used to create image in our gui components
//    private ImageIcon loadImage(String relativePath) {
//
//        try{
//            // Get the absolute path to the working directory
//            String basePath = System.getProperty("user.dir");
//            File imageFile = new File(basePath, relativePath); // Resolve relative path
//            if (imageFile.exists()) {
//                BufferedImage image = ImageIO.read(imageFile);
//                return new ImageIcon(image); // Return the image if found
//            } else {
//                System.err.println("Error: Image not found at " + imageFile.getAbsolutePath());
//            }
//        } catch (IOException e) {
//                System.err.println("error loading image" + e.getMessage());
//
//        }
//        return new ImageIcon(); //return empty icon to avoid nullpointer
//    }
//}

    private ImageIcon loadImage(String path) {

        try{
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            System.err.println("error loading image" + e.getMessage());

        }
        return null;
    }

}
