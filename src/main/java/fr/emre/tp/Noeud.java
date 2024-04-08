package fr.emre.tp;

import java.util.HashSet;
import java.util.Set;

public class Noeud {

    public enum TypeNoeud {SEQUENCE, EXPRESSION, EXPR, VAR, INT, OUTPUT, INPUT, NIL}

    public static int COMPTEUR = 0;

    public TypeNoeud type;
    public String valeur;
    public Noeud gauche, droite;

    public Noeud(TypeNoeud t, String v) {
        type = t;
        valeur = v;
        gauche = null;
        droite = null;

    }

    public Noeud(TypeNoeud t, String v, Noeud e1, Noeud e2) {
        type = t;
        valeur = v;
        gauche = e1;
        droite = e2;
    }

    @Override
    public String toString() {
        String str = "";
        if (!(gauche == null && droite == null))
            str = "(";

        str = str.concat(valeur);

        if (gauche != null) {
            str = str.concat(" " + gauche.toString() + " ");
        }
        if (droite != null) {
            str = str.concat(" " + droite.toString() + " ");
        }
        if (!(gauche == null && droite == null))
            str = str.concat(")");

        return str;
    }

    public Set<String> getLet() {
        Set<String> s = new HashSet<String>();

        if ((this.type) == TypeNoeud.EXPRESSION && (this.valeur) == "let") {
            s.add(this.gauche.valeur);
        }

        Set<String> setLeft = this.gauche != null ? this.gauche.getLet() : null;
        Set<String> setRight = this.droite != null ? this.droite.getLet() : null;

        if (setLeft != null)
            s.addAll(setLeft);
        if (setRight != null)
            s.addAll(setRight);

        return s;
    }

    public String generer() {
        switch (this.type) {
            case SEQUENCE:
                return ( gauche==null?"":gauche.generer())+(droite==null?"":droite.generer());
            case EXPRESSION:
                return genererExpression();
            case EXPR:
                return genererExpr();
            case VAR:
            case INT:
                return "\t\tmov eax, " + this.valeur + "\n" +
                        "\t\tpush eax\n";
            case OUTPUT:
                return genererExpr();
            case INPUT:
                return genererExpr();
            case NIL:
                return genererExpr();
            default:
                return genererExpr();
        }

    }

    public String genererExpression() {
        String res = "";
        int tempCpt = 0;
        String expressionGauche;
        String expressionDroite;
        Noeud temp;
        switch (this.valeur) {
            case "let":
                String fils_droit = this.droite.generer();
                res +=
                        fils_droit +
                                "\t\tmov " + this.gauche.valeur +
                                ", eax\n"
                ;
                break;
            case "while":
                tempCpt = COMPTEUR++;
                res += "\tetiq_debut_while_" + tempCpt + ":\n" +
                        this.gauche.generer() +
                        "\t\tjz etiq_fin_while_" + tempCpt + "\n" +
                        this.droite.generer() +
                        "\t\tjmp etiq_debut_while_" + tempCpt + "\n" +
                        "\tetiq_fin_while_" + tempCpt + ":\n";
                break;
            case "if":
                tempCpt = COMPTEUR++;
                res += this.gauche.generer() +
                        "\t\tjz etiq_if_sinon_" + tempCpt + "\n" +
                        this.droite.gauche.generer() +
                        "\t\tjmp etiq_if_fin_" + tempCpt + "\n" +
                        "\tetiq_if_sinon_" + tempCpt + ":\n" +
                        (this.droite.droite == null ? "" : this.droite.droite.generer()) +
                        "\tetiq_if_fin_" + tempCpt + ":\n";
                break;
        }


        return res;
    }

    public String genererExpr() {
        String res = "";
        String expressionGauche;
        String expressionDroite;
        int tempCpt = 0;
        Noeud temp;
        switch (this.valeur) {
            case "+":
                expressionGauche = this.gauche.generer();
                expressionDroite = this.droite.generer();
                res += expressionGauche + expressionDroite +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tadd eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "-":
                expressionGauche = this.gauche.generer();
                expressionDroite = this.droite.generer();
                res += expressionGauche + expressionDroite +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tsub eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "*":
                expressionGauche = this.gauche.generer();
                expressionDroite = this.droite.generer();
                res += expressionGauche + expressionDroite +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tmul eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "/":
                expressionGauche = this.gauche.generer();
                expressionDroite = this.droite.generer();
                res += expressionGauche + expressionDroite +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tdiv eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "<":
                tempCpt = COMPTEUR++;
                temp = new Noeud(TypeNoeud.EXPR, "-", this.gauche, this.droite);
                res += temp.generer() +
                        "\t\tjl etiq_debut_lt_" + tempCpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp etiq_fin_lt_" + tempCpt + "\n" +
                        "\tetiq_debut_lt_" + tempCpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tetiq_fin_lt_" + tempCpt + ":\n";
                break;
            case "<=":
                tempCpt = COMPTEUR++;
                temp = new Noeud(TypeNoeud.EXPR, "-", this.gauche, this.droite);
                res += temp.generer() +
                        "\t\tjg etiq_debut_lte_" + tempCpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp etiq_fin_lte_" + tempCpt + "\n" +
                        "\tetiq_debut_lte_" + tempCpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tetiq_fin_lte_" + tempCpt + ":\n";
                break;
            case ">":
                tempCpt = COMPTEUR++;
                temp = new Noeud(TypeNoeud.EXPR, "-", this.droite, this.gauche);
                res += temp.generer() +
                        "\t\tjl etiq_debut_gt_" + tempCpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp etiq_fin_gt_" + tempCpt + "\n" +
                        "\tetiq_debut_gt_" + tempCpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tetiq_fin_gt_" + tempCpt + ":\n";
                break;
            case ">=":
                tempCpt = COMPTEUR++;
                temp = new Noeud(TypeNoeud.EXPR, "-", this.droite, this.gauche);
                res += temp.generer() +
                        "\t\tjg etiq_debut_gte_" + tempCpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp etiq_fin_gte_" + tempCpt + "\n" +
                        "\tetiq_debut_gte_" + tempCpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tetiq_fin_gte_" + tempCpt + ":\n";
                break;
            case "and":
                tempCpt = COMPTEUR++;
                expressionGauche = this.gauche.generer();
                expressionDroite = this.droite.generer();
                res +=
                        expressionGauche +
                                "\t\tjz etiq_fin_and_" + tempCpt + "\n" +
                                expressionDroite
                                + "\tetiq_fin_and_" + tempCpt + ":\n";
                break;

        }
        return res;
    }


}