package test;

import java.util.*;

public class Main {

    public static void main(String[] args){

        /*
        Node A = new Node();
        Node B = new Node();
        Node C = new Node();
        Node D = new Node();
        Node E = new Node();
        Node F = new Node();
        Node G = new Node();

        A.neighbours = new Node[]{B, D, E};
        B.neighbours = new Node[]{A, D, C};
        C.neighbours = new Node[]{B, G};
        D.neighbours = new Node[]{A, B, E, F};
        E.neighbours = new Node[]{A, D};
        F.neighbours = new Node[]{D};
        G.neighbours = new Node[]{C};*/

        Map<Character, Node> graph = new HashMap<Character, Node>(){{

            put('A', new Node("White", new Character[]{'B','D','E'}));
            put('B', new Node("White", new Character[]{'A', 'D', 'C'}));
            put('C', new Node("White", new Character[]{'B', 'G'}));
            put('D', new Node("White", new Character[]{'A', 'B', 'E', 'F'}));
            put('E', new Node("White", new Character[]{'A', 'D'}));
            put('F', new Node("White", new Character[]{'D'}));
            put('G', new Node("White", new Character[]{'C'}));

        }};



        /*
        Node[] graph = new Node[]{
                new Node('A', "White", new Character[]{'B','D','E'}),
                new Node('B', "White", new Character[]{'A', 'D', 'C'}),
                new Node('C', "White", new Character[]{'B', 'G'}),
                new Node('D', "White", new Character[]{'A', 'B', 'E', 'F'}),
                new Node('E', "White", new Character[]{'A', 'D'}),
                new Node('F', "White", new Character[]{'D'}),
                new Node('G', "White", new Character[]{'C'})
        };*/

        List<Node> visited = bfs(graph, 'A');

        for(int i = 0; i < visited.size(); i++){

            System.out.print("The neighbours of node " + i + " are:");

            for(Character c: visited.get(i).neighbours){
                System.out.print(" " + c);
            }

            System.out.println();

        }

    }

    public static List<Node> bfs(Map<Character, Node> graph, Character vertex){

        Queue<Node> queue = new LinkedList<Node>();
        List<Node> visited = new ArrayList<Node>();

        queue.add(graph.get(vertex));

        while(!queue.isEmpty()){

            Node currentNode = queue.remove();
            System.out.println("Removed node from the queue with the neighbours: ");

            for(Character c: currentNode.neighbours){
                System.out.println(c);
            }

            currentNode.colour = "Black";
            visited.add(currentNode);

            for(Character neighbourLabel: currentNode.neighbours){

                Node neighbour = graph.get(neighbourLabel);

                if(neighbour.colour.equals("White")){

                    queue.add(neighbour);
                    neighbour.colour = "Grey";

                }

            }

        }

        return visited;

    }

}

/*
class Node {

    public String colour;
    public Node[] neighbours;

    //All nodes are white by default
    public Node(){
        colour = "White";
    }

}*/


class Node {

    public String colour;
    public Character[] neighbours;

    public Node(String colour, Character[] neighbours){
        this.colour = colour;
        this.neighbours = neighbours;
    }

}

/*
class Node {

    public char label;
    public String colour;
    public Character[] neighbours;

    public Node(char label, String colour, Character[] neighbours){
        this.label = label;
        this.colour = colour;
        this.neighbours = neighbours;
    }

}*/