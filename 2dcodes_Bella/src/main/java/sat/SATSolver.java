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
        ImList<Clause> clauses = formula.getClauses();
        return solve (clauses, new Environment());
    }

    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses.isEmpty()) {
            return env;

        } else {
            Clause sc = null;
            for (Clause c : clauses) {
                if (c.isEmpty()) {                                  
                    return null;
                }

                if ((sc == null) || (c.size() < sc.size())) {
                    sc = c;
                }
            }

            Literal l = sc.chooseLiteral();
            Variable var = l.getVariable();

            if (sc.isUnit()) {


                if (l.equals(PosLiteral.make(l.getVariable()))) {
                    env = env.putTrue(var);
                } else {
                    env = env.putFalse(var);
                }
                return solve(substitute(clauses, l), env);
            }

            else {
                if (l.equals(NegLiteral.make(l.getVariable()))) {
                    l = l.getNegation();
                }


                Environment posTrial = solve(substitute(clauses, l), env.put(var, Bool.TRUE));
                if (posTrial != null) {
                    return posTrial;
                }


                else {
                    l = l.getNegation();
                    return solve(substitute(clauses, l), env.put(var, Bool.FALSE));
                }
            }
        }
    }


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
