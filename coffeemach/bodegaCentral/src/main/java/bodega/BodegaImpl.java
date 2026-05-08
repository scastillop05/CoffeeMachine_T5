package bodega;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.util.*;

public class BodegaImpl implements Bodega {

    private final Map<String, Integer> ingredientes = new LinkedHashMap<>();
    private final Map<String, Integer> monedas = new LinkedHashMap<>();
    private final Map<String, Integer> suministros = new LinkedHashMap<>();
    private int kitsReparacion = 5;

    private JTextArea displayArea;

    public BodegaImpl() {
        ingredientes.put("Agua", 10000);
        ingredientes.put("Cafe", 5000);
        ingredientes.put("Azucar", 5000);
        ingredientes.put("Vaso", 200);

        monedas.put("100", 100);
        monedas.put("200", 100);
        monedas.put("500", 100);

        suministros.put("Filtro", 50);
        suministros.put("Manguera", 20);
    }

    public void setDisplayArea(JTextArea area) {
        this.displayArea = area;
    }

    private void mostrar(String texto) {
        if (displayArea != null) {
            displayArea.setText(texto);
        }
    }

    @Override
    public void consultarIngredientes() {
        StringBuilder sb = new StringBuilder("=== Ingredientes ===\n");
        for (Map.Entry<String, Integer> e : ingredientes.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        mostrar(sb.toString());
    }

    @Override
    public void consultarMonedas() {
        StringBuilder sb = new StringBuilder("=== Monedas ===\n");
        for (Map.Entry<String, Integer> e : monedas.entrySet()) {
            sb.append("  $").append(e.getKey()).append(": ").append(e.getValue()).append(" unidades\n");
        }
        mostrar(sb.toString());
    }

    @Override
    public void consultarSuministros() {
        StringBuilder sb = new StringBuilder("=== Suministros ===\n");
        for (Map.Entry<String, Integer> e : suministros.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        sb.append("  Kits de reparación: ").append(kitsReparacion).append("\n");
        mostrar(sb.toString());
    }

    @Override
    public void entregaKitReparacion() {
        if (kitsReparacion > 0) {
            kitsReparacion--;
            mostrar("Kit de reparación entregado.\nQuedan: " + kitsReparacion + " kits.");
        } else {
            mostrar("No hay kits de reparación disponibles.");
        }
    }

    @Override
    public void retirarExistencias() {
        String item = JOptionPane.showInputDialog("Nombre del ítem a retirar:");
        if (item == null || item.trim().isEmpty()) return;
        String cantStr = JOptionPane.showInputDialog("Cantidad a retirar:");
        if (cantStr == null) return;
        try {
            int cant = Integer.parseInt(cantStr.trim());
            mostrar(operarStock(item.trim(), -cant, "retirado"));
        } catch (NumberFormatException e) {
            mostrar("Cantidad inválida.");
        }
    }

    @Override
    public void abastecerExistencia() {
        String item = JOptionPane.showInputDialog("Nombre del ítem a abastecer:");
        if (item == null || item.trim().isEmpty()) return;
        String cantStr = JOptionPane.showInputDialog("Cantidad a agregar:");
        if (cantStr == null) return;
        try {
            int cant = Integer.parseInt(cantStr.trim());
            mostrar(operarStock(item.trim(), cant, "abastecido"));
        } catch (NumberFormatException e) {
            mostrar("Cantidad inválida.");
        }
    }

    @Override
    public void separarExistencias() {
        String item = JOptionPane.showInputDialog("Nombre del ítem a separar:");
        if (item == null || item.trim().isEmpty()) return;
        String cantStr = JOptionPane.showInputDialog("Cantidad a separar para despacho:");
        if (cantStr == null) return;
        try {
            int cant = Integer.parseInt(cantStr.trim());
            String resultado = operarStock(item.trim(), -cant, "separado para despacho");
            mostrar(resultado);
        } catch (NumberFormatException e) {
            mostrar("Cantidad inválida.");
        }
    }

    private String operarStock(String item, int delta, String accion) {
        if (ingredientes.containsKey(item)) {
            int nuevo = ingredientes.get(item) + delta;
            if (nuevo < 0) return "Stock insuficiente de " + item + ". Disponible: " + ingredientes.get(item);
            ingredientes.put(item, nuevo);
            return Math.abs(delta) + " unidades de " + item + " " + accion + ". Stock actual: " + nuevo;
        }
        if (monedas.containsKey(item)) {
            int nuevo = monedas.get(item) + delta;
            if (nuevo < 0) return "Stock insuficiente de monedas $" + item + ". Disponible: " + monedas.get(item);
            monedas.put(item, nuevo);
            return Math.abs(delta) + " monedas de $" + item + " " + accion + ". Stock actual: " + nuevo;
        }
        if (suministros.containsKey(item)) {
            int nuevo = suministros.get(item) + delta;
            if (nuevo < 0) return "Stock insuficiente de " + item + ". Disponible: " + suministros.get(item);
            suministros.put(item, nuevo);
            return Math.abs(delta) + " de " + item + " " + accion + ". Stock actual: " + nuevo;
        }
        return "Ítem no encontrado: " + item;
    }

    public void abastecer(String item, int cantidad) {
        operarStock(item, cantidad, "abastecido");
    }

    public Map<String, Integer> getIngredientes() { return ingredientes; }
    public Map<String, Integer> getMonedas() { return monedas; }
    public Map<String, Integer> getSuministros() { return suministros; }
    public int getKitsReparacion() { return kitsReparacion; }
    public void setKitsReparacion(int n) { this.kitsReparacion = n; }
}
