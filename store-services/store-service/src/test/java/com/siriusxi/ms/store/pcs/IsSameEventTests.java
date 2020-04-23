package com.siriusxi.ms.store.pcs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.api.event.Event;
import org.junit.jupiter.api.Test;

import static com.siriusxi.ms.store.api.event.Event.Type.CREATE;
import static com.siriusxi.ms.store.api.event.Event.Type.DELETE;
import static com.siriusxi.ms.store.pcs.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class IsSameEventTests {

  ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testEventObjectCompare() throws JsonProcessingException {

    /*
     Event #1 and #2 are the same event, but occurs as different times
     Event #3 and #4 are different events
    */
    Event<Integer, Product> event1 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
    Event<Integer, Product> event2 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
    Event<Integer, Product> event3 = new Event<>(DELETE, 1, null);
    Event<Integer, Product> event4 = new Event<>(CREATE, 1, new Product(2, "name", 1, null));

    String event1JSon = mapper.writeValueAsString(event1);

    assertThat(event1JSon, is(sameEventExceptCreatedAt(event2)));
    assertThat(event1JSon, not(sameEventExceptCreatedAt(event3)));
    assertThat(event1JSon, not(sameEventExceptCreatedAt(event4)));
  }
}
