package b_Money;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CurrencyTest {
	Currency SEK, DKK, NOK, EUR;
	
	@Before
	public void setUp() throws Exception {
		/* Setup currencies with exchange rates */
		SEK = new Currency("SEK", 0.15);
		DKK = new Currency("DKK", 0.20);
		EUR = new Currency("EUR", 1.5);
	}

	@Test
	public void testGetName() {
		assertEquals("DKK", DKK.getName());
		assertEquals("EUR", EUR.getName());
		assertEquals("SEK", SEK.getName());
	}
	
	@Test
	public void testGetRate() {
		assertEquals(0.15, SEK.getRate(), 0);
		assertEquals(0.20, DKK.getRate(), 0);
		assertEquals(1.5, EUR.getRate(), 0);
	}
	
	@Test
	public void testSetRate() {
		Currency USD = new Currency("USD", 1d);
		assertEquals(1d, USD.getRate(), 0d);
		USD.setRate(2d);
		assertEquals(2d, USD.getRate(), 0d);
	}
	
	@Test
	public void testGlobalValue() {
		assertEquals(Integer.valueOf(150), EUR.universalValue(100));
		assertEquals(Integer.valueOf(-40), DKK.universalValue(-200));
		assertEquals(Integer.valueOf(0), SEK.universalValue(0));
	}
	
	@Test
	public void testValueInThisCurrency() {
		assertEquals(Integer.valueOf(1000), EUR.valueInThisCurrency(10000, SEK));
		assertEquals(Integer.valueOf(13333), SEK.valueInThisCurrency(10000, DKK));
		assertEquals(Integer.valueOf(0), EUR.valueInThisCurrency(0, DKK));
		assertEquals(Integer.valueOf(-22500), DKK.valueInThisCurrency(-30000, SEK));
	}

}
