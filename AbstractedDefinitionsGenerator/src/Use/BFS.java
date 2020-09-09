/**
Author: Ghadah Alghamdi
*/

package Use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import Graph.Def_Vertex;
import Graph.Edge;
import Graph.NDef_Vertex;
import Graph.Vertex;
import Converter.ToOWL;

//this i version of bfs method doesn't yet support the property chains, transitive/reflexive properties and the GCI axioms
public class BFS{
	
	private final Graph graph;
	public BFS(Graph g) {
		this.graph = g;
	}
	Boolean endTherecursion = false;
	//change the name of this method BFS_adjmap_4
	
	
	public OWLEquivalentClassesAxiom get_abstract_def() {
		
		Map<Vertex, List<Vertex>> adjacency_list_map = graph.getAdjListMap();
		System.out.println("adjacency_list_map " + adjacency_list_map);
		Vertex starting_vertex = graph.getVertexLhs();
		
		BFS_1(starting_vertex, adjacency_list_map);
		List<Vertex> primitive_vertices = graph.getPrimitiveVertices();
		List<Vertex> existential_vertices = graph.getExistentialVertices();
        
        List<Vertex> primitive_concepts_no_redundancies = BFS_2_primitive_vertices(primitive_vertices, adjacency_list_map);
		List<Vertex> exist_restrictions_no_redundancies = remove_redundant_exist_restrictions(existential_vertices, adjacency_list_map);
		return getOWLDefinition(starting_vertex, primitive_concepts_no_redundancies, exist_restrictions_no_redundancies);
		}
	
	//Get adjacent vertices to primitive concepts
	public OWLSubClassOfAxiom get_subof_ax() {
		Map<Vertex, List<Vertex>> adjacency_list_map = graph.getAdjListMap();
		Vertex starting_vertex = graph.getVertexLhs();
		List<Vertex> adjacent_vertices = get_immediate_adjacent_vertices(starting_vertex, adjacency_list_map);
		System.out.println("the adjacent_vertices: " + adjacent_vertices);
		return getOWLSubClassOfAxiom(starting_vertex, adjacent_vertices);
	}
	
	
	
	//get parents (adjacent vertices)
	public List<Vertex> get_immediate_adjacent_vertices(Vertex starting_vertex, Map<Vertex, List<Vertex>> adjacency_list_map){
		Vertex lhs_v = starting_vertex;
		Vertex DV = lhs_v;
		for(Vertex v: adjacency_list_map.keySet()) {
			//System.out.println("0 current key vertex inside map of adj. lists: " + v);
			if(v.toString().equals(lhs_v.toString())) {
				//System.out.println("the current vertex is equal to input: " + v);
				DV = v;
			}
			
		}
		List<Vertex> adjacent_vertices = adjacency_list_map.get(DV);
		return adjacent_vertices;
	}
	
	//BFS_1 takes only one vertex (the starting vertex to begin the search for the primitive concepts and existential restrictions)
		//BFS_2 takes list of vertices to remove the subsumption relations between primitive vertices and existential vertices
		public void BFS_1(Vertex starting_vertex, Map<Vertex, List<Vertex>> adjacency_list_map) {
			//Vertex lhs_v = graph.getVertexLhs();
			Vertex lhs_v = starting_vertex;
			Vertex DV = lhs_v;
			for(Vertex v: adjacency_list_map.keySet()) {
				if(v.toString().equals(lhs_v.toString())) {
					System.out.println("the current vertex is equal to input: " + v);
					DV = v;
				}
				
			}
			
			List<Vertex> existential_vertices = new ArrayList<>();
			List<Vertex> primitive_vertices = new ArrayList<>();
			Map<Vertex, Boolean> visited= new HashMap<>();
			
			for(Vertex key_vertex: adjacency_list_map.keySet()) {
				visited.put(key_vertex, false);
			}
			
			LinkedList<Vertex> queue = new LinkedList<Vertex>();
			visited.replace(DV, true);
			queue.add(DV);
			
			while (queue.size() != 0) { 
				/*Set<Vertex> check_queue_defined = new HashSet<>();
				Set<Vertex> check_queue_notdefined = new HashSet<>();
				Set<Vertex> check_queue_role_group_v = new HashSet<>();
				for(Vertex v_queue: queue) {
					if(v_queue instanceof Def_Vertex) {
						check_queue_defined.add(v_queue);
					}
					else if(v_queue instanceof NDef_Vertex) {
						check_queue_notdefined.add(v_queue);
					}
					else if(v_queue instanceof Role_g_Vertex) {
						check_queue_role_group_v.add(v_queue);
					}
					}
				
				if(check_queue_defined.isEmpty() && check_queue_role_group_v.isEmpty()) {
					break;
				}*/
				
	            DV = queue.poll(); 
	        		List<Vertex> adj_vertices = adjacency_list_map.get(DV);
	        			
	        			if(adj_vertices != null) {
	        			Iterator<Vertex> it = adj_vertices.listIterator();
	        			while(it.hasNext()) { 
	        				Vertex v = it.next();        				
	        				if(v.toString().contains("-role-label")) {
	        					existential_vertices.add(v);
	        				}        	   
	        				for(Vertex key_v: visited.keySet()) {
	        					if(key_v.toString().equals(v.toString())) {
	        						if(!visited.get(key_v)) {
	        							if(key_v instanceof NDef_Vertex) {
	        								primitive_vertices.add(key_v);
		        						}
	        							visited.replace(key_v, true);
	        							queue.add(key_v);
	        							}
	        						}
	        					}
	        				}
	        			}
				}
			
	        //set the primitive parents with possible redundancies?
	        //then get them inside BFS_2
			System.out.println("the primitive_vertices inside BFS_1: " + primitive_vertices );
	        graph.setPrimitiveVertices(primitive_vertices);
	        graph.setExistentialVertices(existential_vertices);
	        
		}
		
	
	//method to remove redundant primitive concepts and redundant existential_restrictions through second BFS round
	//create internal methods:
		//remove_redundant_primitive_concepts
		//remove redundant existential restrictions
	
	

