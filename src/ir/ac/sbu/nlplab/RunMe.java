package ir.ac.sbu.nlplab;

import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import ir.ac.sbu.nlplab.fomalscience.mathematics.MathExpressionUtility;
import ir.ac.sbu.nlplab.metascience.control.AnalystOfPhysicalSystem;

public class RunMe {

	public static void main(String[] args) {

		// creates pattern layout
		PatternLayout layout = new PatternLayout();
		String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
		layout.setConversionPattern(conversionPattern);

		// creates console appender
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(layout);
		consoleAppender.activateOptions();

		// creates file appender
		FileAppender fileAppender = new FileAppender();
		fileAppender.setFile("applog3.txt");
		fileAppender.setLayout(layout);
		fileAppender.activateOptions();

		// configures the root logger
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.OFF);
		rootLogger.addAppender(consoleAppender);
		rootLogger.addAppender(fileAppender);

		// creates a custom logger and log messages
		Logger logger = Logger.getLogger(RunMe.class);

		logger.info("Hello World!");
		OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
//		ontologyModel.getDocumentManager().setProcessImports(true);
		try {
			InputStream inputStream = FileManager.get()
					.open("C:/Personal/Soroush/Workspace/MetaScience/Ontology/All.owl");
			// InputStream inputStream2 = FileManager.get()
			// .open("C:/Personal/Soroush/Workspace/MetaScience/Ontology/All.owl");
			try {
				ontologyModel.read(inputStream, null);
				// ontologyModel.read(inputStream2, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JenaException je) {
			System.err.println("ERROR" + je.getMessage());
			je.printStackTrace();
		}
		// ExtendedIterator classes = ontologyModel.listClasses();
		// while (classes.hasNext()) {
		// OntClass thisClass = (OntClass) classes.next();
		// // System.out.println(thisClass.getURI().toString());
		// logger.info("Found class: " + thisClass.toString());
		// ExtendedIterator instances = thisClass.listInstances();
		// while (instances.hasNext()) {
		// Individual thisInstance = (Individual) instances.next();
		// if (thisInstance.getLocalName().equals("c1")) {
		// logger.info(" Found instance: " + thisInstance.toString());
		// }
		// }
		// }

		Individual analysisTaskIndividual = ontologyModel
				.getIndividual("http://nlplab.sbu.ac.ir/All#analysis_task_cir1");
		try {
			AnalystOfPhysicalSystem.perform(analysisTaskIndividual);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			EvalUtilities util = new EvalUtilities(false, true);
			// Show an expression in the Java form:
			// String javaForm = util.toJavaForm("d(sin(x)*cos(x),x)");
			// // prints: D(Times(Sin(x),Cos(x)),x)
			// System.out.println(javaForm.toString());
			//
			// // Use the Java form to create an expression with F.* static
			// // methods:
			// //IAST function = D(Times(Sin(x), Cos(x)), x);
			// IExpr result = util.evaluate("d(sin(x)*cos(x),x)");//
			// util.evaluate(function);
			// // print: -Sin(x)^2+Cos(x)^2
			// System.out.println(result.toString());
			//
			// // evaluate the string directly
			// result = util.evaluate("d(sin(x)*cos(x),x)");
			// // print: -Sin(x)^2+Cos(x)^2
			// System.out.println(result.toString());
			//
			// // evaluate the last result ($ans contains "last answer")
			// result = util.evaluate("$ans+cos(x)^2");
			// // print: -Sin(x)^2+2*Cos(x)^2
			// System.out.println(result.toString());
			//
			// // evaluate an Integrate[] expression
			// result = util.evaluate("integrate(sin(x)^5,x)");
			// // print: -1/5*Cos(x)^5+2/3*Cos(x)^3-Cos(x)
			// System.out.println(result.toString());

			// Use [...] for function arguments instead of (...) and upper case
			// names for predefined functions (i.e. Sin[...]
			// instead of sin[...]).

//			String srTest = MathExpressionUtility.solveEquality("{va - vb == R * ia, ia == 4*x, R == 3, vb == 8*x}",
//					"{va, vb, ia, R}", "va");
			// logger.info(util2.evaluate("Solve({x^2==4,x+y^2==6}, {x,y})"));
			// if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			// // If true the parser doesn't distinguish between lower- or
			// // uppercase predefined symbols
			// result = util2.evaluate("integrate[sin[x]^5,x]");
			// } else {
			// result = util2.evaluate("Integrate[Sin[x]^5,x]");
			// }
			// // print: -1/5*Cos(x)^5+2/3*Cos(x)^3-Cos(x)
			// System.out.println(result.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// static {
	// try {
	// System.loadLibrary("sbmlj");
	// // For extra safety, check that the jar file is in the classpath.
	// Class.forName("org.sbml.libsbml.libsbml");
	// } catch (UnsatisfiedLinkError e) {
	// System.err.println("Error encountered while attempting to load
	// libSBML:");
	// System.err.println("Please check the value of your "
	// + (System.getProperty("os.name").startsWith("Mac OS")
	// ? "DYLD_LIBRARY_PATH" : "LD_LIBRARY_PATH")
	// +
	// " environment variable and/or your" +
	// " 'java.library.path' system property (depending on" +
	// " which one you are using) to make sure it list the" +
	// " directories needed to find the " +
	// System.mapLibraryName("sbmlj") + " library file and" +
	// " libraries it depends upon (e.g., the XML parser).");
	// System.exit(1);
	// } catch (ClassNotFoundException e) {
	// System.err.println("Error: unable to load the file 'libsbmlj.jar'." +
	// " It is likely that your -classpath command line " +
	// " setting or your CLASSPATH environment variable " +
	// " do not include the file 'libsbmlj.jar'.");
	// e.printStackTrace();
	//
	// System.exit(1);
	// } catch (SecurityException e) {
	// System.err.println("Error encountered while attempting to load
	// libSBML:");
	// e.printStackTrace();
	// System.err.println("Could not load the libSBML library files due to a" +
	// " security exception.\n");
	// System.exit(1);
	// }
	// }
}
