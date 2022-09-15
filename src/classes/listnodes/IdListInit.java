package classes.listnodes;

import interfaces.ExprListElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class IdListInit {

    private ArrayList<ExprListElement> idListInit;

    public IdListInit(ExprListElement exprListElement) {
        this.idListInit = new ArrayList<>();
        this.idListInit.add(exprListElement);
    }

    public void addExprListElement(ExprListElement exprListElement) {
        this.idListInit.add(exprListElement);
    }

    public IdListInit(ExprListElement idLeaf, ExprListElement exprNode) {
        this.idListInit = new ArrayList<>();
        this.idListInit.add(idLeaf);
        this.idListInit.add(exprNode);
    }

    public void addExprListElement(ExprListElement idLeaf, ExprListElement exprNode) {
        this.idListInit.add(idLeaf);
        this.idListInit.add(exprNode);
    }

    public ArrayList<ExprListElement> getIdListInit() {
        return idListInit;
    }
}
