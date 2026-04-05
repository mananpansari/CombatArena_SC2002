package boundary;

import engine.GameSession;

public class MainStart {
    public static void main(String[] args) {

        // Initialize input and LS
        InputHandler input = new InputHandler();
        LoadingScreen loader = new LoadingScreen(input);

        boolean keepPlaying = true;

        while (keepPlaying) {
            // Setup character, items and difficulty via LoadingScreen
            GameSession session = loader.setupGame();
            // Start the engine battle loop
            session.start(input);
            // Check for replay using InputHandler
            keepPlaying = input.getReplay();

            if (keepPlaying) {
                System.out.println("...Initialising new Combat Session...");
            }
        }

        System.out.println("Exiting game, goodbye.");
    }
}