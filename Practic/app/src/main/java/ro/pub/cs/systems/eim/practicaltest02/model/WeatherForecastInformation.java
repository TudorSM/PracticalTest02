package ro.pub.cs.systems.eim.practicaltest02.model;

public class WeatherForecastInformation {

    private String EUR;
    private String USD;

    public WeatherForecastInformation() {
        this.EUR = null;
        this.USD = null;
    }

    public WeatherForecastInformation(String eur, String usd) {
        this.EUR = eur;
        this.USD = usd;
    }

    public String getEUR() {
        return EUR;
    }
    public void setEUR(String eur) {
        this.EUR = eur;
    }
    public String getUSD() {
        return USD;
    }
    public void setUSD(String usd) {
        this.USD = usd;
    }



    @Override
    public String toString() {
        return "WeatherForecastInformation{" +
                "temperature='" + EUR + '\'' +
                ", windSpeed='" + USD + '}';
    }

}
