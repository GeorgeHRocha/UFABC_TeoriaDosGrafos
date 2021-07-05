// Problema 3
// George Harrison Rocha
// 11201810309

/*
Para resolver o problema, eu usei, entre outras estruturas, a estrutura List, e criei um vetor de Lists 
na estutura Graph (lista de adjacencias), onde cada índice, que representa um vértice, guarda sua coordenada 
e o endereço do primeiro nó das adjacencias. Após a leitura de todas as coordenadas, eu usei a função 
build_complete_graph para criar um grafo completo, usando as coordenadas armazenadas no vetor List do grafo 
para calcular as distâncias entre os vértices (ou seja, pesos das arestas). Após isso, usei o algoritmo de Prim, 
implementado com heap binária, para criar a árvore mínima e depois criei um vetor das arestas da árvore retornada 
pela função prim. Para ordenar o vetor de arestas, usei o algoritmo quicksort. Finalmente, imprimo a soma das 
distâncias das arestas da árvore e as arestas.

OBS: As funções de Heap são adaptações dos algoritmos que eu vi em Algoritmos e Estrutura de Dados I.
*/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define INFTY (2000000000)

typedef struct s_node Node;
typedef struct s_coord Coord;
typedef struct s_list List;
typedef struct s_graph WGraph;
typedef struct s_edge WEdge; // Aresta ponderada
typedef struct s_edge Edge; // Aresta não ponderada
typedef struct s_tree Tree;
typedef struct s_heap Heap;

struct s_node {
    int w;
    double p;
    Node *next;
};

struct s_coord {
	long x;
    long y;
};

struct s_list {
    Node *head;
    Coord coord;
};

struct s_graph {
    int V;
    int E;
    List *adj;  // Lista de adjacencias
};

struct s_edge {
    int u;
    int v;
    double p;
};

struct s_tree {
	double *key;
	int *pred;
};

struct s_heap {
    int *A;
    int heap_size;
    int size;
};

// Funções para o plano cartesiano
Coord coord(long, long);
double dist(Coord, Coord);

// Funções para o grafo
WGraph *graph(int);
Node *node(int, double);
void build_complete_graph(WGraph *);

// Funções auxiliares
void exchange_int(int *, int *);
void exchange_edge(Edge *, Edge *);
void start_int(int *, int, int);
void start_double(double *, int, double);
double sum_double(double *, int, int);

// Funções para a árvore
Tree *tree(int);
Tree *prim (WGraph *);

// Funções para Heap
int left_child(int);
int right_child(int);
int parent(int);
Heap *heap(int);
void min_heapify(Heap *, int, double *);
int extract_min(Heap *, double *);
void decrease_key(Heap *, int, double, double *);
void destroy_heap(Heap *);

// Funções para arestas
WEdge edge(int, int, double);
void insert_edge(WGraph *, WEdge);
Edge *edges(WGraph *, Tree *);
void quick_sort_edges(Edge *, int, int , int);
void print_edgesT (Edge *, int, int);

int main() {
    int n;
    scanf("%d", &n);
    WGraph *G = graph(n);

    long x, y;
    for (int i = 0; i < n; i++) {
        scanf("%ld %ld", &x, &y);
        G -> adj[i].coord = coord(x, y);
    }

    build_complete_graph(G);

    Tree *T = prim(G);
    Edge *edgesT = edges(G, T);

    char s[] = "comprimento de cabeamento minimo:";
    printf("%s %.4lf\n", s, sum_double(T -> key, 1, G -> V - 1));
    print_edgesT(edgesT, 1, G -> V - 1);

    return 0;
}

// Funções para o plano cartesiano
Coord coord(long x, long y) {
    Coord c;
    c.x = x;
    c.y = y;
    return c;
}

double dist(Coord c1, Coord c2) {
    return sqrt(pow(c2.x - c1.x, 2) + pow(c2.y - c1.y, 2));
}

// Funções para o grafo
WGraph *graph(int V) {
    WGraph *g = (WGraph *)malloc(sizeof(WGraph));

    g -> V = V;
    g -> E = 0;
    g -> adj = (List *)malloc(V *sizeof(List));

    for (int i = 0; i < V; i++) {
        g -> adj[i].head = NULL;
        g -> adj[i].coord = coord(-1, -1);
    }
    
    return g;
}

Node *node(int w, double p) {
    Node *no = (Node *)malloc(sizeof(Node));
    no -> w = w;
    no -> p = p;
    no -> next = NULL;
    return no;
}

void build_complete_graph(WGraph *G) {
    for (int u = 0; u < G -> V - 1; u++) {
        for (int v = u + 1; v < G -> V; v++) {
            double p = dist(G -> adj[u].coord, G -> adj[v].coord);
            insert_edge(G, edge(u, v, p));
        }
    }
}

// Funções auxiliares
void exchange_int(int *a, int *b) {
    int aux = *a;
    *a = *b;
    *b = aux;
}

void exchange_edge(Edge *A, Edge *B) {
    Edge aux = *A;
    *A = *B;
    *B = aux;
}

void start_int(int *v, int n, int x) {
    for (int i = 0; i < n; i++) {
        v[i] = x;
    }
}

void start_double(double *v, int n, double x) {
    for (int i = 0; i < n; i++) {
        v[i] = x;
    }
}