	public List<Vertex> BFS_2_primitive_vertices(List<Vertex> primitive_parents, Map<Vertex, List<Vertex>> adjacency_list_map){
		
		List<Vertex> primitives_parents_clean = new ArrayList<>(primitive_parents);
		List<Vertex> primitives_parents_founded = new ArrayList<>();
		System.out.println("the primitive_parents: " + primitive_parents);
		System.out.println("the adjacency_list_map: inside bfs 2 primitives: " + adjacency_list_map);
		for(Vertex primitive: primitive_parents) {

			System.out.println("current primitive to perform BFS: " + primitive);

			Map<Vertex, Boolean> visited= new HashMap<>();
					LinkedList<Vertex> queue = new LinkedList<Vertex>();
					for(Vertex key_vertex: adjacency_list_map.keySet()) {
						visited.put(key_vertex, false);
					}
			Vertex primitive_to_queue = primitive;
			visited.replace(primitive_to_queue, true);
			queue.add(primitive_to_queue);
			 while (queue.size() != 0) 
				        { 
				        primitive_to_queue = queue.poll(); 
			if(primitive_to_queue instanceof Def_Vertex) {
				        	System.out.println("The current dequeued vertex is defined");
				        }else if(primitive_to_queue instanceof NDef_Vertex) {
				        	System.out.println("The current dequeued vertex is primitive");
				        }
			System.out.println("current primitive that was dequeued: " + primitive_to_queue);
				        List<Vertex> adj_vertices = adjacency_list_map.get(primitive_to_queue);
			System.out.println("the adj_vertices to the current primitive to clean: " + adj_vertices);
				        Iterator<Vertex> it = adj_vertices.listIterator();
			while(it.hasNext()) {
							Vertex v = it.next();
							System.out.println("current v inside clean primitives and constraints: (inside checkin parents of primitives) " + v);
							for(Vertex key_v: visited.keySet()) {
								if(key_v.toString().equals(v.toString())) {
									System.out.println("key_v is equal to v inside checkin parents of primitives");
									if(key_v instanceof NDef_Vertex) {
										System.out.println("the key_v to be added to the founded primitives: " + key_v);
										primitives_parents_founded.add(key_v);
									}
									
									if(!visited.get(key_v)) {
										visited.replace(key_v, true);
										queue.add(key_v);
										}
									}
							}
						}

			}
		
		}
		
		System.out.println("primitives_parents_founded: " + primitives_parents_founded);
	
	for(Vertex v_pr: primitives_parents_founded) {
		if(primitive_parents.contains(v_pr)) {
			primitives_parents_clean.remove(v_pr);
		}
	}
	System.out.println("primitives_parents_clean: " + primitives_parents_clean);
	//bfs_cl.setClosestPrimtives(primitives_parents_clean);
	Graph graph = new Graph();
	graph.setProximalPrimitiveVertices(primitives_parents_clean);
	return primitives_parents_clean; 
	}
	
	
	
