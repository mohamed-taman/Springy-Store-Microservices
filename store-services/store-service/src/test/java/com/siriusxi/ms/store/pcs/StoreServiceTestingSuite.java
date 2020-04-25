package com.siriusxi.ms.store.pcs;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;

@SuiteDisplayName("Store Service Testing Suite")
@SelectClasses(value = {StoreServiceApplicationTests.class, MessagingTests.class})
public class StoreServiceTestingSuite {}
