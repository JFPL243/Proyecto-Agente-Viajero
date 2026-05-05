package co.edu.upb.agenteviajero;
import java.util.*;

public class Dijkstra {

    public static Map<Nodo, Integer> distancias = new HashMap<>();

    public static Map<Nodo, Nodo> anteriores = new HashMap<>();
    
    public static List<Nodo> camino = new ArrayList<>();
    
    
    public static void ejecutar(Grafo grafo, Nodo origen, Nodo destino) {
        distancias.clear();
        anteriores.clear();
        camino.clear();
        
        for (Nodo n : grafo.getNodos()) {
        	distancias.put(n, Integer.MAX_VALUE);
        	anteriores.put(n, null);
        }
        
        distancias.put(origen, 0);
        
        List<Nodo> pendientes = new ArrayList<>(grafo.getNodos());
        
        
        
        while(!pendientes.isEmpty()) {
        	
        	Nodo actual = null;
        	
        	for(Nodo n : pendientes) {
        		if(actual == null || distancias.get(n) < distancias.get(actual)) {
              		actual = n;
              	}
            }
        	
        	if(actual == null || distancias.get(actual) == Integer.MAX_VALUE) break;
        	if(actual == destino) break;
        	
        	pendientes.remove(actual);
            
            for (Arista a : grafo.getAristas()) {
            	Nodo vecino = null;
            	if(a.getOrigen() == actual) vecino = a.getDestino();
            	
            	
            	if(vecino != null && pendientes.contains(vecino)) {
            		int nuevaDist = distancias.get(actual) + a.getPeso();
            		if(nuevaDist < distancias.get(vecino)) {
            			distancias.put(vecino, nuevaDist);
            			anteriores.put(vecino, actual);
            		}
            	}
            }

        }
        
        Nodo paso = destino;
        while(paso != null){
        	camino.add(0, paso);
        	paso = anteriores.get(paso);
        }
        
    }
}