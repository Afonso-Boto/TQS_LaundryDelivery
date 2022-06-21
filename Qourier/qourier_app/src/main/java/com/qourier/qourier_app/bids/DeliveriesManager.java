package com.qourier.qourier_app.bids;

import static com.qourier.qourier_app.data.DeliveryState.*;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.message.MessageCenter;
import com.qourier.qourier_app.repository.BidsRepository;
import com.qourier.qourier_app.repository.DeliveryRepository;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.amqp.AmqpApplicationContextClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DeliveriesManager {
    private final BidsRepository bidsRepository;
    private final DeliveryRepository deliveryRepository;
    private final AccountManager accountManager;
    private final MessageCenter messageCenter;
    private long auctionSpan;

    @Autowired
    public DeliveriesManager(
            BidsRepository bidsrepository,
            DeliveryRepository deliveryRepository,
            AccountManager accountManager,
            MessageCenter messageCenter) {
        this.bidsRepository = bidsrepository;
        this.deliveryRepository = deliveryRepository;
        this.accountManager = accountManager;
        this.messageCenter = messageCenter;
        this.auctionSpan = 600000;
    }

    public Delivery createDelivery(Delivery newDelivery) {
        deliveryRepository.save(newDelivery);

        createAuction(newDelivery);

        return newDelivery;
    }

    public void createAuction(Delivery delivery) {
        new Timer()
                .schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                // Code to be run after 2s
                                String winnerId = getBidderWinner(delivery.getDeliveryId());
                                if (winnerId != null) {
                                    accountManager.assignWork(winnerId, delivery.getDeliveryId());
                                    bidsRepository.deleteAllById(
                                            bidsRepository
                                                    .findByDeliveryId(delivery.getDeliveryId())
                                                    .stream()
                                                    .map(Bid::getRidersId)
                                                    .toList());

                                    // Set delivery status
                                    delivery.setRiderId(winnerId);
                                    delivery.setDeliveryState(FETCHING);
                                    deliveryRepository.save(delivery);

                                    messageCenter.notifyRiderAssignment(
                                            delivery.getRiderId(), delivery.getDeliveryId());
                                } else {
                                    deliveryRepository.delete(delivery);
                                }
                            }
                        },
                        auctionSpan);
    }

    public void setNewAuctionSpan(int seconds) {
        auctionSpan = seconds * 1000L;
    }

    public Bid createBid(Bid bid) {
        if (bid.getDistance() == null) bid.setDistance(Double.MAX_VALUE);
        return bidsRepository.save(bid);
    }

    public DeliveryState getDeliveryState(Long deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId).getDeliveryState();
    }

    public DeliveryState setDeliveryState(Long deliveryId, String riderId) {
        DeliveryState previousState =
                deliveryRepository.findByDeliveryId(deliveryId).getDeliveryState();
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId);

        // Iterate states if rider id is right
        if (delivery.getRiderId().equals(riderId)) {
            switch (previousState) {
                case BID_CHECK -> delivery.setDeliveryState(FETCHING);
                case FETCHING -> delivery.setDeliveryState(SHIPPED);
                case SHIPPED -> delivery.setDeliveryState(DELIVERED);
                case DELIVERED -> delivery.setDeliveryState(BID_CHECK);
            }
        }
        deliveryRepository.save(delivery);

        // If delivery is finished -> rider is free for other deliveries
        if (delivery.getDeliveryState() == DELIVERED) accountManager.assignWork(riderId, null);

        return delivery.getDeliveryState();
    }

    public String getBidderWinner(Long deliveryId) {
        List<Bid> bids =
                bidsRepository.findByDeliveryId(
                        deliveryId, Sort.by(Sort.Direction.ASC, "distance"));
        if (!bids.isEmpty()) {
            return bids.get(0).getRidersId();
        }
        return null;
    }

    public Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> getToDoDeliveries() {
        return deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getDeliveryState() == DeliveryState.BID_CHECK)
                .toList();
    }

    public List<Delivery> getDeliveriesFromCustomer(String customerId) {
        return deliveryRepository.findByCustomerId(customerId);
    }

    public void deleteAll() {
        deliveryRepository.deleteAll(deliveryRepository.findAll());
    }
}
