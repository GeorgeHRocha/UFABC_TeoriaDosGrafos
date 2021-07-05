// Problema 6
// GEORGE HARRISON ROCHA
// 11201810309

/*
Este programa implementa o algoritmo de Edmonds Karp e, consequentemente,
o algoritmo de busca e largura. O algoritmo de busca e largura foi
implementado com algumas modificações, entre elas: o método retorna true
se existe caminho entre s e t e false caso contrário. A fim de deixar mais
eficiente, o método bfs() retorna true assim que o vértice t é visitado,
não visitando, portanto, os vértices alcançáveis que ainda não foram visitados.

Para representar a rede residual, utilizou-se arcos virtuais como auxílio.

Primeiramente o programa lê os números 'n', 'm', 's' e 't', que denotam,
respectivamente, o número de vértices, o número de arcos, a fonte e o
sorvedouro. Logo em seguida, cria-se um digrafo ponderado e lê-se a
sequência de pares de vértices com o respectivo peso w. Finalmente,
usando a classe NetworkFlow, calcula-se o fluxo máximo e o fluxo
através de cada arco do digrafo. Enfim, imprime-se o fluxo máximo e os
arcos com seus respectivo fluxos.
*/

package tg.problema6.java; //! TIRAR ISSO ANTES DE ENVIAR
import java.util.Arrays;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Classe Digrafo.
 */
public class Digraph {
    private int V;
    private int E;
    private Arc [] adj;

    /**
     * Construtor do Digrafo.
     * @param V o número de vértices
     */
    public Digraph (int V) {
        this.V = V;
        this.E = 0;
        adj = new Arc[V];
    }

    public int order() {
        return this.V;
    }

    public int size() {
        return this.E;
    }

    public Arc adj(int u) {
        return this.adj[u];
    }

    /**
     * Adiciona um arco ao digrafo, juntamente com seu "gêmeo" virtual.
     * @param e o arco
     */
    public void add_arc(Arc e) {
        e.next = adj[e.tail()];
        adj[e.tail()] = e;

        Arc va = new Arc(e.head(), e.tail(), e.flow, true);
        va.next = adj[va.tail()];
        adj[va.tail()] = va;

        e.twin = va;
        va.twin = e;

        this.E += 1;
    }

    /**
     * Cria um vetor contendo todos os arcos do digrafo.
     * @return um vetor de arcos
     */
    public Arc[] arcs () {
        Arc [] arcs = new Arc[E];

        int k = 0;
        for (int i = 0; i < V; i++) {
            for (Arc no = adj[i]; no != null; no = no.next) {
                if (!no.virtual()) {
                    arcs[k] = no;
                    k++;
                }
            }
        }
        Arrays.sort(arcs, new VerticesCompare());
        return arcs;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());  // Numero de vértices
        Digraph D = new Digraph(n);

        int m = Integer.parseInt(br.readLine());  // Número de arcos;

        int s = Integer.parseInt(br.readLine());  // Fonte
        int t = Integer.parseInt(br.readLine());  // Sorvedouro

        // Leitura dos arcos
        for (int i = 0; i < m; i++) {
            String e[] = br.readLine().split(" ");
            int x = Integer.parseInt(e[0]);
            int y = Integer.parseInt(e[1]);
            int w = Integer.parseInt(e[2]);
            D.add_arc(new Arc(x, y, w));
        }

        // Contrução da rede de fluxo
        NetworkFlow fm = new NetworkFlow(D, s, t);

        System.out.println(fm.maxumum_flow());

        Arc[] arcs = D.arcs();
        for (Arc e: arcs) {
            System.out.println(e);
        }
        br.close();
    }
}

/**
 * Classe arco
 */
class Arc {
    private int tail;
    private int head;
    private boolean virtual;

    int capacity;
    int flow;

    Arc next;
    Arc twin;

    // Construtor de um arco não virtual
    public Arc (int tail, int head, int capacity) {
        set_arc(tail, head, capacity);
        this.flow = 0;
        this.virtual = false;
    }

    // Construtor de um arco
    public Arc (int tail, int head, int capacity, boolean virtual) {
        set_arc(tail, head, capacity);
        this.virtual = virtual;
    }

