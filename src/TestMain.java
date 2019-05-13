import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.ReasonerVocabulary;

public class TestMain {

	public static void main(String[] args) {
		OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
//		ontologyModel.getReasoner().setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
		// OntModel ontologyModel = ModelFactory.createOntologyModel();
		try {
			InputStream inputStream = FileManager.get()
					.open("C:/Personal/Soroush/Workspace/MetaScience/Ontology/All.owl");
			// InputStream inputStream2 = FileManager.get()
			// .open("C:/Personal/Soroush/Workspace/MetaScience/Ontology/All.owl");
			try {
				String sReplace = "0123456723";
				String s2 = sReplace.replaceAll("23", ",,,");
				ontologyModel.read(inputStream, null);
				// String indURI = "http://nlplab.sbu.ac.ir/Physics#ohms_law";
				// String classURI =
				// "http://nlplab.sbu.ac.ir/MetaScience#Quantitative_Law";
				// Individual ind = ontologyModel.getIndividual(indURI);
				// OntClass oc = ontologyModel.getOntClass(classURI);
				// boolean b = ind.hasOntClass(oc, false);
				// System.err.println(b);
				// Individual punned =
				// ontologyModel.getIndividual("http://nlplab.sbu.ac.ir/All#Test2");
				// System.err.println(punned.getURI());
				String sURI = "http://nlplab.sbu.ac.ir/Example#cir1";
				String oURI = "http://nlplab.sbu.ac.ir/Example#cir1r1";
				String pdURI = "http://nlplab.sbu.ac.ir/Science#system_contains_element";
				String puURI = "http://nlplab.sbu.ac.ir/Science#contains";
				Individual s = ontologyModel.getIndividual(sURI);
				Individual o = ontologyModel.getIndividual(oURI);
				Property pd = ontologyModel.getProperty(pdURI);
				Property pu = ontologyModel.getProperty(puURI);
				boolean bd = s.hasProperty(pd);
				boolean bu = s.hasProperty(pu);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JenaException je) {
			System.err.println("ERROR" + je.getMessage());
			je.printStackTrace();
		}
	}
}
