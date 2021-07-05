// Problema 1
// George Harrison Rocha
// 11201810309
package tg.problema1.java;
import java.util.Scanner;

/**
 * Classe grafo, onde E denota |E(G)|.
 */
public class Graph  {
    int V;
    int E;
    Node adj[];
    
    /**
     * Classe nó, onde next denota o próximo nó na lista encadeada.
     */
    public class Node {
        int w;
        Node next;

        /**
         * Construtor do nó.
         * @param w o rótulo do vértice
         */
        Node(int w) {
            this.w = w;
            this.next = null;
        }
    }

    /**
     * Classe aresta.
     */
    public class Edge {
        int u;
        int v;

        /**
         * Construtor da aresta.
         * @param u o rótulo do vértice u
         * @param v o rótulo do vértice v
         */
        Edge (int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    /**
     * Construtor do grafo.
     * @param V |V(G)|
     */
    public Graph (int V) {
        this.V = V;
        this.E = 0;
        adj = new Node[V];
    }

    /**
     * Método que testa a partinência de aresta.
     * @param E a aresta uv
     * @return true se a aresta E existe e false caso contrário
     */
    public boolean graph_has_edge (Edge E) {
        for (Node p = adj[E.u]; p != null; p = p.next) {
            if (p.w == E.v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que insere uma aresta no grafo sem duplicidade.
     * @param E a aresta a ser inserida
     */
    public void graph_insert_edge(Edge E) {
        if (!graph_has_edge(E)) {
            Node u = new Node(E.u);
            Node v = new Node(E.v);

            u.next = adj[E.v];
            v.next = adj[E.u];

            adj[E.u] = v;
            adj[E.v] = u;

            this.E += 1;
        }
    }

    /**
     * Método que verifica se é possível realizar o casamento.
     * @return true caso existam três funcionários que mutuamente aceitem contracenar juntos,
     * e false caso contrário
     */
    public boolean vai_ter_casamento () {
        for (int i = 0; i < V; i++) {
            Node ni = adj[i];
            if (ni != null && ni.next != null) {
                while (ni.next != null) {
                    Node nf = ni.next;
                    while (nf != null) {
                        if (graph_has_edge(new Edge(ni.w, nf.w))) {
                            return true;
                        }
                        nf = nf.next;
                    }
                    ni = ni.next;
                }
            }
        }
        return false;
    }

    public static void main(String [] args) {
        Scanner scan = new Scanner(System.in);
        boolean vai_ter = false;

        int n = scan.nextInt();
        int m = scan.nextInt();
        if (m > 0) {
            Graph G = new Graph(n);
            for (int i = 0; i < m; i++) {
                int u = scan.nextInt();
                int v = scan.nextInt();
                G.graph_insert_edge(G.new Edge(u, v));
            }
            vai_ter = G.vai_ter_casamento();
        }
        if (vai_ter) {
            System.out.println("VAI TER CASAMENTO!");
        } else {
            System.out.println("NAO VAI TER CASAMENTO");
        }
        scan.close();
    }
}