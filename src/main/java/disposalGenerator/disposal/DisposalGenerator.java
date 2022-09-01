package disposalGenerator.disposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import disposalGenerator.configuration.Configuration;
import disposalGenerator.GUI.MainFrame;
import disposalGenerator.model.MongoDAO;
import disposalGenerator.model.entities.CollectionPointStatusEntity;
import disposalGenerator.model.entities.Coordinates;
import disposalGenerator.model.entities.ItineraryEntity;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DisposalGenerator implements Runnable {

    static Logger log = LogManager.getLogger(DisposalGenerator.class);
    private QueueSession session;
    private Queue queue;
    private QueueSender sender;
    private MongoDAO mongoDAO;

    private static Set<DisposalGeneratorCallback> callbackSet = new HashSet<>();//Lista di callback per inviare eventi (all'interfaccia grafica)
    private static DisposalGenerator instance = null;                   //Istanza per oggetto singleton
    private ScheduledExecutorService scheduler;

    private boolean mongoConnectionStatus = false;
    private boolean artemisConnectionStatus = false;

    private Semaphore semaphore;


    private UUID vehicleId;

    public void start(String vehicleId) {
        System.out.println(vehicleId);
        this.vehicleId = UUID.fromString(vehicleId);
        run();
    }


    /**
     * Metodo pubblico per ottenere il singleton TunnelManager
     *
     * @return TunnelManager
     */
    public static synchronized DisposalGenerator getDisposalGenerator() {
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
        String uri = "tcp://" + Configuration.artemisHost + ":" + Configuration.artemisPort;

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

            semaphore = new Semaphore(1);

            callbackSet.forEach(callback -> callback.onMongoConnectionStatusChange(true));

            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                callbackSet.forEach(callback -> callback.onRescheduledUpdateData(Configuration.pollingTime));
                updateNow(MainFrame.selectedRoute);
            }, 0, Configuration.pollingTime, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error(e.getMessage());
            callbackSet.forEach(callback -> callback.onError(e.getMessage()));
        }

    }

    public synchronized void updateNow(String routeId) {

        try {
            semaphore.acquire();
            callbackSet.forEach(callback -> callback.onRescheduledUpdateData(-100));
            log.info("Recupero dati dal database");
            List<ItineraryEntity> itineraries = mongoDAO.getItinerariesByVehicleId(vehicleId);
            log.info("Trovate "+itineraries.size()+ " rotte per il veicolo "+vehicleId);
            callbackSet.forEach(callback -> callback.onRoutes(itineraries));

            //AGGIORNARE STATO DI TUTTE LE ROTTE

            if (routeId != "") {
                log.info("Rotta selezionata: " +routeId);
                ItineraryEntity itinerary = itineraries.stream().filter(itineraryEntity -> itineraryEntity.getId().equals(UUID.fromString(routeId))).findFirst().get();
                List<UUID>ids = new ArrayList<>();
                for (Coordinates c : itinerary.getCoordinates()) {
                    if (c.getCollectionPointId() != null) {
                        ids.add(c.getCollectionPointId());
                    }
                }
                List<CollectionPointStatusEntity> collectionPointStatusEntities = mongoDAO.getCollectionPointStatusByIDIn(ids);
                log.info("Collection point trovati: "+collectionPointStatusEntities.size());
                callbackSet.forEach(callback -> callback.onCollectionPoint(collectionPointStatusEntities));
            }
            log.info("Pronto per una nuova ricerca");
            callbackSet.forEach(callback -> callback.onRescheduledUpdateData(-101));
            semaphore.release();
        } catch (Exception e) {
            log.error(e.getMessage());
            callbackSet.forEach(callback -> callback.onRescheduledUpdateData(-102));
            callbackSet.forEach(callback -> callback.onError(e.getMessage()));
            throw new RuntimeException(e);
        }


    }

    public void sendDisposal(String typeOfDisposal, int capacity, UUID collectionPointAt, UUID vehicleIdFrom) {

        try {
            DisposalDriver disposalDriver = new DisposalDriver(
                    UUID.randomUUID().toString(),
                    new Date(),
                    typeOfDisposal,
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

    public void disconnect() throws Exception {

        if (sender != null) {
            sender.close();
        }
        callbackSet.forEach(callback -> callback.onArtemisConnectionStatusChange(false));
        if (scheduler != null) scheduler.shutdown();
        //DISCOLLEGARSI DA MONGO
        if (mongoDAO != null) mongoDAO.closeSession();
        callbackSet.forEach(callback -> callback.onMongoConnectionStatusChange(false));


    }


}
