/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package co.edu.upb.agenteviajero;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 *
 * @author jjbrival este editar
 */
public class Graphvisualizer extends JFrame{

	public Graphvisualizer() {
	    setTitle("Agente Viajero - Visualizador de Grafos");
	    setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLayout(new BorderLayout());

	    Grafo grafo = new Grafo();

	    PanelGrafo   panelGrafo    = new PanelGrafo(grafo);
	    PanelTabla   panelDijkstra = new PanelTabla();
	    PanelKruskal panelKruskal  = new PanelKruskal();
	    PanelAdyacencia panelMatriz = new PanelAdyacencia(grafo);
	    
	    

	    JTabbedPane tabs = new JTabbedPane();
	    tabs.addTab("Grafo",    panelGrafo);
	    tabs.addTab("Tabla", panelDijkstra);
	    tabs.addTab("Kruskal",  panelKruskal);
	    tabs.addTab("Matriz", panelMatriz);

	    JButton btnDijkstra = crearBoton("Dijkstra", new Color(34, 150, 80));
	    JButton btnKruskal  = crearBoton("Kruskal",  new Color(130, 40, 180));
	    JButton btnAStar = crearBoton("A*", new Color(200, 80, 20));


	    
	    
	    btnDijkstra.addActionListener(e -> panelGrafo.ejecutarDijkstra(panelDijkstra));
	    btnKruskal.addActionListener(e  -> panelGrafo.ejecutarKruskal(panelKruskal));
		btnAStar.addActionListener(e -> panelGrafo.ejecutarAStar(panelDijkstra));

	    JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
	    
	    barra.setBackground(new Color(240, 240, 245));
	    barra.add(btnDijkstra);
	    barra.add(btnKruskal);
	    barra.add(btnAStar);
	    
	    JLabel lblEstado = new JLabel("Origen: C9  |  Selecciona destino con ▶ Dijkstra");
	    lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
	    lblEstado.setForeground(new Color(80, 80, 80));
	    panelGrafo.setLblEstado(lblEstado);

	    barra.add(Box.createHorizontalStrut(20));
	    barra.add(lblEstado);

	    add(tabs,  BorderLayout.CENTER);
	    add(barra, BorderLayout.SOUTH);
	    setVisible(true);

	    // Cargar el grafo en segundo plano
	    new SwingWorker<Void, Void>() {
	        protected Void doInBackground() {
	            cargarGrafo(grafo);
	            return null;
	        }
	        protected void done() {
	            panelGrafo.repaint();
	        }
	    }.execute();
	}
	
	

    private JButton crearBoton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        return b;
    }

    private void cargarGrafo(Grafo grafo) {
        HashMap<String, Nodo> n = new HashMap<>();
        
        String[] filas = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R"};
        int startX = 75, startY = 75;

        for(int i = 0; i < 18; i++) {
            for(int j = 0; j < 18; j++) {
                String nombre = filas[i] + (j+1);
                int x = startX + j * 135;
                int y = startY + i * 135;
                n.put(nombre, new Nodo(nombre, x, y));
            }
        }


        for (Nodo nodo : n.values()) grafo.agregarNodo(nodo);
        
        for(int i = 0; i < 18; i++) {
            for(int j = 0; j < 18; j++) {
                String actual = filas[i] + (j+1);
                if(j+1 < 18) grafo.agregarArista(n.get(actual), n.get(filas[i]+(j+2)), 15);   // derecha
                if(j-1 >= 0)  grafo.agregarArista(n.get(actual), n.get(filas[i]+(j)), 15);     // izquierda
                if(i+1 < 18) {
                    grafo.agregarArista(n.get(actual), n.get(filas[i+1]+(j+1)), 10);           // bajar
                    grafo.agregarArista(n.get(filas[i+1]+(j+1)), n.get(actual), 30);           // subir
                }
                if(i+1 < 18 && j+1 < 18) grafo.agregarArista(n.get(actual), n.get(filas[i+1]+(j+2)), 33); // diagonal abajo-derecha
                if(i+1 < 18 && j-1 >= 0) grafo.agregarArista(n.get(actual), n.get(filas[i+1]+(j)), 33);   // diagonal abajo-izquierda
                if(i-1 >= 0 && j+1 < 18) grafo.agregarArista(n.get(actual), n.get(filas[i-1]+(j+2)), 33); // diagonal arriba-derecha
                if(i-1 >= 0 && j-1 >= 0) grafo.agregarArista(n.get(actual), n.get(filas[i-1]+(j)), 33);   // diagonal arriba-izquierda
            }
        }
        
        String[] jangs = {
        	    "A9","A12","A18",
        	    "B3","B5","B8","B10","B13","B16",
        	    "C3","C7","C15",
        	    "D1","D4","D6","D9","D14",
        	    "E3","E6","E11","E16",
        	    "F1","F3","F8","F10","F13","F18",
        	    "G14","G16",
        	    "H1","H5","H7","H10","H13",
        	    "I3","I8","I12","I14","I16","I18",
        	    "J2","J6","J10","J14",
        	    "K5","K12","K13","K15","K18",
        	    "L2","L4","L10","L16","L18",
        	    "M1","M3","M6","M9","M12","M14",
        	    "N3","N5","N8","N11","N16","N18",
        	    "O1","O15",
        	    "P3","P6","P10",
        	    "Q8","Q13","Q14",
        	    "R1","R5","R9","R10","R18"
        	};

        	for(String id : jangs) grafo.eliminarNodo(id);
        

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Graphvisualizer());
    }
}
