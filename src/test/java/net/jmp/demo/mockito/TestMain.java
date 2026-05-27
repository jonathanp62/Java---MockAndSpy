package net.jmp.demo.mockito;

/*
 * (#)TestMain.java 0.1.0   05/27/2026
 *
 * @author   Jonathan Parker
 *
 * MIT License
 *
 * Copyright (c) 2026 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import ch.qos.logback.classic.Logger;

import ch.qos.logback.classic.spi.ILoggingEvent;

import ch.qos.logback.core.read.ListAppender;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.jmp.demo.mockito.payments.PaymentDatabase;
import net.jmp.demo.mockito.payments.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

/// The main test class
class TestMain {
    /// The main constructor
    private Constructor<Main> mainConstructor;

    /// The method
    private Method processPaymentsMethod;

    /// The setup method
    @BeforeEach
    void setUp() throws Exception {
        this.mainConstructor = Main.class.getDeclaredConstructor(String[].class, PaymentService.class);
        this.processPaymentsMethod = Main.class.getDeclaredMethod("processPayments");

        this.mainConstructor.setAccessible(true);       // Force the constructor to be accessible
        this.processPaymentsMethod.setAccessible(true); // Force the method to be accessible
    }

    /// The test process payments method
    @Test
    void testProcessPayments() throws Exception {
        final Main main = this.mainConstructor.newInstance((Object) new String[] {}, new PaymentService(new PaymentDatabase()));
        final Logger logger = (Logger) LoggerFactory.getLogger(Main.class.getName());   // Get the logger for the Main class
        final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

        listAppender.start();
        logger.addAppender(listAppender);

        try {
            this.processPaymentsMethod.invoke(main);

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Success        : SUCCESS")),
                    "Expected success payment log message"
            );

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Invalid Account: REJECTED")),
                    "Expected rejected payment log message"
            );

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Invalid Amount : FAILURE")),
                    "Expected failure payment log message"
            );
        } finally {
            logger.detachAppender(listAppender);
            listAppender.stop();
        }
    }

    /// The test mocked payment service
    @Test
    void testMockedPaymentService() throws Exception {
        final PaymentService paymentService = mock(PaymentService.class);

        when(paymentService.processPayment("000515123456789", 100.00)).thenReturn("SUCCESS");
        when(paymentService.processPayment("001151123456789", 100.00)).thenReturn("REJECTED");
        when(paymentService.processPayment("000515123456789", 100_000.01)).thenReturn("FAILURE");

        final Main main = this.mainConstructor.newInstance(new String[] {}, paymentService);

        processPaymentsMethod.invoke(main);

        verify(paymentService, times(1)).processPayment("000515123456789", 100.00);
        verify(paymentService, times(1)).processPayment("001151123456789", 100.00);
        verify(paymentService, times(1)).processPayment("000515123456789", 100_000.01);

        verifyNoMoreInteractions(paymentService);
    }

    /// The test spied payment service
    @Test
    void testSpiedPaymentService() throws Exception {
        final PaymentService paymentService = spy(new PaymentService(new PaymentDatabase()));
        final Main main = this.mainConstructor.newInstance(new String[] {}, paymentService);
        final Logger logger = (Logger) LoggerFactory.getLogger(Main.class.getName());   // Get the logger for the Main class
        final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

        doReturn("**success**").when(paymentService).processPayment("000515123456789", 100.00);
        doReturn("**rejected**").when(paymentService).processPayment("001151123456789", 100.00);
        doReturn("**failure**").when(paymentService).processPayment("000515123456789", 100_000.01);

        listAppender.start();
        logger.addAppender(listAppender);

        try {
            this.processPaymentsMethod.invoke(main);

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Success        : **success**")),
                    "Expected success payment log message"
            );

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Invalid Account: **rejected**")),
                    "Expected rejected payment log message"
            );

            assertTrue(
                    listAppender.list.stream()
                            .anyMatch(event -> event.getFormattedMessage().contains("Invalid Amount : **failure**")),
                    "Expected failure payment log message"
            );
        } finally {
            logger.detachAppender(listAppender);
            listAppender.stop();
        }
    }
}
