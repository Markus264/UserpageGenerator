import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeammateReader {
    public static List<Teammate> readTeammates(String path) throws IOException {
        ArrayList<Teammate> teammates = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = Files.newBufferedReader(Paths.get(path));
            String line = null;

            while ((line=reader.readLine()) != null) {
                teammates.add(parseTeammate(line));
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }

        return teammates;
    }

    private static Teammate parseTeammate(String line) {
        String[] split = line.split(";");

        String ID = split[0];
        String picLink = split[1];

        ArrayList<String> tournaments = new ArrayList<>();

        for(int i = 2; i<split.length; i++) {
            tournaments.add(split[i]);
        }

        return new Teammate(ID, tournaments, picLink);
    }
}