	public List<Vertex> remove_redundant_exist_restrictions(List<Vertex> existential_vertices, Map<Vertex, List<Vertex>> adjacency_list_map){
		
		
		Set<Vertex> existential_vertices_no_roles = new HashSet<>();
		for(Vertex vrp : existential_vertices) {
			String vrp_string = vrp.toString();
			String vrp_name = "";
			if(vrp_string.contains("_i_")) {
				vrp_name = get_vertex_name_type_rg(vrp);
			}else {
				vrp_name = get_vertex_name_type_simple(vrp);
			}
			for(Vertex key_adj_list : adjacency_list_map.keySet()) {
				String key_adj_list_name = key_adj_list.toString();
				if(key_adj_list_name.equals(vrp_name)) {
					existential_vertices_no_roles.add(key_adj_list);
				}
			}
			
		}
		//get subsumption relations between fillers of restrictions
		//(1)Checking if the fillers of existential restrictions have subsumption relations between them
		Map<Vertex, List<Vertex>> fillers_subsumptions = BFS_2_existential_vertices(existential_vertices_no_roles, adjacency_list_map);
		//(2)Use result of (1) to check if the existential restrictions have subsumption relations between them
		Map<Vertex, List<Vertex>> retrictions_subsumptions = get_subsumption_relations_restrictions(fillers_subsumptions, existential_vertices);
		//(3-1)Use result of (2) to figure redundant restrictions between simple existential restrictions (r some B)
		List<Vertex> simple_restrictions_no_redundancies = get_simple_types_restrictions_no_redundancies(retrictions_subsumptions, existential_vertices);
		//(3-2)Use result of (3) to figure redundant restrictions between nested existential restrictions (r some (r some B ...))
		List<Vertex> nested_restrictions_no_redundancies = get_nested_types_restrictions_no_redundancies(retrictions_subsumptions, existential_vertices);
		
		List<Vertex> restrictions_no_redundancies = new ArrayList<>();
		restrictions_no_redundancies.addAll(simple_restrictions_no_redundancies);
		restrictions_no_redundancies.addAll(nested_restrictions_no_redundancies);
		
		return restrictions_no_redundancies;
		
		
	}
	
	public Map<Vertex, List<Vertex>> BFS_2_existential_vertices(Set<Vertex> existential_vertices_no_roles, Map<Vertex, List<Vertex>> adj_list){
		Map<Vertex, List<Vertex>> fillers_subsumptions = new HashMap<>();
		for(Vertex restriction_filler: existential_vertices_no_roles) {
			List<Vertex> adj_constraints = new ArrayList<>();
			
			Map<Vertex, Boolean> visited_2= new HashMap<>();
			LinkedList<Vertex> queue_2 = new LinkedList<Vertex>();
			for(Vertex key_vertex: adj_list.keySet()) {
				visited_2.put(key_vertex, false);
			}
	
			Vertex constraint_to_queue = restriction_filler;
			visited_2.replace(constraint_to_queue, true);
	        queue_2.add(constraint_to_queue);
	        while (queue_2.size() != 0) 
	        { 
	        	constraint_to_queue = queue_2.poll(); 
	 
	        List<Vertex> adj_vertices = adj_list.get(constraint_to_queue);
	 
	        Iterator<Vertex> it = adj_vertices.listIterator();
	        adj_constraints.addAll(adj_vertices);
			while(it.hasNext()) {
				Vertex v = it.next();
				for(Vertex key_v: visited_2.keySet()) {
					if(key_v.toString().equals(v.toString())) {
						if(!visited_2.get(key_v)) {
							visited_2.replace(key_v, true);
							queue_2.add(key_v);
							}
						}
				}
			}
	        }
	        
	        fillers_subsumptions.put(restriction_filler, adj_constraints);
		}
		
		return fillers_subsumptions;
	}
	//get true parents
	public Map<Vertex, List<Vertex>> get_subsumption_relations_restrictions(Map<Vertex, List<Vertex>> fillers_subsumptions, List<Vertex> existential_vertices){
		Map<Vertex, List<Vertex>> fillers_subsumptions_2 = new HashMap<>();
		for(Vertex vrp: existential_vertices) {
			fillers_subsumptions_2.put(vrp, new ArrayList<>());
		}
		
		for(Map.Entry<Vertex, List<Vertex>> filler_sub_entry: fillers_subsumptions_2.entrySet()) {
			Vertex key = filler_sub_entry.getKey();
			String key_name_no_role = "";
			if(key.toString().contains("_i_")) {
				key_name_no_role = get_vertex_name_type_rg(key);
				}
			else {
				key_name_no_role = get_vertex_name_type_simple(key);
				}
				for(Vertex key_v: fillers_subsumptions.keySet()) {
					String key_v_name = key_v.toString();
					if(key_v_name.equals(key_name_no_role)) {
						List<Vertex> parents = fillers_subsumptions.get(key_v);
						filler_sub_entry.setValue(parents);
					}
				}
			}
		
		Map<Vertex, List<Vertex>> restrictions_subsumption_relations = new HashMap<>();
		
		//get the role edges from BFS class
		//Graph graph = new Graph();
		List<Edge> role_edges = graph.getRoleEdges();
		for(Map.Entry<Vertex, List<Vertex>> filler_sub_entry: fillers_subsumptions_2.entrySet()) {
			Vertex key = filler_sub_entry.getKey();
			String key_name = key.toString();
			String[] key_name_s = key_name.split("_i_");
			String key_name_w_role = key_name_s[0];
			String[] key_name_w_role_s = key_name_w_role.split("-role-label: ");
			String key_role_name = key_name_w_role_s[1]; 
			key_name = key_name_w_role_s[0];
			List<Vertex> list_of_parents = filler_sub_entry.getValue();
			List<Vertex> true_parents = new ArrayList<>();
			for(Vertex v_parent: list_of_parents) {
				String v_parent_name = v_parent.toString();				
					for(Vertex v: existential_vertices) {
						String v_name = v.toString();
						if(v_name.contains("_i_")) {
							String[] v_name_s = v_name.split("_i_");
							String v_name_w_role = v_name_s[0];
							String[] v_name_w_role_s = v_name_w_role.split("-role-label: ");
							v_name = v_name_w_role_s[0];
							String v_role_name = v_name_w_role_s[1];
							if(v_parent_name.equals(v_name)) {
								if(v_role_name.equals(key_role_name)) {
									if(!true_parents.contains(v)) {
									true_parents.add(v);
									}
								}
								else if(!v_role_name.equals(key_role_name)) {									
									if(checkSubsumptionRelationBetweenTwoRoles_TD(v_role_name, key_role_name, role_edges)) {
										if(!true_parents.contains(v)) {
											true_parents.add(v);	
										}
										
									}
								}
							}
						}
						else {
							if(v_name.contains("-role-label: ")) {
								String[] v_name_s = v_name.split("-role-label: ");
								v_name = v_name_s[0];
								String v_role_name = v_name_s[1];
								if(v_parent_name.equals(v_name)) {
									if(v_role_name.equals(key_role_name)) {
										if(!true_parents.contains(v)) {
										true_parents.add(v);
										}
									}else if(!v_role_name.equals(key_role_name)) {										
										if(checkSubsumptionRelationBetweenTwoRoles_TD(v_role_name, key_role_name, role_edges)) {
											if(!true_parents.contains(v)) {
												true_parents.add(v);	
											}
											
										}
									}
								}
							}
						}
					}
			}
			restrictions_subsumption_relations.put(key, true_parents);
			
		}
		
		return restrictions_subsumption_relations;
		
	}
	
