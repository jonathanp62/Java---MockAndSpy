package net.jmp.demo.mockito.payments;

/*
 * (#)PaymentDatabase.java  0.1.0   05/23/2026
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

import static net.jmp.util.logging.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// The payment database
public class PaymentDatabase {
    /// The logger.
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /// The default constructor
    public PaymentDatabase() {
        super();
    }

    /// Return true if the account is valid
    ///
    /// @param  accountId   java.lang.String
    /// @return             boolean
    public boolean isValidAccount(final String accountId) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(accountId));
        }

        final boolean result = accountId.startsWith("000515");

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /// Save the transaction
    ///
    /// @param  accountId   java.lang.String
    /// @param  amount      double
    /// @return             boolean
    public boolean saveTransaction(final String accountId, final double amount) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(accountId, amount));
        }

        boolean result;

        if (amount <= 100_000.00) {
            this.logger.info("Saved transaction: Account: {}; Amount: {}", accountId, amount);

            result = true;
        } else {
            this.logger.error("Failed to save transaction: Account: {}; Amount: {}", accountId, amount);

            result = false;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }
}
