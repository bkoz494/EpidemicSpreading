package uksw.graphs;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class App {

    public final static int ENVIRONMENT_SIZE = 1000;  //called L in PDF
    public final static int STREET_DIST = 100; // called dis in PDF
    public final static int PEOPLE_DIST = 50;  // called dist in PDF
    public final static int PEOPLE_AMOUNT = 1000;    // {100, 200, 300, 500}
    public final static int D_START = 2000;    // {0, 5, 10, 20}
    public final static int D_INFECTIOUS = 3000;  // {10, 20, 30, 50, 100}
    public final static int D_RECOVERED = 2000;   //{20, 50, 100, 200, 500}

    SingleGraph myGraph;
    static List<Person> people;
    static Random rand = new Random();

	public App(String[] args) {
        people = new ArrayList<>();

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
        for(Person person : people){
            person.moveToDest();
            if(person.isDestReached()){
                person.newDestination();
            }

            //set random status
            if(rand.nextDouble() < 0.01){
                int tmp = rand.nextInt(4);
                switch(tmp){
                    case 0:
                    person.setStatus(Status.SUSCEPTIBLE);
                        break;
                    case 1:
                    person.setStatus(Status.EXPOSED);
                        break;
                    case 2:
                    person.setStatus(Status.INFECTED);
                        break;
                    case 3:
                    person.setStatus(Status.RECOVERED);
                        break;
                }
            }

            List<Person> surroundingPeople = people.stream()
                    .filter(sp -> {
                        Integer xDiff = Math.abs(sp.getXCoord() - person.getXCoord());
                        Integer yDiff = Math.abs(sp.getYCoord() - person.getYCoord());
                        return xDiff < PEOPLE_DIST && yDiff < PEOPLE_DIST && !sp.equals(person);
                    })
                    .collect(Collectors.toList());

            Map<String, Long> encounteredPeople = person.getEncounteredPeople();
            surroundingPeople.forEach(sp -> encounteredPeople.put(sp.getId(), System.currentTimeMillis()));

            Map<Status, List<Person>> grouped = surroundingPeople.stream()
                    .collect(Collectors.groupingBy(Person::getStatus));

            switch (person.getStatus()) {
                case SUSCEPTIBLE:
                    List<Person> infected = grouped.get(Status.INFECTED);

                    if(infected == null){
                        break;
                    }

                    for(Person i : infected) {
                        Long contactDuration = System.currentTimeMillis() - encounteredPeople.get(i.getId());
                        Double probabilityOfInfection = 1.0 - 1.0/Math.sqrt(contactDuration);

                        if(Math.random() < probabilityOfInfection) {
                            person.setStatus(Status.EXPOSED);
                            person.setStatusChangeTime(System.currentTimeMillis());
                            break;
                        }
                    }
                    break;
                case EXPOSED:
                    if(person.getStatusChangeTime() + D_START < System.currentTimeMillis()){
                        person.setStatus(Status.INFECTED);
                    }
                    break;
                case INFECTED:
                    if(person.getStatusChangeTime() + D_START + D_INFECTIOUS < System.currentTimeMillis()){
                        person.setStatus(Status.RECOVERED);
                        person.setStatusChangeTime(System.currentTimeMillis());
                    }
                    break;
                case RECOVERED:
                    if(person.getStatusChangeTime() + D_RECOVERED < System.currentTimeMillis()){
                        person.setStatus(Status.SUSCEPTIBLE);
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new App(args);
        while(true) refresh();
    }
}
