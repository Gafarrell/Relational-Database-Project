package io.github.gafarrell.commands;

public class CloseProgramCmd extends SQLCommand{
    @Override
    public boolean execute() throws Exception {
        System.out.println("All done.");
        System.exit(0);
        return true;
    }
}
