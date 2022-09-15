package compiler;

import classes.exceptions.AlreadyDeclearedException;
import classes.exceptions.ExpressionTypeError;
import classes.exceptions.NotDeclearedException;
import classes.innernodes.*;
import classes.leaves.*;
import interfaces.ExprListElement;
import interfaces.StatNodeElement;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

public class SemanticAnalyzer {

    private Stack<Env> symbolTablesStack;
    private ArrayList<String> nodesThatOpenScope;
    private ArrayList<String> nodesWithDeclaration;

    public SemanticAnalyzer() {
        this.symbolTablesStack = new Stack<>();
        setNodesThatOpenScope();
        setNodesWithDeclaration();
    }

    private void setNodesThatOpenScope() {
        this.nodesThatOpenScope = new ArrayList<>();
        nodesThatOpenScope.add("ProgramOp");
        nodesThatOpenScope.add(FunNode.class.getName());
        nodesThatOpenScope.add("MainOp");
        nodesThatOpenScope.add("IfStatOp");
        nodesThatOpenScope.add("ElseOp");
        nodesThatOpenScope.add("WhileOp");
    }

    private void setNodesWithDeclaration() {
        this.nodesWithDeclaration = new ArrayList<>();
        this.nodesWithDeclaration.add("VarDeclOp");
        this.nodesWithDeclaration.add("ParDeclOp");
    }

    public boolean isASTLeaf(TreeNode node) {
        if(
           node.getClass().equals(ConstLeaf.class)
           || node.getClass().equals(TypeLeaf.class)
           || node.getClass().equals(IdLeaf.class)
           || node.getClass().equals(OutLeaf.class)
           || node.getClass().equals(VarLeaf.class)
           || node.getClass().equals(WriteStatLeaf.class)) {
            return true;
        }
        return false;
    }

    private Boolean isScopeNode(TreeNode node) {
        if(isASTLeaf(node))
            return false;
        if(node.getClass().equals(FunNode.class)) {
            return true;
        }
        InnerNode innerNode = ((InnerNode) node);
        String nodeName = (String) innerNode.getUserObject();
        return this.nodesThatOpenScope.contains(nodeName);
    }

    private Boolean isDeclarationNode(TreeNode node) {
        if(isASTLeaf(node))
            return false;
        InnerNode innerNode = ((InnerNode) node);
        String nodeName = (String) innerNode.getUserObject();
        return this.nodesWithDeclaration.contains(nodeName);
    }

    private boolean isEmptyEnvironment() {
        return this.symbolTablesStack.empty();
    }

    private Env addEnvironment(Env environment) {
        return this.symbolTablesStack.push(environment);
    }

    private Env getNearestEnv() {
        // check if this shit returns null if not in stack.
        try {
            Env nearestEnvironment = this.symbolTablesStack.peek();
            return nearestEnvironment;
        } catch (Exception e) {
            return null;
        }
    }

    private Env popNearestEnv() {
        // check if this shit returns null if not in stack.
        try {
            Env nearestEnvironment = this.symbolTablesStack.pop();
            return nearestEnvironment;
        } catch (Exception e) {
            return null;
        }
    }

    private String getTypeFromString(String value) {
        try {
            Integer.parseInt(value);
            return "INTEGER";
        } catch(NumberFormatException e) { }
        //controlliamo se è double
        try {
            Double.parseDouble(value);
            return "REAL";
        } catch(NumberFormatException e) { }

        if(value.equals("true") || value.equals("false"))
            return "BOOL";

        return "STRING";
    }

    private void verifyIdName(TreeNode node) {
        //controlliamo se stiamo passando un indirizzo
        if(node instanceof IdLeaf)
            if(((IdLeaf) node).getEntry().startsWith("@"))
                ((IdLeaf) node).setEntry(((IdLeaf) node).getEntry().substring(1));
    }

    private boolean someDifferent(ArrayList<String> arrayList, String string) {
        //qui vediamo se il tipo che dovrebbe ritornare con la funzione
        //corrisponde ai tipi ritornati dagli statement che contiene
        if(string.equals("VOID") && arrayList.isEmpty())
            return false;
        else if(string.equals("VOID"))
            return true;
        for (String s : arrayList)
            if(!(s.equals(string)))
                return true;
        return false;
    }

