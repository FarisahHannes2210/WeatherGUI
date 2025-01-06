import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;

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


        //weather condition image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 60, 450, 300);
        add(weatherConditionImage);

        //temperature label
        JLabel temperatureLabel = new JLabel("24°C");
        temperatureLabel.setBounds(0, 300, 450, 54);
        temperatureLabel.setFont(new Font("Roboto", Font.BOLD, 40));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureLabel);

        //weather condition description label
        JLabel weatherConditionDescriptionLabel = new JLabel("Cloudy");
        weatherConditionDescriptionLabel.setBounds(0, 360, 450, 54);
        weatherConditionDescriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 24));
        weatherConditionDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDescriptionLabel);

        //humidity image
        JLabel humidityLabel = new JLabel(loadImage("src/assets/humidity.png"));
        humidityLabel.setBounds(10, 500, 74, 66);
        add(humidityLabel);

        //humidity label
        JLabel humidityValueLabel = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityValueLabel.setBounds(80, 510, 50, 55);
        humidityValueLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(humidityValueLabel);

        //windspeed image
        JLabel windSpeedLabel = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedLabel.setBounds(200, 500, 74, 66);
        add(windSpeedLabel);

        //windspeed label
        JLabel windSpeedValueLabel = new JLabel("<html><b>Wind Speed</b> 100% </html>");
        windSpeedValueLabel.setBounds(280, 510, 100, 55);
        windSpeedValueLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(windSpeedValueLabel);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //change cursor to ahand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);




        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                //validate input - remove whitesapce to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    JOptionPane.showMessageDialog(WeatherAppGui.this, "Could not retrieve weather data." +
                            " Please check the location.", "Error", JOptionPane.ERROR_MESSAGE);

                    return;
                }




                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

//                if (weatherData != null && weatherData.get("tokyo") != null) {
//                    // Proceed with using weatherData
//                    JSONObject results = (JSONObject) weatherData.get("tokyo");
//                } else {
//                    System.out.println("weatherData or the 'results' key is null");
//                }

                //---------------------------------------------------------

                //update gui

//                System.out.println("weatherData bfore: " + weatherData);
                //update weather condition image
                assert weatherData != null;
                String weatherCondition = (String) weatherData.get("weather_condition");

//                System.out.println("weatherData: " + weatherData);

                //depending on the condition, we will update the image based on the weather condition

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureLabel.setText(temperature + "C");

                //update humidity
                long humidity = (long) weatherData.get("humidity");
                humidityValueLabel.setText("<html><b>Humidity</b>" + humidity + "</html>");

                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedValueLabel.setText("<html><b>Wind Speed</b>" + windspeed + "km/h</html>");

            }
        });

        add(searchButton);



    }

    //used to create image in our gui components
    private ImageIcon loadImage(String path) {

        try{
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();

        }

        System.out.println("image not found");
        return null;
    }

}
