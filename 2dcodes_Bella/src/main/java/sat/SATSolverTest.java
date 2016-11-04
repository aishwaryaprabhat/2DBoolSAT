package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import immutable.ImList;
import javafx.geometry.Pos;
import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public static void main (String[]args) throws IOException {
        File filePath = new File("C:\\Users\\Lenovo\\Documents\\Temporary\\2dcodes_Bella\\src\\main\\java\\sat\\largeSat.cnf");
        File resultPath = new File("C:\\Users\\Lenovo\\Documents\\Temporary\\2dcodes_Bella\\src\\main\\java\\sat\\BoolAssignment.text");

        Scanner input = null;
        if (filePath.exists() && filePath.isFile()) {
            input = new Scanner(filePath);
        }

        Formula SATProblem = parser(input);

        Literal a = PosLiteral.make("a");
        Literal b = PosLiteral.make("b");
        Literal na = a.getNegation();
        Literal nb = b.getNegation();

        System.out.println("SAT solver starts!!!");
        long startTime = System.nanoTime();
        Environment e = SATSolver.solve(SATProblem);
        long endTime = System.nanoTime();
        long timeTaken = endTime - startTime;
        System.out.println("Time: "+timeTaken/1000000.0+" ms");
        if(e!=null) {
            System.out.println("Problem is satisfiable");
            String rawResults = e.toString();

            Pattern envRegex = Pattern.compile("\\[(.*?)\\]");
            Matcher m = envRegex.matcher(rawResults);
            String results="";
            while (m.find()){
                results = m.group(1);
            }

            String [] resultArray = results.trim().split(",");
            String finalResults="";
            System.out.println(finalResults);

            for (int q=0; q<resultArray.length; q++){
                String[] entry = resultArray[q].trim().split("->");
                if(q!=resultArray.length-1) {
                    finalResults += (entry[0] + ":" + entry[1] + "\r\n");
                }
                else{
                    finalResults += (entry[0] + ":" + entry[1] + "\n");
                }
            }
            //System.out.println(e.toString());
            OutputStream outputStream = new FileOutputStream(resultPath);
            Writer outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(finalResults);

            outputStreamWriter.close();

            System.out.println("Done writing results to Bool.Assignment.txt");

        }
        else{
            System.out.println("Problem is unsatisfiable");
        }
    }

    public static Formula parser(Scanner input){
        Formula f = null;
        int noOfVar;
        int noOfClauses;
        ArrayList<String> makeToClause = new ArrayList<String>();
        ArrayList<Clause> makeToFormula = new ArrayList<Clause>();

        while (input.hasNextLine()) {
            String line = input.nextLine();

            if (line.startsWith("c")) {
                //System.out.println("Comment: " + line);

            } else if (line.startsWith("p")) {
                String[] problem = line.trim().split("\\s+");
                noOfVar = Integer.parseInt(problem[2]);
                noOfClauses = Integer.parseInt(problem[3]);
                //System.out.println("Problem: " + problem[0]);
                //System.out.println("Problem Type: " + problem[1]);
                //System.out.println("Problem Variables: " + problem[2]);
                //System.out.println("Problem Clauses: " + problem[3]);

            } else {
                String[] clause = line.trim().split(" ");
                for (String lit : clause) {
                    if (!lit.equals("0")) {
                        //System.out.println("Valid Literal");
                        makeToClause.add(lit);
                    }
                    else{
                        //System.out.println("Zero");
                        String[] a = makeToClause.toArray(new String[makeToClause.size()]);
                        Literal[] b = new Literal[a.length];
                        for (int i=0; i<a.length; i++){
                            if (a[i].startsWith("-")){
                                //System.out.println("Neg: " + a[i]);
                                Literal newNegLit = PosLiteral.make(a[i].substring(1)).getNegation();
                                b[i]=newNegLit;
                            }
                            else{
                                //System.out.println("Pos: " + a[i]);
                                Literal newPosLit = PosLiteral.make(a[i]);
                                b[i]=newPosLit;
                            }
                        }
                        Clause c = makeCl(b);
                        //System.out.println(c.toString());
                        makeToFormula.add(c);
                        makeToClause.clear();
                    }
                }
                //System.out.println(line);
            }
            f = makeFm(makeToFormula.toArray(new Clause[makeToFormula.size()]));
        }
        return f;
    }

    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
*/    	
    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}