	/*public List<Vertex> get_simple_types_restrictions_no_redundancies(Map<Vertex, List<Vertex>> retrictions_subsumptions, List<Vertex> existential_vertices){
		List<Vertex> existential_restrictions_parents = new ArrayList<>();
		for(Map.Entry<Vertex, List<Vertex>> child_parent_entry: retrictions_subsumptions.entrySet()) {
			List<Vertex> entry_values = child_parent_entry.getValue();
			for(Vertex entry_values_v: entry_values) {
				if(!entry_values_v.toString().contains("_i_")) {
					existential_restrictions_parents.add(entry_values_v);
				}
			}
		}
		
		List<Vertex> existential_vertices_copy = new ArrayList<>(existential_vertices);
		for(Vertex existential_vertex_parent: existential_restrictions_parents) {
			existential_vertices_copy.remove(existential_vertex_parent);
		}
		List<Vertex> existential_vertices_no_redundancies = new ArrayList<>();
		for(Vertex v: existential_vertices_copy) {
			if(!v.toString().contains("_i_")) {
				existential_vertices_no_redundancies.add(v);
			}
		}
		
		return existential_vertices_no_redundancies;
	}*/
	
	
public List<Vertex> get_simple_types_restrictions_no_redundancies(Map<Vertex, List<Vertex>> retrictions_subsumptions, List<Vertex> existential_vertices){
		
		//before deducing parents, clean the map retrictions_subsumptions from cycles
		//(1)
		Map<Vertex, List<Vertex>> retrictions_subsumptions_no_cycles = new HashMap<>();
		for(Map.Entry<Vertex, List<Vertex>> child_parent_entry: retrictions_subsumptions.entrySet()) {
			List<Vertex> entry_values = child_parent_entry.getValue();
			retrictions_subsumptions_no_cycles.put(child_parent_entry.getKey(), entry_values);
			List<Vertex> entry_values_no_cycles = new ArrayList<>(entry_values);
			for(Vertex entry_values_v: entry_values) {
				if(entry_values_v.equals(child_parent_entry.getKey())) {
					entry_values_no_cycles.remove(entry_values_v);
					retrictions_subsumptions_no_cycles.replace(child_parent_entry.getKey(), entry_values_no_cycles);
				}
			}
		
		}
		
		System.out.println("retrictions_subsumptions_no_cycles: " + retrictions_subsumptions_no_cycles);
		
		//(2) detect equivalences
		Map<Vertex, List<Vertex>> retrictions_subsumptions_no_equiv = detectEquivalences(retrictions_subsumptions_no_cycles);
		
		
		List<Vertex> existential_restrictions_parents = new ArrayList<>();
		for(Map.Entry<Vertex, List<Vertex>> child_parent_entry: retrictions_subsumptions_no_equiv.entrySet()) {
			List<Vertex> entry_values = child_parent_entry.getValue();
			for(Vertex entry_values_v: entry_values) {
				if(!entry_values_v.toString().contains("_i_")) {
					existential_restrictions_parents.add(entry_values_v);
				}
			}
		}
		System.out.println("the set existential_vertices:" + existential_vertices);
		System.out.println("the existential_restrictions_parents: " + existential_restrictions_parents);
	
		List<Vertex> existential_vertices_copy = new ArrayList<>(existential_vertices);
		for(Vertex existential_vertex_parent: existential_restrictions_parents) {
			existential_vertices_copy.remove(existential_vertex_parent);
		}
		List<Vertex> existential_vertices_no_redundancies = new ArrayList<>();
		for(Vertex v: existential_vertices_copy) {
			if(!v.toString().contains("_i_")) {
				existential_vertices_no_redundancies.add(v);
			}
		}
		
		return existential_vertices_no_redundancies;
	}


//detect equivalences of elements in a hashmap (checking if the child has a parent where the parent is also a child of the parent)
public Map<Vertex, List<Vertex>> detectEquivalences(Map<Vertex, List<Vertex>> retrictions_subsumptions_no_cycles) {
	Map<Vertex, List<Vertex>> retrictions_subsumptions_no_equiv = new HashMap<>(retrictions_subsumptions_no_cycles);
	for(Map.Entry<Vertex, List<Vertex>> child_parent_entry_1: retrictions_subsumptions_no_cycles.entrySet()) {
		//get the key, and the parents of the current entry
		Vertex child_1 = child_parent_entry_1.getKey();
		List<Vertex> parents_1 = child_parent_entry_1.getValue();
		for(Map.Entry<Vertex, List<Vertex>> child_parent_entry_2: retrictions_subsumptions_no_cycles.entrySet()) {
			Vertex child_2 = child_parent_entry_2.getKey();
			List<Vertex> parents_2 = child_parent_entry_2.getValue();
			if(!child_1.equals(child_2)) {
				//check if parents_1 contains child_2
				if(parents_1.contains(child_2)) {
					if(parents_2.contains(child_1)) {
						//there is equivalence, in this case will
						retrictions_subsumptions_no_equiv.replace(child_1, new ArrayList<>());
					}
				}
			}
		}
		
	}
	
	System.out.println("the retrictions_subsumptions_no_equiv without equiv: " + retrictions_subsumptions_no_equiv);
	return retrictions_subsumptions_no_equiv;
}
	
