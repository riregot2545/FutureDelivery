package com.nix.futuredelivery.service;

import com.nix.futuredelivery.transportation.TransportationProcessor;
import com.nix.futuredelivery.transportation.model.exceptions.NoneCarsExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.NoneDriversExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductPositionNotExistException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOversellsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTransportationWorker {
    private final TransportationProcessor transportationProcessor;

    @Scheduled(cron = "0 0 0 * * *")
    public void distributeStoreOrders() throws NoneCarsExistsException, NoneDriversExistsException, ProductsIsOversellsException, ProductPositionNotExistException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
        log.info("Time is now {}, starting order distribution...", dateFormat.format(new Date()));
        transportationProcessor.proceedOrders();
        log.info("Time is now {}, order distribution ended.", dateFormat.format(new Date()));
    }
}