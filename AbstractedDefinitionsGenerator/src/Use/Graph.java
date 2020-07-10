package Use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Graph.Edge;
//import Graph.Edge;
import Graph.Vertex;

public class Graph {
	
	 List<Edge> role_edges = new ArrayList<>();
	 List<Vertex> primitive_vertices = new ArrayList<>();
	 List<Vertex> existential_vertices = new ArrayList<>();
	 List<Vertex> proximal_primitive_vertices = new ArrayList<>();
	 List<Vertex> existential_vertices_no_redundancies = new ArrayList<>();
	
	
	
	 Vertex vertex_lhs;
	 Map<Vertex, List<Vertex>> adj_list_map = new HashMap<>();
	
	
	public void setAdjListMap(Map<Vertex, List<Vertex>> adj_list_map) {
		this.adj_list_map = adj_list_map;
	}
	
	public Map<Vertex, List<Vertex>> getAdjListMap() {
        return adj_list_map;
    }
	
	public void setRoleEdges(List<Edge> role_edges) {
		this.role_edges = role_edges;
	}
	
	public List<Edge> getRoleEdges(){
		return role_edges;
	}
	
	public void setPrimitiveVertices(List<Vertex> primitive_vertices) {
		this.primitive_vertices = primitive_vertices;
	}
	
	public List<Vertex> getPrimitiveVertices(){
		return primitive_vertices;
	}
   
	public void setVertexLhs(Vertex vertex_lhs){
		this.vertex_lhs = vertex_lhs;
	}
	
	public Vertex getVertexLhs() {
		return vertex_lhs;
	}

	public List<Vertex> getExistentialVertices() {
		return existential_vertices;
	}

	public void setExistentialVertices(List<Vertex> existential_vertices) {
		this.existential_vertices = existential_vertices;
	}

	public List<Vertex> getProximalPrimitiveVertices() {
		return proximal_primitive_vertices;
	}

	public void setProximalPrimitiveVertices(List<Vertex> proximal_primitive_vertices) {
		this.proximal_primitive_vertices = proximal_primitive_vertices;
	}

	public List<Vertex> getExistentialVerticesNoRedundancies() {
		return existential_vertices_no_redundancies;
	}

	public void setExistentialVerticesNoRedundancies(List<Vertex> existential_vertices_no_redundancies) {
		this.existential_vertices_no_redundancies = existential_vertices_no_redundancies;
	}
 
    
}