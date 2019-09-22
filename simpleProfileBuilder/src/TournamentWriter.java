import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TournamentWriter {
    public void writeTournaments(List<Tournament> tournaments, String path) throws IOException {
        List<String> data = new ArrayList<>();

        tournaments.forEach(p -> data.add(p.toString()));

        Path file = Paths.get(path);
        Files.write(file, data, Charset.forName("UTF-8"));
    }
}
