# Hangman Game for Android
A simple and engaging Hangman game built for Android using Java in Android Studio. The player guesses letters to reveal a hidden word within a limited number of attempts. This project serves as an excellent learning resource for beginners in Android development with Java.

## Features

✅ Random word selection from a predefined list.  
✅ Interactive alphabet buttons (A-Z) for guessing letters.  
✅ Limited attempts (6 chances) to guess the word.  
✅ Visual feedback for correct/incorrect guesses.  
✅ Game over and win conditions with appropriate messages.  

## Installation

### 1. Clone the Repository

Clone this repository to your local machine using the following command:

```bash
git clone https://github.com/k-aghakhani/Hangman.git
```

### 2. Open in Android Studio

1. Launch Android Studio.
2. Select **Open an existing project** and navigate to the cloned Hangman folder.
3. Let Android Studio sync the project with Gradle.

### 3. Build and Run

1. Connect an Android device or start an emulator.
2. Click **Run** in Android Studio to build and install the app on your device/emulator.

## How to Play

1. The game starts with a hidden word displayed as dashes (e.g., `_ _ _ _` for a 4-letter word).
2. Below the word, the number of remaining attempts (6) is shown.
3. Use the alphabet buttons (A-Z) to guess letters:
   - If the letter is in the word, it will appear in the correct position(s).
   - If the letter is not in the word, the number of attempts decreases by 1.
4. The game ends when:
   - ✅ You guess the word correctly (Win). 🎉
   - ❌ You run out of attempts (Game Over). 💀

## Project Structure

📂 **app/src/main/java/com/aghakhani/hangman/MainActivity.java** - The main Java file containing the game logic, UI setup, and letter-guessing functionality.  
📂 **app/src/main/res/layout/activity_main.xml** - The layout file defining the UI, including the word display, attempts counter, and alphabet buttons.  
📂 **app/src/main/AndroidManifest.xml** - The manifest file for the app.  

## Contributing

Contributions are welcome! If you'd like to contribute to this project, follow these steps:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes and commit them:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a **Pull Request** on GitHub.

Feel free to report issues or suggest improvements via the **Issues** tab.

## License

This project is licensed under the **MIT License** - see the `LICENSE` file for details.

## Contact

For questions or feedback, reach out to me via:

📌 **GitHub:** [k-aghakhani](https://github.com/k-aghakhani)  
📧 **Email:** [kiarash1988@gmail.com](mailto:kiarash1988@gmail.com)  

🚀 Happy coding! 🎮

Made with ❤️ and ☕
