package Converter;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import Graph.Vertex;

public class ToOWL {
	
	//manager
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	//datafactory
	OWLDataFactory df = manager.getOWLDataFactory();
	
	
	//From Vertex to OWLClass
	
	public OWLClass getOwlClassFromVertex(Vertex vertex_class) {
		OWLClass class_v = df.getOWLClass(IRI.create(vertex_class.toString()));
		
		return class_v;
	}
	
	
	//From Vertex to Role
	public OWLObjectProperty getOwlPropertyFromVertex(Vertex vertex_property) {
		OWLObjectProperty property_v = df.getOWLObjectProperty(IRI.create(vertex_property.toString()));
		
		return property_v;
	}
	
	
	//From Edge to OWLAxiom
	
	
	//From labelledVertex to existential Expression

	
	//the role group property
	String role_group_property_name = "http://snomed.info/id/609096000";
	public OWLObjectSomeValuesFrom getOBSV(Vertex labelled_vertex) {
		//these vertices contain role g
		OWLObjectSomeValuesFrom obsv = null;
		String labelled_vertex_name = labelled_vertex.toString();
		System.out.println("the labelled_vertex_name: " + labelled_vertex_name);
		Set<OWLObjectSomeValuesFrom> obsv_conjuncts = new HashSet<>();
		//if(labelled_vertex_name.contains("role_g")) {
		//OWLObjectProperty role_group_property = df.getOWLObjectProperty(IRI.create(role_group_property_name));
			if(labelled_vertex_name.contains("vx_")) {
				OWLObjectProperty role_group_property = df.getOWLObjectProperty(IRI.create(role_group_property_name));
				String[] labelled_vertex_name_s = labelled_vertex_name.split("vx_");
				String[] labelled_vertex_name_s_u = removeTheElement(labelled_vertex_name_s,0);
				System.out.println("labelled_vertex_name_s_u[0]: " + labelled_vertex_name_s_u[0]);
				for(int i = 0; i < labelled_vertex_name_s_u.length; i++) {
					if(labelled_vertex_name_s_u[i].contains("-role-label:")) {
						String[] labelled_vertex_name_s_u_roles_split = labelled_vertex_name_s_u[i].split("-role-label:");
						String class_name = labelled_vertex_name_s_u_roles_split[0];
						OWLClass cl = df.getOWLClass(IRI.create(class_name));
						String property_name = labelled_vertex_name_s_u_roles_split[1];
						//split the property name
						String[] property_name_s = property_name.split("_i_");
						//the property name is in the first index
						String property_name_s_n = property_name_s[0];
						property_name_s_n = property_name_s_n.trim();
						//System.out.println("the property name is: " + property_name_s_n);
						OWLObjectProperty pr = df.getOWLObjectProperty(IRI.create(property_name_s_n));
						OWLObjectSomeValuesFrom obsv_i = df.getOWLObjectSomeValuesFrom(pr, cl);
						obsv_conjuncts.add(obsv_i);
					}
				}
				
				//create the object intersectionfo?
				OWLObjectIntersectionOf obsv_conjunct = df.getOWLObjectIntersectionOf(obsv_conjuncts);
				obsv = df.getOWLObjectSomeValuesFrom(role_group_property, obsv_conjunct);
			} 
				/*if(labelled_vertex_name.contains("-role-label:")) {
					String[] labelled_vertex_name_split = labelled_vertex_name.split("-role-label:");
					String class_name = labelled_vertex_name_split[0];
					
					OWLClass cl = df.getOWLClass(IRI.create(class_name));
					String property_name = labelled_vertex_name_split[1];
					String[] property_name_s = property_name.split("_i_");
					//the property name is in the first index
					String property_name_s_n = property_name_s[0];
					property_name_s_n = property_name_s_n.trim();
					//System.out.println("the property name is: " + property_name_s_n);
					OWLObjectProperty pr = df.getOWLObjectProperty(IRI.create(property_name_s_n));
					OWLObjectSomeValuesFrom obsv_n = df.getOWLObjectSomeValuesFrom(pr, cl);
					obsv = df.getOWLObjectSomeValuesFrom(role_group_property, obsv_n);
				}*/
				
			
			
			
		//} 
		
		
		else {
			String[] labelled_vertex_name_split = labelled_vertex_name.split("-role-label:");
			String class_name = labelled_vertex_name_split[0];
			
			OWLClass cl = df.getOWLClass(IRI.create(class_name));
			
			String property_name = labelled_vertex_name_split[1];
			if(property_name.toString().contains("_i_")) {
				OWLObjectProperty role_group_property = df.getOWLObjectProperty(IRI.create(role_group_property_name));
				
				System.out.println("the property_name: " + property_name);
				String[] property_name_s = property_name.split("_i_");
				property_name = property_name_s[0];
				property_name = property_name.trim();
				OWLObjectProperty pr = df.getOWLObjectProperty(IRI.create(property_name));
				OWLObjectSomeValuesFrom obsv_n = df.getOWLObjectSomeValuesFrom(pr, cl);
				obsv = df.getOWLObjectSomeValuesFrom(role_group_property, obsv_n);
			}
			
			else{
				property_name = property_name.trim();
			//System.out.println("the property name is: " + property_name);
			OWLObjectProperty pr = df.getOWLObjectProperty(IRI.create(property_name));
			obsv = df.getOWLObjectSomeValuesFrom(pr, cl);
			}
		}
		
		return obsv;
	}
	
	
	//we use this converter to convert the returned primitive verticies to owl classes, and the lablled_vertex to existential expression.
	//then form the equivalence axiom
	public OWLEquivalentClassesAxiom getOWLDefinition(OWLClass lhs_cl, Set<OWLClassExpression> rhs_conjunct_set) {
		OWLObjectIntersectionOf conjuncts = df.getOWLObjectIntersectionOf(rhs_conjunct_set);
		OWLEquivalentClassesAxiom def_axiom = df.getOWLEquivalentClassesAxiom(lhs_cl, conjuncts);
		
		return def_axiom;
	}
	
	
	// Function to remove the element from an string array
    public static String[] removeTheElement(String[] arr, 
                                          int index) 
    { 
  
        // If the array is empty 
        // or the index is not in array range 
        // return the original array 
        if (arr == null
            || index < 0
            || index >= arr.length) { 
  
            return arr; 
        } 
  
        // Create another array of size one less 
        String[] anotherArray = new String[arr.length - 1]; 
  
        // Copy the elements except the index 
        // from original array to the other array 
        for (int i = 0, k = 0; i < arr.length; i++) { 
  
            // if the index is 
            // the removal element index 
            if (i == index) { 
                continue; 
            } 
            // if the index is not 
            // the removal element index 
            anotherArray[k++] = arr[i]; 
        }
        // return the resultant array 
        return anotherArray; 
    } 
	//denormalise

}
