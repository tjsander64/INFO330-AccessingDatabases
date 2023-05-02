import java.sql.*;
import java.util.*;

    // REPO FILE
    // REPO FILE

public class TeamAnalyzer {
    // All the "against" column suffixes:
    static String[] types = {
        "bug","dark","dragon","electric","fairy","fight",
        "fire","flying","ghost","grass","ground","ice","normal",
        "poison","psychic","rock","steel","water"
    };

    public static void main(String... args) throws Exception {
        // Take six command-line parameters
        if (args.length < 6) {
            print("You must give me six Pokemon to analyze");
            System.exit(-1);
        }
        // This bit of JDBC magic I provide as a free gift :-)
        // The rest is up to you.
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:pokemon.db")) {

            /*
            Analyzing 1
            Bulbasaur (grass poison) is strong against ['fire', 'flying', 'ice', 'psychic'] but weak against ['electric', 'fairy', 'fight', 'grass', 'water'] */

            for (String arg : args) {
                System.out.println("Analyzing " + arg);

                //had scope problems, just creating structures off the bat

                String result = "";
                String name = "";
                List<String> pokemonTypes = new ArrayList<>();
                Map<Integer, String> typeMap = new HashMap<>(); // this will link the type columns to their output index
                for(int i = 2; i < types.length; i++) {
                    typeMap.put(i, types[i]);
                }
                List<String> strongList = new ArrayList<>();
                List<String> weakList = new ArrayList<>();

                // get name

                String sql = "SELECT name" + 
                             "FROM pokemon" +
                             "WHERE pokedex_number = " + arg + ";";

                try(Statement statement = con.createStatement()) {
                    try (ResultSet results = statement.executeQuery(sql)) {
                        name = results.getString("name");
                    }
                }

                result += name; 
                
                // get types

                sql = "SELECT pokemon_type.type_id, type.name" + 
                            "FROM pokemon_type, type" +
                            "WHERE pokemon_type.pokemon_id = " + arg + 
                                "AND type.id = pokemon_type.type_id;";
                
                try (Statement statement2 = con.createStatement()) {
                    try (ResultSet results = statement2.executeQuery(sql)) {
                        while(results.next()) {
                            pokemonTypes.add(results.getString("name"));
                        }
                    }  
                }
                result += types.toString();   
                result += " is strong against ";         

                // get list of strengths, weaknesses
        
                      
                sql = "SELECT * " + 
                       "FROM against" +
                       "WHERE type_source_id1 = " + pokemonTypes.get(0) +
                        " AND type_source_id2 = " + pokemonTypes.get(1) + ";";
                    

                 try (Statement statement3 = con.createStatement()) {
                        try (ResultSet results = statement3.executeQuery(sql)) {
                            while(results.next()) {
                                for(int i = 2; i < 20; i++) {
                                    Double currVal = results.getDouble(i);
                                    String currType = typeMap.get(i);
                                    if (currVal > 1) {
                                        strongList.add(currType);
                                    } else if (currVal < 1) {
                                        weakList.add(currType);
                                    }
                                }
                            }
                        }
                    }

                result += strongList.toString();
                result += " but weak against " + weakList.toString();

                System.out.println(result);

                // Analyze the pokemon whose pokedex_number is in "arg"

                // You will need to write the SQL, extract the results, and compare
                // Remember to look at those "against_NNN" column values; greater than 1
                // means the Pokemon is strong against that type, and less than 1 means
                // the Pokemon is weak against that type
            }

            String answer = input("Would you like to save this team? (Y)es or (N)o: ");
            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("YES")) {
                String teamName = input("Enter the team name: ");

                // Write the pokemon team to the "teams" table
                print("Saving " + teamName + " ...");
            }
            else {
                print("Bye for now!");
            }
            }
        }        
    }

    /*
     * These are here just to have some symmetry with the Python code
     * and to make console I/O a little easier. In general in Java you
     * would use the System.console() Console class more directly.
     */
    public static void print(String msg) {
        System.console().writer().println(msg);
    }

    /*
     * These are here just to have some symmetry with the Python code
     * and to make console I/O a little easier. In general in Java you
     * would use the System.console() Console class more directly.
     */
    public static String input(String msg) {
        return System.console().readLine(msg);
    }
}
