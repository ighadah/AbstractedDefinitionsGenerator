package Graph;

public class SimpleEdge extends Edge {

	public SimpleEdge(Def_Vertex def_source, Def_Vertex def_destination) {
		this.def_source = def_source;
		this.def_destination = def_destination;
}

	public SimpleEdge(NDef_Vertex source, NDef_Vertex destination) {
		this.source = source;
		this.destination = destination;
}

public SimpleEdge(Def_Vertex def_source, NDef_Vertex destination) {
	this.def_source = def_source;
	this.destination = destination;
}

public SimpleEdge(NDef_Vertex source, Def_Vertex def_destination) {
	this.source = source;
	this.def_destination = def_destination;
}

}
