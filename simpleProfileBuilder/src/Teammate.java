import java.util.ArrayList;

public class Teammate {
    String profileID;
    ArrayList<String> tournaments;
    String pictureLink;

    public Teammate(String profileID, ArrayList<String> tournaments, String pictureLink) {
        this.profileID = profileID;
        this.tournaments = tournaments;
        this.pictureLink = pictureLink;
    }

    @Override
    public String toString() {

        String tournamentString = "";

        for(String s : tournaments) {
            tournamentString+= ";" + s;
        }


        return profileID + ";" + pictureLink + tournamentString;
    }
}
