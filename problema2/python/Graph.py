# Problema 2
# George Harrison Rocha
# 11201810309

class Graph:
    def __init__(self, V):
        """
        Construtor do grafo, onde E denota |E(G)|.
        :param V: |V(G)|
        """
        self.V = V
        self.E = 0
        self.adj = [[] for _ in range(V)]  # Lista de adjacências
        self.vis = [False] * V  # Vetor de visitado
        self.pred = [-1] * V  # Vetor de predecessor

    def has_edge(self, u, v):
        """
        Método que testa a partinência de aresta.
        :param u: a aresta u
        :param v: a aresta v
        :return: True se a aresta uv existe e False caso contrário
        """
        return u in self.adj[v]

    def insert_edge(self, u, v):
        """
        Método que insere uma aresta no grafo sem duplicidade.
        :param u: o extremo u da aresta
        :param v: o extremo v da aresta
        """
        if not self.has_edge(u, v):
            self.adj[v].append(u)
            self.adj[u].append(v)
            self.E += 1

    def bfs(self, s=0):
        """
        Método busca-largura.
        :param s: um vértice de V(G)
        """
        self.vis[s] = True
        F = []  # Fila
        F.append(s)  # Enfileirando
        while len(F) > 0:
            u = F.pop(0)  # Desenfileirando
            for v in self.adj[u]:
                if not self.vis[v]:
                    self.vis[v] = True
                    self.pred[v] = u
                    F.append(v)


n = int(input())
m = int(input())

f = 'output=# de novos voos:'

if m > 0:
    G = Graph(n)
    for _ in range(m):
        u, v = map(int, input().split())
        G.insert_edge(u, v)

    qtcc = 0  # Quantidade de componentes conexas
    while not all(G.vis):
        G.bfs(G.vis.index(False))
        qtcc += 1

    print(f, qtcc - 1)
else:
    print(f, n - 1)
