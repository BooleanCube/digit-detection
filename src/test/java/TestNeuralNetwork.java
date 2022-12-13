import com.boole.Constant;
import com.boole.network.NetworkManager;
import com.boole.network.models.Layer;
import com.boole.network.models.LayerType;
import com.boole.network.models.NeuralNetwork;
import com.boole.network.models.Node;
import com.boole.statistics.TestingDisplay;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Arrays;

public class TestNeuralNetwork {

    public static void main(String[] args) throws IOException, ParseException {
        NetworkManager.init();
        System.out.println(Arrays.toString(NetworkManager.getNetwork().runTests()));
        NetworkManager.miniBatchTraining(200, 10);
        System.out.println(Arrays.toString(NetworkManager.getNetwork().runTests()));
    }

}
