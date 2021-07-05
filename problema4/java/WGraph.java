// Problema 4
// George Harrison Rocha
// 11201810309

/*
Este programa implementa o algoritmo de kruskal e a estrutura Find_Set.
A classe Find_Set contém os métodos set_init, make_set, find_set e 
union, cuja implementação envolve união ponderada e compressão de caminhos.
A classe WGraph contém os atributos V, que representa a quantidade de vértices;
E, que representa a quantidade de arestas; vertices, um vetor com as
coordenadas dos vértices; e adj, a lista de adjacencias.

Primeiramente o programa lê um número inteiro n, que representa o 
número de vértices. Após isso, cria-se um grafo e lê-se as coordenadas
de seus vértices. Com todas as coordenadas, constrói-se um grafo completo
e aplica-se o algoritmo de kruskal. Com o vetor de arestas gerado pelo
algoritmo de kruskal, imprime-se o a soma dos pesos dessas arestas e,
finalmente, as arestas.
*/
package tg.problema4.java;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.Math;
import java.util.Locale;

/**
 * Classe grafo, onde E denota |E(G)|.
 */
public class WGraph {
    int V;
    int E;
    private Point [] vertices;  // Vetor que guarda as coordenadas dos vértices
    private Node []adj;  // Lista adjacencias

    /**
     * Construtor do grafo.
     * @param V |V(G)|
     */
    public WGraph (int V) {
        this.V = V;
        this.E = 0;
        adj = new Node[V];
        vertices = new Point[V];
    }
    
    /**
     * Classe nó, onde next denota o próximo nó na lista encadeada.
     */
    class Node {
    	int w;
    	double p;
    	Node next;
        
        /**
         * Construtor do nó.
         * @param w o rótulo do vértice
         * @param p o peso
         */
    	Node (int w, double p) {
    		this.w = w;
    		this.p = p;
    		this.next = null;
    	}
    }

    /**
     * Classe aresta, onde o rótulo de u é menor que o rótulo de v.
     */
    class Edge {
        int u;
        int v;
        double p;

        /**
         * Construtor da aresta.
         * @param u o rótulo do vértice u
         * @param v o rótulo do vértice v
         * @param p o peso da aresta uv
         */
        Edge (int u, int v, double p) {
        	if (u < v) {
            	this.u = u;
            	this.v = v;
            } else {
            	this.u = v;
            	this.v = u;
            }
            this.p = p;
        }

        @Override
        public String toString() {
            return u + " " + v;
        }
    }

    /**
     * Método que insere uma aresta no grafo.
     * @param e a aresta a ser inserida
     */
    public void insert_edge(Edge e) {
            Node u = new Node(e.u, e.p);
            Node v = new Node(e.v, e.p);

            u.next = adj[e.v];
            v.next = adj[e.u];

            adj[e.u] = v;
            adj[e.v] = u;

            this.E += 1;
    }

    /**
     * Método que insere um vértice no grafo.
     * @param i o rótulo do vértice
     * @param coord a coordenada do vértice
     */
    public void insert_vertex(int i, Point coord) {
        vertices[i] = coord;
    }
    
    /**
     * Método que constrói um grafo completo a partir 
     * das coordenadas dos vértices.
     */
    public void build_complete_graph() {
    	for (int u = 0; u < V - 1; u++) {
        	for (int v = u + 1; v < V; v++) {
            	double p = vertices[u].dist(vertices[v]);
            	insert_edge(new Edge(u, v, p));
        	}
    	}
	}

    /**
     * Método que constrói um vetor das arestas de G.
     * @return um vetor de arestas
     */
    public Edge[] edges() {
    	Edge [] edges = new Edge[E];

        int k = 0;
    	for (int i = 0; i < V; i++)  {
    		for (Node no = adj[i]; no != null; no = no.next) {
    			if (no.w > i) {
    				edges[k] = new Edge(i, no.w, no.p);
                    k++;
    			}
    		}
    	}
        return edges;
    }

    /**
     * Método auxiliar que soma os pesos da arestas.
     * @param edges o vetor de arestas
     * @param E o tamanho do vetor de arestas
     * @return a soma dos pesos das arestas
     */
    public double sum_weight(Edge[] edges, int E) {
        double sum = 0;
        for (int i = 0; i < E; i++) {
            sum += edges[i].p;
        }

        return sum;
    }

