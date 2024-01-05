package b_Money;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BankTest {
	String testName;
	Currency SEK, DKK;
	Bank SweBank, Nordea, DanskeBank;
	
	@Before
	public void setUp() throws Exception {
		testName = "Rob";
		DKK = new Currency("DKK", 0.20);
		SEK = new Currency("SEK", 0.15);
		SweBank = new Bank("SweBank", SEK);
		Nordea = new Bank("Nordea", SEK);
		DanskeBank = new Bank("DanskeBank", DKK);
		SweBank.openAccount("Ulrika");
		SweBank.openAccount("Bob");
		Nordea.openAccount("Bob");
		DanskeBank.openAccount("Gertrud");
	}

	@Test
	/* Testuje się poprawność 
	 * zwracanej wartości
	 */
	public void testGetName() {
		assertEquals("DanskeBank", DanskeBank.getName());
		assertEquals("Nordea", Nordea.getName());
		assertEquals("SweBank", SweBank.getName());
	}

	@Test
	/* Testuje się poprawność 
	 * zwracanej wartości
	 */
	public void testGetCurrency() {
		assertEquals(DKK, DanskeBank.getCurrency());
		assertEquals(SEK, Nordea.getCurrency());
		assertEquals(SEK, SweBank.getCurrency());
	}

	@Test
	/* Testuje się czy zakazanie tworzenia 2 kont o takim samym Id
	 * i możliwość stworzenia nowego konta o nowym Id
	 */
	public void testOpenAccount() throws AccountExistsException, AccountDoesNotExistException {
		assertThrows(
			AccountExistsException.class,
			() -> SweBank.openAccount("Ulrika"));
		SweBank.openAccount(testName);
		assertThrows(
			AccountExistsException.class,
			() -> SweBank.openAccount(testName));
	}

	@Test
	/* Testuje się możliwość wpłaty pieniędzy na nieistniące konto
	 * i wpłaty pieniędzy na istniące konto
	 */
	public void testDeposit() throws AccountDoesNotExistException {
		assertThrows(AccountDoesNotExistException.class, () -> SweBank.deposit("Ulrika0", new Money(10000, SEK)));
		Double ulrikasBalance = SweBank.getBalance("Ulrika");
		SweBank.deposit("Ulrika", new Money(10000, SEK));
		assertNotEquals(ulrikasBalance, SweBank.getBalance("Ulrika"));
		assertEquals(ulrikasBalance + 100.00d, SweBank.getBalance("Ulrika"), 0);
	}

	@Test
	/* Testuje się możliwość wypłaty pieniędzy na nieistniące konto
	 * i wypłaty pieniędzy na istniące konto
	 */
	public void testWithdraw() throws AccountDoesNotExistException {
		assertThrows(AccountDoesNotExistException.class, () -> SweBank.withdraw("Ulrika0", new Money(10000, SEK)));
		Double ulrikasBalance = SweBank.getBalance("Ulrika");
		SweBank.withdraw("Ulrika", new Money(10000, SEK));
		assertEquals(ulrikasBalance-100.00d, SweBank.getBalance("Ulrika"), 0);
	}
	
	@Test
	/* Testuje się poprawność 
	 * zwracanej wyniku 
	 * dla wartości dodatnich, ujemnych i zerowych
	 * oraz testuje się możliwość otrzymania balansu z nieistniejącego konta
	 */
	
	public void testGetBalance() throws AccountDoesNotExistException {
		assertThrows(AccountDoesNotExistException.class, () -> DanskeBank.getBalance("Gertrud0"));
		assertEquals(0d, DanskeBank.getBalance("Gertrud"), 0);
		DanskeBank.deposit("Gertrud", new Money(10000, DKK));
		assertEquals(100.00d, DanskeBank.getBalance("Gertrud"), 0);
		DanskeBank.withdraw("Gertrud", new Money(20000, DKK));
		assertEquals(-100.00d, DanskeBank.getBalance("Gertrud"), 0);
	}
	
	@Test
	/* Testuje się możliwość robienia z lub do kont nieistniących 
	 * i poprawność wykonania przelewu
	 * w ramach jednego banku i między różnymi
	 */
	public void testTransfer() throws AccountDoesNotExistException {
		// dla kont z różnych Banków
		Double nordeaBobBalance = Nordea.getBalance("Bob");
		Double sweBankBobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob1", Nordea, "Bob", new Money(10000, DKK)));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob", Nordea, "Bob1", new Money(10000, DKK)));
		SweBank.transfer("Bob", Nordea, "Bob", new Money(10000, DKK));
		assertEquals(sweBankBobBalance-new Money(SEK.valueInThisCurrency(10000, DKK), SEK).getAmount(), SweBank.getBalance("Bob"), 0);
		assertEquals(nordeaBobBalance+new Money(SEK.valueInThisCurrency(10000, DKK), SEK).getAmount(), Nordea.getBalance("Bob"), 0);	
		// dla kont z tego samego Banku
		Double ulrikaBalance = SweBank.getBalance("Ulrika");
		Double bobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob1", "Bob", new Money(10000, SEK)));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob", "Bob1", new Money(10000, SEK)));
		SweBank.transfer("Bob", "Ulrika", new Money(10000, SEK));
		assertEquals(bobBalance-100.00d, SweBank.getBalance("Bob"), 0);
		assertEquals(ulrikaBalance+100.00d, SweBank.getBalance("Ulrika"), 0);
	}
	
	@Test
	/* Testuje się możliwość dodania płatności regularnej na nieistniące konto,
	 * dodanie i działanie płatności regularnej
	 * i usunięcie płatności regularnej
	 */
	public void testTimedPayment() throws AccountDoesNotExistException {
		//dodanie i działanie płatności regularnej 
		Double nordeaBobBalance = Nordea.getBalance("Bob");
		Double sweBankBobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.addTimedPayment("Bob1", "aa", 1, 1, new Money(10000, SEK), Nordea, "Bob" ));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.addTimedPayment("Bob", "aa", 1, 1, new Money(10000, SEK), Nordea, "Bob1" ));
		SweBank.addTimedPayment("Bob", "aa", 1, 1, new Money(10000, SEK), Nordea, "Bob" );
		SweBank.tick();
		assertEquals(sweBankBobBalance-100.00d, SweBank.getBalance("Bob"), 0);
		assertEquals(nordeaBobBalance+100.00d, Nordea.getBalance("Bob"), 0);	
		//usunięcie płatności regularnej
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.removeTimedPayment("Bob1", "aa"));
		nordeaBobBalance = Nordea.getBalance("Bob");
		sweBankBobBalance = SweBank.getBalance("Bob");
		SweBank.removeTimedPayment("Bob", "aa");
		SweBank.tick();
		assertEquals(sweBankBobBalance, SweBank.getBalance("Bob"), 0);
		assertEquals(nordeaBobBalance, Nordea.getBalance("Bob"), 0);
	}
}