	public List<Vertex> get_nested_types_restrictions_no_redundancies(Map<Vertex, List<Vertex>> retrictions_subsumptions, List<Vertex> existential_vertices){
		
		List<Vertex> nested_types_restrictions_no_redundancies = new ArrayList<>();
		
		List<Vertex> existential_vertices_nested = new ArrayList<>();
		Map<Integer, List<Vertex>> existential_vertices_indices = new HashMap<>();
		for(Vertex vertex: existential_vertices) {
			String vertex_name = vertex.toString();
			if(vertex_name.contains("_i_")) {
				existential_vertices_nested.add(vertex);
				String[]vertex_name_s = vertex_name.split("_i_");
				int index_num_vertex_n = Integer.parseInt(vertex_name_s[1]);	
				existential_vertices_indices.put(index_num_vertex_n, new ArrayList<>());	
			}
		}
		
		for(Map.Entry<Integer, List<Vertex>> indexed_vertex_entry : existential_vertices_indices.entrySet()) {
			List<Vertex> same_indexed_vertices = new ArrayList<>();
			for(int i = 0; i < existential_vertices_nested.size(); i++) {
				Vertex vertex_1 = existential_vertices_nested.get(i);
				String vertex_1_name = vertex_1.toString();
					if(vertex_1_name.contains("_i_")) {
					String[]vertex_1_name_s = vertex_1_name.split("_i_");
					int index_num_vertex_n = Integer.parseInt(vertex_1_name_s[1]);
					if(indexed_vertex_entry.getKey().equals(index_num_vertex_n)) {
						same_indexed_vertices.add(vertex_1);
					}
				}
				}
			
			indexed_vertex_entry.setValue(same_indexed_vertices);
		}
		
		List<Entry<Integer, List<Vertex>>> existential_vertices_indices_list = new ArrayList<>();
		for(Map.Entry<Integer, List<Vertex>> vertices_indices_entry: existential_vertices_indices.entrySet()) {
			existential_vertices_indices_list.add(vertices_indices_entry);
		}
		
		Map<Integer, Boolean> existential_vertices_indices_flags = new HashMap<>();
		for(Integer index: existential_vertices_indices.keySet()) {
			existential_vertices_indices_flags.put(index, true);
		}
		List<Edge> role_edges = graph.getRoleEdges();
		for(int i = 0; i < existential_vertices_indices_list.size(); i++) {
			Entry<Integer, List<Vertex>> entry_1 = existential_vertices_indices_list.get(i);
			if(existential_vertices_indices_flags.get(entry_1.getKey())) {			
				for(int j = 0; j < existential_vertices_indices_list.size(); j++) {
					Entry<Integer, List<Vertex>> entry_2 = existential_vertices_indices_list.get(j);
					if(existential_vertices_indices_flags.get(entry_2.getKey())) {				
						if(!entry_1.equals(entry_2)) {
							List<Vertex> entry_1_values = entry_1.getValue();
							List<Vertex> entry_2_values = entry_2.getValue();
							Set<String> entry_1_values_names = new HashSet<>();
							for(Vertex entry_1_values_vertex: entry_1_values) {
								String entry_1_values_vertex_name = get_vertex_name_with_role(entry_1_values_vertex);
								entry_1_values_names.add(entry_1_values_vertex_name);
							}
							Set<String> entry_2_values_names = new HashSet<>();
							for(Vertex entry_2_values_vertex: entry_2_values) {
								String entry_2_values_vertex_name = get_vertex_name_with_role(entry_2_values_vertex);
								entry_2_values_names.add(entry_2_values_vertex_name);
							}
							List<Vertex> entry_1_values_parents = new ArrayList<>();
							for(Vertex entry_v_1: entry_1_values) {
								List<Vertex> entry_v_1_parents = retrictions_subsumptions.get(entry_v_1);
								entry_1_values_parents.addAll(entry_v_1_parents);	
							}
							Set<String> entry_1_values_parents_names = new HashSet<>();
							for(Vertex v_1_parent: entry_1_values_parents) {
								String v_1_parent_name = get_vertex_name_with_role(v_1_parent);
								entry_1_values_parents_names.add(v_1_parent_name);
							}
							if(entry_1_values_names.equals(entry_2_values_names)){
								continue; 
							}
						
							Map<String, Boolean> entry_2_flagged = new HashMap<>();
							for(String v_name_2: entry_2_values_names) {
								entry_2_flagged.put(v_name_2, false);
							}
					
							for(String name_1: entry_1_values_names) {
								for(String name_2: entry_2_values_names) {
									if(name_1.equals(name_2)) {
										entry_2_flagged.put(name_2, true);
									}
									else if(!name_1.equals(name_2)) {										
											String name_1_role = get_vertex_role(name_1);
											String name_2_role = get_vertex_role(name_2);
											if(checkSubsumptionRelationBetweenTwoRoles_TD(name_2_role, name_1_role, role_edges)) {
												String name_1_no_role = get_vertex_name_without_role(name_1);
												String name_2_no_role = get_vertex_name_without_role(name_2);
												if(name_1_no_role.equals(name_2_no_role)) {
													entry_2_flagged.replace(name_2, true);
												}
											}
						
											for(String e_1_parent: entry_1_values_parents_names) {
												if(e_1_parent.equals(name_2)) {
													entry_2_flagged.replace(name_2, true);
												}
											}
									}
								}
							}
								
							if(areAllTrue(entry_2_flagged)) {
								existential_vertices_indices_flags.replace(entry_2.getKey(), false);
							}
							
						if(entry_1_values_names.containsAll(entry_2_values_names)) {
							existential_vertices_indices_flags.replace(entry_2.getKey(), false);
								}else if(entry_2_values_names.equals(entry_1_values_parents_names)) {
									existential_vertices_indices_flags.replace(entry_2.getKey(), false);
								}
				}
			}
			}
		}
	}
		
		for(Map.Entry<Integer, List<Vertex>> exist_vertices_indices_entry :existential_vertices_indices.entrySet()) {
			Integer key = exist_vertices_indices_entry.getKey();
			if(existential_vertices_indices_flags.get(key)) {
				nested_types_restrictions_no_redundancies.addAll(exist_vertices_indices_entry.getValue());
			}
		}
		
		return nested_types_restrictions_no_redundancies;
		
	}
	
