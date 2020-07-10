package Graph;



import Graph.Def_Vertex;
import Graph.NDef_Vertex;

public class Edge {
	
	protected NDef_Vertex source;
	protected NDef_Vertex destination;
    protected Def_Vertex def_source; 
    protected Def_Vertex def_destination;
    protected Role_g_Vertex role_group_source_vertex;
    protected Role_g_Vertex role_group_dist_vertex;
	//the edge is a translation of the is a owl axiom
    
    /*public Edge(Def_Vertex def_source, Def_Vertex def_destination, NDef_Vertex source, NDef_Vertex destination) {
    		this.def_source = def_source;
        this.def_destination = def_destination;
        this.source = source;
        this.destination = destination;
    }*/
    
    
   
    public NDef_Vertex getDestination() {
        return destination;
    }

    public NDef_Vertex getSource() {
        return source;
    }
    
    public Def_Vertex getDefDestination() {
        return def_destination;
    }

    public Def_Vertex getDefSource() {
        return def_source;
    }
    
    public Vertex getGSource() {
    	//check if defined source then returns the defined vertex
    		if(source != null) {
    			return source;
    		}else if(def_source != null){
    			return def_source;
    		}else if(role_group_source_vertex != null) {
    			return role_group_source_vertex;
    		}
    		
    		return null;
    }
    
    
    public Vertex getGDestination() {
    	//check if defined source then returns the defined vertex
    		if(destination != null) {
    			return destination;
    		}else if(def_destination != null){
    			return def_destination;
    		}else if(role_group_dist_vertex != null) {
    			return role_group_dist_vertex;
    		}
    		
    		return null;
    }
    
    @Override
    public String toString() {
    	 StringBuilder s = new StringBuilder();
    	 if(def_source != null && def_destination != null) {
    	 s.append(" -Defined Source: " + def_source.toString() + " -Defined Destination: " + def_destination.toString());
    	 }else if(source != null && destination != null) {
     s.append(" -Primtive Source: " + source.toString() + " -Primtive Destination: " + destination.toString());
    	 }else if(def_source != null && destination != null) {
    	s.append(" -Defined Source: " + def_source.toString() + " -Primitive Destination: " + destination.toString());
    	 }else if(source != null && def_destination != null) {
    	s.append(" -Primitive Source: " + source.toString() + " -Defined Destination: " + def_destination.toString()); 
    	 }
    	 //s.append("\n");
    	 //s.append("- Source: " + source.toString() + " - Destination: " + destination.toString());
    	 return s.toString();
    }
    

}

