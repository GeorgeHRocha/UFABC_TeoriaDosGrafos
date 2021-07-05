package tg.problema3.java;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.Math;
import java.util.Locale;

public class Graph {
    private int V;
    private int E;
    private Point[] vertices;
    private Node[] adj;

    public Graph (int V) {
        this.V = V;
        this.E = 0;
        this.vertices = new Point[V];
        this.adj = new Node[V];
    }

    public int order () {
        return this.V;
    }

    public int size () {
        return this.E;
    }

    public Node adj (int u) {
        return adj[u];
    }

    public void insert_edge(Edge e) {
        Node u = new Node(e.u, e.p);
        Node v = new Node(e.v, e.p);

        u.next = adj[e.v];
        v.next = adj[e.u];

        adj[e.u] = v;
        adj[e.v] = u;

        this.E += 1;
    }

    public void insert_vertex(int i, Point coord) {
        vertices[i] = coord;
    }

    public void build_complete_graph() {
    	for (int u = 0; u < V - 1; u++) {
        	for (int v = u + 1; v < V; v++) {
            	double p = vertices[u].dist(vertices[v]);
            	insert_edge(new Edge(u, v, p));
        	}
    	}
	}

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        Graph G = new Graph(n);

        for (int i = 0; i < n; i++) {
            long x = scan.nextLong();
            long y = scan.nextLong();
            G.insert_vertex(i, new Point(x, y));
        }

        G.build_complete_graph();

        Tree T = new Tree(G);
        Edge []edges = T.edges();

        String s = "comprimento de cabeamento minimo:";
        System.out.printf(Locale.US, "%s %.4f\n", s, T.sum_weight());

        for (int i = 0; i < G.order() - 1; i++) {
            System.out.println(edges[i]);
        }

        scan.close();
    }
}

class Node {
    int w;
    double p;
    Node next;

    public Node (int w, double p) {
        this.w = w;
        this.p = p;
        this.next = null;
    }
}

class Edge {
    int u;
    int v;
    double p;

    public Edge (int u, int v, double p) {
        setEdge(u, v);
        this.p = p;
    }

    public Edge (int u, int v) {
        setEdge(u, v);
        this.p = 0;
    }

    private void setEdge (int u, int v) {
        if (u < v) {
            this.u = u;
            this.v = v;
        } else {
            this.u = v;
            this.v = u;
        }
    }

    @Override
    public String toString() {
        return u + " " + v;
    }
}

class Tree {
    private double []key;
    private int []pred;
    private Graph G;

    public Tree (Graph G) {
        this.key = new double[G.order()];
        this.pred = new int[G.order()];
        this.G = G;
        prim();
    }

    private void prim() {
        for (int i = 0; i < G.order(); i++) {
            key[i] = Double.POSITIVE_INFINITY;
            pred[i] = -1;
        }
        Heap Q = new Heap(G.order(), key);
        Q.init();

        pred[0] = 0;
        key[0] = 0;
        
        int [] visited = new int[G.order()];

        while (Q.heap_size() != 0) {
            int u = Q.extract_min();
            visited[u] = 1;
            for (Node v = G.adj(u); v != null; v = v.next) {
                if (visited[v.w] != 1 && v.p < key[v.w]) {
                    pred[v.w] = u;
                    int j = 0;

                    while (j < Q.heap_size() && v.w != Q.A(j)) {
                        j++;
                    }
                    Q.decrease_key(j, v.p);
                }
            }
        }
    }

    public Edge[] edges () {
        Edge []edges = new Edge[G.order() - 1];
        for (int i = 1; i < G.order(); i++) {
            edges[i - 1] = new Edge(pred[i], i);
        }
        Arrays.sort(edges, new VerticesCompare());
        return edges;
    }

    public double sum_weight() {
        double sum = 0;
        for (int i = 0; i < G.order(); i++) {
            sum += key[i];
        }

        return sum;
    }
}

class Heap {
    private int [] A;
    private int heap_size;
    private int size;
    private double [] key;

    public Heap (int n, double [] key) {
        this.A = new int[n];
        this.heap_size = 0;
        this.size = n;
        this.key = key;
    }

    // public Heap (int n) {
    //     this.A = new int[n];
    //     this.heap_size = 0;
    //     this.size = n;
    //     this.key = this.A;
    // }

    public void init () {
        for (int i = 0; i < this.size; i++) {
            this.A[i] = i;
        }

        this.heap_size = size;
    }

    public int heap_size() {
        return this.heap_size;
    }

    public int [] A() {
        return this.A;
    }

    public int A(int i) {
        return this.A[i];
    }

    private static int left_child(int i) {
        return (2 * i) + 1;
    }

    private static int right_child(int i) {
        return (2 * i) + 2;
    }

    private static int parent(int i) {
        return (i - 1) / 2;
    }

    private void min_heapify(int i) {
        int l = left_child(i);
        int r = right_child(i);

        int min;
        if (l < heap_size() && key[A(l)] < key[A(i)]) {
            min = l;
        } else {
            min = i;
        }
        if (r < heap_size() && key[A(r)] < key[A(min)]) {
            min = r;
        }

        if (min != i) {
            int aux = A[i];
            A[i] = A[min];
            A[min] = aux;
            min_heapify(min);
        }
    }

    public int extract_min() {
        int min = A[0];
        A[0] = A[heap_size() - 1];
        heap_size--;
        min_heapify(0);
        return min;
    }

    private void up_heapify (int i) {
        while (i > 0 && key[A(parent(i))] > key[A(i)]) {
            int aux = A[i];
            A[i] = A[parent(i)];
            A[parent(i)] = aux;
            i = parent(i);
        }
    }

    public void decrease_key (int i, double key) {
        this.key[A(i)] = key;
        up_heapify(i);
    }
    
}

class Point {
    private long x;
    private long y;

    public Point(long x, long y) {
        this.x = x;
		this.y = y;
    }

    public long x() {
        return this.x;
    }

    public long y() {
        return this.y;
    }

    public double dist(Point c) {
		return Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
	}
}

class VerticesCompare implements Comparator <Edge> {

    @Override
	public int compare(Edge e1, Edge e2) {
        if (e1.u < e2.u) {
			return -1;
		}
        if (e1.u > e2.u) {
			return 1;
		}
    
        if (e1.v < e2.v) {
        	return -1;
        }
        if (e1.v > e2.v) { 
			return 1;
		}
        return 0;
    }
}
