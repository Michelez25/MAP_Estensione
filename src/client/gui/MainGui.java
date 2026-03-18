package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'interfaccia grafica.
 * Avvia l'applicazione JavaFX caricando il layout dal file FXML.
 */
public class MainGui extends Application {

    /**
     * Inizializza e mostra la finestra principale dell'applicazione.
     *
     * @param primaryStage finestra principale fornita da JavaFX
     * @throws Exception se si verifica un errore nel caricamento del file FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica il file grafico FXML
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setTitle("QT Miner - Clustering Explorer");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }

    /**
     * Entry point dell'applicazione JavaFX.
     *
     * @param args argomenti da riga di comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}