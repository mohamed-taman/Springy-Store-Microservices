package com.siriusxi.ms.store.pcs;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

//@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Store Service Testing Suite")
@SelectClasses(value = {StoreServiceApplicationTests.class, MessagingTests.class})
class StoreServiceTestingSuite {}
