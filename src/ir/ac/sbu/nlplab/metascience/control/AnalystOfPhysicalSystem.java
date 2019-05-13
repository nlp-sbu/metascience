package ir.ac.sbu.nlplab.metascience.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.Logger;

import ir.ac.sbu.nlplab.RunMe;
import ir.ac.sbu.nlplab.metascience.Ont;
import ir.ac.sbu.nlplab.metascience.exception.AnalysisOfPhysicalSystemException;
import ir.ac.sbu.nlplab.metascience.model.Constraint;
import ir.ac.sbu.nlplab.metascience.model.ConstraintNetwork;
import ir.ac.sbu.nlplab.metascience.model.ConstraintState;
import ir.ac.sbu.nlplab.metascience.model.VarTypePoWClass;
import ir.ac.sbu.nlplab.metascience.model.Variable;
import ir.ac.sbu.nlplab.metascience.model.VariableType;
import ir.ac.sbu.nlplab.metascience.model.impl.ConstraintNetworkImpl;
import ir.ac.sbu.nlplab.metascience.model.impl.VariableImpl;
import ir.ac.sbu.nlplab.metascience.model.impl.VariableTypeImpl;
import ir.ac.sbu.nlplab.science.ScienceFacade;

public class AnalystOfPhysicalSystem {
	static protected Logger logger = Logger.getLogger(RunMe.class);

	public static void perform(Individual analysisTaskIndividual) throws AnalysisOfPhysicalSystemException {
		if (!analysisTaskIndividual.hasOntClass(Ont.METASCIENCE_NS + Ont.ANALYSIS_OF_PHYSICAL_SYSTEM))
			throw new AnalysisOfPhysicalSystemException(
					analysisTaskIndividual.getURI() + " does not seem to be an analysis task.");

		OntModel analysisTaskOntModel = analysisTaskIndividual.getOntModel();
		Property p1 = analysisTaskOntModel.getOntProperty(Ont.METASCIENCE_NS + Ont.ANALYSIS_TARGETS_SYSTEM);
		Resource systemResource = analysisTaskIndividual.getPropertyResourceValue(p1);
		if (systemResource == null)
			throw new AnalysisOfPhysicalSystemException(
					analysisTaskIndividual.getURI() + " does not target a system.");
		Individual system = analysisTaskOntModel.getIndividual(systemResource.getURI());
		if (system == null)
			throw new AnalysisOfPhysicalSystemException("System does not exist.");
		logger.info("System: " + system);

		Property p2 = analysisTaskOntModel.getOntProperty(Ont.METASCIENCE_NS + Ont.ANALYSIS_PROVIDES_THEORY);
		Resource theoryResource = analysisTaskIndividual.getPropertyResourceValue(p2);
		if (theoryResource == null)
			throw new AnalysisOfPhysicalSystemException(
					analysisTaskIndividual.getURI() + " does not provide a theory.");
		Individual theory = analysisTaskOntModel.getIndividual(theoryResource.getURI());
		if (theory == null)
			throw new AnalysisOfPhysicalSystemException("Theory does not exist.");
		logger.info("Theory: " + theory);
		Set<OntProperty> OntProperties = new HashSet<OntProperty>();
		collectStructuralPredicates(theory, analysisTaskOntModel, OntProperties);
		collectBehavioralPredicates(theory, analysisTaskOntModel, OntProperties);
		Individual abstractTheory = theory;
		Individual concreteTheoryIndividual;
		while (true) {
			concreteTheoryIndividual = abstractTheory;
			Property p3 = analysisTaskOntModel.getOntProperty(Ont.METASCIENCE_NS + Ont.MODEL_IMPLEMENTS_MODEL);
			Resource abstractTheoryResource = concreteTheoryIndividual.getPropertyResourceValue(p3);
			if (abstractTheoryResource == null)
				break;
			abstractTheory = analysisTaskOntModel.getIndividual(abstractTheoryResource.getURI());

			logger.info("Theory: " + abstractTheory);
			collectStructuralPredicates(abstractTheory, analysisTaskOntModel, OntProperties);
			collectBehavioralPredicates(abstractTheory, analysisTaskOntModel, OntProperties);
		}
		Model into = ModelFactory.createDefaultModel();

		extractIntoModelByIndividualAndProperties(into, analysisTaskOntModel, system, OntProperties);
		ConstraintNetwork constraintNetwork = new ConstraintNetworkImpl();
		buildConstraintNetwork(analysisTaskOntModel, into, theory, system, constraintNetwork);
		System.out.println(constraintNetwork);
		solveConstraintNetwork(constraintNetwork);
		// for(Constraint c: constraintNetwork.getConstraints()) {
		// if(c.getState()==ConstraintState.READY) {
		// Set<Variable> vs = c.apply();
		// System.err.println(vs);
		// }
		// }
	}