    private String identifierCheckig(IdLeaf idLeaf) throws NotDeclearedException {
        //controlliamo i tipi per ogni uso di identificatori (variabili, definizioni di funzioni, invocazoni)
        if(idLeaf.getEnvironment() == null)
            idLeaf.setEnvironment(((InnerNode) idLeaf.getParent()).getEnvironment());
        //se è una variabile controlla semplicemente che sia stata dichiarata e prendi il tipo
        if(!((idLeaf.getParent() instanceof CallFunNode) &&
                (((IdLeaf)((CallFunNode) idLeaf.getParent()).children().nextElement()) == idLeaf))
                && !(idLeaf.getParent() instanceof FunNode)) {
            idLeaf.setType(idLeaf.getEnvironment().lookup(idLeaf.getEntry()).getTypes().get(0));
            if(idLeaf.getEnvironment().lookup(idLeaf.getEntry()).isPointer())
                idLeaf.setPointer(true);
            return idLeaf.getType();
        }
        //se è una chiamata a funzione devi fare il match tra parametri att./formali
        else if(idLeaf.getParent() instanceof CallFunNode) {

            //prendiamo tutti i valori che la funzione dovrebbe seguire
            EnvEntry funDefEntry = idLeaf.getEnvironment().lookup(idLeaf.getEntry());
            ArrayList<String> formalParamTypes = funDefEntry.getParamTypes();
            String returnType = funDefEntry.getReturnType();
            //prendiamo i valori usati per la chiamata
            ArrayList<String> actualParamTypes = new ArrayList<>();
            Enumeration children = idLeaf.getParent().children();//ossia figlio1(figlio2,figlio3,....)
            children.nextElement();//scartiamo il primo perché è il nome della funzione
            boolean pointer = false;
            while (children.hasMoreElements()) {
                String actualParam = "";
                //i parametri potrebbero essere variabili o espressioni, inoltre potrebbero avere in/out
                ExprListElement param = (ExprListElement) children.nextElement();
                ExprNode child;
                if (param instanceof ExprNode)
                    child = (ExprNode) param;
                else if (param instanceof IdLeaf)
                    child = new ExprNode(param);
                else if (param instanceof ConstLeaf)
                    child = new ExprNode(param);
                else if (param instanceof CallFunNode)
                    child = new ExprNode(param);
                else
                    throw new NotDeclearedException("Semantic Error: errata invocazione della funzione");
                if (child.getUserObject() instanceof IdLeaf) {
                    actualParam = ((IdLeaf) child.getUserObject()).getEntry();
                    //caso puntatore
                    if (((IdLeaf) child.getUserObject()).getEntry().startsWith("@")) {
                        pointer = true;
                        actualParam = actualParam.substring(1);
                        ((IdLeaf) child.getUserObject()).setPointer(true);
                    }else
                        pointer = false;
                    EnvEntry actualParamEntry = idLeaf.getEnvironment().lookup(actualParam);
                    if (actualParamEntry.getParamTypes().size() > 0) {
                        if (pointer) {
                            actualParamTypes.add(actualParamEntry.getParamTypes().get(0));
                            actualParamTypes.add("POINTER");
                        } else
                            actualParamTypes.add(actualParamEntry.getParamTypes().get(0));
                    } else {
                        actualParamTypes.add("VOID");
                    }
                } else if (child.getUserObject() instanceof ConstLeaf) {
                    actualParamTypes.add(constChecking((ConstLeaf) child.getUserObject()));
                } else if (child.getUserObject() instanceof CallFunNode) {
                    //ricorsivamente
                    CallFunNode callFunNode = (CallFunNode) child.getUserObject();
                    actualParamTypes.add(identifierCheckig((IdLeaf) callFunNode.children().nextElement()));
                }else if(child.getUserObject() instanceof ExprNode) {
                    actualParamTypes.add(expressionChecking((ExprNode) child.getUserObject()));
                }else {
                    //qui va gestito il caso delle espressioni
                    actualParamTypes.add(expressionChecking(child));
                }
            }
            //se c'è match allora il tipo della funzione sarà quello dichiarato di ritorno
            if(actualParamTypes.size()!=formalParamTypes.size())//altrimenti errore
                throw new NotDeclearedException("Semantic Error: errata invocazione della funzione ["+idLeaf.getEntry()+"]");
            if(!(actualParamTypes.size()==0)) {
                for (int i = 0; i < actualParamTypes.size(); i++) {
                    //i parametri della chiamata sono gestiti dall'ultimo al primo
                    //quindi sono speculari
                    if (actualParamTypes.get(i).equals("POINTER")) {
                        if (formalParamTypes.get(formalParamTypes.size() - 1 - i).equals("POINTER")) {
                            if (!formalParamTypes.get(formalParamTypes.size() - 1 - (i - 1)).equals(actualParamTypes.get(i - 1)))
                                throw new NotDeclearedException("Semantic Error: errata invocazione della funzione");
                            else
                                i = i + 1;//abbiamo quindi già controllato il successivo
                        }
                    }else if (!(actualParamTypes.get(i).equals(formalParamTypes.get(formalParamTypes.size() - 1 - i))))
                        throw new NotDeclearedException("Semantic Error: errata invocazione della funzione");
                }
            }
            ((CallFunNode) idLeaf.getParent()).setType(returnType);
            return returnType;
        }else if(idLeaf.getParent() instanceof FunNode) {
            //settiamo il tipo della funzione
            FunNode funNode = (FunNode) idLeaf.getParent();
            String returnType = funNode.getEnvironment().lookup(funNode.getNodeName()).getReturnType();
            funNode.setType(returnType);

            //settiamo i tipi per i parametri, la lista di parametri è sempre il secondo figlio
            Enumeration children = funNode.children();
            //scartiamo il primo figlio (nome della funzione)
            children.nextElement();
            TreeNode funNodeChild = null;
            //ora passiamo ai parametri (ci sarò sempre almeno uno stmt)
            while (children.hasMoreElements()) {
                funNodeChild = (TreeNode) children.nextElement();
                if(funNodeChild instanceof ParDeclNode) {
                    //può essere un normale tipo o un puntatore ad un tipo
                    Enumeration parDeclChildren = ((ParDeclNode) funNodeChild).children();
                    TreeNode firstParDeclChild = (TreeNode) parDeclChildren.nextElement();
                    ArrayList<String> parDeclType = new ArrayList<>();
                    //se non è un puntatore
                    if(firstParDeclChild instanceof TypeLeaf) {
                        parDeclType.add(((TypeLeaf) firstParDeclChild).getType());
                        ((ParDeclNode) funNodeChild).setType(parDeclType);
                    }else if(firstParDeclChild instanceof OutLeaf) {//se è un puntatore
                        parDeclType.add("POINTER");
                        TypeLeaf pointedType = (TypeLeaf) parDeclChildren.nextElement();
                        parDeclType.add(pointedType.getType());
                        ((ParDeclNode) funNodeChild).setType(parDeclType);
                        ((ParDeclNode) funNodeChild).getIdLeaf().setPointer(true);
                        funNode.getEnvironment().getSymTable().get(((ParDeclNode) funNodeChild).getIdLeaf().getEntry()).setPointer(true);
                    }else
                        throw new ExpressionTypeError("Semantic Error: tipi parametri non validi");
                }else
                    break;
            }

            //adesso dobbiamo controllare che il ritorno sia corretto
            //per fare questo dobbiamo controllare gli statement
            //ignoriamo le dichiarazioni (già controllate)
            ArrayList<String> returnTypes = new ArrayList<>();
            if(children.hasMoreElements()) {
                while (!(funNodeChild instanceof StatNodeElement))
                    funNodeChild = (TreeNode) children.nextElement();
                while (children.hasMoreElements() || funNodeChild instanceof StatNodeElement) {
                    statementChecking((StatNodeElement) funNodeChild, returnTypes, funNode.getEnvironment());
                    if (children.hasMoreElements())
                        funNodeChild = (TreeNode) children.nextElement();
                    else
                        break;
                }
            }

            if(someDifferent(returnTypes,returnType))
                throw new ExpressionTypeError("Semantic Error: i tipi di ritorno non combaciano con quello dichiarato");
            return returnType;
        }
        return "VOID";
    }

