package co.edu.upb.agenteviajero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class PanelGrafo extends JPanel {
    private Grafo grafo;
    private Nodo  nodoSeleccionado = null;
    private static final int RADIO = 24;

    private List<Nodo>   caminoDijkstra  = null;
    private List<Nodo>   ordenRecorrido  = null;
    private List<Arista> aristasKruskal  = null;
    private int          aristasAnimadas = 0;
    private int          nodoAnimado     = -1;
    private int          distanciaTotal  = 0;
    private String       modoActual      = "";

    private double zoom       = 1.0;
    private double translateX = 0, translateY = 0;

    private Timer timerAnimacion;
   
    private final String origenId = "B9";
    private final List<String> destinosId = Arrays.asList(
    "A1","A17","E1","F16","I1","K10","K14","M16","N6","O16","P9","R2","R8","R13"
);
    private JLabel lblEstado;
    
    private int dragStartX, dragStartY;
    private boolean dragging = false;
    

    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(Color.WHITE);

        addMouseWheelListener(e -> {
            double factor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            double mouseX = e.getX();
            double mouseY = e.getY();
            translateX -= mouseX / zoom;
            translateY -= mouseY / zoom;
            zoom *= factor;
            zoom = Math.max(0.2, Math.min(zoom, 5.0));
            translateX += mouseX / zoom;
            translateY += mouseY / zoom;
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                
                dragStartX = e.getX();
                dragStartY = e.getY();
                dragging = false;
            }
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                dragging = true;
                translateX += (e.getX() - dragStartX) / zoom;
                translateY += (e.getY() - dragStartY) / zoom;
                dragStartX = e.getX();
                dragStartY = e.getY();
                repaint();
            }
        });
    }

    public void zoomIn()  { zoom *= 1.1; zoom = Math.min(zoom, 5.0); repaint(); }
    public void zoomOut() { zoom *= 0.9; zoom = Math.max(zoom, 0.2); repaint(); }

    public void ejecutarDijkstra(PanelTabla panelTabla) {
    limpiar();
    
    Nodo origen = buscarPorId(origenId);
    if(origen == null) {
        JOptionPane.showMessageDialog(this, "Nodo origen no encontrado."); return;
    }

    String[] destinos = destinosId.toArray(new String[0]);
    String destinoId = (String) JOptionPane.showInputDialog(this,
        "Nodo DESTINO:", "Dijkstra", JOptionPane.PLAIN_MESSAGE,
        null, destinos, destinos[0]);
    if(destinoId == null) return;

    Nodo destino = buscarPorId(destinoId);

    Dijkstra.ejecutar(grafo, origen, destino);
    caminoDijkstra = Dijkstra.camino;
    distanciaTotal = Dijkstra.distancias.get(destino);
    modoActual     = "DIJKSTRA";

    if(caminoDijkstra.size() <= 1) {
        JOptionPane.showMessageDialog(this, "No existe camino.");
        caminoDijkstra = null;
    }

    panelTabla.mostrarDijkstra(Dijkstra.distancias, Dijkstra.anteriores, caminoDijkstra);
    repaint();
    
    if(lblEstado != null)
        lblEstado.setText("Camino: " + caminoDijkstra.stream()
            .map(Nodo::getId)
            .reduce((a,b) -> a + " → " + b).orElse("") +
            "   |   Distancia: " + distanciaTotal);
}

    public void ejecutarKruskal(PanelKruskal panelKruskal) {
        limpiar();
        if (grafo.getNodos().size() < 2) {
            JOptionPane.showMessageDialog(this, "Necesitas al menos 2 nodos."); return;
        }
        Kruskal.ejecutar(grafo);
        aristasKruskal  = new ArrayList<>(Kruskal.aristasArbol);
        aristasAnimadas = 0;
        modoActual      = "KRUSKAL";
        panelKruskal.mostrarKruskal(aristasKruskal, Kruskal.pesoTotal);
        animarKruskal();
        
        if(lblEstado != null)
            lblEstado.setText("Kruskal — Peso total: " + Kruskal.pesoTotal);
    }

    private void animarKruskal() {
        aristasAnimadas = 0;
        timerAnimacion  = new Timer(25, null);
        timerAnimacion.addActionListener(e -> {
            aristasAnimadas++;
            repaint();
            if (aristasAnimadas >= aristasKruskal.size()) timerAnimacion.stop();
        });
        timerAnimacion.start();
        repaint();
    }

    public void limpiar() {
        if (timerAnimacion != null) timerAnimacion.stop();
        caminoDijkstra  = null;
        ordenRecorrido  = null;
        aristasKruskal  = null;
        aristasAnimadas = 0;
        nodoAnimado     = -1;
        modoActual      = "";
        repaint();
    }

    private Nodo buscarPorId(String id) {
        return grafo.getNodos().stream()
            .filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }


    private boolean aristaEnCamino(Arista a, List<Nodo> camino) {
        if (camino == null || camino.size() < 2) return false;
        for (int i = 0; i < camino.size() - 1; i++) {
            Nodo u = camino.get(i), v = camino.get(i + 1);
            if ((a.getOrigen() == u && a.getDestino() == v) ||
                (a.getOrigen() == v && a.getDestino() == u)) return true;
        }
        return false;
    }

    private boolean aristaEnKruskal(Arista a) {
        if (aristasKruskal == null) return false;
        int limite = Math.min(aristasAnimadas, aristasKruskal.size());
        for (int i = 0; i < limite; i++) {
            Arista k = aristasKruskal.get(i);
            if ((k.getOrigen() == a.getOrigen() && k.getDestino() == a.getDestino()) ||
                (k.getOrigen() == a.getDestino() && k.getDestino() == a.getOrigen()))
                return true;
        }
        return false;
    }
    
    public void setEstado(JLabel label, String texto) {
        label.setText(texto);
    }	

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(translateX * zoom, translateY * zoom);
        g2.scale(zoom, zoom);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     // PRIMER LOOP — solo aristas
        for (Arista a : grafo.getAristas()) {
            int x1 = a.getOrigen().getX(),  y1 = a.getOrigen().getY();
            int x2 = a.getDestino().getX(), y2 = a.getDestino().getY();

            boolean enDijkstra = aristaEnCamino(a, caminoDijkstra);
            boolean enKruskal  = aristaEnKruskal(a);

            Color colorArista; float grosor;
            if      (enDijkstra) { colorArista = new Color(34, 180, 90);  grosor = 3.5f; }
            else if (enKruskal)  { colorArista = new Color(160, 80, 200); grosor = 3.5f; }
            else                 { colorArista = Color.GRAY;              grosor = 1.0f; }

            g2.setColor(colorArista);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawLine(x1, y1, x2, y2);
        }

        // SEGUNDO LOOP — solo pesos encima
        for (Arista a : grafo.getAristas()) {
            int x1 = a.getOrigen().getX(),  y1 = a.getOrigen().getY();
            int x2 = a.getDestino().getX(), y2 = a.getDestino().getY();

            double dx = x2 - x1, dy = y2 - y1;
            double len = Math.sqrt(dx*dx + dy*dy);
            double offset = 40;
            int mx = x1 + (int)(dx / len * offset);
            int my = y1 + (int)(dy / len * offset);

            boolean enDijkstra = aristaEnCamino(a, caminoDijkstra);
            boolean enKruskal  = aristaEnKruskal(a);

            Color colorPeso = enDijkstra ? new Color(20, 140, 60)
                            : enKruskal  ? new Color(120, 40, 180)
                            : new Color(180, 50, 50);

            g2.setColor(Color.WHITE);
            g2.fillOval(mx - 13, my - 13, 26, 26);
            g2.setColor(colorPeso);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawOval(mx - 13, my - 13, 26, 26);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String ps = String.valueOf(a.getPeso());
            g2.drawString(ps, mx - fm.stringWidth(ps) / 2, my + 5);
            
            if(enDijkstra) {
                // verificar si esta arista va en dirección contraria al camino
                boolean contraria = false;
                for(int i = 0; i < caminoDijkstra.size()-1; i++) {
                    Nodo u = caminoDijkstra.get(i);
                    Nodo v = caminoDijkstra.get(i+1);
                    if(a.getOrigen() == v && a.getDestino() == u) {
                        contraria = true; break;
                    }
                }
                if(contraria) {
                    g2.setColor(new Color(220, 0, 0));
                    g2.setFont(new Font("Arial", Font.BOLD, 28));
                    g2.drawString("X", mx - 9, my + 10);
                }
            }
        
        }
        g2.setStroke(new BasicStroke(1.5f));
        for (Nodo n : grafo.getNodos()) {
            Color relleno;
            if(n.getId().equals(origenId)) {
                relleno = new Color(255, 220, 0);
            } else if(destinosId.contains(n.getId())) {
                relleno = new Color(255, 105, 180);
            } else {
                relleno = new Color(100, 149, 237);
            }

            switch (modoActual) {
                case "DIJKSTRA":
                    if (caminoDijkstra != null) {
                        if (n == caminoDijkstra.get(0))
                            relleno = new Color(34, 180, 90);
                        else if (n == caminoDijkstra.get(caminoDijkstra.size() - 1))
                            relleno = new Color(220, 60, 60);
                        else if (caminoDijkstra.contains(n))
                            relleno = new Color(255, 200, 0);
                    }
                    break;
                case "KRUSKAL":
                    if (aristasKruskal != null) {
                        int limite = Math.min(aristasAnimadas, aristasKruskal.size());
                        for (int i = 0; i < limite; i++) {
                            Arista k = aristasKruskal.get(i);
                            if (k.getOrigen() == n || k.getDestino() == n) {
                                relleno = new Color(190, 120, 230); break;
                            }
                        }
                    }
                    break;
            }

            if (n == nodoSeleccionado) relleno = new Color(255, 165, 0);

            // dibujar círculo UNA sola vez
            g2.setColor(relleno);
            g2.fillOval(n.getX() - RADIO, n.getY() - RADIO, RADIO * 2, RADIO * 2);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(n.getX() - RADIO, n.getY() - RADIO, RADIO * 2, RADIO * 2);

            // texto al final encima del círculo
            if(n.getId().equals(origenId) || destinosId.contains(n.getId())) {
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(n.getId(),
                    n.getX() - fm.stringWidth(n.getId()) / 2, n.getY() + 5);
            
        }

        }

    }
    
    public void setLblEstado(JLabel label) {
        this.lblEstado = label;
    }
    }
