import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DelivC {

	private File inputFile;
	private File outputFile;
	private PrintWriter output;
	private Graph graph;

	// Constructor - DO NOT MODIFY
	public DelivC(File in, Graph gr) {
		inputFile = in;
		graph = gr;

		// Set up for writing to a file
		try {
			// Use input file name to create output file in the same location
			String inputFileName = inputFile.toString();
			String outputFileName = inputFileName.substring(0, inputFileName.length() - 4).concat("_out.txt");
			outputFile = new File(outputFileName);

			// A Printwriter is an object that can write to a file
			output = new PrintWriter(outputFile);
		} catch (Exception x) {
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}

		// Calls the method that will do the work of deliverable C
		runDelivC();

		output.flush();
	}

	// *********************************************************************************
	// This is where your work starts

	private void runDelivC() {

		ArrayList<Node> m = graph.getNodeList(); // assign graph nodelist to m.
		ArrayList<Edge> g = graph.getEdgeList(); // assign graph nodelist to m.
		ArrayList<Edge> mst = prims(m, g);// implements prims algorithm, please check method for further explanation.

		/**
		 * this part of the code handles the mundane, it calculates the total cost of
		 * the mst by using an advanced for loop and adding all the distances of the
		 * nodes in the mst together. then takes care of the printing to the console,
		 * and the file.
		 */
		int totalLength = 0; // creates integer variable called totalLength, this will be used to determine
								// total
		// cost of the mst
		for (Edge o : mst) { // creates an advanced for loop that will go through each edge of the mst list
			totalLength = totalLength + o.getDistance();// adds each edge distance to total length.
		}
		System.out.println(
				"The minimum spanning tree has a total cost of " + totalLength + " and includes the following edges:"); // prints
																														// heading
																														// to
																														// console
		output.println(
				"The minimum spanning tree has a total cost of " + totalLength + " and includes the following edges:"); // prints
																														// heading
																														// to
																														// file
		for (Edge c : mst) { // creates an advanced for loop that will go through each edge of the mst list
			System.out.println(c.getTail().getAbbrev() + "-" + c.getHead().getAbbrev());// prints the head and
			// tails of the node to the consoles, in the order they were discovered
			output.println(c.getTail().getAbbrev() + "-" + c.getHead().getAbbrev());// prints the head and
			// tails of the node to the file, in the order they were discovered

		}
	}

	/**
	 * the method bellow implements prims algorithim, the algorithm sets the key for
	 * all nodes in the graph to the value infinity, it then finds the start node
	 * and sets its value to 0, the algorithm then takes the node list of the graph,
	 * and places it in a priority queue that ranks the nodes from smallest key
	 * value to largest key value. then while that que is not empty, it will take
	 * the node with the smallest key value, remove it from the queue, and checks if
	 * that node has a parent node, if it does, the edge that connects those two
	 * nodes is then added, to the mst arraylist(this will be the list returned by
	 * the method). the algorithm then finds all the head nodes connected to the
	 * outgoing edges of that node, and sets the key value of the head nodes equal
	 * to the edge distance of the edge that connects the two nodes, as long as the
	 * current key value of the node is larger than then the edge value. it will do
	 * this over and over, tell the priority queue is empty
	 * 
	 * @param nodeList.  an arraylist of nodes
	 * @param edgeGraph. and array list of edges
	 * @return the mst of the graph, in the form of an arraylist of edges.
	 */
	public static ArrayList<Edge> prims(ArrayList<Node> nodeList, ArrayList<Edge> edgeGraph) {
		ArrayList<Edge> mst = new ArrayList<Edge>();// creates arraylist of edges called mst
		// this will be where we store the edges for are minimum spanning tree.
		ArrayList<Edge> graphEdge = edgeGraph;// creates an arraylist of edges that holds the edges
		// of the graph
		int infinity = Integer.MAX_VALUE;// creates variable infinity that is set to the maximum integer value
		for (Node q : nodeList) {// for each vertex u of G.V
			q.setKey(infinity);// u.key = infinity
			q.setParentNode(null);// u.pi = NIL
		}
		findStart(nodeList).setKey(0);// r.key = 0
		PriorityQueue<Node> Q = new PriorityQueue<Node>(new CompareKeys());// Q = Ø
		for (Node w : nodeList) {// for each vertex u of G.V
			Q.add(w);// INSERT(Q, u)
		}
		while (Q.size() != 0) {// while Q not equal to 0
			Node u = Q.remove();// u = EXTRACT-MIN(Q)
			if (u.getParentNode() != null) {// if the nodes parent node is not equal to null
				mst.add(edgeFinder(u, u.getParentNode(), graphEdge));// add edge that
				// makes up the node and parent node to the the mst.
			}
			for (Node e : adjacent(u)) { // for each vertex v in G.Adj[u]
				if (memberOfQueue(Q, e) && edgeWeight(u, e, graphEdge) < e.getKey()) {// if v of Q and w(u, v) < v.key
					Q.remove(e);// removes node from queue
					e.setParentNode(u);// v.pi = u
					e.setKey(edgeWeight(u, e, graphEdge));// v.key = w(u, v)
					Q.add(e);// adds node to queue

				}
			}
		}
		return mst;

	}

	/**
	 * this method finds the starting node for the graph
	 * 
	 * @param nodelist an array list of nodes
	 * @return the node whos value is equal to s
	 */
	public static Node findStart(ArrayList<Node> nodelist) {
		Node start = null; // creates node variable called start and sets it to null
		String s = "s"; // creates a string variable called s and sets it equal to s
		for (Node d : nodelist) {// an advanced for loop that will go through each item of the nodelist
			if (d.getValue().compareToIgnoreCase(s) == 0) {// if the value of the node is equal to s
				start = d;// start node is set equal to node
			}
		}
		return start;// return node start
	}

	/**
	 * sets all the nodes passed in the argument to white.
	 * 
	 * @param m node arraylist
	 */

	public static void setToWhite(ArrayList<Node> m) {
		for (int i = 0; i < m.size(); i++) {// for loop that will loop for the length of the arraylist passed
			m.get(i).setColor("WHITE");// sets the color of the nod to white
			m.get(i).setParentNode(null);// sets the parent node to null
		}
	}

	/**
	 * this is a simple comparator class that will be used to sort edges from
	 * smalles to largest.
	 * 
	 * @author sam vossen
	 *
	 */

	public static class CompareEdges implements Comparator<Edge> {// creates a comparator class called compareEdges

		@Override
		public int compare(Edge o1, Edge o2) {
			// TODO Auto-generated method stub
			return compareTo(o1, o2); // calls comparTo method
		}

		public static int compareTo(Edge e1, Edge e2) {
			if (e1.getDistance() > e2.getDistance()) {// compares if edge e1 is larger than edge e2, if so returns 1
				return 1;
			}
			if (e1.getDistance() < e2.getDistance()) {// compares if edge e1 is smaller than edge e2, if so returns -1
				return -1;
			}
			String a = e1.getHead().getAbbrev(); // gets the abbreviation for the head node of edge e1
			String b = e2.getHead().getAbbrev(); // gets the abbreviation for the head node of edge e2
			return a.compareToIgnoreCase(b); // compare the two abbreviations and returns either a positive, negative, 0

		}
	}

	/**
	 * this method checks to see if an arraylist of nodes has any nodes whose color
	 * is still white
	 * 
	 * @param nodeList an arraylist of nodes
	 * @return returns true the list still has white nodes, and false if it doesnt.
	 */
	public static boolean hasWhites(ArrayList<Node> nodeList) {
		boolean b = false;// creates a boolean variable called b and sets it to false
		for (Node a : nodeList) {// creates an advanced for loop that will loop through each node in the nodelist
									// passed
			if (a.getColor().compareToIgnoreCase("WHITE") == 0) {// if node a is white
				b = true;// b is set equal to true
				break;// break loop
			}
		}
		return b;// return b
	}

	/**
	 * this is a simple comparator that is used to sort nodes from smallest key
	 * value to largest key value, if the keys are the same it will sort the nodes
	 * alphabetically by abreviation
	 * 
	 * @author sam vossen
	 *
	 */
	public static class CompareKeys implements Comparator<Node> {// creates a comparator class called compareEdges

		@Override
		public int compare(Node o1, Node o2) {
			// TODO Auto-generated method stub
			return compareTo(o1, o2); // calls comparTo method
		}

		public static int compareTo(Node e1, Node e2) {
			if (e1.getKey() > e2.getKey()) {// compares if node e1 is larger than node e2, if so returns 1
				return 1;
			}
			if (e1.getKey() < e2.getKey()) {// compares if node e1 is smaller than node e2, if so returns -1
				return -1;
			}
			return e1.getAbbrev().compareToIgnoreCase(e2.getAbbrev());// returns either 1, -1, or zero, based on
			// Alphabetical order

		}
	}

	/**
	 * this method is used create a node list of all the nodes adjacent to the node
	 * passed in the argument
	 * 
	 * @param node. a node
	 * @return an arraylist of nodes that are adjacent.
	 */
	public static ArrayList<Node> adjacent(Node node) {
		ArrayList<Node> r = new ArrayList<Node>();// creates new arraylist of nodes called r
		ArrayList<Edge> t = new ArrayList<Edge>();// creates new arraylist of nodes called t
		t = node.getOutgoingEdges();// gets all the outgoing edges of nodes and adds them to edge arraylist t
		for (Edge y : t) {// for each edge in the edge arraylist t
			r.add(y.getHead());// add the head node of the edge to node arraylist r.
		}
		return r;// returns node arraylist r.

	}

	/**
	 * this method fins if a node is currently part of queue
	 * 
	 * @param queue. a node priority queue
	 * @param node.  a node object
	 * @return true if the node is part of the queque, false if it is not.
	 */
	public static boolean memberOfQueue(PriorityQueue<Node> queue, Node node) {
		PriorityQueue<Node> queueCopy = queue;// copies the queue passed to a new node priority queue called
		// queue copy
		for (Node u : queueCopy) {// for each node in the queue
			if (node.equals(u)) {// if the node is equal to the node passed in the argument
				return true;// return true
			}
		}
		return false;// return false
	}

	/**
	 * this method finds the distance of the edge that connects the two nodes
	 * passed.
	 * 
	 * @param a. a node object.
	 * @param b. a node object.
	 * @param c. an arraylist of edges
	 * @return the distance of the edge that connects the two nodes.
	 */
	public static int edgeWeight(Node a, Node b, ArrayList<Edge> c) {
		for (Edge o : c) {// for each edge in the edge list
			if (o.getTail().getAbbrev().compareToIgnoreCase(a.getAbbrev()) == 0
					&& o.getHead().getAbbrev().compareToIgnoreCase(b.getAbbrev()) == 0) {
				// if the abbreviation of the tail node is the same as the abbreviation for node
				// a
				// and the abbreviation of the head node is the same as the abbreviation of node
				// b
				return o.getDistance();// return the distance of the edge
			}
		}
		return 0;// return 0
	}

	/**
	 * this method finds the edge of that is made of the two nodes.
	 * 
	 * @param a. a node object.
	 * @param b. a node object.
	 * @param c. an arraylist of edges
	 * @return the edge that is made of the two nodes.
	 */
	public static Edge edgeFinder(Node a, Node b, ArrayList<Edge> c) {
		for (Edge o : c) {// for each edge in edge arraylist c
			if (o.getTail().getAbbrev().compareToIgnoreCase(a.getAbbrev()) == 0
					&& o.getHead().getAbbrev().compareToIgnoreCase(b.getAbbrev()) == 0) {
				// if the abbreviation of the tail node is the same as the abbreviation for node
				// a
				// and the abbreviation of the head node is the same as the abbreviation of node
				// b
				return o;// return the edge
			}
		}
		return null;// return null
	}

}
