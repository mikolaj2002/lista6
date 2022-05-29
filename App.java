import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/**
 * Klasa uruchamia aplikację, wyświetla okno wyboru parametrów i planszę z prostokątami.
 */
public class App extends Application
{
    private final static int MARGIN = 2;
    private final static int SQUARE_SIZE = 50;

    private int width;
    private int height;
    private int n;
    private int m;
    private int k;
    private double p;

    @Override
    public void start(Stage primaryStage) { getParameters(primaryStage); }
    
    public static void main(String[] args) { launch(args); }

    private void getParameters(Stage primaryStage)
    {
        final GridPane inputPane = new GridPane();
        inputPane.setHgap(10);
        inputPane.setVgap(5);
        
        final Label sizeLabel = new Label("Podaj rozmiar planszy");
        final Label speedLabel = new Label("Podaj szybkość działania [ms]");
        final Label probabLabel = new Label("Podaj prawdopodobieństwo zmiany koloru na losowy");

        final TextField nInput = new TextField();
        final TextField mInput = new TextField();
        final TextField kInput = new TextField();
        final TextField pInput = new TextField();

        final Button startButton = new Button("Start");

        final Label errorLabel = new Label();

        inputPane.add(sizeLabel, 0, 0);
        inputPane.add(nInput, 0, 1);
        inputPane.add(mInput, 1, 1);
        inputPane.add(speedLabel, 0, 2);
        inputPane.add(kInput, 0, 3);
        inputPane.add(probabLabel, 0, 4);
        inputPane.add(pInput, 0, 5);
        inputPane.add(startButton, 0, 6);
        inputPane.add(errorLabel, 0, 7);

        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                errorLabel.setText("");

                try
                {
                    n = Integer.parseInt(nInput.getText().trim());
                    m = Integer.parseInt(mInput.getText().trim());
                }
                catch (final NumberFormatException ex)
                {
                    errorLabel.setText("Rozmiar planszy musi być liczbą całkowitą");
                    return;
                }

                try { k = Integer.parseInt(kInput.getText().trim()); }
                catch (final NumberFormatException ex)
                {
                    errorLabel.setText("Szybkość działania musi być liczbą całkowitą");
                    return;
                }

                try { p = Double.parseDouble(pInput.getText().trim()); }
                catch (final NumberFormatException ex)
                {
                    errorLabel.setText("Prawdopodobieństewo musi być liczbą rzeczywistą");
                    return;
                }

                if (n <= 0 || m <= 0 || k <= 0 || p < 0)
                {
                    errorLabel.setText("Każdy parametr musi być nieujemny/dodatni");
                    return;
                }

                showMainBoard(primaryStage);
            }
        });

        primaryStage.setTitle("Lista 6");
        primaryStage.setScene(new Scene(inputPane, 600, 300));
        primaryStage.show();
    }

    private void showMainBoard(Stage primaryStage)
    {
        width = n * SQUARE_SIZE + (n - 1) * MARGIN;
        height = m * SQUARE_SIZE + (m - 1) * MARGIN;

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(MARGIN);
        gridPane.setVgap(MARGIN);

        final Board mainBoard = new Board(gridPane, n, m, k, p, SQUARE_SIZE, MARGIN);

        primaryStage.setTitle("Lista 6");
        primaryStage.setScene(new Scene(gridPane, width, height));
        primaryStage.show();

        primaryStage.widthProperty().addListener((obs, oldValue, newValue) -> {
            int width = (newValue.intValue() - 20 - (n - 1) * MARGIN) / n;
            mainBoard.setSquareWidth(width);
        });

        primaryStage.heightProperty().addListener((obs, oldValue, newValue) -> {
            int height = (newValue.intValue() - 40 - (m - 1) * MARGIN) / m;
            mainBoard.sertSquareHeight(height);
        });
    }
}