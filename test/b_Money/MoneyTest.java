package b_Money;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MoneyTest {
	Currency SEK, DKK, NOK, EUR;
	Money SEK100, EUR10, SEK200, EUR20, SEK0, EUR0, SEKn100;
	
	@Before
	public void setUp() throws Exception {
		SEK = new Currency("SEK", 0.15);
		DKK = new Currency("DKK", 0.20);
		EUR = new Currency("EUR", 1.5);
		SEK100 = new Money(10000, SEK);
		EUR10 = new Money(1000, EUR);
		SEK200 = new Money(20000, SEK);
		EUR20 = new Money(2000, EUR);
		SEK0 = new Money(0, SEK);
		EUR0 = new Money(0, EUR);
		SEKn100 = new Money(-10000, SEK);
	}

	@Test
	public void testGetAmount() {
		assertEquals(-100.00d, SEKn100.getAmount(), 0);
		assertEquals(0.00d, EUR0.getAmount(), 0);
		assertEquals(200.00d, SEK200.getAmount(), 0);
		assertEquals(10.00d, EUR10.getAmount(), 0);
	}

	@Test
	public void testGetCurrency() {
		assertSame(SEKn100.getCurrency(), SEK100.getCurrency());
		assertSame(EUR0.getCurrency(), EUR0.getCurrency());
		assertNotSame(SEKn100.getCurrency(), EUR0.getCurrency());
		assertNotSame(EUR10.getCurrency(), SEK0.getCurrency());
	}

	@Test
	public void testToString() {
		assertEquals("10.0 EUR", EUR10.toString());
		assertEquals("0.0 EUR", EUR0.toString());
		assertEquals("100.0 SEK", SEK100.toString());
		assertEquals("-100.0 SEK", SEKn100.toString());
	}

	@Test
	public void testGlobalValue() {
		assertEquals(Integer.valueOf(1500), EUR10.universalValue());
		assertEquals(Integer.valueOf(0), SEK0.universalValue());
		assertEquals(Integer.valueOf(1500), SEK100.universalValue());
		assertEquals(Integer.valueOf(-1500), SEKn100.universalValue());
	}

	@Test
	public void testEqualsMoney() {
		assertTrue(EUR0.equals(SEK0));
		assertTrue(EUR10.equals(SEK100));
		assertTrue(EUR10.equals(EUR10));
		assertTrue(EUR20.equals(SEK200));
		assertFalse(EUR10.equals(SEKn100));
		assertFalse(EUR10.equals(EUR20));
		assertFalse(SEK200.equals(EUR0));
		assertFalse(SEK200.equals(SEK100));
	}

	@Test
	public void testAdd() {
		assertEquals(EUR20, EUR20.add(EUR0));
		assertEquals(SEK100, SEK200.add(SEKn100));
		assertEquals(SEK200, SEK100.add(EUR10));
		assertEquals(EUR0, EUR0.add(SEK0));
	}

	@Test
	public void testSub() {
		assertEquals(EUR10, EUR10.sub(EUR0));
		assertEquals(EUR0, EUR10.sub(SEK100));
		assertEquals(SEK100, SEK200.sub(EUR10));
		assertEquals(SEK0, SEK100.sub(EUR10));
	}

	@Test
	public void testIsZero() {
		assertTrue(EUR0.isZero());
		assertTrue(SEK0.isZero());
	}

	@Test
	public void testNegate() {
		assertEquals(SEK0, SEK0.negate());
		assertEquals(SEKn100, SEK100.negate());
		assertEquals(SEK100, SEKn100.negate());
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, EUR0.compareTo(SEK0));
		assertEquals(0, EUR20.compareTo(SEK200));
		assertTrue(EUR0.compareTo(EUR20) < 0);
		assertTrue(EUR10.compareTo(SEK200) < 0);
		assertTrue(EUR20.compareTo(SEKn100) > 0);
		assertTrue(SEK0.compareTo(SEKn100) > 0);
	}
}
