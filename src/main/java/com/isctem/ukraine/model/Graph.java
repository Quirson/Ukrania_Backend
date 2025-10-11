package com.isctem.ukraine.model;

import java.util.*;

/**
 * Representa o grafo completo da rede ferroviária ucraniana.
 * Implementa tanto Matriz de Adjacência quanto Lista de Adjacência.
 */
public class Graph {
    // Estruturas de dados principais
    private final Map<String, Oblast> oblasts;
    private final List<Connection> connections;

    // Matriz de Adjacência: [i][j] = distância entre oblast i e oblast j
    private double[][] adjacencyMatrix;

    // Lista de Adjacência: Map<oblastId, List<Connection>>
    private final Map<String, List<Connection>> adjacencyList;

    // Mapeamento de IDs para índices da matriz
    private final Map<String, Integer> idToIndex;
    private final Map<Integer, String> indexToId;

    private int nodeCount;
    private boolean isDirected;

    /**
     * Construtor
     */
    public Graph(boolean isDirected) {
        this.oblasts = new HashMap<>();
        this.connections = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
        this.idToIndex = new HashMap<>();
        this.indexToId = new HashMap<>();
        this.nodeCount = 0;
        this.isDirected = isDirected;
    }

    /**
     * Construtor para grafo não-direcionado
     */
    public Graph() {
        this(false);
    }

    // ========== MÉTODOS DE ADIÇÃO ==========

    /**
     * Adiciona um Oblast ao grafo
     */
    public void addOblast(Oblast oblast) {
        if (!oblasts.containsKey(oblast.getId())) {
            oblasts.put(oblast.getId(), oblast);
            adjacencyList.put(oblast.getId(), new ArrayList<>());

            // Atribuir índice para matriz
            idToIndex.put(oblast.getId(), nodeCount);
            indexToId.put(nodeCount, oblast.getId());
            nodeCount++;

            // Reconstruir matriz
            rebuildAdjacencyMatrix();
        }
    }

    /**
     * Adiciona uma conexão entre dois Oblasts
     */
    public void addConnection(Connection connection) {
        Oblast from = connection.getFrom();
        Oblast to = connection.getTo();

        // Garantir que os Oblasts existem
        addOblast(from);
        addOblast(to);

        // Adicionar à lista de conexões
        connections.add(connection);

        // Adicionar à lista de adjacência
        adjacencyList.get(from.getId()).add(connection);
        if (!isDirected) {
            // Para grafo não-direcionado, adicionar em ambas direções
            Connection reverse = new Connection(to, from, connection.getDistance(),
                    connection.getRailwayType());
            adjacencyList.get(to.getId()).add(reverse);
        }

        // Atualizar matriz
        updateMatrixConnection(from.getId(), to.getId(), connection.getDistance());
    }

    /**
     * Adiciona conexão simplificada
     */
    public void addConnection(String fromId, String toId, double distance) {
        Oblast from = oblasts.get(fromId);
        Oblast to = oblasts.get(toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Um ou ambos Oblasts não existem");
        }

        addConnection(new Connection(from, to, distance));
    }

    // ========== CONSTRUÇÃO DA MATRIZ ==========

    /**
     * Reconstrói a matriz de adjacência
     */
    private void rebuildAdjacencyMatrix() {
        adjacencyMatrix = new double[nodeCount][nodeCount];

        // Inicializar com infinito
        for (int i = 0; i < nodeCount; i++) {
            Arrays.fill(adjacencyMatrix[i], Double.POSITIVE_INFINITY);
            adjacencyMatrix[i][i] = 0; // Distância para si mesmo é 0
        }

        // Preencher com conexões existentes
        for (Connection conn : connections) {
            if (!conn.isDestroyed()) {
                int fromIdx = idToIndex.get(conn.getFrom().getId());
                int toIdx = idToIndex.get(conn.getTo().getId());
                adjacencyMatrix[fromIdx][toIdx] = conn.getDistance();
                if (!isDirected) {
                    adjacencyMatrix[toIdx][fromIdx] = conn.getDistance();
                }
            }
        }
    }

    /**
     * Atualiza uma conexão específica na matriz
     */
    private void updateMatrixConnection(String fromId, String toId, double distance) {
        if (adjacencyMatrix == null) return;

        Integer fromIdx = idToIndex.get(fromId);
        Integer toIdx = idToIndex.get(toId);

        if (fromIdx != null && toIdx != null) {
            adjacencyMatrix[fromIdx][toIdx] = distance;
            if (!isDirected) {
                adjacencyMatrix[toIdx][fromIdx] = distance;
            }
        }
    }

