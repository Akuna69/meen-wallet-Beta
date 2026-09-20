package io.meen.apollo.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.meen.apollo.R
import io.meen.apollo.data.debug.LappClient
import io.meen.apollo.utils.RandomUser
import io.meen.common.utils.BitcoinUtils
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class IncomingSwapTests : BaseInstrumentationTest() {

    /**
     * Test playbook:
     * 1. Setup recoverable user
     * 2. Receive a LN payment >> dust
     * 3. Check that balance updates correctly
     * 4. Logout
     * 5. Drop fulfillment tx from mempool
     * 6. Recover wallet with user
     * 7. Check that balance is correct
     * 8. Spend all funds (on chain)
     * 9. Check that balance updates correctly
     * 10. Generate 1 block to confirm spend all funds payment
     * 11. Check that spend all funds payment is confirmed
     */
    @Test
    fun test_01_cooperative_htcl_expending_due_to_forgotten_preimage() {
        val user = RandomUser()
        autoFlows.createRecoverableUser(user.pin, user.email, user.password)

        val amountInSats: Long = 400_000
        autoFlows.receiveMoneyFromLNWithAmountLessInvoice(amountInSats)

        homeScreen.waitUntilBalanceCloseTo(BitcoinUtils.satoshisToBitcoins(amountInSats))

        autoFlows.logOut()

        LappClient().dropLastTxFromMempool()

        autoFlows.signIn(user.email, user.password, pin = user.pin)

        homeScreen.waitUntilBalanceCloseTo(BitcoinUtils.satoshisToBitcoins(amountInSats))

        val note = "spendAllFunds"
        autoFlows.spendAllFunds(note)

        homeScreen.waitUntilBalanceCloseTo(BitcoinUtils.satoshisToBitcoins(0))

        LappClient().generateBlocks(1)

        homeScreen.goToOperationDetail(note) // will fail if still pending
        opDetailScreen.waitForStatusChange(context.getString(R.string.operation_completed))
    }
}