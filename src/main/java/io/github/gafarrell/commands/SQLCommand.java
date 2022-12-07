package io.github.gafarrell.commands;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLCommand {
    protected static final String BLACK =   "\u001B[30m";
    protected static final String RED =     "\u001B[31m";
    protected static final String GREEN =   "\u001B[32m";
    protected static final String YELLOW =  "\u001B[33m";
    protected static final String BLUE =    "\u001B[34m";
    protected static final String PURPLE =  "\u001B[35m";
    protected static final String CYAN =    "\u001B[36m";
    protected static final String WHITE =   "\u001B[37m";


    protected boolean successful = false;
    protected String commandMessage = "";

    public abstract boolean execute() throws Exception;

    public String getCommandMessage() {
        return commandMessage;
    }
    public static boolean compare(Object obj1, Object obj2, String operator){
        if (obj1.getClass() != obj2.getClass()){
            return false;
        }

        if (obj1 instanceof String){
            switch (operator) {
                case "=" -> {
                    return obj1.equals(obj2);
                }
                case "!=" -> {
                    return !obj1.equals(obj2);
                }
                default -> {return false;}
            }
        }

        else if (obj1 instanceof Number casted1 && obj2 instanceof Number casted2){
            switch (operator) {
                case "=" -> {
                    if (casted1.equals(casted2)) return true;
                }
                case "<=" -> {
                    if (casted1 instanceof Float && casted1.floatValue() <= casted2.floatValue()) return true;
                    if (casted1.intValue() <= casted2.intValue()) return true;
                }
                case ">=" -> {
                    if (casted1 instanceof Float && casted1.floatValue() >= casted2.floatValue()) return true;
                    if (casted1.intValue() <= casted2.intValue()) return true;
                }
                case "<" -> {
                    if (casted1 instanceof Float && casted1.floatValue() < casted2.floatValue()) return true;
                    if (casted1.intValue() <= casted2.intValue()) return true;
                }
                case ">" -> {
                    if (casted1 instanceof Float && casted1.floatValue() > casted2.floatValue()) return true;
                    if (casted1.intValue() <= casted2.intValue()) return true;
                }
                default -> {
                    return false;
                }
            }
        }

        return false;
    }
}
