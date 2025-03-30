package com.aghakhani.hangman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView attemptsTextView;
    private GridLayout alphabetGrid;

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
        alphabetGrid = findViewById(R.id.alphabetGrid);

        // Create alphabet buttons
        createAlphabetButtons();

        // Start a new game
        startNewGame();
    }

    // Method to create buttons for each letter of the alphabet
    private void createAlphabetButtons() {
        int index = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Button button = new Button(this);
            button.setText(String.valueOf(c));
            button.setTextSize(16);
            button.setPadding(8, 8, 8, 8);

            // Set click listener for each button
            final char guess = c;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkGuess(guess);
                    button.setEnabled(false); // Disable button after clicking
                }
            });

            // Create layout params for the button
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;

            // For Y and Z, place them in the middle of the last row
            if (c == 'Y') {
                params.rowSpec = GridLayout.spec(6); // Last row (row 6, zero-based)
                params.columnSpec = GridLayout.spec(1); // Second column (zero-based)
            } else if (c == 'Z') {
                params.rowSpec = GridLayout.spec(6); // Last row (row 6, zero-based)
                params.columnSpec = GridLayout.spec(2); // Third column (zero-based)
            } else {
                // For other letters, place them normally
                params.rowSpec = GridLayout.spec(index / 4); // Row based on index
                params.columnSpec = GridLayout.spec(index % 4); // Column based on index
            }

            button.setLayoutParams(params);

            // Add button to GridLayout
            alphabetGrid.addView(button);

            // Increment index for the next button (except for Y and Z, which are manually placed)
            if (c != 'Y' && c != 'Z') {
                index++;
            }
        }
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
            disableAllButtons();
        }

        // Check lose condition
        if (attemptsLeft <= 0) {
            wordTextView.setText("Game Over! The word was: " + wordToGuess);
            disableAllButtons();
        }
    }

    // Method to check the user's guess
    private void checkGuess(char guess) {
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

    // Method to disable all alphabet buttons
    private void disableAllButtons() {
        for (int i = 0; i < alphabetGrid.getChildCount(); i++) {
            alphabetGrid.getChildAt(i).setEnabled(false);
        }
    }
}