package classes.innernodes;

import classes.leaves.IdLeaf;
import classes.leaves.OutLeaf;
import classes.leaves.TypeLeaf;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ParDeclNode extends InnerNode {

    public ParDeclNode(TypeLeaf typeLeaf, IdLeaf idLeaf) {
        super("ParDeclOp");
        this.idLeaf = idLeaf;
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(typeLeaf);
        children.add(idLeaf);
        super.addChildren(children);
    }

    public ParDeclNode(OutLeaf outLeaf, TypeLeaf typeLeaf, IdLeaf idLeaf) {
        super("ParDeclOp");
        this.idLeaf = idLeaf;
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(outLeaf);
        children.add(typeLeaf);
        children.add(idLeaf);
        super.addChildren(children);
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }

    private ArrayList<String> type;

    public IdLeaf getIdLeaf() {
        return idLeaf;
    }

    private IdLeaf idLeaf = null;

}
