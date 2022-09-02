/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package disposalGenerator.GUI;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import disposalGenerator.disposal.DisposalGenerator;
import disposalGenerator.disposal.DisposalGeneratorCallback;
import disposalGenerator.model.entities.CollectionPointStatusEntity;
import disposalGenerator.model.entities.Coordinates;
import disposalGenerator.model.entities.ItineraryEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author erman
 */
public class MainFrame extends javax.swing.JFrame {
    static Logger log = LogManager.getLogger(MainFrame.class);
    private DisposalGenerator disposalGenerator;

    public static String selectedRoute = "";

    private boolean mongoConnectionStatus = false;
    private boolean artemisConnectionStatus = false;

    private boolean sleep = false;

    private ScheduledExecutorService scheduler;


    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Chiusura app...");
            try {
                DisposalGenerator.getDisposalGenerator().disconnect();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.info("Terminato");
            LogManager.shutdown();
        }));


    }

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        disposalGenerator = DisposalGenerator.getDisposalGenerator();
        disposalListener();
    }

    private void disposalListener() {
        disposalGenerator.addListener(new DisposalGeneratorCallback() {
            @Override
            public void onMongoConnectionStatusChange(boolean connected) {
                mongoConnectionStatus = connected;

                if (connected) {
                    jLabelMongoConnectionStatus.setText("CONNESSO");

                    if (artemisConnectionStatus) {

                        jButtonDisconnect.setEnabled(true);
                        jTextFieldVehicleUUID.setEditable(false);
                        jButtonConnect.setEnabled(false);

                        jButtonSEND.setEnabled(true);
                        jButtonUPDATENOW.setEnabled(true);

                        DefaultTableModel tblModel = (DefaultTableModel) jTableRoute.getModel();
                        tblModel.setRowCount(0); //Metodo per svuotare la tabella

                        tblModel = (DefaultTableModel) jTableCP.getModel();
                        tblModel.setRowCount(0); //Metodo per svuotare la tabella

                    }
                } else {
                    jLabelMongoConnectionStatus.setText("DISCONNESSO");
                }

            }

            @Override
            public void onArtemisConnectionStatusChange(boolean connected) {
                artemisConnectionStatus = connected;

                if (connected) {
                    jLabelArtemisConnectionStatus.setText("CONNESSO");
                    if (mongoConnectionStatus) {

                        jButtonDisconnect.setEnabled(true);
                        jTextFieldVehicleUUID.setEditable(false);
                        jButtonConnect.setEnabled(false);

                        jButtonSEND.setEnabled(true);
                        jButtonUPDATENOW.setEnabled(true);

                        DefaultTableModel tblModel = (DefaultTableModel) jTableRoute.getModel();
                        tblModel.setRowCount(0); //Metodo per svuotare la tabella

                        tblModel = (DefaultTableModel) jTableCP.getModel();
                        tblModel.setRowCount(0); //Metodo per svuotare la tabella

                    }
                } else {
                    jLabelArtemisConnectionStatus.setText("DISCONNESSO");
                }

            }

            @Override
            public void onError(String error) {
                log.error(error);
                JOptionPane.showMessageDialog(null, error, "Errore", JOptionPane.ERROR_MESSAGE);

            }

            @Override
            public void onMessage(String message) {
                log.info(message);
                JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);

            }

            @Override
            public void onRoutes(List<ItineraryEntity> itineraryEntities) {


                DefaultTableModel tblModel = (DefaultTableModel) jTableRoute.getModel();
                tblModel.setRowCount(0); //Metodo per svuotare la tabella

                for (ItineraryEntity itineraryEntity : itineraryEntities) {
                    String data[] = {itineraryEntity.getId().toString(), itineraryEntity.getState().toString(), String.valueOf(itineraryEntity.getServedNodes().size()-2), String.valueOf(itineraryEntity.getTimestamp()), String.valueOf(itineraryEntity.getCost())};
                    tblModel.insertRow(0, data);
                }


            }

            @Override
            public void onCollectionPoint(List<CollectionPointStatusEntity> collectionPointStatusEntities) {

                DefaultTableModel tblModel = (DefaultTableModel) jTableCP.getModel();
                tblModel.setRowCount(0); //Metodo per svuotare la tabella


                for (CollectionPointStatusEntity collectionPointStatus : collectionPointStatusEntities) {


                    String data[] = {collectionPointStatus.getId().toString(), String.valueOf(collectionPointStatus.getAverageDemand()), String.valueOf(collectionPointStatus.getEffectiveDemand()), collectionPointStatus.isRouted()?"ROUTED":"NOT ROUTED"};
                    tblModel.insertRow(0, data);


                }

            }

            @Override
            public void onRescheduledUpdateData(int timeSeconds) {


                if (timeSeconds == -100) {
                    jLabel3.setText("Recupero dati");
                    sleep = true;
                } else if (timeSeconds == -101) {
                    jLabel3.setText("Dati recuperati. Pronto per una nuova ricerca");
                    sleep = false;
                } else if (timeSeconds == -102) {
                    jLabel3.setText("Errore nel recupero dei dati");
                    sleep = false;
                } else {
                    AtomicInteger count = new AtomicInteger(timeSeconds);

                    if (scheduler != null) scheduler.shutdown();
                    scheduler = Executors.newScheduledThreadPool(1);

                    scheduler.scheduleAtFixedRate(() -> {
                        if (!sleep)
                            jLabel3.setText(count.get() == 1 ? "Prossimo aggiornamento dati tra 1 secondo" : "Prossimo aggiornamento dati tra " + count + " secondi");
                        count.getAndDecrement();
                    }, 0, 1, TimeUnit.SECONDS);

                }


            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectionPanel = new javax.swing.JPanel();
        jButtonDisconnect = new javax.swing.JButton();
        jButtonConnect = new javax.swing.JButton();
        jTextFieldVehicleUUID = new javax.swing.JTextField();
        routePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRoute = new javax.swing.JTable();
        cpPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCP = new javax.swing.JTable();
        disposalPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldVehicleUUIDtoSend = new javax.swing.JTextField();
        jTextFieldCapacityToSend = new javax.swing.JTextField();
        jTextFieldCollectionPointFromtoSend = new javax.swing.JTextField();
        jComboBoxTypeOfDisposal = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jButtonSEND = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelArtemisConnectionStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabelMongoConnectionStatus = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jButtonUPDATENOW = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonDisconnect.setText("DISCONNETTI");
        jButtonDisconnect.setEnabled(false);
        jButtonDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisconnectActionPerformed(evt);
            }
        });

        jButtonConnect.setText("CONNETTI");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        jTextFieldVehicleUUID.setText("Inserisci UUID del veicolo");
        jTextFieldVehicleUUID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldVehicleUUIDFocusLost(evt);
            }
        });
        jTextFieldVehicleUUID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldVehicleUUIDMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout connectionPanelLayout = new javax.swing.GroupLayout(connectionPanel);
        connectionPanel.setLayout(connectionPanelLayout);
        connectionPanelLayout.setHorizontalGroup(
            connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldVehicleUUID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDisconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        connectionPanelLayout.setVerticalGroup(
            connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDisconnect, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jButtonConnect, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldVehicleUUID))
                .addContainerGap())
        );

        jTableRoute.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Rotta", "Stato", "Numero Collection Point", "TimeStamp", "Costo"
            }
        ));
        jTableRoute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableRouteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableRoute);

        javax.swing.GroupLayout routePanelLayout = new javax.swing.GroupLayout(routePanel);
        routePanel.setLayout(routePanelLayout);
        routePanelLayout.setHorizontalGroup(
            routePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        routePanelLayout.setVerticalGroup(
            routePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
        );

        jTableCP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Collection Point", "Litri aspettati", "Litri effettivi", "Stato"
            }
        ));
        jTableCP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCPMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableCP);

        javax.swing.GroupLayout cpPanelLayout = new javax.swing.GroupLayout(cpPanel);
        cpPanel.setLayout(cpPanelLayout);
        cpPanelLayout.setHorizontalGroup(
            cpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        cpPanelLayout.setVerticalGroup(
            cpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );

        jTextFieldVehicleUUIDtoSend.setText("Inserisci UUID Veicolo");
        jTextFieldVehicleUUIDtoSend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldVehicleUUIDtoSendFocusLost(evt);
            }
        });
        jTextFieldVehicleUUIDtoSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldVehicleUUIDtoSendMouseClicked(evt);
            }
        });

        jTextFieldCapacityToSend.setText("Inserisci capacit� raccolta (in litri)");
        jTextFieldCapacityToSend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldCapacityToSendFocusLost(evt);
            }
        });
        jTextFieldCapacityToSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldCapacityToSendMouseClicked(evt);
            }
        });

        jTextFieldCollectionPointFromtoSend.setText("Inserisci UUID collection point");
        jTextFieldCollectionPointFromtoSend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldCollectionPointFromtoSendFocusLost(evt);
            }
        });
        jTextFieldCollectionPointFromtoSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldCollectionPointFromtoSendMouseClicked(evt);
            }
        });

        jComboBoxTypeOfDisposal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GENERAL_WASTE", "PLASTIC_AND_METALS", "GLASS", "PAPER_AND_CARD_BOARDS", "ORGANIC" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jComboBoxTypeOfDisposal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldVehicleUUIDtoSend, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldCapacityToSend, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldCollectionPointFromtoSend, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(99, 99, 99))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jTextFieldVehicleUUIDtoSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCapacityToSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCollectionPointFromtoSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxTypeOfDisposal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jButtonSEND.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButtonSEND.setText("INVIA");
        jButtonSEND.setEnabled(false);
        jButtonSEND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSENDActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSEND, new java.awt.GridBagConstraints());

        javax.swing.GroupLayout disposalPanelLayout = new javax.swing.GroupLayout(disposalPanel);
        disposalPanel.setLayout(disposalPanelLayout);
        disposalPanelLayout.setHorizontalGroup(
            disposalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(disposalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        disposalPanelLayout.setVerticalGroup(
            disposalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(disposalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(disposalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel1.setText("ARTEMIS STATUS: ");

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelArtemisConnectionStatus.setText("DISCONNESSO");
        jPanel1.add(jLabelArtemisConnectionStatus, new java.awt.GridBagConstraints());

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setText("MONGO STATUS: ");

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelMongoConnectionStatus.setText("DISCONNESSO");
        jPanel2.add(jLabelMongoConnectionStatus, new java.awt.GridBagConstraints());

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButtonUPDATENOW.setText("AGGIORNA ORA");
        jButtonUPDATENOW.setEnabled(false);
        jButtonUPDATENOW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUPDATENOWActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 343, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonUPDATENOW, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonUPDATENOW, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cpPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(disposalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(routePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(connectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(routePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disposalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisconnectActionPerformed

        try {
            disposalGenerator.disconnect();

            jButtonDisconnect.setEnabled(false);
            jTextFieldVehicleUUID.setEditable(true);
            jButtonConnect.setEnabled(true);

            jButtonSEND.setEnabled(true);
            jButtonUPDATENOW.setEnabled(true);

            if (scheduler != null) scheduler.shutdown();
            jLabel3.setText("");


            DefaultTableModel tblModel = (DefaultTableModel) jTableRoute.getModel();
            tblModel.setRowCount(0); //Metodo per svuotare la tabella

            tblModel = (DefaultTableModel) jTableCP.getModel();
            tblModel.setRowCount(0); //Metodo per svuotare la tabella

        } catch (Exception e) {
            log.error(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);

        }

    }//GEN-LAST:event_jButtonDisconnectActionPerformed

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed

        try {
            UUID.fromString(jTextFieldVehicleUUID.getText());

            disposalGenerator.start(jTextFieldVehicleUUID.getText());


            jTextFieldVehicleUUIDtoSend.setText(jTextFieldVehicleUUID.getText());
        } catch (Exception e) {
            log.error(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);


        }


    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jTextFieldVehicleUUIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldVehicleUUIDMouseClicked
        if (jTextFieldVehicleUUID.getText().equals("Inserisci UUID del veicolo")) jTextFieldVehicleUUID.setText("");
    }//GEN-LAST:event_jTextFieldVehicleUUIDMouseClicked

    private void jTextFieldVehicleUUIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldVehicleUUIDFocusLost
        if (jTextFieldVehicleUUID.getText().equals("")) jTextFieldVehicleUUID.setText("Inserisci UUID del veicolo");
    }//GEN-LAST:event_jTextFieldVehicleUUIDFocusLost

    private void jTextFieldVehicleUUIDtoSendFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldVehicleUUIDtoSendFocusLost
        if (jTextFieldVehicleUUIDtoSend.getText().equals(""))
            jTextFieldVehicleUUIDtoSend.setText("Inserisci UUID Veicolo");
    }//GEN-LAST:event_jTextFieldVehicleUUIDtoSendFocusLost

    private void jTextFieldVehicleUUIDtoSendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldVehicleUUIDtoSendMouseClicked
        if (jTextFieldVehicleUUIDtoSend.getText().equals("Inserisci UUID Veicolo"))
            jTextFieldVehicleUUIDtoSend.setText("");
    }//GEN-LAST:event_jTextFieldVehicleUUIDtoSendMouseClicked

    private void jTextFieldCapacityToSendFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCapacityToSendFocusLost
        if (jTextFieldCapacityToSend.getText().equals(""))
            jTextFieldCapacityToSend.setText("Inserisci capacit� raccolta (in litri)");
    }//GEN-LAST:event_jTextFieldCapacityToSendFocusLost

    private void jTextFieldCapacityToSendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldCapacityToSendMouseClicked
        if (jTextFieldCapacityToSend.getText().equals("Inserisci capacit� raccolta (in litri)"))
            jTextFieldCapacityToSend.setText("");
    }//GEN-LAST:event_jTextFieldCapacityToSendMouseClicked

    private void jTextFieldCollectionPointFromtoSendFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCollectionPointFromtoSendFocusLost
        if (jTextFieldCollectionPointFromtoSend.getText().equals(""))
            jTextFieldCollectionPointFromtoSend.setText("Inserisci UUID collection point");
    }//GEN-LAST:event_jTextFieldCollectionPointFromtoSendFocusLost

    private void jTextFieldCollectionPointFromtoSendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldCollectionPointFromtoSendMouseClicked
        if (jTextFieldCollectionPointFromtoSend.getText().equals("Inserisci UUID collection point"))
            jTextFieldCollectionPointFromtoSend.setText("");
    }//GEN-LAST:event_jTextFieldCollectionPointFromtoSendMouseClicked

    private void jTableCPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCPMouseClicked
        JTable t = (JTable) evt.getSource();
        int row = t.getSelectedRow();
        //int column = t.getSelectedColumn();
        String s = (String) t.getValueAt(row, 0);
        jTextFieldCollectionPointFromtoSend.setText(s);
    }//GEN-LAST:event_jTableCPMouseClicked

    private void jButtonUPDATENOWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUPDATENOWActionPerformed


        new Thread(new Runnable() {
            @Override
            public void run() {
                disposalGenerator.updateNow(selectedRoute);
            }
        }).start();


    }//GEN-LAST:event_jButtonUPDATENOWActionPerformed

    private void jButtonSENDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSENDActionPerformed
        // TODO add your handling code here:

        String vehicleID = jTextFieldVehicleUUIDtoSend.getText();
        String capacity = jTextFieldCapacityToSend.getText();
        String cpID = jTextFieldCollectionPointFromtoSend.getText();
        String typeOfDisposal = (String) jComboBoxTypeOfDisposal.getSelectedItem();
        disposalGenerator.sendDisposal(typeOfDisposal, Integer.valueOf(capacity), UUID.fromString(cpID), UUID.fromString(vehicleID));
    }//GEN-LAST:event_jButtonSENDActionPerformed

    private void jTableRouteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableRouteMouseClicked

        JTable t = (JTable) evt.getSource();
        int row = t.getSelectedRow();
        //int column = t.getSelectedColumn();
        String s = (String) t.getValueAt(row, 0);

        selectedRoute = s;

        new Thread(new Runnable() {
            @Override
            public void run() {
                disposalGenerator.updateNow(s);
            }
        }).start();

    }//GEN-LAST:event_jTableRouteMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel connectionPanel;
    private javax.swing.JPanel cpPanel;
    private javax.swing.JPanel disposalPanel;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonDisconnect;
    private javax.swing.JButton jButtonSEND;
    private javax.swing.JButton jButtonUPDATENOW;
    private javax.swing.JComboBox<String> jComboBoxTypeOfDisposal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelArtemisConnectionStatus;
    private javax.swing.JLabel jLabelMongoConnectionStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTableCP;
    private javax.swing.JTable jTableRoute;
    private javax.swing.JTextField jTextFieldCapacityToSend;
    private javax.swing.JTextField jTextFieldCollectionPointFromtoSend;
    private javax.swing.JTextField jTextFieldVehicleUUID;
    private javax.swing.JTextField jTextFieldVehicleUUIDtoSend;
    private javax.swing.JPanel routePanel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
