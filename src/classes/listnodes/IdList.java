package classes.listnodes;


import classes.leaves.IdLeaf;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class IdList {

    private ArrayList<IdLeaf> idList;
    private String type;

    public IdList(IdLeaf idLeaf) {
        this.idList = new ArrayList<>();
        this.idList.add(idLeaf);
    }

    public void addIdLeaf(IdLeaf idLeaf) {
        this.idList.add(idLeaf);
    }

    public ArrayList<IdLeaf> getIdList() {
        return idList;
    }
}
