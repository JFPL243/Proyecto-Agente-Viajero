package co.edu.upb.agenteviajero;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PanelAdyacencia extends JPanel {

    private final String[] filas = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R"};
    private final Set<String> negros = new HashSet<>(Arrays.asList(
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
    ));
    private final String origenId = "C9";
    private final Set<String> destinosId = new HashSet<>(Arrays.asList(
        "A1","A17","E1","F16","I1","K10","K14","M16","N6","O16","P9","R2","R8","R13"
    ));

    private double zoom = 1.0;
    private double translateX = 0, translateY = 0;
    private int dragStartX, dragStartY;
    private String nodoSeleccionado = null;

    private Grafo grafo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblNodo;

    public PanelAdyacencia(Grafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout());

        // Panel izquierdo — mapa
        JPanel mapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.translate(translateX * zoom, translateY * zoom);
                g2.scale(zoom, zoom);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int tam = 40, ox = 60, oy = 40;

                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.DARK_GRAY);
                for(int j = 0; j < 18; j++) {
                    String label = String.valueOf(j+1);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(label, ox + j*tam + tam/2 - fm.stringWidth(label)/2, oy - 5);
                }
                for(int i = 0; i < 18; i++) {
                    g2.drawString(filas[i], ox - 20, oy + i*tam + tam/2 + 4);
                }

                for(int i = 0; i < 18; i++) {
                    for(int j = 0; j < 18; j++) {
                        String id = filas[i] + (j+1);
                        int x = ox + j*tam, y = oy + i*tam;

                        if(id.equals(nodoSeleccionado)) {
                            g2.setColor(new Color(255, 165, 0));
                        } else if(id.equals(origenId)) {
                            g2.setColor(new Color(80, 180, 80));
                        } else if(destinosId.contains(id)) {
                            g2.setColor(new Color(100, 180, 240));
                        } else if(negros.contains(id)) {
                            g2.setColor(Color.BLACK);
                        } else {
                            g2.setColor(Color.WHITE);
                        }

                        g2.fillRect(x, y, tam, tam);
                        g2.setColor(id.equals(nodoSeleccionado) ? new Color(200, 100, 0) : Color.GRAY);
                        g2.setStroke(new BasicStroke(id.equals(nodoSeleccionado) ? 2f : 0.5f));
                        g2.drawRect(x, y, tam, tam);

                        if(!negros.contains(id)) {
                            g2.setColor(Color.DARK_GRAY);
                            g2.setFont(new Font("Arial", Font.PLAIN, 8));
                            FontMetrics fm = g2.getFontMetrics();
                            g2.drawString(id, x + tam/2 - fm.stringWidth(id)/2, y + tam/2 + 3);
                        }
                    }
                }
            }
        };

        mapa.setBackground(Color.WHITE);

        mapa.addMouseWheelListener(e -> {
            double factor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
            double mouseX = e.getX(), mouseY = e.getY();
            translateX -= mouseX / zoom;
            translateY -= mouseY / zoom;
            zoom *= factor;
            zoom = Math.max(0.2, Math.min(zoom, 5.0));
            translateX += mouseX / zoom;
            translateY += mouseY / zoom;
            mapa.repaint();
        });

        mapa.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();

                // calcular celda clickeada
                double worldX = e.getX() / zoom - translateX;
                double worldY = e.getY() / zoom - translateY;
                int tam = 40, ox = 60, oy = 40;
                int col = (int)((worldX - ox) / tam);
                int row = (int)((worldY - oy) / tam);

                if(row >= 0 && row < 18 && col >= 0 && col < 18) {
                    String id = filas[row] + (col+1);
                    if(!negros.contains(id)) {
                        nodoSeleccionado = id;
                        mostrarAdyacencia(id);
                        mapa.repaint();
                    }
                }
            }
        });

        mapa.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                translateX += (e.getX() - dragStartX) / zoom;
                translateY += (e.getY() - dragStartY) / zoom;
                dragStartX = e.getX();
                dragStartY = e.getY();
                mapa.repaint();
            }
        });

        // Panel derecho — tabla adyacencia
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblNodo = new JLabel("Selecciona un nodo");
        lblNodo.setFont(new Font("Arial", Font.BOLD, 14));
        lblNodo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        modeloTabla = new DefaultTableModel(new String[]{"Vecino", "Dirección", "Costo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setGridColor(new Color(220, 220, 220));

        // alternar colores de filas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if(!sel) setBackground(r % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        panelDerecho.add(lblNodo, BorderLayout.NORTH);
        panelDerecho.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapa, panelDerecho);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);
    }

    private void mostrarAdyacencia(String id) {
        lblNodo.setText("Nodo: " + id);
        modeloTabla.setRowCount(0);

        Nodo nodo = grafo.getNodos().stream()
            .filter(n -> n.getId().equals(id))
            .findFirst().orElse(null);
        if(nodo == null) return;

        for(Arista a : grafo.getAristas()) {
            if(a.getOrigen() == nodo) {
                String vecino = a.getDestino().getId();
                String dir = obtenerDireccion(nodo, a.getDestino());
                modeloTabla.addRow(new Object[]{vecino, dir, a.getPeso()});
            }
        }
    }

    private String obtenerDireccion(Nodo origen, Nodo destino) {
        int dx = destino.getX() - origen.getX();
        int dy = destino.getY() - origen.getY();
        if(dy == 0 && dx > 0) return "Derecha →";
        if(dy == 0 && dx < 0) return "Izquierda ←";
        if(dx == 0 && dy > 0) return "Abajo ↓";
        if(dx == 0 && dy < 0) return "Arriba ↑";
        if(dx > 0 && dy > 0) return "Diagonal abajo-der";
        if(dx < 0 && dy > 0) return "Diagonal abajo-izq";
        if(dx > 0 && dy < 0) return "Diagonal arriba-der";
        if(dx < 0 && dy < 0) return "Diagonal arriba-izq";
        return "?";
    }
}