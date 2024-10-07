import org.zeromq.ZMQ;

public class UserApp {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PUERTO_SOLICITUDES = 5557;

    private int id;
    private int posX;
    private int posY;
    private int tiempoHastaSolicitud; // en segundos (simulando minutos)
    private boolean servicioAsignado;
    private boolean solicitudRealizada;
    private long tiempoRespuesta;

    private ZMQ.Context context;
    private ZMQ.Socket socket;

    public UserApp(int id, int posX, int posY, int tiempoHastaSolicitud) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.tiempoHastaSolicitud = tiempoHastaSolicitud;
        this.servicioAsignado = false;
        this.solicitudRealizada = false;

        // Inicializar contexto y socket para ZeroMQ
        this.context = ZMQ.context(1);
        this.socket = context.socket(ZMQ.REQ);
        this.socket.connect("tcp://" + SERVER_IP + ":" + PUERTO_SOLICITUDES);
    }

    public void solicitarTaxi() {
        try {
            // Simular el tiempo antes de la solicitud (tiempoHastaSolicitud representa minutos)
            Thread.sleep(tiempoHastaSolicitud * 1000); // Conversión a segundos

            // Enviar la solicitud al servidor
            String solicitud = String.format("%d,%d,%d", id, posX, posY);
            System.out.println("Usuario " + id + " enviando solicitud desde (" + posX + ", " + posY + ")");
            long tiempoInicio = System.currentTimeMillis();
            socket.send(solicitud.getBytes(ZMQ.CHARSET), 0);

            // Esperar respuesta del servidor
            String respuesta = socket.recvStr();
            long tiempoFin = System.currentTimeMillis();
            tiempoRespuesta = tiempoFin - tiempoInicio;

            // Procesar la respuesta del servidor
            if ("OK".equals(respuesta)) {
                servicioAsignado = true;
                System.out.println("Usuario " + id + " ha recibido un taxi. Tiempo de respuesta: " + tiempoRespuesta + "ms");
            } else {
                System.out.println("Usuario " + id + " no recibió un taxi. Respuesta del servidor: " + respuesta);
            }

            solicitudRealizada = true;
        } catch (InterruptedException e) {
            System.err.println("Usuario " + id + " fue interrumpido antes de realizar la solicitud.");
        } finally {
            // Cerrar el socket y contexto de ZeroMQ
            socket.close();
            context.close();
        }
    }

    public static void main(String[] args) {
        // Ejemplo de creación de un usuario con ID 1, posición (2, 3) y tiempo de espera de 5 minutos 
        // Ejemplo de creación de un usuario con ID 1, posición (2, 3) y tiempo de espera de 5 minutos
        UserApp user = new UserApp(1, 2, 3, 5);
        user.solicitarTaxi();
    }
}

