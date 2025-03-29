package com.aghakhani.hangman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView attemptsTextView;
    private EditText guessEditText;
    private Button guessButton;

    private String[] words = {"JAVA", "ANDROID", "STUDIO", "CODE", "GAME"}; // List of possible words
    private String wordToGuess; // The word to guess
    private char[] wordDisplay; // Array to display word with dashes
    private int attemptsLeft = 6; // Number of attempts allowed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        wordTextView = findViewById(R.id.wordTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView);
        guessEditText = findViewById(R.id.guessEditText);
        guessButton = findViewById(R.id.guessButton);

        // Start a new game
        startNewGame();

        // Set button click listener
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });
    }

    // Method to start a new game
    private void startNewGame() {
        // Select a random word from the list
        int randomIndex = (int) (Math.random() * words.length);
        wordToGuess = words[randomIndex];

        // Initialize the display array with dashes
        wordDisplay = new char[wordToGuess.length()];
        for (int i = 0; i < wordToGuess.length(); i++) {
            wordDisplay[i] = '_';
        }

        // Reset attempts
        attemptsLeft = 6;

        // Update UI
        updateDisplay();
    }

    // Method to update the UI
    private void updateDisplay() {
        // Convert char array to string with spaces for readability
        StringBuilder display = new StringBuilder();
        for (char c : wordDisplay) {
            display.append(c).append(" ");
        }
        wordTextView.setText(display.toString());

        // Update attempts left
        attemptsTextView.setText("Attempts left: " + attemptsLeft);

        // Check win condition
        if (String.valueOf(wordDisplay).equals(wordToGuess)) {
            wordTextView.setText("You Win! The word was: " + wordToGuess);
            guessButton.setEnabled(false);
        }

        // Check lose condition
        if (attemptsLeft <= 0) {
            wordTextView.setText("Game Over! The word was: " + wordToGuess);
            guessButton.setEnabled(false);
        }
    }

    // Method to check the user's guess
    private void checkGuess() {
        // Get the guessed letter
        String input = guessEditText.getText().toString().toUpperCase();
        if (input.isEmpty() || input.length() > 1) {
            return; // Ignore invalid input
        }

        char guess = input.charAt(0);
        guessEditText.setText(""); // Clear input field

        // Check if the letter is in the word
        boolean correctGuess = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                wordDisplay[i] = guess;
                correctGuess = true;
            }
        }

        // If guess was wrong, decrease attempts
        if (!correctGuess) {
            attemptsLeft--;
        }

        // Update the display
        updateDisplay();
    }
}