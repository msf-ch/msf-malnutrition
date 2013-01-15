package org.msf.android.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;
import org.joda.time.Years;

public class MedicalTimeUtils {
	public static final int AGE_DAYS_MINIMUM_DEFAULT = 0;
	public static final int AGE_DAYS_MAXIMUM_DEFAULT = 28;
	public static final int AGE_WEEKS_MINIMUM_DEFAULT = 2;
	public static final int AGE_WEEKS_MAXIMUM_DEFAULT = 26;
	public static final int AGE_MONTHS_MINIMUM_DEFAULT = 3;
	public static final int AGE_MONTHS_MAXIMUM_DEFAULT = 48;
	public static final int AGE_YEARS_MINIMUM_DEFAULT = 1;
	public static final int AGE_YEARS_MAXIMUM_DEFAULT = Integer.MAX_VALUE;

	public static int getElapsedTimeYears(Date startDate) {
		LocalDate birthLocalDate = new LocalDate(startDate);
		LocalDate now = new LocalDate();

		Years age = Years.yearsBetween(birthLocalDate, now);
		return age.getYears();
	}

	public static int getElapsedTimeMonths(Date startDate) {
		LocalDate birthLocalDate = new LocalDate(startDate);
		LocalDate now = new LocalDate();

		Months age = Months.monthsBetween(birthLocalDate, now);
		return age.getMonths();
	}

	public static int getElapsedTimeWeeks(Date startDate) {
		LocalDate birthLocalDate = new LocalDate(startDate);
		LocalDate now = new LocalDate();

		Weeks age = Weeks.weeksBetween(birthLocalDate, now);
		return age.getWeeks();
	}

	public static int getElapsedTimeDays(Date startDate) {
		LocalDate birthLocalDate = new LocalDate(startDate);
		LocalDate now = new LocalDate();

		Days age = Days.daysBetween(birthLocalDate, now);
		return age.getDays();
	}

	public static String getDefaultLongAge(Date birthDate) {
		LocalDate birthLocalDate = new LocalDate(birthDate);
		LocalDate now = new LocalDate();

		int days = Days.daysBetween(birthLocalDate, now).getDays();
		int weeks = Weeks.weeksBetween(birthLocalDate, now).getWeeks();
		int months = Months.monthsBetween(birthLocalDate, now).getMonths();
		int years = Years.yearsBetween(birthLocalDate, now).getYears();

		List<DurationFieldType> fieldTypes = new ArrayList<DurationFieldType>();

		if (days >= AGE_DAYS_MINIMUM_DEFAULT
				&& days <= AGE_DAYS_MAXIMUM_DEFAULT) {
			fieldTypes.add(DurationFieldType.days());
		}

		if (weeks >= AGE_WEEKS_MINIMUM_DEFAULT
				&& weeks <= AGE_WEEKS_MAXIMUM_DEFAULT) {
			fieldTypes.add(DurationFieldType.weeks());
		}

		if (months >= AGE_MONTHS_MINIMUM_DEFAULT
				&& weeks <= AGE_MONTHS_MAXIMUM_DEFAULT) {
			fieldTypes.add(DurationFieldType.months());
		}

		if (years >= AGE_YEARS_MINIMUM_DEFAULT
				&& years <= AGE_YEARS_MAXIMUM_DEFAULT) {
			fieldTypes.add(DurationFieldType.years());
		}

		PeriodType type = PeriodType.forFields(fieldTypes
				.toArray(new DurationFieldType[fieldTypes.size()]));
		Period period = new Period(birthLocalDate, new LocalDate(),
				type);

		StringBuffer sb = new StringBuffer();

		if (type.isSupported(DurationFieldType.years())) {
			int periodYears = period.getYears();
			sb.append(periodYears);
			sb.append(" year");
			if (periodYears > 1) {
				sb.append("s");
			}
		}

		if (type.isSupported(DurationFieldType.months())) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			int periodMonths = period.getMonths();
			sb.append(periodMonths);
			sb.append(" month");
			if (periodMonths > 1) {
				sb.append("s");
			}
		}

		if (type.isSupported(DurationFieldType.weeks())) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			int periodWeeks = period.getWeeks();
			sb.append(periodWeeks);
			sb.append(" week");
			if (periodWeeks > 1) {
				sb.append("s");
			}
		}

		if (type.isSupported(DurationFieldType.days())) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			int periodDays = period.getDays();
			sb.append(periodDays);
			sb.append(" day");
			if (periodDays > 1) {
				sb.append("s");
			}
		}
		
		return sb.toString();
	}
}
