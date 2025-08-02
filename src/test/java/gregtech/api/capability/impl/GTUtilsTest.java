package gregtech.api.capability.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.*;

import static gregtech.api.GTValues.*;
import static gregtech.api.util.GTUtility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class GTUtilsTest {

	private static Stream<Arguments> voltageTestArgs() {
		final long[] voltages = {
			2, 4, 8, 9, 16, 17, 31,
			32, 33, 127,
			128, 129, 256, 257, 511,
			512, 513, 2047,
			2048, 2049, 4096,
			32767,
			32768, 32769,
			1_000_000, 2_000_000,
			3_000_000,
			Integer.MAX_VALUE, Long.MAX_VALUE / 4, Long.MAX_VALUE
		};

		final int[] expectedTiers = {
			ULV, ULV, ULV, ULV, ULV, ULV, ULV,
			LV, LV, LV,
			MV, MV, MV, MV, MV,
			HV, HV, HV,
			EV, EV, EV,
			IV,
			LuV, LuV,
			UV, UV,
			UHV,
			MAX, MAX, MAX
		};

		return IntStream.range(0, voltages.length)
		                .mapToObj(i -> arguments(voltages[i], expectedTiers[i]));
	}

	@ParameterizedTest
	@MethodSource("voltageTestArgs")
	public void test_voltage_tier(long voltage, int expected) {
		int tier = getTierByVoltage(voltage);
		assertEquals(expected, tier, "Incorrect voltage for input value " + voltage);
	}

	@ParameterizedTest
	@MethodSource("voltageTestArgs")
	public void test_voltage_tier_bitshift(long voltage, int expected) {
		int tier;
		if(voltage <= V[ULV])
			tier = 0;
		else if(voltage > V[MAX])
			tier = MAX;
		else {
			tier = (byte) ((62 - Long.numberOfLeadingZeros(voltage - 1)) >> 1);
			if(voltage < V[tier])
				tier--;
		}

		assertEquals(expected, tier, "Incorrect voltage for input value " + voltage);
	}

	@ParameterizedTest
	@MethodSource("voltageTestArgs")
	public void test_voltage_between_tiers(long voltage, int expected) {
		int tier = getTierByVoltage(voltage);
		if(tier > ULV && tier < MAX && voltage > V[tier]) {
			System.out.printf("%d => %d (%s)%n", voltage, V[tier], VN[tier]);
			assertEquals(expected, tier);
		}
	}

	private static Stream<Arguments> powerTiers() {
		return IntStream.range(ULV, MAX).boxed().map(Arguments::arguments);
	}

	@ParameterizedTest
	@MethodSource("powerTiers")
	public void test_bitshift_math_for_quadrupling(int i) {
		assertEquals(1 << (2 * i), (1 << i) * (1 << i), "Failed on index " + i);
	}

	private static Stream<Arguments> durationInputs() {
		return Arrays.stream(new int[] {7, 8, 9, 15, 16, 17, 32, 33, 34, 63, 64, 65, 127, 128, 129})
		             .boxed()
		             .map(Arguments::of);
	}

	@ParameterizedTest
	@MethodSource("durationInputs")
	public void test_duration_bitshift_math(int duration) {
		int log2d = (int) (Math.log(duration) / Math.log(2));
		System.out.println("Duration: " + duration + ", result: " + log2d);
		assertEquals(log2d, (31 - Integer.numberOfLeadingZeros(duration)), "Failed on duration " + duration);
	}

	@ParameterizedTest
	@MethodSource("powerTiers")
	public void test_pow4_bitshift_math(int i) {
		assertEquals(Math.pow(4, i), 1L << (2 * i));
	}
}
