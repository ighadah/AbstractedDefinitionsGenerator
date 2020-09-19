/**
Author: Ghadah Alghamdi
*/

package Converter;


import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import Graph.Vertex;
import Graph.Def_Vertex;
import Graph.Edge;
import Graph.NDef_Vertex;
import Graph.Role_g_Vertex;
import Graph.SimpleEdge;
import Graph.ExistsEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;




public class ToGraph {
	
	
	//getVertexFromClass, the method returns the vertex type, takes as parameter the owl class
	public Vertex getVertexFromClass(OWLClass owlclass) {
		//return the new Vertex (a method implemented in the Vertex class)
		return new Vertex(owlclass.getIRI().toString());
	}
	
	
	//getVertexFromClass, the method returns the vertex type, takes as parameter the owl class
	public Vertex getDef_N_VertexFromClass(OWLClass owlclass, OWLOntology O) {
		//return the new Vertex (a method implemented in the Vertex class)
		if(isDefined(owlclass, O)) {
		return new Def_Vertex(owlclass.getIRI().toString());
		}
		else if(!(isDefined(owlclass, O))) {
		return new NDef_Vertex(owlclass.getIRI().toString());
		}
		return null;
	}
	

	//getVertexFromClass, the method returns the vertex type, takes as parameter the owl class
		public Def_Vertex getDefVertexFromClass(OWLClass owlclass) {
			//return the new Vertex (a method implemented in the Vertex class)
			return new Def_Vertex(owlclass.getIRI().toString());
		}
		
		//getVertexFromClass, the method returns the vertex type, takes as parameter the owl class
		public NDef_Vertex getNDefVertexFromClass(OWLClass owlclass) {
			//return the new Vertex (a method implemented in the Vertex class)
			return new NDef_Vertex(owlclass.getIRI().toString());
		}
		
		//get RoleGroup vertex from the string that I assign to indicate the number 
		public Role_g_Vertex getRoleGroupVertex(String name_vertex) {
			return new Role_g_Vertex(name_vertex);
		}
	
	//return list of vertecies from the ontology classes signature 
	
	
	public List<Vertex> getConceptsInSignature(OWLOntology ontology) {

		List<Vertex> concept_list = new ArrayList<>();
		Set<OWLClass> class_set = ontology.getClassesInSignature();

		for (OWLClass owlClass : class_set) {
			concept_list.add(getVertexFromClass(owlClass));
		}

		return concept_list;
	}
	
	
	public List<Vertex> getConceptsInSignature_def_n(OWLOntology ontology) {

		List<Vertex> concept_list = new ArrayList<>();
		Set<OWLClass> class_set = ontology.getClassesInSignature();
	
		for (OWLClass owlClass : class_set) {
			if(isDefined(owlClass, ontology)) {
			concept_list.add(getDefVertexFromClass(owlClass));
			}else {
			concept_list.add(getNDefVertexFromClass(owlClass));
			}
		}

		return concept_list;
	}
	
	//get concepts in RHS and convert to List of vertices
		public List<Vertex> getClassVerticesInSignatureInRHSExpression(OWLClassExpression rhs){
			List<Vertex> vertices_list = new ArrayList<>();
			Set<OWLClass> class_set = rhs.getClassesInSignature();
			for(OWLClass owlClass: class_set) {
				vertices_list.add(getVertexFromClass(owlClass));
			}
			
			return vertices_list;
		}
		public List<Vertex> getPropertyVerticesInSignatureInRHSExpression(OWLClassExpression rhs){
			List<Vertex> vertices_list = new ArrayList<>();
			Set<OWLObjectProperty> property_set = rhs.getObjectPropertiesInSignature();
			for(OWLObjectProperty owlProperty: property_set) {
				vertices_list.add(getVertexFromProperty(owlProperty));
			}
			
			return vertices_list;
		}
	
	//getEdgeFromIs?
	//from AxiomConverter 
	/*public Edge AxiomConverter(OWLLogicalAxiom axiom) {
		if(axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
			if(!owlSCOA.isGCI()) {
			//check if the owlSCOA has an atomic rhs, then continue the conversion, other wise 
			//get rhs
		
			OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
			if(owlSCOA_rhs instanceof OWLClass) {
				Vertex owlSCOA_rhs_vertex = getVertexFromClass(owlSCOA_rhs.asOWLClass());
				OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
				Vertex owlSCOA_lhs_vertex = getVertexFromClass(owlSCOA_lhs.asOWLClass());
				//get the vertiexs from the classes? 
				//create vertices from the owl classes
				return new Edge(owlSCOA_lhs_vertex, owlSCOA_rhs_vertex);
			}
		}
		else if(axiom instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom equiv = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> axiom_subofs = equiv.asOWLSubClassOfAxioms();
			for(OWLSubClassOfAxiom axiom_subof: axiom_subofs) {
				if(!axiom_subof.isGCI()) {
					OWLClassExpression owlSCOA_rhs = axiom_subof.getSuperClass();
					if(owlSCOA_rhs instanceof OWLClass) {
						//System.out.println("the owlSCOA_rhs of equiv is: " + owlSCOA_rhs);
						Vertex owlSCOA_rhs_vertex = getVertexFromClass(owlSCOA_rhs.asOWLClass());
						OWLClassExpression owlSCOA_lhs = axiom_subof.getSubClass();
						Vertex owlSCOA_lhs_vertex = getVertexFromClass(owlSCOA_lhs.asOWLClass());
						//get the vertiexs from the classes? 
						//create vertices from the owl classes
						return new Edge(owlSCOA_lhs_vertex, owlSCOA_rhs_vertex);
					}
				}
			}
		}
		}
		return null;
	}*/
	
	
	/*public Edge AxiomConverter(OWLLogicalAxiom axiom, OWLOntology O) {
		//either keep a reference of the status of the concepts, or everytime we need to convert an axiom, we need the ontology to check if the concept in the axiom is defined or not!.
		if(axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
			if(!owlSCOA.isGCI()) {
				OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
				if(owlSCOA_rhs instanceof OWLClass) {
					//check if the owlSCOA_rhs is defined or not
					if(isDefined(owlSCOA_rhs.asOWLClass(), O)) {
						//Convert it to the correct Vertex type
						Def_Vertex owlSCOA_rhs_def = getDefVertexFromClass(owlSCOA_rhs.asOWLClass());
						OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
						//check if the lhs is defined or not
						if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
							//then get the defined vertex from the owl class
							Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
							//then return the correct edge (where both sides are defined)
							return new Edge(owlSCOA_rhs_def, owlSCOA_lhs_def);
						}else {
							NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
							return new Edge(owlSCOA_rhs_def, owlSCOA_lhs_ndef);
						}
						
						//then check if the lhs is also defined
					}else {
						//if the rhs is not defined
						NDef_Vertex owlSCOA_rhs_ndef = getNDefVertexFromClass(owlSCOA_rhs.asOWLClass());
						//then checj if the lhs is defined or not
						OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
						if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
							//then get the defined vertex from the owl class
							Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
							//then return the correct edge (where both sides are defined)
							return new Edge(owlSCOA_rhs_ndef, owlSCOA_lhs_def);
						}else {
							NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
							return new Edge(owlSCOA_rhs_ndef, owlSCOA_lhs_ndef);
						}
							
					}
				}
			}
			
			
			
			
		}
		return null;
	}
	*/
	
