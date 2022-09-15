package classes.listnodes;

import classes.innernodes.ParDeclNode;
import classes.innernodes.VarDeclNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ParamDeclList {

    private ArrayList<ParDeclNode> parDeclList;

    public ParamDeclList() {
        this.parDeclList = new ArrayList<>();
    }

    public void addNonEmptyParamDeclList(NonEmptyParamDeclList nonEmptyParamDeclList) {
        this.parDeclList.addAll(nonEmptyParamDeclList.getNonEmptyParDeclList());
    }

    public ArrayList<ParDeclNode> getParDeclList() {
        return parDeclList;
    }
}
