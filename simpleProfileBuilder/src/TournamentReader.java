import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TournamentReader {
    public static List<Tournament> readTournaments(String path) throws IOException {
        ArrayList<Tournament> tournaments = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = Files.newBufferedReader(Paths.get(path));
            String line = null;

            while((line=reader.readLine()) !=null ) {
                tournaments.add(parseTournament(line));
            }
        } finally {
            if(reader!= null) {
                reader.close();
            }
        }

        return tournaments;
    }

    public static Tournament parseTournament(String line) {
        String[] split = line.split(";");

        String name = split[0];
        String notes = split[1];
        String position = split[2];

        ArrayList<String> teammates = new ArrayList<>();

        for(int i = 3; i<split.length; i++) {
            teammates.add(split[i]);
        }

        return new Tournament(name, teammates, position, notes);
    }
}
