import org.zeromq.ZMQ;

public class User {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PUERTO_SOLICITUDES = 5557;

    private int id;
    private int posX;
    private int posY;
    private int tiempoHastaSolicitud;
    private boolean servicioAsignado;
    private boolean solicitudRealizada;
    private long tiempoRespuesta;

    private ZMQ.Context context;
    private ZMQ.Socket socket;

    public User(int id, int posX, int posY, int tiempoHastaSolicitud) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.tiempoHastaSolicitud = tiempoHastaSolicitud;
        this.servicioAsignado = false;
        this.solicitudRealizada = false;
        this.tiempoRespuesta = -1;

        try {
            context = ZMQ.context(1);
            socket = context.socket(ZMQ.REQ);
            socket.connect("tcp://" + SERVER_IP + ":" + PUERTO_SOLICITUDES);
            System.out.println("Usuario " + id + " conectado exitosamente al servidor de solicitudes en el puerto " + PUERTO_SOLICITUDES);
        } catch (Exception e) {
            System.err.println("Error al conectar el usuario " + id + " al servidor: " + e.getMessage());
        }
    }

    public void solicitarTaxi() {
        if (!solicitudRealizada) {
            solicitudRealizada = true;

            try {
                System.out.println("Usuario " + id + " espera " + tiempoHastaSolicitud + " segundos antes de solicitar un taxi...");
                Thread.sleep(tiempoHastaSolicitud * 1000);

                long tiempoInicio = System.currentTimeMillis();

                System.out.println("Usuario " + id + " solicita taxi desde posición: " + posX + "," + posY);
                String solicitud = "Usuario " + id + " solicita taxi desde " + posX + "," + posY;
                socket.send(solicitud.getBytes(ZMQ.CHARSET));

                String respuesta = socket.recvStr(0);
                registrarTiempoRespuesta(tiempoInicio);

                System.out.println("Respuesta del servidor: " + respuesta);

                if (respuesta.contains("Taxi asignado")) {
                    asignarTaxi();
                } else {
                    rechazarServicio();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Solicitud del usuario " + id + " interrumpida.");
            } catch (Exception e) {
                System.err.println("Error en la solicitud del usuario " + id + ": " + e.getMessage());
            }
        }
    }

    public void registrarTiempoRespuesta(long tiempoInicio) {
        tiempoRespuesta = System.currentTimeMillis() - tiempoInicio;
        System.out.println("Usuario " + id + " recibió respuesta en " + tiempoRespuesta + " milisegundos.");
    }

    public void asignarTaxi() {
        servicioAsignado = true;
        System.out.println("Usuario " + id + " ha recibido un taxi.");
    }

    public void rechazarServicio() {
        servicioAsignado = false;
        System.out.println("Usuario " + id + " no ha recibido un taxi. Solicitud rechazada.");
    }

    public void close() {
        try {
            socket.close();
            context.close();
            System.out.println("Conexión del usuario " + id + " cerrada exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión del usuario " + id + ": " + e.getMessage());
        }
    }
}