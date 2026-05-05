package co.edu.upb.agenteviajero;

import javax.swing.*;
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
    private final String origenId = "B9";
    private final Set<String> destinosId = new HashSet<>(Arrays.asList(
        "A1","A17","E1","F16","I1","K10","K14","M16","N6","O16","P9","R2","R8","R13"
    ));

    private double zoom = 1.0;
    private double translateX = 0, translateY = 0;
    private int dragStartX, dragStartY;

    public PanelAdyacencia() {
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
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                translateX += (e.getX() - dragStartX) / zoom;
                translateY += (e.getY() - dragStartY) / zoom;
                dragStartX = e.getX();
                dragStartY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(translateX * zoom, translateY * zoom);
        g2.scale(zoom, zoom);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tamano = 40;
        int offsetX = 60;
        int offsetY = 40;

        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(Color.DARK_GRAY);
        for(int j = 0; j < 18; j++) {
            String label = String.valueOf(j+1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label,
                offsetX + j * tamano + tamano/2 - fm.stringWidth(label)/2,
                offsetY - 5);
        }

        for(int i = 0; i < 18; i++) {
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(filas[i],
                offsetX - 20,
                offsetY + i * tamano + tamano/2 + 4);
        }

        for(int i = 0; i < 18; i++) {
            for(int j = 0; j < 18; j++) {
                String id = filas[i] + (j+1);
                int x = offsetX + j * tamano;
                int y = offsetY + i * tamano;

                if(id.equals(origenId)) {
                    g2.setColor(new Color(80, 180, 80)); // verde
                } else if(destinosId.contains(id)) {
                    g2.setColor(new Color(100, 180, 240)); // azul claro
                } else if(negros.contains(id)) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }

                g2.fillRect(x, y, tamano, tamano);
                g2.setColor(Color.GRAY);
                g2.drawRect(x, y, tamano, tamano);

                if(!negros.contains(id)) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.setFont(new Font("Arial", Font.PLAIN, 8));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(id, x + tamano/2 - fm.stringWidth(id)/2, y + tamano/2 + 3);
                }
            }
        }
    }
}