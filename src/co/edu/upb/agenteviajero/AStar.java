package co.edu.upb.agenteviajero;

import java.util.*;

public class AStar {

    public static List<Nodo> camino = new ArrayList<>();
    public static int costoTotal = 0;
    public static Map<Nodo, Integer> distancias = new HashMap<>();
    public static Map<Nodo, Nodo> anteriores = new HashMap<>();

    public static void ejecutar(Grafo grafo, Nodo origen, Nodo destino) {
        camino.clear();
        costoTotal = 0;

        Map<Nodo, Integer> g = new HashMap<>();
        Map<Nodo, Integer> f = new HashMap<>();
        Set<Nodo> cerrados = new HashSet<>();

        for(Nodo n : grafo.getNodos()) {
            g.put(n, Integer.MAX_VALUE);
            f.put(n, Integer.MAX_VALUE);
        }

        g.put(origen, 0);
        f.put(origen, heuristica(origen, destino));

        List<Nodo> abiertos = new ArrayList<>();
        abiertos.add(origen);

        while(!abiertos.isEmpty()) {
            Nodo actual = null;
            for(Nodo n : abiertos) {
                if(actual == null || f.get(n) < f.get(actual)) actual = n;
            }

            if(actual == destino) break;

            abiertos.remove(actual);
            cerrados.add(actual);

            for(Arista a : grafo.getAristas()) {
                if(a.getOrigen() != actual) continue;
                Nodo vecino = a.getDestino();
                if(cerrados.contains(vecino)) continue;

                int nuevoG = g.get(actual) + a.getPeso();
                if(nuevoG < g.get(vecino)) {
                    g.put(vecino, nuevoG);
                    f.put(vecino, nuevoG + heuristica(vecino, destino));
                    anteriores.put(vecino, actual);
                    if(!abiertos.contains(vecino)) abiertos.add(vecino);
                }
            }
        }

        Nodo paso = destino;
        while(paso != null) {
            camino.add(0, paso);
            paso = anteriores.get(paso);
        }
        
        distancias = g;

        if(camino.size() > 1) costoTotal = g.get(destino);
        else camino.clear();
    }

    private static int heuristica(Nodo a, Nodo b) {
    int dx = Math.abs(a.getX() - b.getX()) / 135;
    int dy = Math.abs(a.getY() - b.getY()) / 135;
    // costo mínimo posible: moverse en diagonal cuesta 33, horizontal 15, bajar 10
    return Math.min(dx, dy) * 10 + Math.abs(dx - dy) * 10;
}
}