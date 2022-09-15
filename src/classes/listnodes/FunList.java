package classes.listnodes;


import classes.innernodes.FunNode;
import classes.innernodes.VarDeclNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class FunList {

    private ArrayList<FunNode> funList;

    public FunList() {
        this.funList = new ArrayList<>();
    }

    public void addFunNode(FunNode funNode) {
        this.funList.add(funNode);
    }

    public ArrayList<FunNode> getFunList() {
        return funList;
    }
}
