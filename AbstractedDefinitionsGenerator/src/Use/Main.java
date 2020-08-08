/**
Author: Ghadah Alghamdi
*/

package Use;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import Converter.ToGraph;
import Graph.Def_Vertex;
import Graph.Edge;
import Graph.Vertex;

import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class Main {
	//Graph graph = new Graph();
	Graph graph = new Graph();
	public void useBFS_get_defined_sig(String sig_file, String module_file) throws OWLOntologyCreationException, IOException, ClassNotFoundException, OWLOntologyStorageException {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(sig_file + "-useBFS-output-stream-whole-module-2017-newclass-3.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
		
		File file1 = new File(sig_file);
		IRI iri1 = IRI.create(file1);
		OWLOntology sig_O = manager1.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri1),
				new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
		
		
		System.out.println("the sig_O axioms size: " + sig_O.getLogicalAxiomCount());
		System.out.println("the sig_O classes size: " + sig_O.getClassesInSignature().size());
		System.out.println("the sig_O properties size: " + sig_O.getObjectPropertiesInSignature().size());
		
		
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		File file2 = new File(module_file);
		IRI iri2 = IRI.create(file2);
		OWLOntology O = manager2.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri2),
				new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
		
		System.out.println("the O axioms size: " + O.getLogicalAxiomCount());
		System.out.println("the O classes size: " + O.getClassesInSignature().size());
		System.out.println("the O properties size: " + O.getObjectPropertiesInSignature().size());
		
		
		//OWLOntology module = extract_module(sig_file, o_file);
		//Extract the star module for the defined sig concepts
		Set<OWLEntity> defined_cls = new HashSet<>();
		for(OWLClass cl: sig_O.getClassesInSignature()) {
			if(isDefined(cl, O)) {
				defined_cls.add(cl);					
			}
		}
		System.out.println("the size of defined_cls is: " + defined_cls.size());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		SyntacticLocalityModuleExtractor sme_original = new SyntacticLocalityModuleExtractor(manager,
				O, uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR);
		OWLOntology module_1 = sme_original.extractAsOntology(defined_cls, IRI.generateDocumentIRI());
		
		//save the module
		System.out.println("the module_1 axioms size: " + module_1.getLogicalAxiomCount());
		System.out.println("the module_1 classes size: " + module_1.getClassesInSignature().size());
		System.out.println("the module_1 properties size: " + module_1.getObjectPropertiesInSignature().size());
		
		OutputStream os_onto = new FileOutputStream(sig_file + "-module.owl");
		manager.saveOntology(module_1, new FunctionalSyntaxDocumentFormat(), os_onto);
		OWLOntology O_classified = classifyOntology(module_1);
		
		//OWLOntology O_classified = classifyOntology(O);
		
		ToGraph toGraph = new ToGraph();		
		
		OWLOntologyManager manager4 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology_all = manager4.createOntology();
		manager4.addAxioms(ontology_all, module_1.getLogicalAxioms());
		manager4.addAxioms(ontology_all, O_classified.getLogicalAxioms());
		
		OWLOntologyManager manager5 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology_abstract_def = manager5.createOntology();
		
		
		long startTime1 = System.currentTimeMillis();
		Map<Vertex, List<Vertex>> adjacency_list_map = toGraph.ontologyConverter(ontology_all);
		//System.out.println("adjacency_list_map: " + adjacency_list_map);
		long endTime1 = System.currentTimeMillis();
		System.out.println("Total OWL-To-Graph Conversion Duration = " + (endTime1 - startTime1) + " millis");
		
		
		graph.setAdjListMap(adjacency_list_map);
		Map<Vertex, List<Vertex>> gotten_map = graph.getAdjListMap();
		System.out.println("gotten_map: " + gotten_map);
        List<Edge> list_role_edges = new ArrayList<>();
        
        
        Set<OWLEquivalentClassesAxiom> abstracted_definitions = new HashSet<>();
      
		List<OWLObjectPropertyExpression> transitive_roles_exps = new ArrayList<>();
		 for(OWLLogicalAxiom axiom: module_1.getLogicalAxioms()) {
			 if(axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
					OWLTransitiveObjectPropertyAxiom otobp = (OWLTransitiveObjectPropertyAxiom) axiom;
					OWLObjectPropertyExpression pr_exp = otobp.getProperty();
					transitive_roles_exps.add(pr_exp);
				} 
		 }
        
        for(OWLLogicalAxiom axiom: module_1.getLogicalAxioms()) {
			list_role_edges.add(toGraph.AxiomConverter_RoleInclusions(transitive_roles_exps, axiom));
		}
        
        graph.setRoleEdges(list_role_edges);
        
        long startTime21 = System.currentTimeMillis();

       	for(OWLClass defined_cl: sig_O.getClassesInSignature()) {
       		if(isDefined(defined_cl, O){
       			Def_Vertex DV = toGraph.getDefVertexFromClass(defined_cl);
       			System.out.println("current DV: " + DV);
       			Set<OWLEquivalentClassesAxiom> equiv_of_current_defined = module_1.getEquivalentClassesAxioms(defined_cl);
       			System.out.println("current equiv_of_current_defined: " + equiv_of_current_defined);
       			BFS get_def = new BFS(graph);
       			//for each DV vertex in defined_cls get the RHS
       			graph.setVertexLhs(DV);
       			Vertex gotten_dv = graph.getVertexLhs();
       			System.out.println("current gotten DV: " + gotten_dv);
       			OWLEquivalentClassesAxiom OWCA = get_def.get_abstract_def();
       			Set<OWLSubClassOfAxiom> subof = OWCA.asOWLSubClassOfAxioms();
       			for(OWLSubClassOfAxiom s: subof) {
       				OWLClassExpression rhs = s.getSuperClass();
       				if(rhs instanceof OWLObjectIntersectionOf) {
       					Set<OWLClassExpression> conjuncts = rhs.asConjunctSet();
       					if(!conjuncts.isEmpty()) {
       						abstracted_definitions.add(OWCA);
       					}
       				}
       			}
       		}
        }
       	long endTime21 = System.currentTimeMillis();
       	System.out.println("Total Definitions Extraction Duration = " + (endTime21 - startTime21) + " millis");
        System.out.println("size of abstracted_definitions: " + abstracted_definitions.size());
        Set<OWLEquivalentClassesAxiom> entailed_abstracted_definitions = new HashSet<>();
        //validate the definitions
        for(OWLEquivalentClassesAxiom abstract_def: abstracted_definitions) {
        	System.out.println("the current abstract_def is: " + abstract_def);
        		if(checkEntailement(abstract_def, module_1)) {
        			entailed_abstracted_definitions.add(abstract_def);
        		}
        }
        
        
        System.out.println("size of entailed_abstracted_definitions: " + entailed_abstracted_definitions.size());
        
        manager5.addAxioms(ontology_abstract_def, entailed_abstracted_definitions);
        
        OutputStream os_onto_witness_1 = new FileOutputStream(sig_file + "-abstract_def_newclass-3-2017.owl");
		manager5.saveOntology(ontology_abstract_def, new FunctionalSyntaxDocumentFormat(), os_onto_witness_1);
		
		
        }
	
	
	public boolean checkEntailement(OWLLogicalAxiom logicalAxiom, OWLOntology o) {
		//OWLReasonerFactory fac = new Reasoner.ReasonerFactory();
		//OWLReasoner hermit_reasoner = fac.createReasoner(o);
		Configuration c = new Configuration();
		OWLReasoner reasoner = new Reasoner(c,o);
		if(reasoner.isEntailed(logicalAxiom)) {
			System.out.println("The axiom is entailed by O");
			reasoner.dispose();
			return true;
		}else {
			System.out.println("The axiom is NOT entailed by O");
			reasoner.dispose();
			return false;
		}
	}
	
	public boolean isDefined(OWLClass cl, OWLOntology ontology) {
		Set<OWLEquivalentClassesAxiom> cl_axioms = ontology.getEquivalentClassesAxioms(cl);
		if(!(cl_axioms.isEmpty())) {
			return true;
		}
		return false; 
	}
	
	public OWLOntology classifyOntology(OWLOntology ontology) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		
		
		System.out.println("ontology classes size = " + ontology.getClassesInSignature().size());
		System.out.println("ontology properties size = " + ontology.getObjectPropertiesInSignature().size());
		
		
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubClassAxiomGenerator());
		gens.add(new InferredEquivalentClassAxiomGenerator());

		OWLOntology infOnt = manager.createOntology();
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
				gens);
		iog.fillOntology(df, infOnt);
		reasoner.dispose();
		return infOnt;			
	}
	
	public static void main(String args[]) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, ClassNotFoundException {
		System.out.println("--- Computing abstracted defs ---");
		/*//String filePath1 = args[0];
		System.out.println("--------------Signature file name: " + filePath1 + "--------------");
		String filePath2 = args[1];
		System.out.println("--------------Ontology file name: " + filePath2 + "--------------");
		UseGraph_server g = new UseGraph_server();
		if(filePath1 != null && filePath2 != null) {
			long startTime1 = System.currentTimeMillis();
			
			g.useBFS_get_defined_sig(filePath1, filePath2);
			long endTime1 = System.currentTimeMillis();
			System.out.println("Total Duration = " + (endTime1 - startTime1) + " millis");
		}*/
	
		////file paths for testing purposes:
		
		Main g = new Main();
		//String filePath1 = "/Users/ghadahalghamdi/Documents/Redundancy-removal-testing-examples-30-05/ex-3-simple.owl";
		//String filePath1 = "/Users/ghadahalghamdi/Downloads/MRI-signature.owl-sig_module.owl-sig_equiv_defined.owl";
		//String filePath2 = "/Users/ghadahalghamdi/Downloads/MRI-signature.owl-sig_module.owl";
		String filePath1 = "/Users/ghadahalghamdi/Documents/SNOMED-CT-project/MRI-signature.owl";
		
		//String filePath2 = "/Users/ghadahalghamdi/Downloads/MRI-signature.owl-sig_module.owl-sig_equiv_defined.owl-sig_module-2017.owl";
		String filePath2 ="/Users/ghadahalghamdi/Documents/SNOMED-CT-project/MRI-signature.owl-sig_module-2017.owl";
		//g.use_BFS_adjmap(filePath1);
		g.useBFS_get_defined_sig(filePath1, filePath2);
		//g.useBFS_mod(filePath2);
	}

}
