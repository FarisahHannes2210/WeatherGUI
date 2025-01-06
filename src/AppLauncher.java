import javax.swing.*;

public class AppLauncher {

    public static void main(String[] args) {


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new WeatherAppGui().setVisible(true);

                //test if API for location work
//                System.out.println(WeatherApp.getLocationData("Kuala Lumpur"));

                //test if getting current time works
//                System.out.println(WeatherApp.getCurrentTime());

            }
        });
    }
}




