/**
Author: Ghadah Alghamdi
*/

package Graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//final private String id;
    private String name;
    private List<Vertex> parents = new ArrayList<>();
    
    public Vertex(String name) {
        //this.id = id;
        this.name = name;
    }
    
    public void setName(String vertex_name) {
    		this.name = vertex_name;
    }
    //specify the type of the vertex?
    
    @Override
    public String toString() {
    		return name;
    }
    
    
    public void addParent(Vertex vertex) {
    		parents.add(vertex);
    }
    
    public List<Vertex> getParents() {
		return parents;
    }
}
