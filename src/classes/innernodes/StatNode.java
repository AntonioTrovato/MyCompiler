package classes.innernodes;

import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class StatNode extends InnerNode implements StatNodeElement{

    public StatNode(StatNodeElement statNodeElement) {
        super(statNodeElement);
    }

    public StatNode(ExprNode exprNode) {
        super("RETURN");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(exprNode);
        super.addChildren(children);
    }

    @Override
    public Env getEnvironment() {
        return super.getEnvironment();
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    private ArrayList<String> types = new ArrayList<>();

}
