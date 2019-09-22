import java.util.ArrayList;

public class ProcessedPlayer {
    String playerID;
    String name;
    String flagLink;
    String picLink;
    int tourneyAmount;
    ArrayList<String> tourneys;

    public ProcessedPlayer(String playerID, String name, String flagLink,String picLink,int tourneyAmount, ArrayList<String> tourneys) {
        this.playerID = playerID;
        this.name = name;
        this.flagLink = flagLink;
        this.picLink = picLink;
        this.tourneyAmount = tourneyAmount;
        this.tourneys = tourneys;
    }
}
