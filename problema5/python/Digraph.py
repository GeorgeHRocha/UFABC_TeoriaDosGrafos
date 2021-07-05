# Problema 5
# George Harrison Rocha
# 11201810309

"""
Este programa implementa o algoritmo de dijkstra e a estrutura heap. 
A classe SSP implementa o algoritmo de dijkstra, e este, por sua vez, 
utiliza a classe Heap para manter a fila de prioridades. 

A classe Heap tem o atributo opcional 'key', que recebe uma função 
como valor. Esta função pega um elemento da heap e retorna um valor que 
então é usado para fazer as operações na heap. Caso não seja passado 
nenhuma função, o valor de retorno é o próprio elemento. No caso desse 
programa, a função passada é uma que pega um elemento x da heap retona 
o valor armazenado no índice x da lista 'cost' da classe SSP. 

Primeiramente, o programa lê os valores 'n', 'm', 'origin' e 'destination', 
que representam, respectivamente, a quantidade de vértices, a quantidade 
de conexões entre vértices, o vértice de origem e o vértice de destino. Logo em seguida, 
cria-se um digrafo ponderado e lê-se uma sequência de pares de vértices 
com o respectivo peso w. Dado o contexto do problema, para cada par u, v lido, 
adiciona-se ao digrafo um arco uv e um arco vu, ambos com peso w. Finalmente, 
usando a classe SSP, calcula-se o caminho mínimo entre 'origin' e 'destination', 
e imprime, caso exista caminho, a quantidade de vértices do caminho encontrado, 
o custo do caminho e os vértices do caminho. Caso não exista um caminho, 
imprime-se 'NO ROUTE FOUND!'
"""

from math import inf


class Arc:

    def __init__(self, head, tail, weight):
        self.__head = head
        self.__tail = tail
        self.__weight = weight

    def head(self):
        return self.__head

    def tail(self):
        return self.__tail

    def weight(self):
        return self.__weight


class WDigraph:
    def __init__(self, V):
        self.__V = V
        self.__E = 0
        self.__adj = [[] for _ in range(V)]  # Lista de adjacencia

    def order(self):
        return self.__V

    def size(self):
        return self.__E

    def add_arc(self, e):
        self.__adj[e.tail()].append(e)
        self.__E += 1

    def adj(self, u):
        return self.__adj[u]


class SSP:
    __UNREACHABLE = -1

    def __init__(self, D, root=0):
        self.__D = D
        self.__root = root
        self.__cost = [inf] * D.order()
        self.__pred = [self.__UNREACHABLE] * D.order()
        self.__dijkstra()

    def __relax(self, e, Q):
        u = e.tail()
        v = e.head()
        if self.__cost[v] > self.__cost[u] + e.weight():
            self.__cost[v] = self.__cost[u] + e.weight()
            self.__pred[v] = u
            Q.up_heapify(Q.A().index(v))

    def __dijkstra(self):
        Q = Heap(self.__D.order(), key=lambda x: self.__cost[x])
        Q.init()

        self.__cost[self.__root] = 0
        self.__pred[self.__root] = self.__root
        Q.up_heapify(Q.A().index(self.__root))

        while Q.heap_size():
            u = Q.extract_min()
            if self.__cost[u] == inf:
                return
            for e in self.__D.adj(u):
                self.__relax(e, Q)

    def dist_to(self, u):
        return self.__cost[u]

    def has_path_to(self, u):
        return self.__cost[u] < inf

    def path_to(self, u):
        if not self.has_path_to(u):
            return None

        path = [u]

        if self.__root == u:
            return path

        p = self.__pred[u]
        print(self.__pred)
        print(self.__cost)
        while p != self.__root:
            # print('p', p, 'root', self.__root, 'pred[p]', self.__pred[p])
            path.insert(0, p)
            p = self.__pred[p]
        path.insert(0, p)

        return path


class Heap:
    def __init__(self, n, key=None):
        self.__A = []
        self.__heap_size = 0
        self.__size = n
        self.__key = key if key is not None else lambda x: x

    def init(self):
        self.__A = list(range(self.__size))
        self.__heap_size = self.__size

    def heap_size(self):
        return self.__heap_size

    def A(self, i=-1):
        return self.__A[i] if i != -1 else self.__A

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
        if l < self.heap_size() and self.__key(self.A(l)) < self.__key(self.A(i)):
            min = l
        else:
            min = i
        if r < self.heap_size() and self.__key(self.A(r)) < self.__key(self.A(min)):
            min = r

        if min != i:
            self.__A[i], self.__A[min] = self.A(min), self.A(i)
            self.__min_heapify(min)

    def extract_min(self):
        min = self.A(0)
        self.__A[0] = self.A(self.heap_size() - 1)
        self.__heap_size -= 1
        self.__min_heapify(0)
        return min

    def up_heapify(self, i):
        while i > 0 and self.__key(self.A(self.__parent(i))) > self.__key(self.A(i)):
            self.__A[i], self.__A[self.__parent(i)] = self.A(self.__parent(i)), self.A(i)
            i = self.__parent(i)


input()
n = int(input()[15:])
m = int(input()[10:])
origin = int(input()[7:])
destination = int(input()[12:])
input()

D = WDigraph(n)

for _ in range(m):
    x, y, w = input().split(' ')
    x, y, w = int(x), int(y), float(w)
    D.add_arc(Arc(x, y, w))
    D.add_arc(Arc(y, x, w))

ssp = SSP(D, origin)
route = ssp.path_to(destination)

if route is None:
    print('NO ROUTE FOUND!')
else:
    total_jumps = len(route)
    print('TOTAL JUMPS:', total_jumps)
    print(f'ENERGY REQUIRED: {ssp.dist_to(destination):.4f}')
    print('ROUTE:')
    for i, v in enumerate(route):
        print(f'{i}: {v}')
