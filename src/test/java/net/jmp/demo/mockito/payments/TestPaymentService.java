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

import static org.mockito.Mockito.*;

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

    /// The payment service with a normal database object
    private PaymentService paymentService;

    /// The payment service with a mocked database object
    private PaymentService paymentServiceWithMock;

    /// The payment service with a real database object wrapper
    private PaymentService paymentServiceWithSpy;

    /// The setup method
    @BeforeEach
    void setUp() {
        this.paymentService = new PaymentService(new PaymentDatabase());
        this.paymentServiceWithMock = new PaymentService(this.mockDatabase);
        this.paymentServiceWithSpy = new PaymentService(this.spyDatabase);
    }

    /// Test the payment service for a successful transaction with a normal database object
    @Test
    void testSuccess() {
        final String result = this.paymentService.processPayment("000515123456789", 100_000.00);

        assertEquals("SUCCESS", result);
    }

    /// Test the payment service for a failed transaction with a normal database object
    @Test
    void testFailure() {
        final String result = this.paymentService.processPayment("000515123456789", 100_000.01);

        assertEquals("FAILURE", result);
    }

    /// Test the payment service for a rejected transaction with a normal database object
    @Test
    void testRejected() {
        final String result = this.paymentService.processPayment("123456789", 100_000.00);

        assertEquals("REJECTED", result);
    }

    /// Test the payment service with a mocked database object
    ///
    /// This test uses stubbing to control the behavior of the mock database object
    /// such that an invalid account number and amount are passed to the service
    /// and the service returns a success result.
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

    /// Test the payment service with a mocked database object
    ///
    /// This test uses verification to ensure that the mock database object was called
    /// with the expected parameters.
    @Test
    void testVerification_EnsuresMethodWasCalled() {
        // Arrange
        when(this.mockDatabase.isValidAccount("456")).thenReturn(true);

        // Act
        this.paymentServiceWithMock.processPayment("456", 100.00);

        // Assert/Verify: Check if the service actually called the DB to save the transaction
        verify(this.mockDatabase, times(1)).saveTransaction("456", 100.00);

        // Extra check: Verify that a specific bad account was never checked
        verify(this.mockDatabase, never()).isValidAccount("999");
    }

    /// Test the payment service with a mocked database object
    ///
    /// This test uses argument captor to inspect the internal parameters of the mock
    /// database object.
    @Test
    void testArgumentCaptor_InspectsInternalParameters() {
        // Arrange
        when(this.mockDatabase.isValidAccount("789")).thenReturn(true);

        // Act
        this.paymentServiceWithMock.processPayment("789", 75.50);

        // Assert: Capture the exact double value passed to saveTransaction
        verify(this.mockDatabase).saveTransaction(eq("789"), this.amountCaptor.capture());

        final double capturedAmount = this.amountCaptor.getValue();

        assertEquals(75.50, capturedAmount);
    }

    @Test
    void testSpy_CallsRealMethodsButAllowsStubbing_1() {
        // Note: spyDatabase is a SPY. By default, it runs the real code in PaymentDatabase.
        // So, PaymentDatabase.isValidAccount() naturally returns false if the account number
        // does not start with "000515".

        // Act: Test with default real behavior (returns false -> REJECTED)
        final String realResult = this.paymentServiceWithSpy.processPayment("any_id", 10.00);

        assertEquals("REJECTED", realResult);

        // Arrange: Stub ONLY one specific method on the spy
        doReturn(true).when(this.spyDatabase).isValidAccount("trusted_id");

        // Act: Test stubbed behavior on the spy
        String stubbedResult = this.paymentServiceWithSpy.processPayment("trusted_id", 10.00);

        // Assert
        assertEquals("SUCCESS", stubbedResult);
    }

    @Test
    void testSpy_CallsRealMethodsButAllowsStubbing_2() {
        // Note: spyDatabase is a SPY. By default, it runs the real code in PaymentDatabase.
        // So, PaymentDatabase.isValidAccount() naturally returns false if the account number
        // does not start with "000515".

        // Arrange: Stub ONLY one specific method on the spy - Return false for a valid amount
        doReturn(false).when(this.spyDatabase).saveTransaction("000515123456789", 10_000.00);

        // Act: Test stubbed behavior on the spy
        String stubbedResult = this.paymentServiceWithSpy.processPayment("000515123456789", 10_000.00);

        // Assert
        assertEquals("FAILURE", stubbedResult);
    }
}
