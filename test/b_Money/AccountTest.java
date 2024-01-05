package b_Money;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AccountTest {
	Currency SEK, DKK;
	Bank Nordea;
	Bank DanskeBank;
	Bank SweBank;
	Account testAccount;
	
	@Before
	public void setUp() throws Exception {
		SEK = new Currency("SEK", 0.15);
		SweBank = new Bank("SweBank", SEK);
		SweBank.openAccount("Alice");
		testAccount = new Account("Hans", SEK);
		testAccount.deposit(new Money(10000000, SEK));

		SweBank.deposit("Alice", new Money(1000000, SEK));
	}
	
	@Test
	/* Testuje się dodawanie i usuwanie płatności regularnej 
	*/
	public void testAddRemoveTimedPayment() {
		testAccount.addTimedPayment("aaa", 1, 1, new Money(10000, SEK), SweBank, "Alice");
		assertTrue(testAccount.timedPaymentExists("aaa"));
		testAccount.removeTimedPayment("aaa");
		assertFalse(testAccount.timedPaymentExists("aaa"));
	}
	
	@Test
	/* Testuje się dodawanie i usuwanie płatności regularnej 
	 * oraz poprawność działania metody tick() 
	*/
	public void testTimedPayment() throws AccountDoesNotExistException {
		testAccount.addTimedPayment("aaa", 1, 2, new Money(10000, SEK), SweBank, "Alice");
		assertTrue(testAccount.timedPaymentExists("aaa"));

		//podgotowienia się
		Money testBalance = testAccount.getBalance();
		Double aliceBalance = SweBank.getBalance("Alice");

		//musi być takie same bo next == 2
		testAccount.tick();
		assertEquals(testBalance, testAccount.getBalance());
		assertEquals(aliceBalance, SweBank.getBalance("Alice"), 0);
		testBalance = testAccount.getBalance();
		aliceBalance = SweBank.getBalance("Alice");

		//musi różnić się o 100.00 SEK
		testAccount.tick();
		assertEquals(testBalance.sub(new Money(10000, SEK)), testAccount.getBalance());
		assertEquals(aliceBalance + 100.0d, SweBank.getBalance("Alice"), 0);

		//musi różnić się jeszce o 100.00 SEK czyli sumaruycznie 0 200.00 SEK
		testAccount.tick();
		assertEquals(testBalance.sub(new Money(20000, SEK)), testAccount.getBalance());
		assertEquals(aliceBalance + 200.0d, SweBank.getBalance("Alice"), 0);
		testAccount.removeTimedPayment("aaa");
		assertFalse(testAccount.timedPaymentExists("aaa"));
	}

	@Test
	/* Testuje się poprawność działania wypłaty i wpłaty
	 */
	public void testAddWithdraw() {
		testAccount.withdraw(new Money(1000000, SEK));
		assertEquals(new Money(9000000, SEK), testAccount.getBalance());
		testAccount.deposit(new Money(1000000, SEK));
		assertEquals(new Money(10000000, SEK), testAccount.getBalance());
	}
	
	@Test
	/* Testuje się poprawność wzróconej wartości
	 */
	public void testGetBalance() {
		assertEquals(new Money(10000000, SEK), testAccount.getBalance());
	}
}
