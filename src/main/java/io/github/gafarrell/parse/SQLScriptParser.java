package io.github.gafarrell.parse;

import io.github.gafarrell.commands.SQLCommand;
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
    private static Pattern sqlTokenizer = Pattern.compile("([\\w\\d]+);?");
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

    public SQLScriptParser(String s){
        if (s.indexOf(';') == -1) throw new RuntimeException("Missing semicolon from command!");

        Matcher parameters = variableExtractor.matcher(s);
        ArrayList<String> variables = null;
        if (parameters.find()) {
            variables = (ArrayList<String>) Arrays.stream(parameters.toMatchResult().group(1).split(",")).toList();
            s = parameters.replaceAll("");
            for (String arg : variables){
                // TODO: finalize variable extraction.
            }
        }

        Matcher command = sqlTokenizer.matcher(s);

        if (command.find()){
            List<MatchResult> cmdTokens = command.results().toList();
            switch (cmdTokens.get(0).group().toUpperCase()){
                case "CREATE":
                    if (cmdTokens.get(1).group().equalsIgnoreCase("table")) {
                        assert variables != null;
                        commands.add(new TableCreateCmd(DatabaseConnector.getInstance().getCurrent(), variables));
                    }
            }
        }
    }

    public void nextCommand() throws IOException {
        if (reader.ready())
        {
            // TODO: Implement command queing.
        }
    }
}
