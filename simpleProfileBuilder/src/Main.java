//TODO Adds tournaments to people that didnt play a tournament sometimes, can manually fix?
//just added SMT Winter 2018

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import jdk.nashorn.internal.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Filbehandling.oppdaterTournaments();
        Filbehandling.oppdaterTeammates();

        readCommand();
    }

    public static void readCommand() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Write a command");

        String command = reader.readLine();

        if(command.equals("new"))
        {
            newTournament();
        } else if(command.equals("finish"))
        {
            endTournament();
        }  else if (command.equals("teammates"))
        {
            updateTeammates();
        } else if (command.equals("print"))
        {
            writeText();
        } else
        {
            System.out.println("This is not a command");
            readCommand();
        }
    }

    public static void writeText() throws IOException
    {
        String yellow = "#FFF6A7";
        String pink = "#FF78CB";

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<ProcessedPlayer> ProcessedPlayers = new ArrayList<>();

        System.out.println("Type 'confirm' to start printing userpage. There are currently " + Filbehandling.tournaments.size() + " tournaments" +
                " and " + Filbehandling.teammates.size() + " teammates to process!");

        String confirm = reader.readLine();

        if(confirm.equals("confirm"))
        {
            System.out.println("Print started!");

            String userpage = "[box=Tournaments]\n";
            userpage += "[color=" + yellow + "]Total amount of tournaments signed up for: [b]" + Filbehandling.tournaments.size() + "[/b] (Not counting weekly tournaments)[/color]\n\n";

            String ongoing = "[color=" + pink + "][b]Ongoing Tournaments[/b][/color]";

            //Loop through finished tournaments and write them
            for(int i = 0; i < Filbehandling.tournaments.size(); i++)
            {
                if(!Filbehandling.tournaments.get(i).position.equals("ongoing"))
                {
                    userpage +="[centre][color=" + pink + "][b]" + Filbehandling.tournaments.get(i).name + "[/b] - [/color]";

                    //Colors for position
                    if(Filbehandling.tournaments.get(i).position.equals("1st Place"))
                    {
                        userpage+="[color=#ffd700]1st Place[/color]";
                    } else if (Filbehandling.tournaments.get(i).position.equals("2nd Place"))
                    {
                        userpage+="[color=#c0c0c0]2nd Place[/color]";
                    } else if (Filbehandling.tournaments.get(i).position.equals("3rd Place"))
                    {
                        userpage+="[color=#cd7f32]3rd Place[/color]";
                    } else
                    {
                        userpage+="[color=" + pink + "]" + Filbehandling.tournaments.get(i).position + "[/color]";
                    }


                    //Players
                    userpage+="[/centre][color=" + pink + "][b]Players:[/b][/color]";
                    userpage+="\n[list][*][img]https://s.ppy.sh/images/flags/no.gif[/img] [profile] Markus[/profile]";

                    //Write players
                    for(int o = 0; o < Filbehandling.tournaments.get(i).players.size(); o++)
                    {
                        ProcessedPlayer player = getPlayer(ProcessedPlayers, Filbehandling.tournaments.get(i).players.get(o));
                        userpage+="[*][img]https:" + player.flagLink + "[/img][profile] " + player.name + "[/profile]";
                    }

                    //Write notes
                    userpage+="[/list]\n[color=" + pink + "][b]Notes:[/b][/color]";
                    userpage+="\n[color=" + yellow + "]" + Filbehandling.tournaments.get(i).notes + "[/color]";


                    //End
                    userpage+="\n\n\n\n\n\n";
                } else
                {
                    //Ongoing tournaments
                    ongoing += "\n\n\n\n\n\n[color=" + pink + "][b]" + Filbehandling.tournaments.get(i).name + "[/b]";
                    ongoing += "\n[b]Players:[/b][/color]";
                    ongoing += "\n[list][*][img]https://s.ppy.sh/images/flags/no.gif[/img] [profile] Markus[/profile]";

                    //Write players
                    for(int o = 0; o < Filbehandling.tournaments.get(i).players.size(); o++)
                    {
                        ProcessedPlayer player = getPlayer(ProcessedPlayers, Filbehandling.tournaments.get(i).players.get(o));
                        ongoing+="\n[*][img]https:" + player.flagLink + "[/img][profile] " + player.name + "[/profile]";
                    }
                    ongoing+="[/list]";
                }

                System.out.println("Tournaments: " + i + "/" + Filbehandling.tournaments.size());
            }

            System.out.println("Tournaments finished");

            //Add ongoing
            userpage += ongoing;

            //End of tournaments
            userpage += "\n[/box]";

            //Sort players by amount of tourneys then by name
            Collections.sort(ProcessedPlayers, new Comparator<ProcessedPlayer>()
            {
                @Override
                public int compare(ProcessedPlayer o1, ProcessedPlayer o2)
                {
                    if(o1.tourneyAmount==o2.tourneyAmount)
                    {
                        return o1.name.compareTo(o2.name);
                    } else
                    {
                        return Integer.valueOf(o2.tourneyAmount).compareTo(o1.tourneyAmount);
                    }
                }
            });

            //Start of Teammates

            userpage += "\n[box=Teammates]\n[centre]";
            userpage += "\n[color=" + yellow + "]People I have played with in tournaments, and how many times.[/color]\n\n";

            int oneTourneyPlayersStart = 0;

            //Write player list multiple tourneys
            for(int i = 0; i < ProcessedPlayers.size(); i++)
            {
                if(ProcessedPlayers.get(i).tourneyAmount>1)
                {
                    userpage+="\n[color=" + pink + "]________________________________________________________________[/color]\n\n";
                    userpage+="[url=https://osu.ppy.sh/users/" + ProcessedPlayers.get(i).playerID + "]";
                    userpage+="[img]" + ProcessedPlayers.get(i).picLink + "[/img][/url]";
                    userpage+="[heading]" + ProcessedPlayers.get(i).name + " - " + ProcessedPlayers.get(i).tourneyAmount + " times[/heading]";
                    userpage+="[color=" + pink + "][b]Tournaments we played:[/b][/color]\n[color=" + yellow + "]";

                    //Write tournaments for player
                    for(int o = 0; o < ProcessedPlayers.get(i).tourneys.size(); o++)
                    {
                        userpage += ProcessedPlayers.get(i).tourneys.get(o) + "\n";
                    }
                } else
                {
                    oneTourneyPlayersStart = i;
                    break;
                }

            }

            userpage+="[/color][color=" + pink + "]________________________________________________________________[/color]\n\n";
            userpage+="[u][b]People I have only played with once:[/b][/u]\n";

            //Write players list one tourney
            for(int i = oneTourneyPlayersStart; i<ProcessedPlayers.size(); i++)
            {
                userpage += "[profile]" + ProcessedPlayers.get(i).name + "[/profile][color=" + yellow + "] - " + ProcessedPlayers.get(i).tourneys.get(0) + "[/color]\n";
            }

            //End of Teammates
            userpage += "\n[/centre]\n[/box]";



            //Start of stats
            userpage+="\n[box=Tournament Statistics]";


            userpage+="\n[color=" + yellow + "]Statistics only works for tournaments where the results are clear e.g. 1st, 2nd, 3rd, 4th Place or Top X[/color]";

            //Amount of finishes in top 3
            userpage+="\n\n\n[color=" + pink + "][b]Amount of top 3 finishes:[/b][/color]";
            userpage+="\n[color=" + yellow + "]1st place finishes - " + getTotalPosition("1st Place");
            userpage+="\n2nd place finishes - " + getTotalPosition("2nd Place");
            userpage+="\n3rd place finishes - " + getTotalPosition("3rd Place") + "[/color]";

            //Average positions
            //1v1
            userpage+="\n\n\n\n[color=" + pink + "][b]Average finishes in 1v1 tournaments:[/b][/color]";
            userpage+="\n[color=" + yellow + "]Past 5 tournaments - Top " + getAverageTourneyplacement1v1(5);
            userpage+="\nPast 10 tournaments - Top " + getAverageTourneyplacement1v1(10);
            userpage+="\nTotal - Top " + getAverageTourneyplacement1v1(Filbehandling.tournaments.size()) + "[/color]";

            //Team
            userpage+="\n\n[color=" + pink + "][b]Average finishes in team tournaments:[/b][/color]";
            userpage+="\n[color=" + yellow + "]Past 5 tournaments - Top " + getAverageTourneyplacementTeam(5);
            userpage+="\nPast 10 tournaments - Top " + getAverageTourneyplacementTeam(10);
            userpage+="\nTotal - Top " + getAverageTourneyplacementTeam(Filbehandling.tournaments.size()) + "[/color]";

            //All tournaments
            userpage+="\n\n[color=" + pink + "][b]Average finishes in all tournaments:[/b][/color]";
            userpage+="\n[color=" + yellow + "]Past 5 tournaments - Top " + getAverageTourneyplacement(5);
            userpage+="\nPast 10 tournaments - Top " + getAverageTourneyplacement(10);
            userpage+="\nTotal - Top " + getAverageTourneyplacement(Filbehandling.tournaments.size()) + "[/color]";


            //Top 5 teammates
            userpage+="\n\n\n\n[color=" + pink + "][b]Top 5 teammates based on average finishes: (Only people I have teamed with twice or more)[/b][/color]";

            ArrayList<TeammatePlacements> teammatePlacements = getArrayOfTeammatePlacements(ProcessedPlayers);


            userpage+="[color=" + yellow + "]";
            //Write top 5 teammates
            for(int i = 1; i < 6; i++)
            {
                userpage+="\n[b]" + i + ". " + teammatePlacements.get(i-1).parent.name + "[/b] - Top " + teammatePlacements.get(i-1).avgPosition + " average";
            }
            userpage+="[/color]";



            //End of stats
            userpage+="\n[/box]";

            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write(userpage);

            writer.close();

            System.out.println("Print finished!");
        }
    }

    public static ProcessedPlayer getPlayer(ArrayList<ProcessedPlayer> ProcessedPlayers, String id) {
        Teammate currentPlayer = new Teammate("",new ArrayList<String>(),"");

        //Get current player
        for(int i = 0; i < Filbehandling.teammates.size(); i++)
        {
            if(Filbehandling.teammates.get(i).profileID.equals(id))
            {
                currentPlayer = Filbehandling.teammates.get(i);
                break;
            }
        }

        //Check if player is in processed players
        for(int i = 0; i < ProcessedPlayers.size(); i++)
        {
            if(ProcessedPlayers.get(i).playerID.equals(id))
            {
                return ProcessedPlayers.get(i);
            }
        }

        ProcessedPlayer processedPlayer = new ProcessedPlayer(id, getUsername(id), getFlag(id), currentPlayer.pictureLink, currentPlayer.tournaments.size(), currentPlayer.tournaments);

        ProcessedPlayers.add(processedPlayer);

        return processedPlayer;
    }

    public static void updateTeammates() throws IOException
    {
        int counter = 0;

        System.out.println("Teammates I have teamed with twice or more:");

        for(int i = 0; i < Filbehandling.teammates.size(); i++)
        {
            if(Filbehandling.teammates.get(i).tournaments.size()>1)
            {
                counter++;
                if(Filbehandling.teammates.get(i).pictureLink.equals(""))
                {
                    System.out.println(ConsoleColors.RED + counter + ". " + getUsername(Filbehandling.teammates.get(i).profileID) + " - No image added!" + ConsoleColors.RESET);
                } else
                {
                    System.out.println(counter + ". " + getUsername(Filbehandling.teammates.get(i).profileID) + " - " + Filbehandling.teammates.get(i).pictureLink);
                }
            }
        }

        if(Filbehandling.teammates.size()==0)
        {
            System.out.println("You have not played with anyone twice or more");
        } else
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose a player to update image for");

            String player = reader.readLine();
            int playerParsed = Integer.parseInt(player);

            int numberFound = 0;

            for(int i = 0; i < Filbehandling.teammates.size(); i++)
            {
                if(Filbehandling.teammates.get(i).tournaments.size()>1) {
                    if(numberFound==playerParsed-1)
                    {
                        numberFound=i;
                        break;
                    }
                    numberFound++;
                }
            }

            System.out.println(ConsoleColors.CYAN + getUsername(Filbehandling.teammates.get(numberFound).profileID) + ConsoleColors.RESET);

            System.out.println("Insert the link to the image you wish to use");

            String link = reader.readLine();

            Filbehandling.teammates.get(numberFound).pictureLink = link;


            //Saves teammate to file
            TeammateWriter writer = new TeammateWriter();
            try
            {
                writer.writeTeammates(Filbehandling.teammates, "src/Teammates.csv");
            } catch (IOException e)
            {
                System.out.println("ERROR");
            }

            System.out.println("Link updated!");
        }


        try
        {
            readCommand();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void endTournament() throws IOException
    {
        System.out.println("Choose and ongoing tournament to end: ");

        int counter = 0;

        for(int i = 0; i < Filbehandling.tournaments.size(); i++)
        {
            if(Filbehandling.tournaments.get(i).position.equals("ongoing"))
            {
                counter++;
                System.out.println(counter + ". " + Filbehandling.tournaments.get(i).name);

            }
        }

        if(counter==0)
        {
            System.out.println("There are currently no ongoing tournaments");
        } else
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String number = reader.readLine();
            int parsedNumber = Integer.parseInt(number);

            int numberFound = 0;

            for(int i = 0; i < Filbehandling.tournaments.size(); i++)
            {
                if(Filbehandling.tournaments.get(i).position.equals("ongoing")) {
                    if(numberFound==parsedNumber-1)
                    {
                        numberFound=i;
                        break;
                    }
                    numberFound++;
                }
            }

            System.out.println(ConsoleColors.CYAN + Filbehandling.tournaments.get(numberFound).name + ConsoleColors.RESET);

            System.out.println("Write your position in the tournament");
            String position = reader.readLine();

            System.out.println("Write notes about the tournament");
            String notes = reader.readLine();


            System.out.println("Tournament: " + ConsoleColors.CYAN + Filbehandling.tournaments.get(numberFound).name + ConsoleColors.RESET);
            System.out.println("Position: " + ConsoleColors.CYAN + position + ConsoleColors.RESET);
            System.out.println("Notes: " + ConsoleColors.CYAN + notes + ConsoleColors.RESET);
            System.out.println("Type 'confirm' to end this tournament with these settings.");

            String confirmation = reader.readLine();

            if(confirmation.equals("confirm"))
            {
                Filbehandling.tournaments.get(numberFound).position = position;
                Filbehandling.tournaments.get(numberFound).notes = notes;

                //Saves tournament to file
                TournamentWriter writer = new TournamentWriter();
                try
                {
                    writer.writeTournaments(Filbehandling.tournaments, "src/Tournaments.csv");
                } catch (IOException e)
                {
                    System.out.println("ERROR");
                }

                System.out.println("Tournament updated");
            } else
            {
                System.out.println("Aborted");
            }


            try
            {
                readCommand();
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }

    public static void newTournament() throws IOException
    {
        /*
            Reads information about the tournament: Name, players
         */

        //Name
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the name of the tournament");

        String TourneyName = reader.readLine();

        System.out.println(ConsoleColors.CYAN + TourneyName + ConsoleColors.RESET);

        //Player amount
        System.out.println("How many players on your team?");
        int playerAmount = Integer.parseInt(reader.readLine());

        System.out.println(ConsoleColors.CYAN + playerAmount + ConsoleColors.RESET);

        ArrayList<String> players = new ArrayList<>();

        //Players
        if(playerAmount>1)
        {
            System.out.println("Enter player IDs for each player");

            System.out.println(ConsoleColors.CYAN + "Markus");

            //Read all userIDs and write their names
            for (int i = 0; i < playerAmount - 1; i++)
            {
                String player = reader.readLine();

                //Write out username
                System.out.println(getUsername(player));

                players.add(player);
            }
        }



            /*
                Prints confirmation text
             */

            System.out.println(ConsoleColors.RESET + "Tournament name: " + ConsoleColors.CYAN + TourneyName + ConsoleColors.RESET);
            System.out.println("Players:");
            System.out.println(ConsoleColors.CYAN + "Markus");

            //Usernames
            for(int i = 0; i < players.size(); i++)
            {
                System.out.println(getUsername(players.get(i)));
            }

            //Check confirmation
            System.out.println(ConsoleColors.RESET + "Type 'confirm' to register this tournament");

            String confirm = reader.readLine();

            if(confirm.equals("confirm"))
            {
                registerTournament(TourneyName, players);
                System.out.println("Tournament added");
            } else
            {
                System.out.println("Aborted");
            }

            try
            {
                readCommand();
            } catch (IOException e) {
                System.out.println("Error");
            }
    }


    public static void registerTournament(String name, ArrayList<String> players)
    {
        //Creates object for tournament
        Tournament tourney = new Tournament(name, players, "ongoing", "");

        Filbehandling.tournaments.add(tourney);

        TournamentWriter writer = new TournamentWriter();

        //Saves tournament to file
        try
        {
            writer.writeTournaments(Filbehandling.tournaments, "src/Tournaments.csv");
        } catch (IOException e)
        {
            System.out.println("ERROR");
        }

        ArrayList<String> tournament = new ArrayList<>();
        tournament.add(name);


        for(int o = 0; o < players.size(); o++)
        {
            for(int i = 0; i < Filbehandling.teammates.size(); i++)
            {
                if(players.get(o).equals(Filbehandling.teammates.get(i).profileID))
                {
                    Filbehandling.teammates.get(i).tournaments.add(name);
                    break;
                } else if(i==Filbehandling.teammates.size()-1)
                {
                    Filbehandling.teammates.add(new Teammate(players.get(o), tournament, ""));
                    break;
                }
            }

            if(Filbehandling.teammates.size()==0)
            {
                Filbehandling.teammates.add(new Teammate(players.get(0), tournament, ""));
            }
        }


        TeammateWriter writerT = new TeammateWriter();

        try
        {
            writerT.writeTeammates(Filbehandling.teammates, "src/Teammates.csv");
        } catch (IOException e)
        {
            System.out.println("ERROR");
        }
    }


    //Get username from user ID
    public static String getUsername (String uID) {
        String username = "";

        try
        {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e)
        {
            System.out.println("Waiting for a second was interrupted");
        }
        try
        {
            Document doc = Jsoup.connect("https://old.ppy.sh/u/" + uID).get();
            username = doc.select(".profile-username").text();
        } catch (IOException e)
        {
            System.out.println("An error has accored while reading username");
        }

        return username;
    }


    //Get flag from userID
    public static String getFlag (String uID)
    {
        String flag = "";

        try
        {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e)
        {
            System.out.println("Waiting for a second was interrupted");
        }

        try
        {
            Document doc = Jsoup.connect("https://old.ppy.sh/u/" + uID).get();
            flag = doc.select(".flag").attr("src");
        } catch (IOException e)
        {
            System.out.println("An error occured while reading flag");
        }

        return flag;
    }

    public static double getAverageTourneyplacement(int amount)
    {
        double amountfound = 0;
        double total = 0;

        for(int i = Filbehandling.tournaments.size()-1; i>0; i--)
        {
            if(amountfound==amount) break;

            Tournament t = Filbehandling.tournaments.get(i);

            if(t.position.equals("1st Place"))
            {
                total+=1;
                amountfound++;
            } else if (t.position.equals("2nd Place"))
            {
                total+=2;
                amountfound++;
            } else if (t.position.equals("3rd Place"))
            {
                total+=3;
                amountfound++;
            } else if (t.position.equals("4th Place"))
            {
                total+=4;
                amountfound++;
            } else
            {
                String[] split = t.position.split(" ");
                if(split[0].equals("Top"))
                {
                    total+=Integer.parseInt(split[1]);
                    amountfound++;
                }
            }
        }

        return round(total/amountfound,2);
    }

    public static double getAverageTourneyplacement1v1(int amount)
    {
        double amountfound = 0;
        double total = 0;

        for(int i = Filbehandling.tournaments.size()-1; i>0; i--)
        {
            if(amountfound==amount) break;

            Tournament t = Filbehandling.tournaments.get(i);

            if(t.players.size()==0)
            {
                if(t.position.equals("1st Place"))
                {
                    total+=1;
                    amountfound++;
                } else if (t.position.equals("2nd Place"))
                {
                    total+=2;
                    amountfound++;
                } else if (t.position.equals("3rd Place"))
                {
                    total+=3;
                    amountfound++;
                } else if (t.position.equals("4th Place"))
                {
                    total+=4;
                    amountfound++;
                } else
                {
                    String[] split = t.position.split(" ");
                    if(split[0].equals("Top"))
                    {
                        total+=Integer.parseInt(split[1]);
                        amountfound++;
                    }
                }
            }
        }

        return round(total/amountfound,2);
    }



    public static double getAverageTourneyplacementTeam(int amount)
    {
        double amountfound = 0;
        double total = 0;

        for(int i = Filbehandling.tournaments.size()-1; i>0; i--)
        {
            if(amountfound==amount) break;

            Tournament t = Filbehandling.tournaments.get(i);

            if(t.players.size()!=0)
            {
                if(t.position.equals("1st Place"))
                {
                    total+=1;
                    amountfound++;
                } else if (t.position.equals("2nd Place"))
                {
                    total+=2;
                    amountfound++;
                } else if (t.position.equals("3rd Place"))
                {
                    total+=3;
                    amountfound++;
                } else if (t.position.equals("4th Place"))
                {
                    total+=4;
                    amountfound++;
                } else
                {
                    String[] split = t.position.split(" ");
                    if(split[0].equals("Top"))
                    {
                        total+=Integer.parseInt(split[1]);
                        amountfound++;
                    }
                }
            }
        }

        return round(total/amountfound,2);
    }

    public static TeammatePlacements getTeammatePlacements (ProcessedPlayer player)
    {
        double total = 0;
        double amountFound = 0;

        for(int i = 0; i < player.tourneys.size(); i++)
        {
            for(int o = 0; o < Filbehandling.tournaments.size(); o++)
            {
                Tournament t = Filbehandling.tournaments.get(o);

                if(player.tourneys.get(i).equals(t.name))
                {
                    if(t.position.equals("1st Place"))
                    {
                        total+=1;
                        amountFound++;
                    } else if (t.position.equals("2nd Place"))
                    {
                        total+=2;
                        amountFound++;
                    } else if (t.position.equals("3rd Place"))
                    {
                        total+=3;
                        amountFound++;
                    } else if (t.position.equals("4th Place"))
                    {
                        total+=4;
                        amountFound++;
                    } else
                    {
                        String[] split = t.position.split(" ");
                        if(split[0].equals("Top"))
                        {
                            total+=Integer.parseInt(split[1]);
                            amountFound++;
                        }
                    }

                    break;
                }
            }
        }

        if(amountFound>1)
        {
            return new TeammatePlacements(round(total/amountFound,2), player);
        } else
        {
            return new TeammatePlacements(0, player);
        }
    }

    public static int getTotalPosition (String compareText)
    {
        int counter = 0;

        for(int i = 0; i < Filbehandling.tournaments.size(); i++)
        {
            if(Filbehandling.tournaments.get(i).position.equals(compareText))
            {
                counter++;
            }
        }

        return counter;
    }

    public static ArrayList<TeammatePlacements> getArrayOfTeammatePlacements (ArrayList<ProcessedPlayer> playerList)
    {
        ArrayList<TeammatePlacements> placementList = new ArrayList<>();

        for(int i = 0; i < playerList.size(); i++)
        {
            if(playerList.get(i).tourneyAmount>1)
            {
                TeammatePlacements player = getTeammatePlacements(playerList.get(i));

                if(player.avgPosition!=0)
                {
                    placementList.add(player);

                }
            } else
            {
                break;
            }
        }

        Collections.sort(placementList, new Comparator<TeammatePlacements>()
        {
            @Override
            public int compare(TeammatePlacements o1, TeammatePlacements o2)
            {
                return Double.valueOf(o1.avgPosition).compareTo(o2.avgPosition);
            }
        });

        return placementList;
    }


    //Rounds doubles
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
