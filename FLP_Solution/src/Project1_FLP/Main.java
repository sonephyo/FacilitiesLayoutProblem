package Project1_FLP;

public class Main {
    static final int num_of_stations = 48;

    static final int num_of_threads = 64;
    static final int countOfGAOperations = 200;


    public static void main(String[] args) {

        Layout layout = new Layout(num_of_threads);
        layout.evaluate(num_of_stations, countOfGAOperations);

    }
}