package com.aghakhani.hangman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView attemptsTextView;
    private GridLayout alphabetGrid;

    private String[] words = {"JAVA", "ANDROID", "STUDIO", "CODE", "GAME"}; // List of possible words
    private ArrayList<String> availableWords; // List of words that haven't been used yet
    private ArrayList<String> usedWords; // List of words that have been used
    private String wordToGuess; // The word to guess
    private char[] wordDisplay; // Array to display word with dashes
    private int attemptsLeft = 6; // Number of attempts allowed
    private int currentLevel = 1; // Track the current level

    private MediaPlayer winSound; // MediaPlayer for win sound
    private MediaPlayer loseSound; // MediaPlayer for lose sound

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        wordTextView = findViewById(R.id.wordTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView);
        alphabetGrid = findViewById(R.id.alphabetGrid);

        // Initialize the lists for available and used words
        availableWords = new ArrayList<>(Arrays.asList(words));
        usedWords = new ArrayList<>();

        // Initialize MediaPlayers for sounds
        winSound = MediaPlayer.create(this, R.raw.win_sound);
        loseSound = MediaPlayer.create(this, R.raw.lose_sound);

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

    // Method to start a new game or move to the next level
    private void startNewGame() {
        // Check if there are any words left to guess
        if (availableWords.isEmpty()) {
            // If no words are left, show a dialog to end the game
            showGameCompletedDialog("Congratulations! You've completed all levels!\nYou reached Level: " + currentLevel);
            return;
        }

        // Select a random word from the available words
        Random random = new Random();
        int randomIndex = random.nextInt(availableWords.size());
        wordToGuess = availableWords.get(randomIndex);

        // Move the selected word from available to used
        usedWords.add(wordToGuess);
        availableWords.remove(wordToGuess);

        // Initialize the display array with dashes
        wordDisplay = new char[wordToGuess.length()];
        for (int i = 0; i < wordToGuess.length(); i++) {
            wordDisplay[i] = '_';
        }

        // Reset attempts
        attemptsLeft = 6;

        // Re-enable all buttons
        for (int i = 0; i < alphabetGrid.getChildCount(); i++) {
            alphabetGrid.getChildAt(i).setEnabled(true);
        }

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

        // Create the text for attemptsTextView with different colors for "Level" part
        String levelText = "Level: " + currentLevel;
        String attemptsText = " | Attempts left: " + attemptsLeft;
        String fullText = levelText + attemptsText;

        SpannableString spannableString = new SpannableString(fullText);
        // Set color for "Level: X" part (e.g., blue)
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_blue_dark)),
                0, levelText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        // The rest of the text (" | Attempts left: Y") will use the default color
        attemptsTextView.setText(spannableString);

        // Check win condition
        if (String.valueOf(wordDisplay).equals(wordToGuess)) {
            // Play win sound
            if (winSound != null) {
                winSound.start();
            }
            // Show win dialog
            showWinDialog("Congratulations! You guessed the word: " + wordToGuess);
            disableAllButtons();
        }

        // Check lose condition
        if (attemptsLeft <= 0) {
            // Play lose sound
            if (loseSound != null) {
                loseSound.start();
            }
            showLoseDialog("Game Over! The word was: " + wordToGuess + "\nYou reached Level: " + currentLevel);
            disableAllButtons();
        }
    }

    // Method to show the win dialog with Continue and Exit options
    private void showWinDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("You Win!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel++; // Move to the next level
                        startNewGame(); // Start the next level
                        dialog.dismiss(); // Close the dialog
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // Close the app
                    }
                })
                .setCancelable(false); // Prevent closing the dialog by pressing back
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show the lose dialog with Restart and Exit options
    private void showLoseDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Game Result")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel = 1; // Reset level to 1
                        resetWordLists(); // Reset the word lists
                        startNewGame(); // Restart the game
                        dialog.dismiss(); // Close the dialog
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // Close the app
                    }
                })
                .setCancelable(false); // Prevent closing the dialog by pressing back
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show a dialog when all words are guessed
    private void showGameCompletedDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Game Completed!")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel = 1; // Reset level to 1
                        resetWordLists(); // Reset the word lists
                        startNewGame(); // Restart the game
                        dialog.dismiss(); // Close the dialog
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // Close the app
                    }
                })
                .setCancelable(false); // Prevent closing the dialog by pressing back
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to reset the word lists
    private void resetWordLists() {
        availableWords.clear();
        usedWords.clear();
        availableWords.addAll(Arrays.asList(words));
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

    // Release MediaPlayer resources when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (winSound != null) {
            winSound.release();
            winSound = null;
        }
        if (loseSound != null) {
            loseSound.release();
            loseSound = null;
        }
    }
}