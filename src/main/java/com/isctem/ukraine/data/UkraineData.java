package com.isctem.ukraine.data;

import com.isctem.ukraine.model.Graph;
import com.isctem.ukraine.model.Oblast;
import com.isctem.ukraine.model.Connection;

/**
 * Classe responsável por carregar os dados reais da Ucrânia.
 * Inclui todos os 25 Oblasts com coordenadas geográficas reais
 * e conexões ferroviárias baseadas em distâncias aproximadas.
 */
public class UkraineData {

    /**
     * Cria e popula o grafo completo da Ucrânia
     */
    public static Graph createUkraineGraph() {
        Graph graph = new Graph(false); // Grafo não-direcionado

        // Adicionar todos os Oblasts
        addAllOblasts(graph);

        // Adicionar todas as conexões ferroviárias
        addAllConnections(graph);

        return graph;
    }

    /**
     * Adiciona todos os 25 Oblasts da Ucrânia
     */
    private static void addAllOblasts(Graph graph) {
        // REGIÃO CENTRAL
        graph.addOblast(new Oblast("kyiv", "Kyiv", 50.4501, 30.5234,
                false, 2952301, "Central"));

        graph.addOblast(new Oblast("cherkasy", "Cherkasy", 49.4285, 32.0617,
                false, 1192137, "Central"));

        graph.addOblast(new Oblast("chernihiv", "Chernihiv", 51.4982, 31.2893,
                false, 959315, "Northern"));

        graph.addOblast(new Oblast("zhytomyr", "Zhytomyr", 50.2547, 28.6587,
                false, 1208212, "Northern"));

        graph.addOblast(new Oblast("vinnytsia", "Vinnytsia", 49.2328, 28.4681,
                false, 1545416, "Central"));

        graph.addOblast(new Oblast("khmelnytskyi", "Khmelnytskyi", 49.4229, 26.9871,
                false, 1254702, "Western"));

        // REGIÃO OESTE
        graph.addOblast(new Oblast("lviv", "Lviv", 49.8397, 24.0297,
                false, 2512084, "Western"));

        graph.addOblast(new Oblast("ivano-frankivsk", "Ivano-Frankivsk", 48.9226, 24.7111,
                false, 1373252, "Western"));

        graph.addOblast(new Oblast("ternopil", "Ternopil", 49.5535, 25.5948,
                false, 1045879, "Western"));

        graph.addOblast(new Oblast("rivne", "Rivne", 50.6199, 26.2516,
                false, 1152961, "Western"));

        graph.addOblast(new Oblast("volyn", "Volyn", 50.7472, 25.3254,
                false, 1035330, "Western"));

        graph.addOblast(new Oblast("zakarpattia", "Zakarpattia", 48.6208, 22.2879,
                false, 1253791, "Western"));

        graph.addOblast(new Oblast("chernivtsi", "Chernivtsi", 48.2921, 25.9358,
                false, 904374, "Western"));

        // REGIÃO SUL
        graph.addOblast(new Oblast("odesa", "Odesa", 46.4825, 30.7233,
                false, 2390572, "Southern"));

        graph.addOblast(new Oblast("mykolaiv", "Mykolaiv", 46.9750, 31.9946,
                false, 1119862, "Southern"));

        graph.addOblast(new Oblast("kherson", "Kherson", 46.6354, 32.6169,
                true, 1037640, "Southern")); // FRONTLINE - Dombas

        graph.addOblast(new Oblast("crimea", "Crimea", 45.0355, 34.1021,
                true, 2033700, "Southern")); // Ocupada

        // REGIÃO LESTE (DOMBAS)
        graph.addOblast(new Oblast("donetsk", "Donetsk", 48.0159, 37.8028,
                true, 4165901, "Eastern")); // FRONTLINE - Dombas

        graph.addOblast(new Oblast("luhansk", "Luhansk", 48.5740, 39.3078,
                true, 2151833, "Eastern")); // FRONTLINE - Dombas

        graph.addOblast(new Oblast("zaporizhzhia", "Zaporizhzhia", 47.8388, 35.1396,
                true, 1687401, "Southern")); // FRONTLINE - Dombas

        graph.addOblast(new Oblast("dnipropetrovsk", "Dnipropetrovsk", 48.4647, 35.0462,
                false, 3176648, "Eastern"));

        graph.addOblast(new Oblast("kharkiv", "Kharkiv", 49.9935, 36.2304,
                true, 2658461, "Eastern")); // Próximo à frontline

        graph.addOblast(new Oblast("poltava", "Poltava", 49.5883, 34.5514,
                false, 1386978, "Central"));

        graph.addOblast(new Oblast("sumy", "Sumy", 50.9077, 34.7981,
                false, 1068247, "Northern"));

        graph.addOblast(new Oblast("kirovohrad", "Kirovohrad", 48.5132, 32.2597,
                false, 945549, "Central"));
    }

