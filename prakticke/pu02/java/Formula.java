
import java.lang.System;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

//hlavna tireda formula
//Formula<virtual>
public abstract class Formula {
    public Formula() {
    }

    public abstract Formula[] subf();
        //Formula[] pole = {};
        //return pole;

    //virtualne metody
    public abstract Formula copy();
    public abstract String toString();
    // vrati retazcovu reprezentaciu formuly

    public abstract Boolean isSatisfied(Map<String, Boolean> v);
       // return true;
    // vrati true, ak je formula splnen

    public abstract Boolean equals(Formula other);

    // vrati true ak je tato formula rovnaka
    public abstract Integer deg();

    public abstract Formula substitute(Formula what, Formula replacement);

    // vrati mnozinu nazvov premennych
    public abstract Set<String> vars();

}


class Variable extends Formula {
    String name = "";

    Variable(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Variable copy(){
        return new Variable(name);
    }


    public Formula[] subf() {
        return new Formula[0];
    }


    public String toString() {
        return name;
    }

    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        premenne.add(name);
        return premenne;
    }


    public Boolean isSatisfied(Map<String, Boolean> v)
    {
        return v.get(name);
    }

    public Boolean equals(Formula other){
        return other.toString().equals(this.toString());
    }

    @Override
    public Integer deg() {
        return 0;
    }

    @Override
    public Formula substitute(Formula what, Formula replacement) {
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Variable(name());
    }

}

class Negation extends Formula {
    private Formula _original;

    Negation(Formula originalFormula) {
        this._original = originalFormula;
    }

    public Formula originalFormula() {
        return _original;
    }

    @Override
    public Formula[] subf() {
        Formula[] pole = new Formula[1];
        pole[0] = originalFormula();
        return pole;
    }

    public Negation copy(){
        return new Negation(_original);
    }

    public String toString() {

        return "-" + _original.toString();
    }
    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        return _original.vars();

    }
    public Boolean isSatisfied(Map<String, Boolean> v)
    {
        if(_original.isSatisfied(v)){
            return false;};
        return true;
    }

    public Boolean equals(Formula other) {

        return this.toString() == other.toString();
    }

    public Integer deg() {
        return 1 +  _original.deg();
    }

    public Formula substitute(Formula what, Formula replacement) {
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Negation(originalFormula().substitute(what, replacement));    }

}

class Conjunction extends Formula {
    Formula poleFormul[];

    Conjunction(){}
    Conjunction(Formula[] conjuncts) {
        this.poleFormul = conjuncts;
    }

    @Override
    public Formula[] subf() {
        Formula[] pole = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            pole[i] = poleFormul[i];
        }

        return pole;
    }

    @Override
    public Formula copy() {
        Formula[] novePole = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            novePole[i] = poleFormul[i].copy();
        }
        return new Conjunction(novePole);
    }


    public String toString() {
        String vysledok = "(";
        for (int i = 0; i < poleFormul.length; i++) {
            vysledok += poleFormul[i].toString() + "&";
        }
        vysledok = vysledok.substring(0, vysledok.length() - 1);
        vysledok += ")";
        return vysledok;
    }
    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        for (int i = 0; i < poleFormul.length; i++) {
            premenne.addAll((poleFormul[i].vars()));
        }
        return premenne;
    }
    public Boolean isSatisfied(Map<String, Boolean> v) {
        for (int i = 0; i < poleFormul.length; i++) {
            if(!poleFormul[i].isSatisfied(v)){
                return false;
            }
        }
        return true;
    }

    public Boolean equals(Formula other) {
        return other.toString().equals(this.toString());
    }
    public Integer deg() {
        int pocet = 0;
        for (int i = 0; i < poleFormul.length; i++) {
            pocet += 1 + poleFormul[i].deg();
        }
        return pocet-1;
    }

    public Formula substitute(Formula what, Formula replacement) {
        Formula[] nahradene = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            nahradene[i] = poleFormul[i].substitute(what, replacement);
        }
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Conjunction(nahradene);
    }

}

//podtireda disunkcia
class Disjunction extends Formula {
    Formula poleFormul[];

    Disjunction(Formula[] disjuncts) {
        this.poleFormul = disjuncts;
    }

    @Override
    public Formula[] subf() {
        Formula[] pole = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            pole[i] = poleFormul[i];
        }

