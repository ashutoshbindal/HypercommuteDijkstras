package com.gtfs;

import java.io.*;
import java.util.*;

public class DijkstraShortestPath {

    public static ArrayList<stops> node = new ArrayList<>();

    public Map<Vertex<Integer>,Integer> shortestPath(Graph<Integer> graph, Vertex<Integer> sourceVertex){

        //heap + map data structure
        BinaryMinHeap<Vertex<Integer>> minHeap = new BinaryMinHeap<>();

        //stores shortest distance from root to every vertex
        Map<Vertex<Integer>,Integer> distance = new HashMap<>();

        //stores parent of every vertex in shortest distance
        Map<Vertex<Integer>, Vertex<Integer>> parent = new HashMap<>();

        //initialize all vertex with infinite distance from source vertex
        for(Vertex<Integer> vertex : graph.getAllVertex()){
            minHeap.add(Integer.MAX_VALUE, vertex);
        }

        //set distance of source vertex to 0
        minHeap.decrease(sourceVertex, 0);

        //put it in map
        distance.put(sourceVertex, 0);

        //source vertex parent is null
        parent.put(sourceVertex, null);

        //iterate till heap is not empty
        while(!minHeap.empty()){
            //get the min value from heap node which has vertex and distance of that vertex from source vertex.
            BinaryMinHeap<Vertex<Integer>>.Node heapNode = minHeap.extractMinNode();
            Vertex<Integer> current = heapNode.key;

            //update shortest distance of current vertex from source vertex
            distance.put(current, heapNode.weight);

            //iterate through all edges of current vertex
            for(Edge<Integer> edge : current.getEdges()){

                //get the adjacent vertex
                Vertex<Integer> adjacent = getVertexForEdge(current, edge);

                //if heap does not contain adjacent vertex means adjacent vertex already has shortest distance from source vertex
                if(!minHeap.containsData(adjacent)){
                    continue;
                }

                //add distance of current vertex to edge weight to get distance of adjacent vertex from source vertex
                //when it goes through current vertex
                int newDistance = distance.get(current) + edge.getWeight();

                //see if this above calculated distance is less than current distance stored for adjacent vertex from source vertex
                if(minHeap.getWeight(adjacent) > newDistance) {
                    minHeap.decrease(adjacent, newDistance);
                    parent.put(adjacent, current);
                }
            }
        }
        return distance;
    }

    private Vertex<Integer> getVertexForEdge(Vertex<Integer> v, Edge<Integer> e){
        return e.getVertex1().equals(v) ? e.getVertex2() : e.getVertex1();
    }
    
    public static void main(String args[]) throws IOException{

        //Nodes
        InputStream is = DijkstraShortestPath.class.getResourceAsStream("/stops.txt");
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
//        BufferedReader br = new BufferedReader(new FileReader("/stops.txt"));

        String line = "";
        // ArrayList<stops> node = new ArrayList<>();

        line = br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] temp = line.split(",");
            stops s = new stops(temp[0], temp[1], temp[2], Float.parseFloat(temp[3]), Float.parseFloat(temp[4]));
            node.add(s);
        }
        br.close();

        //Calendar dates
        InputStream is2 = DijkstraShortestPath.class.getResourceAsStream("/calendar_dates.txt");
        BufferedReader br2= new BufferedReader(new InputStreamReader(is2));
