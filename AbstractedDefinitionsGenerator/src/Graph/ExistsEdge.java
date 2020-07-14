/**
Author: Ghadah Alghamdi
*/

package Graph;

public class ExistsEdge extends Edge {

    private String role_label;

    //private Role_g_Vertex role_group_vertex;
    
    public ExistsEdge(Def_Vertex def_source, Def_Vertex def_destination) {
		this.def_source = def_source;
		this.def_destination = def_destination;
}

    public ExistsEdge(NDef_Vertex source, NDef_Vertex destination) {
		this.source = source;
		this.destination = destination;
}

    public ExistsEdge(Def_Vertex def_source, NDef_Vertex destination) {
	this.def_source = def_source;
	this.destination = destination;
}

    public ExistsEdge(NDef_Vertex source, Def_Vertex def_destination) {
	this.source = source;
	this.def_destination = def_destination;
}
    
    //the source is role group, destination is Ndefined vertex
    /*public ExistsEdge(Role_g_Vertex role_group_source_vertex, String role_label, NDef_Vertex destination) {
    	this.role_group_source_vertex = role_group_source_vertex;
    	this.role_label = role_label;
    	this.destination = destination;
    }*/
    
    public ExistsEdge(NDef_Vertex source, String role_label, Role_g_Vertex role_group_dist_vertex) {
    	//this.role_group_source_vertex = role_group_source_vertex;
    	this.role_group_dist_vertex = role_group_dist_vertex;
    	this.role_label = role_label;
    	this.source = source;
    }
    
   
    /*//the source is Ndefined vertex, destination is role group
    public ExistsEdge(NDef_Vertex source, Role_g_Vertex role_group_dist_vertex) {
    	this.source = source;
    	this.role_group_dist_vertex = role_group_dist_vertex;
    }*/
    
    //the source is role group, destination is Ndefined vertex
    //new
    public ExistsEdge(Role_g_Vertex role_group_source_vertex, NDef_Vertex destination) {
    	//this.source = source;
    	this.destination = destination;
    //	this.role_group_dist_vertex = role_group_dist_vertex;
    	this.role_group_source_vertex = role_group_source_vertex;
    }
        
    //the source is role group, destination is defined vertex
    /*public ExistsEdge(Role_g_Vertex role_group_source_vertex, String role_label, Def_Vertex def_destination) {
    	this.role_group_source_vertex = role_group_source_vertex;
    	this.role_label = role_label;
    	this.def_destination = def_destination;
    }
    */
    //new
    public ExistsEdge(Def_Vertex def_source, String role_label, Role_g_Vertex role_group_dist_vertex) {
    	//this.role_group_source_vertex = role_group_source_vertex;
    	this.role_group_dist_vertex = role_group_dist_vertex;
    	this.role_label = role_label;
    	this.def_source = def_source;
    }
    
    
    //the source is defined vertex, the destination is role group
    /*public ExistsEdge(Def_Vertex def_source, Role_g_Vertex role_group_dist_vertex) {
    	this.def_source = def_source;
    	this.role_group_dist_vertex = role_group_dist_vertex;
    }*/
    
    //new
    public ExistsEdge(Role_g_Vertex role_group_source_vertex, Def_Vertex def_destination) {
    	this.def_destination = def_destination;
    //	this.role_group_dist_vertex = role_group_dist_vertex;
    	this.role_group_source_vertex = role_group_source_vertex;
    }
    

    public ExistsEdge(Def_Vertex def_source, String role_label, Def_Vertex def_destination) {
    	this.def_source = def_source;
    	this.role_label = role_label;
    	this.def_destination = def_destination;
    }
    

    public ExistsEdge(Def_Vertex def_source, String role_label, NDef_Vertex destination) {
    	this.def_source = def_source;
    	this.role_label = role_label;
    	this.destination = destination;
    }
    
    public ExistsEdge(NDef_Vertex source, String role_label, Def_Vertex def_destination) {
    	this.source = source;
    	this.role_label = role_label;
    	this.def_destination = def_destination;
    }
    
    public ExistsEdge(NDef_Vertex source, String role_label, NDef_Vertex destination) {
    	this.source = source;
    	this.role_label = role_label;
    	this.destination = destination;
    }
    
   
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
    
    /*public Role_g_Vertex getRoleGSource() {
    	return role_group_source_vertex;
    }
    
    public Role_g_Vertex getRoleGDistantion() {
    	return role_group_dist_vertex;
    }*/

  /*  public Vertex getGSource() {
	//check if defined source then returns the defined vertex
		if(source != null) {
			return source;
		}else if(def_source != null){
			return def_source;
		}
		
		return null;
    }
*/

   /* public Vertex getGDestination() {
	//check if defined source then returns the defined vertex
		if(destination != null) {
			return destination;
		}else if(def_destination != null){
			return def_destination;
		}
		
		return null;
    }*/
    
    public String getLabel() {
    		return role_label;
    }
    
    public void selLabel(String role_label) {
		this.role_label = role_label;
}

@Override
public String toString() {
	StringBuilder s = new StringBuilder();
	if(def_source != null && role_label != null && def_destination != null) {
		 s.append(" -Defined Source: " + def_source.toString() + " -Defined Destination: " + def_destination.toString() + " -Role Labeled: " + role_label.toString());
	 }else if(source != null && role_label != null && destination != null) {
		 s.append(" -Primtive Source: " + source.toString() + " -Primitive Destination: " + destination.toString() + " -Role Labeled: " + role_label.toString());
	 }else if(def_source != null && role_label != null && destination != null) {
		 s.append(" -Defined Source: " + def_source.toString() + " -Primitive Destination: " + destination.toString() + " -Role Labeled: " + role_label.toString());
	 }else if(source != null && role_label != null && def_destination != null) {
		 s.append(" -Primitive Source: " + source.toString() + " -Defined Destination: " + def_destination.toString() + " -Role Labeled: " + role_label.toString()); 
	 }
	 else if(role_group_source_vertex != null && destination != null) {
		 s.append(" -Source Role Group: " + role_group_source_vertex.toString() + " -Primitive Destination: " + destination.toString());
	 }else if(role_group_source_vertex != null && def_destination != null) {
		 s.append(" -Source Role Group: " + role_group_source_vertex.toString() + " -Defined Destination: " + def_destination.toString());
	 }else if(source != null && role_label != null && role_group_dist_vertex != null) {
		 s.append(" -Primitive Source: " + source.toString() + " -Destination Role Group: " + role_group_dist_vertex.toString() + " -Role Labeled: " + role_label.toString());
	 }else if(def_source != null && role_label != null && role_group_dist_vertex != null) {
		 s.append(" -Defined Source: " + def_source.toString() + " -Destination Role Group: " + role_group_dist_vertex.toString() + " -Role Labeled: " + role_label.toString());
	 }
	return s.toString();
}

}
