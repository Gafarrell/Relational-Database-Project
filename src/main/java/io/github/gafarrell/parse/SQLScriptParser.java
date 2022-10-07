package io.github.gafarrell.parse;

import io.github.gafarrell.commands.CloseProgramCmd;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.commands.creation.DatabaseCreateCmd;
import io.github.gafarrell.commands.creation.DatabaseDropCmd;
import io.github.gafarrell.commands.creation.TableCreateCmd;
import io.github.gafarrell.commands.creation.TableDropCmd;
import io.github.gafarrell.commands.modification.TableAlterCmd;
import io.github.gafarrell.commands.query.SelectCmd;
import io.github.gafarrell.commands.query.UseCmd;
import io.github.gafarrell.database.DatabaseConnector;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLScriptParser {
    private static Pattern sqlTokenizer = Pattern.compile("([\\w\\d*]+)+;?");
    private static Pattern variableExtractor = Pattern.compile("\\((.+)\\)");
    private static Pattern alterVariableExtractor = Pattern.compile(" ([\\w\\d]+) (ADD|DROP) (.+)");

    private BufferedReader reader;
    private File fileToParse;
    private ArrayList<SQLCommand> commands = new ArrayList<>();

    public SQLScriptParser(File file) throws Exception {
        System.out.println("Parsing file.");
        fileToParse = file;
        if (file.exists())
        {
            reader = new BufferedReader(new FileReader(fileToParse));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase(".exit")){
                    commands.add(new CloseProgramCmd());
                    return;
                }
                if (line.contains("--")) line = line.substring(0, line.indexOf("--"));
                if (line.trim().isEmpty()) continue;
                if (!line.contains(";")) throw new Exception("Missing semicolon from script!");
                line = line.replace(";", "");
                parseCommand(line);
            }
            reader.close();
        }
        else throw new Exception("File " + file.getName() + " does not exist.");
    }

    public SQLScriptParser(String s) throws Exception {
        parseCommand(s);
    }

    private void parseCommand(String s) throws Exception {
        ArrayList<String> variables = new ArrayList<>();
        Matcher parameters = variableExtractor.matcher(s);

        Matcher alterMatcher = alterVariableExtractor.matcher(s.toUpperCase());
        if (alterMatcher.find()){
            variables.add(s.substring(alterMatcher.start(1), alterMatcher.end(1)));
            variables.add(alterMatcher.group(2));
            variables.addAll(Arrays.asList(s.substring(alterMatcher.start(3), alterMatcher.end(3)).replace("(", " ").replace(")", "").split(",")));
            s = s.substring(0, 12).trim();
        }
        else if (parameters.find()) {
            String[] vars = parameters.toMatchResult().group(1).split(",");
            variables = new ArrayList<>(Arrays.asList(vars));
            s = parameters.replaceAll("");
            for (int i = 0; i < variables.size(); i++){
                variables.set(i, variables.get(i).replace("("," ").replace(")"," ").trim());
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
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new TableCreateCmd(variables));
                    }
                    else if (cmdTokens.get(1).group().equalsIgnoreCase("database")) {
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new DatabaseCreateCmd(variables));
                    }
                    else {
                        System.out.println("!Failed: Invalid command syntax");
                    }
                    break;

                case "USE":
                    commands.add(new UseCmd(cmdTokens.get(1).group()));
                    break;
                case "DROP":
                    if (cmdTokens.get(1).group().equalsIgnoreCase("database")){
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new DatabaseDropCmd(variables));
                    }
                    else if (cmdTokens.get(1).group().equalsIgnoreCase("table")){
                        variables.add(cmdTokens.get(2).group());
                        commands.add(new TableDropCmd(variables));
                    }
                    else {
                        System.out.println("!Failed: Invalid command syntax");
                    }
                    break;

                case "SELECT":
                    if (cmdTokens.get(1).group().equalsIgnoreCase("*")){
                        if (cmdTokens.get(2).group().equalsIgnoreCase("from")){
                            variables.add("*");
                            variables.add(cmdTokens.get(3).group());
                            commands.add(new SelectCmd(variables));
                        }
                    }
                    else {
                        System.out.println("!Failed: Specified select not yet implemented.");
                    }
                    break;
                case "ALTER":
                    commands.add(new TableAlterCmd(variables));
                    break;
                case "EXIT":
                    commands.add(new CloseProgramCmd());
                default:
                    System.out.println("Command not valid!");
                    break;
            }
        }
    }

    public void execute() throws Exception {
        for (SQLCommand command : commands){
            try {
                command.execute();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        commands.clear();
    }
}
