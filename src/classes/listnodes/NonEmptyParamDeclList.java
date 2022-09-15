package classes.listnodes;

import classes.innernodes.ParDeclNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class NonEmptyParamDeclList {

    private ArrayList<ParDeclNode> nonEmptyParDeclList;

    public NonEmptyParamDeclList(ParDeclNode parDeclNode) {
        this.nonEmptyParDeclList = new ArrayList<>();
        this.nonEmptyParDeclList.add(parDeclNode);
    }

    public void addParDeclNode(ParDeclNode parDeclNode) {
        this.nonEmptyParDeclList.add(parDeclNode);
    }

    public ArrayList<ParDeclNode> getNonEmptyParDeclList() {
        return nonEmptyParDeclList;
    }
}
