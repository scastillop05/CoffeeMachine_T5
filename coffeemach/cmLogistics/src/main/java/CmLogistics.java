import java.util.ArrayList;
import java.util.List;
import com.zeroc.Ice.*;
import logistica.ConsolaLogistica;
import servicios.*;

public class CmLogistics {
    public static void main(String[] args) {
        List<String> extArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "CmLogistic.cfg", extArgs)) {

            ServicioComLogisticaPrx serverCentral = ServicioComLogisticaPrx.checkedCast(
                    communicator.propertyToProxy("ServerCentral")).ice_twoway();

            ServicioAbastecimientoPrx maquinaCafe = ServicioAbastecimientoPrx.checkedCast(
                    communicator.propertyToProxy("MaquinaCafe")).ice_twoway();

            new ConsolaLogistica(serverCentral, maquinaCafe).iniciar();
        }
    }
}