double sum_double(double *v, int start , int end) {
    double sum = 0;
    for (int i = start; i <= end; i++) {
        sum += v[i];
    }
    return sum;
}

// Funções para a árvore
Tree *tree(int V) {
	Tree *T = (Tree *)malloc(sizeof(Tree));
	T -> key = (double *)malloc(V *sizeof(double));
	T -> pred = (int *)malloc(V *sizeof(int));

    start_double(T -> key, V, INFTY);
    start_int(T -> pred, V, -1);
	
	return T;
}

Tree *prim (WGraph *G) {
	Tree *T = tree(G -> V);
	T -> pred[0] = 0;
	T -> key[0] = 0.0;
	
	Heap *Q = heap(G -> V);  //Fila de prioridades

    // Colocando todos os vértices de G na vila
    for (int i = 0; i < G -> V; i++) {
        Q -> A[i] = i;
        Q -> heap_size++;
    }

    // vetor de visitado, onde visited[i] == 1 siginifica que i está na 
    // fila de prioridades e visited[i] == 0 significa que não está.
    int *visited = (int *)malloc(G -> V * (sizeof(int)));
    start_int(visited, G -> V, 1);
	
	while(Q -> heap_size != -1) {
		int u = extract_min(Q, T -> key);
        visited[u] = 0;
        for (Node *v = G -> adj[u].head; v != NULL; v = v -> next) {
            if (visited[v -> w] && v -> p < T -> key[v -> w]) {
                T -> pred[v -> w] = u;
                int j = 0;

                // Buscando a posição do elemento v na Heap
                while (j <= Q -> heap_size && v -> w != Q -> A[j]) {
                    j++;
                }
                decrease_key(Q, j, v -> p, T -> key);
            }
        }	
	}
    free(visited);
    destroy_heap(Q);
    return T;
}

// Funções para Heap
int left_child(int i) {
    return 2 * i + 1;
}
int right_child(int i) {
    return (2 * i) + 2;
}
int parent(int i)  {
    return (i - 1) / 2;
}

Heap *heap(int n) {
    Heap *H = (Heap *)malloc(sizeof(Heap));
    H -> A = (int *)malloc(n * sizeof(int));
    H -> heap_size = -1;
    H -> size = n;
}

void min_heapify(Heap *H, int i, double *K) {  //Corrige descendo
    int esq = left_child(i);
    int dir =  right_child(i);
    
    int menor;
    if (esq <= H -> heap_size && K[H -> A[esq]] < K[H -> A[i]]) {
    	menor = esq;
    } else {
    	menor = i;
    }
    if (dir <= H -> heap_size && K[H -> A[dir]] < K[H -> A[menor]]) {
		menor = dir;
	}
	
	if (menor != i) {
        exchange_int(&(H -> A[i]), &(H -> A[menor]));
		min_heapify(H, menor, K);
	}
}

int extract_min(Heap *H, double *K) {
    int min = H -> A[0];
    H -> A[0] = H -> A[H -> heap_size];
    H -> heap_size--;
    min_heapify(H, 0, K);
	return min;
}

void decrease_key(Heap *H, int i, double key, double *K) {
    K[H -> A[i]] = key;

    //Corrigindo subindo
    while (i > 0 && K[H -> A[parent(i)]] > K[H -> A[i]]) {
        exchange_int(&(H -> A[i]), &(H -> A[parent(i)]));
        i = parent(i);
    }
}

void destroy_heap(Heap *H) {
    free(H -> A);
    free(H);
}

// Funções para arestas
WEdge edge(int u, int v, double p) {
    WEdge E;

    E.u = u;
    E.v = v;
    E.p = p;

    return E;
}

void insert_edge(WGraph *G, WEdge E){
    Node *u = node(E.u, E.p);
    Node *v = node(E.v, E.p);

    u -> next = G -> adj[E.v].head;
    v -> next = G -> adj[E.u].head;

    G -> adj[E.u].head = v;
    G -> adj[E.v].head = u;
    
    G -> E++;
}

Edge *edges(WGraph *G, Tree *T) {
    Edge *edgesT = (WEdge *)malloc(G -> V * sizeof(Edge));
    for (int i = 1; i < G -> V; i++) {
        if (T->pred[i] < i) {
            edgesT[i].u = T->pred[i];
            edgesT[i].v = i;
        } else {
            edgesT[i].u = i;
            edgesT[i].v = T->pred[i];
        }
    }
    quick_sort_edges(edgesT, 1, G -> V - 1, G -> V);
    return edgesT;
}

void quick_sort_edges(Edge *edgesT, int start, int end, int n) {
    if (start < end) {
        int i = start, j = start;
        WEdge aux;
        while (j < end) {
            if ((edgesT[j].u < edgesT[end].u ) || (edgesT[j].u == edgesT[end].u && edgesT[j].v < edgesT[end].v)) {
                exchange_edge(&edgesT[i], &edgesT[j]);
                i++;
            }
            j++;
        }
        exchange_edge(&edgesT[i], &edgesT[end]);
        quick_sort_edges(edgesT, start, i - 1, n);
        quick_sort_edges(edgesT, i + 1, end, n);
    }
}

void print_edgesT (Edge *edges, int start, int end) {
    for (int i = start; i <= end; i++) {
        printf("%d %d\n", edges[i].u, edges[i].v);
    }
}