	//was AxiomConverter
		public Edge AxiomConverter_simple_is_a(OWLLogicalAxiom axiom, OWLOntology O) {
			//either keep a reference of the status of the concepts, or everytime we need to convert an axiom, we need the ontology to check if the concept in the axiom is defined or not!.
			if(axiom instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
				if(!owlSCOA.isGCI()) {
					OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
					if(owlSCOA_rhs instanceof OWLClass) {
						//check if the owlSCOA_rhs is defined or not
						if(isDefined(owlSCOA_rhs.asOWLClass(), O)) {
							//Convert it to the correct Vertex type
							Def_Vertex owlSCOA_rhs_def = getDefVertexFromClass(owlSCOA_rhs.asOWLClass());
							OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
							//check if the lhs is defined or not
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
								//then get the defined vertex from the owl class
								Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//then return the correct edge (where both sides are defined)
								return new SimpleEdge(owlSCOA_rhs_def, owlSCOA_lhs_def);
							}else {
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								return new SimpleEdge(owlSCOA_rhs_def, owlSCOA_lhs_ndef);
							}
							
							//then check if the lhs is also defined
						}else {
							//if the rhs is not defined
							NDef_Vertex owlSCOA_rhs_ndef = getNDefVertexFromClass(owlSCOA_rhs.asOWLClass());
							//then checj if the lhs is defined or not
							OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
								//then get the defined vertex from the owl class
								Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//then return the correct edge (where both sides are defined)
								return new SimpleEdge(owlSCOA_rhs_ndef, owlSCOA_lhs_def);
							}else {
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								return new SimpleEdge(owlSCOA_rhs_ndef, owlSCOA_lhs_ndef);
							}
								
						}
					}
				}
				
				
				
				
			}
			return null;
		}
		
		
		//convert the A is exists relation
		
		public ExistsEdge AxiomConverter_exists_is_a(OWLLogicalAxiom axiom, OWLOntology O) {
			if(axiom instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
				if(!owlSCOA.isGCI()) {
					OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
					OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
					//A subof rg some 
					if(owlSCOA_rhs instanceof OWLObjectSomeValuesFrom) {
						//System.out.println("The axiom rhs is exists");
						OWLObjectSomeValuesFrom exists_exp = (OWLObjectSomeValuesFrom) owlSCOA_rhs;
						OWLClassExpression exists_exp_filler = exists_exp.getFiller();
						//check if the filler is instanceof exists
						//A subof rg some r some
						/*if(exists_exp_filler instanceof OWLObjectSomeValuesFrom) {
							System.out.println("the filler is nested exists: " + exists_exp_filler);
							OWLObjectSomeValuesFrom exists_exp_filler_exp = (OWLObjectSomeValuesFrom) exists_exp_filler;
							
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
								Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//add numbering to the role groups 
								//this number would be incremented with each exists_exp_filler that was found (meet the exists condition)
								index_role_g++;
								String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_def.toString() + "-" + index_role_g;
								Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
								
								//return method was here
								//new ExistsEdge(owlSCOA_lhs_def, role_group_vertex);
								
								//according to this number, create the inside edges where the role group
								//is the source, and the destination is the vertex after adding the number to the name.
								//get the filler of exists_exp_filler_exp
								
								OWLClassExpression exists_exp_filler_exp_filler = exists_exp_filler_exp.getFiller();
								//check if it's conjunct set
								//A subof rg some r some (conjuncts)
								if(exists_exp_filler_exp_filler instanceof OWLObjectIntersectionOf) {
									Set<OWLClassExpression> exists_exp_filler_exp_filler_conjuncts = exists_exp_filler_exp_filler.asConjunctSet();
									for(OWLClassExpression exists_exp_filler_exp_filler_conjunct: exists_exp_filler_exp_filler_conjuncts) {
										if(exists_exp_filler_exp_filler_conjunct instanceof OWLObjectSomeValuesFrom) {
											//get the filler, which will be owl class (simple one) 
											//as in snomed ct there is no 3 nested role expressions, but recheck this!!!
											OWLObjectSomeValuesFrom filler_conjunct_exp = (OWLObjectSomeValuesFrom) exists_exp_filler_exp_filler_conjunct;
											OWLClassExpression filler_conjunct_exp_filler = filler_conjunct_exp.getFiller();
											if(filler_conjunct_exp_filler instanceof OWLClass) {
												//create the existsEdge, where the source is role group and destination
												OWLClass fcef_cl = (OWLClass) filler_conjunct_exp_filler;
												//get the role name
												OWLObjectPropertyExpression pr = filler_conjunct_exp.getProperty();
												if(isDefined(fcef_cl, O)) {
													Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
													//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g, fcef_cl_def_v);
													return new ExistsEdge(fcef_cl_def_v, pr.toString() + "_" + index_role_g, role_group_vertex);
												}else if(!(isDefined(fcef_cl, O))) {
													NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
													return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g , fcef_cl_ndef_v);
												}
											}
											
										}
									}
								}
								//A subof rg some r some B
								 if(exists_exp_filler_exp_filler instanceof OWLClass) {
									OWLClass fcef_cl = (OWLClass) exists_exp_filler_exp_filler;
									//get the role name
									OWLObjectPropertyExpression pr = exists_exp_filler_exp.getProperty();
									if(isDefined(fcef_cl, O)) {
										Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
										//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g, fcef_cl_def_v);
										//return new ExistsEdge(role_group_vertex, pr.toString(), fcef_cl_def_v);
										return new ExistsEdge(fcef_cl_def_v, pr.toString(), role_group_vertex);
									}else if(!(isDefined(fcef_cl, O))) {
										NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
										//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g , fcef_cl_ndef_v);
										return new ExistsEdge(role_group_vertex, pr.toString(), fcef_cl_ndef_v);
									}
								}
								
								//return new ExistsEdge(owlSCOA_lhs_def, role_group_vertex);
								return new ExistsEdge(role_group_vertex, owlSCOA_lhs_def);
							}else if(!(isDefined(owlSCOA_lhs.asOWLClass(), O))) {
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								index_role_g++;
								String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_ndef.toString() + "-" + index_role_g;
								Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
								
								OWLClassExpression exists_exp_filler_exp_filler = exists_exp_filler_exp.getFiller();
								//check if it's conjunct set
								if(exists_exp_filler_exp_filler instanceof OWLObjectIntersectionOf) {
									Set<OWLClassExpression> exists_exp_filler_exp_filler_conjuncts = exists_exp_filler_exp_filler.asConjunctSet();
									for(OWLClassExpression exists_exp_filler_exp_filler_conjunct: exists_exp_filler_exp_filler_conjuncts) {
										if(exists_exp_filler_exp_filler_conjunct instanceof OWLObjectSomeValuesFrom) {
											//get the filler, which will be owl class (simple one) 
											//as in snomed ct there is no 3 nested role expressions, but recheck this!!!
											OWLObjectSomeValuesFrom filler_conjunct_exp = (OWLObjectSomeValuesFrom) exists_exp_filler_exp_filler_conjunct;
											OWLClassExpression filler_conjunct_exp_filler = filler_conjunct_exp.getFiller();
											if(filler_conjunct_exp_filler instanceof OWLClass) {
												//create the existsEdge, where the source is role group and destination
												OWLClass fcef_cl = (OWLClass) filler_conjunct_exp_filler;
												//get the role name
												OWLObjectPropertyExpression pr = filler_conjunct_exp.getProperty();
												if(isDefined(fcef_cl, O)) {
													Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
													//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g, );
													return new ExistsEdge(fcef_cl_def_v, pr.toString() + "_" + index_role_g, role_group_vertex);
												}else if(!(isDefined(fcef_cl, O))) {
													NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
													return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g , fcef_cl_ndef_v);
												}
											}
											
										}
									}
									//A subof rg some r some B
								}else if(exists_exp_filler_exp_filler instanceof OWLClass) {
									OWLClass fcef_cl = (OWLClass) exists_exp_filler_exp_filler;
									//get the role name
									OWLObjectPropertyExpression pr = exists_exp_filler_exp.getProperty();
									if(isDefined(fcef_cl, O)) {
										Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
										//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g, fcef_cl_def_v);
										//return new ExistsEdge(role_group_vertex, pr.toString(), fcef_cl_def_v);
										return new ExistsEdge(fcef_cl_def_v, pr.toString(), role_group_vertex);
									}else if(!(isDefined(fcef_cl, O))) {
										NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
										//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g , fcef_cl_ndef_v);
										return new ExistsEdge(role_group_vertex, pr.toString(), fcef_cl_ndef_v);
									}
								}
								
								//return new ExistsEdge(owlSCOA_lhs_ndef, role_group_vertex); 
								return new ExistsEdge(role_group_vertex, owlSCOA_lhs_ndef); 
							}
						
							
							
						}*///no nesting
						 if(exists_exp_filler instanceof OWLClass) {
							 
							//System.out.println("the filler is simple class: " + exists_exp_filler);
							OWLObjectPropertyExpression pr_exp = exists_exp.getProperty();
							
							String pr_exp_name = pr_exp.toString();
							pr_exp_name = pr_exp_name.substring(pr_exp_name.indexOf("<") + 1);
							pr_exp_name = pr_exp_name.substring(0, pr_exp_name.indexOf(">"));		
							pr_exp_name = pr_exp_name.trim();
							//System.out.println("the pr_exp is: " + pr_exp);
							OWLClass exists_exp_filler_cl = (OWLClass) exists_exp_filler;
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
								//System.out.println("the owlSCOA_lhs is defined: " + owlSCOA_lhs);
								//convert the cl filler to the correct vertex depending
								
								Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//owlSCOA_lhs_def.setName(owlSCOA_lhs.toString() + "-role-label: " + pr_exp);
								//System.out.println("the owlSCOA_lhs_def after name change is: " + owlSCOA_lhs_def);
								//System.out.println("the owlSCOA_lhs_def: " + owlSCOA_lhs_def);
								if(isDefined(exists_exp_filler_cl, O)) {
									//System.out.println("the exists_exp_filler_cl is defined: " + exists_exp_filler_cl);
									Def_Vertex exists_exp_filler_cl_def = getDefVertexFromClass(exists_exp_filler_cl);
									//set the name of exists_exp_filler_cl_def
									exists_exp_filler_cl_def.setName(exists_exp_filler_cl_def.toString() + "-role-label: " + pr_exp_name);
									//System.out.println("the exists_exp_filler_cl_def is: " + exists_exp_filler_cl_def);
									//OWLObjectPropertyExpression pr_exp = exists_exp.getProperty();
									//return new ExistsEdge(owlSCOA_lhs_def, pr_exp.toString(), exists_exp_filler_cl_def);
									return new ExistsEdge(exists_exp_filler_cl_def, pr_exp_name, owlSCOA_lhs_def);
								}else if(!(isDefined(exists_exp_filler_cl, O))) {
									//System.out.println("the exists_exp_filler_cl is not defined: " + exists_exp_filler_cl);
									NDef_Vertex exists_exp_filler_cl_v = getNDefVertexFromClass(exists_exp_filler_cl);
									exists_exp_filler_cl_v.setName(exists_exp_filler_cl_v.toString() + "-role-label: " + pr_exp_name);
									//System.out.println("the exists_exp_filler_cl_v is: " + exists_exp_filler_cl_v);
									//OWLObjectPropertyExpression pr_exp = exists_exp.getProperty();
									//return new ExistsEdge(owlSCOA_lhs_def, pr_exp.toString(), exists_exp_filler_cl_v);
									return new ExistsEdge(exists_exp_filler_cl_v, pr_exp_name, owlSCOA_lhs_def);
								}
							}else if(!(isDefined(owlSCOA_lhs.asOWLClass(), O))) {
								//System.out.println("the owlSCOA_lhs is not defined: " + owlSCOA_lhs);
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//owlSCOA_lhs_ndef.setName(owlSCOA_lhs_ndef.toString() + "-role-label: " + pr_exp);
								//System.out.println("the owlSCOA_lhs_ndef: " + owlSCOA_lhs_ndef);
								if(!(isDefined(exists_exp_filler_cl, O))) {
								NDef_Vertex exists_exp_filler_cl_v = getNDefVertexFromClass(exists_exp_filler_cl);
								exists_exp_filler_cl_v.setName(exists_exp_filler_cl_v + "-role-label: " + pr_exp_name);
								//System.out.println("the exists_exp_filler_cl_v is not defined: " + exists_exp_filler_cl_v);
								//return new ExistsEdge(owlSCOA_lhs_ndef, pr_exp.toString(), exists_exp_filler_cl_v);
								return new ExistsEdge(exists_exp_filler_cl_v, pr_exp_name, owlSCOA_lhs_ndef);
								}else if(isDefined(exists_exp_filler_cl, O)) {
									Def_Vertex exists_exp_filler_cl_def = getDefVertexFromClass(exists_exp_filler_cl);
									exists_exp_filler_cl_def.setName(exists_exp_filler_cl_def.toString() + "-role-label: " + pr_exp_name);
									//System.out.println("the exists_exp_filler_cl_def is defined: " + exists_exp_filler_cl_def);
									//return new ExistsEdge(owlSCOA_lhs_ndef, pr_exp.toString(), exists_exp_filler_cl_def);
									return new ExistsEdge(exists_exp_filler_cl_def, pr_exp_name, owlSCOA_lhs_ndef);
								}
								
							}
							
						}
						
					
					
					}
					
				}
				
				
			}
			return null;
		}
		
		
		int index_role_g = 0;
		public List<ExistsEdge> AxiomConverter_exists_is_a_nested(OWLLogicalAxiom axiom, OWLOntology O) {
			//list of the existsEdge
			//System.out.println("axiom conversion: AxiomConverter_exists_is_a_nested ");
			List<ExistsEdge> existsEdges = new ArrayList<>();
			
			
			if(axiom instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
				if(!owlSCOA.isGCI()) {
					OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
					OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
					//A subof rg some 
					if(owlSCOA_rhs instanceof OWLObjectSomeValuesFrom) {
						//System.out.println("The axiom rhs is exists");
						OWLObjectSomeValuesFrom exists_exp = (OWLObjectSomeValuesFrom) owlSCOA_rhs;
						OWLClassExpression exists_exp_filler = exists_exp.getFiller();
						//check if the filler is instanceof exists
						//A subof rg some r some
						if(exists_exp_filler instanceof OWLObjectSomeValuesFrom) {
							//System.out.println("the filler is nested exists: " + exists_exp_filler);
							OWLObjectSomeValuesFrom exists_exp_filler_exp = (OWLObjectSomeValuesFrom) exists_exp_filler;
							
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
								//System.out.println("the owlSCOA_lhs is defined: " + owlSCOA_lhs);
								Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								//add numbering to the role groups 
								//this number would be incremented with each exists_exp_filler that was found (meet the exists condition)
								index_role_g++;
								String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_def.toString() + "_i_" + index_role_g;
								Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
								
							
								OWLClassExpression exists_exp_filler_exp_filler = exists_exp_filler_exp.getFiller();
				
								//A subof rg some r some B
								 if(exists_exp_filler_exp_filler instanceof OWLClass) {
									 //System.out.println("the exists_exp_filler_exp_filler: " + exists_exp_filler_exp_filler);
									OWLClass fcef_cl = (OWLClass) exists_exp_filler_exp_filler;
									//get the role name
									OWLObjectPropertyExpression pr = exists_exp_filler_exp.getProperty();
									String pr_name = pr.toString();
									pr_name = pr_name.substring(pr_name.indexOf("<") + 1);
									pr_name = pr_name.substring(0, pr_name.indexOf(">"));		
									pr_name = pr_name.trim();
									if(isDefined(fcef_cl, O)) {
										//System.out.println("the fcef_cl is defined: " + fcef_cl);
										Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
										//fcef_cl_def_v.setName(fcef_cl_def_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" +  role_group_vertex_name);
										fcef_cl_def_v.setName(fcef_cl_def_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
										existsEdges.add(new ExistsEdge(fcef_cl_def_v, pr.toString(), role_group_vertex));
										
									}else if(!(isDefined(fcef_cl, O))) {
										//System.out.println("the fcef_cl is not defined: " + fcef_cl);
										NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
										
										//fcef_cl_ndef_v.setName(fcef_cl_ndef_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" +  role_group_vertex_name);
										fcef_cl_ndef_v.setName(fcef_cl_ndef_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
										//System.out.println("the fcef_cl_ndef_v that is not defined: " + fcef_cl_ndef_v);
										//return new ExistsEdge(role_group_vertex, pr.toString() + "_" + index_role_g , fcef_cl_ndef_v);
										//return new ExistsEdge(role_group_vertex, pr.toString(), fcef_cl_ndef_v);
										existsEdges.add(new ExistsEdge(fcef_cl_ndef_v, pr.toString(), role_group_vertex));
										//System.out.println("the edge was added to the existsEdges list " + existsEdges);
									}
								}
						
								existsEdges.add(new ExistsEdge(role_group_vertex, owlSCOA_lhs_def));
								//System.out.println("the existsEdges list so far: " + existsEdges);
							}else if(!(isDefined(owlSCOA_lhs.asOWLClass(), O))) {
								//System.out.println("the owlSCOA_lhs is not defined: " + owlSCOA_lhs);
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								index_role_g++;
								String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_ndef.toString() + "_i_" + index_role_g;
								Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
								
								OWLClassExpression exists_exp_filler_exp_filler = exists_exp_filler_exp.getFiller();
								
								if(exists_exp_filler_exp_filler instanceof OWLClass) {
									// System.out.println("the exists_exp_filler_exp_filler: " + exists_exp_filler_exp_filler);
									OWLClass fcef_cl = (OWLClass) exists_exp_filler_exp_filler;
									//get the role name
									OWLObjectPropertyExpression pr = exists_exp_filler_exp.getProperty();
									String pr_name = pr.toString();
									pr_name = pr_name.substring(pr_name.indexOf("<") + 1);
									pr_name = pr_name.substring(0, pr_name.indexOf(">"));		
									pr_name = pr_name.trim();
									if(isDefined(fcef_cl, O)) {
										//System.out.println("the fcef_cl is defined: " + fcef_cl);
										Def_Vertex fcef_cl_def_v = getDefVertexFromClass(fcef_cl);
										//fcef_cl_def_v.setName(fcef_cl_def_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" +  role_group_vertex_name);
										fcef_cl_def_v.setName(fcef_cl_def_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
										existsEdges.add(new ExistsEdge(fcef_cl_def_v, pr.toString(), role_group_vertex));
									}else if(!(isDefined(fcef_cl, O))) {
									//	System.out.println("the fcef_cl is defined: " + fcef_cl);
										NDef_Vertex fcef_cl_ndef_v = getNDefVertexFromClass(fcef_cl);
										//fcef_cl_ndef_v.setName(fcef_cl_ndef_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" +  role_group_vertex_name);
										fcef_cl_ndef_v.setName(fcef_cl_ndef_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
										existsEdges.add(new ExistsEdge(fcef_cl_ndef_v, pr.toString(), role_group_vertex));
									}
								}
								
								existsEdges.add(new ExistsEdge(role_group_vertex, owlSCOA_lhs_ndef));
							//	System.out.println("the existsEdges list so far: " + existsEdges);
							}
						}//no nesting
						//A subof rg some (conjuncts)
						else if(exists_exp_filler instanceof OWLObjectIntersectionOf) {
							
							if(isDefined(owlSCOA_lhs.asOWLClass(), O)) {
							
							Def_Vertex owlSCOA_lhs_def = getDefVertexFromClass(owlSCOA_lhs.asOWLClass());
							//add numbering to the role groups 
							//this number would be incremented with each exists_exp_filler that was found (meet the exists condition)
							index_role_g++;
							String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_def.toString() + "_i_" + index_role_g;
							Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
						
							Set<OWLClassExpression> exists_exp_filler_conjuncts = exists_exp_filler.asConjunctSet();
							for(OWLClassExpression e_e_f_conjunct: exists_exp_filler_conjuncts) {
								//the conjuncts in snomed ct of role group cannot be simple owl class
								if(e_e_f_conjunct instanceof OWLObjectSomeValuesFrom) {
									OWLObjectSomeValuesFrom e_e_f_conjunct_obsv = (OWLObjectSomeValuesFrom) e_e_f_conjunct;
									OWLClassExpression e_e_f_conjunct_obsv_filler = e_e_f_conjunct_obsv.getFiller();  
									//this filler is always simple class
									if(e_e_f_conjunct_obsv_filler instanceof OWLClass) {
										OWLClass cl_e_e_f_conjunct_obsv_filler = (OWLClass) e_e_f_conjunct_obsv_filler;
										//get the role name
										OWLObjectPropertyExpression pr = e_e_f_conjunct_obsv.getProperty();
										String pr_name = pr.toString();
										pr_name = pr_name.substring(pr_name.indexOf("<") + 1);
										pr_name = pr_name.substring(0, pr_name.indexOf(">"));		
										pr_name = pr_name.trim();
										if(isDefined(cl_e_e_f_conjunct_obsv_filler, O)) {
											Def_Vertex cl_e_e_f_conjunct_obsv_filler_v = getDefVertexFromClass(cl_e_e_f_conjunct_obsv_filler);
											//cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" +  role_group_vertex_name);
											cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
											existsEdges.add(new ExistsEdge(cl_e_e_f_conjunct_obsv_filler_v, pr.toString(), role_group_vertex));
										}else if(!isDefined(cl_e_e_f_conjunct_obsv_filler, O)) {
											NDef_Vertex cl_e_e_f_conjunct_obsv_filler_v = getNDefVertexFromClass(cl_e_e_f_conjunct_obsv_filler);
											//cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" + role_group_vertex_name);
											cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
											existsEdges.add(new ExistsEdge(cl_e_e_f_conjunct_obsv_filler_v, pr.toString(), role_group_vertex));
										}
									}
								}
							}
							
							existsEdges.add(new ExistsEdge(role_group_vertex, owlSCOA_lhs_def));
							}//end of lhs that is defined
							else if(!(isDefined(owlSCOA_lhs.asOWLClass(), O))) {
								NDef_Vertex owlSCOA_lhs_ndef = getNDefVertexFromClass(owlSCOA_lhs.asOWLClass());
								index_role_g++;
								String role_group_vertex_name = "role_g_"+ owlSCOA_lhs_ndef.toString() + "_i_" + index_role_g;
								Role_g_Vertex role_group_vertex = getRoleGroupVertex(role_group_vertex_name);
								Set<OWLClassExpression> exists_exp_filler_conjuncts = exists_exp_filler.asConjunctSet();
								for(OWLClassExpression e_e_f_conjunct: exists_exp_filler_conjuncts) {
									if(e_e_f_conjunct instanceof OWLObjectSomeValuesFrom) {
										OWLObjectSomeValuesFrom e_e_f_conjunct_obsv = (OWLObjectSomeValuesFrom) e_e_f_conjunct;
										OWLClassExpression e_e_f_conjunct_obsv_filler = e_e_f_conjunct_obsv.getFiller();
										if(e_e_f_conjunct_obsv_filler instanceof OWLClass) {
											OWLClass cl_e_e_f_conjunct_obsv_filler = (OWLClass) e_e_f_conjunct_obsv_filler;
											OWLObjectPropertyExpression pr = e_e_f_conjunct_obsv.getProperty();
											String pr_name = pr.toString();
											pr_name = pr_name.substring(pr_name.indexOf("<") + 1);
											pr_name = pr_name.substring(0, pr_name.indexOf(">"));		
											pr_name = pr_name.trim();
											if(isDefined(cl_e_e_f_conjunct_obsv_filler, O)) {
												Def_Vertex cl_e_e_f_conjunct_obsv_filler_v = getDefVertexFromClass(cl_e_e_f_conjunct_obsv_filler);
												//cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" + role_group_vertex_name);
												cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
												existsEdges.add(new ExistsEdge(cl_e_e_f_conjunct_obsv_filler_v, pr.toString(), role_group_vertex));
											}else if(!isDefined(cl_e_e_f_conjunct_obsv_filler, O)) {
												NDef_Vertex cl_e_e_f_conjunct_obsv_filler_v = getNDefVertexFromClass(cl_e_e_f_conjunct_obsv_filler);
												//cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g + "_" + role_group_vertex_name);
												cl_e_e_f_conjunct_obsv_filler_v.setName(cl_e_e_f_conjunct_obsv_filler_v.toString() + "-role-label: " + pr_name + "_i_" + index_role_g);
												existsEdges.add(new ExistsEdge(cl_e_e_f_conjunct_obsv_filler_v, pr.toString(), role_group_vertex));
											}
										}
									}
								}
								existsEdges.add(new ExistsEdge(role_group_vertex, owlSCOA_lhs_ndef));
							}
							
						}
					}
					
				}
				
				
			}
			//System.out.println("current returned list of edges: " + existsEdges);
			return existsEdges;
		}
		
		public NDef_Vertex getVertexFromProperty(OWLObjectProperty owlobjectproperty) {
			//return the new Vertex (a method implemented in the Vertex class)
			return new NDef_Vertex(owlobjectproperty.getIRI().toString());
		}
		//how to return the role vertex labelled with transitive/ 
		//initialise a list of roles that include all of the roles of the ontology 
		//then label the one that is transitive
		//the create a method that checks if the role is transitive or not
		//this check will be used while converting the edges of the role axioms
		
		/*//1- convert all of the properties in O to NDef vertices, this returns a list of role vertices
		public void fromObjectPropertyToRoleVertex(Set<OWLObjectProperty> properties_of_O) {
			//
		}*/
		
		//1- convert from owl transitive object property axiom to labelled role(add all of the transitive labelled roles to a list, then return that list)
		//this list will be initialised outside of the converter to fill it 
		//then inside of rest of role inclusions, we will check if the role is already in the list of transitive properties. 
		//then we want to convert the role vertex to transitive, by checking the owl axioms
		//then return (or update) the role vertex in the list (the initial list)
		
		
		
		//the method isTransitive takes as input the objectproperty (vertex)
		/*
		public boolean isTransitive(OWLAxiom owlaxiom) {
			//label the transitive property
			OWLObjectPropertyExpression pr;
		
			if(owlaxiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
				//convert the transitive role 
				OWLTransitiveObjectPropertyAxiom otobp = (OWLTransitiveObjectPropertyAxiom) owlaxiom;
			
				NDef_Vertex role_vertex_transitive = getVertexFromProperty(otobp.getProperty().asOWLObjectProperty());
				//then return 
				return role_vertex_transitive;
			}
			return null;
		}
		*/
		//add a inside condition with each condition that checks if the property is transitive or not.
		
		public Edge AxiomConverter_RoleInclusions(List<OWLObjectPropertyExpression> transitiveRoles, OWLAxiom owlaxiom) {
			//List<NDef_Vertex> transitive_roles = getTransitive_roles(owlAxiom);
			//System.out.println("the set of transitive roles epxressions inside axiom converter: " + transitiveRoles);
			if(owlaxiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {
				System.out.println("the axiom type is subobject property: ");
				OWLSubObjectPropertyOfAxiom pr_axiom = (OWLSubObjectPropertyOfAxiom) owlaxiom;
				OWLObjectPropertyExpression pr_lhs = pr_axiom.getSubProperty();
				
					NDef_Vertex pr_lhs_v = getVertexFromProperty(pr_lhs.asOWLObjectProperty());
					if(transitiveRoles.contains(pr_lhs)) {
					pr_lhs_v = new NDef_Vertex(pr_lhs.toString() + "-transitive");
					}
					
					
					OWLObjectPropertyExpression pr_rhs = pr_axiom.getSuperProperty();
					NDef_Vertex pr_rhs_v = getVertexFromProperty(pr_rhs.asOWLObjectProperty());
					if(transitiveRoles.contains(pr_lhs)) {
						pr_lhs_v = new NDef_Vertex(pr_rhs.toString() + "-transitive");
					}
			
					return new SimpleEdge(pr_rhs_v, pr_lhs_v);
			}
			//add that if owlaxiom is of type property chain
			if(owlaxiom.isOfType(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
				//System.out.println("the axiom type is property chain");
				//System.out.println("the set of transitive roles epxressions inside axiom converter: " + transitiveRoles);
				OWLSubPropertyChainOfAxiom property_chain_ax = (OWLSubPropertyChainOfAxiom) owlaxiom;
				List<OWLObjectPropertyExpression> properties_in_chain = property_chain_ax.getPropertyChain();
				//System.out.println("the properties in chain are: " + properties_in_chain);
				List<String> transitive_properties_names = new ArrayList<>();
				List<String> non_transitive_prs_names = new ArrayList<>();
				List<String> all_prs_names = new ArrayList<>();
				//System.out.println("the transitiveRoles is: " + transitiveRoles);
				for(OWLObjectPropertyExpression property: properties_in_chain) {
					//System.out.println("current property in chain is: " + property);
					non_transitive_prs_names.add(property.toString());
					if(transitiveRoles.contains(property)) {
						//System.out.println("the transitive roles contain the property in chain: " + transitiveRoles);						
						non_transitive_prs_names.remove(property.toString());
						transitive_properties_names.add(property.toString());
					}
				}
				
				
				for(String transitive_pr_name: transitive_properties_names) {
					String tr_pr_new_name = transitive_pr_name + "-transitive";
					all_prs_names.add(tr_pr_new_name);
				}
				
				all_prs_names.addAll(non_transitive_prs_names);
				
				//then get the super property to represent the rhs of the edge
				//we will create the vertex from the names of the properties in the list! (represent pr_lhs_v)
				NDef_Vertex pr_lhs_v = new NDef_Vertex(all_prs_names.toString());
				OWLObjectPropertyExpression pr_rhs = property_chain_ax.getSuperProperty();
				String pr_rhs_name = pr_rhs.toString() + "-ch";
				//NDef_Vertex pr_rhs_v = getVertexFromProperty(pr_rhs.asOWLObjectProperty());
				NDef_Vertex pr_rhs_v = new NDef_Vertex(pr_rhs_name);
				//System.out.println("the pr_lhs_v of role edge is: " + pr_lhs_v);
				//System.out.println("the pr_rhs_v of role edge is: " + pr_rhs_v);
				return new SimpleEdge(pr_rhs_v, pr_lhs_v);
			}
			
			return null;	
		}
		
		
		
	
	public boolean isDefined(OWLClass cl, OWLOntology ontology) {
		Set<OWLEquivalentClassesAxiom> cl_axioms = ontology.getEquivalentClassesAxioms(cl);
		if(!(cl_axioms.isEmpty())) {
			return true;
		}
		return false; 
	}
	
	
	/*public Edge AxiomConverter_opposite(OWLLogicalAxiom axiom) {
		if(axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
			//check if the owlSCOA has an atomic rhs, then continue the conversion, other wise 
			//get rhs
		
			OWLClassExpression owlSCOA_rhs = owlSCOA.getSuperClass();
			Vertex owlSCOA_rhs_vertex = getVertexFromClass(owlSCOA_rhs.asOWLClass());
			if(owlSCOA_rhs instanceof OWLClass) {
				OWLClassExpression owlSCOA_lhs = owlSCOA.getSubClass();
				Vertex owlSCOA_lhs_vertex = getVertexFromClass(owlSCOA_lhs.asOWLClass());
				//get the vertiexs from the classes? 
				//create vertices from the owl classes
				//return new Edge(owlSCOA_lhs_vertex, owlSCOA_rhs_vertex);
				return new Edge(owlSCOA_rhs_vertex, owlSCOA_lhs_vertex);
			}
		}else if(axiom instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom equiv = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> axiom_subofs = equiv.asOWLSubClassOfAxioms();
			for(OWLSubClassOfAxiom axiom_subof: axiom_subofs) {
				if(!(axiom_subof.isGCI())) {
					OWLClassExpression owlSCOA_rhs = axiom_subof.getSuperClass();
					Vertex owlSCOA_rhs_vertex = getVertexFromClass(owlSCOA_rhs.asOWLClass());
					if(owlSCOA_rhs instanceof OWLClass) {
						OWLClassExpression owlSCOA_lhs = axiom_subof.getSubClass();
						Vertex owlSCOA_lhs_vertex = getVertexFromClass(owlSCOA_lhs.asOWLClass());
						//get the vertiexs from the classes? 
						//create vertices from the owl classes
						//return new Edge(owlSCOA_lhs_vertex, owlSCOA_rhs_vertex);
						return new Edge(owlSCOA_rhs_vertex, owlSCOA_lhs_vertex);
					}
				}
			}
		}
		return null;
	}*/
	
	public List<Edge> AxiomsConverter(Set<OWLLogicalAxiom> axioms, OWLOntology O) {
		List<Edge> list_edges = new ArrayList<>();
		
		//List<OWLObjectPropertyExpression> transitive_roles_exps = new ArrayList<>();
		for(OWLLogicalAxiom axiom: axioms) {
			//System.out.println("current axiom: " + axiom);
			//list_edges.add(AxiomConverter(axiom, O));
			list_edges.add(AxiomConverter_simple_is_a(axiom, O));
			//also add to the list_edges the exists edges
			list_edges.add(AxiomConverter_exists_is_a(axiom, O));
			list_edges.addAll(AxiomConverter_exists_is_a_nested(axiom,O));
			//System.out.println("current list_edges: " + list_edges);
			
			//check if O contains transitiv
			
			/*if(axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
				OWLTransitiveObjectPropertyAxiom otobp = (OWLTransitiveObjectPropertyAxiom) axiom;
				OWLObjectPropertyExpression pr_exp = otobp.getProperty();
				//NDef_Vertex pr_lhs_v = getVertexFromProperty(pr_exp.asOWLObjectProperty());
				transitive_roles_exps.add(pr_exp);
			}
			list_edges.add(AxiomConverter_RoleInclusions(transitive_roles_exps, axiom));*/
		}
		return list_edges;
	}
	
	
	/*public List<Edge> AxiomsConverter_opposite(Set<OWLLogicalAxiom> axioms) {
		List<Edge> list_edges = new ArrayList<>();
		for(OWLLogicalAxiom axiom: axioms) {
			list_edges.add(AxiomConverter_opposite(axiom));
		}
		return list_edges;
	}*/
	
	/*//create ontology converter method, the ontologyconverter call the axiom converter to get list of edges, 
	 * then from the list of edges, 
	 * form the adj list (to return a Graph) type?
	 * 
	public Graph ontologyConverter() {
		//how to get a graph from the edges
	}*/
	
	//method that returns adjacency list from list of edges
	public Map<Vertex, List<Vertex>> buildAdjacencyList(List<Edge> edges, List<Vertex> o_vertices) {
		Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
		//get all the vertices? from the edges..?
		for(Vertex vertex: o_vertices) {
			adjVertices.put(vertex, new ArrayList<>());
		}
		
		for (Map.Entry<Vertex, List<Vertex>> entry : adjVertices.entrySet()) {
			List<Vertex> sources_neighbours = new ArrayList<>();
		      for(Edge edge: edges) {
		    	  System.out.println("current edge is: " + edge);
		    	  	//if(edge.getSource().equals(entry.getKey())) {
		    	  if(entry.getKey().toString().equals(edge.getSource().toString())) {
		    	  		//System.out.println("current source in the map: " + entry.getKey());
		    	  		//System.out.println("current matched sources: " + edge.getSource());
		    	  		//System.out.println("current edge destination: " + edge.getDestination());
		    	  		sources_neighbours.add(edge.getDestination());
		    	  		
		    	  	}
		      }
		     // System.out.println("sources_neighbours: for the above source " + sources_neighbours);
	    	  	  entry.setValue(sources_neighbours);
		    }
		return adjVertices;
	}
	
	
	
	
	public Map<Vertex, List<Vertex>> buildAdjacencyList_def_pr(List<Edge> edges, List<Vertex> o_vertices) {
		Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
		//get all the vertices? from the edges..?
		
		
		
		for(Vertex vertex: o_vertices) {
			adjVertices.put(vertex, new ArrayList<>());
		}
		
		for (Map.Entry<Vertex, List<Vertex>> entry : adjVertices.entrySet()) {
			List<Vertex> sources_neighbours = new ArrayList<>();
		      for(Edge edge: edges) {
		    	 // System.out.println("current edge is: " + edge);
		    	  	//if(edge.getSource().equals(entry.getKey())) {
		    	  if(entry.getKey().toString().equals(edge.getGSource().toString())) {
		    	  	//	System.out.println("current source in the map: " + entry.getKey());
		    	  	//	System.out.println("current matched sources: " + edge.getGSource());
		    	  	//	System.out.println("current edge destination: " + edge.getGDestination());
		    	  		sources_neighbours.add(edge.getGDestination());
		    	  		
		    	  	}
		      }
		  //    System.out.println("sources_neighbours: for the above source " + sources_neighbours);
	    	  	  entry.setValue(sources_neighbours);
		    }
		return adjVertices;
	}
	
	
	
	public Map<Vertex, List<Vertex>> buildAdjacencyList_child_parent(List<Edge> edges, List<Vertex> o_vertices) {
		Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
		//get all the vertices? from the edges..?
		
		
		
		for(Vertex vertex: o_vertices) {
			adjVertices.put(vertex, new ArrayList<>());
		}
		
		for (Map.Entry<Vertex, List<Vertex>> entry : adjVertices.entrySet()) {
			List<Vertex> sources_neighbours = new ArrayList<>();
		      for(Edge edge: edges) {
		    //	  System.out.println("current edge is: " + edge);
		    	  //if(edge.getSource().equals(entry.getKey())) {
		    	 String edited_edge_dist_name = edge.getGDestination().toString();
		    	  //String edited_edge_source_name = edge.getGSource().toString();
		    	  if(edge.toString().contains("-role-label:")) {
		    		  
		    	  //String edge_dist_name = edge.getGDestination().toString();
		    	//  System.out.println("The edge distination name: " + edited_edge_dist_name);
		    	  //System.out.println("the edge name: " + edge_dist_name);
		    	  //edge_dist_name = edge_dist_name.substring(edge_dist_name.indexOf("label:") + 1);
		    	  String[]edge_dist_name_splitted = edited_edge_dist_name.split("-role-label:");
		 
		   // 	  System.out.println("split the edge_dist_name: " + edge_dist_name_splitted[0]);
		    	  edited_edge_dist_name = edge_dist_name_splitted[0];
		    	  }
		    	  //if(entry.getKey().toString().equals(edge.getGDestination().toString())) {
		    	  if(entry.getKey().toString().equals(edited_edge_dist_name)) {
		    	  		//System.out.println("current source in the map: " + entry.getKey());
		    	  		//System.out.println("current matched sources: " + edge.getGSource());
		    	  		//System.out.println("current edge destination: " + edge.getGDestination());
		    	  		sources_neighbours.add(edge.getGSource());
		    	  //		System.out.println("the source of current edge: " + edge.getGSource());
		    	  		
		    	  	}
		      }
		      //System.out.println("sources_neighbours: for the above source " + sources_neighbours);
	    	  	  entry.setValue(sources_neighbours);
		    }
		return adjVertices;
	}
	
	
	public Map<Vertex, List<Vertex>> buildAdjacencyList_child_parent_for_nested(List<Edge> edges, List<Vertex> o_vertices) {
		Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
		//get all the vertices? from the edges..?
		
		
		
		for(Vertex vertex: o_vertices) {
			adjVertices.put(vertex, new ArrayList<>());
			
		}
		
		for(Edge edge: edges) {
			Vertex source = edge.getGSource();
			String source_name = source.toString();
			Vertex destinstion = edge.getGDestination();
			String destinstion_name = destinstion.toString();
			String source_name_edit = source.toString();
			String destinstion_name_edit = destinstion.toString();
			if(source_name.contains("-role-label:")){
				String[]source_name_splitted = source_name.split("-role-label:");
				source_name_edit = source_name_splitted[0];
			//	System.out.println("current source_name_edit: "+ source_name_edit);
			}
			if(destinstion_name.contains("-role-label:")) {
				String[]destination_name_splitted = destinstion_name.split("-role-label:");
				destinstion_name_edit = destination_name_splitted[0];
			}
			if(!adjVertices.keySet().toString().contains(source_name_edit)) {
				adjVertices.put(source, new ArrayList<>());
			}if(!adjVertices.keySet().toString().contains(destinstion_name_edit)) {
				adjVertices.put(destinstion, new ArrayList<>());
			}
		}
		
		//System.out.println("the map adjVertices after filling: "+ adjVertices);
		
		for (Map.Entry<Vertex, List<Vertex>> entry : adjVertices.entrySet()) {
			List<Vertex> sources_neighbours = new ArrayList<>();
			//System.out.println("current entry key: " + entry.getKey());
		      for(Edge edge: edges) {
		    	  //System.out.println("current edge is: " + edge);
		    	 
		    	 String edited_edge_dist_name = edge.getGDestination().toString();
		   
		    	  if(edge.toString().contains("-role-label:")) {
		    		  
		    		  if(edge.toString().contains("_i_")){
		    			  //String edge_source_name = edge.getGSource().toString();
		    			  //System.out.println("The indexed edge source: " + edge_source_name);
		    			  //split the name of the edge source to get the vertex name as concept only
		    			  
		    		  }
		    		
		    		  //System.out.println("The edge distination name: " + edited_edge_dist_name);
		    		  String[]edge_dist_name_splitted = edited_edge_dist_name.split("-role-label:");
		    		  //System.out.println("split the edge_dist_name: " + edge_dist_name_splitted[0]);
		    		  edited_edge_dist_name = edge_dist_name_splitted[0];
		    	  	}
		    	  
		    	  	if(edited_edge_dist_name.contains("role_g")) {
		    	  		//System.out.println("the role_g vertex: " + edge.getGDestination());
		    	  		//String[]edge_dist_name_splitted = edited_edge_dist_name.split("role_g_");
		    	  		//System.out.println("split the edge_dist_name (source of role g): " + edge_dist_name_splitted[1]);
		    	  	}
		    	 
		    	  if(entry.getKey().toString().equals(edited_edge_dist_name)) {
		    		  
		    		 
		    	  		sources_neighbours.add(edge.getGSource());
		    	  		//System.out.println("the source of current edge: " + edge.getGSource());
		    	  		
		    	  	}
		    	  
		    	  String edge_source_indexed_vertex_name_no_role = "";
		    	  if(entry.getKey() instanceof Role_g_Vertex) {
	    	  			String role_group_key = entry.getKey().toString();
	    	  			String[] role_group_key_splitted = role_group_key.split("_i_");
	    	  			String role_group_index = role_group_key_splitted[1];
	    	  			
	    	  		//	System.out.println("the current key is of type role group: " + entry.getKey());
	    	  		//	System.out.println("the current key role_group_index: " + role_group_index);
	    	  			String edge_source_name = edge.getGSource().toString();
	    	  			if(edge_source_name.contains("_i_")) {
	    	  			//	System.out.println("the indexed source name: " + edge_source_name);
	    	  				String[] edge_source_indexed_split = edge_source_name.split("_i_");
	    	  				String edge_source_indexed_vertex_name = edge_source_indexed_split[0];
	    	  			//	System.out.println("the edge_source_indexed_vertex_name: " + edge_source_indexed_vertex_name);
	    	  				if(edge_source_indexed_vertex_name.contains("-role-label:")) {
	    	  					String[] edge_source_indexed_vertex_name_no_role_s = edge_source_indexed_vertex_name.split("-role-label:");
	    	  					edge_source_indexed_vertex_name_no_role = edge_source_indexed_vertex_name_no_role_s[0];
	    	  					//System.out.println("the edge_source_indexed_vertex_name_no_role: " + edge_source_indexed_vertex_name_no_role);
		    	  				String edge_source_indexed = edge_source_indexed_split[1];
		    	  		//		System.out.println("the edge_source_indexed: " + edge_source_indexed);
		    	  				if(role_group_index.equals(edge_source_indexed)) {
		    	  				//	System.out.println("the indexed source equals to the role group dist index");
		    	  				}
	    	  				}
	    	  				
	    	  			}
	    	  			//System.out.println("the edge_source_indexed_vertex_name_no_role: " + edge_source_indexed_vertex_name_no_role);
	    	  			///System.out.println("current entry key after cleaning the edge source vertex: " + entry.getKey());
	    	  			
	    	  			
	    	  		}
		    	  
		    	  if(entry.getKey().toString().equals(edge_source_indexed_vertex_name_no_role)) {
	  					//System.out.println("edge_source_indexed_vertex_name_no_role equals to current entry key: ");
	  					//sources_neighbours.add(entry.);
	  					}
		    	  		
		    	  
		      }
		      //System.out.println("sources_neighbours: for the above source " + sources_neighbours);
	    	  	  entry.setValue(sources_neighbours);
		    }
		return adjVertices;
	}
	
	
	
	public Map<Vertex, List<Vertex>> buildAdjacencyList_child_parent_for_nested_2(List<Edge> edges, List<Vertex> o_vertices) {
		Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
		//get all the vertices? from the edges..?
		
		
		
		for(Vertex vertex: o_vertices) {
			adjVertices.put(vertex, new ArrayList<>());
			
		}
		
		for(Edge edge: edges) {
			Vertex source = edge.getGSource();
			String source_name = source.toString();
			Vertex destinstion = edge.getGDestination();
			String destinstion_name = destinstion.toString();
			String source_name_edit = source.toString();
			String destinstion_name_edit = destinstion.toString();
			if(source_name.contains("-role-label:")){
				String[]source_name_splitted = source_name.split("-role-label:");
				source_name_edit = source_name_splitted[0];
				//System.out.println("current source_name_edit: "+ source_name_edit);
			}
			if(destinstion_name.contains("-role-label:")) {
				String[]destination_name_splitted = destinstion_name.split("-role-label:");
				destinstion_name_edit = destination_name_splitted[0];
			}
			if(!adjVertices.keySet().toString().contains(source_name_edit)) {
				adjVertices.put(source, new ArrayList<>());
			}if(!adjVertices.keySet().toString().contains(destinstion_name_edit)) {
				adjVertices.put(destinstion, new ArrayList<>());
			}
		}
		
		//System.out.println("the map adjVertices after filling: "+ adjVertices);
		//System.out.println("the list of edges: " + edges);
		
		for (Map.Entry<Vertex, List<Vertex>> entry : adjVertices.entrySet()) {
			List<Vertex> sources_neighbours = new ArrayList<>();
			//System.out.println("current entry key: " + entry.getKey());
		      for(Edge edge: edges) {
		    	  //System.out.println("current edge is: " + edge);
		    	 
		    	 String edited_edge_dist_name = edge.getGDestination().toString();
		    	// System.out.println("the edge dist name: " + edited_edge_dist_name);
		    	  if(edge.toString().contains("-role-label:")) {
		    		    
		    		  //System.out.println("The edge distination name: " + edited_edge_dist_name);
		    		  String[]edge_dist_name_splitted = edited_edge_dist_name.split("-role-label:");
		    		  //System.out.println("split the edge_dist_name: " + edge_dist_name_splitted[0]);
		    		  edited_edge_dist_name = edge_dist_name_splitted[0];
		    	  	}
		    	  if(edge.getGSource() instanceof Role_g_Vertex) {
		    		 // System.out.println("the edge source is role group: "  + edge.getGSource());
		    	  }
		    	  if(entry.getKey().toString().equals(edited_edge_dist_name)) {
		    		 // System.out.println("The entry key is equal to edge distination name: " + edited_edge_dist_name);
		    		 // System.out.println("the source of the edge to add is: " + edge.getSource());
		    	  		sources_neighbours.add(edge.getGSource());
		    	  		//System.out.println("The sources_neighbourse: " + sources_neighbours);
		    	  	}
		    	  		    	  
		      }
	    	  	  entry.setValue(sources_neighbours);
		    }
		return adjVertices;
	}
	
	public Map<Vertex, List<Vertex>> ontologyConverter(OWLOntology ontology) {
		Normaliser n = new Normaliser();
		OWLOntology O_normalised = null;
		try {
			O_normalised = n.normalise(ontology);
			//System.out.println("ontology normalisation is done!");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Edge> edges = AxiomsConverter(O_normalised.getLogicalAxioms(), ontology);
		//System.out.println("axiom conversion is done!");
		//clean the edges list
		List<Edge> edges_clean = new ArrayList<>(edges);
		for(Edge edge: edges) {
			if(edge == null) {
			edges_clean.remove(edge);}
		}
		List<Vertex> o_vertices = getConceptsInSignature_def_n(ontology);
		//System.out.println("the o_vertices: " + o_vertices);
		//when calling getConceptsInsginature it must be called with original ontology (not normalised)
		//Map<Vertex, List<Vertex>> adjList = buildAdjacencyList(edges_clean, o_vertices);
		//Map<Vertex, List<Vertex>> adjList = buildAdjacencyList_def_pr(edges_clean, o_vertices);
		//Map<Vertex, List<Vertex>> adjList = buildAdjacencyList_child_parent(edges_clean, o_vertices);
		//Map<Vertex, List<Vertex>> adjList = buildAdjacencyList_child_parent_for_nested(edges_clean, o_vertices);
		Map<Vertex, List<Vertex>> adjList = buildAdjacencyList_child_parent_for_nested_2(edges_clean, o_vertices);
		//System.out.println("the edges list is: " + edges_clean);
		//System.out.println("adjList computation is done!");
		//Graph g = new Graph();
		//g.setAdjVertices(adjList);
		//System.out.println("setting graph object is done!");
		return adjList;
	}
	
	

}
