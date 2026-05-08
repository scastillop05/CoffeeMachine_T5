package guiInventario;

import bodega.BodegaImpl;
import mantenimientoExistencias.InventarioImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Interfaz extends JFrame {

    private final BodegaImpl bodega;
    private final InventarioImpl inventario;

    private JTextArea textAreaResultado;

    public Interfaz() {
        bodega = new BodegaImpl();
        inventario = new InventarioImpl(bodega);

        setTitle("Bodega Central - CoffeeMach");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Panel de resultado
        textAreaResultado = new JTextArea();
        textAreaResultado.setEditable(false);
        textAreaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(textAreaResultado);
        scroll.setBorder(new TitledBorder("Resultado"));
        scroll.setPreferredSize(new Dimension(680, 200));
        contentPane.add(scroll, BorderLayout.CENTER);

        bodega.setDisplayArea(textAreaResultado);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(4, 3, 8, 8));
        panelBotones.setBorder(new TitledBorder("Operaciones"));
        contentPane.add(panelBotones, BorderLayout.NORTH);

        JButton btnConsultarIng = new JButton("Consultar Ingredientes");
        JButton btnConsultarMon = new JButton("Consultar Monedas");
        JButton btnConsultarSum = new JButton("Consultar Suministros");
        JButton btnEntregarKit  = new JButton("Entregar Kit Reparación");
        JButton btnRetirar      = new JButton("Retirar Existencias");
        JButton btnAbastecer    = new JButton("Abastecer Existencia");
        JButton btnSeparar      = new JButton("Separar Existencias");
        JButton btnAbastecerIng = new JButton("Reabastecer Ingredientes");
        JButton btnAbastecerMon = new JButton("Reabastecer Monedas");
        JButton btnAbastecerSum = new JButton("Reabastecer Suministros");

        panelBotones.add(btnConsultarIng);
        panelBotones.add(btnConsultarMon);
        panelBotones.add(btnConsultarSum);
        panelBotones.add(btnEntregarKit);
        panelBotones.add(btnRetirar);
        panelBotones.add(btnAbastecer);
        panelBotones.add(btnSeparar);
        panelBotones.add(btnAbastecerIng);
        panelBotones.add(btnAbastecerMon);
        panelBotones.add(btnAbastecerSum);

        btnConsultarIng.addActionListener(e -> bodega.consultarIngredientes());
        btnConsultarMon.addActionListener(e -> bodega.consultarMonedas());
        btnConsultarSum.addActionListener(e -> bodega.consultarSuministros());
        btnEntregarKit.addActionListener(e  -> bodega.entregaKitReparacion());
        btnRetirar.addActionListener(e      -> bodega.retirarExistencias());
        btnAbastecer.addActionListener(e    -> bodega.abastecerExistencia());
        btnSeparar.addActionListener(e      -> bodega.separarExistencias());
        btnAbastecerIng.addActionListener(e -> inventario.abastecerIngredientes());
        btnAbastecerMon.addActionListener(e -> inventario.abastecerMonedas());
        btnAbastecerSum.addActionListener(e -> inventario.abastecerSuministros());

        textAreaResultado.setText("Bodega Central iniciada.\nSeleccione una operación.");
    }
}
