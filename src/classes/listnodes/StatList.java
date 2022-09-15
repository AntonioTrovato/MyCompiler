package classes.listnodes;

import classes.innernodes.StatNode;
import interfaces.ExprListElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class StatList {

    private ArrayList<StatNode> statList;

    public StatList(StatNode statNode) {
        this.statList = new ArrayList<>();
        this.statList.add(statNode);
    }

    public void addStatNode(StatNode statNode) {
        this.statList.add(statNode);
    }

    public ArrayList<StatNode> getStatList() {
        return statList;
    }
}
