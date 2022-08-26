package disposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

import javax.jms.*;
import java.util.Date;
import java.util.UUID;



public class DisposalGenerator {

    private static QueueSession session;
    private static Queue queue;


    public static void main(String[] args) {
        String host = "172.31.1.229";
        Long port = Long.valueOf(32040);
        String queueName = "disposals";

        String uri = "tcp://"+host+":"+port;

        try {
            // this code could be substituted by a lookup operation in a naming service
            QueueConnectionFactory connFactory = new ActiveMQQueueConnectionFactory(uri);
            connFactory.createQueueConnection("artemis", "artemis");

            QueueConnection connection = connFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            /* the code below could be substituted by a lookup operation in a naming service */
            queue = session.createQueue(queueName);
            QueueSender sender = session.createSender(queue);

            System.out.println();

//            for(int i=0; i<1;i++){
//                DisposalDriver disposalDriver = new DisposalDriver(
//                        UUID.randomUUID().toString(),
//                        new Date(),
//                        "GENERAL_WASTE",
//                        100,
//                        UUID.fromString("ae2c98ac-0f2a-48b1-b5f0-477e5c7938bf"),  //Collection Point
//                        UUID.fromString("17a6c27b-771a-4ff7-871f-c1784003cb29")  //Vehicle
//                );
//                String serialized = new ObjectMapper().writeValueAsString(disposalDriver);
//                TextMessage disposalMessage = session.createTextMessage(serialized);
//                sender.send(disposalMessage);
//            }

            sender.close();

        } catch (JMSException /*| JsonProcessingException*/ e) {
            System.err.println("Error " + e);
        }



    }
}
