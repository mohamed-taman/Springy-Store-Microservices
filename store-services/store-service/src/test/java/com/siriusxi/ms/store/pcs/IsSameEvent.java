package com.siriusxi.ms.store.pcs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siriusxi.ms.store.api.event.Event;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
class IsSameEvent extends TypeSafeMatcher<String> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Event expectedEvent;

  private IsSameEvent(Event expectedEvent) {
    this.expectedEvent = expectedEvent;
  }

  public static Matcher<String> sameEventExceptCreatedAt(Event expectedEvent) {
    return new IsSameEvent(expectedEvent);
  }

  @Override
  protected boolean matchesSafely(String eventAsJson) {

    if (expectedEvent == null) return false;

    log.trace("Convert the following json string to a map: {}", eventAsJson);
    Map mapEvent = convertJsonStringToMap(eventAsJson);
    mapEvent.remove("eventCreatedAt");

    Map mapExpectedEvent = getMapWithoutCreatedAt(expectedEvent);

    log.trace("Got the map: {}", mapEvent);
    log.trace("Compare to the expected map: {}", mapExpectedEvent);
    return mapEvent.equals(mapExpectedEvent);
  }

  @Override
  public void describeTo(Description description) {
    String expectedJson = convertObjectToJsonString(expectedEvent);
    description.appendText("expected to look like " + expectedJson);
  }

  private Map getMapWithoutCreatedAt(Event event) {
    Map mapEvent = convertObjectToMap(event);
    mapEvent.remove("eventCreatedAt");
    return mapEvent;
  }

  private Map convertObjectToMap(Object object) {
    JsonNode node = mapper.convertValue(object, JsonNode.class);
    return mapper.convertValue(node, Map.class);
  }

  private String convertObjectToJsonString(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Map convertJsonStringToMap(String eventAsJson) {
    try {
      return mapper.readValue(eventAsJson, new TypeReference<HashMap>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
