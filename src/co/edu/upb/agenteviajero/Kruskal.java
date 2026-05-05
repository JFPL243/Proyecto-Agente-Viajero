
package co.edu.upb.agenteviajero;

import java.util.*;

public class Kruskal {

  
    public static List<Arista> aristasArbol = new ArrayList<>();
    public static int pesoTotal = 0;
    private static Map<Nodo, Nodo> padre = new HashMap<>();


    private static Nodo encontrar(Nodo n) {
    	if(padre.get(n) != n) {
    		padre.put(n, encontrar(padre.get(n)));  		
    		
    	}
    	return padre.get(n);
    }

    private static void unir(Nodo a, Nodo b) {
    	Nodo raizA = encontrar(a);
    	Nodo raizb = encontrar(b);
    	padre.put(raizA, raizb);
    }


    public static void ejecutar(Grafo grafo) {
        aristasArbol.clear();
        pesoTotal = 0;
        padre.clear();
        
        
        for(Nodo n : grafo.getNodos()) {
        	padre.put(n, n);
        }
        
        List<Arista> aristas = new ArrayList<>(grafo.getAristas());
        aristas.sort(Comparator.comparingInt(Arista::getPeso));
        
        for(Arista a: aristas) {
        	Nodo u = a.getOrigen();
        	Nodo v = a.getDestino();
        	if(!encontrar(u).equals(encontrar(v))) {
        		aristasArbol.add(a);
        		pesoTotal += a.getPeso();
        		unir(u, v);
        				
        	}
        }

    }
}