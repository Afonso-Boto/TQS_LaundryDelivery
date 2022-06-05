package com.qourier.qourier_app.bids;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.data.Rider;
import com.qourier.qourier_app.repository.BidsRepository;
import com.qourier.qourier_app.repository.DeliveryRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class DeliveriesManager {
    private final BidsRepository bidsRepository;
    private final DeliveryRepository deliveryRepository;
    private final AccountManager accountManager;

    @Autowired
    public DeliveriesManager(
            BidsRepository bidsrepository,
            DeliveryRepository deliveryRepository,
            AccountManager accountManager) {
        this.bidsRepository = bidsrepository;
        this.deliveryRepository = deliveryRepository;
        this.accountManager = accountManager;
    }

    public void bidOnDelivery() {}

    public Delivery createDelivery(Delivery newDelivery) {
        deliveryRepository.save(newDelivery);

        createAuction(newDelivery);
        System.out.println("Run on func call");
        newDelivery.setDeliveryState(DeliveryState.DELIVERED);

        return newDelivery;
    }

    public void createAuction(Delivery delivery) {
        System.out.println("Created Auction");
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
                                } else {
                                    deliveryRepository.delete(delivery);
                                }
                                System.out.println("Finished Auction");
                            }
                        },
                        2000);
    }

    public Bid createBid(Bid bid) {
        return bidsRepository.save(bid);
    }

    public DeliveryState getDeliveryState(Long deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId).getDeliveryState();
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

    public List<Delivery> getDeliveriesFromCustomer(String customerId) {
        return deliveryRepository.findByCustomerId(customerId);
    }
}
