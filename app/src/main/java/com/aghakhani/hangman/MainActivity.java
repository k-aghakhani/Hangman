package com.aghakhani.hangman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView attemptsTextView;
    private GridLayout alphabetGrid;

    private ArrayList<String> availableWords;
    private ArrayList<String> usedWords;
    private String wordToGuess;
    private char[] wordDisplay;
    private int attemptsLeft = 10;
    private int currentLevel = 1;

    private MediaPlayer winSound;
    private MediaPlayer loseSound;
    private MediaPlayer clickSound;

    private RequestQueue requestQueue;

    private final String[] defaultWords = {"JAVA", "ANDROID", "STUDIO", "CODE", "GAME", "CAT","DOG","ELEPHANT","FISH","GAME", "APPLE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        wordTextView = findViewById(R.id.wordTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView);
        alphabetGrid = findViewById(R.id.alphabetGrid);

        // Initialize the lists for available and used words
        availableWords = new ArrayList<>();
        usedWords = new ArrayList<>();

        // Initialize MediaPlayers for sounds
        winSound = MediaPlayer.create(this, R.raw.win_sound);
        loseSound = MediaPlayer.create(this, R.raw.lose_sound);
        clickSound = MediaPlayer.create(this, R.raw.click_sound);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Create alphabet buttons
        createAlphabetButtons();

        // Fetch words from API and start a new game
        fetchWordsFromApi();
    }

    // Method to check if the device is connected to the internet
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // Method to fetch words from the API
    private void fetchWordsFromApi() {
        if (!isInternetConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
            builder.setMessage("No internet connection. The game will use default words. Connect to the internet to fetch new words.")
                    .setTitle("No Internet")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            availableWords.clear();
                            availableWords.addAll(Arrays.asList(defaultWords));
                            startNewGame();
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        String url = "https://random-word-api.vercel.app/api?words=10";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            availableWords.clear();
                            for (int i = 0; i < response.length(); i++) {
                                String word = response.getString(i).toUpperCase();
                                if (word.matches("[A-Z]+")) {
                                    availableWords.add(word);
                                }
                            }
                            if (availableWords.isEmpty()) {
                                availableWords.addAll(Arrays.asList(defaultWords));
                            }
                            startNewGame();
                        } catch (Exception e) {
                            e.printStackTrace();
                            availableWords.clear();
                            availableWords.addAll(Arrays.asList(defaultWords));
                            startNewGame();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        availableWords.clear();
                        availableWords.addAll(Arrays.asList(defaultWords));
                        startNewGame();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    // Method to create buttons for each letter of the alphabet
    private void createAlphabetButtons() {
        int index = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Button button = new Button(this);
            button.setText(String.valueOf(c));
            button.setTextSize(16);
            button.setPadding(16, 16, 16, 16);
            button.setBackgroundResource(R.drawable.button_background);
            button.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            final char guess = c;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Load and start the button animation
                    Animation scaleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale);
                    v.startAnimation(scaleAnimation);

                    // Play click sound
                    if (clickSound != null) {
                        clickSound.start();
                    }
                    checkGuess(guess);
                    button.setEnabled(false);
                    button.setBackgroundResource(R.drawable.button_background_disabled);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);

            if (c == 'Y') {
                params.rowSpec = GridLayout.spec(6);
                params.columnSpec = GridLayout.spec(1);
            } else if (c == 'Z') {
                params.rowSpec = GridLayout.spec(6);
                params.columnSpec = GridLayout.spec(2);
            } else {
                params.rowSpec = GridLayout.spec(index / 4);
                params.columnSpec = GridLayout.spec(index % 4);
            }

            button.setLayoutParams(params);
            alphabetGrid.addView(button);

            if (c != 'Y' && c != 'Z') {
                index++;
            }
        }
    }

    // Method to start a new game or move to the next level
    private void startNewGame() {
        if (availableWords.isEmpty()) {
            showGameCompletedDialog("Congratulations! You've completed all levels!\nYou reached Level: " + currentLevel);
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(availableWords.size());
        wordToGuess = availableWords.get(randomIndex);

        usedWords.add(wordToGuess);
        availableWords.remove(wordToGuess);

        if (wordToGuess != null) {
            wordDisplay = new char[wordToGuess.length()];
            for (int i = 0; i < wordToGuess.length(); i++) {
                wordDisplay[i] = '_';
            }
        } else {
            showErrorDialog("Failed to select a word. Please try again.");
            return;
        }

        attemptsLeft = 10;

        for (int i = 0; i < alphabetGrid.getChildCount(); i++) {
            alphabetGrid.getChildAt(i).setEnabled(true);
            alphabetGrid.getChildAt(i).setBackgroundResource(R.drawable.button_background);
        }

        updateDisplay();
    }

    // Method to update the UI
    private void updateDisplay() {
        if (wordDisplay == null) {
            return;
        }

        StringBuilder display = new StringBuilder();
        for (char c : wordDisplay) {
            display.append(c).append(" ");
        }
        wordTextView.setText(display.toString());

        String levelText = "Level: " + currentLevel;
        String attemptsText = " | Attempts left: " + attemptsLeft;
        String fullText = levelText + attemptsText;

        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.level_text_color)),
                0, levelText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        attemptsTextView.setText(spannableString);

        if (wordToGuess != null && String.valueOf(wordDisplay).equals(wordToGuess)) {
            if (winSound != null) {
                winSound.start();
            }
            showWinDialog("Congratulations! You guessed the word: " + wordToGuess);
            disableAllButtons();
        }

        if (attemptsLeft <= 0) {
            if (loseSound != null) {
                loseSound.start();
            }
            showLoseDialog("Game Over! The word was: " + wordToGuess + "\nYou reached Level: " + currentLevel);
            disableAllButtons();
        }
    }

    // Method to show the win dialog with Continue and Exit options
    private void showWinDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setMessage(message)
                .setTitle("You Win!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel++;
                        startNewGame();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show the lose dialog with Restart and Exit options
    private void showLoseDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setMessage(message)
                .setTitle("Game Result")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel = 1;
                        resetWordLists();
                        fetchWordsFromApi();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show a dialog when all words are guessed
    private void showGameCompletedDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setMessage(message)
                .setTitle("Game Completed!")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentLevel = 1;
                        resetWordLists();
                        fetchWordsFromApi();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fetchWordsFromApi();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to reset the word lists
    private void resetWordLists() {
        availableWords.clear();
        usedWords.clear();
    }

    // Method to check the user's guess
    private void checkGuess(char guess) {
        if (wordToGuess == null) {
            showErrorDialog("No word to guess. Please try again.");
            return;
        }

        boolean correctGuess = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                wordDisplay[i] = guess;
                correctGuess = true;
            }
        }

        if (!correctGuess) {
            attemptsLeft--;
        }

        updateDisplay();
    }

    // Method to disable all alphabet buttons
    private void disableAllButtons() {
        for (int i = 0; i < alphabetGrid.getChildCount(); i++) {
            alphabetGrid.getChildAt(i).setEnabled(false);
            alphabetGrid.getChildAt(i).setBackgroundResource(R.drawable.button_background_disabled);
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
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }
}