package compiler;

import classes.exceptions.AlreadyDeclearedException;
import classes.exceptions.NotDeclearedException;

import java.util.HashMap;

public class Env {
    public Env(Env nearestEnv, String tableName) {
        this.symTable = new HashMap<>();
        this.nearestEnv = nearestEnv;
        this.tableName = tableName;
        this.tableNumber = Env.tableNumberCurrent++;
    }

    public EnvEntry lookup(String id) throws NotDeclearedException {
        for (Env e = this; e != null; e = e.nearestEnv) {
            EnvEntry found = e.symTable.getOrDefault(id, null);
            if (found != null) return found;
        }
        throw new NotDeclearedException("Semantic Error: Identifier["+id+"] Not Decleared");
    }

    public Env put(String id, EnvEntry entry) throws AlreadyDeclearedException {
        if (this.symTable.containsKey(id))
            throw new AlreadyDeclearedException("Semantic Error: Identifier["+id+"] Already Decleared");
        this.symTable.put(id, entry);
        return this;
    }

    public HashMap<String, EnvEntry> getSymTable() {
        return symTable;
    }

    public void printTables() {
        if (this.nearestEnv != null) {
            this.nearestEnv.printTables();
            System.out.println("===========================================================\n" +
                    "table number: " + this.tableNumber + "; Table name: " + this.tableName +  "; table: " + this.symTable +
                    "; previous: " + this.nearestEnv.tableName + ".\n");
        }else {
            System.out.println("===========================================================\n" +
                    "table number: " + this.tableNumber + "; Table name: " + this.tableName +  "; table: " + this.symTable +
                    "; previous: null.\n");
        }
    }

    public Env getNearestEnv() {
        return nearestEnv;
    }

    private HashMap<String, EnvEntry> symTable;
    private Env nearestEnv;
    private String tableName;
    private static int tableNumberCurrent = 0;
    private int tableNumber;
}
