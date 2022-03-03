package uksw.graphs;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Tools {
    
    public static void pause(long delay) {
		try {
			Thread.sleep(delay);
		} catch(InterruptedException ie) {}
	}

    public static SingleGraph grid(int environmentSize, int edgeDist) {
		SingleGraph myGraph = new SingleGraph("grid von Neumann or Moore");
		// creation of nodes
		int line =0;
		while(line <= environmentSize){
			int col = 0;
			while(col <= environmentSize){
				Node v = myGraph.addNode(line+"-"+col);
				v.addAttribute("x",col);
				v.addAttribute("y",line);
				v.addAttribute("ui.style", "size:1px;");
				col += edgeDist;
			}
			line += edgeDist;
		}

		// creation of edges for von Neumann neighborhood
		int val = 1;
		line = 0;
		while(line <= environmentSize){
			int col = 0;
			while(col <= environmentSize){
				int nextcol = col+edgeDist;
				if(nextcol > environmentSize) break;
				Node v = myGraph.getNode(line+"-"+col);
				Node w = myGraph.getNode(line+"-"+nextcol);
				Edge e = myGraph.addEdge(v.getId()+"_"+w.getId(),v.getId(),w.getId());
				col += edgeDist;
			}
			line += edgeDist;
		}

		line = 0;
		while(line <=environmentSize){
			int col = 0;
			while(col <= environmentSize){
				int nextline = line+edgeDist;
				if(nextline > environmentSize) break;
				Node v = myGraph.getNode(line+"-"+col);
				Node w = myGraph.getNode(nextline+"-"+col);
				Edge e = myGraph.addEdge(v.getId()+"_"+w.getId(),v.getId(),w.getId());
				col += edgeDist;
			}
			line += edgeDist;
		}
		return myGraph;
	}
}
