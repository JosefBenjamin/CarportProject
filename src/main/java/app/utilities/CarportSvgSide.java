package app.utilities;

public class CarportSvgSide {

    private int width;
    private int height;
    private Svg carportSvg;


    public CarportSvgSide(int width, int height) {

        this.width = width;
        this.height = height;
        this.carportSvg = new Svg(0, 0, "0 0 " + width + " " + height, "40%");
        carportSvg.addRectangle(0, 0, width, height, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addPosts();
        addRafters();

    }

    public void addPosts() {
        double postWidth = 10;
        double postHeight = height;
        int numberOfPosts = 4;
        double spacing = (width - postWidth) / (numberOfPosts - 1);
        for (int i = 0; i < numberOfPosts; i++) {
            double x = i * spacing;
            carportSvg.addRectangle(x, 0, postWidth, postHeight, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }
    }

    public void addRafters() {

        double beamHeight = 10; // Thickness of the top beam
        carportSvg.addRectangle(0, 0, width, beamHeight, "stroke:#000000; fill: #ffffff");

    }


    @Override
    public String toString() {
        return carportSvg.toString();
    }

}
