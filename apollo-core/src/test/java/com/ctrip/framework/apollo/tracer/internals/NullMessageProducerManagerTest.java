package com.ctrip.framework.apollo.tracer.internals;

import static org.junit.Assert.assertTrue;

import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class NullMessageProducerManagerTest {

  private MessageProducerManager messageProducerManager;

  @Before
  public void setUp() throws Exception {
    messageProducerManager = new NullMessageProducerManager();
  }

  @Test
  public void testGetProducer() throws Exception {
    assertTrue(messageProducerManager.getProducer() instanceof NullMessageProducer);
  }
}
