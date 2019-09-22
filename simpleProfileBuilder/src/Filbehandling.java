import java.io.IOException;
import java.util.List;

public class Filbehandling {
    public static List<Tournament> tournaments = null;
    public static List<Teammate> teammates = null;

    public static void oppdaterTournaments() {
        try {
            tournaments = TournamentReader.readTournaments("src/Tournaments.csv");
        } catch (IOException e) {
            System.out.println("ERROR");
        }

        if(tournaments == null) {
            System.exit(1);
        }
    }

    public static void oppdaterTeammates() {
        try {
            teammates = TeammateReader.readTeammates("src/Teammates.csv");
        } catch (IOException e) {
            System.out.println("ERROR");
        }

        if(teammates == null) {
            System.exit(1);
        }
    }
}
