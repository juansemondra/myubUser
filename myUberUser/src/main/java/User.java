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

        // Crear el contexto y el socket ZeroMQ para el usuario
        context = ZMQ.context(1);
        socket = context.socket(ZMQ.REQ); // Socket Request
        socket.connect("tcp://" + SERVER_IP + ":" + PUERTO_SOLICITUDES); // Conectarse al servidor central
    }

    public void solicitarTaxi() {
        if (!solicitudRealizada) {
            solicitudRealizada = true;
            System.out.println("Usuario " + id + " solicita taxi desde posición: (" + posX + ", " + posY + ")");

            // Enviar solicitud al servidor usando ZeroMQ
            String solicitud = "Usuario " + id + " solicita taxi desde (" + posX + ", " + posY + ")";
            socket.send(solicitud.getBytes(ZMQ.CHARSET));

            // Esperar la respuesta del servidor
            String respuesta = socket.recvStr(0); // Respuesta del servidor
            System.out.println("Respuesta del servidor: " + respuesta);

            if (respuesta.contains("Taxi asignado")) {
                asignarTaxi();
            } else {
                rechazarServicio();
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
        socket.close();
        context.close();
    }
}