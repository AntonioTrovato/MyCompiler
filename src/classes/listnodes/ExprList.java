package classes.listnodes;

import interfaces.ExprListElement;

import java.util.ArrayList;

public class ExprList {

    private ArrayList<ExprListElement> exprList;

    public ExprList(ExprListElement exprListElement) {
        this.exprList = new ArrayList<>();
        this.exprList.add(exprListElement);
    }

    public void addExprListElement(ExprListElement exprListElement) {
        this.exprList.add(exprListElement);
    }

    public ArrayList<ExprListElement> getExprList() {
        return exprList;
    }
}
