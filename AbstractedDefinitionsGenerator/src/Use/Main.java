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
import java.util.HashMap;
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
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import Converter.ToGraph;
import Converter.ToOWL;
import Graph.Def_Vertex;
import Graph.Edge;
import Graph.NDef_Vertex;
import Graph.Vertex;

import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class Main {
	//Graph graph = new Graph();
	Graph graph = new Graph();
	
	//the second file was module_file
	public void useBFS_get_defined_sig(String sig_file, String O_file) throws OWLOntologyCreationException, IOException, ClassNotFoundException, OWLOntologyStorageException {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(O_file + "-useBFS-output-stream-07-10-10-2020-sct2020.txt"));
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
		File file2 = new File(O_file);
		IRI iri2 = IRI.create(file2);
		OWLOntology O = manager2.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri2),
				new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
		
		System.out.println("the O axioms size: " + O.getLogicalAxiomCount());
		System.out.println("the O classes size: " + O.getClassesInSignature().size());
		System.out.println("the O properties size: " + O.getObjectPropertiesInSignature().size());
		
		
		
		//OWLOntology module = extract_module(sig_file, o_file);
		//Extract the star module for the defined sig concepts
		Set<OWLEntity> module_sig = new HashSet<>();
		for(OWLEntity en: sig_O.getSignature()) {
			//if(isDefined(cl, O)) {
			if(en.toString().equals("<http://snomed.info/id/90688005>") 
   					|| 
   					en.toString().equals("<http://snomed.info/id/42399005>") 
   					|| 
   					en.toString().equals("<http://snomed.info/id/14669001>") 
   					|| 
   					//en.toString().contains("236423003")
   					//|| 
   					en.toString().equals("<http://snomed.info/id/302233006>") 
   					|| 
   					en.toString().equals("<http://snomed.info/id/51292008>")
   					|| 
   					en.toString().equals("<http://snomed.info/id/840580004>")
   					//en.toString().equals("<http://snomed.info/id/64572001>")
   					//|| 
   					//en.toString().equals("<http://snomed.info/id/404684003>")
   					) {
				module_sig.add(en);		
			}
			//}
		}
		System.out.println("the size of defined_cls is: " + module_sig.size());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		SyntacticLocalityModuleExtractor sme_original = new SyntacticLocalityModuleExtractor(manager,
				O, uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR);
		
		OWLOntology module_1 = sme_original.extractAsOntology(module_sig, IRI.generateDocumentIRI());
		
		//save the module
		System.out.println("the module_1 axioms size: " + module_1.getLogicalAxiomCount());
		System.out.println("the module_1 classes size: " + module_1.getClassesInSignature().size());
		System.out.println("the module_1 properties size: " + module_1.getObjectPropertiesInSignature().size());
		
		OutputStream os_onto = new FileOutputStream(sig_file + "-module.owl");
		manager.saveOntology(module_1, new FunctionalSyntaxDocumentFormat(), os_onto);
		//OWLOntology O_classified = classifyOntology(module_1);
		
		OWLOntology O_classified = classifyOntology(O);
		
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
		
		
        List<Edge> list_role_edges = new ArrayList<>();
        
        BFS bfs = new BFS(graph);
        
        Set<OWLEquivalentClassesAxiom> abstracted_definitions = new HashSet<>();
        Set<OWLSubClassOfAxiom> inclusion_axioms = new HashSet<>();
        Set<OWLSubObjectPropertyOfAxiom> property_inclusion_axioms = new HashSet<>();
        
        Set<Vertex> sigma_plus_vertices = new HashSet<>();
        Set<Vertex> sigma_plus_property_vertices = new HashSet<>();
        Set<Vertex> sigma_plus_class_vertices = new HashSet<>();
        
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
        
        
        
        
        //System.out.println("the list role_edges: " + list_role_edges);
        
      //clean the list_role_edges from null edges!
   		List<Edge> list_role_edges_clean = new ArrayList<>(list_role_edges);
   		for(Edge edge: list_role_edges) {
   			if(edge == null) {
   				list_role_edges_clean.remove(edge);
   			}
   		}
   		graph.setRoleEdges(list_role_edges_clean);
   		//System.out.println("the list_role_edges_clean: " +list_role_edges_clean);
        long startTime21 = System.currentTimeMillis();
     	for(OWLEntity en: O.getSignature()) {
       		if(en.isOWLClass()) {
       			//Chronic renal failure syndrome (disorder)
       			if(en.toString().equals("<http://snomed.info/id/90688005>") 
       					|| 
       					//Renal failure syndrome (disorder)
       					en.toString().equals("<http://snomed.info/id/42399005>") 
       					|| 
       					//Acute renal failure syndrome (disorder)
       					en.toString().equals("<http://snomed.info/id/14669001>") 
       					|| 
       					//en.toString().contains("236423003")
       					//|| 
       					//Renal artery stenosis (disorder)
       					en.toString().equals("<http://snomed.info/id/302233006>") 
       					|| 
       					//Hepatorenal syndrome (disorder)
       					en.toString().equals("<http://snomed.info/id/51292008>")
       					|| 
       					//Peripheral arterial disease (disorder)
       					en.toString().equals("<http://snomed.info/id/840580004>")
       					//en.toString().equals("<http://snomed.info/id/64572001>")
       					//|| 
       					//en.toString().equals("<http://snomed.info/id/404684003>")
       					) {
       				
       			if(isDefined(en.asOWLClass(), O)) {
       			
       				Def_Vertex DV = toGraph.getDefVertexFromClass(en.asOWLClass());
       				//System.out.println("current DV to generate abs def: " + DV);
       				//Set<OWLEquivalentClassesAxiom> equiv_of_current_defined = module_1.getEquivalentClassesAxioms(en.asOWLClass());
       				//System.out.println("current equiv_of_current_defined: " + equiv_of_current_defined);
       				//BFS get_def = new BFS(graph);
       				//for each DV vertex in defined_cls get the RHS
       				graph.setVertexLhs(DV);
       				Vertex gotten_dv = graph.getVertexLhs();
       				System.out.println("current gotten DV: " + gotten_dv);
       				OWLEquivalentClassesAxiom OWCA = bfs.get_abstract_def();
       				Set<OWLSubClassOfAxiom> subof = OWCA.asOWLSubClassOfAxioms();
       				for(OWLSubClassOfAxiom s: subof) {
       					OWLClassExpression rhs = s.getSuperClass();
       					if(rhs instanceof OWLObjectIntersectionOf) {
       						Set<OWLClassExpression> conjuncts = rhs.asConjunctSet();
       						if(!conjuncts.isEmpty()) {
       							abstracted_definitions.add(OWCA);
       							//test getting from graph object the closest primitives and the existential restrictions, in order to add it to Sigma +
       							sigma_plus_class_vertices.addAll(graph.getProximalPrimitiveVertices());
       							sigma_plus_class_vertices.add(graph.getVertexLhs());
       							sigma_plus_vertices.addAll(graph.getExistentialVerticesNoRedundancies());
       						}
       					}
       				}
       			}//if the concept is primitive then get the immediate adjacent vertices
       			else if(!isDefined(en.asOWLClass(), O)) {
       				NDef_Vertex NDV = toGraph.getNDefVertexFromClass(en.asOWLClass());
       			//	System.out.println("current NDV to generate inclusion: " + NDV);
       				graph.setVertexLhs(NDV);
       				//Vertex gotten_dv = graph.getVertexLhs();
       			
       				//Set<OWLSubClassOfAxiom> subof_ax = O.getSubClassAxiomsForSubClass(en.asOWLClass());
       			//	System.out.println("current subof_ax: " + subof_ax);
       				//inclusion_axioms.addAll(subof_ax);
       				//get the vertices of the RHS of axioms in the set subof_ax
       				/*for(OWLSubClassOfAxiom subof: subof_ax) {
       				//	System.out.println("The current subof: " + subof);
       					if(!subof.isGCI()) {
       						OWLClassExpression rhs = subof.getSuperClass();
       						sigma_plus_class_vertices.addAll(toGraph.getClassVerticesInSignatureInRHSExpression(rhs));
       						sigma_plus_property_vertices.addAll(toGraph.getPropertyVerticesInSignatureInRHSExpression(rhs));
       						sigma_plus_class_vertices.add(NDV);
       						}
       					}*/
       				
       				OWLSubClassOfAxiom OSCA = bfs.get_abstract_inclusion();
       				OWLClassExpression rhs = OSCA.getSuperClass();
       				if(rhs instanceof OWLObjectIntersectionOf) {
   						Set<OWLClassExpression> conjuncts = rhs.asConjunctSet();
   						if(!conjuncts.isEmpty()) {
   							inclusion_axioms.add(OSCA);
   							//test getting from graph object the closest primitives and the existential restrictions, in order to add it to Sigma +
   							sigma_plus_class_vertices.addAll(graph.getProximalPrimitiveVertices());
   							sigma_plus_class_vertices.add(graph.getVertexLhs());
   							sigma_plus_vertices.addAll(graph.getExistentialVerticesNoRedundancies());
   						}
   					}
       				
       				//check if the primitive concept is in the RHS of a GCI axiom
       				Set<OWLSubClassOfAxiom> subof_ax = O.getSubClassAxiomsForSuperClass(en.asOWLClass());
       				if(!subof_ax.isEmpty()) {
       					inclusion_axioms.addAll(subof_ax);
       					for(OWLSubClassOfAxiom subof: subof_ax) {
           				//	System.out.println("The current subof: " + subof);
           					//if(!subof.isGCI()) {
           						OWLClassExpression lhs = subof.getSubClass();
           						sigma_plus_class_vertices.addAll(toGraph.getClassVerticesInSignatureInExpression(lhs));
           						sigma_plus_property_vertices.addAll(toGraph.getPropertyVerticesInSignatureInExpression(lhs));
           						sigma_plus_class_vertices.add(NDV);
           						//}
           					}
       				}
       			}
       			}
       		}
       		
       		if(en.isOWLObjectProperty()) {
       			NDef_Vertex NPV = toGraph.getVertexFromProperty(en.asOWLObjectProperty());
       			//System.out.println("current property vertex: " + NPV);
       			Set<OWLSubObjectPropertyOfAxiom> owl_sub_property = O.getObjectSubPropertyAxiomsForSubProperty(en.asOWLObjectProperty());
       			//System.out.println("The current owl_sub_property before retrieval from edges: " + owl_sub_property);
       			//if the axiom owl_sub_property axiom is empty then it means that it's not expected to find a supertype for the subproperty
       			graph.setVertexLhs(NPV);
       			//instead of using  bfs, use the role edges?
       			//assuming that the gotten npv is the destination? (the child), we will get its parent (the source from the role edges)
       			if(!owl_sub_property.isEmpty()) {
       				for(Edge edge: list_role_edges_clean) {
       					//get from edge the parent?
       					//check if the current edge child vertex (destination is equal to the current entity)
       					//System.out.println("the current edge: " + edge);
       					//if(!edge.equals(null)) {
       						Vertex edge_destination = edge.getGDestination();
       					//	System.out.println("the edge_destination is: " + edge_destination);
       						if(NPV.toString().equals(edge_destination.toString())){
       							Vertex edge_source = edge.getGSource();
       							String property_vertex1_name = edge_source.toString();
       							String property_vertex2_name = edge_destination.toString();
       							//System.out.println("the current en is:  is equal to the destination in the edge: " + en);
       							OWLSubObjectPropertyOfAxiom owl_sub_property_axiom = bfs.getOWLSubProperty(property_vertex1_name, property_vertex2_name);
       						//	System.out.println("the owl_sub_property_axiom is: " + owl_sub_property_axiom);
       							property_inclusion_axioms.add(owl_sub_property_axiom);
       							//add the vertices (edge_destination) and (edge_source) to Sigma_plus_vertices
       							//sigma_plus_vertices.add(edge_source);
       							sigma_plus_property_vertices.add(edge_source);
       							sigma_plus_property_vertices.add(edge_destination);
       						}
       					//}
       				}
       			}
       		}
       		
       		
	}
     	
     	long endTime21 = System.currentTimeMillis(); 	
       	System.out.println("Total Definitions Extraction (with inclusions) Duration = " + (endTime21 - startTime21) + " millis");
     	
     	for(Vertex vertex: sigma_plus_vertices) {
     		//System.out.println("the vertex in sigma_plus_vertices: " + vertex);
       		String vertex_role_name = "";
       		String vertex_class_name = "";
       		if(vertex.toString().contains("-role-label")) {
       			if(vertex.toString().contains("_i_")) {
       				String[] vertex_name_s = vertex.toString().split("_i_");
       				String vertex_name_w_role = vertex_name_s[0];
       				String[] vertex_name_w_role_s = vertex_name_w_role.split("-role-label: ");
       				vertex_role_name = vertex_name_w_role_s[1];
       				vertex_class_name = vertex_name_w_role_s[0];
       			}else {
       				String[] vertex_name_w_role_s = vertex.toString().split("-role-label: ");
       				vertex_role_name = vertex_name_w_role_s[1];
       				vertex_class_name = vertex_name_w_role_s[0];
       			}
       			//creating a new vertex role might not make it possible to get subsumption (as it might not be recogised using the role edges, test this!)
       			//if I do the same for the class (filler) of existential vertex then I'm sure that it will be regarded as a new vertex and then won't 
       			//be .. there is a way to figure the concept from the existential vertex, but first test this
       			NDef_Vertex property_vertex = new NDef_Vertex(vertex_role_name);
       			sigma_plus_property_vertices.add(property_vertex);
       			///
       			Vertex class_vertex =  new Vertex(vertex_class_name);
       			sigma_plus_class_vertices.add(class_vertex);
       		}else {
       			vertex_class_name = vertex.toString();
       			Vertex class_vertex =  new Vertex(vertex_class_name);
       			sigma_plus_class_vertices.add(class_vertex);
       		}	
       	}
     	
     	
     	
     	//System.out.println("the set sigma_plus_vertices: " + sigma_plus_vertices);
     	System.out.println("the size set sigma_plus_vertices: " + sigma_plus_vertices.size());
     	//get accurate set of sigma_plus_vertices
     	Set<Vertex> clean_sigma_plus_vertices = new HashSet<>();
     	clean_sigma_plus_vertices.addAll(sigma_plus_class_vertices);
     	clean_sigma_plus_vertices.addAll(sigma_plus_property_vertices);
     	Set<String> clean_sigma_plus_vertices_no_duplicates = new HashSet<>();
     	
     	//get the names of the vertices in the set clean_sigma_plus_vertices, put them in a new set
     	for(Vertex v: clean_sigma_plus_vertices) {
     		String v_name = v.toString();
     		clean_sigma_plus_vertices_no_duplicates.add(v_name);
     	}
     	
     	//System.out.println("the set sigma_plus_class_vertices: " + sigma_plus_class_vertices);
     	System.out.println("the size set sigma_plus_class_vertices: " + sigma_plus_class_vertices.size());
     	
     	
     	System.out.println("the size of set clean_sigma_plus_vertices_no_duplicates: " + clean_sigma_plus_vertices_no_duplicates.size());
     	//System.out.println("the set clean_sigma_plus_vertices_no_duplicates: " + clean_sigma_plus_vertices_no_duplicates);
     	
       	//remove duplicates from the set sigma_plus_class_vertices
     	Set<String> clean_sigma_plus_class_vertices_no_duplicates_str = new HashSet<>();
     	for(Vertex v: sigma_plus_class_vertices) {
     		String v_name = v.toString();
     		clean_sigma_plus_class_vertices_no_duplicates_str.add(v_name);
     	}
     	
     	Set<Vertex> clean_sigma_plus_class_vertices_no_duplicates = new HashSet<>();
     	for(String v_str : clean_sigma_plus_class_vertices_no_duplicates_str) {
     		Vertex v = new Vertex(v_str);
     		clean_sigma_plus_class_vertices_no_duplicates.add(v);
     	}
     	
     	//System.out.println("the set clean_sigma_plus_class_vertices_no_duplicates: " + clean_sigma_plus_class_vertices_no_duplicates);
     	System.out.println("the size set clean_sigma_plus_class_vertices_no_duplicates: " + clean_sigma_plus_class_vertices_no_duplicates.size());
     	
        long startTime31 = System.currentTimeMillis();
   		System.out.println(" --Generating completion axioms-- ");
   		
   		for(Vertex cl_vertex_1: clean_sigma_plus_class_vertices_no_duplicates) {
   			//System.out.println("the current cl_vertex_1: " + cl_vertex_1);
   			for(Vertex cl_vertex_2: clean_sigma_plus_class_vertices_no_duplicates) {
   				//System.out.println("the current cl_vertex_2: " + cl_vertex_2);
   				if(!cl_vertex_1.equals(cl_vertex_2)) {
   					if(bfs.BFS_sigma_plus_vertices(cl_vertex_1, cl_vertex_2, adjacency_list_map)) {
   						Set<OWLClassExpression> rhs_conjunct = new HashSet<>();
   					//	System.out.println("there is subsumption between the classes");
   						ToOWL toOWL = new ToOWL();
   						OWLClass cl_1 = toOWL.getOwlClassFromVertex(cl_vertex_1);
   						OWLClass cl_2 = toOWL.getOwlClassFromVertex(cl_vertex_2);
   						rhs_conjunct.add(cl_2);
   						OWLSubClassOfAxiom subof = toOWL.getOWLSubClassOf(cl_1, rhs_conjunct);
   						//System.out.println("subof (subsumption between vertices in Sigma plus): " + subof);
   						//before adding the subof check if it's a transitive closure, this will be done by going through the set inclusion_axioms, for example if I have A <= C, then check if 
   						//if(isTransitive(subof, inclusion_axioms)) {
   						//	System.out.println("the axiom is transitive");}
   					//	else {
   						inclusion_axioms.add(subof);
   						//}
   					}
   				}
   			}
   		}
   		
   		for(Vertex op_vertex_1: sigma_plus_property_vertices) {
   			for(Vertex op_vertex_2: sigma_plus_property_vertices) {
   				String property_vertex1_name = op_vertex_1.toString();
					String property_vertex2_name = op_vertex_2.toString();
					if(bfs.checkSubsumptionRelationBetweenTwoRoles_TD(property_vertex1_name, property_vertex2_name, list_role_edges)) {
						//System.out.println("there is subsumption between the properties");
						OWLSubObjectPropertyOfAxiom sub_object_owl_axiom = bfs.getOWLSubProperty(property_vertex1_name, property_vertex2_name);
						//System.out.println("sub_object_owl_axiom: " + sub_object_owl_axiom);
						property_inclusion_axioms.add(sub_object_owl_axiom);
					}
   			}
   		}
   		long endTime31 = System.currentTimeMillis(); 	
   		System.out.println("Total inferring inclusion axioms Duration = " + (endTime31 - startTime31) + " millis");
   		
   		long startTime41 = System.currentTimeMillis();
   		//remove trasnitive closure axioms from the set inclusion_axioms
   		OWLDataFactory df = manager1.getOWLDataFactory();
   		//System.out.println("the inclusion_axioms set: " + inclusion_axioms);
   		Set<OWLSubClassOfAxiom> inclusion_axioms_no_transitive = new HashSet<>(inclusion_axioms);
   		for(OWLSubClassOfAxiom subof_ax_1: inclusion_axioms) {
   			OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   			OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   			if(rhs_1 instanceof OWLClass) {
   			Set<OWLSubClassOfAxiom> subofs_2 = O.getSubClassAxiomsForSubClass(rhs_1.asOWLClass());
   			for(OWLSubClassOfAxiom subof_ax_2: subofs_2) {
   			//for(OWLSubClassOfAxiom subof_ax_2: inclusion_axioms) {
   				if(!subof_ax_1.equals(subof_ax_2)) {
   					OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   					OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   					OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
   					if(inclusion_axioms.contains(lhs_1_rhs_2)) {
   						inclusion_axioms_no_transitive.remove(lhs_1_rhs_2);
   					}
   				}
   			//}
   			}
   			}else if(rhs_1 instanceof OWLObjectIntersectionOf) {
   				Set<OWLClassExpression> rhs_1_conjuncts = rhs_1.asConjunctSet();
   				for(OWLClassExpression rhs_1_conjunct: rhs_1_conjuncts) {
   					if(rhs_1_conjunct instanceof OWLClass) {
   						Set<OWLSubClassOfAxiom> subofs_2 = O.getSubClassAxiomsForSubClass(rhs_1_conjunct.asOWLClass());
   						for(OWLSubClassOfAxiom subof_ax_2: subofs_2) {
   				   			//for(OWLSubClassOfAxiom subof_ax_2: inclusion_axioms) {
   				   				if(!subof_ax_1.equals(subof_ax_2)) {
   				   					OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   				   					OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   				   					OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
   				   					if(inclusion_axioms.contains(lhs_1_rhs_2)) {
   				   						inclusion_axioms_no_transitive.remove(lhs_1_rhs_2);
   				   					}
   				   				}
   				   			//}
   				   			}
   						
   					}
   				}
   			}
   			
   		}
   		//System.out.println("the inclusion_axioms_no_transitive set: " + inclusion_axioms_no_transitive);
       	//System.out.println("the current sigma_plus_property_vertices: " + sigma_plus_property_vertices);
   		
   		//remove transitive closure axioms from property_inclusion_axioms
   		Set<OWLSubObjectPropertyOfAxiom> property_inclusion_axioms_no_transitive = new HashSet<>(property_inclusion_axioms);
   		
   		for(OWLSubObjectPropertyOfAxiom sub_pr_of_ax_1: property_inclusion_axioms) {
   			OWLObjectPropertyExpression lhs_1 = sub_pr_of_ax_1.getSubProperty();
   			OWLObjectPropertyExpression rhs_1 = sub_pr_of_ax_1.getSuperProperty();
   			
   			for(OWLSubObjectPropertyOfAxiom sub_pr_of_ax_2: property_inclusion_axioms) {
   				if(!sub_pr_of_ax_1.equals(sub_pr_of_ax_2)) {
   					OWLObjectPropertyExpression lhs_2 = sub_pr_of_ax_2.getSubProperty();
   		   			OWLObjectPropertyExpression rhs_2 = sub_pr_of_ax_2.getSuperProperty();
   					OWLSubObjectPropertyOfAxiom lhs_1_rhs_2 = df.getOWLSubObjectPropertyOfAxiom(lhs_1, rhs_2);
   					if(property_inclusion_axioms.contains(lhs_1_rhs_2)) {
   						property_inclusion_axioms_no_transitive.remove(lhs_1_rhs_2);
   					}
   				}
   			}
   			
   		}
   	
       	
      //remove redundant inclusion axioms
   		//Set<OWLSubClassOfAxiom> no_redundant_inclusion_axioms = new HashSet<>(inclusion_axioms);
   		//assume that subof_1 is A <= B
   		//combine the abstracted defs with inclusion in one set to check for this duplication!
   		Set<OWLAxiom> inclusions_and_equivs = new HashSet<>();
   		//inclusions_and_equivs.addAll(inclusion_axioms);
   		inclusions_and_equivs.addAll(inclusion_axioms_no_transitive);
   		inclusions_and_equivs.addAll(abstracted_definitions);
   		Set<OWLAxiom> inclusions_and_equivs_no_redundant = new HashSet<>(inclusions_and_equivs);
   		
   		for(OWLAxiom ax_1: inclusions_and_equivs) {
   			for(OWLAxiom ax_2: inclusions_and_equivs) {
   				if(ax_1 instanceof OWLEquivalentClassesAxiom) {
   					//System.out.println("The current ax_1: " + ax_1);
   					OWLEquivalentClassesAxiom equiv_ax_1 = (OWLEquivalentClassesAxiom) ax_1;
   					if(ax_2 instanceof OWLSubClassOfAxiom) {
   						//System.out.println("The current ax_2: " + ax_2);
   					OWLSubClassOfAxiom subof_ax_2 = (OWLSubClassOfAxiom) ax_2;
   					Set<OWLSubClassOfAxiom> ax_1_subs = equiv_ax_1.asOWLSubClassOfAxioms();
   					for(OWLSubClassOfAxiom ax_1_sub: ax_1_subs) {
   						if(!ax_1_sub.isGCI()) {
   							if(!ax_1_sub.equals(subof_ax_2)) {
   							//	System.out.println("the current ax_1_sub: " + ax_1_sub);
   							OWLClassExpression lhs_1 = ax_1_sub.getSubClass();
   							OWLClassExpression rhs_1 = ax_1_sub.getSuperClass();
   							OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   			   				OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   			   			if(!ax_1_sub.equals(subof_ax_2)) {
   		   					if(lhs_1.equals(lhs_2)) {
   		   						if(rhs_1.containsConjunct(rhs_2)) {
   		   						//	System.out.println("The current axiom will be removed: " + subof_ax_2);
   		   							inclusions_and_equivs_no_redundant.remove(subof_ax_2);
   		   							}
   		   						}
   		   					}
   							}
   						}
   						}
   					}
   				}
   				
   				if(ax_1 instanceof OWLSubClassOfAxiom) {
   					//System.out.println("The current ax_1 (subof): " + ax_1);
   					OWLSubClassOfAxiom subof_ax_1 = (OWLSubClassOfAxiom) ax_1;
   					if(ax_2 instanceof OWLEquivalentClassesAxiom) {
   					//	System.out.println("The current ax_2: (equiv) " + ax_2);
   						OWLEquivalentClassesAxiom equiv_ax_2 = (OWLEquivalentClassesAxiom) ax_2;
   						Set<OWLSubClassOfAxiom> ax_2_subs = equiv_ax_2.asOWLSubClassOfAxioms();
   						for(OWLSubClassOfAxiom ax_2_sub: ax_2_subs) {
   							if(!ax_2_sub.isGCI()) {
   								if(!ax_2_sub.equals(subof_ax_1)) {
   						//		System.out.println("the current ax_2_sub: " + ax_2_sub);
   							OWLClassExpression lhs_2 = ax_2_sub.getSubClass();
   							OWLClassExpression rhs_2 = ax_2_sub.getSuperClass();
   							OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   			   				OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   			   			if(!ax_2_sub.equals(subof_ax_1)) {
   		   					if(lhs_1.equals(lhs_2)) {
   		   						if(rhs_2.containsConjunct(rhs_1)) {
   		   						//	System.out.println("The current axiom will be removed: " + subof_ax_1);
   		   							inclusions_and_equivs_no_redundant.remove(subof_ax_1);
   		   							}
   		   						}
   		   					}
   							}
   						}
   						}
   					}
   				}
   				
   				if(ax_1 instanceof OWLSubClassOfAxiom) {
   				//	System.out.println("The current ax_1 (subof): " + ax_1);
   					if(ax_2 instanceof OWLSubClassOfAxiom) {
   						if(!ax_1.equals(ax_2)) {
   					//	System.out.println("The current ax_2: (subof) " + ax_2);
   						OWLSubClassOfAxiom subof_ax_1 = (OWLSubClassOfAxiom) ax_1;
   	   					OWLSubClassOfAxiom subof_ax_2 = (OWLSubClassOfAxiom) ax_2;
   	   					OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   	   					OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   	   					OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   	   					OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   	   					if(lhs_1.equals(lhs_2)) {
   	   						if(rhs_2.containsConjunct(rhs_1)) {
   	   					//		System.out.println("The current axiom will be removed: " + subof_ax_1);
   	   							inclusions_and_equivs_no_redundant.remove(subof_ax_1);
   	   						}
   	   					}
   					}
   					}
   				}
   			}
   		}
   		long endTime41 = System.currentTimeMillis(); 	
   		System.out.println("Total removing transitive and redundant axioms Duration = " + (endTime41 - startTime41) + " millis");
   		
   		//normalise the inclusion axioms, then remove transitive closure axioms from it.
   		//OWLDataFactory df = manager1.getOWLDataFactory();
   		//Set<OWLSubClassOfAxiom> no_redundant_inclusion_axioms_normalised = new HashSet<>();
   		
   		//Set<OWLAxiom> no_redundant_inclusion_axioms_normalised = new HashSet<>();
   		//Set<OWLAxiom> axioms_no_transitive = remove_transitive_closure_axioms(inclusions_and_equivs_no_redundant, O);
   		
   		//System.out.println("the axioms without transitive closure: " + axioms_no_transitive);
   		
   		
   		System.out.println();
   		Set<OWLSubClassOfAxiom> inclusions_and_equivs_no_redundant_subofs = new HashSet<>();
   		for(OWLAxiom axiom: inclusions_and_equivs_no_redundant) {
   			if(axiom.isOfType(AxiomType.SUBCLASS_OF)) {
   				OWLSubClassOfAxiom subof_ax = (OWLSubClassOfAxiom) axiom;
   				inclusions_and_equivs_no_redundant_subofs.add(subof_ax);
   			}
   		}
   		
   		
   		System.out.println("The size of inclusion_axioms: " + inclusion_axioms.size());
   		System.out.println("The size of inclusions_and_equivs_no_redundant: " + inclusions_and_equivs_no_redundant.size());
   		
       	System.out.println("size of abstracted_definitions: " + abstracted_definitions.size());
        System.out.println("size of inclusion_axioms: "+ inclusion_axioms.size());
        System.out.println("size of inclusions_and_equivs_no_redundant_subofs: "+ inclusions_and_equivs_no_redundant_subofs.size());
        System.out.println("size of property_inclusion_axioms_no_transitive: "+ property_inclusion_axioms_no_transitive.size());
        
      
        Set<OWLEquivalentClassesAxiom> entailed_abstracted_definitions = new HashSet<>();
        Set<OWLSubClassOfAxiom> entailed_inclusion_axioms = new HashSet<>();
        Set<OWLSubObjectPropertyOfAxiom> entailed_property_inclusion_axioms = new HashSet<>();
        //validate the definitions
        for(OWLEquivalentClassesAxiom abstract_def: abstracted_definitions) {
        	System.out.println("the current abstract_def is: " + abstract_def);
        		if(checkEntailement(abstract_def, O)) {
        			entailed_abstracted_definitions.add(abstract_def);
        		}
        }
        
                
        for(OWLSubClassOfAxiom subof_ax: inclusions_and_equivs_no_redundant_subofs) {
        	System.out.println("the current subof_ax is: " + subof_ax);
        		if(checkEntailement(subof_ax, O)) {
        			entailed_inclusion_axioms.add(subof_ax);
        		}
        }
        
        for(OWLSubObjectPropertyOfAxiom property_ax: property_inclusion_axioms_no_transitive) {
        	System.out.println("the current property_ax is: " + property_ax);
        	if(checkEntailement(property_ax, O)) {
        		entailed_property_inclusion_axioms.add(property_ax);
        		}
        }
        
        System.out.println("size of entailed_abstracted_definitions: " + entailed_abstracted_definitions.size());
        System.out.println("size of entailed_inclusion_axioms: " + entailed_inclusion_axioms.size());
        System.out.println("size of entailed_property_inclusion_axioms: " + entailed_property_inclusion_axioms.size());
        
        manager5.addAxioms(ontology_abstract_def, entailed_abstracted_definitions);
        manager5.addAxioms(ontology_abstract_def, entailed_inclusion_axioms);
        manager5.addAxioms(ontology_abstract_def, entailed_property_inclusion_axioms);
        //System.out.println("the ontology_abstract_def axioms: " + ontology_abstract_def.getLogicalAxioms());
        
        //A post process remove transitive closure axioms from all the axioms in ontology_abstract_def for cases such as: A == B1 and C, B1 <= B2, A <= B2 (remove A <= B2)
       
        long startTime51 = System.currentTimeMillis();
        
        Set<OWLAxiom> ontology_abstract_def_axs_no_transitive = new HashSet<>(ontology_abstract_def.getAxioms());
        for(OWLAxiom ax_1: ontology_abstract_def.getAxioms()) {
        		if(ax_1 instanceof OWLEquivalentClassesAxiom) {
        			OWLEquivalentClassesAxiom equiv_ax = (OWLEquivalentClassesAxiom) ax_1;
        			Set<OWLSubClassOfAxiom> equiv_ax_subofs = equiv_ax.asOWLSubClassOfAxioms();
        			for(OWLSubClassOfAxiom subof_eq_1: equiv_ax_subofs) {
        				if(!subof_eq_1.isGCI()) {
        					OWLClassExpression lhs_1 = subof_eq_1.getSubClass();
        					OWLClassExpression rhs_1 = subof_eq_1.getSuperClass();
        					if(rhs_1 instanceof OWLObjectIntersectionOf) {
        						Set<OWLClassExpression> rhs_1_conjuncts = rhs_1.asConjunctSet();
        						for(OWLClassExpression rhs_1_conjunct: rhs_1_conjuncts) {
        							if(rhs_1_conjunct instanceof OWLClass) {
        								Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1_conjunct.asOWLClass());
        								for(OWLSubClassOfAxiom subof_2: subofs_2) {
        									OWLClassExpression lhs_2 = subof_2.getSubClass();
        									OWLClassExpression rhs_2 = subof_2.getSuperClass();
        									if(rhs_2 instanceof OWLObjectIntersectionOf) {
        										//the trasnitivity will occur whichever the type of rhs_2 is so for every conjunct of rhs_2 check if the axiom lhs_1, rhs_2_conjunct exist!
        										Set<OWLClassExpression> rhs_2_conjuncts = rhs_2.asConjunctSet();
        										for(OWLClassExpression rhs_2_conjuct: rhs_2_conjuncts) {
        											OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2_conjuct);
                									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
                										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
                									}
        										}
        									}
        									if(rhs_2 instanceof OWLClass) {
        									OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
        									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
        										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
        									}
        									}
        								}
        							}
        						}
        					}else if(rhs_1 instanceof OWLClass) {
        						Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1.asOWLClass());
        						for(OWLSubClassOfAxiom subof_2: subofs_2) {
        							OWLClassExpression lhs_2 = subof_2.getSubClass();
								OWLClassExpression rhs_2 = subof_2.getSuperClass();
								OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
								if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
									ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
								}
        						}
        					}
        				}
        			} 
        		}
        		if(ax_1 instanceof OWLSubClassOfAxiom) {
        			OWLSubClassOfAxiom subof_1 = (OWLSubClassOfAxiom) ax_1;
        			OWLClassExpression lhs_1 = subof_1.getSubClass();
				OWLClassExpression rhs_1 = subof_1.getSuperClass();
				if(rhs_1 instanceof OWLObjectIntersectionOf) {
					Set<OWLClassExpression> rhs_1_conjuncts = rhs_1.asConjunctSet();
					for(OWLClassExpression rhs_1_conjunct: rhs_1_conjuncts) {
						if(rhs_1_conjunct instanceof OWLClass) {
							Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1_conjunct.asOWLClass());
							for(OWLSubClassOfAxiom subof_2: subofs_2) {
								OWLClassExpression lhs_2 = subof_2.getSubClass();
								OWLClassExpression rhs_2 = subof_2.getSuperClass();
								if(rhs_2 instanceof OWLObjectIntersectionOf) {
									//the trasnitivity will occur whichever the type of rhs_2 is so for every conjunct of rhs_2 check if the axiom lhs_1, rhs_2_conjunct exist!
									Set<OWLClassExpression> rhs_2_conjuncts = rhs_2.asConjunctSet();
									for(OWLClassExpression rhs_2_conjuct: rhs_2_conjuncts) {
										OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2_conjuct);
    									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
    										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
    									}
									}
								}
								if(rhs_2 instanceof OWLClass) {
								OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
								if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
									ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
								}
								}
							}
						}
					}
				}else if(rhs_1 instanceof OWLClass) {
					Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1.asOWLClass());
					for(OWLSubClassOfAxiom subof_2: subofs_2) {
						OWLClassExpression lhs_2 = subof_2.getSubClass();
					OWLClassExpression rhs_2 = subof_2.getSuperClass();
					OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
					if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
						ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
					}
					}
				}
        		}
        }
        
        long endTime51 = System.currentTimeMillis(); 	
   		System.out.println("Total post process step to remove transitive axioms Duration = " + (endTime51 - startTime51) + " millis");
        
   		OWLOntology ontology_abstract_def_no_transitive = manager5.createOntology();
        manager5.addAxioms(ontology_abstract_def_no_transitive, ontology_abstract_def_axs_no_transitive);
       // System.out.println("the ontology_abstract_def: " + ontology_abstract_def.getLogicalAxiomCount());
       // System.out.println("the ontology_abstract_def_no_transitive: " + ontology_abstract_def_no_transitive.getLogicalAxiomCount());
        
        //Another post process step is to remove inclusion axioms that are inferred because of this
        //(as an example): A1 == B1 and C1, A2 == B1 and B2 and C1 then A1 <= A2, I would use the method of checking subsumption relations 
        //between the existential restrictions, I would pass the rhs or A1 and A2 to check if they subsume each other using the method:
        
        //go through every two equivalent axioms then convert the RHS_1 to list of vertices, and the same with RHS_2
        //in the conversion step, we need to convert the existential restrictions to existential vertices?
        Map<OWLAxiom, List<Vertex>> owl_axiom_rhs_exps = new HashMap<>();
        //after finding that there is subsumption relations between the current list<Vertex> then check if the lhs of owl_axiom_1 and 
        //owl_axiom_2 subsume each other and that subsumption is in the ontology
		/*for(OWLAxiom ax: O.getLogicalAxioms()) {
			List<Vertex> rhs_vertices = new ArrayList<>();
			owl_axiom_rhs_exps.put(ax, new ArrayList<>());
			if(ax instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom ax_quiv = (OWLEquivalentClassesAxiom) ax;
				Set<OWLSubClassOfAxiom> subof_ax_quiv = ax_quiv.asOWLSubClassOfAxioms();
				for(OWLSubClassOfAxiom subof: subof_ax_quiv) {
					if(!subof.isGCI()) {
						OWLClassExpression lhs = subof.getSubClass();
						OWLClassExpression rhs = subof.getSuperClass();
						if(rhs instanceof OWLObjectIntersectionOf) {
							Set<OWLClassExpression> rhs_conjuncts = rhs.asConjunctSet();
							for(OWLClassExpression rhs_conjunct: rhs_conjuncts) {
								if(rhs_conjunct instanceof OWLClass) {
									Vertex vertex_cl = toGraph.getVertexFromClass(rhs_conjunct.asOWLClass());
									rhs_vertices.add(vertex_cl);
								}else if(rhs_conjunct instanceof OWLObjectSomeValuesFrom) {
									OWLObjectSomeValuesFrom rhs_1_conjunct_obsv = (OWLObjectSomeValuesFrom) rhs_conjunct;
									OWLClassExpression rhs_1_conjunct_obsv_filler = rhs_1_conjunct_obsv.getFiller();
									//check if the rhs_conjunct is a simple type exists expression
									if(rhs_1_conjunct_obsv_filler instanceof OWLClass) {
										//call the 
										Vertex exists_vertex = toGraph.getExistentialVertexFromSimpleExistentialRestriction(rhs_1_conjunct_obsv);
										rhs_vertices.add(exists_vertex);
									}if(rhs_1_conjunct_obsv_filler instanceof OWLObjectSomeValuesFrom) {
										Vertex exists_vertex = toGraph.getExistentialVertexFromNestedExistentialRestriction_no_conjunction(rhs_1_conjunct_obsv);
										rhs_vertices.add(exists_vertex);
									}if(rhs_1_conjunct_obsv_filler instanceof OWLObjectIntersectionOf) {
										List<Vertex> exists_vertex = toGraph.getExistentialVertexFromNestedExistentialRestriction_with_conjunction(rhs_1_conjunct_obsv);
										rhs_vertices.addAll(exists_vertex);
									}
								}
							}
						}
					}
				}
			}
			
			owl_axiom_rhs_exps.replace(ax, rhs_vertices);
		}*/
		
		
		
        OutputStream os_onto_witness_1 = new FileOutputStream(O_file + "-abstract_def-07-10-10-2020-sct2020.owl");
		manager5.saveOntology(ontology_abstract_def_no_transitive, new FunctionalSyntaxDocumentFormat(), os_onto_witness_1);
		
		
        }
	
	/*public void useBFS_get_defined_sig(String sig_file, String module_file) throws OWLOntologyCreationException, IOException, ClassNotFoundException, OWLOntologyStorageException {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(sig_file + "-useBFS-04-2017.txt"));
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
        Set<OWLSubClassOfAxiom> inclusion_axioms = new HashSet<>();
        Set<OWLSubObjectPropertyOfAxiom> property_inclusion_axioms = new HashSet<>();
        
        
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
        
        
        List<Edge> list_role_edges_clean = new ArrayList<>(list_role_edges);
   		for(Edge edge: list_role_edges) {
   			if(edge == null) {
   				list_role_edges_clean.remove(edge);
   			}
   		}
   		graph.setRoleEdges(list_role_edges_clean);
        BFS bfs = new BFS(graph);
        
        Set<Vertex> sigma_plus_vertices = new HashSet<>();
        Set<Vertex> sigma_plus_property_vertices = new HashSet<>();
        Set<Vertex> sigma_plus_class_vertices = new HashSet<>();
        
        
        long startTime21 = System.currentTimeMillis();
        
       	//for(OWLClass cl: sig_O.getClassesInSignature()) {
        for(OWLEntity en: sig_O.getSignature()) {
        			if(en.isOWLClass()) {
        				
        				//GET THE ABSTRACT DEFINITION FOR THE DEFINED CONCEPT
        				if(isDefined(en.asOWLClass(), O)){
        					Def_Vertex DV = toGraph.getDefVertexFromClass(en.asOWLClass());
        					System.out.println("current DV: " + DV);
        					Set<OWLEquivalentClassesAxiom> equiv_of_current_defined = module_1.getEquivalentClassesAxioms(en.asOWLClass());
        					System.out.println("current equiv_of_current_defined: " + equiv_of_current_defined);
        					//BFS get_def = new BFS(graph);
        					//for each DV vertex in defined_cls get the RHS
        					graph.setVertexLhs(DV);
        					Vertex gotten_dv = graph.getVertexLhs();
        					System.out.println("current gotten DV: " + gotten_dv);
        					OWLEquivalentClassesAxiom OWCA = bfs.get_abstract_def();
        					Set<OWLSubClassOfAxiom> subof = OWCA.asOWLSubClassOfAxioms();
        					for(OWLSubClassOfAxiom s: subof) {
        						OWLClassExpression rhs = s.getSuperClass();
        						if(rhs instanceof OWLObjectIntersectionOf) {
        							Set<OWLClassExpression> conjuncts = rhs.asConjunctSet();
        							if(!conjuncts.isEmpty()) {
        								abstracted_definitions.add(OWCA);
        								sigma_plus_class_vertices.addAll(graph.getProximalPrimitiveVertices());
        								sigma_plus_class_vertices.add(graph.getVertexLhs());
        	        						sigma_plus_vertices.addAll(graph.getExistentialVerticesNoRedundancies());
        							}
        						}
        					}
        				}

        				//GET THE INCLUSION AXIOM FOR THE PRIMITIVE CONCEPT
        				else if(!isDefined(en.asOWLClass(), O)) {
        	   				NDef_Vertex NDV = toGraph.getNDefVertexFromClass(en.asOWLClass());
        	   				System.out.println("current NDV: " + NDV);
        	   				Set<OWLSubClassOfAxiom> subof_ax = O.getSubClassAxiomsForSubClass(en.asOWLClass());
        	   				System.out.println("current subof_ax: " + subof_ax);
        	   				inclusion_axioms.addAll(subof_ax);
        	   				//get the vertices of the RHS of axioms in the set subof_ax
        	   				for(OWLSubClassOfAxiom subof: subof_ax) {
        	   					System.out.println("The current subof: " + subof);
        	   					if(!subof.isGCI()) {
        	   						OWLClassExpression rhs = subof.getSuperClass();
        	   						sigma_plus_class_vertices.addAll(toGraph.getClassVerticesInSignatureInRHSExpression(rhs));
        	   						sigma_plus_property_vertices.addAll(toGraph.getPropertyVerticesInSignatureInRHSExpression(rhs));
        	   						sigma_plus_class_vertices.add(NDV);
        	   						}
        	   					}
        	       			}
        			}
        			//GET THE ROLE AXIOM FOR THE ROLE IN SIGMA
        			if(en.isOWLObjectProperty()) {
        				NDef_Vertex NPV = toGraph.getVertexFromProperty(en.asOWLObjectProperty());
        				Set<OWLSubObjectPropertyOfAxiom> owl_sub_property = O.getObjectSubPropertyAxiomsForSubProperty(en.asOWLObjectProperty());
        				graph.setVertexLhs(NPV);
        				//instead of using  bfs, use the role edges?
        				//assuming that the gotten npv is the destination? (the child), we will get its parent (the source from the role edges)
        				if(!owl_sub_property.isEmpty()) {
        				for(Edge edge: list_role_edges_clean) {
        					//get from edge the parent?
        					//check if the current edge child vertex (destination is equal to the current entity)
        					
        					Vertex edge_destination = edge.getGDestination();
        					if(NPV.toString().equals(edge_destination.toString())){
        						Vertex edge_source = edge.getGSource();
        						String property_vertex1_name = edge_source.toString();
        	   					String property_vertex2_name = edge_destination.toString();
        						OWLSubObjectPropertyOfAxiom owl_sub_property_axiom = bfs.getOWLSubProperty(property_vertex1_name, property_vertex2_name);
        						property_inclusion_axioms.add(owl_sub_property_axiom);
        						sigma_plus_property_vertices.add(edge_source);
        						sigma_plus_property_vertices.add(edge_destination);
        					}
        					
        				}
        				}
        			}
        }
       
        for(Vertex vertex: sigma_plus_vertices) {
       		String vertex_role_name = "";
       		String vertex_class_name = "";
       		if(vertex.toString().contains("-role-label")) {
       			if(vertex.toString().contains("_i_")) {
       				String[] vertex_name_s = vertex.toString().split("_i_");
       				String vertex_name_w_role = vertex_name_s[0];
       				String[] vertex_name_w_role_s = vertex_name_w_role.split("-role-label: ");
       				vertex_role_name = vertex_name_w_role_s[1];
       				vertex_class_name = vertex_name_w_role_s[0];
       			}else {
       				String[] vertex_name_w_role_s = vertex.toString().split("-role-label: ");
       				vertex_role_name = vertex_name_w_role_s[1];
       				vertex_class_name = vertex_name_w_role_s[0];
       			}
       			
       			NDef_Vertex property_vertex = new NDef_Vertex(vertex_role_name);
       			sigma_plus_property_vertices.add(property_vertex);
      
       			Vertex class_vertex =  new Vertex(vertex_class_name);
       			sigma_plus_class_vertices.add(class_vertex);
       		}else {
       			vertex_class_name = vertex.toString();
       			Vertex class_vertex =  new Vertex(vertex_class_name);
       			sigma_plus_class_vertices.add(class_vertex);
       		}		
       	}
        
        //System.out.println("the set sigma_plus_class_vertices: " + sigma_plus_class_vertices);
        System.out.println(" --Generating completion axioms-- ");
        for(Vertex cl_vertex_1: sigma_plus_class_vertices) {
   			//System.out.println("the current cl_vertex_1: " + cl_vertex_1);
   			for(Vertex cl_vertex_2: sigma_plus_class_vertices) {
   				//System.out.println("the current cl_vertex_2: " + cl_vertex_2);
   				if(!cl_vertex_1.equals(cl_vertex_2)) {
   					if(bfs.BFS_sigma_plus_vertices(cl_vertex_1, cl_vertex_2, adjacency_list_map)) {
   						Set<OWLClassExpression> rhs_conjunct = new HashSet<>();
   						ToOWL toOWL = new ToOWL();
   						OWLClass cl_1 = toOWL.getOwlClassFromVertex(cl_vertex_1);
   						OWLClass cl_2 = toOWL.getOwlClassFromVertex(cl_vertex_2);
   						rhs_conjunct.add(cl_2);
   						OWLSubClassOfAxiom subof = toOWL.getOWLSubClassOf(cl_1, rhs_conjunct);
   						inclusion_axioms.add(subof);
   					}
   				}
   			}
   		}
        
      //remove trasnitive closure axioms from the set inclusion_axioms
   		OWLDataFactory df = manager1.getOWLDataFactory();
   		Set<OWLSubClassOfAxiom> inclusion_axioms_no_transitive = new HashSet<>(inclusion_axioms);
   		for(OWLSubClassOfAxiom subof_ax_1: inclusion_axioms) {
   			OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   			OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   			
   			for(OWLSubClassOfAxiom subof_ax_2: inclusion_axioms) {
   				if(!subof_ax_1.equals(subof_ax_2)) {
   					OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   					OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   					OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
   					if(inclusion_axioms.contains(lhs_1_rhs_2)) {
   						inclusion_axioms_no_transitive.remove(lhs_1_rhs_2);
   					}
   				}
   			}
   			
   		}
        
        //System.out.println("the current sigma_plus_property_vertices: " + sigma_plus_property_vertices);
        
        for(Vertex op_vertex_1: sigma_plus_property_vertices) {
   			for(Vertex op_vertex_2: sigma_plus_property_vertices) {
   				String property_vertex1_name = op_vertex_1.toString();
					String property_vertex2_name = op_vertex_2.toString();
					if(bfs.checkSubsumptionRelationBetweenTwoRoles_TD(property_vertex1_name, property_vertex2_name, list_role_edges)) {
						OWLSubObjectPropertyOfAxiom sub_object_owl_axiom = bfs.getOWLSubProperty(property_vertex1_name, property_vertex2_name);
						property_inclusion_axioms.add(sub_object_owl_axiom);
					}
   			}
   		}
        
      //remove transitive closure axioms from property_inclusion_axioms
   		Set<OWLSubObjectPropertyOfAxiom> property_inclusion_axioms_no_transitive = new HashSet<>(property_inclusion_axioms);
   		
   		for(OWLSubObjectPropertyOfAxiom sub_pr_of_ax_1: property_inclusion_axioms) {
   			OWLObjectPropertyExpression lhs_1 = sub_pr_of_ax_1.getSubProperty();
   			OWLObjectPropertyExpression rhs_1 = sub_pr_of_ax_1.getSuperProperty();
   			
   			for(OWLSubObjectPropertyOfAxiom sub_pr_of_ax_2: property_inclusion_axioms) {
   				if(!sub_pr_of_ax_1.equals(sub_pr_of_ax_2)) {
   					OWLObjectPropertyExpression lhs_2 = sub_pr_of_ax_2.getSubProperty();
   		   			OWLObjectPropertyExpression rhs_2 = sub_pr_of_ax_2.getSuperProperty();
   					OWLSubObjectPropertyOfAxiom lhs_1_rhs_2 = df.getOWLSubObjectPropertyOfAxiom(lhs_1, rhs_2);
   					if(property_inclusion_axioms.contains(lhs_1_rhs_2)) {
   						property_inclusion_axioms_no_transitive.remove(lhs_1_rhs_2);
   					}
   				}
   			}
   			
   		}
   		
   		Set<OWLAxiom> inclusions_and_equivs = new HashSet<>();
   		inclusions_and_equivs.addAll(inclusion_axioms_no_transitive);
   		inclusions_and_equivs.addAll(abstracted_definitions);
   		Set<OWLAxiom> inclusions_and_equivs_no_redundant = new HashSet<>(inclusions_and_equivs);
      //remove redundant inclusion axioms
   		Set<OWLSubClassOfAxiom> no_redundant_inclusion_axioms = new HashSet<>(inclusion_axioms);
   		for(OWLSubClassOfAxiom subof_1: inclusion_axioms) {
   			OWLClassExpression lhs_1 = subof_1.getSubClass();
   			OWLClassExpression rhs_1 = subof_1.getSuperClass();
   			for(OWLSubClassOfAxiom subof_2: inclusion_axioms) {
   				OWLClassExpression lhs_2 = subof_2.getSubClass();
   				OWLClassExpression rhs_2 = subof_2.getSuperClass();
   				if(!subof_1.equals(subof_2)) {
   					if(lhs_1.equals(lhs_2)) {
   						if(rhs_2.containsConjunct(rhs_1)) {
   							no_redundant_inclusion_axioms.remove(subof_1);
   						}
   					}
   				}
   			}
   		}
        
   		
   		for(OWLAxiom ax_1: inclusions_and_equivs) {
   			for(OWLAxiom ax_2: inclusions_and_equivs) {
   				if(ax_1 instanceof OWLEquivalentClassesAxiom) {
   					System.out.println("The current ax_1: " + ax_1);
   					OWLEquivalentClassesAxiom equiv_ax_1 = (OWLEquivalentClassesAxiom) ax_1;
   					if(ax_2 instanceof OWLSubClassOfAxiom) {
   						System.out.println("The current ax_2: " + ax_2);
   					OWLSubClassOfAxiom subof_ax_2 = (OWLSubClassOfAxiom) ax_2;
   					Set<OWLSubClassOfAxiom> ax_1_subs = equiv_ax_1.asOWLSubClassOfAxioms();
   					for(OWLSubClassOfAxiom ax_1_sub: ax_1_subs) {
   						if(!ax_1_sub.isGCI()) {
   							if(!ax_1_sub.equals(subof_ax_2)) {
   								System.out.println("the current ax_1_sub: " + ax_1_sub);
   							OWLClassExpression lhs_1 = ax_1_sub.getSubClass();
   							OWLClassExpression rhs_1 = ax_1_sub.getSuperClass();
   							OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   			   				OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   			   			if(!ax_1_sub.equals(subof_ax_2)) {
   		   					if(lhs_1.equals(lhs_2)) {
   		   						if(rhs_1.containsConjunct(rhs_2)) {
   		   							System.out.println("The current axiom will be removed: " + subof_ax_2);
   		   							inclusions_and_equivs_no_redundant.remove(subof_ax_2);
   		   							}
   		   						}
   		   					}
   							}
   						}
   						}
   					}
   				}
   				
   				if(ax_1 instanceof OWLSubClassOfAxiom) {
   					System.out.println("The current ax_1 (subof): " + ax_1);
   					OWLSubClassOfAxiom subof_ax_1 = (OWLSubClassOfAxiom) ax_1;
   					if(ax_2 instanceof OWLEquivalentClassesAxiom) {
   						System.out.println("The current ax_2: (equiv) " + ax_2);
   						OWLEquivalentClassesAxiom equiv_ax_2 = (OWLEquivalentClassesAxiom) ax_2;
   						Set<OWLSubClassOfAxiom> ax_2_subs = equiv_ax_2.asOWLSubClassOfAxioms();
   						for(OWLSubClassOfAxiom ax_2_sub: ax_2_subs) {
   							if(!ax_2_sub.isGCI()) {
   								if(!ax_2_sub.equals(subof_ax_1)) {
   								System.out.println("the current ax_2_sub: " + ax_2_sub);
   							OWLClassExpression lhs_2 = ax_2_sub.getSubClass();
   							OWLClassExpression rhs_2 = ax_2_sub.getSuperClass();
   							OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   			   				OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   			   			if(!ax_2_sub.equals(subof_ax_1)) {
   		   					if(lhs_1.equals(lhs_2)) {
   		   						if(rhs_2.containsConjunct(rhs_1)) {
   		   							System.out.println("The current axiom will be removed: " + subof_ax_1);
   		   							inclusions_and_equivs_no_redundant.remove(subof_ax_1);
   		   							}
   		   						}
   		   					}
   							}
   						}
   						}
   					}
   				}
   				
   				if(ax_1 instanceof OWLSubClassOfAxiom) {
   					System.out.println("The current ax_1 (subof): " + ax_1);
   					if(ax_2 instanceof OWLSubClassOfAxiom) {
   						if(!ax_1.equals(ax_2)) {
   						System.out.println("The current ax_2: (subof) " + ax_2);
   						OWLSubClassOfAxiom subof_ax_1 = (OWLSubClassOfAxiom) ax_1;
   	   					OWLSubClassOfAxiom subof_ax_2 = (OWLSubClassOfAxiom) ax_2;
   	   					OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
   	   					OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
   	   					OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
   	   					OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
   	   					if(lhs_1.equals(lhs_2)) {
   	   						if(rhs_2.containsConjunct(rhs_1)) {
   	   							System.out.println("The current axiom will be removed: " + subof_ax_1);
   	   							inclusions_and_equivs_no_redundant.remove(subof_ax_1);
   	   						}
   	   					}
   					}
   					}
   				}
   			}
   		}
       
        
       	long endTime21 = System.currentTimeMillis();
       	System.out.println("Total Definitions Extraction Duration = " + (endTime21 - startTime21) + " millis");
       	
       	
       	Set<OWLSubClassOfAxiom> inclusions_and_equivs_no_redundant_subofs = new HashSet<>();
   		for(OWLAxiom axiom: inclusions_and_equivs_no_redundant) {
   			if(axiom.isOfType(AxiomType.SUBCLASS_OF)) {
   				OWLSubClassOfAxiom subof_ax = (OWLSubClassOfAxiom) axiom;
   				inclusions_and_equivs_no_redundant_subofs.add(subof_ax);
   			}
   		}
   		
   		
   		System.out.println("The size of inclusion_axioms: " + inclusion_axioms.size());
   		System.out.println("The size of inclusions_and_equivs_no_redundant: " + inclusions_and_equivs_no_redundant.size());
   		
   		
        System.out.println("size of abstracted_definitions: " + abstracted_definitions.size());
        System.out.println("size of inclusion_axioms: "+ inclusion_axioms);
        System.out.println("size of inclusions_and_equivs_no_redundant_subofs: "+ inclusions_and_equivs_no_redundant_subofs);
        System.out.println("size of property_inclusion_axioms_no_transitive: "+ property_inclusion_axioms_no_transitive);
        
        Set<OWLEquivalentClassesAxiom> entailed_abstracted_definitions = new HashSet<>();
        Set<OWLSubClassOfAxiom> entailed_inclusion_axioms = new HashSet<>();
        Set<OWLSubObjectPropertyOfAxiom> entailed_property_inclusion_axioms = new HashSet<>();
        
        
        //validate the definitions
        for(OWLEquivalentClassesAxiom abstract_def: abstracted_definitions) {
        	System.out.println("the current abstract_def is: " + abstract_def);
        		if(checkEntailement(abstract_def, module_1)) {
        			entailed_abstracted_definitions.add(abstract_def);
        		}
        }
        
        for(OWLSubClassOfAxiom subof_ax: inclusions_and_equivs_no_redundant_subofs) {
        	System.out.println("the current subof_ax is: " + subof_ax);
        		if(checkEntailement(subof_ax, O)) {
        		//if(checkEntailement(abstract_def, module_1)) {
        			entailed_inclusion_axioms.add(subof_ax);
        		}
        }
        
        for(OWLSubObjectPropertyOfAxiom property_ax: property_inclusion_axioms_no_transitive) {
        	System.out.println("the current property_ax is: " + property_ax);
        	if(checkEntailement(property_ax, O)) {
        		//if(checkEntailement(abstract_def, module_1)) {
        		entailed_property_inclusion_axioms.add(property_ax);
        		}
        }
        
        
        System.out.println("size of entailed_abstracted_definitions: " + entailed_abstracted_definitions.size());
        System.out.println("size of entailed_inclusion_axioms: " + entailed_inclusion_axioms.size());
        System.out.println("size of entailed_property_inclusion_axioms: " + entailed_property_inclusion_axioms.size());
        
        
        manager5.addAxioms(ontology_abstract_def, entailed_abstracted_definitions);
        manager5.addAxioms(ontology_abstract_def, entailed_inclusion_axioms);
        manager5.addAxioms(ontology_abstract_def, entailed_property_inclusion_axioms);
        
        //A post process step to remove transitive closure axioms from all the axioms in ontology_abstract_def for cases such as: A == B1 and C, B1 <= B2, A <= B2 (remove A <= B2)
        Set<OWLAxiom> ontology_abstract_def_axs_no_transitive = new HashSet<>(ontology_abstract_def.getAxioms());
        for(OWLAxiom ax_1: ontology_abstract_def.getAxioms()) {
        		if(ax_1 instanceof OWLEquivalentClassesAxiom) {
        			OWLEquivalentClassesAxiom equiv_ax = (OWLEquivalentClassesAxiom) ax_1;
        			Set<OWLSubClassOfAxiom> equiv_ax_subofs = equiv_ax.asOWLSubClassOfAxioms();
        			for(OWLSubClassOfAxiom subof_eq_1: equiv_ax_subofs) {
        				if(!subof_eq_1.isGCI()) {
        					OWLClassExpression lhs_1 = subof_eq_1.getSubClass();
        					OWLClassExpression rhs_1 = subof_eq_1.getSuperClass();
        					if(rhs_1 instanceof OWLObjectIntersectionOf) {
        						Set<OWLClassExpression> rhs_1_conjuncts = rhs_1.asConjunctSet();
        						for(OWLClassExpression rhs_1_conjunct: rhs_1_conjuncts) {
        							if(rhs_1_conjunct instanceof OWLClass) {
        								Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1_conjunct.asOWLClass());
        								for(OWLSubClassOfAxiom subof_2: subofs_2) {
        									OWLClassExpression lhs_2 = subof_2.getSubClass();
        									OWLClassExpression rhs_2 = subof_2.getSuperClass();
        									if(rhs_2 instanceof OWLObjectIntersectionOf) {
        										//the trasnitivity will occur whichever the type of rhs_2 is so for every conjunct of rhs_2 check if the axiom lhs_1, rhs_2_conjunct exist!
        										Set<OWLClassExpression> rhs_2_conjuncts = rhs_2.asConjunctSet();
        										for(OWLClassExpression rhs_2_conjuct: rhs_2_conjuncts) {
        											OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2_conjuct);
                									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
                										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
                									}
        										}
        									}
        									if(rhs_2 instanceof OWLClass) {
        									OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
        									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
        										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
        									}
        									}
        								}
        							}
        						}
        					}else if(rhs_1 instanceof OWLClass) {
        						Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1.asOWLClass());
        						for(OWLSubClassOfAxiom subof_2: subofs_2) {
        							OWLClassExpression lhs_2 = subof_2.getSubClass();
								OWLClassExpression rhs_2 = subof_2.getSuperClass();
								OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
								if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
									ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
								}
        						}
        					}
        				}
        			} 
        		}
        		if(ax_1 instanceof OWLSubClassOfAxiom) {
        			OWLSubClassOfAxiom subof_1 = (OWLSubClassOfAxiom) ax_1;
        			OWLClassExpression lhs_1 = subof_1.getSubClass();
				OWLClassExpression rhs_1 = subof_1.getSuperClass();
				if(rhs_1 instanceof OWLObjectIntersectionOf) {
					Set<OWLClassExpression> rhs_1_conjuncts = rhs_1.asConjunctSet();
					for(OWLClassExpression rhs_1_conjunct: rhs_1_conjuncts) {
						if(rhs_1_conjunct instanceof OWLClass) {
							Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1_conjunct.asOWLClass());
							for(OWLSubClassOfAxiom subof_2: subofs_2) {
								OWLClassExpression lhs_2 = subof_2.getSubClass();
								OWLClassExpression rhs_2 = subof_2.getSuperClass();
								if(rhs_2 instanceof OWLObjectIntersectionOf) {
									//the trasnitivity will occur whichever the type of rhs_2 is so for every conjunct of rhs_2 check if the axiom lhs_1, rhs_2_conjunct exist!
									Set<OWLClassExpression> rhs_2_conjuncts = rhs_2.asConjunctSet();
									for(OWLClassExpression rhs_2_conjuct: rhs_2_conjuncts) {
										OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2_conjuct);
    									if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
    										ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
    									}
									}
								}
								if(rhs_2 instanceof OWLClass) {
								OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
								if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
									ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
								}
								}
							}
						}
					}
				}else if(rhs_1 instanceof OWLClass) {
					Set<OWLSubClassOfAxiom> subofs_2 = ontology_abstract_def.getSubClassAxiomsForSubClass(rhs_1.asOWLClass());
					for(OWLSubClassOfAxiom subof_2: subofs_2) {
						OWLClassExpression lhs_2 = subof_2.getSubClass();
					OWLClassExpression rhs_2 = subof_2.getSuperClass();
					OWLSubClassOfAxiom lhs_1_rhs_2 = df.getOWLSubClassOfAxiom(lhs_1, rhs_2);
					if(ontology_abstract_def.getLogicalAxioms().contains(lhs_1_rhs_2)) {
						ontology_abstract_def_axs_no_transitive.remove(lhs_1_rhs_2);
					}
					}
				}
        		}
        }
        OWLOntology ontology_abstract_def_no_transitive = manager5.createOntology();
        manager5.addAxioms(ontology_abstract_def_no_transitive, ontology_abstract_def_axs_no_transitive);
        System.out.println("the ontology_abstract_def: " + ontology_abstract_def.getLogicalAxiomCount());
        System.out.println("the ontology_abstract_def_no_transitive: " + ontology_abstract_def_no_transitive.getLogicalAxiomCount());
        
        OutputStream os_onto_witness_1 = new FileOutputStream(module_file + "-abstract_def-05-ERA-small-d-c-no-trs-2.owl");
		manager5.saveOntology(ontology_abstract_def_no_transitive, new FunctionalSyntaxDocumentFormat(), os_onto_witness_1);
        }*/
	
	
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
	
	//PostProcessor (remove transitive closure axioms, and duplicate axioms)
	
	
	//Method that removes transitive closure axioms (I'm assuming that all transitive closure axioms are inclusion axioms)
		public void remove_transitive_closure_axioms(String filePath) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
			OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
			
			File file1 = new File(filePath);
			IRI iri1 = IRI.create(file1);
			OWLOntology O = manager1.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri1),
					new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
			
			
			System.out.println("the O axioms size: " + O.getLogicalAxiomCount());
			System.out.println("the O classes size: " + O.getClassesInSignature().size());
			System.out.println("the O properties size: " + O.getObjectPropertiesInSignature().size());
			
			
			Set<OWLSubClassOfAxiom> inclusion_axioms = new HashSet<>();
			for(OWLAxiom axiom: O.getLogicalAxioms()) {
				if(axiom.isOfType(AxiomType.SUBCLASS_OF)) {
					OWLSubClassOfAxiom subof = (OWLSubClassOfAxiom) axiom;
					if(!subof.isGCI()) {
						inclusion_axioms.add(subof);
					}
				}
			}
			
			System.out.println("the inclusion axioms size: " + inclusion_axioms.size());
			//Set<OWLSubClassOfAxiom> inclusion_axioms_no_transitive = new HashSet<>(inclusion_axioms);
			Set<OWLAxiom> O_axioms = new HashSet<>(O.getAxioms());
			
			OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
			OWLDataFactory df = manager2.getOWLDataFactory();
			//A <= B
			for(OWLSubClassOfAxiom subof_i: inclusion_axioms) {
				OWLClassExpression lhs_i = subof_i.getSubClass();
				OWLClassExpression rhs_i = subof_i.getSuperClass();
				//get the axioms where B is a subclass
				if(rhs_i instanceof OWLClass) {
					Set<OWLSubClassOfAxiom> subofs_j = O.getSubClassAxiomsForSubClass(rhs_i.asOWLClass());
					//B <= C
					for(OWLSubClassOfAxiom subof_j: subofs_j) {
						OWLClassExpression rhs_j = subof_j.getSuperClass();
						OWLSubClassOfAxiom lhs_i_rhs_j = df.getOWLSubClassOfAxiom(lhs_i, rhs_j);
						if(inclusion_axioms.contains(lhs_i_rhs_j)) {
							OWLAxiom lhs_i_rhs_j_ax = (OWLAxiom) lhs_i_rhs_j;
							O_axioms.remove(lhs_i_rhs_j_ax);
						}
					}
				}
				
				
			}
			
			OWLOntologyManager manager3 = OWLManager.createOWLOntologyManager();
			OWLOntology O_without_transitive_closures = manager3.createOntology();
			manager3.addAxioms(O_without_transitive_closures, O_axioms);
			System.out.println("the O_without_transitive_closures axioms size: " + O_without_transitive_closures.getLogicalAxiomCount());
			System.out.println("the O_without_transitive_closures classes size: " + O_without_transitive_closures.getClassesInSignature().size());
			System.out.println("the O_without_transitive_closures properties size: " + O_without_transitive_closures.getObjectPropertiesInSignature().size());
			OutputStream os_onto_witness_1 = new FileOutputStream(filePath + "_no-transitive.owl");
			manager3.saveOntology(O_without_transitive_closures, new FunctionalSyntaxDocumentFormat(), os_onto_witness_1);
		}
	
	
	//remove duplicate axioms coming from equivalences
		public void remove_duplicate_axioms_from_equivalences(String filePath) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
			OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
			
			File file1 = new File(filePath);
			IRI iri1 = IRI.create(file1);
			OWLOntology O = manager1.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri1),
					new OWLOntologyLoaderConfiguration().setLoadAnnotationAxioms(true));
			
			
			System.out.println("the O axioms size: " + O.getLogicalAxiomCount());
			System.out.println("the O classes size: " + O.getClassesInSignature().size());
			System.out.println("the O properties size: " + O.getObjectPropertiesInSignature().size());
			Set<OWLEquivalentClassesAxiom> equiv_axs = new HashSet<>();
			for(OWLAxiom O_ax: O.getAxioms()) {
				if(O_ax.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					OWLEquivalentClassesAxiom equiv_ax = (OWLEquivalentClassesAxiom) O_ax;
					equiv_axs.add(equiv_ax);
				}
			}
			Set<OWLAxiom> O_axioms_no_duplicates = new HashSet<>(O.getAxioms());
			//by assuming that ax_2 is the bigger axiom that contain the conjunct part
			for(OWLLogicalAxiom ax_1: O.getLogicalAxioms()) {
				for(OWLLogicalAxiom ax_2: O.getLogicalAxioms()) {
					if(!ax_1.equals(ax_2)) {
						if(ax_2 instanceof OWLEquivalentClassesAxiom) {
							OWLEquivalentClassesAxiom equivaxiom = (OWLEquivalentClassesAxiom) ax_2;
							if(ax_1 instanceof OWLSubClassOfAxiom) {
								OWLSubClassOfAxiom subof_1 = (OWLSubClassOfAxiom) ax_1;
								if(!subof_1.isGCI()) {
									OWLClassExpression lhs_1 = subof_1.getSubClass();
									OWLClassExpression rhs_1 = subof_1.getSuperClass();
									Set<OWLSubClassOfAxiom> subofs_2 = equivaxiom.asOWLSubClassOfAxioms();
									for(OWLSubClassOfAxiom subof_2: subofs_2) {
										if(!subof_2.isGCI()) {
											OWLClassExpression lhs_2 = subof_2.getSubClass();
											OWLClassExpression rhs_2 = subof_2.getSuperClass();
											if(lhs_1.equals(lhs_2)) {
												if(rhs_2.containsConjunct(rhs_1)) {
													O_axioms_no_duplicates.remove(subof_1);
												}
											}
										}
									}
								}
							}
						}else if(ax_2 instanceof OWLSubClassOfAxiom) {
							OWLSubClassOfAxiom subof_2 = (OWLSubClassOfAxiom) ax_2;
							if(!subof_2.isGCI()) {
								if(ax_1 instanceof OWLSubClassOfAxiom) {
									OWLSubClassOfAxiom subof_1 = (OWLSubClassOfAxiom) ax_1;
									if(!subof_1.isGCI()) {
										OWLClassExpression lhs_1 = subof_1.getSubClass();
										OWLClassExpression rhs_1 = subof_1.getSuperClass();
										OWLClassExpression lhs_2 = subof_2.getSubClass();
										OWLClassExpression rhs_2 = subof_2.getSuperClass();
										if(lhs_1.equals(lhs_2)) {
											if(rhs_2.containsConjunct(rhs_1)) {
												O_axioms_no_duplicates.remove(subof_1);
											}
										}
										
									}
								}
							}
						}
					}
				}
			}
			
			OWLOntologyManager manager3 = OWLManager.createOWLOntologyManager();
			OWLOntology O_without_duplicates = manager3.createOntology();
			manager3.addAxioms(O_without_duplicates, O_axioms_no_duplicates);
			System.out.println("the O_without_duplicates axioms size: " + O_without_duplicates.getLogicalAxiomCount());
			System.out.println("the O_without_duplicates classes size: " + O_without_duplicates.getClassesInSignature().size());
			System.out.println("the O_without_duplicates properties size: " + O_without_duplicates.getObjectPropertiesInSignature().size());
			OutputStream os_onto_witness_1 = new FileOutputStream(filePath + "_no-dup_from_equiv.owl");
			manager3.saveOntology(O_without_duplicates, new FunctionalSyntaxDocumentFormat(), os_onto_witness_1);
		
		}
		
		public boolean isTransitive(OWLSubClassOfAxiom subof,  Set<OWLSubClassOfAxiom> inclusion_axioms) {
			//assum that subof: A <= C
			OWLClassExpression lhs_tr = subof.getSubClass();
			OWLClassExpression rhs_tr = subof.getSuperClass();
			//go through inclusion axioms: check if current one is A <= B, then in another loop: check if B <= C
			for(OWLSubClassOfAxiom subof_ax_1: inclusion_axioms) {
				OWLClassExpression lhs_1 = subof_ax_1.getSubClass();
				OWLClassExpression rhs_1 = subof_ax_1.getSuperClass();
				
				for(OWLSubClassOfAxiom subof_ax_2: inclusion_axioms) {
					if(!subof_ax_1.equals(subof_ax_2)) {
						OWLClassExpression lhs_2 = subof_ax_2.getSubClass();
						OWLClassExpression rhs_2 = subof_ax_2.getSuperClass();
						if(lhs_tr.equals(lhs_1) && rhs_tr.equals(rhs_2)) {
							return true;
						}
					}
				}
				
			}
			return false;
			
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
