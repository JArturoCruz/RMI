package cliente;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import compute.Compute;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;

public class ComputePi {
    public static void main(String args[]) {
        String myIP = "localhost";      // <--- PON AQUÍ LA IP DE ESTA MÁQUINA (CLIENTE)
        String serverIP = "localhost";  // <--- PON AQUÍ LA IP DE LA MÁQUINA SERVIDOR

        startWebServer(myIP);

        System.setProperty("java.rmi.server.hostname", myIP);
        System.setProperty("java.rmi.server.codebase", "http://" + myIP + ":8000/client.jar");

        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry(serverIP);
            Compute comp = (Compute) registry.lookup(name);

            int digits = (args.length > 1) ? Integer.parseInt(args[1]) : 45;

            System.out.println("Enviando tarea Pi (" + digits + " digitos) al servidor " + serverIP + "...");
            Pi task = new Pi(digits);
            BigDecimal pi = comp.executeTask(task);

            System.out.println("Resultado recibido: " + pi);

            System.exit(0);

        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void startWebServer(String ip) {
        try {
            int port = 8000;
            Path path = Path.of(System.getProperty("user.dir"), "out/production/RMI");

            var server = SimpleFileServer.createFileServer(
                    new InetSocketAddress(port),
                    path,
                    OutputLevel.INFO
            );
            server.start();
            System.out.println("Servidor Web (Cliente) activo en http://" + ip + ":" + port + "/");
        } catch (Exception e) {
            System.err.println("Advertencia Servidor Web: " + e.getMessage());
        }
    }
}