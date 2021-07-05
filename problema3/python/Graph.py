from math import inf, sqrt, pow


class Graph:
    def __init__(self, V):
        self.V = V
        self.E = 0
        self.__vertices = []
        self.__adj = [[] for _ in range(V)]

    def insert_edge(self, u, v, p):
        self.__adj[v].append([u, p])
        self.__adj[u].append([v, p])
        self.E += 1

    def build_complete_graph(self):
        for u in range(self.V - 1):
            for v in range(u + 1, self.V):
                p = self.__vertices[u].dist(self.__vertices[v])
                self.insert_edge(u, v, p)

    def insert_vertex(self, v):
        self.__vertices.append(v)

    def prim(self):
        key = [inf] * self.V
        pred = [-1] * self.V

        pred[0] = key[0] = 0

        Q = Heap(self.V, key=lambda x: key[x])
        Q.init()

        visited = [0] * self.V

        while Q.heap_size:
            u = Q.extract_min()
            visited[u] = 1
            for v in self.__adj[u]:
                if not visited[v[0]] and v[1] < key[v[0]]:
                    pred[v[0]] = u
                    key[v[0]] = v[1]
                    Q.up_heapify(Q.A.index(v[0]))
                    
        return key, pred


class Heap:
    def __init__(self, n, key=None):
        self.A = []
        self.heap_size = 0
        self.size = n
        self.__key = key if key is not None else lambda x: x

    def init(self):
        self.A = list(range(self.size))
        self.heap_size = self.size

    @staticmethod
    def __left_child(i):
        return (2 * i) + 1

    @staticmethod
    def __right_child(i):
        return (2 * i) + 2

    @staticmethod
    def __parent(i):
        return (i - 1) // 2

    def __min_heapify(self, i):
        l = self.__left_child(i)
        r = self.__right_child(i)

        min = None
        if l < self.heap_size and self.__key(self.A[l]) < self.__key(self.A[i]):
            min = l
        else:
            min = i
        if r < self.heap_size and self.__key(self.A[r]) < self.__key(self.A[min]):
            min = r

        if min != i:
            self.A[i], self.A[min] = self.A[min], self.A[i]
            self.__min_heapify(min)

    def extract_min(self):
        min = self.A[0]
        self.A[0] = self.A[self.heap_size - 1]
        self.heap_size -= 1
        self.__min_heapify(0)
        return min

    def up_heapify(self, i):
        while i > 0 and self.__key(self.A[self.__parent(i)]) > self.__key(self.A[i]):
            self.A[i], self.A[self.__parent(i)] = self.A[self.__parent(i)], self.A[i]
            i = self.__parent(i)


class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def dist(self, c):
        return sqrt(pow(self.x - c.x, 2) + pow(self.y - c.y, 2))


n = int(input())
G = Graph(n)

for i in range(n):
    x, y = map(int, input().split(' '))
    G.insert_vertex(Point(x, y))

G.build_complete_graph()

key, pred = G.prim()

edges = [[pred[i], i] if pred[i] < i else [i, pred[i]] for i in range(1, G.V)]
edges.sort()

print(f'comprimento de cabeamento minimo: {sum(key):.4f}')

for e in edges:
    print(e[0], e[1])
