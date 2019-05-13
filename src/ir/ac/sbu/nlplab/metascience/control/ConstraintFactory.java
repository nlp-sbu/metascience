package ir.ac.sbu.nlplab.metascience.control;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;

import ir.ac.sbu.nlplab.metascience.Ont;
import ir.ac.sbu.nlplab.metascience.exception.NotLawOfNatureException;
import ir.ac.sbu.nlplab.metascience.model.AllEqualConstraint;
import ir.ac.sbu.nlplab.metascience.model.ConstraintNetwork;
import ir.ac.sbu.nlplab.metascience.model.EqualityConstraint;
import ir.ac.sbu.nlplab.metascience.model.SumZeroConstraint;
import ir.ac.sbu.nlplab.metascience.model.Variable;
import ir.ac.sbu.nlplab.metascience.model.impl.AllEqualConstraintOfNatureImpl;
import ir.ac.sbu.nlplab.metascience.model.impl.EqualityConstraintOfNatureImpl;
import ir.ac.sbu.nlplab.metascience.model.impl.SumZeroConstraintOfNatureImpl;

public class ConstraintFactory {

	public static void createConstraintOfNature(
			OntModel scientificOntology,
			Individual lawOfNature,
			Individual partOfWorld,
			Model structure,
			Resource structureNode,
			ConstraintNetwork constraintNetwork)
			throws NotLawOfNatureException {
		OntClass lawOfNatureOntClass = scientificOntology.getOntClass(Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE);
		if (!lawOfNature.hasOntClass(lawOfNatureOntClass, false)) {
			throw new NotLawOfNatureException();
		}
		OntClass sumZeroOntClass = scientificOntology.getResource(Ont.METASCIENCE_NS + Ont.SUM_ZERO_LAW)
				.as(OntClass.class);
		OntClass equalityOntClass = scientificOntology.getOntClass(Ont.METASCIENCE_NS + Ont.EQUALITY_LAW)
				.as(OntClass.class);
		OntClass inequalityOntClass = scientificOntology.getOntClass(Ont.METASCIENCE_NS + Ont.INEQUALITY_LAW)
				.as(OntClass.class);
		OntClass allEqualOntClass = scientificOntology.getOntClass(Ont.METASCIENCE_NS + Ont.ALL_EQUAL_LAW)
				.as(OntClass.class);
		if (lawOfNature.hasOntClass(sumZeroOntClass, false)) {
			createSumZeroConstraint(scientificOntology, lawOfNature, partOfWorld, structure, structureNode,
					constraintNetwork);
		} else if (lawOfNature.hasOntClass(equalityOntClass, false)) {
			createEqualityConstraint(scientificOntology, lawOfNature, partOfWorld, structure, structureNode,
					constraintNetwork);
		} else if (lawOfNature.hasOntClass(inequalityOntClass, false)) {
			createInequalityConstraint(scientificOntology, lawOfNature, partOfWorld, structure, structureNode,
					constraintNetwork);

		} else if (lawOfNature.hasOntClass(allEqualOntClass, false)) {
			createAllEqualConstraint(scientificOntology, lawOfNature, partOfWorld, structure, structureNode,
					constraintNetwork);
		}
	}

	protected static void createAllEqualConstraint(
			OntModel science,
			Individual lawOfNature,
			Individual targetOfLaw,
			Model givenRDFModel,
			Resource givenNode,
			ConstraintNetwork constraintNetwork) {
		AllEqualConstraint aec = new AllEqualConstraintOfNatureImpl(lawOfNature, givenNode);
		constraintNetwork.addConstraint(aec);
		ObjectProperty aecUsesVariableType = science.getObjectProperty(Ont.METASCIENCE_NS + Ont.ALL_EQUAL_LAW_USES_VARIABLE);
		DatatypeProperty aecUsesPredicate = science.getDatatypeProperty(Ont.METASCIENCE_NS+Ont.ALL_EQUAL_LAW_USES_PREDICATE);
		String variableTypeURI = lawOfNature.getProperty(aecUsesVariableType).getObject().asResource().getURI();
		String predicateURI = lawOfNature.getPropertyValue(aecUsesPredicate).asLiteral().toString();
		Property p = givenRDFModel.getProperty(predicateURI);
		Set<String> pows = new HashSet<String>();
		StmtIterator si1 = givenRDFModel.listStatements(givenNode, p, (Resource)null);
		while (si1.hasNext()) {
			pows.add(si1.next().getObject().asResource().getURI());
		}
		StmtIterator si2 = givenRDFModel.listStatements(null, p, givenNode);
		while (si2.hasNext()) {
			pows.add(si2.next().getSubject().getURI());
		}
		for(String powURI:pows) {
			Variable v = constraintNetwork.findVariable(variableTypeURI, powURI);
			if(v==null) continue;
			aec.addVariable(v);
		}
	}
	// OntClass oc = getBase().getOntModel().getOntClass(Ont.METASCIENCE_NS +
	// Ont.QUANTITATIVE_LAW);
	// if (!base.hasOntClass(oc, false)) {
	// throw new OntologicalTypeMismatchException(base, oc);
	// }

