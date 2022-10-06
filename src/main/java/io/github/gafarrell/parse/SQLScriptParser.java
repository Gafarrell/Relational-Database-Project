package io.github.gafarrell.parse;

import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.commands.creation.DatabaseCreateCmd;
import io.github.gafarrell.commands.creation.DatabaseRemoveCmd;
import io.github.gafarrell.commands.creation.TableCreateCmd;
import io.github.gafarrell.database.DatabaseConnector;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLScriptParser {
    private static Pattern sqlTokenizer = Pattern.compile("([\\w\\d]+)+;?");
    private static Pattern variableExtractor = Pattern.compile("\\((.+)\\)");

    private BufferedReader reader;
    private File fileToParse;
    private ArrayList<SQLCommand> commands = new ArrayList<>();

    public SQLScriptParser(File file) throws Exception {
        fileToParse = file;
        if (file.exists())
        {
            reader = new BufferedReader(new FileReader(fileToParse));
        }
        else throw new Exception("File " + file.getName() + " does not exist.");
    }

    public SQLScriptParser(String s) throws Exception {
        System.out.println("Reading: " + s);
        Matcher parameters = variableExtractor.matcher(s);
        ArrayList<String> variables = new ArrayList<>();

        if (parameters.find()) {
            String[] vars = parameters.toMatchResult().group(1).split(",");
            variables = new ArrayList<>(Arrays.asList(vars));
            s = parameters.replaceAll("");
            for (int i = 0; i < variables.size(); i++){
                variables.set(i, variables.get(i).replace("(","").replace(")",""));
            }
        }


        Matcher command = sqlTokenizer.matcher(s);


        if (!command.results().toList().isEmpty()){
            command.reset();
            List<MatchResult> cmdTokens = command.results().toList();

            switch (cmdTokens.get(0).group().toUpperCase()){
                case "CREATE":
                    if (cmdTokens.size() != 3) return;
                    if (cmdTokens.get(1).group().equalsIgnoreCase("table")) {
                        System.out.println("Adding variables...");
                        variables.add(cmdTokens.get(2).group());
                        System.out.println("Creating table...");
                        commands.add(new TableCreateCmd(variables));
                    }
                    if (cmdTokens.get(1).group().equalsIgnoreCase("database")) {
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new DatabaseCreateCmd(variables));
                    }
                    break;

                case "USE":
                    if (DatabaseConnector.getInstance().use(cmdTokens.get(1).group())){
                        System.out.println("Using " + cmdTokens.get(1).group());
                    }
                    break;
                case "DROP":
                    if (cmdTokens.get(1).group().equalsIgnoreCase("database")){
                        System.out.println("Dropping DB");
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new DatabaseRemoveCmd(variables));
                    }
                    break;

                case "SELECT":
                case "ALTER":
                    System.out.println("Command not yet implemented!");
                    break;
                default:
                    System.out.println("Command not valid!");
                    break;
            }
        }
    }

    public void execute() throws Exception {
        for (SQLCommand command : commands){
            command.execute();
        }
        commands.clear();
    }
}
