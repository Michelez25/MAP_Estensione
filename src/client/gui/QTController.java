package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

/**
 * Controller JavaFX per l'interfaccia grafica del QT Miner.
 * Gestisce la comunicazione con il server e le interazioni dell'utente  per il mining, il salvataggio e il caricamento dei cluster.
 */
public class QTController {

    /** Campo di testo per il nome della tabella del database. */
    @FXML private TextField tableNameField;

    /** Campo di testo per il raggio di clustering. */
    @FXML private TextField radiusField;

    /** Area di testo per la visualizzazione dei risultati. */
    @FXML private TextArea outputArea;

    /** Stream per l'invio di oggetti al server. */
    private ObjectOutputStream out;

    /** Stream per la ricezione di oggetti dal server. */
    private ObjectInputStream in;

    /**
     * Inizializza il controller stabilendo la connessione con il server.
     * Viene invocato automaticamente da JavaFX al caricamento del layout FXML.
     */
    @FXML
    public void initialize() {
        try {
            // Connessione al server
            Socket socket = new Socket("localhost", 8080);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            outputArea.setText("Connesso al server localhost:8080\n");
        } catch (IOException e) {
            outputArea.setText("ERRORE: Impossibile connettersi al server. Assicurati che Server.bat sia avviato.");
        }
    }

    /**
     * Verifica che il valore nel campo raggio sia un numero reale.
     * Mostra un alert di errore se il campo è vuoto o contiene una stringa non numerica.
     *
     * @return il valore del raggio come double, oppure -1 se l'input non è valido
     */
    private double validateRadius() {
        String radiusText = radiusField.getText().trim();

        // Controlla se il campo è vuoto
        if (radiusText.isEmpty()) {
            showAlert("Campo raggio vuoto", "Inserisci un valore numerico per il raggio.");
            return -1;
        }

        try {
            return Double.parseDouble(radiusText);
        } catch (NumberFormatException e) {
            showAlert("Valore non valido", "\"" + radiusText + "\" non è un numero valido.\nInserisci un valore numerico.");
            return -1;
        }
    }

    /**
     * Mostra un alert di errore con titolo e messaggio personalizzati.
     *
     * @param title   titolo della finestra di alert
     * @param message messaggio di errore da mostrare
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
    * Gestisce il pulsante di avvio del mining.
    * Invia al server il nome della tabella e il raggio, poi mostra i cluster scoperti.
    * Mostra un alert se la tabella non esiste nel database.
    * Mostra "Nessun risultato." nell'area di output se il raggio non produce cluster.
    */
    @FXML
    private void handleMining() {
        // Valida il raggio prima di procedere
        double radius = validateRadius();
        if (radius < 0) return;

   try {
    
    // Caso 0: Invio nome tabella
    out.writeObject(0);
    out.writeObject(tableNameField.getText());
    String tableResponse = (String) in.readObject();
    if (tableResponse.startsWith("ERR:")) {
        showAlert("Tabella non trovata", "La tabella \"" + tableNameField.getText() + "\" non esiste nel database.");
        return;
    }

    // Caso 1: Invio raggio
    out.writeObject(1);
    out.writeObject(radius);

    String miningResponse = (String) in.readObject();
    if (miningResponse.startsWith("ERR:")) {
        outputArea.setText("Nessun risultato.");
    } else if (miningResponse.equals("OK")) {
        in.readObject(); // Salta il numero di cluster
        String clusterSet = (String) in.readObject();
        outputArea.setText("Mining completato con successo:\n" + clusterSet);
    }
} catch (Exception e) {
    outputArea.appendText("\nErrore durante il mining: " + e.getMessage());
}
    }

    /**
     * Gestisce il pulsante di salvataggio dei risultati.
     * Apre una finestra di dialogo per scegliere il file e invia il nome al server.
     */
    @FXML
    private void handleSave() {
        try {
            // Apre la finestra per scegliere dove salvare
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Risultati Mining");
            fileChooser.setInitialDirectory(new File(".")); // Apre nella cartella src
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File DAT", "*.dat"));

            // Passiamo una nuova Stage per mostrare la finestra
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                out.writeObject(2);
                out.writeObject(file.getName());
                if (in.readObject().equals("OK")) {
                    outputArea.appendText("\nRisultati salvati correttamente nel file: " + file.getName());
                }
            }
        } catch (Exception e) {
            outputArea.appendText("\nErrore nel salvataggio: " + e.getMessage());
        }
    }

    /**
     * Gestisce il pulsante di caricamento dei risultati da file.
     * Apre una finestra di dialogo per scegliere il file e invia il nome al server.
     */
    @FXML
    private void handleLoad() {
        try {
            // Apre la finestra per scegliere il file da caricare
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Carica Risultati Mining");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File DAT", "*.dat"));

            File file = fileChooser.showOpenDialog(new Stage());

            if (file != null) {
                out.writeObject(3);
                out.writeObject(file.getName());
                if (in.readObject().equals("OK")) {
                    String clusterSet = (String) in.readObject();
                    outputArea.setText("Dati caricati con successo da " + file.getName() + ":\n" + clusterSet);
                }
            }
        } catch (Exception e) {
            outputArea.appendText("\nErrore nel caricamento: " + e.getMessage());
        }
    }
}