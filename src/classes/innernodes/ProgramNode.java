package classes.innernodes;

import classes.listnodes.*;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ProgramNode extends InnerNode{

    public ProgramNode(VarDeclList varDeclList, FunList funList, MainNode mainNode) {
        super("ProgramOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.addAll(varDeclList.getVarDeclList());
        children.addAll(funList.getFunList());
        children.add(mainNode);
        super.addChildren(children);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

}