    /**
     * Método que implementa o algoritmo de kruskal.
     * @return o vetor de arestas da árvore geradora mínima
     */
    public Edge[] kruskal() {
        Edge [] H = new Edge[V - 1];
        Edge [] F = edges();
        Arrays.sort(F, new WeightCompare());

        Union_Find S = new Union_Find(V);
        
        int k = 0;
        for (int i = 0; i < E; i++) {
            int u = F[i].u;
            int v = F[i].v;
            if (S.find_set(u) != S.find_set(v)) {
                S.union(u, v);
                H[k] = F[i];
                k++;
            }
        }
        
        Arrays.sort(H, new VerticesCompare());
        return H;
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int n = scan.nextInt();
        WGraph G = new WGraph(n);
        
        for (int i = 0; i < n; i++) {
            long x = scan.nextLong();
            long y = scan.nextLong();
            G.insert_vertex(i, new Point(x, y));
        }

        G.build_complete_graph();

        Edge [] edges = G.kruskal();

        String s = "comprimento de cabeamento minimo:";
        System.out.printf(Locale.US, "%s %.4f\n", s, G.sum_weight(edges, G.V - 1));

        for (Edge e: edges) {
            System.out.println(e);
        }

        scan.close();
    }
}

/**
 * Classe Union_Find.
 */
class Union_Find {
     private Subset [] leader;

     /**
      * Construtor da estrutura Union_Find.
      * @param V a quantidade de elementos
      */
    Union_Find(int V) {
        leader = new Subset[V];
        set_init(V);
    }
    /**
     * Classe subset, onde rank é a altura do grupo
     * e parent é o representante do grupo.
     */
    private class Subset {
        int parent;
        int rank;

        /**
         * Construtor do subset.
         * @param i o elemento i
         */
        Subset(int i) {
            this.parent = i;
            rank = 0;
        }
    }

    /**
     * Método que cria n conjuntos.
     * @param n quantidade de elementos.
     */
    private void set_init(int n) {
        for (int i = 0; i < n; i++) {
            make_set(i);
        }
    }

    /**
     * Método make_set.
     * @param i o elemento i
     */
    private void make_set(int i) {
        leader[i] = new Subset(i);
    }

    /**
     * Método find_set, implementada com compressão de caminhos.
     * @param i o elemento i
     * @return o representante do conjunto que contém i
     */
    public int find_set(int i) {
        if (i != leader[i].parent) {
            leader[i].parent = find_set(leader[i].parent);
        }
        return leader[i].parent;
    }

    /**
     * Método union, implementada com união ponderada.
     * @param x o elemento x
     * @param y o elemento y
     */
    public void union(int x, int y) {
        x = find_set(x);
        y = find_set(y);

        if (leader[x].rank > leader[y].rank) {
            leader[y].parent = x;
        } else {
            leader[x].parent = y;
            if (leader[x].rank == leader[y].rank) {
                leader[y].rank++;
            }
        }
    }
}

/**
 * Classe ponto, onde x e y representam coordenadas no plano cartesiano.
 */
class Point {
	long x;
	long y;
	
    /**
     * Constrututor do ponto.
     * @param x
     * @param y
     */
	Point(long x, long y) {
		this.x = x;
		this.y = y;
	}
	
    /**
     * Método que calcula a distância entre dois pontos.
     * @param c um ponto no plano cartesiano
     * @return a distância entre os pontos c e o ponto (x, y)
     */
	public double dist(Point c) {
		return Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
	}
}

/**
 * Classe auxiliar para ordenar arestas por peso.
 */
class WeightCompare implements Comparator <WGraph.Edge> {

    @Override
	public int compare(WGraph.Edge e1, WGraph.Edge e2) {
        if (e1.p < e2.p) return -1;
        if (e1.p > e2.p) return 1;
        else return 0;
    }
}

/**
 * Classe auxiliar para ordenar arestas pelos vértices.
 */
class VerticesCompare implements Comparator <WGraph.Edge> {

    @Override
	public int compare(WGraph.Edge e1, WGraph.Edge e2) {
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
