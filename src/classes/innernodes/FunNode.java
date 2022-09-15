package classes.innernodes;

import classes.leaves.IdLeaf;
import classes.leaves.TypeLeaf;
import classes.listnodes.ParamDeclList;
import classes.listnodes.StatList;
import classes.listnodes.VarDeclList;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class FunNode extends InnerNode{
    private String nome;

    public FunNode(IdLeaf idLeaf, ParamDeclList paramDeclList, TypeLeaf typeLeaf,
                   VarDeclList varDeclList, StatList statList) {
        super(idLeaf.getEntry());
        nome = idLeaf.getEntry();
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(idLeaf);
        children.addAll(paramDeclList.getParDeclList());
        children.add(typeLeaf);
        children.addAll(varDeclList.getVarDeclList());
        children.addAll(statList.getStatList());
        super.addChildren(children);
    }

    public FunNode(IdLeaf idLeaf, ParamDeclList paramDeclList,
                   VarDeclList varDeclList, StatList statList) {
        super(idLeaf.getEntry());
        nome = idLeaf.getEntry();
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(idLeaf);
        children.addAll(paramDeclList.getParDeclList());
        children.addAll(varDeclList.getVarDeclList());
        children.addAll(statList.getStatList());
        super.addChildren(children);
    }

    public String getNome() {
        return nome;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

}
