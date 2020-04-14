package com.siriusxi.ms.store.pcs.api;

import com.siriusxi.ms.store.api.composite.StoreEndpoint;
import com.siriusxi.ms.store.api.composite.StoreService;
import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class StoreController implements StoreEndpoint {
    /** Store service business logic interface. */
    private final StoreService storeService;

    @Autowired
    public StoreController(@Qualifier("StoreServiceImpl") StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAggregate getProduct(int id) {
        return storeService.getProduct(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createProduct(ProductAggregate body) {
        storeService.createProduct(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProduct(int id) {
        storeService.deleteProduct(id);
    }
}