    // ========== MÉTODOS DE CONSULTA ==========

    /**
     * Retorna todos os Oblasts
     */
    public Collection<Oblast> getAllOblasts() {
        return oblasts.values();
    }

    /**
     * Retorna Oblast por ID
     */
    public Oblast getOblast(String id) {
        return oblasts.get(id);
    }

    /**
     * Retorna todas as conexões
     */
    public List<Connection> getAllConnections() {
        return new ArrayList<>(connections);
    }

    /**
     * Retorna conexões de um Oblast específico
     */
    public List<Connection> getConnections(String oblastId) {
        return new ArrayList<>(adjacencyList.getOrDefault(oblastId, new ArrayList<>()));
    }

    /**
     * Retorna vizinhos de um Oblast
     */
    public List<Oblast> getNeighbors(String oblastId) {
        List<Connection> conns = adjacencyList.get(oblastId);
        if (conns == null) return new ArrayList<>();

        return conns.stream()
                .filter(Connection::isUsable)
                .map(c -> c.getTo())
                .toList();
    }

    /**
     * Retorna a distância entre dois Oblasts
     */
    public double getDistance(String fromId, String toId) {
        Integer fromIdx = idToIndex.get(fromId);
        Integer toIdx = idToIndex.get(toId);

        if (fromIdx == null || toIdx == null) {
            return Double.POSITIVE_INFINITY;
        }

        return adjacencyMatrix[fromIdx][toIdx];
    }

    /**
     * Verifica se existe conexão entre dois Oblasts
     */
    public boolean hasConnection(String fromId, String toId) {
        return getDistance(fromId, toId) != Double.POSITIVE_INFINITY;
    }

    /**
     * Retorna conexão específica entre dois Oblasts
     */
    public Connection getConnection(String fromId, String toId) {
        List<Connection> conns = adjacencyList.get(fromId);
        if (conns == null) return null;

        return conns.stream()
                .filter(c -> c.getTo().getId().equals(toId))
                .findFirst()
                .orElse(null);
    }

    // ========== MÉTODOS DE MANIPULAÇÃO ==========

    /**
     * Remove um Oblast e todas suas conexões
     */
    public void removeOblast(String oblastId) {
        oblasts.remove(oblastId);
        adjacencyList.remove(oblastId);
        connections.removeIf(c ->
                c.getFrom().getId().equals(oblastId) ||
                        c.getTo().getId().equals(oblastId));

        // Remover de outras listas de adjacência
        for (List<Connection> conns : adjacencyList.values()) {
            conns.removeIf(c -> c.getTo().getId().equals(oblastId));
        }

        rebuildAdjacencyMatrix();
    }

    /**
     * Remove uma conexão específica
     */
    public void removeConnection(String fromId, String toId) {
        connections.removeIf(c -> c.connects(
                oblasts.get(fromId), oblasts.get(toId)));

        adjacencyList.get(fromId).removeIf(c -> c.getTo().getId().equals(toId));
        if (!isDirected) {
            adjacencyList.get(toId).removeIf(c -> c.getTo().getId().equals(fromId));
        }

        updateMatrixConnection(fromId, toId, Double.POSITIVE_INFINITY);
    }

    /**
     * Destrói uma conexão (simulação de ataque)
     */
    public void destroyConnection(String fromId, String toId) {
        Connection conn = getConnection(fromId, toId);
        if (conn != null) {
            conn.setDestroyed(true);
            rebuildAdjacencyMatrix();
        }
    }

    /**
     * Destrói um Oblast (simulação de ataque)
     */
    public void destroyOblast(String oblastId) {
        Oblast oblast = oblasts.get(oblastId);
        if (oblast != null) {
            oblast.setDestroyed(true);
            // Marcar todas conexões como destruídas
            for (Connection conn : adjacencyList.get(oblastId)) {
                conn.setDestroyed(true);
            }
            rebuildAdjacencyMatrix();
        }
    }

    // ========== MÉTODOS GETTERS ==========

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public Map<String, List<Connection>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public Map<String, Integer> getIdToIndex() {
        return idToIndex;
    }

