package classes.listnodes;

import classes.innernodes.VarDeclNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class VarDeclList {

    private ArrayList<VarDeclNode> varDeclList;

    public VarDeclList() {
        this.varDeclList = new ArrayList<>();
    }

    public void addVarDeclNode(VarDeclNode varDeclNode) {
        this.varDeclList.add(varDeclNode);
    }

    public ArrayList<VarDeclNode> getVarDeclList() {
        return varDeclList;
    }
}
