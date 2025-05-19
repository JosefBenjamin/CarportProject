package app.utilities;

public class CarportSvgTop {

    private int width;
    private int length;
    private Svg carportSvg;


    public CarportSvgTop(int width, int length) {

        this.width = width;
        this.length = length;
        this.carportSvg = new Svg(0, 0, "0 0 " + width + " " + length, "40%");
        carportSvg.addRectangle(0, 0, width, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addBeams();
        addRafters();

    }

    public void addBeams() {
        double beamThickness = 4.5;
        carportSvg.addRectangle(0, 0, beamThickness, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(width - beamThickness, 0, beamThickness, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }


    public void addRafters() {
        double rafterSpacing = 55.7;
        double rafterThickness = 4.5;
        for (double i = 0; i < length; i += rafterSpacing) {
            carportSvg.addRectangle(0, i, width, rafterThickness, "stroke:#000000; fill: #ffffff");
        }

    }


    @Override
    public String toString() {
        return carportSvg.toString();
    }

}
