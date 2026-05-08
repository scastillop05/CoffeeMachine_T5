package mantenimientoExistencias;

import bodega.BodegaImpl;

public class InventarioImpl implements Inventario {

    private static final int DEFAULT_INGREDIENTES = 5000;
    private static final int DEFAULT_MONEDAS = 50;
    private static final int DEFAULT_SUMINISTROS = 10;

    private final BodegaImpl bodega;

    public InventarioImpl(BodegaImpl bodega) {
        this.bodega = bodega;
    }

    @Override
    public void abastecerIngredientes() {
        for (String key : bodega.getIngredientes().keySet()) {
            bodega.abastecer(key, DEFAULT_INGREDIENTES);
        }
        bodega.consultarIngredientes();
    }

    @Override
    public void abastecerMonedas() {
        for (String key : bodega.getMonedas().keySet()) {
            bodega.abastecer(key, DEFAULT_MONEDAS);
        }
        bodega.consultarMonedas();
    }

    @Override
    public void abastecerSuministros() {
        for (String key : bodega.getSuministros().keySet()) {
            bodega.abastecer(key, DEFAULT_SUMINISTROS);
        }
        bodega.setKitsReparacion(bodega.getKitsReparacion() + 5);
        bodega.consultarSuministros();
    }
}
