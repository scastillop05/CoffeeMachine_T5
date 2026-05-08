package logistica;

import java.util.*;
import servicios.*;

public class ConsolaLogistica {

    private final ServicioComLogisticaPrx serverCentral;
    private final ServicioAbastecimientoPrx maquinaCafe;
    private int codigoOperador;

    public ConsolaLogistica(ServicioComLogisticaPrx sc, ServicioAbastecimientoPrx mq) {
        this.serverCentral = sc;
        this.maquinaCafe = mq;
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================");
        System.out.println("  Sistema de Logística CoffeeMach  ");
        System.out.println("====================================");

        autenticar(scanner);

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Ver máquinas asignadas");
            System.out.println("2. Ver máquinas con alarmas activas");
            System.out.println("3. Resolver alarma (abastecer máquina)");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                switch (opcion) {
                    case 1: listarMaquinas(); break;
                    case 2: listarAlarmas(); break;
                    case 3: resolverAlarma(scanner); break;
                    case 4: continuar = false; break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
        System.out.println("Sesión finalizada.");
    }

    private void autenticar(Scanner scanner) {
        boolean autenticado = false;
        while (!autenticado) {
            System.out.print("Código de operador: ");
            String codStr = scanner.nextLine().trim();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine().trim();
            try {
                codigoOperador = Integer.parseInt(codStr);
                autenticado = serverCentral.inicioSesion(codigoOperador, password);
                if (!autenticado) {
                    System.out.println("Credenciales incorrectas. Intente de nuevo.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Código inválido.\n");
            }
        }
        System.out.println("Bienvenido, operador " + codigoOperador + ".\n");
    }

    private void listarMaquinas() {
        List<String> maquinas = serverCentral.asignacionMaquina(codigoOperador);
        System.out.println("\n--- Máquinas Asignadas ---");
        if (maquinas == null || maquinas.isEmpty()) {
            System.out.println("  No tiene máquinas asignadas.");
            return;
        }
        for (String m : maquinas) {
            String[] parts = m.split("-", 2);
            System.out.println("  ID: " + parts[0] + "  |  Ubicación: "
                    + (parts.length > 1 ? parts[1] : ""));
        }
    }

    private List<String> listarAlarmas() {
        List<String> alarmas = serverCentral.asignacionMaquinasDesabastecidas(codigoOperador);
        System.out.println("\n--- Máquinas con Alarmas Activas ---");
        if (alarmas == null || alarmas.isEmpty()) {
            System.out.println("  No hay alarmas activas.");
            return Collections.emptyList();
        }
        int i = 1;
        for (String a : alarmas) {
            // idMaquina#ubicacion#fechaInicial#idAlarma#descripcion
            String[] p = a.split("#");
            System.out.printf("  [%d] Máquina %s | %s | Alarma ID: %s | Tipo: %s | Fecha: %s%n",
                    i++, p[0], p[1], p[3], p[4], p[2]);
        }
        return alarmas;
    }

    private void resolverAlarma(Scanner scanner) {
        List<String> alarmas = listarAlarmas();
        if (alarmas.isEmpty()) return;

        System.out.print("ID de la máquina a abastecer: ");
        String codStr = scanner.nextLine().trim();
        System.out.print("ID de la alarma a resolver: ");
        String alarmaStr = scanner.nextLine().trim();

        try {
            int codMaquina = Integer.parseInt(codStr);
            int idAlarma = Integer.parseInt(alarmaStr);
            System.out.println("Enviando orden de abastecimiento a máquina " + codMaquina + "...");
            maquinaCafe.abastecer(codMaquina, idAlarma);
            System.out.println("Orden enviada exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println("Valores inválidos.");
        } catch (Exception e) {
            System.out.println("Error al enviar orden: " + e.getMessage());
        }
    }
}