	protected static void solveConstraintNetwork(ConstraintNetwork cn) {
		boolean toBeContinued = true;
		int iteration = 0;
		while (toBeContinued) {
			iteration++;
			toBeContinued = false;
			for (Constraint c : cn.getConstraints()) {
				if (c.getState() == ConstraintState.READY) {
					System.out.print(iteration);
					System.out.print("  ");
					System.out.println(c);
					System.out.println(c.apply());
//					System.out.println(cn);
					toBeContinued = true;
					break;
				}
			}
		}
		for (Variable variable : cn.getVariables()) {
			if (variable.getNumValues() > 1) {
				System.out.println(variable.getValuesSetEqual());
			}
		}
	}

	protected static void collectStructuralPredicates(Individual theory, OntModel science,
			Set<OntProperty> predicates) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?structuralProperty \n" +
				"WHERE {\n" +
				"	?theoryView a <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY + ">.\n" +
				"	?theoryView <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY__THEORY + "> <"
				+ theory.getURI()
				+ ">.\n"
				+
				"	?theoryView <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY__VIEW + "> <" + Ont.SCIENCE_NS
				+ Ont.STRUCTURE + ">.\n" +
				"	?theoryView ?p ?structuralProperty. filter isLiteral(?structuralProperty)\n" +
				// " ?theoryView <" + OntologyURIs.METASCIENCE +
				// "theoryViewDeclaresPredicate> ?structurePredicate.\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery, science);
		ResultSet selectResults = selectQueryExecution.execSelect();
		Set<String> setOfStructuralProperty = new HashSet<String>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			String structuralPropertyString = solution.getLiteral("structuralProperty").toString();
			setOfStructuralProperty.add(structuralPropertyString);
		}
		selectQueryExecution.close();
		for (String propertyURI : setOfStructuralProperty) {
			Property p = science.getProperty(propertyURI);
			if (p.canAs(OntProperty.class)) {
				OntProperty op = p.as(OntProperty.class);
				predicates.add(op);
			}
		}
	}

	protected static void collectBehavioralPredicates(Individual theory, OntModel science,
			Set<OntProperty> predicates) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?behavioralProperty \n" +
				"WHERE {\n" +
				"	?theoryView a <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY + ">.\n" +
				"	?theoryView <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY__THEORY + "> <"
				+ theory.getURI()
				+ ">.\n"
				+
				"	?theoryView <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY__VIEW + "> <" + Ont.SCIENCE_NS
				+ Ont.BEHAVIOR + ">.\n" +
				"	?theoryView ?p ?behavioralProperty. filter isLiteral(?behavioralProperty)\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery, science);
		ResultSet selectResults = selectQueryExecution.execSelect();
		Set<String> setOfBehavioralProperty = new HashSet<String>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			String behavioralPropertyString = solution.getLiteral("behavioralProperty").toString();
			setOfBehavioralProperty.add(behavioralPropertyString);
		}
		selectQueryExecution.close();
		for (String propertyURI : setOfBehavioralProperty) {
			Property p = science.getProperty(propertyURI);
			if (p.canAs(OntProperty.class)) {
				OntProperty op = p.as(OntProperty.class);
				predicates.add(op);
			}
		}
	}

	static public void extractIntoModelByIndividualAndProperties(Model added, OntModel from, Individual seed,
			Set<OntProperty> setEdge) {
		Set<OntResource> setOfOntResource = new HashSet<OntResource>();
		setOfOntResource.add(seed);
		Set<Statement> setCandidateStatement = new HashSet<Statement>();
		while (true) {
			if (setOfOntResource.isEmpty())
				break;
			Iterator<OntResource> OntResourceIterator = setOfOntResource.iterator();
			OntResource ontResource = OntResourceIterator.next();
			if (ontResource.isIndividual()) {
				Individual individual = ontResource.asIndividual();
				for (StmtIterator si = individual.listProperties(); si.hasNext();) {
					Statement s = si.nextStatement();
					boolean statementAlreadyAdded = false;
					for (StmtIterator si2 = added.listStatements(); si2.hasNext();) {
						if (s.equals(si2.next())) {
							statementAlreadyAdded = true;
							break;
						}
					}
					if (!statementAlreadyAdded) {
						setCandidateStatement.add(s);
					}
				}
				for (StmtIterator si = from.listStatements(null, null, individual); si.hasNext();) {
					Statement s = si.nextStatement();
					boolean statementAlreadyAdded = false;
					for (StmtIterator si2 = added.listStatements(); si2.hasNext();) {
						if (s.equals(si2.next())) {
							statementAlreadyAdded = true;
							break;
						}
					}
					if (!statementAlreadyAdded) {
						setCandidateStatement.add(s);
					}
				}
				for (Iterator<Statement> si = setCandidateStatement.iterator(); si.hasNext();) {
					boolean toBeAdded = false;
					Statement statement = si.next();
					Property property = statement.getPredicate();
					if (property.canAs(OntResource.class)) {
						OntResource propertyOntResource = property.as(OntResource.class);
						if (propertyOntResource.canAs(OntProperty.class)) {
							OntProperty ontProperty = propertyOntResource.as(OntProperty.class);
							for (OntProperty edge : setEdge) {
								if (edge.equals(ontProperty)) {
									toBeAdded = true;
									break;
								} else if (edge.hasSubProperty(ontProperty, false)) {
									toBeAdded = true;
									break;
								}
							}
						}
					}
					if (toBeAdded) {
						added.add(statement);
						RDFNode node1 = statement.getSubject();
						RDFNode node2 = statement.getObject();
						if (node1.isResource()) {
							setOfOntResource.add(node1.as(OntResource.class));
						}
						if (node2.isResource()) {
							setOfOntResource.add(node2.as(OntResource.class));
						}
					}
				}
				setCandidateStatement.clear();
			}
			setOfOntResource.remove(ontResource);
		}
	}

	public static void buildConstraintNetwork(OntModel science, Model givenRDFModel,
			Individual theory, Individual system, ConstraintNetwork constraintNetwork) {
		givenRDFModel.write(System.out);
		addVariablesFromGivenSystem(science, givenRDFModel, theory, system, constraintNetwork);
		createVariablesOfConstraintNetwoek(science, theory, givenRDFModel, constraintNetwork);
		createConstraintsOfConstraintNetwork(science, theory,
				givenRDFModel, system, constraintNetwork);
	}

	private static void addVariablesFromGivenSystem(OntModel scientificOntology, Model givenModel,
			Individual theory, Individual system, ConstraintNetwork constraintNetwork) {
		Set<Resource> givenNodes = new HashSet<Resource>();
		for (Iterator<RDFNode> ri = givenModel.listObjects(); ri.hasNext();) {
			RDFNode node = ri.next();
			if (node.isResource()) {
				givenNodes.add(node.asResource());
			}
		}
		for (Iterator<Resource> ri = givenModel.listSubjects(); ri.hasNext();) {
			givenNodes.add(ri.next());
		}
		Set<Resource> givenVars = new HashSet<Resource>();
		for (Resource r : givenNodes) {
			if (scientificOntology.getIndividual(r.getURI())
					.hasOntClass(scientificOntology.getIndividual(Ont.METASCIENCE_NS + Ont.VARIABLE), false)) {
				givenVars.add(r);
			}
		}
		for (Resource givenVarResource : givenVars) {
			Individual givenVarIndividual = scientificOntology.getIndividual(givenVarResource.getURI());
			OntProperty targetProperty = scientificOntology
					.getOntProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_DESCRIBES_POW);
			OntProperty varTypeProperty = scientificOntology.getOntProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE);
			OntProperty valueProperty = scientificOntology
					.getOntProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_VALUE_STRING);
			Individual varTypeIndividual = scientificOntology.listObjectsOfProperty(givenVarIndividual, varTypeProperty)
					.next().as(Individual.class);
			Individual targetIndividual = scientificOntology.listObjectsOfProperty(givenVarIndividual, targetProperty)
					.next().as(Individual.class);
			Set<RDFNode> valueNodes = scientificOntology.listObjectsOfProperty(givenVarIndividual, valueProperty)
					.toSet();
			Set<String> valueStrings = new HashSet<String>();
			for (RDFNode valueNode : valueNodes)
				valueStrings.add(valueNode.asLiteral().toString());
			VariableType variableType = constraintNetwork.findVariableType(varTypeIndividual);
			if (variableType == null)
				variableType = new VariableTypeImpl(varTypeIndividual);
			Variable variable = constraintNetwork.findVariable(varTypeIndividual.getURI(), targetIndividual.getURI());
			if (variable == null) {
				variable = new VariableImpl(givenVarIndividual, variableType, targetIndividual);
				constraintNetwork.addVariable(variable);
			}
			for (String valueString : valueStrings) {
				variable.addValue(valueString);
			}
		}
	}

	private static void createVariablesOfConstraintNetwoek(
			OntModel scientificOntology,
			Individual theory,
			Model structure,
			ConstraintNetwork constraintNetwork) {
		/**
		 * Finds all relevant variableType-s together with corresponding parts
		 * of world, according to the BEHAVIOR view of the given theory.
		 */

		Set<VarTypePoWClass> varTypePoWClasses = ScienceFacade.listStateVariableTypes(scientificOntology, theory);
		/*
		 * For each variableType-pow pair, add a variable with the corresponding
		 * type, and have the variable describe the corresponding pow in the
		 * given structureModel
		 */
		for (VarTypePoWClass vtpwc : varTypePoWClasses) {
			Individual varTypeIndividual = vtpwc.getVarType();
			OntClass powOntClass = vtpwc.getPoWClass();
			VariableType variableType = constraintNetwork.findVariableType(varTypeIndividual);
			if (variableType == null) {
				variableType = new VariableTypeImpl(varTypeIndividual);
			}
			for (Iterator<Resource> ri = listResources(structure).iterator(); ri.hasNext();) {
				Resource structureNode = ri.next();
				Individual structureNodeInOntology = scientificOntology.getIndividual(structureNode.getURI());
				if (structureNodeInOntology == null)
					continue;
				if (structureNodeInOntology.hasOntClass(powOntClass)) {
					VariableImpl v = new VariableImpl(variableType.getBase(), variableType, structureNode);
					constraintNetwork.addVariable(v);
					logger.info(v);
					Set<Resource> potentialVariables = scientificOntology.listResourcesWithProperty(
							scientificOntology.getProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_DESCRIBES_POW),
							structureNodeInOntology).toSet();
					for (Resource r : potentialVariables) {
						if (r.as(Individual.class).hasOntClass(
								scientificOntology.getOntClass(Ont.METASCIENCE_NS + Ont.VARIABLE), false)) {
							if (r.getProperty(
									scientificOntology.getOntProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE))
									.getObject().equals(varTypeIndividual)) {

							}
						}
					}

				}
				if (scientificOntology.getOntResource(structureNode.getURI()).asIndividual().hasOntClass(powOntClass)) {
					VariableImpl v = new VariableImpl(variableType.getBase(), variableType, structureNode);
					constraintNetwork.addVariable(v);
					logger.info(v);
				}
			}
		}
		logger.info(constraintNetwork.getVariables().size());
	}

	private static void createConstraintsOfConstraintNetwork(OntModel science, Individual theory,
			Model structureModel, Individual systemIndividual, ConstraintNetwork constraintNetwork) {
		/*
		 * Build up the set of all resources in the structureModel: setResource
		 */
		Set<Resource> givenNodes = new HashSet<Resource>();
		for (Iterator<RDFNode> ri = structureModel.listObjects(); ri.hasNext();) {
			RDFNode node = ri.next();
			if (node.isResource()) {
				givenNodes.add(node.asResource());
			}
		}
		for (Iterator<Resource> ri = structureModel.listSubjects(); ri.hasNext();) {
			givenNodes.add(ri.next());
		}

		/* For each resource in the structureModel ... */
		for (Resource givenNode : givenNodes) {
			Individual givenNodeInOntology = science.getIndividual(givenNode.getURI());
			if (givenNodeInOntology == null)
				continue;
			Individual givenNodeIdea = ScienceFacade.getIdea(science, givenNodeInOntology);
			if (givenNodeIdea == null)
				continue;
			Set<Individual> laws = ScienceFacade.getLawsRepresentingPartOfWorld(science, theory,
					givenNodeInOntology);
			logger.info(givenNodeInOntology.getLocalName() + ", " + laws.toString());
			for (Individual law : laws) {
				try {
					ConstraintFactory.createConstraintOfNature(science, law, givenNodeIdea,
							structureModel,
							givenNode, constraintNetwork);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	private static Set<Resource> listResources(Model model) {
		Set<Resource> setResource = new HashSet<Resource>();
		for (Iterator<RDFNode> ri = model.listObjects(); ri.hasNext();) {
			RDFNode node = ri.next();
			if (node.isResource()) {
				setResource.add(node.asResource());
			}
		}
		for (Iterator<Resource> ri = model.listSubjects(); ri.hasNext();) {
			setResource.add(ri.next());
		}
		return setResource;
	}

	private static Set<Individual> findLaws(Individual idea, Individual theory, OntModel science) {
		Set<Individual> setLaw = new HashSet<Individual>();
		String theoryContainsLawURI = Ont.METASCIENCE_NS + Ont.THEORY_CONTAINS_LAW;
		for (StmtIterator lawsOfTheory = theory
				.listProperties(science.getProperty(theoryContainsLawURI)); lawsOfTheory
						.hasNext();) {
			Resource lawResource = lawsOfTheory.next().getObject().asResource();
			if (lawResource.canAs(Individual.class)) {
				Individual lawInd = lawResource.as(Individual.class);
				String lawRepsPoW = Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE_REPRESENTS_PART_OF_WORLD;
				for (StmtIterator partsOfWorld = lawInd
						.listProperties(science.getProperty(lawRepsPoW)); partsOfWorld
								.hasNext();) {
					if (partsOfWorld.next().getObject().equals(idea)) {
						setLaw.add(lawInd);
						break;
					}
				}
			}
		}
		return setLaw;
	}
}