    /**
     * Método auxiliar para "setar" propriedades comuns de arcos virtuais e arcos não virtuais.
     * @param tail a origem do arco
     * @param head o destino do arco
     * @param capacity a capacidade do arco
     */
    private void set_arc(int tail, int head, int capacity) {
        this.tail = tail;
        this.head = head;
        this.capacity = capacity;
        this.next = null;
        this.twin = null;
    }

    public int tail() {
        return this.tail;
    }

    public int head() {
        return this.head;
    }

    public boolean virtual() {
        return this.virtual;
    }

    @Override
    public String toString() {
        return tail + " " + head + " " + flow;
    }
}

/**
 * Classe que computa uma rede de fluxo.
 */
class NetworkFlow {
    private Digraph D;
    private int s;
    private int t;
    private boolean [] vis;
    private ArrayList<Arc> pred;
    private int maxumum_flow;

    /**
     * Construtor da rede de fluxo.
     * @param D o digrafo, onde os pesos dos arcos representam a capacidade
     * @param s a fonte
     * @param t o sorvedouro
     */
    public NetworkFlow (Digraph D, int s, int t) {
        this.D = D;
        this.s = s;
        this.t = t;
        vis = new boolean[D.order()];
        pred = new ArrayList<Arc>(D.order());
        this.maxumum_flow = edmonds_karp();
    }

    public int maxumum_flow() {
        return this.maxumum_flow;
    }

    /**
     * Método que extrai a capacidade mínima do caminho aumentador.
     * @param p o caminho aumentador
     * @return a capacidade mínima do caminho
     */
    private int min(ArrayList <Arc> p) {
        int menor = capacity(p.get(0));
        for (Arc e: p) {
            int atual = capacity(e);
            if (atual < menor) {
                menor = atual;
            }
        }
        return menor;
    }

    /**
     * Recupera o caminho encontrado pela bfs na rede residual.
     * @return um ArrayList contendo os arcos do caminho de s à t
     */
    private ArrayList <Arc> path() {
        ArrayList <Arc> path = new ArrayList <Arc>();

        Arc p = pred.get(t);
        while (p != pred.get(s)) {
            path.add(0, p);
            p = pred.get(p.tail());
        }

        return path;
    }

    /**
     * Implementação do algoritmo de Edmonds Karp.
     * @return um inteiro que representa o fluxo máximo do digrafo D
     */
    public int edmonds_karp() {
        int valor = 0;
        ArrayList <Arc> p;
        while (bfs()) {
            p = path();
            int delta = min(p);
            valor += delta;
            for (Arc e: p) {
                if (!e.virtual()) {
                    e.flow += delta;
                    e.twin.capacity += delta;
                } else {
                    e.twin.flow -= delta;
                    e.capacity -= delta;
                }
            }
        }
        return valor;
    }

    /**
     * Implementação do algoritmo de busca e largura,
     * adaptado para verificar se existe caminho de s a t na rede residual.
     * @return true se existe caminho entre s e t e false caso contrário
     */
    private boolean bfs () {
        pred.clear();
        for (int i = 0; i < D.order(); i++) {
            vis[i] = false;
            pred.add(null);
        }
        vis[s] = true;
        ArrayList <Integer> fila = new ArrayList <Integer>();
        fila.add(s);

        while (fila.size() > 0) {
            int u = fila.remove(0);
            for (Arc v = D.adj(u); v != null; v = v.next) {
                if (capacity(v) > 0) {
                    if (!vis[v.head()]) {
                        vis[v.head()] = true;
                        pred.set(v.head(), v);
                        fila.add(v.head());
                    }
                    if (vis[t]) {
                        return true;
                    }
                }
            }
        }
        return vis[t];
    }

    /**
     * Computa a capacidade do arco e na rede residual.
     * @param e o arco
     * @return um inteiro que representa a capacidade do arco e na rede residual
     */
    private int capacity(Arc e) {
        if (!e.virtual()) {
            return e.capacity - e.flow;
        } else {
            return e.twin.flow;
        }
    }
}

/**
 * Classe auxiliar usada para fazer a ordenação dos arcos.
 */
class VerticesCompare implements Comparator <Arc> {

    @Override
	public int compare(Arc e1, Arc e2) {
        if (e1.tail() < e2.tail()) {
			return -1;
		}
        if (e1.tail() > e2.tail()) {
			return 1;
		}

        if (e1.head() < e2.head()) {
        	return -1;
        }
        if (e1.head() > e2.head()) {
			return 1;
		}
        return 0;
    }
}
