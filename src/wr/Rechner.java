package wr;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.event.KeyAdapter; 
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URL;

import javax.swing.border.EmptyBorder;


public class Rechner {
	private JFrame frmWhrungsrechner;
	private JComboBox<String> baseCurrencyCombo;
	private JComboBox<String> targetCurrencyCombo;
	private JTextField amountField;
	private JLabel resultLabel;
	
	// API 
	
	private static final String API_KEY = "4a88a5ab129e44abb6af1cc93d15f594";
	private static final String API_URL = "https://exchange-rates.abstractapi.com/v1/live/";

	/**
	 * Die Applikation starten.
	 */
	public static void main(String[] args) {
		 EventQueue.invokeLater(() -> {
	            try {
	                Rechner window = new Rechner();
	                window.frmWhrungsrechner.setVisible(true);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        });
	    }
	
	/**
	 * Die Anwendung erstellen.
	 */
	
	public Rechner() {

		initialize();
	}

	/**
	 * Den Inhalt des Frames initialisieren.
	 */
	
	private void initialize() {
		frmWhrungsrechner = new JFrame();
        frmWhrungsrechner.setTitle("Währungsrechner 1.0");
        frmWhrungsrechner.setBounds(100, 100, 400, 300);
        frmWhrungsrechner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmWhrungsrechner.getContentPane().setLayout(new GridLayout(5, 2, 10, 10));
        frmWhrungsrechner.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

        // Betrag eingeben
        JLabel amountLabel = new JLabel("Betrag:");
        amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frmWhrungsrechner.getContentPane().add(amountLabel);
        amountField = new JTextField();
        
     // KeyListener, um nur Ziffern zu erlauben 
        amountField.addKeyListener(new KeyAdapter() { 
        	public void keyTyped(KeyEvent e) { 
        		char c = e.getKeyChar(); 
        		if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) { e.consume();  
        		} 
        		} 
        	});
        
        frmWhrungsrechner.getContentPane().add(amountField);
        
        // Basiswährung auswählen
        JLabel baseCurrencyLabel = new JLabel("Basiswährung:");
        baseCurrencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frmWhrungsrechner.getContentPane().add(baseCurrencyLabel);
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "AUD"};
        baseCurrencyCombo = new JComboBox<>(currencies);
        frmWhrungsrechner.getContentPane().add(baseCurrencyCombo);
        
        // Zielwährung auswählen
        JLabel targetCurrencyLabel = new JLabel("Zielwährung:");
        targetCurrencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frmWhrungsrechner.getContentPane().add(targetCurrencyLabel);
        targetCurrencyCombo = new JComboBox<>(currencies);
        frmWhrungsrechner.getContentPane().add(targetCurrencyCombo);
        
        // Berechnung auslösen
        JButton calculateButton = new JButton("Berechnen");
        calculateButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Links: 5 Pixel
        frmWhrungsrechner.getContentPane().add(calculateButton);

        // Ergebnis anzeigen
        resultLabel = new JLabel("Ergebnis: ");
        frmWhrungsrechner.getContentPane().add(resultLabel);

        // Event-Handler für den Button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateExchange();
            }
        });
    } 	

	 /**
     * Berechnung des Wechselkurses und Anzeige des Ergebnisses.
     */
	private void calculateExchange() {
	    try {
	        String baseCurrency = (String) baseCurrencyCombo.getSelectedItem();
	        String targetCurrency = (String) targetCurrencyCombo.getSelectedItem();
	        double amount = Double.parseDouble(amountField.getText());

	        // Wechselkurs abrufen
	        double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);

	        if (exchangeRate < 0) {
	            resultLabel.setText("Fehler beim Abrufen des Wechselkurses.");
	            return;
	        }

	        // Ergebnis berechnen
	        double result = amount * exchangeRate;
	        resultLabel.setText("Ergebnis: " + String.format("%.2f", result) + " " + targetCurrency);

	    } catch (NumberFormatException ex) {
	        resultLabel.setText("Ungültige Eingabe.");
	    }
	}

	private double getExchangeRate(String baseCurrency, String targetCurrency) {
	    try {
	        // URI erstellen
	        URI uri = new URI(API_URL + "?api_key=" + API_KEY + "&base=" + baseCurrency + "&target=" + targetCurrency);

	        // URL von URI erstellen
	        URL url = uri.toURL();

	        // API-Aufruf durchführen
	        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");

	        // Antwort lesen
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) { // OK
	            java.io.BufferedReader in = new java.io.BufferedReader(
	                    new java.io.InputStreamReader(connection.getInputStream()));
	            StringBuilder response = new StringBuilder();
	            String line;
	            while ((line = in.readLine()) != null) {
	                response.append(line);
	            }
	            in.close();

	            // JSON-Antwort parsen
	            org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
	            return jsonResponse.getJSONObject("exchange_rates").getDouble(targetCurrency);
	        } else {
	            System.out.println("Error: HTTP response code " + responseCode);
	            return -1; // Fehlerwert zurückgeben
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1; // Fehlerwert zurückgeben
	    }
	}

	// ... restlicher Code ...


}
