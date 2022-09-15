

import classes.innernodes.ProgramNode;
import compiler.IntermediateCodeGenerator;
import compiler.SemanticAnalyzer;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;

public class Main {
    //Main

    private static String obtainTestFileName(String argumentTest) {
        //togliamo il percorso
        String fileNameAndExt = (new File(argumentTest)).getName();
        //togliamo l'estensione
        String testFileName = fileNameAndExt.replaceAll(".txt","");

        return testFileName;
    }

    static public void main(String argv[]) {
        try {
            //analisi lessicale e sintattica
            parser p = new parser(new MyFunLexer(new FileReader(argv[0])));
            JTree ast = new JTree((ProgramNode) p.parse().value);
            //analisi semantica
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(ast);
            //generazione codice intermedio
            IntermediateCodeGenerator intermediateCodeGenerator = new IntermediateCodeGenerator();
            String testFileName = obtainTestFileName(argv[0]);
            intermediateCodeGenerator.generate(ast,testFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
