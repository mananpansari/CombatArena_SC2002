package Boundary;

import engine.GameSession;

public class Main {
    public static void main(String[] args) {

        // Initialize input and LS
        InputHandler input = new InputHandler(); 
        LoadingScreen loader = new LoadingScreen(input); 
        
        boolean keepPlaying = true;

        while (keepPlaying) {
            // Setup character,items and difficulty via LS
            GameSession session = loader.setupGame();
            // Start the engine battle loop
            session.start(input);
            //Check for replay using IH
            keepPlaying = input.getReplay();

            if (keepPlaying) {
                System.out.println("...Initialising new Combat Session...");
            }
        } 

        System.out.println("Exiting game, goodbye.");
    }
}