	public OWLEquivalentClassesAxiom getOWLDefinition(Vertex lhs_v, List<Vertex> primitive_vertices_no_redundancies, List<Vertex> existential_vertices_no_redundancies) {

		ToOWL toOWL = new ToOWL();
		System.out.println("lhs_v: " + lhs_v);
		OWLClass cl_lhs = toOWL.getOwlClassFromVertex(lhs_v);
		Set<OWLClass> owlclass_primitives = new HashSet<>();
		Set<OWLObjectSomeValuesFrom> obsv_set = new HashSet<>();
		Set<OWLClassExpression> conjuncts_set = new HashSet<>();
		
		for(Vertex closest_pr: primitive_vertices_no_redundancies) {
			OWLClass cl_pr = toOWL.getOwlClassFromVertex(closest_pr);
			owlclass_primitives.add(cl_pr);
		}
		List<Vertex> existential_vertices_nested_type = new ArrayList<>();
		Map<Integer, List<Vertex>> existential_vertices_indices = new HashMap<>();
		for(Vertex vertex: existential_vertices_no_redundancies) {
			String vertex_name = vertex.toString();
			if(vertex_name.contains("_i_")) {
				existential_vertices_nested_type.add(vertex);
				String[]vertex_name_s = vertex_name.split("_i_");
				int index_num_vertex_n = Integer.parseInt(vertex_name_s[1]);	
				existential_vertices_indices.put(index_num_vertex_n, new ArrayList<>());
				
				
			}
		}
		List<Vertex> existential_vertices_simple_type = new ArrayList<>();
		for(Vertex vertex: existential_vertices_no_redundancies) {
			String vertex_name = vertex.toString();
			if(!vertex_name.contains("_i_")) {
				existential_vertices_simple_type.add(vertex);
			}
		}
		for(Map.Entry<Integer, List<Vertex>> index_vertex_entry : existential_vertices_indices.entrySet()) {
			List<Vertex> same_index_vertices = new ArrayList<>();
			for(int i = 0; i < existential_vertices_nested_type.size(); i++) {
				Vertex vertex_1 = existential_vertices_nested_type.get(i);
				String vertex_1_name = vertex_1.toString();
					if(vertex_1_name.contains("_i_")) {
					String[]vertex_1_name_s = vertex_1_name.split("_i_");

					int index_num_vertex_n = Integer.parseInt(vertex_1_name_s[1]);
					if(index_vertex_entry.getKey().equals(index_num_vertex_n)) {
						same_index_vertices.add(vertex_1);
					}
				}
				}
			
			index_vertex_entry.setValue(same_index_vertices);
		}

		List<Vertex> nested_conjuncts_vertices = new ArrayList<>();
		for(Map.Entry<Integer, List<Vertex>> index_vertex_same: existential_vertices_indices.entrySet()) {
			List<Vertex> same_index_vertices = index_vertex_same.getValue();
			String siv_name = "";
			Vertex siv_vertex = null;
			if(same_index_vertices.size() > 1) {
			for(Vertex siv: same_index_vertices) {
				siv_name += "vx_" + siv.toString();
				siv_vertex = new Vertex(siv_name);
				
			}
			nested_conjuncts_vertices.add(siv_vertex);
			}else if(same_index_vertices.size() == 1) {
				nested_conjuncts_vertices.addAll(same_index_vertices);
			}
			
		}
		for(Vertex constraint_v: nested_conjuncts_vertices) {
			OWLObjectSomeValuesFrom obsv = toOWL.getOBSV(constraint_v);
			obsv_set.add(obsv);
		}
		
		for(Vertex constraint_v: existential_vertices_simple_type) {
			OWLObjectSomeValuesFrom obsv = toOWL.getOBSV(constraint_v);
			obsv_set.add(obsv);
		}
		conjuncts_set.addAll(owlclass_primitives);
		conjuncts_set.addAll(obsv_set);
		OWLEquivalentClassesAxiom abstract_def = toOWL.getOWLDefinition(cl_lhs, conjuncts_set);		
		return abstract_def;
		
	}
	
	
	public OWLSubClassOfAxiom getOWLSubClassOfAxiom(Vertex lhs_v, List<Vertex> adjacent_vertices) {
		ToOWL toOWL = new ToOWL();
		System.out.println("lhs_v: " + lhs_v);
		OWLClass cl_lhs = toOWL.getOwlClassFromVertex(lhs_v);
		Set<OWLClass> owlclasses = new HashSet<>();
		Set<OWLObjectSomeValuesFrom> obsv_set = new HashSet<>();
		Set<OWLClassExpression> conjuncts_set = new HashSet<>();
		
		List<Vertex> existential_vertices_nested_type = new ArrayList<>();
		Map<Integer, List<Vertex>> existential_vertices_indices = new HashMap<>();
		
		List<Vertex> existential_vertices_simple_type = new ArrayList<>();
		
		for(Vertex adj_vertex: adjacent_vertices) {
			String vertex_name = adj_vertex.toString();
			if(vertex_name.contains("-role-label")) {
				if(vertex_name.contains("_i_")) {
					existential_vertices_nested_type.add(adj_vertex);
					String[]vertex_name_s = vertex_name.split("_i_");
					int index_num_vertex_n = Integer.parseInt(vertex_name_s[1]);	
					existential_vertices_indices.put(index_num_vertex_n, new ArrayList<>());	
				}
			
			//get name of vertex to check if the vertex contain role label then it's an existential vertex
			
				//then convert to OBSV
				if(!vertex_name.contains("_i_")) {
					existential_vertices_simple_type.add(adj_vertex);
				}	
			}else if(!vertex_name.contains("-role-label")) {
				OWLClass cl = toOWL.getOwlClassFromVertex(adj_vertex);
				owlclasses.add(cl);
			}
		}
		
		
		for(Map.Entry<Integer, List<Vertex>> index_vertex_entry : existential_vertices_indices.entrySet()) {
			List<Vertex> same_index_vertices = new ArrayList<>();
			for(int i = 0; i < existential_vertices_nested_type.size(); i++) {
				Vertex vertex_1 = existential_vertices_nested_type.get(i);
				String vertex_1_name = vertex_1.toString();
					if(vertex_1_name.contains("_i_")) {
					String[]vertex_1_name_s = vertex_1_name.split("_i_");

					int index_num_vertex_n = Integer.parseInt(vertex_1_name_s[1]);
					if(index_vertex_entry.getKey().equals(index_num_vertex_n)) {
						same_index_vertices.add(vertex_1);
					}
				}
				}
			
			index_vertex_entry.setValue(same_index_vertices);
		}
		
		
		List<Vertex> nested_conjuncts_vertices = new ArrayList<>();
		for(Map.Entry<Integer, List<Vertex>> index_vertex_same: existential_vertices_indices.entrySet()) {
			List<Vertex> same_index_vertices = index_vertex_same.getValue();
			String siv_name = "";
			Vertex siv_vertex = null;
			if(same_index_vertices.size() > 1) {
			for(Vertex siv: same_index_vertices) {
				siv_name += "vx_" + siv.toString();
				siv_vertex = new Vertex(siv_name);
				
			}
			nested_conjuncts_vertices.add(siv_vertex);
			}else if(same_index_vertices.size() == 1) {
				nested_conjuncts_vertices.addAll(same_index_vertices);
			}
			
		}
		for(Vertex constraint_v: nested_conjuncts_vertices) {
			OWLObjectSomeValuesFrom obsv = toOWL.getOBSV(constraint_v);
			obsv_set.add(obsv);
		}
		
		for(Vertex constraint_v: existential_vertices_simple_type) {
			OWLObjectSomeValuesFrom obsv = toOWL.getOBSV(constraint_v);
			obsv_set.add(obsv);
		}
		
		
		conjuncts_set.addAll(owlclasses);
		conjuncts_set.addAll(obsv_set);
		
		OWLSubClassOfAxiom subof = toOWL.getOWLSubClassOf(cl_lhs,conjuncts_set);
		return subof;
		
	}
	
	
	public OWLSubObjectPropertyOfAxiom getOWLSubProperty(String parent_role_name, String child_role_name) {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		
		ToOWL toOWL = new ToOWL();
		
		OWLObjectProperty sub_property = df.getOWLObjectProperty(IRI.create(child_role_name));
		OWLObjectProperty super_property = df.getOWLObjectProperty(IRI.create(parent_role_name));
		
		OWLSubObjectPropertyOfAxiom object_property_ax = toOWL.getOWLSubPropertyOf(sub_property, super_property);
		
		return object_property_ax;	
	}
	
