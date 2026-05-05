/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.upb.agenteviajero;

/**
 *
 * @author jjbrival
 */
import java.util.ArrayList;

public class Grafo {
    private ArrayList<Nodo> nodos = new ArrayList<>();
    private ArrayList<Arista> aristas = new ArrayList<>();

    public void agregarNodo(Nodo n) {
        nodos.add(n);
    }

    public void agregarArista(Nodo a, Nodo b, int peso) {
        aristas.add(new Arista(a, b, peso));
    }

    public ArrayList<Nodo> getNodos() { return nodos; }
    public ArrayList<Arista> getAristas() { return aristas; }
    
    public void eliminarNodo(String id) {
        Nodo nodo = nodos.stream()
            .filter(n -> n.getId().equals(id))
            .findFirst().orElse(null);
        if(nodo == null) return;
        nodos.remove(nodo);
        aristas.removeIf(a -> a.getOrigen() == nodo || a.getDestino() == nodo);
    }
}