/**
Author: Ghadah Alghamdi
*/

package PostProcessor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class PostProcessor {
	
	public static boolean isTautology(OWLAxiom ax) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology empty_ontology;
		try {
			empty_ontology = manager.createOntology();
			//OWLReasoner reasoner =new Reasoner.ReasonerFactory().createReasoner(empty_ontology);
			Configuration c = new Configuration();
			OWLReasoner reasoner = new Reasoner(c,empty_ontology);
			if (reasoner.isEntailed(ax)) {
				return true;
			}
			else {
				return false;
			}
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isRedundant(OWLLogicalAxiom axiom, OWLOntology ontology, OWLReasoner reasoner) {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		manager.removeAxiom(ontology, axiom);
		if(reasoner.isEntailed(axiom)) {
			System.out.println("The current axiom is redundant");
			return true;
		}
		System.out.println("The current axiom is NOT redundant");
		return false;
		
	}
	
	
public static boolean isRedundant(OWLLogicalAxiom axiom, OWLOntology ontology) {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		manager.removeAxiom(ontology, axiom);
		//OWLReasonerFactory fac = new Reasoner.ReasonerFactory();
		//OWLReasoner reasoner = fac.createReasoner(ontology);
		Configuration c = new Configuration();
		OWLReasoner reasoner = new Reasoner(c,ontology);
		if(reasoner.isEntailed(axiom)) {
			System.out.println("The current axiom is redundant");
			return true;
		}
		reasoner.dispose();
		System.out.println("The current axiom is NOT redundant");
		return false;
	}

	public void removeTautology(String onto_file) throws 
	OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
		File file1 = new File(onto_file);
		IRI iri1 = IRI.create(file1);
		OWLOntology input_onto = manager1.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri1),
				new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
		System.out.println("ontology axioms size = " + input_onto.getLogicalAxiomCount());
		System.out.println("ontology classes size = " + input_onto.getClassesInSignature().size());
		System.out.println("ontology properties size = " + input_onto.getObjectPropertiesInSignature().size());
	
		Set<OWLAxiom> tautologies = new HashSet<>();
		for(OWLAxiom ax: input_onto.getLogicalAxioms()) {
			if(isTautology(ax)) {
				tautologies.add(ax);
			}
		}
		
		manager1.removeAxioms(input_onto, tautologies);
		System.out.println("The tautologies size: " + tautologies.size());
		System.out.println("The tautologies are: " + tautologies);
		OutputStream os_onto = new FileOutputStream(onto_file + "-no-tautology.owl");
		manager1.saveOntology(input_onto, new FunctionalSyntaxDocumentFormat(), os_onto);
		
		System.out.println("New ontology axioms size = " + input_onto.getLogicalAxiomCount());
		System.out.println("New ontology classes size = " + input_onto.getClassesInSignature().size());
		System.out.println("New ontology properties size = " + input_onto.getObjectPropertiesInSignature().size());

	}
	
	
	
public void removeRedundancy(String onto_file) throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		File file1 = new File(onto_file);
		IRI iri1 = IRI.create(file1);
		OWLOntology input_onto = manager1.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri1),
				new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
		System.out.println("ontology axioms size = " + input_onto.getLogicalAxiomCount());
		System.out.println("ontology classes size = " + input_onto.getClassesInSignature().size());
		System.out.println("ontology properties size = " + input_onto.getObjectPropertiesInSignature().size());
		OWLOntology ontology_copy = manager2.createOntology();
		manager2.addAxioms(ontology_copy, input_onto.getAxioms());
		Set<OWLAxiom> redundancies = new HashSet<>();
		//OWLReasonerFactory fac = new Reasoner.ReasonerFactory();
		//OWLReasoner reasoner = fac.createReasoner(input_onto);
		Configuration c = new Configuration();
		OWLReasoner reasoner = new Reasoner(c,input_onto);
		for(OWLLogicalAxiom ax: input_onto.getLogicalAxioms()) {
			if(isRedundant(ax, ontology_copy, reasoner)) {
				redundancies.add(ax);
			}
		}
	
		
		manager1.removeAxioms(input_onto, redundancies);
		System.out.println("The redundancies size: " + redundancies.size());
		System.out.println("The redundancies are: " + redundancies);
		OutputStream os_onto = new FileOutputStream(onto_file + "-no-redundancy.owl");
		manager1.saveOntology(input_onto, new FunctionalSyntaxDocumentFormat(), os_onto);
		
		System.out.println("New ontology axioms size = " + input_onto.getLogicalAxiomCount());
		System.out.println("New ontology classes size = " + input_onto.getClassesInSignature().size());
		System.out.println("New ontology properties size = " + input_onto.getObjectPropertiesInSignature().size());

	}






public OWLOntology removeSubofInEqui(OWLOntology view) {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	for(OWLLogicalAxiom l_ax: view.getLogicalAxioms()) {
		//System.out.println("current logcial axiom in the view: " + l_ax);
		for(OWLLogicalAxiom l_ax_2: view.getLogicalAxioms()) {
			if(l_ax.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom l_ax_subof = (OWLSubClassOfAxiom) l_ax;
				OWLClassExpression lhs = l_ax_subof.getSubClass();
				OWLClassExpression rhs = l_ax_subof.getSuperClass();
				if(l_ax_2.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					if(l_ax_2.getClassesInSignature().contains(lhs.asOWLClass()) && l_ax_2.getClassesInSignature().contains(rhs.asOWLClass())) {
						manager.removeAxiom(view, l_ax_subof);
					}
				}
			}
		}
	}
	
	return view;
}

public OWLOntology removeSubofInEqui_2(OWLOntology view) {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	for(OWLLogicalAxiom l_ax: view.getLogicalAxioms()) {
		for(OWLLogicalAxiom l_ax_2: view.getLogicalAxioms()) {
			if(l_ax.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom l_ax_subof = (OWLSubClassOfAxiom) l_ax;
				OWLClassExpression lhs = l_ax_subof.getSubClass();
				OWLClassExpression rhs = l_ax_subof.getSuperClass();
				if(l_ax_2.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					OWLEquivalentClassesAxiom equi_l_ax_2 = (OWLEquivalentClassesAxiom) l_ax_2;
					Set<OWLSubClassOfAxiom> subofs_equi = equi_l_ax_2.asOWLSubClassOfAxioms();
					for(OWLSubClassOfAxiom subof_equi: subofs_equi) {
						if(!(subof_equi.isGCI())) {
							OWLClassExpression lhs_equi = subof_equi.getSubClass();
							OWLClassExpression rhs_equi = subof_equi.getSuperClass();
							if(lhs_equi.equals(lhs) && rhs_equi.getClassesInSignature().contains(rhs.asOWLClass())) {
								manager.removeAxiom(view, l_ax_subof);
							}
						}
					}
				}
			}
		}
	}
	
	return view;
}
	public static void main (String [] args) throws OWLOntologyCreationException, OWLOntologyStorageException, CloneNotSupportedException, IOException {
		
		System.out.println("- Remove Redundancies -");
		String filePath1 = args[0];

		System.out.println("--------------Ontology file name: " + filePath1 + "--------------");
	

		if(filePath1 != null) {
		PostProcessor pp = new PostProcessor();
		//pp.removeTautology(filePath1);
		pp.removeRedundancy(filePath1);
		//pp.use_classifyOntology(filePath1);
		}
	}
}



