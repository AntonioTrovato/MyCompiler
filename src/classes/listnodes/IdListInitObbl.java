package classes.listnodes;

import interfaces.ExprListElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class IdListInitObbl {

    private ArrayList<ExprListElement> idListInitObbl;

    public IdListInitObbl(ExprListElement idLeaf, ExprListElement constLeaf) {
        this.idListInitObbl = new ArrayList<>();
        this.idListInitObbl.add(idLeaf);
        this.idListInitObbl.add(constLeaf);
    }

    public void addExprListElement(ExprListElement idLeaf, ExprListElement constLeaf) {
        this.idListInitObbl.add(idLeaf);
        this.idListInitObbl.add(constLeaf);
    }

    public ArrayList<ExprListElement> getIdListInitObbl() {
        return idListInitObbl;
    }
}
