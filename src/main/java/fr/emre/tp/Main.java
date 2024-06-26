package fr.emre.tp;

import java.io.*;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        if (args.length > 0)
            yy = new LexicalAnalyzer(new FileReader(args[0]));
        else
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));
        parser p = new parser(yy);

        Node racine = (Node) p.parse().value;
        System.out.println(racine);


        Set<String> data;
        data = racine.getLet();
        String dataSet = "DATA SEGMENT \n";
        for (String i : data)
            dataSet += "\t" + i + " DD\n";
        dataSet += "DATA ENDS \n";

        String code = "CODE SEGMENT \n";
        code += racine.generate();
        code += "CODE ENDS";


        String arg = args[0].contains("pgcd") ? "pgcd.asm" : args[0].contains("prix") ? "prix.asm" : "factorielle.asm";

        try (FileWriter fileWriter = new FileWriter(arg, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
             printWriter.print(dataSet);
             printWriter.print(code);
        } catch (IOException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

}