    private String constChecking(ConstLeaf constLeaf) {
        //check per le costanti
        if(constLeaf.getEntry().equals("true") || constLeaf.getEntry().equals("false"))
            constLeaf.setType("BOOL");
        else
            constLeaf.setType(getTypeFromString(constLeaf.getEntry()));
        return constLeaf.getType();
    }

    private String expressionChecking(ExprNode exprNode) {
        //check per le espressioni
        if(exprNode.getEnvironment()==null)
            exprNode.setEnvironment(((InnerNode) exprNode.getParent()).getEnvironment());
        if(exprNode.getUserObject() instanceof String ) {//nodi operazione
            String operation = (String) exprNode.getUserObject();
            Enumeration children = exprNode.children();
            if (operation.equals("AddOp") || operation.equals("MulOp")
                    || operation.equals("DiffOp")
                    || operation.equals("PowOp")) {
                //primo addendo
                String typeAdd1 = expressionChecking((ExprNode) children.nextElement());
                String typeAdd2 = expressionChecking((ExprNode) children.nextElement());
                if ((typeAdd1.equals("INTEGER") && typeAdd2.equals("INTEGER"))
                        || typeAdd1.equals("REAL") && typeAdd2.equals("REAL")) {
                    exprNode.setType(typeAdd1);
                    return exprNode.getType();
                } else if ((typeAdd1.equals("REAL") && typeAdd2.equals("INTEGER"))
                        || (typeAdd1.equals("INTEGER") && typeAdd2.equals("REAL"))) {
                    exprNode.setType("REAL");
                    return exprNode.getType();
                }else throw new ExpressionTypeError("Semantic Error: tipi dell'espressione aritmetica non validi");
            }else if(operation.equals("DivIntOp")) {
                //primo addendo
                String typeAdd1 = expressionChecking((ExprNode) children.nextElement());
                String typeAdd2 = expressionChecking((ExprNode) children.nextElement());
                if ((typeAdd1.equals("INTEGER") && typeAdd2.equals("INTEGER"))
                        || typeAdd1.equals("REAL") && typeAdd2.equals("REAL")) {
                    exprNode.setType("INTEGER");
                    return exprNode.getType();
                } else if ((typeAdd1.equals("REAL") && typeAdd2.equals("INTEGER"))
                        || (typeAdd1.equals("INTEGER") && typeAdd2.equals("REAL"))) {
                    exprNode.setType("INTEGER");
                    return exprNode.getType();
                }else throw new ExpressionTypeError("Semantic Error: tipi dell'espressione aritmetica non validi");
            }else if(operation.equals("DivOp")) {
                //primo addendo
                String typeAdd1 = expressionChecking((ExprNode) children.nextElement());
                String typeAdd2 = expressionChecking((ExprNode) children.nextElement());
                if ((typeAdd1.equals("INTEGER") && typeAdd2.equals("INTEGER"))
                        || typeAdd1.equals("REAL") && typeAdd2.equals("REAL")) {
                    exprNode.setType("REAL");
                    return exprNode.getType();
                } else if ((typeAdd1.equals("REAL") && typeAdd2.equals("INTEGER"))
                        || (typeAdd1.equals("INTEGER") && typeAdd2.equals("REAL"))) {
                    exprNode.setType("REAL");
                    return exprNode.getType();
                }else throw new ExpressionTypeError("Semantic Error: tipi dell'espressione aritmetica non validi");
            }else if (operation.equals("AndOp") || operation.equals("OrOp")) {
                String typeExp1 = expressionChecking((ExprNode) children.nextElement());
                String typeExp2 = expressionChecking((ExprNode) children.nextElement());
                if (typeExp1.equals("BOOL") && typeExp2.equals("BOOL")) {
                    exprNode.setType("BOOL");
                    return "BOOL";
                } else
                    throw new ExpressionTypeError("Semantic Error: tipi dell'espressione logica non validi");
            } else if (operation.equals("StrCatOp")) {
                String typeExp1 = expressionChecking((ExprNode) children.nextElement());
                String typeExp2 = expressionChecking((ExprNode) children.nextElement());
                if(!typeExp1.equals("STRING") && !typeExp2.equals("STRING"))
                    throw new ExpressionTypeError("Semantic Error: almeno un elemento deve essere stringa nella " +
                            "concatenazione");
                if (typeExp1.equals("STRING") && typeExp2.equals("STRING")) {
                    exprNode.setType("STRING");
                    return "STRING";
                }else if((typeExp1.equals("STRING") && typeExp2.equals("INTEGER")) ||
                         (typeExp1.equals("STRING") && typeExp2.equals("REAL"))||
                         (typeExp1.equals("STRING") && typeExp2.equals("BOOL")) ||
                         (typeExp1.equals("INTEGER") && typeExp2.equals("STRING")) ||
                         (typeExp1.equals("REAL") && typeExp2.equals("STRING"))||
                         (typeExp1.equals("BOOL") && typeExp2.equals("STRING"))) {
                    exprNode.setType("STRING");
                    return "STRING";
                }else
                    throw new ExpressionTypeError("Semantic Error: tipi non validi per concatenazione, void o notype");
            } else if (operation.equals("GTOp") || operation.equals("GEOp")
                    || operation.equals("LTOp") || operation.equals("LEOp")) {
                String typeAdd1 = expressionChecking((ExprNode) children.nextElement());
                String typeAdd2 = expressionChecking((ExprNode) children.nextElement());
                if ((typeAdd1.equals("INTEGER") && typeAdd2.equals("INTEGER"))
                        || typeAdd1.equals("REAL") && typeAdd2.equals("REAL")) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                }else if ((typeAdd1.equals("REAL") && typeAdd2.equals("INTEGER"))
                        || (typeAdd1.equals("INTEGER") && typeAdd2.equals("REAL"))) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                }else
                    throw new ExpressionTypeError("Semantic Error: tipi dell'espressione booleana non validi");
            } else if (operation.equals("EQOp") || operation.equals("NEOp")) {
                String typeAdd1 = expressionChecking((ExprNode) children.nextElement());
                String typeAdd2 = expressionChecking((ExprNode) children.nextElement());
                if ((typeAdd1.equals("INTEGER") && typeAdd2.equals("INTEGER"))
                        || typeAdd1.equals("REAL") && typeAdd2.equals("REAL")) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                }else if ((typeAdd1.equals("REAL") && typeAdd2.equals("INTEGER"))
                        || (typeAdd1.equals("INTEGER") && typeAdd2.equals("REAL"))) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                }else if (typeAdd1.equals("BOOL") && typeAdd2.equals("BOOL")) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                }else if (typeAdd1.equals("STRING") && typeAdd2.equals("STRING")) {
                    exprNode.setType("BOOL");
                    return exprNode.getType();
                } else
                    throw new ExpressionTypeError("Semantic Error: tipi dell'espressione booleana non validi");
            } else if (operation.equals("UminusOp")) {
                String typeAdd = expressionChecking((ExprNode) children.nextElement());
                if (typeAdd.equals("INTEGER") || typeAdd.equals("REAL")) {
                    exprNode.setType(typeAdd);
                    return typeAdd;
                }
            } else if (operation.equals("NotOp")) {
                String typeAdd = expressionChecking((ExprNode) children.nextElement());
                if (typeAdd.equals("BOOL")) {
                    exprNode.setType(typeAdd);
                    return typeAdd;
                }
            }
        }else {//figli dei nodi operazione
            if(exprNode.getUserObject() instanceof ConstLeaf) {
                if (((ConstLeaf) exprNode.getUserObject()).getEntry().equals("true")
                        || ((ConstLeaf) exprNode.getUserObject()).getEntry().equals("false"))
                    return "BOOL";
                return constChecking((ConstLeaf) exprNode.getUserObject());
            }else if((exprNode.getUserObject() instanceof IdLeaf)) {
                ((IdLeaf) exprNode.getUserObject()).setParent((MutableTreeNode) exprNode.getParent());
                return identifierCheckig((IdLeaf) exprNode.getUserObject());
            }else if (exprNode.getUserObject() instanceof CallFunNode) {
                ((CallFunNode) exprNode.getUserObject()).setEnvironment(exprNode.getEnvironment());
                ((CallFunNode) exprNode.getUserObject()).setParent((MutableTreeNode) exprNode.getParent());
                return identifierCheckig((IdLeaf) ((CallFunNode) exprNode.getUserObject()).children().nextElement());
            }else if (exprNode.getUserObject() instanceof ExprNode) {
                ((ExprNode) exprNode.getUserObject()).setEnvironment(exprNode.getEnvironment());
                ((ExprNode) exprNode.getUserObject()).setParent((MutableTreeNode) exprNode.getParent());
                return expressionChecking((ExprNode) exprNode.getUserObject());
            }else throw new NotDeclearedException("Semantic Error: espressione non valida");
        }
        throw new NotDeclearedException("Semantic Error: espressione non valida");
    }

    private String assignChecking(AssignStatNode assignStatNode) throws ExpressionTypeError{
        //qui dobbiamo controllare che le assegnazioni avvengano correttamente
        Enumeration children = assignStatNode.children();
        IdLeaf idLeaf = (IdLeaf) children.nextElement();
        ExprNode exprNode = (ExprNode) children.nextElement();
        String exprType = expressionChecking(exprNode);
        //a secopnda di chi segue capiamo la produzione corrispondente
        if(exprNode.getUserObject() instanceof IdLeaf)
            exprType = ((IdLeaf) exprNode.getUserObject()).getType();
        else if(exprNode.getUserObject() instanceof ConstLeaf)
            exprType = constChecking((ConstLeaf) exprNode.getUserObject());
        else if(exprNode.getUserObject() instanceof CallFunNode)
            exprType = ((CallFunNode) exprNode.getUserObject()).getType();
        if(identifierCheckig(idLeaf).equals(exprType)) {
            assignStatNode.setType("notype");
            return "notype";
        }
        throw new ExpressionTypeError("Semantic Error: tipi dell'assegnazione non corretti");
    }

    private String declarationChecking(VarDeclNode varDeclNode) throws ExpressionTypeError{
        //qui controllaiamo le dichiarazioni
        if(varDeclNode.getEnvironment() == null)
            varDeclNode.setEnvironment(((InnerNode) varDeclNode.getParent()).getEnvironment());
        Enumeration children = varDeclNode.children();
        TreeNode treeNode = (TreeNode) children.nextElement();//ignoriamo il type/var
        if(treeNode instanceof TypeLeaf) {
            String varsType = ((TypeLeaf) treeNode).getType();
            ArrayList<ExprListElement> exprListElements = new ArrayList<>();
            while (children.hasMoreElements())
                exprListElements.add((ExprListElement) children.nextElement());
            for(int i = 0; i < exprListElements.size(); i++) {
                //aggiungi caso in cui siamo al limite
                if(((i == exprListElements.size()-1) && exprListElements.get(i) instanceof IdLeaf) ||
                exprListElements.get(i+1) instanceof IdLeaf) {
                    String idType = identifierCheckig((IdLeaf) exprListElements.get(i));
                    if (!(idType.equals(varsType)))
                        throw new ExpressionTypeError("Semantic Error: tipi della dichiarazione non corretti");
                }else if(exprListElements.get(i) instanceof IdLeaf) {
                    if(exprListElements.get(i+1) instanceof ExprNode) {
                        identifierCheckig((IdLeaf) exprListElements.get(i));
                        String exprType = expressionChecking((ExprNode) exprListElements.get(i+1));
                        if (!(exprType.equals(varsType)))
                            throw new ExpressionTypeError("Semantic Error: tipi della dichiarazione non corretti");
                        i = i+1;
                    }
                }
            }
            varDeclNode.setType("notype");
            return "notype";
        }else if(treeNode instanceof VarLeaf) {
            ArrayList<ExprListElement> exprListElements = new ArrayList<>();
            while (children.hasMoreElements())
                exprListElements.add((ExprListElement) children.nextElement());
            for(int i = 0; i < exprListElements.size(); i++) {
                //aggiungi caso in cui siamo al limite
                if(exprListElements.get(i) instanceof IdLeaf) {
                    if(exprListElements.get(i+1) instanceof ConstLeaf) {
                        String constType = constChecking((ConstLeaf) exprListElements.get(i+1));
                        identifierCheckig((IdLeaf) exprListElements.get(i));
                        if (!(constType.equals(identifierCheckig((IdLeaf) exprListElements.get(i)))))
                            throw new ExpressionTypeError("Semantic Error: tipi della dichiarazione non corretti");
                        i = i+1;
                    }
                }
            }
            varDeclNode.setType("notype");
            return "notype";
        }
        throw new ExpressionTypeError("Semantic Error: tipi della dichiarazione non corretti");
    }

    private ArrayList<String> statementChecking(StatNodeElement statNodeElement, ArrayList<String> returnTypes,
                                                Env environment) {
       //check ricorsivo per gli statement, il tipo di uno statement è notype
       //oppure la lista dei tipi ritornati dai suoi "sotto-statement"
       Object statNodeUserObject;
       if(statNodeElement instanceof StatNode)
           statNodeUserObject  = ((StatNode) statNodeElement).getUserObject();
       else
            statNodeUserObject = statNodeElement;
       if(statNodeUserObject.equals("RETURN")) {
           if(statNodeElement.getEnvironment() == null)
               ((StatNode) statNodeElement).setEnvironment(environment);
           //il figlio sarà l'expr da valutare
           Enumeration returnChildren = ((StatNode) statNodeElement).children();
           ExprNode returnExpression = (ExprNode) returnChildren.nextElement();
           returnTypes.add(expressionChecking(returnExpression));
       }else if(statNodeUserObject instanceof AssignStatNode){
           if(((AssignStatNode) statNodeUserObject).getEnvironment() == null)
               ((AssignStatNode) statNodeUserObject).setEnvironment(environment);
           assignChecking((AssignStatNode) statNodeUserObject);
        }else if(statNodeUserObject instanceof IfStatNode) {
           if(((IfStatNode) statNodeUserObject).getEnvironment() == null)
               ((IfStatNode) statNodeUserObject).setEnvironment(environment);
           //dobbiamo controllare che il controllo si a booleano e poi i return nella lista degli statement
           Enumeration ifStatChildren = ((IfStatNode) statNodeUserObject).children();
           if(ifStatChildren.hasMoreElements()) {
               ExprNode booleanExpression = (ExprNode) (ifStatChildren.nextElement());
               if (!(expressionChecking(booleanExpression).equals("BOOL")))
                   throw new ExpressionTypeError("Semantic Error: la condizione di if e while deve essere booleana");
               //ignoriamo tutti gli altri figli non statement dell'if
               TreeNode ifChild = (TreeNode) ifStatChildren.nextElement();
               while (!(ifChild instanceof StatNodeElement)) {
                   declarationChecking((VarDeclNode) ifChild);
                   ifChild = (TreeNode) ifStatChildren.nextElement();
               }//l'ultimo sarà uno stmt
               //adesso ricorsivamente controlliamo i loro return
               while (ifStatChildren.hasMoreElements() || ifChild instanceof StatNodeElement) {
                   statementChecking((StatNodeElement) ifChild, returnTypes,((IfStatNode) statNodeUserObject).getEnvironment());
                   if (ifStatChildren.hasMoreElements())
                       ifChild = (TreeNode) ifStatChildren.nextElement();
                   else
                       break;
               }
           }
           ((IfStatNode) statNodeUserObject).setTypes(returnTypes);
       }else if(statNodeUserObject instanceof WhileStatNode) {
           if(((WhileStatNode) statNodeUserObject).getEnvironment() == null)
               ((WhileStatNode) statNodeUserObject).setEnvironment(environment);
           //dobbiamo controllare che il controllo si a booleano e poi i return nella lista degli statement
           Enumeration whileStatChildren = ((WhileStatNode) statNodeUserObject).children();
           if(whileStatChildren.hasMoreElements()) {
               ExprNode booleanExpression = (ExprNode) (whileStatChildren.nextElement());
               if (!(expressionChecking(booleanExpression).equals("BOOL")))
                   throw new ExpressionTypeError("Semantic Error: la condizione di if e while deve essere booleana");
               //ignoriamo tutti gli altri figli non statement del while
               TreeNode whileChild = (TreeNode) whileStatChildren.nextElement();
               while (!(whileChild instanceof StatNodeElement)) {
                   declarationChecking((VarDeclNode) whileChild);
                   whileChild = (TreeNode) whileStatChildren.nextElement();
               }//l'ultimo sarà uno stmt
               //adesso ricorsivamente controlliamo i loro return
               while (whileStatChildren.hasMoreElements() || whileChild instanceof StatNodeElement) {
                   statementChecking((StatNodeElement) whileChild, returnTypes,((WhileStatNode) statNodeUserObject).getEnvironment());
                   if (whileStatChildren.hasMoreElements())
                       whileChild = (TreeNode) whileStatChildren.nextElement();
                   else
                       break;
               }
           }
           ((WhileStatNode) statNodeUserObject).setTypes(returnTypes);
       }else if(statNodeUserObject instanceof ElseNode) {
           if(((ElseNode) statNodeUserObject).getEnvironment() == null)
               ((ElseNode) statNodeUserObject).setEnvironment(environment);
           //dobbiamo controllare che il controllo si a booleano e poi i return nella lista degli statement
           Enumeration elseChildren = ((ElseNode) statNodeUserObject).children();
           //ignoriamo tutti gli altri figli non statement dell'if
           if(elseChildren.hasMoreElements()) {
               TreeNode elseChild = (TreeNode) elseChildren.nextElement();
               while (!(elseChild instanceof StatNodeElement)) {
                   declarationChecking((VarDeclNode) elseChild);
                   elseChild = (TreeNode) elseChildren.nextElement();
               }
               //adesso ricorsivamente controlliamo i loro return
               while (elseChildren.hasMoreElements() || elseChild instanceof StatNodeElement) {
                   statementChecking((StatNodeElement) elseChild, returnTypes,statNodeElement.getEnvironment());
                   if (elseChildren.hasMoreElements())
                       elseChild = (TreeNode) elseChildren.nextElement();
                   else
                       break;
               }
           }
           ((ElseNode) statNodeUserObject).setTypes(returnTypes);
       }else if((statNodeUserObject instanceof WriteStatNode) || (statNodeUserObject instanceof ReadStatNode) ||
               (statNodeUserObject instanceof CallFunNode)) {
           if(statNodeUserObject instanceof WriteStatNode) {
               Enumeration writeChildren = ((WriteStatNode) statNodeUserObject).children();
               TreeNode child = (TreeNode) writeChildren.nextElement();
               ExprNode child2 = (ExprNode) writeChildren.nextElement();
               if(child2.getUserObject() instanceof String)
                   if(child2.getUserObject().equals("StrCatOp")) {
                       child2.setEnvironment(environment);
                       expressionChecking(child2);
                       expressionChecking(child2);
                   }
           }else if(statNodeUserObject instanceof ReadStatNode) {
               Enumeration readChildren = ((ReadStatNode) statNodeUserObject).children();
               TreeNode child = (TreeNode) readChildren.nextElement();
               ExprNode readExpr = null;
               while (readChildren.hasMoreElements()) {
                   TreeNode child2 = (TreeNode) readChildren.nextElement();
                   if(child2 instanceof ExprNode) {
                       readExpr = (ExprNode) child2;
                       break;
                   }
               }
               if(readExpr != null) {
                   if (readExpr.getUserObject() instanceof String)
                       if (readExpr.getUserObject().equals("StrCatOp")) {
                           readExpr.setEnvironment(environment);
                           expressionChecking(readExpr);
                           expressionChecking(readExpr);
                       }
               }
           }
       }
       return returnTypes;
    }

    private String mainFunChecking(MainNode mainNode) {
        //controllo per il main
        //dobbiamo verificare che non ritorni nulla
        Enumeration mainNodeChildren = mainNode.children();
        ArrayList<String> mainNodeReturnTypes = new ArrayList<>();
        ArrayList<Boolean> reachableReturn = new ArrayList<>();
        while(mainNodeChildren.hasMoreElements()) {
            TreeNode mainNodeChild = (TreeNode) mainNodeChildren.nextElement();
            if(mainNodeChild instanceof StatNodeElement)
                statementChecking((StatNodeElement) mainNodeChild,mainNodeReturnTypes,mainNode.getEnvironment());
        }
        if(someDifferent(mainNodeReturnTypes,"VOID"))
            throw new ExpressionTypeError("Semantic Error: il main non può avere return!");
        else
            mainNode.setType("notype");
        return "notype";
    }

    public void visitForScoping(TreeNode node, ArrayList<TreeNode> visitedNodes) throws AlreadyDeclearedException {
       if(!isASTLeaf(node)) {
            InnerNode thisNode = (InnerNode) node;
            if (thisNode.getUserObject() instanceof DefaultMutableTreeNode) {
                node = (TreeNode) thisNode.getUserObject();
            }
        }

        if (!visitedNodes.contains(node)) {

            //se il nodo crea scope
            if (isScopeNode(node)) {
                InnerNode innerNode = ((InnerNode) node);
                String nodeName = (String) innerNode.getUserObject();

                if(nodeName.equals("MainOp")) {
                    //mettiamo il main nella tab globale
                    Env globalEnv = getNearestEnv();
                    globalEnv.put("MainOp",new EnvEntry());
                }else if(node instanceof FunNode) {
                    //mettiamo le funzioni nella globale
                    Env globalEnv = getNearestEnv();
                    EnvEntry funEntry = new EnvEntry();
                    Enumeration children = node.children();
                    TreeNode child;
                    //qui segniamo la firma da inserire nella tab
                    while (children.hasMoreElements()) {
                        child = (TreeNode) children.nextElement();
                        if(child instanceof IdLeaf)
                            continue;
                        else if(child instanceof ParDeclNode) {
                            Enumeration params = ((ParDeclNode) child).children();
                            TreeNode thisParameter = (TreeNode) params.nextElement();
                            if(thisParameter instanceof OutLeaf) {
                                funEntry.addParamType("POINTER");
                                thisParameter = (TreeNode) params.nextElement();
                            }
                            funEntry.addParamType(((TypeLeaf) thisParameter).getType());
                        }else if(child instanceof TypeLeaf) {
                            funEntry.setReturnType(((TypeLeaf) child).getType());
                            break;
                        }
                    }
                    globalEnv.put(nodeName, funEntry);
                }

                //aggiungiamo l'env creato dal nodo in cima allo stack
                Env environment = null;
                if (node instanceof ElseNode)
                    environment = new Env(getNearestEnv().getNearestEnv(),nodeName);
                environment = new Env(getNearestEnv(), nodeName);
                addEnvironment(environment);
                InnerNode iNode = (InnerNode) node;
                iNode.setEnvironment(environment);

                if (node.getChildCount() >= 0) {
                    //ricorsivamente
                    for (Enumeration children = node.children(); children.hasMoreElements();) {
                        TreeNode child = (TreeNode) children.nextElement();
                        visitForScoping(child, visitedNodes);
                    }

                    //DEBUG
                    /*compiler.Env currentEnvironment = getNearestEnv();
                    currentEnvironment.printTables();*/

                    popNearestEnv();
                }
            }
            else if (isDeclarationNode(node)) {
                //Controlla se il nodo ha figli
                if (node.getChildCount() >= 0) {
                    Enumeration children = node.children();
                    TreeNode firstChild = (TreeNode) children.nextElement();
                    Env currentEnvironment = getNearestEnv();

                    //VarDecl
                    if (node.getClass().equals(VarDeclNode.class)) {
                        // In base ai figli capiamo a quale produzione corrisponde
                        if (firstChild.getClass().equals(TypeLeaf.class)) {
                            String type = ( (TypeLeaf) firstChild).getType();

                            while (children.hasMoreElements()) {
                                //aggiungiamo nello scope attuale questa dichiarazione
                                TreeNode child = (TreeNode) children.nextElement();
                                if (child.getClass().equals(IdLeaf.class)) {
                                    IdLeaf child_id = (IdLeaf) child;
                                    String id_name = child_id.getEntry();
                                    currentEnvironment.put(id_name, new EnvEntry(type));
                                }
                            }

                        } else {
                            while (children.hasMoreElements()) {
                                IdLeaf child_id = (IdLeaf) children.nextElement();
                                ConstLeaf child_const_value = (ConstLeaf) children.nextElement();

                                String id_name = child_id.getEntry();
                                String value = child_const_value.getEntry();
                                currentEnvironment.put(id_name, new EnvEntry(getTypeFromString(value)));
                            }
                        }

                    //ParDecl
                    } else {
                        if (firstChild.getClass().equals(TypeLeaf.class)) {
                            String type = ((TypeLeaf) firstChild).getType();
                            IdLeaf child_id = (IdLeaf) children.nextElement();
                            String id_name = child_id.getEntry();

                            currentEnvironment.put(id_name, new EnvEntry(type));
                        }
                        else {
                            String type = ((TypeLeaf) children.nextElement()).getType();
                            IdLeaf child_id = (IdLeaf) children.nextElement();
                            String id_name = child_id.getEntry();

                            currentEnvironment.put(id_name, new EnvEntry(type));
                        }
                    }
                }
            }
        }
    }

    public void visitForTypeChecking(TreeNode node) throws NotDeclearedException {
        //System.out.println(node);

        if(!isASTLeaf(node)) {
            InnerNode thisNode = (InnerNode) node;
            //sistemiamo la situazione per gli environment nel caso di nodo innestato
            if ((thisNode.getUserObject() instanceof DefaultMutableTreeNode) && !thisNode.getUserObject().equals("ProgramOp")) {
                if(thisNode.getUserObject() instanceof IdLeaf)
                    ((IdLeaf) thisNode.getUserObject()).setEnvironment(((InnerNode) thisNode.getParent()).getEnvironment());
                else if(thisNode.getUserObject() instanceof ConstLeaf)
                    ((ConstLeaf) thisNode.getUserObject()).setEnvironment(((InnerNode) thisNode.getParent()).getEnvironment());
                else if(thisNode.getUserObject() instanceof AssignStatNode);
                else
                    ((InnerNode) thisNode.getUserObject()).setEnvironment(((InnerNode) thisNode.getParent()).getEnvironment());
                node = (TreeNode) thisNode.getUserObject();
            }//nel caso di nodo non innestato
            else if(!thisNode.getUserObject().equals("ProgramOp") && thisNode.getEnvironment() == null) {
                thisNode.setEnvironment(((InnerNode) thisNode.getParent()).getEnvironment());
            }
        }

        //effettuiamo i check per i costrutti del linguaggio
        if(node instanceof IdLeaf)
            identifierCheckig((IdLeaf) node);
        else if(node instanceof ConstLeaf)
            constChecking((ConstLeaf) node);
        else if(node instanceof VarDeclNode)
            declarationChecking((VarDeclNode) node);
        else if(node instanceof MainNode)
            mainFunChecking((MainNode) node);

        boolean programop = false;

        if (node.getChildCount() >= 0) {
            if(node instanceof ProgramNode)
                programop = true;
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                verifyIdName(n);
                visitForTypeChecking(n);
            }
        }
    }

    public void analyze(JTree ast) {
        TreeNode root = (TreeNode) ast.getModel().getRoot();
        try {
            visitForScoping(root, new ArrayList<>());
            visitForTypeChecking(root);
            //Se è andato tutto bene possiamo dare notype al ProgramNode
            ((ProgramNode) root).setType("notype");
        } catch (AlreadyDeclearedException | NotDeclearedException e) {
            e.printStackTrace();
        }
    }
}