//        BufferedReader br2 = new BufferedReader(new FileReader("/calendar_dates.txt"));

        String line2 = "";
        line2 = br2.readLine();
        ArrayList<calendar> cal = new ArrayList<>();

        while((line2 = br2.readLine()) != null)
        {
            String[] temp = line2.split(",");
            calendar ca = new calendar(temp[0], temp[1]);
            cal.add(ca);
        }
        br2.close();
        
        Scanner scanner = new Scanner(System.in);
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter the source stop id:");
        String src = scanner.nextLine();

        System.out.println("Enter the destination stop id:");
        String dest = scanner.nextLine();

        System.out.println("Enter the number of hops:");
        int hops = scan.nextInt();

        System.out.println("Enter the time of departure:");
        String dept = scanner.nextLine();
        String[] temp_time = dept.split(":");
        long dept_time = (Long.parseLong(temp_time[0])*3600) + (Long.parseLong(temp_time[1])*60) + Long.parseLong(temp_time[2]);

        boolean serv = false;
        String service_id = "";
        while(!serv)
        {
            System.out.println("Enter the date of travel: ");
            String travel_date = scanner.nextLine();
            for(int i=0; i<cal.size(); i++)
            {
                if(cal.get(i).date.equals(travel_date))
                {
                    serv = true;
                    service_id = cal.get(i).service_id;
                    break;
                }
            }
            if(!serv)
            {
                System.out.println("There is no service available on this day. Try some other time!");
            }
        }

        /*
        public static ArrayList<graphStops>[] graph = new ArrayList[10000];
        */



        //start time
        long start_time = System.nanoTime();

        //list of all valid trip id's
        InputStream is3 = DijkstraShortestPath.class.getResourceAsStream("/trips.txt");
        BufferedReader br3= new BufferedReader(new InputStreamReader(is3));
//        BufferedReader br3 = new BufferedReader(new FileReader("/trips.txt"));
        ArrayList<String> valid_trip = new ArrayList<>();
        String line3 = "";
        line3 = br3.readLine();
        while((line3 = br3.readLine()) != null)
        {
            String[] temp = line3.split(",");
            if(temp[1].equals(service_id))
            {
                valid_trip.add(temp[2]);
            }
        }

        br3.close();

        Graph<graphStops> graph = new Graph<>(false);

        //Edges
        InputStream is1 = DijkstraShortestPath.class.getResourceAsStream("/stop_times.txt");
        BufferedReader br1= new BufferedReader(new InputStreamReader(is1));

        String line1 = "";
        line1 = br1.readLine();
        String id = "";
        String stopId = "";
        long prev_time = 0;
        long d_time = 0;
        graphStops prev_stop = new graphStops("", "", 0);
        while((line1 = br1.readLine()) != null)
        {
            String[] temp = line1.split(",");
            String swap = temp[3];
            temp[3] = temp[1];
            temp[1] = swap;

            if(!valid_trip.contains(temp[0]))
            {
                continue;
            }
            if(id == null)
            {
                id = temp[0];
                stopId = temp[1];
                if(temp[3] != null)
                {
                    String[] t = temp[3].split(":");
                    d_time = (Long.parseLong(t[0])*3600) + (Long.parseLong(t[1])*60) + Long.parseLong(t[2]);
                }
                graphStops temp_obj = new graphStops(stopId, id, d_time);
                prev_stop = temp_obj;
            }
            else if(id.equals(temp[0]))
            {
                // int i=0;
                // for(; i<node.size(); i++)
                // {
                //     if(node.get(i).stop_id.equals(stopId))
                //         break;
                // }
                // ArrayList<graphStops> toCheck = new ArrayList<>();
                // toCheck = graph[i];
                // boolean flag = false;
                // for(int y=0; y<toCheck.size(); y++)
                // {
                //     if(toCheck.get(y).stop_id.equals(temp[1]) && toCheck.get(y).dept_time == d_time)
                //     {
                //         flag = true;
                //         break;
                //     }
                // }
                // if(!flag)
                // {
                    graphStops t = new graphStops(temp[1], id, prev_time);
                    // graph[i].add(t);
                    graph.addEdge(prev_stop, t, d_time - prev_time);
                    prev_stop = t;
                    prev_time = d_time;
                // }
                stopId = temp[1];
            }
            else
            {
                id = temp[0];
                stopId = temp[1];
                if(temp[3] != null)
                {
                    String[] t = temp[3].split(":");
                    d_time = (Long.parseLong(t[0])*3600) + (Long.parseLong(t[1])*60) + Long.parseLong(t[2]);
                    prev_time = d_time;
                }
                graphStops temp_obj = new graphStops(stopId, id, d_time);
                prev_stop = temp_obj;
            }
        }

        br1.close();

        DijkstraShortestPath dsp = new DijkstraShortestPath();
        Vertex<Integer> sourceVertex = graph.getVertex(1);
        Map<Vertex<Integer>,Integer> distance = dsp.shortestPath(graph, sourceVertex);
        System.out.print(distance);

        //end-time
        long end_time = System.nanoTime();
        System.out.println(end_time - start_time);
    }
}