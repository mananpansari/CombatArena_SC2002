package boundary;

import engine.GameSession;

public class MainStart {
public static void main(String[] args) {
    InputHandler input = new InputHandler();
    LoadingScreen loader = new LoadingScreen(input);
    boolean keepPlaying = true;
    while (keepPlaying) {
    GameSession session = loader.setupGame();
    session.start(input);
    keepPlaying = input.getReplay();
    if (keepPlaying) {
        System.out.println("\n...Initialising new Combat Session...\n");
    }
    }
    System.out.println("Exiting game, goodbye.");
}
}