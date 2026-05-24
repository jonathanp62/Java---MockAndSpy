package net.jmp.demo.mockito.payments;

/*
 * (#)TestPaymentService.java   0.1.0   05/24/2026
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

/// The payment service test class
@ExtendWith(MockitoExtension.class) // Required to enable Mockito annotations in JUnit 5
class TestPaymentService {
    /// The complete fake database object (all methods return defaults)
    @Mock
    private PaymentDatabase mockDatabase;

    /// A real database object wrapper (calls real methods unless stubbed)
    @Spy
    private PaymentDatabase spyDatabase;

    /// Used to inspect parameters passed to mocks
    @Captor
    private ArgumentCaptor<Double> amountCaptor;

    /// The payment service with a mocked database object
    private PaymentService paymentServiceWithMock;

    /// The payment service with a real database object wrapper
    private PaymentService paymentServiceWithSpy;

    /// The setup method
    @BeforeEach
    void setUp() {
        this.paymentServiceWithMock = new PaymentService(this.mockDatabase);
        this.paymentServiceWithSpy = new PaymentService(this.spyDatabase);
    }

    /// Test the payment service with a mocked database object
    @Test
    void testMockStubbing_ReturnsExpectedValue() {
        // 1. Tell the mock how to behave (Stubbing)
        //  a. Make the database return true for an invalid account number
        //  b. Make the database return true for an invalid amount
        when(this.mockDatabase.isValidAccount("987654321")).thenReturn(true);
        when(this.mockDatabase.saveTransaction("987654321", 100_001.00)).thenReturn(true);

        // 2. Execute the service logic using the mocked database
        final String result = this.paymentServiceWithMock.processPayment("987654321", 100_001.00);

        // 3. Verify the outcome
        assertEquals("SUCCESS", result);
    }
}