	public Boolean checkSubsumptionRelationBetweenTwoRoles_TD(String parent_role_name, String child_role_name, List<Edge> role_edges) {
		endTherecursion = false;
		
			for(Edge role_edge: role_edges) {
				if(endTherecursion) {break;}
				if(role_edge != null) {
					Vertex parent_role_of_edge = role_edge.getGSource();
					String parent_role_of_edge_name = parent_role_of_edge.toString();
					Vertex child_role_of_edge = role_edge.getGDestination();
					String child_role_of_edge_name = child_role_of_edge.toString();
					if(parent_role_of_edge_name.equals(parent_role_name)) {
						if(child_role_of_edge_name.equals(child_role_name)) {
							endTherecursion = true;
							return true;
						}else {
							checkSubsumptionRelationBetweenTwoRoles_TD(child_role_of_edge_name, child_role_name, role_edges);
						}
					}
				}
			}

			return endTherecursion;
		}
	
	public String get_vertex_name_type_rg(Vertex v) {
		String v_name = "";
		if(v.toString().contains("_i_")) {
			String[] v_name_s = v.toString().split("_i_");
			String[] v_name_s_2 = v_name_s[0].split("-role-label: ");
			v_name = v_name_s_2[0];
		}
		
		return v_name;
	}
	
	public String get_vertex_name_type_simple(Vertex v) {
		String v_name = "";
		if(v.toString().contains("-role-label: ")) {
			String[] v_name_s = v.toString().split("-role-label: ");
			v_name = v_name_s[0];
		}
		return v_name;
	}
	
	public String get_vertex_role(Vertex v) {
		String v_name = "";
		if(v.toString().contains("_i_")) {
			String[] v_name_s = v.toString().split("_i_");
			String[] v_name_s_2 = v_name_s[0].split("-role-label: ");
			v_name = v_name_s_2[1];
		}
		
		return v_name;
	}
	
	public String get_vertex_role(String name_with_role) {
		String v_name = "";
		String[] v_name_s_2 = name_with_role.split("-role-label: ");
		v_name = v_name_s_2[1];
		return v_name;
	}
	
	public String get_vertex_name_with_role(Vertex v) {
		String v_name = "";
		if(v.toString().contains("_i_")) {
			String[] v_name_s = v.toString().split("_i_");
			v_name = v_name_s[0];
		}
		
		return v_name;
	}
	
	public String get_vertex_name_without_role(String name_with_role) {
		String v_name = "";
		String[] v_name_s = name_with_role.toString().split("-role-label: ");
		v_name = v_name_s[0];
		return v_name;
	}
	public static boolean areAllTrue(Map<String, Boolean> map)
	{
	    for(boolean b: map.values()) if(!b) return false;
	    return true;
	}
	

}
