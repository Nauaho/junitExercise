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
		Integer ulrikasBalance = SweBank.getBalance("Ulrika");
		SweBank.deposit("Ulrika", new Money(10000, SEK));
		assertNotEquals(ulrikasBalance, SweBank.getBalance("Ulrika"));
		assertEquals(Integer.valueOf(ulrikasBalance + 10000), SweBank.getBalance("Ulrika"));
	}

	@Test
	/* Testuje się możliwość wypłaty pieniędzy na nieistniące konto
	 * i wypłaty pieniędzy na istniące konto
	 */
	public void testWithdraw() throws AccountDoesNotExistException {
		assertThrows(AccountDoesNotExistException.class, () -> SweBank.withdraw("Ulrika0", new Money(10000, SEK)));
		Integer ulrikasBalance = SweBank.getBalance("Ulrika");
		SweBank.withdraw("Ulrika", new Money(10000, SEK));
		assertEquals(ulrikasBalance, SweBank.getBalance("Ulrika"));
		assertNotEquals(Integer.valueOf(ulrikasBalance - 10000), ulrikasBalance);
	}
	
	@Test
	/* Testuje się poprawność 
	 * zwracanej wyniku 
	 * dla wartości dodatnich, ujemnych i zerowych
	 * oraz testuje się możliwość otrzymania balansu z nieistniejącego konta
	 */
	public void testGetBalance() throws AccountDoesNotExistException {
		assertThrows(AccountDoesNotExistException.class, () -> DanskeBank.getBalance("Gertrud0"));
		assertEquals(Integer.valueOf(0), DanskeBank.getBalance("Gertrud"));
		DanskeBank.deposit("Gertrud", new Money(10000, DKK));
		assertEquals(Integer.valueOf(10000), DanskeBank.getBalance("Gertrud"));
		DanskeBank.withdraw("Gertrud", new Money(20000, DKK));
		assertEquals(Integer.valueOf(-10000), DanskeBank.getBalance("Gertrud"));
	}
	
	@Test
	/* Testuje się możliwość robienia z lub do kont nieistniących 
	 * i poprawność wykonania przelewu
	 * w ramach jednego banku i między różnymi
	 */
	public void testTransfer() throws AccountDoesNotExistException {
		// dla kont z różnych Banków
		Integer nordeaBobBalance = Nordea.getBalance("Bob");
		Integer sweBankBobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob1", Nordea, "Bob", new Money(10000, DKK)));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob", Nordea, "Bob1", new Money(10000, DKK)));
		SweBank.transfer("Bob", Nordea, "Bob", new Money(10000, DKK));
		assertEquals(Integer.valueOf(sweBankBobBalance-10000), SweBank.getBalance("Bob"));
		assertEquals(Integer.valueOf(nordeaBobBalance+10000), Nordea.getBalance("Bob"));	
		// dla kont z tego samego Banku
		Integer ulrikaBalance = Nordea.getBalance("Ulrika");
		Integer bobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob1", "Bob", new Money(10000, DKK)));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.transfer("Bob", "Bob1", new Money(10000, DKK)));
		SweBank.transfer("Bob", "Ulrika", new Money(10000, DKK));
		assertEquals(Integer.valueOf(bobBalance-10000), SweBank.getBalance("Bob"));
		assertEquals(Integer.valueOf(ulrikaBalance+10000), SweBank.getBalance("Bob"));
	}
	
	@Test
	/* Testuje się możliwość dodania płatności regularnej na nieistniące konto,
	 * dodanie i działanie płatności regularnej
	 * i usunięcie płatności regularnej
	 */
	public void testTimedPayment() throws AccountDoesNotExistException {
		//dodanie i działanie płatności regularnej 
		Integer nordeaBobBalance = Nordea.getBalance("Bob");
		Integer sweBankBobBalance = SweBank.getBalance("Bob");
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.addTimedPayment("Bob1", "aa", 1, 1, new Money(10000, DKK), Nordea, "Bob" ));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.addTimedPayment("Bob", "aa", 1, 1, new Money(10000, DKK), Nordea, "Bob1" ));
		SweBank.addTimedPayment("Bob", "aa", 1, 1, new Money(10000, DKK), Nordea, "Bob" );
		SweBank.tick();
		assertEquals(Integer.valueOf(sweBankBobBalance-10000), SweBank.getBalance("Bob"));
		assertEquals(Integer.valueOf(nordeaBobBalance+10000), Nordea.getBalance("Bob"));	
		//usunięcie płatności regularnej
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.removeTimedPayment("Bob1", "aa"));
		assertThrows(AccountDoesNotExistException.class, 
			() -> SweBank.removeTimedPayment("Bob", "bb"));
		nordeaBobBalance = Nordea.getBalance("Bob");
		sweBankBobBalance = SweBank.getBalance("Bob");
		SweBank.removeTimedPayment("Bob", "aa");
		SweBank.tick();
		assertEquals(Integer.valueOf(sweBankBobBalance), SweBank.getBalance("Bob"));
		assertEquals(Integer.valueOf(nordeaBobBalance), Nordea.getBalance("Bob"));
	}
}
