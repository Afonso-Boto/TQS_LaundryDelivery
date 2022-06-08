package com.qourier.qourier_app.bids;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.repository.BidsRepository;
import com.qourier.qourier_app.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.qourier.qourier_app.data.DeliveryState.*;
import static com.qourier.qourier_app.data.DeliveryState.BID_CHECK;

@Service
public class DeliveriesManager {
    private final BidsRepository bidsRepository;
    private final DeliveryRepository deliveryRepository;
    private final AccountManager accountManager;
    private long AuctionSpan;

    @Autowired
    public DeliveriesManager(
            BidsRepository bidsrepository,
            DeliveryRepository deliveryRepository,
            AccountManager accountManager) {
        this.bidsRepository = bidsrepository;
        this.deliveryRepository = deliveryRepository;
        this.accountManager = accountManager;
        this.AuctionSpan = 600000;
    }

    public Delivery createDelivery(Delivery newDelivery) {
        deliveryRepository.save(newDelivery);

        createAuction(newDelivery);
        newDelivery.setDeliveryState(DeliveryState.DELIVERED);

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
                                } else {
                                    deliveryRepository.delete(delivery);
                                }
                            }
                        },
                        AuctionSpan);
    }

    public void setNewAuctionSpan(int seconds) {
        AuctionSpan = seconds * 1000L;
    }

    public Bid createBid(Bid bid) {
        if (bid.getDistance() == null) bid.setDistance(Double.MAX_VALUE);
        return bidsRepository.save(bid);
    }

    public DeliveryState getDeliveryState(Long deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId).getDeliveryState();
    }

    public DeliveryState setDeliveryState(Long deliveryId, String riderId) {
        DeliveryState previousState = deliveryRepository.findByDeliveryId(deliveryId).getDeliveryState();
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId);

        // Iterate states if rider id is right
        if(delivery.getRiderId().equals(riderId)){
            switch (previousState) {
                case BID_CHECK -> delivery.setDeliveryState(FETCHING);
                case FETCHING -> delivery.setDeliveryState(SHIPPED);
                case SHIPPED -> delivery.setDeliveryState(DELIVERED);
                case DELIVERED -> delivery.setDeliveryState(BID_CHECK);
            }
        }
        deliveryRepository.save(delivery);

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

    public List<Delivery> getToDoDeliveries() { return deliveryRepository.findAll().stream().filter( delivery -> delivery.getDeliveryState() == DeliveryState.BID_CHECK).toList(); }

    public List<Delivery> getDeliveriesFromCustomer(String customerId) {
        return deliveryRepository.findByCustomerId(customerId);
    }

    public void deleteAll() {
        deliveryRepository.deleteAll(deliveryRepository.findAll());
    }
}
