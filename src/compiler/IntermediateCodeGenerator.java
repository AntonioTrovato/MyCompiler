package compiler;

import classes.innernodes.*;
import classes.leaves.*;
import com.sun.source.tree.Tree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IntermediateCodeGenerator {

    public IntermediateCodeGenerator() {
        this.numOfTabs = 0;
    }

    private void incrementTabs() {
        this.numOfTabs++;
    }

    private void decrementTabs() {
        this.numOfTabs--;
    }

    private File createCFile(String testFileName) {
        String relativePath = "test_files"+File.separator+"c_out"+File.separator;
        String extension = ".c";
        String path = relativePath + testFileName + extension;
        return new File(path);
    }

    private void createNewCFile(File cFile) throws IOException {
        if(!cFile.exists())
            cFile.createNewFile();
    }

    private void writeInitialCode(BufferedWriter cBufferedWriter) throws IOException {
        //scriviamo le include e le procedure più importanti
        writeIncludes(cBufferedWriter);
        writeInitialFunctions(cBufferedWriter);
    }

    private void writeIncludes(BufferedWriter cBufferedWriter) throws IOException {
        cBufferedWriter.write("#include <stdio.h>\n");
        cBufferedWriter.write("#include <stdlib.h>\n");
        cBufferedWriter.write("#include <string.h>\n");
        cBufferedWriter.write("#include <math.h>\n");
        cBufferedWriter.flush();
    }

    private void writeInitialFunctions(BufferedWriter cBufferedWriter) throws IOException {
        //scriviamo una funzione per effettuare concat di str+str/int/float
        writeIntToStringFunction(cBufferedWriter);
        writeFloatToStringFunction(cBufferedWriter);
        writeStringConcatFunction(cBufferedWriter);
    }

    private void writeStringConcatFunction(BufferedWriter cBufferedWriter) throws IOException {
        //creiamo la str da restituire e le aggiungiamo le due da concatenare
        cBufferedWriter.write("\nchar* my_strcat(char* str1, char* str2) {\n");
        cBufferedWriter.write("\tchar* new_str = (char*)calloc(256,sizeof(char));\n\n");
        cBufferedWriter.write("\tstrcat(new_str, str1);\n");
        cBufferedWriter.write("\tstrcat(new_str, str2);\n\n");
        cBufferedWriter.write("\treturn new_str;\n");
        cBufferedWriter.write("}\n\n");
        cBufferedWriter.flush();
    }

    private void writeIntToStringFunction(BufferedWriter cBufferedWriter) throws IOException {
        cBufferedWriter.write("\nchar* int_to_string(int num) {\n");
        //nprintf ci da la lunghezza se chiamata con i parametri: NULL, 0
        cBufferedWriter.write("\tchar* str = malloc(sizeof(char)*16);\n\n");
        //infine "stampiamo" il numero nella stringa
        cBufferedWriter.write("\tsprintf(str, \"%d\", num);\n\n");
        cBufferedWriter.write("\treturn str;\n");
        cBufferedWriter.write("}\n");
        cBufferedWriter.flush();
    }

    private void writeFloatToStringFunction(BufferedWriter cBufferedWriter) throws IOException {
        cBufferedWriter.write("\nchar* float_to_string(float num) {\n");
        //con la lunghezza facciamo la malloc
        cBufferedWriter.write("\tchar* str = malloc(sizeof(char)*16);\n\n");
        //infine "stampiamo" il numero nella stringa
        cBufferedWriter.write("\tsprintf(str, \"%f\", num);\n\n");
        cBufferedWriter.write("\treturn str;\n");
        cBufferedWriter.write("}\n");
        cBufferedWriter.flush();
    }

    private String writeTabs() {
        String tabs = "";
        for(int i = 0; i < this.numOfTabs; i++) {
            tabs += "\t";
        }
        return tabs;
    }

    private void writeIntoCFile(BufferedWriter cBufferedWriter, String stringToWrite) throws IOException {
        //togliamo spazi inutili
        stringToWrite = stringToWrite.trim();

        //scriviamo aggiungendo sempre un ;\n ed i tab necessari
        cBufferedWriter.write(stringToWrite + "\n\n");

        //flush
        cBufferedWriter.flush();
    }

    private String typeConverter(String type) {
        if(type.equals("INTEGER") || type.equals("BOOL"))
            return "int ";
        else if(type.equals("REAL"))
            return "float ";
        else if(type.equals("STRING"))
            return "char* ";
        else
            return "void";
    }

    private String mainCodeGenerator(MainNode mainNode) {
        String mainLine = "int main() {\n";
        incrementTabs();

        Enumeration children = mainNode.children();
        TreeNode child = null;

        ArrayList<TreeNode> declarations = new ArrayList<>();
        ArrayList<TreeNode> statements = new ArrayList<>();

        while (children.hasMoreElements()) {
            child = (TreeNode) children.nextElement();
            if(child instanceof StatNode)
                break;
            declarations.add(child);
        }
        Collections.reverse(declarations);

        if(child instanceof StatNode) {
            statements.add(child);
            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                statements.add(child);
            }
            Collections.reverse(statements);
        }

        if(!declarations.isEmpty())
            for(TreeNode varDeclNode : declarations)
                mainLine += writeTabs() + declarationCodeGeneration((VarDeclNode) varDeclNode);

        for(TreeNode statNode2 : statements)
            mainLine += writeTabs() + statementCodeGeneration((StatNode) statNode2);

        decrementTabs();
        return mainLine + "return 0;\n}\n";
    }

    private String elseCodeGenerator(ElseNode elseNode) {
        if(elseNode == null)
            return "\n";
        String elseLine = "";
        incrementTabs();

        //se non c'è else, l'else sarà vuoto
        Enumeration children = elseNode.children();
        if(children.hasMoreElements())
            elseLine = "else {\n";
        else return "";
        TreeNode child = null;
        //se ci sono figli i primi POTREBBERO essere dichiarazioni
        ArrayList<TreeNode> declarations = new ArrayList<>();
        ArrayList<TreeNode> statements = new ArrayList<>();

        while (children.hasMoreElements()) {
            child = (TreeNode) children.nextElement();
            if(child instanceof StatNode)
                break;
            declarations.add(child);
        }
        Collections.reverse(declarations);

        if(child instanceof StatNode) {
            statements.add(child);
            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                statements.add(child);
            }
            Collections.reverse(statements);
        }

        if(!declarations.isEmpty())
            for(TreeNode varDeclNode : declarations)
                elseLine += writeTabs() + declarationCodeGeneration((VarDeclNode) varDeclNode);

        for(TreeNode statNode2 : statements)
            elseLine += writeTabs() + statementCodeGeneration((StatNode) statNode2);

        decrementTabs();
        return elseLine + "}\n";
    }

    private String statementCodeGeneration(StatNode statNode) {
        String statementLine = "";

        Object statNodeObject = statNode.getUserObject();
        if(statNodeObject instanceof AssignStatNode) {
            //l'assegnamento avviene tra i figli
            Enumeration children = ((AssignStatNode) statNodeObject).children();
            IdLeaf assignId = (IdLeaf) children.nextElement();
            ExprNode assignExp = (ExprNode) children.nextElement();
            if(assignId.getType().equals("STRING")) {
                statementLine += "strcpy("+idCodeGenerator(assignId)+","+expressionCodeGeneration(assignExp)+");\n";
            }else {
                statementLine += writeTabs() + idCodeGenerator(assignId) + " = " + expressionCodeGeneration(assignExp) + ";\n";
            }
            return statementLine;
        }else if(statNodeObject instanceof IfStatNode) {
            incrementTabs();
            statementLine += "if(";
            //aggiungiamo l'espressione da valutare
            Enumeration children = ((IfStatNode) statNodeObject).children();
            ExprNode ifExpr = (ExprNode) children.nextElement();
            statementLine += expressionCodeGeneration(ifExpr) + ") {\n";
            //per le dichiarazioni
            TreeNode child = null;
            ArrayList<TreeNode> declarations = new ArrayList<>();
            ArrayList<TreeNode> statements = new ArrayList<>();

            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                if(child instanceof StatNode)
                    break;
                declarations.add(child);
            }
            Collections.reverse(declarations);
            ElseNode elseNode = null;

            if(child instanceof StatNode) {
                statements.add(child);
                while (children.hasMoreElements()) {
                    child = (TreeNode) children.nextElement();
                    if(child instanceof ElseNode) {
                        elseNode = (ElseNode) child;
                        break;
                    }
                    statements.add(child);
                }
                Collections.reverse(statements);
            }

            if(!declarations.isEmpty())
                for(TreeNode varDeclNode : declarations)
                    statementLine += writeTabs() + declarationCodeGeneration((VarDeclNode) varDeclNode);

            for(TreeNode statNode2 : statements)
                statementLine += writeTabs() + statementCodeGeneration((StatNode) statNode2);
            //potrebbe esserci l'else ancora
            decrementTabs();
            return statementLine + "}" + elseCodeGenerator(elseNode);
        }else if(statNodeObject instanceof WhileStatNode) {
            incrementTabs();
            statementLine += "while(";
            //aggiungiamo l'espressione da valutare
            Enumeration children = ((WhileStatNode) statNodeObject).children();
            ExprNode ifExpr = (ExprNode) children.nextElement();
            statementLine += expressionCodeGeneration(ifExpr) + ") {\n";
            //per le dichiarazioni
            TreeNode child = null;
            ArrayList<TreeNode> declarations = new ArrayList<>();
            ArrayList<TreeNode> statements = new ArrayList<>();

            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                if(child instanceof StatNode)
                    break;
                declarations.add(child);
            }
            Collections.reverse(declarations);

            if(child instanceof StatNode) {
                statements.add(child);
                while (children.hasMoreElements()) {
                    child = (TreeNode) children.nextElement();
                    statements.add(child);
                }
                Collections.reverse(statements);
            }

            if(!declarations.isEmpty())
                for(TreeNode varDeclNode : declarations)
                    statementLine += writeTabs() + declarationCodeGeneration((VarDeclNode) varDeclNode);

            for(TreeNode statNode2 : statements)
                statementLine += writeTabs() + statementCodeGeneration((StatNode) statNode2);
            decrementTabs();
            return statementLine + "}\n";
        }else if(statNodeObject instanceof CallFunNode) {
            return writeTabs() + callFunCodeGenerator((CallFunNode) statNodeObject) + ";\n";
        }else if(statNodeObject instanceof WriteStatNode) {
            //ci serve il primo figlio per il tipo di stampa
            Enumeration children = ((WriteStatNode) statNodeObject).children();
            WriteStatLeaf writeStatLeaf = (WriteStatLeaf) children.nextElement();
            String writeType = (String) writeStatLeaf.getUserObject();
            //a seconda del tipo abbiamo una formattazione
            String format;
            //del secondo (che è un id) serve soprattutto il tipo
            ExprNode nextElement = (ExprNode) children.nextElement();
            if(nextElement.getUserObject() instanceof String) {
                format = "%s";
                if (writeType.equals("WRITE"))
                    statementLine += writeTabs() + "printf(\"" + format + "\", " + expressionCodeGeneration(nextElement) + ");\n";
                else if (writeType.equals("WRITELN"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\n\", " + expressionCodeGeneration(nextElement) + ");\n";
                else if (writeType.equals("WRITET"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\t\", " + expressionCodeGeneration(nextElement) + ");\n";
                else if (writeType.equals("WRITEB"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\b\", " + expressionCodeGeneration(nextElement) + ");\n";
                return statementLine;
            }
            TreeNode child = (TreeNode) (nextElement).getUserObject();
            if(child instanceof IdLeaf) {
                IdLeaf idLeaf = (IdLeaf) child;
                String idType = idLeaf.getType();
                if (typeConverter(idType).equals("int "))
                    format = "%d";
                else if (typeConverter(idType).equals("float "))
                    format = "%f";
                else
                    format = "%s";

                //a seconda della write eseguiamo una printf
                if (writeType.equals("WRITE"))
                    statementLine += writeTabs() + "printf(\"" + format + "\", " + idLeaf.getEntry() + ");\n";
                else if (writeType.equals("WRITELN"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\n\", " + idLeaf.getEntry() + ");\n";
                else if (writeType.equals("WRITET"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\t\", " + idLeaf.getEntry() + ");\n";
                else if (writeType.equals("WRITEB"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\b\", " + idLeaf.getEntry() + ");\n";
            }else if(child instanceof ConstLeaf) {
                ConstLeaf constLeaf = (ConstLeaf) child;
                String constType = constLeaf.getType();
                if (typeConverter(constType).equals("int "))
                    format = "%d";
                else if (typeConverter(constType).equals("float "))
                    format = "%f";
                else
                    format = "%s";

                //a seconda della write eseguiamo una printf
                if (writeType.equals("WRITE"))
                    statementLine += writeTabs() + "printf(\"" + format + "\", " + constLeaf.getEntry().replace("'","\"") + ");\n";
                else if (writeType.equals("WRITELN"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\n\", " + constLeaf.getEntry().replace("'","\"") + ");\n";
                else if (writeType.equals("WRITET"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\t\", " + constLeaf.getEntry().replace("'","\"") + ");\n";
                else if (writeType.equals("WRITEB"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\b\", " + constLeaf.getEntry().replace("'","\"") + ");\n";
                return statementLine;
            }else if(child instanceof CallFunNode) {
                CallFunNode callFunNode = (CallFunNode) child;
                String funType = callFunNode.getType();
                if (typeConverter(funType).equals("int "))
                    format = "%d";
                else if (typeConverter(funType).equals("float "))
                    format = "%f";
                else
                    format = "%s";
                //a seconda della write eseguiamo una printf
                if (writeType.equals("WRITE"))
                    statementLine += writeTabs() + "printf(\"" + format + "\", " + callFunNode.getCalledFunName() + ");\n";
                else if (writeType.equals("WRITELN"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\n\", " + callFunNode.getCalledFunName() + ");\n";
                else if (writeType.equals("WRITET"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\t\", " + callFunNode.getCalledFunName() + ");\n";
                else if (writeType.equals("WRITEB"))
                    statementLine += writeTabs() + "printf(\"" + format + "\\b\", " + callFunNode.getCalledFunName() + ");\n";
                return statementLine;
            }
        }else if(statNodeObject instanceof ReadStatNode) {
            Enumeration children = ((ReadStatNode) statNodeObject).children();
            //il primo è un id
            //la lettura dipende dal tipo
            TreeNode readChild = null;
            ExprNode exprNode = null;
            String scanf = "";
            String printf = "";
            while (children.hasMoreElements()) {
                readChild = (TreeNode) children.nextElement();
                if(readChild instanceof IdLeaf) {
                    IdLeaf child = (IdLeaf) readChild;
                    if((child.getType().equals("INTEGER")) || (child.getType().equals("BOOL")))
                        scanf += writeTabs() + "scanf(\"%d\",&" + child.getEntry() + ");\n";
                    else if(child.getType().equals("REAL"))
                        scanf += writeTabs() + "scanf(\"%f\",&" + child.getEntry() + ");\n";
                    else if(child.getType().equals("STRING"))
                        scanf += writeTabs() + "scanf(\"%s\"," + child.getEntry() + ");\n";
                }else if(readChild instanceof ExprNode)
                    exprNode = (ExprNode) readChild;
            }
            if(exprNode != null) {
                String type = "";
                if(exprNode.getUserObject().equals("StrCatOp")) {
                    type = "STRING";
                }
                if(exprNode.getUserObject() instanceof ConstLeaf) {
                    type = ((ConstLeaf) exprNode.getUserObject()).getType();
                }
                if(exprNode.getUserObject() instanceof IdLeaf) {
                    type = ((IdLeaf) exprNode.getUserObject()).getType();
                }
                if(exprNode.getUserObject() instanceof CallFunNode) {
                    type = ((CallFunNode) exprNode.getUserObject()).getType();
                }
                type = typeConverter(type);
                if(type.equals("int "))
                    printf = writeTabs() + "printf(\"%d\"," + expressionCodeGeneration(exprNode) + ");\n";
                if(type.equals("float "))
                    printf = writeTabs() + "printf(\"%f\"," + expressionCodeGeneration(exprNode) + ");\n";
                if(type.equals("char* "))
                    printf = writeTabs() + "printf(\"%s\"," + expressionCodeGeneration(exprNode) + ");\n";
            }
            return printf + scanf + ";\n";
        }else if(statNodeObject instanceof String) {
            //questa è una return
            ExprNode returnExp = (ExprNode) statNode.children().nextElement();
            return writeTabs() + "return " + expressionCodeGeneration(returnExp) + ";\n";
        }
        decrementTabs();
        return statementLine;
    }

    private String funDefGeneration(FunNode funNode) {
        String funDefLine = "";
        incrementTabs();
        //tipo di ritorno
        String returnType = typeConverter(funNode.getType());
        //prendiamo il nome
        String funName = funNode.getNodeName();
        funDefLine += returnType + " " + funName + "(";
        TreeNode child = null;

        //prendiamo ora i parametri
        Enumeration children = funNode.children();
        children.nextElement();//saltiamo il nome della funzione
        //per sapere se ci sono più parametri
        boolean atLeastOneParam = false;
        while (children.hasMoreElements()) {
            //puntatore
            boolean puntatore = false;
            child = (TreeNode) children.nextElement();
            if(!(child instanceof ParDeclNode))
                break;
            if (atLeastOneParam)
                funDefLine += ", ";
            //prendiamo gli elementi che compongonio il parametro
            Enumeration paramChildren = ((ParDeclNode) child).children();
            while (paramChildren.hasMoreElements()) {
                TreeNode paramChild = (TreeNode) paramChildren.nextElement();
                //se è un puntatore
                if(paramChild instanceof OutLeaf)
                    puntatore = true;
                if(paramChild instanceof TypeLeaf)
                    funDefLine += typeCodeGenerator((TypeLeaf) paramChild);
                if(paramChild instanceof IdLeaf) {
                    funDefLine += idCodeGenerator((IdLeaf) paramChild);
                }
                atLeastOneParam = true;
            }
        }
        funDefLine += ") {\n";



        //saltiamo, se c'è, il tipo di ritorno
        if(child instanceof TypeLeaf)
            child = (TreeNode) children.nextElement();

        ArrayList<TreeNode> declarations = new ArrayList<>();
        ArrayList<TreeNode> statements = new ArrayList<>();

        if(child instanceof VarDeclNode) {
            declarations.add(child);
            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                if(child instanceof StatNode)
                    break;
                declarations.add(child);
            }
            Collections.reverse(declarations);
        }

        if(child instanceof StatNode) {
            statements.add(child);
            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                statements.add(child);
            }
            Collections.reverse(statements);
        }

        //adesso ci vogliono le dichiarazioni
        //ricordiamo che child è già un nodo (essendo fuori dal while)
        if(!declarations.isEmpty())
            for(TreeNode varDeclNode : declarations)
                funDefLine += writeTabs() + declarationCodeGeneration((VarDeclNode) varDeclNode);

        for(TreeNode statNode : statements)
            funDefLine += writeTabs() + statementCodeGeneration((StatNode) statNode);

        decrementTabs();
        return funDefLine + "}";
    }

    private String idCodeGenerator(IdLeaf idLeaf) {
        if(idLeaf.isPointer())
            return "*" + idLeaf.getEntry();
        return idLeaf.getEntry();
    }

    private String callFunCodeGenerator(CallFunNode callFunNode) {
        String callFunLine = "";

        Enumeration children = callFunNode.children();
        //il primo figlio è il nome
        String funName = ((IdLeaf) (TreeNode) children.nextElement()).getEntry();
        callFunLine += funName + "(";
        boolean atLeastOneArg = false;
        ArrayList<TreeNode> reverseChildren = new ArrayList<>();
        while (children.hasMoreElements()) {
            reverseChildren.add((TreeNode) children.nextElement());
        }
        Collections.reverse(reverseChildren);
        for(TreeNode child : reverseChildren) {;
            if(child instanceof IdLeaf) {
                if(atLeastOneArg)
                    callFunLine += ", ";
                //può essere un puntatore o no
                if(((IdLeaf) child).isPointer())
                    callFunLine += "&" + idCodeGenerator(((IdLeaf) child)).replace("*","");
                else
                    callFunLine += idCodeGenerator(((IdLeaf) child));
                atLeastOneArg = true;
            }else if(child instanceof ExprNode) {
                if(atLeastOneArg)
                    callFunLine += ", ";
                callFunLine += expressionCodeGeneration((ExprNode) child);
                atLeastOneArg = true;
            }
        }

        return callFunLine + ")";
    }

    private String expressionCodeGeneration(ExprNode exprNode) {
        String expressionLine = "";
        Object expressionElement = exprNode.getUserObject();
        //lo userobject sarà l'elemento dell'espressione
        if(expressionElement instanceof ConstLeaf) {
            //allora il valore è nell'entry
            if(((ConstLeaf) expressionElement).getEntry().equals("true"))
                expressionLine = "1";
            else if(((ConstLeaf) expressionElement).getEntry().equals("false"))
                expressionLine = "0";
            else if(((ConstLeaf) expressionElement).getType().equals("STRING")) {
                String s = ((ConstLeaf) expressionElement).getEntry().replace("'","\"");
                expressionLine = s;
            } else
                expressionLine = ((ConstLeaf) expressionElement).getEntry();
        }else if(expressionElement instanceof CallFunNode)
            //devo trascrivere la chiamata a funzione
            expressionLine = callFunCodeGenerator((CallFunNode) expressionElement);
        else if(expressionElement instanceof IdLeaf)
            //devo trascrivere la chiamata a funzione
            expressionLine = idCodeGenerator((IdLeaf) expressionElement);
        else if(expressionElement instanceof ExprNode)
            //devo trascrivere l'espressione tra parentesi
            expressionLine = "("+expressionCodeGeneration((ExprNode) expressionElement)+")";
        else if(expressionElement instanceof String) {//il caso delle operazioni
            String operation = (String) expressionElement;
            //poi prendiamo i figli dell'operazione
            Enumeration children = exprNode.children();
            ExprNode child1 = (ExprNode) children.nextElement();
            ExprNode child2 = null;
            String expr2 = "";
            if(children.hasMoreElements()) {
                child2 = (ExprNode) children.nextElement();
                expr2 = expressionCodeGeneration(child2);
            }
            String expr1 = expressionCodeGeneration(child1);
            if (operation.equals("AddOp"))
                expressionLine = expr1 + " + " + expr2;
            else if(operation.equals("MulOp"))
                expressionLine = expr1 + " * " + expr2;
            else if(operation.equals("DiffOp"))
                expressionLine = expr1 + " - " + expr2;
            else if(operation.equals("DivOp"))
                expressionLine = expr1 + " / " + expr2;
            else if(operation.equals("DivIntOp"))
                expressionLine = expr1 + " / " + expr2;
            else if(operation.equals("PowOp"))
                expressionLine = "pow(" + expr1 + ", " + expr2 + ")" ;
            else if(operation.equals("AndOp"))
                expressionLine = expr1 + " && " + expr2;
            else if(operation.equals("OrOp"))
                expressionLine = expr1 + " || " + expr2;
            else if(operation.equals("StrCatOp")) {
                //dobbiamo controllare i tipi se facciamo la cat
                //a sx dobbiamo avere per forza una str
                String secondArgument = "";
                Object child2Object = child2.getUserObject();
                if (child2Object instanceof IdLeaf) {
                    String child2Type = ((IdLeaf) child2Object).getType();
                    if(child2Type.equals("STRING"))
                        secondArgument = expr2;
                    else if((child2Type.equals("INTEGER")) || (child2Type.equals("BOOL")))
                        secondArgument = "int_to_string(" + expr2 + ")";
                    else if(child2Type.equals("REAL"))
                        secondArgument = "float_to_string(" + expr2 + ")";
                }else if(child2Object instanceof ConstLeaf) {
                    String child2Type = ((ConstLeaf) child2Object).getType();
                    if(child2Type.equals("STRING"))
                        secondArgument = expr2;
                    else if((child2Type.equals("INTEGER")) || (child2Type.equals("BOOL")))
                        secondArgument = "int_to_string(" + expr2 + ")";
                    else if(child2Type.equals("REAL"))
                        secondArgument = "float_to_string(" + expr2 + ")";
                }else if(child2Object instanceof CallFunNode) {
                    String child2Type = ((CallFunNode) child2Object).getType();
                    if(child2Type.equals("STRING"))
                        secondArgument = expr2;
                    else if((child2Type.equals("INTEGER")) || (child2Type.equals("BOOL")))
                        secondArgument = "int_to_string(" + expr2 + ")";
                    else if(child2Type.equals("REAL"))
                        secondArgument = "float_to_string(" + expr2 + ")";
                }else if(child2Object instanceof String) {
                    String child2Type = child2.getType();
                    if(child2Type != null) {
                        if (child2Type.equals("STRING"))
                            secondArgument = expr2;
                        else if ((child2Type.equals("INTEGER")) || (child2Type.equals("BOOL")))
                            secondArgument = "int_to_string(" + expr2 + ")";
                        else if (child2Type.equals("REAL"))
                            secondArgument = "float_to_string(" + expr2 + ")";
                    }
                }
                expressionLine = "my_strcat(" + expr1 + ", " + secondArgument + ")";
            }else if(operation.equals("GTOp"))
                expressionLine = expr1 + " > " + expr2;
            else if(operation.equals("GEOp"))
                expressionLine = expr1 + " >= " + expr2;
            else if(operation.equals("LTOp"))
                expressionLine = expr1 + " < " + expr2;
            else if(operation.equals("LEOp"))
                expressionLine = expr1 + " <= " + expr2;
            else if(operation.equals("EQOp")) {
                String child1Type = "";
                String child2Type = "";
                if(child1.getUserObject() instanceof IdLeaf)
                    child1Type = ((IdLeaf) child1.getUserObject()).getType();
                if(child1.getUserObject() instanceof ConstLeaf)
                    child1Type = ((ConstLeaf) child1.getUserObject()).getType();
                if(child1.getUserObject() instanceof CallFunNode)
                    child1Type = ((CallFunNode) child1.getUserObject()).getType();
                if(child2.getUserObject() instanceof IdLeaf)
                    child2Type = ((IdLeaf) child2.getUserObject()).getType();
                if(child2.getUserObject() instanceof ConstLeaf)
                    child2Type = ((ConstLeaf) child2.getUserObject()).getType();
                if(child2.getUserObject() instanceof CallFunNode)
                    child2Type = ((CallFunNode) child2.getUserObject()).getType();
                if(child1Type.equals("STRING") && child2Type.equals("STRING"))
                    expressionLine = "strcmp("+expr1+","+expr2+")==0";
                else
                    expressionLine = expr1 + " == " + expr2;
            }else if(operation.equals("NEOp")) {
                String child1Type = "";
                String child2Type = "";
                if(child1.getUserObject() instanceof IdLeaf)
                    child1Type = ((IdLeaf) child1.getUserObject()).getType();
                if(child1.getUserObject() instanceof ConstLeaf)
                    child1Type = ((ConstLeaf) child1.getUserObject()).getType();
                if(child1.getUserObject() instanceof CallFunNode)
                    child1Type = ((CallFunNode) child1.getUserObject()).getType();
                if(child2.getUserObject() instanceof IdLeaf)
                    child2Type = ((IdLeaf) child2.getUserObject()).getType();
                if(child2.getUserObject() instanceof ConstLeaf)
                    child2Type = ((ConstLeaf) child2.getUserObject()).getType();
                if(child2.getUserObject() instanceof CallFunNode)
                    child2Type = ((CallFunNode) child2.getUserObject()).getType();
                if(child1Type.equals("STRING") && child2Type.equals("STRING"))
                    expressionLine = "strcmp("+expr1+","+expr2+")!=0";
                else
                    expressionLine = expr1 + " != " + expr2;
            }else if(operation.equals("UminusOp"))
                expressionLine = "-" + expr1;
            else if(operation.equals("NotOp"))
                expressionLine = "!" + expr1;

        }
        return expressionLine;
    }

    private String typeCodeGenerator(TypeLeaf typeLeaf) {
        //lo user object sarà il tipo (INTEGER->int, REAL->double, STRING->string, BOOL->bool)
        String type = (String) (typeLeaf.getUserObject());
        return typeConverter(type);
    }

    private String declarationCodeGeneration(VarDeclNode varDeclNode) {
        String declarationLine = "";
        //un flag per ricordare se prima avevo un id
        boolean prevId = false;
        Enumeration children = varDeclNode.children();
        //nel caso di più var
        Enumeration children2 = varDeclNode.children();
        boolean isVar = false;
        //serve un booleano per le stringhe (malloc)
        boolean malloc = false;
        boolean onestring = false;
        String strcpy = "";
        IdLeaf strId = null;
        //nel caso di var globali
        boolean global = false;
        if(varDeclNode.getParent()instanceof ProgramNode)
            global = true;
        while (children.hasMoreElements()) {
            TreeNode child = (TreeNode) children.nextElement();
            if(child instanceof TypeLeaf) {
                if(global && typeCodeGenerator((TypeLeaf) child).equals("char* "))
                    declarationLine = "char ";
                else
                    declarationLine = typeCodeGenerator((TypeLeaf) child);
            }else if(child instanceof IdLeaf) {
                    if(isVar) {
                        if(global && typeConverter(((IdLeaf) child).getType()).equals("char* ") )
                            declarationLine += "char " + idCodeGenerator((IdLeaf) child);
                        else
                            declarationLine += typeConverter(((IdLeaf) child).getType()) + " " + idCodeGenerator((IdLeaf) child);
                    }else if (!prevId)
                        declarationLine += idCodeGenerator((IdLeaf) child);
                    else {
                        if(((IdLeaf) child).getType().equals("STRING")) {
                            if(malloc && !global)
                                declarationLine += " = (char*) malloc(128*sizeof(char))";
                            else if(malloc && global)
                                declarationLine += "[128] ";
                            if(!global)
                                declarationLine += ", *" + idCodeGenerator((IdLeaf) child);
                            else
                                declarationLine += ", " + idCodeGenerator((IdLeaf) child);
                        }else
                            declarationLine += ", " + idCodeGenerator((IdLeaf) child);
                    }
                    if(((IdLeaf) child).getType().equals("STRING")) {
                        malloc = true;
                        strId = (IdLeaf)child;
                        onestring = true;
                    }
                    prevId = true;
            }else if(child instanceof ExprNode) {
                //qui dobbiamo valutare l'espressione/id
                if(malloc && !global) {
                    declarationLine += " = (char*) malloc(128*sizeof(char))";
                    strcpy += ";\nstrcpy("+strId.getEntry()+","+expressionCodeGeneration((ExprNode) child)+");\n";
                }else if(malloc && global) {
                    declarationLine += "[128] ";
                    String expressionCode = expressionCodeGeneration((ExprNode) child);
                    declarationLine += " = " + expressionCode;
                }else {
                    String expressionCode = expressionCodeGeneration((ExprNode) child);
                    declarationLine += " = " + expressionCode;
                }
                malloc = false;
                onestring = false;
            }else if(child instanceof VarLeaf)
                isVar = true;
            else if(child instanceof ConstLeaf) {
                if(malloc && !global) {
                    declarationLine += " = (char*) malloc(128*sizeof(char))";
                    strcpy += ";\nstrcpy("+strId.getEntry()+","+expressionCodeGeneration(new ExprNode((ConstLeaf) child))+");\n";
                }else if(malloc && global) {
                    declarationLine += "[128] ";
                    String constCode = expressionCodeGeneration(new ExprNode((ConstLeaf) child));
                    declarationLine += " = " + constCode;
                }else {
                    String constCode = expressionCodeGeneration(new ExprNode((ConstLeaf) child));
                    declarationLine += " = " + constCode;
                }
                if(isVar)
                    declarationLine += ";\n";
                onestring = false;
                malloc = false;
            }
        }
        if(onestring && !global)
            return declarationLine + " = (char*) malloc(128*sizeof(char))" + strcpy + ";\n";
        else if(onestring && global)
            return declarationLine + "[128];\n";
        return declarationLine + strcpy + ";\n";
    }

    public void visitForCodeGeneration(TreeNode treeNode, BufferedWriter cBufferedWriter) throws IOException {
        Enumeration children = treeNode.children();
        TreeNode child = null;

        ArrayList<TreeNode> declarations = new ArrayList<>();
        ArrayList<TreeNode> functions = new ArrayList<>();

        while (children.hasMoreElements()) {
            child = (TreeNode) children.nextElement();
            if(child instanceof FunNode || child instanceof MainNode)
                break;
            declarations.add(child);
        }
        Collections.reverse(declarations);

        if((child instanceof FunNode)) {
            functions.add(child);
            while (children.hasMoreElements()) {
                child = (TreeNode) children.nextElement();
                if(child instanceof MainNode)
                    break;
                functions.add(child);
            }
            Collections.reverse(functions);
        }
        //child sarà il main

        if(!declarations.isEmpty())
            for(TreeNode varDeclNode : declarations) visitForCodeGeneration(varDeclNode,cBufferedWriter);

        for(TreeNode statNode : functions)visitForCodeGeneration(statNode,cBufferedWriter);

        if(child instanceof MainNode)
            visitForCodeGeneration(child,cBufferedWriter);

        //variabili globali
        if((treeNode instanceof VarDeclNode) && ((treeNode).getParent() instanceof ProgramNode))
            writeIntoCFile(cBufferedWriter, declarationCodeGeneration((VarDeclNode) treeNode));
        //lista di funzioni
        else if(treeNode instanceof FunNode)
            writeIntoCFile(cBufferedWriter, funDefGeneration((FunNode) treeNode));
        else if(treeNode instanceof MainNode)
            writeIntoCFile(cBufferedWriter, mainCodeGenerator((MainNode) treeNode));
    }

    public void generate(JTree ast, String testFileName) {
        TreeNode astRoot = (TreeNode) ast.getModel().getRoot();
        File cFile = createCFile(testFileName);
        try {
            createNewCFile(cFile);
            BufferedWriter cBufferFileWriter = new BufferedWriter(new FileWriter(cFile));
            writeInitialCode(cBufferFileWriter);
            visitForCodeGeneration(astRoot, cBufferFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int numOfTabs;

}
