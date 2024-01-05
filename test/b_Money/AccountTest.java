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
	*/
	public void testTimedPayment() throws AccountDoesNotExistException {
		testAccount.addTimedPayment("aaa", 1, 1, new Money(10000, SEK), SweBank, "Alice");
		assertTrue(testAccount.timedPaymentExists("aaa"));
		testAccount.tick();
		
		testAccount.addTimedPayment("aaa", 1, 1, new Money(10000, SEK), SweBank, "Ali");
		assertFalse(testAccount.timedPaymentExists("ccc"));
	}

	@Test
	public void testAddWithdraw() {
		testAccount.withdraw(new Money(1000000, SEK));
		assertEquals(new Money(9000000, SEK), testAccount.getBalance());
		testAccount.deposit(new Money(1000000, SEK));
		assertEquals(new Money(10000000, SEK), testAccount.getBalance());
	}
	
	@Test
	public void testGetBalance() {
		assertEquals(new Money(10000000, SEK), testAccount.getBalance());
	}
}
