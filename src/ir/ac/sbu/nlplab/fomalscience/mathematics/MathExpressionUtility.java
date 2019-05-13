package ir.ac.sbu.nlplab.fomalscience.mathematics;

import java.util.Set;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

import ir.ac.sbu.nlplab.metascience.model.QuantitativeVariable;

public class MathExpressionUtility {
	public static String evaluateString(String inString) {
		EvalUtilities evalUtility = new EvalUtilities();
		StringBuffer buf = new StringBuffer();
		IExpr result = evalUtility.evaluate(inString);
		try {
			OutputFormFactory.get().convert(buf, result);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static String solveEquality_TEMP(String equality, Set<QuantitativeVariable> variables, QuantitativeVariable unknown) {
		String equalities = "{" + equality;
		String variableNames = "{";
		for(QuantitativeVariable v:variables) {
			variableNames = variableNames + v.getName() + ", ";
		}
		variableNames = variableNames.substring(0, variableNames.length() - 2);
		variableNames = variableNames + "}";
		return solveEquality(equalities, variableNames, unknown.getName());
	}
	
	public static String solveEquality(String equalities, String variableNames, String unknown) {
		EvalUtilities evalUtility = new EvalUtilities(false, true);
		StringBuffer buf = new StringBuffer();
		String s = "Solve(" + equalities + ", " + variableNames + ")";
		IExpr result = evalUtility.evaluate(s);
		try {
			OutputFormFactory.get().convert(buf, result);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		String sr = buf.toString();
		// System.err.println(sr);
		String sVar = unknown + "->";
		int n1 = sr.indexOf(sVar) + sVar.length();
		int nComma = sr.indexOf(",", n1);
		int nCurly = sr.indexOf("}", n1);
		int nBisZum = Integer.MAX_VALUE;
		if (nComma > 0 && nComma < nBisZum)
			nBisZum = nComma;
		if (nCurly > 0 && nCurly < nBisZum)
			nBisZum = nCurly;
		String value = sr.substring(n1, nBisZum);
		return value;
	}

}