    /**
     * Adiciona todas as conexões ferroviárias principais
     * Distâncias baseadas em rotas ferroviárias reais
     */
    private static void addAllConnections(Graph graph) {
        // CONEXÕES A PARTIR DE KYIV (HUB CENTRAL)
        graph.addConnection("kyiv", "cherkasy", 185);
        graph.addConnection("kyiv", "chernihiv", 148);
        graph.addConnection("kyiv", "zhytomyr", 140);
        graph.addConnection("kyiv", "vinnytsia", 268);
        graph.addConnection("kyiv", "poltava", 343);
        graph.addConnection("kyiv", "dnipropetrovsk", 477);
        graph.addConnection("kyiv", "kharkiv", 478);

        // CONEXÕES REGIÃO OESTE
        graph.addConnection("lviv", "ivano-frankivsk", 132);
        graph.addConnection("lviv", "ternopil", 132);
        graph.addConnection("lviv", "rivne", 210);
        graph.addConnection("lviv", "volyn", 150);
        graph.addConnection("lviv", "zakarpattia", 265);

        graph.addConnection("ivano-frankivsk", "ternopil", 122);
        graph.addConnection("ivano-frankivsk", "chernivtsi", 132);
        graph.addConnection("ivano-frankivsk", "zakarpattia", 190);

        graph.addConnection("ternopil", "khmelnytskyi", 110);
        graph.addConnection("ternopil", "chernivtsi", 243);

        graph.addConnection("khmelnytskyi", "vinnytsia", 120);
        graph.addConnection("khmelnytskyi", "rivne", 184);

        graph.addConnection("rivne", "volyn", 72);
        graph.addConnection("rivne", "zhytomyr", 189);

        graph.addConnection("zhytomyr", "vinnytsia", 128);

        // CONEXÕES REGIÃO NORTE
        graph.addConnection("chernihiv", "sumy", 186);
        graph.addConnection("sumy", "kharkiv", 218);
        graph.addConnection("sumy", "poltava", 165);

        // CONEXÕES REGIÃO CENTRAL
        graph.addConnection("vinnytsia", "cherkasy", 195);
        graph.addConnection("vinnytsia", "kirovohrad", 178);
        graph.addConnection("vinnytsia", "odesa", 395);

        graph.addConnection("cherkasy", "kirovohrad", 123);
        graph.addConnection("cherkasy", "poltava", 255);

        graph.addConnection("poltava", "kharkiv", 142);
        graph.addConnection("poltava", "dnipropetrovsk", 216);

        // CONEXÕES REGIÃO SUL
        graph.addConnection("odesa", "mykolaiv", 131);
        graph.addConnection("mykolaiv", "kherson", 71);
        graph.addConnection("mykolaiv", "kirovohrad", 182);

        graph.addConnection("kherson", "crimea", 178);
        graph.addConnection("kherson", "zaporizhzhia", 298);

        graph.addConnection("crimea", "zaporizhzhia", 356);

        // CONEXÕES REGIÃO LESTE (DOMBAS)
        graph.addConnection("dnipropetrovsk", "zaporizhzhia", 82);
        graph.addConnection("dnipropetrovsk", "kharkiv", 214);
        graph.addConnection("dnipropetrovsk", "donetsk", 247);
        graph.addConnection("dnipropetrovsk", "kirovohrad", 190);

        graph.addConnection("zaporizhzhia", "donetsk", 236);

        graph.addConnection("kharkiv", "donetsk", 306);
        graph.addConnection("kharkiv", "luhansk", 268);

        graph.addConnection("donetsk", "luhansk", 145);

        // CONEXÕES ADICIONAIS ESTRATÉGICAS
        graph.addConnection("kirovohrad", "zaporizhzhia", 200);
    }

