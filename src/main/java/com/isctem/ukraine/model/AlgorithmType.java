package com.isctem.ukraine.model;

/**
 * Enum que define os tipos de algoritmos disponíveis no sistema.
 */
public enum AlgorithmType {
    BFS("Breadth-First Search", "Busca em Largura",
            "Explora vizinhos nível por nível", false),

    DFS("Depth-First Search", "Busca em Profundidade",
            "Explora o mais profundo possível antes de retroceder", false),

    DIJKSTRA("Dijkstra", "Algoritmo de Dijkstra",
            "Encontra o caminho mais curto de um ponto para todos os outros", true),

    KRUSKAL("Kruskal", "Algoritmo de Kruskal",
            "Encontra a árvore geradora mínima usando ordenação de arestas", true),

    PRIM("Prim", "Algoritmo de Prim",
            "Encontra a árvore geradora mínima crescendo a partir de um vértice", true);

    private final String fullName;
    private final String portugueseName;
    private final String description;
    private final boolean isOptimal;

    AlgorithmType(String fullName, String portugueseName,
                  String description, boolean isOptimal) {
        this.fullName = fullName;
        this.portugueseName = portugueseName;
        this.description = description;
        this.isOptimal = isOptimal;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPortugueseName() {
        return portugueseName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOptimal() {
        return isOptimal;
    }

    /**
     * Retorna categoria do algoritmo
     */
    public String getCategory() {
        return switch (this) {
            case BFS, DFS -> "Busca/Travessia";
            case DIJKSTRA -> "Caminho Mínimo";
            case KRUSKAL, PRIM -> "Árvore Geradora Mínima";
        };
    }

    /**
     * Retorna complexidade do algoritmo
     */
    public String getComplexity() {
        return switch (this) {
            case BFS -> "O(V + E)";
            case DFS -> "O(V + E)";
            case DIJKSTRA -> "O((V + E) log V)";
            case KRUSKAL -> "O(E log E)";
            case PRIM -> "O((V + E) log V)";
        };
    }

    /**
     * Retorna caso de uso ideal
     */
    public String getIdealUseCase() {
        return switch (this) {
            case BFS -> "Caminho mais curto em grafos não-ponderados";
            case DFS -> "Detectar ciclos, ordenação topológica";
            case DIJKSTRA -> "Caminho mais curto em grafos ponderados";
            case KRUSKAL -> "Conectar todos os pontos com menor custo total";
            case PRIM -> "Conectar todos os pontos a partir de um inicial";
        };
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", fullName, portugueseName);
    }
}