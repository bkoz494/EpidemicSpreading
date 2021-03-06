package uksw.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Person {
    private Node node;
    private int xDest, yDest;
    private Status status;
    private Long statusChangeTime;
    private int xCoord, yCoord;
    private String id;
    private Random rand;
    private SingleGraph myGraph;
    private ArrayList<Node> destSet;
    private Map<String, Long> encounteredPeople = new HashMap<>();
    private int velocity;

    public Person(String id, SingleGraph myGraph, ArrayList<Node> destSet){
        rand = new Random();
        newVelocity();
        this.id = id;
        this.myGraph = myGraph;
        this.destSet = destSet;

        node = myGraph.addNode(id);
        status = Status.SUSCEPTIBLE;
        setStatusChangeTime(System.currentTimeMillis());
        node.addAttribute("ui.style", "fill-color:green; size:10px;");

        //set coordinates
        xCoord = rand.nextInt(App.ENVIRONMENT_SIZE);
        yCoord = rand.nextInt(App.ENVIRONMENT_SIZE);

        if(rand.nextDouble() < 0.5){
            xCoord = xCoord - (xCoord%App.STREET_DIST);
        }
        else{
            yCoord = yCoord - (yCoord%App.STREET_DIST);
        }
        node.addAttribute("x", xCoord);
        node.addAttribute("y", yCoord);

        //set destination
        int destSetSize = destSet.size();
        Node dest = destSet.get(rand.nextInt(destSetSize));
        xDest = dest.getAttribute("x");
        yDest = dest.getAttribute("y");
    }

    public void newDestination(){
        //set destination
        int destSetSize = destSet.size();
        Node dest = destSet.get(rand.nextInt(destSetSize));
        xDest = dest.getAttribute("x");
        yDest = dest.getAttribute("y");
        newVelocity();
    }
    private void newVelocity(){
        velocity = rand.nextInt(3)+1;
    }

    public boolean isDestReached(){
        if((xCoord == xDest)&&(yCoord == yDest)){
            return true;
        }
        return false;
    }

    public void moveToDest(){
        if(Math.abs(xCoord-xDest) < velocity) xCoord = xDest;
        if(Math.abs(yCoord-yDest) < velocity) yCoord = yDest;

            //if x coord is closer to xDest than y coord but x is still not equal xDest, then set proper x coord first
        if(((Math.abs(xCoord-xDest) < Math.abs(yCoord-yDest)) && (xCoord != xDest)) || (yCoord == yDest)){
            if(yCoord%App.STREET_DIST != 0){
                if(yCoord > yDest) yCoord-= velocity;
                else if(yCoord < yDest) yCoord+= velocity;
            }
            else{
                if(xCoord > xDest) xCoord-= velocity;
                else if(xCoord < xDest) xCoord+= velocity;
            }
        }
        else{
            if(xCoord%App.STREET_DIST != 0){
                if(xCoord > xDest) xCoord-= velocity;
                else if(xCoord < xDest) xCoord+= velocity;
            }
            else{
                if(yCoord > yDest) yCoord-= velocity;
                else if (yCoord < yDest) yCoord+= velocity;
            }
        }
        node.addAttribute("x", xCoord);
        node.addAttribute("y", yCoord);
    }

    public int getXCoord(){
        return xCoord;
    }

    public int getYCoord(){
        return yCoord;
    }

    public void setStatus(Status s){
        status = s;
        if(status == Status.SUSCEPTIBLE){
            node.addAttribute("ui.style", "fill-color:green;");
        }
        else if(status == Status.EXPOSED){
            node.addAttribute("ui.style", "fill-color:orange;");
        }
        else if(status == Status.INFECTED){
            node.addAttribute("ui.style", "fill-color:red;");
        }
        else if(status == Status.RECOVERED){
            node.addAttribute("ui.style", "fill-color:blue;");
        }
    }

    public Status getStatus(){
        return status;
    }


    public Long getStatusChangeTime() {
        return statusChangeTime;
    }

    public void setStatusChangeTime(Long statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }

    public String getId() {
        return id;
    }

    public Map<String, Long> getEncounteredPeople() {
        return encounteredPeople;
    }
}