    public Map<Integer, String> getIndexToId() {
        return indexToId;
    }

    // ========== MÉTODOS DE IMPRESSÃO ==========

    /**
     * Imprime a Matriz de Adjacência formatada
     */
    public void printAdjacencyMatrix() {
        System.out.println("\n========== MATRIZ DE ADJACÊNCIA ==========");

        // Cabeçalho
        System.out.print("        ");
        for (int i = 0; i < nodeCount; i++) {
            String id = indexToId.get(i);
            System.out.printf("%-8s", id.substring(0, Math.min(6, id.length())));
        }
        System.out.println();

        // Linhas
        for (int i = 0; i < nodeCount; i++) {
            String id = indexToId.get(i);
            System.out.printf("%-8s", id.substring(0, Math.min(6, id.length())));

            for (int j = 0; j < nodeCount; j++) {
                double dist = adjacencyMatrix[i][j];
                if (dist == Double.POSITIVE_INFINITY) {
                    System.out.print("   ∞    ");
                } else if (dist == 0) {
                    System.out.print("   0    ");
                } else {
                    System.out.printf("%7.1f ", dist);
                }
            }
            System.out.println();
        }
        System.out.println("==========================================\n");
    }

    /**
     * Imprime as Listas de Adjacência
     */
    public void printAdjacencyList() {
        System.out.println("\n========== LISTA DE ADJACÊNCIA ==========");

        for (Oblast oblast : oblasts.values()) {
            System.out.printf("\n%s (%s):\n", oblast.getName(), oblast.getId());

            List<Connection> conns = adjacencyList.get(oblast.getId());
            if (conns.isEmpty()) {
                System.out.println("  (sem conexões)");
            } else {
                for (Connection conn : conns) {
                    if (conn.isUsable()) {
                        System.out.printf("  -> %s (%.1f km) [%s]\n",
                                conn.getTo().getName(),
                                conn.getDistance(),
                                conn.isDestroyed() ? "DESTRUÍDO" : "OK");
                    }
                }
            }
        }
        System.out.println("\n==========================================\n");
    }

    /**
     * Retorna estatísticas do grafo
     */
    public String getStatistics() {
        int totalConnections = connections.size();
        int destroyedConnections = (int) connections.stream()
                .filter(Connection::isDestroyed).count();
        int destroyedOblasts = (int) oblasts.values().stream()
                .filter(Oblast::isDestroyed).count();

        double avgDegree = adjacencyList.values().stream()
                .mapToInt(List::size)
                .average()
                .orElse(0.0);

        return String.format("""
            ========== ESTATÍSTICAS DO GRAFO ==========
            Número de Oblasts: %d
            Oblasts Destruídos: %d
            Número de Conexões: %d
            Conexões Destruídas: %d
            Grau Médio: %.2f
            Tipo: %s
            ==========================================
            """,
                nodeCount, destroyedOblasts, totalConnections,
                destroyedConnections, avgDegree,
                isDirected ? "Direcionado" : "Não-Direcionado");
    }

    /**
     * Cria uma cópia do grafo
     */
    public Graph clone() {
        Graph cloned = new Graph(this.isDirected);

        // Copiar Oblasts
        for (Oblast oblast : this.oblasts.values()) {
            cloned.addOblast(oblast);
        }

        // Copiar Conexões
        for (Connection conn : this.connections) {
            Oblast from = cloned.getOblast(conn.getFrom().getId());
            Oblast to = cloned.getOblast(conn.getTo().getId());
            Connection newConn = new Connection(from, to,
                    conn.getDistance(), conn.getRailwayType());
            newConn.setCondition(conn.getCondition());
            newConn.setDestroyed(conn.isDestroyed());
            cloned.addConnection(newConn);
        }

        return cloned;
    }

    /**
     * Valida a integridade do grafo
     */
    public boolean validate() {
        // Verificar se matriz e listas estão sincronizadas
        for (Connection conn : connections) {
            String fromId = conn.getFrom().getId();
            String toId = conn.getTo().getId();

            double matrixDist = getDistance(fromId, toId);
            double connDist = conn.getDistance();

            if (Math.abs(matrixDist - connDist) > 0.01 &&
                    matrixDist != Double.POSITIVE_INFINITY) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Graph{oblasts=%d, connections=%d, directed=%s}",
                nodeCount, connections.size(), isDirected);
    }
}