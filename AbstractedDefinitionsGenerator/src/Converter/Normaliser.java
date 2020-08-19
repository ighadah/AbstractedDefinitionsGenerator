/**
Author: Ghadah Alghamdi
*/

package Converter;


import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class Normaliser {
	
	//turn equivlances to subofs
	//turn conjunctions to new subofs
	
	public OWLOntology normalise(OWLOntology O) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology O_normalised = manager.createOntology();
		manager.addAxioms(O_normalised, O.getLogicalAxioms());
		for(OWLLogicalAxiom axiom: O.getLogicalAxioms()) {
			if(axiom instanceof OWLSubClassOfAxiom) {
				Set<OWLSubClassOfAxiom> normalised_subofs = normalise_subofs((OWLSubClassOfAxiom) axiom);
				if(normalised_subofs.size() > 0) {
					manager.addAxioms(O_normalised, normalised_subofs);
					manager.removeAxiom(O_normalised, axiom);
					}
				}
			if(axiom instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom equiv_ax = (OWLEquivalentClassesAxiom) axiom;
				Set<OWLSubClassOfAxiom> subofs = equiv_ax.asOWLSubClassOfAxioms();
				for(OWLSubClassOfAxiom subof : subofs) {
					if(!(subof.isGCI())) {
					Set<OWLSubClassOfAxiom> normalised_subofs = normalise_subofs(subof);
					if(normalised_subofs.size() > 0) {
						manager.addAxioms(O_normalised, normalised_subofs);
						manager.removeAxiom(O_normalised, axiom);
					}
					}
					if(subof.isGCI()) {
						//add the GCI as it is
						manager.addAxiom(O_normalised, subof);
					}
				}
			}
		}
		
		return O_normalised;
	}

	
	//normalise subclassof
	public Set<OWLSubClassOfAxiom> normalise_subofs(OWLSubClassOfAxiom axiom){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		Set<OWLSubClassOfAxiom> normalised_subof = new HashSet<>();
		OWLClassExpression subof_rhs = axiom.getSuperClass();
		OWLClassExpression subof_lhs = axiom.getSubClass();
		if(subof_rhs instanceof OWLObjectIntersectionOf) {
			Set<OWLClassExpression> conjunct_rhs = subof_rhs.asConjunctSet();
			for(OWLClassExpression conjunct: conjunct_rhs) {
				//for each conjunct create its subclassof
				OWLSubClassOfAxiom subof_conjunct = df.getOWLSubClassOfAxiom(subof_lhs, conjunct);
				//manager.addAxiom(O_normalised, subof_conjunct);
				normalised_subof.add(subof_conjunct);
			}
		}else if(subof_rhs instanceof OWLClass) {
			normalised_subof.add(axiom);
		}
		return normalised_subof;
	}

}

