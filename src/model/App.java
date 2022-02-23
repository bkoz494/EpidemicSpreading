package model;
import java.util.ArrayList;
import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class App {

    public final static int ENVIRONMENT_SIZE = 1000;  //called L in PDF
    public final static int STREET_DIST = 100; // called dis in PDF
    public final static int PEOPLE_DIST = 50;  // called dist in PDF
    public final static int PEOPLE_AMOUNT = 100;    // {100, 200, 300, 500}
    public final static int D_START = 0;    // {0, 5, 10, 20}
    public final static int D_INFECTIOUS = 10;  // {10, 20, 30, 50, 100}
    public final static int D_RECOVERED = 20;   //{20, 50, 100, 200, 500}

    SingleGraph myGraph;
    static ArrayList<Person> people;
    static Random rand = new Random();

	public App(String[] args) {
        people = new ArrayList<Person>();

        //create grid
        myGraph = Tools.grid(ENVIRONMENT_SIZE, STREET_DIST);
        ArrayList<Node> crossroads = new ArrayList<>(myGraph.getNodeSet());
        //add people to grid
        for(int i=0; i<PEOPLE_AMOUNT; i++){
            String id = String.valueOf(i);
            Person p = new Person(id, myGraph, crossroads);
            people.add(p);
        }

        myGraph.display(false);
    }

    public static void refresh() {
        Tools.pause(10);
        for(Person p : people){
            p.moveToDest();
            if(p.isDestReached()){
                p.newDestination();
            }

            //set random status
            if(rand.nextDouble() < 0.01){
                int tmp = rand.nextInt(4);
                switch(tmp){
                    case 0:
                    p.setStatus(Status.SUSCEPTIBLE);
                        break;
                    case 1:
                    p.setStatus(Status.EXPOSED);
                        break;
                    case 2:
                    p.setStatus(Status.INFECTED);
                        break;
                    case 3:
                    p.setStatus(Status.RECOVERED);
                        break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new App(args);
        while(true) refresh();
    }
}