	protected static void createSumZeroConstraint(
			OntModel science,
			Individual lawOfNature,
			Individual targetOfLaw,
			Model givenRDFModel,
			Resource givenNode,
			ConstraintNetwork constraintNetwork) {
		SumZeroConstraint szc = new SumZeroConstraintOfNatureImpl(lawOfNature, givenNode);
		constraintNetwork.addConstraint(szc);
		ObjectProperty szcUsesVariableType = science.getObjectProperty(Ont.METASCIENCE_NS + Ont.SUM_ZERO_LAW_USES_QUANTITATIVE_VARIABLE);
		DatatypeProperty szcUsesPredicate = science.getDatatypeProperty(Ont.METASCIENCE_NS+Ont.SUM_ZERO_LAW_USES_PREDICATE);
		String variableTypeURI = lawOfNature.getProperty(szcUsesVariableType).getObject().asResource().getURI();
		String predicateURI = lawOfNature.getPropertyValue(szcUsesPredicate).asLiteral().toString();
		Property p = givenRDFModel.getProperty(predicateURI);
		Set<String> pows = new HashSet<String>();
		StmtIterator si1 = givenRDFModel.listStatements(givenNode, p, (Resource)null);
		while (si1.hasNext()) {
			pows.add(si1.next().getObject().asResource().getURI());
		}
		StmtIterator si2 = givenRDFModel.listStatements(null, p, givenNode);
		while (si2.hasNext()) {
			pows.add(si2.next().getSubject().getURI());
		}
		for(String powURI:pows) {
			Variable v = constraintNetwork.findVariable(variableTypeURI, powURI);
			if(v==null) continue;
			szc.addVariable(v);
		}
	}

	protected static void createEqualityConstraint(
			OntModel science,
			Individual lawOfNature,
			Individual targetOfLaw,
			Model givenRDFModel,
			Resource givenNode,
			ConstraintNetwork constraintNetwork) {
		/**
		 * Given a scientific ontology O, a law of nature L in O, a part POW of
		 * world, plus, the structure S of a system, one node N in this
		 * structural model and a constraint network CN, this method creates and
		 * adds to CN, a constraint C based on the law L which targets N
		 */
		String equality = lawOfNature.getProperty(science.getProperty(Ont.METASCIENCE_NS + Ont.LAW_HAS_CONTENT))
				.getObject().asLiteral().toString();
		EqualityConstraint ec = new EqualityConstraintOfNatureImpl(lawOfNature, givenNode, equality);
		constraintNetwork.addConstraint(ec);
		addVars2Constraint1(science, lawOfNature, targetOfLaw, givenRDFModel, givenNode, constraintNetwork,
				ec);
		addVars2Constraint2(science, lawOfNature, targetOfLaw, givenRDFModel, givenNode, constraintNetwork,
				ec);
		addVars2Constraint3(science, lawOfNature, targetOfLaw, givenRDFModel, givenNode, constraintNetwork,
				ec);
	}

