package net.jmp.demo.mockito.payments;

/*
 * (#)TestPaymentDatabase.java  0.1.0   05/26/2026
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

import static org.junit.jupiter.api.Assertions.*;

/// The payment database test class
class TestPaymentDatabase {
    /// The payment database
    private PaymentDatabase paymentDatabase;

    /// The setup method
    @BeforeEach
    void setUp() {
        this.paymentDatabase = new PaymentDatabase();
    }

    /// Test the isValidAccount method for true
    @Test
    void testIsValidAccount() {
        assertTrue(this.paymentDatabase.isValidAccount("000515123456789"));
    }

    /// Test the isValidAccount method for false
    @Test
    void testIsNotValidAccount() {
        assertFalse(this.paymentDatabase.isValidAccount("010515123456789"));
    }

    /// Test the saveTransaction method for true
    @Test
    void testIsValidSaveTransaction() {
        assertTrue(this.paymentDatabase.saveTransaction("000515123456789", 1.00));
        assertTrue(this.paymentDatabase.saveTransaction("000515123456789", 100_000.00));
    }

    /// Test the saveTransaction method for false
    @Test
    void testIsNotValidSaveTransaction() {
        assertFalse(this.paymentDatabase.saveTransaction("000515123456789", 0.99));
        assertFalse(this.paymentDatabase.saveTransaction("000515123456789", 100_000.01));
    }
}

