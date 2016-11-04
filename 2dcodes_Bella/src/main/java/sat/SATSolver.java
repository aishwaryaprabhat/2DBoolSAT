package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

public class SATSolver {

    public static Environment solve(Formula formula) {
        ImList<Clause> clauses = formula.getClauses();              //get the clauses in formula
        return solve (clauses, new Environment());                  //pass the clauses to overloaded solve
    }

    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses.isEmpty()) {                                    //if there is no clause, problem is SATISFIABLE
            return env;                                             //return variable bindings

        } else {                                                    //if there is clause
            Clause sc = null;                                       //sc = smallest clause var
            for (Clause c : clauses) {
                if (c.isEmpty()) {                                  //if the clause is empty
                    return null;                                    //return null, UNSATISFIABLE
                }
                                                                    //FIND SMALLEST CLAUSE
                if ((sc == null) || (c.size() < sc.size())) {       //if c is smaller than current sc
                    sc = c;                                         //reassign sc
                }
            }

            Literal l = sc.chooseLiteral();                         //choose first literal of sc
            Variable var = l.getVariable();                         //get the variable of the literal (literal might be pos/neg)

            if (sc.isUnit()) {                                      //if the smallest clause comprises of one literal

                //set unit clause to TRUE
                if (l.equals(PosLiteral.make(l.getVariable()))) {   //if the single literal is positive literal
                    env = env.putTrue(var);                         //set the variable to TRUE
                } else {                                            //if the single literal is negative literal
                    env = env.putFalse(var);                        //set the variable to FALSE
                }
                return solve(substitute(clauses, l), env);          //update the clauses and recurse
            }

            else {                                                  //if the sc is not a unit clause
                if (l.equals(NegLiteral.make(l.getVariable()))) {   //if literal is a negative literal
                    l = l.getNegation();                            //reassign l to become a positive literal
                }

                //Try to set literal to TRUE, update the clauses, and recurse
                Environment posTrial = solve(substitute(clauses, l), env.put(var, Bool.TRUE));
                if (posTrial != null) {
                    return posTrial;
                }

                //Try to set literal to FALSE, update the clauses, and recurse
                else {
                    l = l.getNegation();
                    return solve(substitute(clauses, l), env.put(var, Bool.FALSE));
                }
            }
        }
    }

    //TO UPDATE THE CLAUSES EACH TIME WE TRY ASSIGNING A LITERAL TO TRUE/FALSE
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        ImList<Clause> reducedClauses = new EmptyImList<Clause>();
        for (Clause c : clauses) {
            Clause rc = c.reduce(l);
            if (rc != null) {
                reducedClauses = reducedClauses.add(rc);
            }
        }
        return reducedClauses;
    }

}
