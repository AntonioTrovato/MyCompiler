package compiler;

import java.util.ArrayList;

public class EnvEntry {

    private ArrayList<String> types = new ArrayList<>();
    private ArrayList<String> paramTypes = new ArrayList<>();
    private String returnType = "VOID";
    private boolean pointer = false;

    public void setPointer(boolean pointer) {
        this.pointer = pointer;
    }

    public boolean isPointer() {
        return pointer;
    }

    public EnvEntry() {}

    public EnvEntry(String type) {
        this.paramTypes.add(type);
        this.types.add(type);
    }

    public boolean addParamType(String paramType) {
        paramTypes.add(paramType);
        return types.add(paramType);
    }

    public boolean setReturnType(String returnType) {
        this.returnType = returnType;
        return types.add(returnType);
    }

    public ArrayList<String> getParamTypes() {
        return paramTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "compiler.EnvEntry{" +
                "types=" + types +
                '}';
    }
}
