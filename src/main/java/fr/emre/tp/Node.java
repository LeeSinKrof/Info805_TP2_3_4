package fr.emre.tp;

import java.util.HashSet;
import java.util.Set;

public class Node {

    public enum TypeNode {SEQUENCE, EXPRESSION, EXPR, VAR, INT, OUTPUT, INPUT, NIL}

    public static int COMPTEUR = 0;

    public TypeNode type;
    public String value;
    public Node left, right;

    public Node(TypeNode t, String v) {
        type = t;
        value = v;
        left = null;
        right = null;
    }

    public Node(TypeNode t, String v, Node e1, Node e2) {
        type = t;
        value = v;
        left = e1;
        right = e2;
    }

    @Override
    public String toString() {
        String s = "";
        if (!(left == null && right == null))
            s = "(";

        s = s.concat(value);

        if (left != null) {
            s = s.concat(" " + left + " ");
        }
        if (right != null) {
            s = s.concat(" " + right + " ");
        }
        if (!(left == null && right == null))
            s = s.concat(")");

        return s;
    }

    public Set<String> getLet() {
        Set<String> s = new HashSet<String>();

        if ((this.type) == TypeNode.EXPRESSION && (this.value) == "let") {
            s.add(this.left.value);
        }

        Set<String> setLeft = this.left != null ? this.left.getLet() : null;
        Set<String> setRight = this.right != null ? this.right.getLet() : null;

        if (setLeft != null)
            s.addAll(setLeft);
        if (setRight != null)
            s.addAll(setRight);

        return s;
    }

    public String generate() {
        switch (this.type) {
            case SEQUENCE:
                return ( left==null?"":left.generate())+(right==null?"":right.generate());
            case EXPRESSION:
                return generateExpression();
            case EXPR:
                return generateExpr();
            case VAR:
            case INT:
                return "\t\tmov eax, " + this.value + "\n" +
                        "\t\tpush eax\n";
            case OUTPUT:
                return generateExpr();
            case INPUT:
                return generateExpr();
            case NIL:
                return generateExpr();
            default:
                return generateExpr();
        }

    }

    public String generateExpression() {
        String res = "";
        int cpt = 0;
        switch (this.value) {
            case "let":
                String child_right = this.right.generate();
                res +=
                        child_right +
                                "\t\tmov " + this.left.value +
                                ", eax\n"
                ;
                break;
            case "while":
                cpt = COMPTEUR++;
                res += "\tdebut_while_" + cpt + ":\n" +
                        this.left.generate() +
                        "\t\tjz sortie_while_" + cpt + "\n" +
                        this.right.generate() +
                        "\t\tjmp debut_while_" + cpt + "\n" +
                        "\tsortie_while_" + cpt + ":\n";
                break;
            case "if":
                cpt = COMPTEUR++;
                res += this.left.generate() +
                        "\t\tjz if_sinon_" + cpt + "\n" +
                        this.right.left.generate() +
                        "\t\tjmp if_sortie_" + cpt + "\n" +
                        "\tif_sinon_" + cpt + ":\n" +
                        (this.right.right == null ? "" : this.right.right.generate()) +
                        "\tif_sortie_" + cpt + ":\n";
                break;
        }
        return res;
    }

    public String generateExpr() {
        String res = "";
        String expressionLeft;
        String expressionRight;
        int cpt = 0;
        Node temp;
        switch (this.value) {
            case "+":
                expressionLeft = this.left.generate();
                expressionRight = this.right.generate();
                res += expressionLeft + expressionRight +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tadd eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "-":
                expressionLeft = this.left.generate();
                expressionRight = this.right.generate();
                res += expressionLeft + expressionRight +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tsub eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "*":
                expressionLeft = this.left.generate();
                expressionRight = this.right.generate();
                res += expressionLeft + expressionRight +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tmul eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "/":
                expressionLeft = this.left.generate();
                expressionRight = this.right.generate();
                res += expressionLeft + expressionRight +
                        "\t\tpop ebx\n" +
                        "\t\tpop eax\n" +
                        "\t\tdiv eax, ebx\n" +
                        "\t\tpush eax\n";
                break;
            case "<":
                cpt = COMPTEUR++;
                temp = new Node(TypeNode.EXPR, "-", this.left, this.right);
                res += temp.generate() +
                        "\t\tjl debut_lt_" + cpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp sortie_lt_" + cpt + "\n" +
                        "\tdebut_lt_" + cpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tsortie_lt_" + cpt + ":\n";
                break;
            case "<=":
                cpt = COMPTEUR++;
                temp = new Node(TypeNode.EXPR, "-", this.left, this.right);
                res += temp.generate() +
                        "\t\tjg debut_lte_" + cpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp sortie_lte_" + cpt + "\n" +
                        "\tdebut_lte_" + cpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tsortie_lte_" + cpt + ":\n";
                break;
            case ">":
                cpt = COMPTEUR++;
                temp = new Node(TypeNode.EXPR, "-", this.right, this.left);
                res += temp.generate() +
                        "\t\tjl debut_gt_" + cpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp sortie_gt_" + cpt + "\n" +
                        "\tdebut_gt_" + cpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tsortie_gt_" + cpt + ":\n";
                break;
            case ">=":
                cpt = COMPTEUR++;
                temp = new Node(TypeNode.EXPR, "-", this.right, this.left);
                res += temp.generate() +
                        "\t\tjg debut_gte_" + cpt + "\n" +
                        "\t\tmov eax,0\n" +
                        "\t\tjmp sortie_gte_" + cpt + "\n" +
                        "\tdebut_gte_" + cpt + ":\n" +
                        "\t\tmov eax,1\n" +
                        "\tsortie_gte_" + cpt + ":\n";
                break;
            case "and":
                cpt = COMPTEUR++;
                expressionLeft = this.left.generate();
                expressionRight = this.right.generate();
                res +=
                        expressionLeft +
                                "\t\tjz sortie_and_" + cpt + "\n" +
                                expressionRight
                                + "\tsortie_and_" + cpt + ":\n";
                break;

        }
        return res;
    }


}