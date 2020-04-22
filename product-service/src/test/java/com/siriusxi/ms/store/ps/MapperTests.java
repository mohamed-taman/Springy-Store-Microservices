package com.siriusxi.ms.store.ps;

import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.ps.persistence.ProductEntity;
import com.siriusxi.ms.store.ps.service.ProductMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapperTests {

  private final ProductMapper mapper = ProductMapper.INSTANCE;

  @Test
  public void mapperTests() {

    assertNotNull(mapper);

    Product api = new Product(1, "n", 1, "sa");

    ProductEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getName(), entity.getName());
    assertEquals(api.getWeight(), entity.getWeight());

    Product api2 = mapper.entityToApi(entity);

    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getName(), api2.getName());
    assertEquals(api.getWeight(), api2.getWeight());
    assertNull(api2.getServiceAddress());
  }
}
