import java.util.ArrayList;

public class Tournament {
    String name;
    ArrayList<String> players;
    String position;
    String notes;

    public Tournament (String name, ArrayList<String> players, String position, String notes) {
        this.name = name;
        this.players = players;
        this.position = position;
        this.notes = notes;
    }

    @Override
    public String toString() {
        String teammateString = "";

        for(String s : players) {
            teammateString+= ";" + s;
        }

        return name + ";" + notes + ";" + position + teammateString;
    }
}