	protected static void addVars2Constraint1(OntModel scientificOntology, Individual lawOfNature,
			Individual targetOfLaw, Model structure, Resource structureNode, ConstraintNetwork constraintNetwork,
			EqualityConstraint ec) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?variable ?variableType ?pow ?p1 \n" +
				"WHERE {\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.MODEL_USES_VARIABLE + "> ?variable.\n"
				+
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE + "> ?variableType.\n" +
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_DESCRIBES_POW + "> ?pow.\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE_REPRESENTS_PART_OF_WORLD
				+ "> <" + targetOfLaw.getURI() + ">.\n" +
				"	<" + targetOfLaw.getURI() + "> ?p1 ?pow.\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery,
				scientificOntology.getRawModel());
		ResultSet selectResults = selectQueryExecution.execSelect();
		List<Individual> variables = new LinkedList<Individual>();
		List<Individual> variableTypes = new LinkedList<Individual>();
		List<Individual> pows = new LinkedList<Individual>();
		List<OntProperty> p1s = new LinkedList<OntProperty>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			Individual variable = scientificOntology.getIndividual(solution.getResource("variable").getURI());
			Individual variableType = scientificOntology.getIndividual(solution.getResource("variableType").getURI());
			Individual pow = scientificOntology.getIndividual(solution.getResource("pow").getURI());
			OntProperty p1 = scientificOntology.getOntProperty(solution.getResource("p1").getURI());
			variables.add(variable);
			variableTypes.add(variableType);
			pows.add(pow);
			p1s.add(p1);
		}
		selectQueryExecution.close();
		java.util.Iterator<Individual> vi = variables.iterator();
		java.util.Iterator<Individual> vti = variableTypes.iterator();
		java.util.Iterator<Individual> powi = pows.iterator();
		java.util.Iterator<OntProperty> p1i = p1s.iterator();
		while (vi.hasNext()) {
			Individual variableIndividual = vi.next();
			Individual variableTypeIndividual = vti.next();
			Individual powIndividual = powi.next();
			OntProperty p1OntProperty = p1i.next().as(OntProperty.class);
			Resource adjacentStructureNode = structureNode.getPropertyResourceValue(p1OntProperty);
			if (adjacentStructureNode == null)
				continue;
			Variable variableInCN = constraintNetwork.findVariable(variableTypeIndividual.getURI(),
					adjacentStructureNode.getURI());
			if (variableInCN == null)
				continue;
			String roleString = variableIndividual.getURI();
			ec.addVariableAs(variableInCN, roleString);
		}
	}

	protected static void addVars2Constraint2(OntModel scientificOntology, Individual lawOfNature,
			Individual targetOfLaw, Model structure, Resource structureNode, ConstraintNetwork constraintNetwork,
			EqualityConstraint ec) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?variable ?variableType ?pow ?p2 \n" +
				"WHERE {\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.MODEL_USES_VARIABLE + "> ?variable.\n"
				+
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE + "> ?variableType.\n" +
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_DESCRIBES_POW + "> ?pow.\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE_REPRESENTS_PART_OF_WORLD
				+ "> <" + targetOfLaw.getURI() + ">.\n" +
				"	?pow ?p2 <" + targetOfLaw.getURI() + ">.\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery,
				scientificOntology.getRawModel());
		ResultSet selectResults = selectQueryExecution.execSelect();
		List<Individual> variables = new LinkedList<Individual>();
		List<Individual> variableTypes = new LinkedList<Individual>();
		List<Individual> pows = new LinkedList<Individual>();
		List<OntProperty> p2s = new LinkedList<OntProperty>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			Individual variable = scientificOntology.getIndividual(solution.getResource("variable").getURI());
			Individual variableType = scientificOntology.getIndividual(solution.getResource("variableType").getURI());
			Individual pow = scientificOntology.getIndividual(solution.getResource("pow").getURI());
			OntProperty p2 = scientificOntology.getOntProperty(solution.getResource("p2").getURI());
			variables.add(variable);
			variableTypes.add(variableType);
			pows.add(pow);
			p2s.add(p2);
		}
		selectQueryExecution.close();
		java.util.Iterator<Individual> vi = variables.iterator();
		java.util.Iterator<Individual> vti = variableTypes.iterator();
		java.util.Iterator<Individual> powi = pows.iterator();
		java.util.Iterator<OntProperty> p2i = p2s.iterator();
		while (vi.hasNext()) {
			Individual variableIndividual = vi.next();
			Individual variableTypeIndividual = vti.next();
			Individual powIndividual = powi.next();
			OntProperty p2OntProperty = p2i.next().as(OntProperty.class);
			Resource adjacentStructureNode = null;
			for (Resource r : structure.listResourcesWithProperty(p2OntProperty).toSet()) {
				if (r.getProperty(p2OntProperty).getObject().equals(structureNode))
					adjacentStructureNode = r;
			}
			if (adjacentStructureNode == null)
				continue;
			Variable variableInCN = constraintNetwork.findVariable(variableTypeIndividual.getURI(),
					adjacentStructureNode.getURI());
			if (variableInCN == null)
				continue;
			ec.addVariableAs(variableInCN, variableIndividual.getURI());
		}
	}

	protected static void addVars2Constraint3(OntModel scientificOntology, Individual lawOfNature,
			Individual targetOfLaw, Model structure, Resource structureNode, ConstraintNetwork constraintNetwork,
			EqualityConstraint ec) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?variable ?variableType \n" +
				"WHERE {\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.MODEL_USES_VARIABLE + "> ?variable.\n"
				+
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE + "> ?variableType.\n" +
				"	?variable <" + Ont.METASCIENCE_NS + Ont.VARIABLE_DESCRIBES_POW + "> <" + targetOfLaw.getURI()
				+ ">.\n" +
				"	<" + lawOfNature.getURI() + "> <" + Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE_REPRESENTS_PART_OF_WORLD
				+ "> <" + targetOfLaw.getURI() + ">.\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery,
				scientificOntology.getRawModel());
		ResultSet selectResults = selectQueryExecution.execSelect();
		List<Individual> variables = new LinkedList<Individual>();
		List<Individual> variableTypes = new LinkedList<Individual>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			Individual variable = scientificOntology.getIndividual(solution.getResource("variable").getURI());
			Individual variableType = scientificOntology.getIndividual(solution.getResource("variableType").getURI());
			variables.add(variable);
			variableTypes.add(variableType);
		}
		selectQueryExecution.close();
		java.util.Iterator<Individual> vi = variables.iterator();
		java.util.Iterator<Individual> vti = variableTypes.iterator();
		while (vi.hasNext()) {
			Individual variableIndividual = vi.next();
			Individual variableTypeIndividual = vti.next();
			Variable variableInCN = constraintNetwork.findVariable(variableTypeIndividual.getURI(),
					structureNode.getURI());
			if (variableInCN == null)
				continue;
			ec.addVariableAs(variableInCN, variableIndividual.getURI());
		}
	}

	protected static void createInequalityConstraint(OntModel scientificOntology, Individual lawOfNature,
			Individual partOfWorld, Model structure, Resource structureNode, ConstraintNetwork constraintNetwork) {
		System.err.println(lawOfNature.getLocalName() + " is an inequality law");
	}
}