    /**
     * Retorna lista de Oblasts da região Dombas (linha de frente)
     */
    public static String[] getDombasOblasts() {
        return new String[]{"donetsk", "luhansk", "zaporizhzhia", "kherson"};
    }

    /**
     * Retorna Oblasts seguros (longe da fronteira)
     */
    public static String[] getSafeOblasts() {
        return new String[]{
                "lviv", "ivano-frankivsk", "ternopil", "volyn",
                "zakarpattia", "chernivtsi", "rivne", "khmelnytskyi"
        };
    }

    /**
     * Retorna principais hubs logísticos
     */
    public static String[] getLogisticsHubs() {
        return new String[]{
                "kyiv", "lviv", "dnipropetrovsk", "kharkiv", "odesa"
        };
    }

    /**
     * Simula destruição de conexões em regiões de fronteira
     */
    public static void simulateWarDamage(Graph graph, double destructionPercent) {
        String[] frontlineOblasts = getDombasOblasts();

        for (String oblastId : frontlineOblasts) {
            for (Connection conn : graph.getConnections(oblastId)) {
                if (Math.random() < destructionPercent / 100.0) {
                    conn.setDestroyed(true);
                }
            }
        }

        // Reconstruir matriz
        graph.getAdjacencyMatrix();
    }

    /**
     * Retorna informações sobre a rede ferroviária
     */
    public static String getNetworkInfo() {
        return """
            ╔══════════════════════════════════════════════════════╗
            ║     REDE FERROVIÁRIA DA UCRÂNIA - INFORMAÇÕES        ║
            ╠══════════════════════════════════════════════════════╣
            ║ Total de Oblasts: 25                                 ║
            ║ Oblasts na Linha de Frente: 4 (Dombas)              ║
            ║ Oblasts Seguros: 8 (Região Oeste)                   ║
            ║ Principais Hubs: 5                                   ║
            ║                                                      ║
            ║ Região Dombas (Frontline):                          ║
            ║   - Donetsk (rico em minérios)                      ║
            ║   - Luhansk (indústria pesada)                      ║
            ║   - Zaporizhzhia (energia nuclear)                  ║
            ║   - Kherson (acesso ao Mar Negro)                   ║
            ║                                                      ║
            ║ Zonas Seguras (Oeste):                              ║
            ║   - Lviv (principal hub oeste)                      ║
            ║   - Ivano-Frankivsk, Ternopil, Volyn               ║
            ║   - Zakarpattia, Chernivtsi, Rivne, Khmelnytskyi   ║
            ╚══════════════════════════════════════════════════════╝
            """;
    }

    /**
     * Retorna descrição detalhada de um Oblast
     */
    public static String getOblastDescription(String oblastId) {
        return switch (oblastId) {
            case "kyiv" -> "Capital da Ucrânia. Principal hub político e logístico.";
            case "donetsk" -> "Região rica em carvão e indústria pesada. Frontline desde 2014.";
            case "luhansk" -> "Centro industrial. Parcialmente controlado por separatistas.";
            case "zaporizhzhia" -> "Maior usina nuclear da Europa. Estrategicamente vital.";
            case "kherson" -> "Porto importante no Mar Negro. Vital para exportação de cereais.";
            case "lviv" -> "Principal cidade do oeste. Hub cultural e refúgio seguro.";
            case "dnipropetrovsk" -> "Centro industrial e tecnológico. Hub logístico importante.";
            case "kharkiv" -> "Segunda maior cidade. Próxima à fronteira russa.";
            case "odesa" -> "Maior porto do Mar Negro. Vital para economia.";
            case "crimea" -> "Peninsula anexada pela Rússia em 2014.";
            default -> "Oblast da Ucrânia com importância estratégica.";
        };
    }
}