        return pole;
    }

    @Override
    public Formula copy() {
        Formula[] novePole = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            novePole[i] = poleFormul[i].copy();
        }
        return new Disjunction(novePole);
    }

    public String toString() {
        String vysledok = "(";
        for (int i = 0; i < poleFormul.length; i++) {
            vysledok += poleFormul[i].toString() + "|";
        }
        vysledok = vysledok.substring(0, vysledok.length() - 1);
        vysledok += ")";
        return vysledok;
    }
    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        for (int i = 0; i < poleFormul.length; i++) {
            premenne.addAll((poleFormul[i].vars()));
        }
        return premenne;
    }
    public Boolean isSatisfied(Map<String, Boolean> v) {
        for (int i = 0; i < poleFormul.length; i++) {
            if(poleFormul[i].isSatisfied(v)){
                return true;
            }
        }
        return false;
    }

    public Boolean equals(Formula other) {
        return other.toString().equals(this.toString());
    }

    public Integer deg() {
        int pocet = 0;
        for (int i = 0; i < poleFormul.length; i++) {
            pocet += 1+poleFormul[i].deg();
        }
        return pocet-1;
    }

    public Formula substitute(Formula what, Formula replacement) {
        Formula[] nahradene = new Formula[poleFormul.length];
        for (int i = 0; i < poleFormul.length; i++) {
            nahradene[i] = poleFormul[i].substitute(what, replacement);
        }
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Disjunction(nahradene);
    }

}


//podtrieda binarnaFormula
 abstract class BinaryFormula extends Formula {
    //new Formula()
    Formula lava = null;
    Formula prava = null;

    public BinaryFormula() {
        lava = null;
        prava = null;
    }

    public BinaryFormula(Formula leftSide, Formula rightSide) {
        lava = leftSide;
        prava = rightSide;
    }

    public abstract BinaryFormula copy();
    public Formula leftSide() {
        return lava;
    }

    public Formula rightSide() {
        return prava;
    }

    public abstract Formula[]  subf();

    @Override
    public abstract String toString();

    public abstract Boolean isSatisfied(Map<String, Boolean> v);

    public Boolean equals(Formula other) {
        return other.toString().equals(this.toString());
    }

    public abstract Integer deg();

    public abstract Formula substitute(Formula what, Formula replacement);

    @Override
    public abstract Set<String> vars();
}


//podtrieda implikacia
class Implication extends BinaryFormula {

    public Implication(Formula leftSide, Formula rightSide) {
        lava = leftSide;
        prava = rightSide;
    }

    @Override
    public Implication copy() {
        return new Implication(lava.copy(), prava.copy());
    }

    public Formula leftSide() {
        return lava;
    }

    public Formula rightSide() {
        return prava;
    }

    public String toString() {

        return "(" + this.lava.toString() + "->" + this.prava.toString() + ")";
    }

    public Formula[] subf() {
        Formula[] pole = new Formula[2];
        pole[0] = lava;
        pole[1] = prava;
        return pole;
    }
    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        premenne.addAll((lava.vars()));
        premenne.addAll((prava.vars()));
        return premenne;
    }
    public Boolean isSatisfied(Map<String, Boolean> v) {
        if((lava.isSatisfied(v)) && (!prava.isSatisfied(v))){
            return false;
        };
        return true;
    }

    public Boolean equals(Formula other) {
        return other.toString().equals(this.toString());
    }

    public Integer deg() {
        return 1 + lava.deg() + prava.deg();
    }

    public Formula substitute(Formula what, Formula replacement) {
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Implication(lava.substitute(what, replacement), prava.substitute(what, replacement));
    }

}

 class Equivalence extends BinaryFormula {
    public Equivalence() {
        lava = null;
        prava = null;
    }

    public Equivalence(Formula leftSide, Formula rightSide) {
        lava = leftSide;
        prava = rightSide;
    }

    public Equivalence copy()
    {
        return new Equivalence(lava.copy(), prava.copy());
    }
    public Formula leftSide() {
        return lava;
    }

    public Formula rightSide() {
        return prava;
    }

    public String toString() {
        return "(" + this.lava.toString() + "<->" + this.prava.toString() + ")";
    }

    public Formula[] subf() {
        Formula[] pole = new Formula[2];
        pole[0] = lava;
        pole[1] = prava;
        return pole;
    }
    public Set<String> vars() {
        HashSet<String> premenne = new HashSet<>();
        premenne.addAll((lava.vars()));
        premenne.addAll((prava.vars()));
        return premenne;
    }
    public Boolean isSatisfied(Map<String, Boolean> v) {

        if((lava.isSatisfied(v)&&prava.isSatisfied(v) || (!lava.isSatisfied(v) && !prava.isSatisfied(v)))){
            return true;
        };
        return false;
    }

    public Boolean equals(Formula other) {
        return other.toString().equals(this.toString());
    }

    public Integer deg() {
        return 1 + lava.deg() + prava.deg();
    }

    public Formula substitute(Formula what, Formula replacement) {
        if(this.equals(what)) {
            return replacement.copy();
        }
        return new Equivalence(lava.substitute(what, replacement), prava.substitute(what, replacement));
    }

}







