package io.github.gafarrell.parse;

import io.github.gafarrell.Debug;
import io.github.gafarrell.commands.CloseProgramCmd;
import io.github.gafarrell.commands.SQLCommand;
import io.github.gafarrell.commands.creation.DatabaseCreateCmd;
import io.github.gafarrell.commands.creation.DatabaseDropCmd;
import io.github.gafarrell.commands.creation.TableCreateCmd;
import io.github.gafarrell.commands.creation.TableDropCmd;
import io.github.gafarrell.commands.locking.BeginTransactionCmd;
import io.github.gafarrell.commands.locking.CommitCmd;
import io.github.gafarrell.commands.modification.DeleteCmd;
import io.github.gafarrell.commands.modification.InsertCmd;
import io.github.gafarrell.commands.modification.TableAlterCmd;
import io.github.gafarrell.commands.modification.UpdateCmd;
import io.github.gafarrell.commands.query.SelectCmd;
import io.github.gafarrell.commands.query.UseCmd;
import io.github.gafarrell.database.DatabaseConnector;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLScriptParser {

    private enum SQLCommandTokenizer{
        CREATE(Pattern.compile("CREATE (DATABASE|TABLE) (.+?) ?(\\((.+)\\)|$)", Pattern.CASE_INSENSITIVE)),
        USE(Pattern.compile("USE (.+)", Pattern.CASE_INSENSITIVE)),
        DELETE(Pattern.compile("DELETE FROM (.+) WHERE (.+)", Pattern.CASE_INSENSITIVE)),
        DROP(Pattern.compile("DROP (DATABASE|TABLE) (.+)", Pattern.CASE_INSENSITIVE)),
        SELECT(Pattern.compile("SELECT (.+) FROM (.+?) ?(WHERE|$)(.+)?", Pattern.CASE_INSENSITIVE)),
        ALTER(Pattern.compile("ALTER TABLE (.+) (ADD|DROP) (.+)", Pattern.CASE_INSENSITIVE)),
        UPDATE(Pattern.compile("UPDATE (.+) SET (.+) WHERE (.+)", Pattern.CASE_INSENSITIVE)),
        INSERT(Pattern.compile("INSERT INTO (.+) values\\((.+)\\)", Pattern.CASE_INSENSITIVE)),
        BEGIN_TRANSACTION(Pattern.compile("BEGIN TRANSACTION", Pattern.CASE_INSENSITIVE)),
        COMMIT(Pattern.compile("COMMIT", Pattern.CASE_INSENSITIVE));

        public final Pattern pattern;
        SQLCommandTokenizer(Pattern pattern){this.pattern = pattern;}
    }
    private final ArrayList<SQLCommand> commands = new ArrayList<>();

    public SQLScriptParser(File file) throws Exception {
        System.out.println("Parsing file.");
        if (file.exists())
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
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

        for (SQLCommandTokenizer tokenizer : SQLCommandTokenizer.values()){
            Matcher matcher = tokenizer.pattern.matcher(s);
            if (!matcher.matches()) continue;

            DatabaseConnector.getInstance().updateDatabases();

            Debug.writeLine(tokenizer);
            switch (tokenizer){
                case USE -> {
                    String dbName = matcher.group(1);
                    commands.add(new UseCmd(dbName));
                }

                case DROP -> {
                    if (matcher.group(1).equalsIgnoreCase("database"))
                        commands.add(new DatabaseDropCmd(matcher.group(2)));
                    else if (matcher.group(1).equalsIgnoreCase("table"))
                        commands.add(new TableDropCmd(matcher.group(2)));
                }

                case ALTER -> {
                    String dbName = matcher.group(1);
                    List<String> alterParameters = new ArrayList<>();
                    alterParameters.add(matcher.group(2));
                    alterParameters.add(matcher.group(3));
                    commands.add(new TableAlterCmd(dbName, alterParameters));
                }

                case CREATE -> {
                    if (matcher.group(1).equalsIgnoreCase("database"))
                        commands.add(new DatabaseCreateCmd(matcher.group(2)));
                    else if (matcher.group(1).equalsIgnoreCase("table")){
                        String tableName = matcher.group(2);
                        List<String> parameters = new ArrayList<>();
                        Debug.writeLine(matcher.groupCount());
                        if (matcher.group(4) != null) parameters = Arrays.asList(matcher.group(4).split(","));
                        commands.add(new TableCreateCmd(tableName, parameters));
                    }
                }

                case UPDATE -> {
                    commands.add(new UpdateCmd(matcher.group(1), matcher.group(2), matcher.group(3)));
                }

                case DELETE ->{
                    commands.add(new DeleteCmd(matcher.group(1), matcher.group(2)));
                }

                case SELECT -> {
                    commands.add(new SelectCmd(matcher.group(2), matcher.group(1), matcher.groupCount() == 4 ? matcher.group(4) : ""));
                }

                case INSERT -> {
                    commands.add(new InsertCmd(matcher.group(1), Arrays.asList(matcher.group(2).split(","))));
                }

                case COMMIT -> {
                    commands.add(new CommitCmd());
                }

                case BEGIN_TRANSACTION -> {
                    commands.add(new BeginTransactionCmd());
                }
            }
        }
    }

    public void executeAllCommands(){
        for (SQLCommand command : commands) {
            try {
                command.execute();
                System.out.println(command.getCommandMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        commands.clear();
    }
}
