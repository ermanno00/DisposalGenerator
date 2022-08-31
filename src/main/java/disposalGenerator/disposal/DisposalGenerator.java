package disposalGenerator.disposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import disposalGenerator.configuration.Configuration;
import disposalGenerator.model.MongoDAO;
import disposalGenerator.model.entities.TypeOfDisposal;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

import javax.jms.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class DisposalGenerator implements Runnable {

    static Logger log = LogManager.getLogger(DisposalGenerator.class);
    private QueueSession session;
    private Queue queue;
    private QueueSender sender;
    private MongoDAO mongoDAO;

    private Set<DisposalGeneratorCallback> callbackSet = new HashSet<>();//Lista di callback per inviare eventi (all'interfaccia grafica)
    private static DisposalGenerator instance = null;                   //Istanza per oggetto singleton
    private ScheduledExecutorService scheduler;


    private UUID vehicleId;

    public void start(String vehicleId){
        this.vehicleId= UUID.fromString(vehicleId);
        run();
    }


    /**
     * Metodo pubblico per ottenere il singleton TunnelManager
     *
     * @return TunnelManager
     */
    public static synchronized DisposalGenerator getTunnelManager() {
        if (instance == null) {
            instance = new DisposalGenerator();
        }
        return instance;
    }

    /**
     * Costruttore privato del Singleton
     */
    private DisposalGenerator() {
    }

    /**
     * Metodo per la sottoscrizione alla callback
     *
     * @param callback
     */
    public void addListener(DisposalGeneratorCallback callback) {
        callbackSet.add(callback);
    }

    /**
     * Metodo per la rimozione di una callback
     *
     * @param callback
     * @return
     */
    public boolean removeLister(DisposalGeneratorCallback callback) {
        return callbackSet.remove(callback);
    }

    @Override
    public void run() {
        String uri = "tcp://"+ Configuration.artemisHost+":"+Configuration.artemisPort;

        try {
            // this code could be substituted by a lookup operation in a naming service
            QueueConnectionFactory connFactory = new ActiveMQQueueConnectionFactory(uri);
            connFactory.createQueueConnection(Configuration.artemisUsername, Configuration.artemisPassword);

            QueueConnection connection = connFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            /* the code below could be substituted by a lookup operation in a naming service */
            queue = session.createQueue(Configuration.artemisQueueName);
            sender = session.createSender(queue);
            callbackSet.forEach(callback -> callback.onArtemisConnectionStatusChange(true));

            //COLLEGARSI A MONGO
            mongoDAO = new MongoDAO();
            scheduler=Executors.newScheduledThreadPool(1);
            callbackSet.forEach(callback -> callback.onMongoConnectionStatusChange(true));
            scheduler.scheduleAtFixedRate(() -> {
                //ottieni dati dal db e mandali in callback
                var itineraries= mongoDAO.getItinerariesByVehicleId(this.vehicleId);
            },0,Configuration.pollingTime, TimeUnit.SECONDS);

        } catch (JMSException e) {
            log.error(e.getMessage());
        }

    }

    public void sendDisposal(TypeOfDisposal typeOfDisposal, int capacity, UUID collectionPointAt, UUID vehicleIdFrom){

        try {
            DisposalDriver disposalDriver = new DisposalDriver(
                    UUID.randomUUID().toString(),
                    new Date(),
                    typeOfDisposal.name(),
                    capacity,
                    collectionPointAt,  //Collection Point
                    vehicleIdFrom //Vehicle
            );
            String serialized = null;
            serialized = new ObjectMapper().writeValueAsString(disposalDriver);
            TextMessage disposalMessage = session.createTextMessage(serialized);
            sender.send(disposalMessage);
            callbackSet.forEach(callback -> callback.onMessage("Disposal raccolto"));
        } catch (JsonProcessingException | JMSException e) {
            log.error(e.getMessage());
            callbackSet.forEach(callback -> callback.onError(e.getMessage()));
        }

    }

    public void disconnect(){
        try {
            sender.close();
            callbackSet.forEach(callback -> callback.onArtemisConnectionStatusChange(false));
            scheduler.shutdown();
            //DISCOLLEGARSI DA MONGO
            mongoDAO.closeSession();
            callbackSet.forEach(callback -> callback.onMongoConnectionStatusChange(false));



        }catch (Exception e){
            log.error("disconnetion problem: "+e.getMessage());
            callbackSet.forEach(callback -> callback.onError(e.getMessage()));

        }
    }


}
