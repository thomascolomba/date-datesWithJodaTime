package datesWithJodaTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


public class JodaTimeHandling {
	public static void main(String[] args) {
		showYear1970();
		showYearMinus5();
		showYearSumerCivilization();
		showYearTyrannosaurus();
		showAddingHourChangesDay();
		showAddingDayChangesMonth();
		showAddingDayChangesYear();
		showAddingMonthChangesYear();
		showLeapAndNonLeapYear();
		showDifferencesBetweenDifferentTimezones();
		showAllTimezonesAvailable();
		showSomeDatesThroughPatterns();
		showParsingOfSomeStringsThroughPatterns();
		showSomeDST();
		showHourDifferenceBetweenLondonAndNewYorkVariesDueToDST();
		showSomeJodaDateTimeAndTimezoneClassesBehaviourDependingOnSystemTimezone();
		showSomeJodaDateTimeAndTimezoneClassesBehaviourNotDependingOnSystemTimezone();
		showJodaDateTimeDoesnotHandleLeapSecond();
		showHowToAddressAnHourThatHappensTwiceDuringTheSameDayWhenTheClockGoesBackward();
	}
	
	private static void showHowToAddressAnHourThatHappensTwiceDuringTheSameDayWhenTheClockGoesBackward() {
		System.out.println("showHowToAddressAnHourThatHappensTwiceDuringTheSameDayWhenTheClockGoesBackward");
		//London Sunday, October 25, 2:00 am -> 1:00 am. So 1:30 am happens twice. (see method showDSTLondonClockBackward()) 
		long timestampFirstTimeItSHalfOne = 1603585800000L;//1603585800000L is the timestamp for the first time it's 1:30 (before clock goes backward)
		DateTime dateTimeFirstTime = new DateTime(timestampFirstTimeItSHalfOne, DateTimeZone.forID("Europe/London"));
		showJodaDateAndTimeAndTimezone(dateTimeFirstTime);
		System.out.println(dateTimeFirstTime.getMillis());
		
		long timestampSecondTimeItSHalfOne = 1603589400000L;//1603589400000L is the timestamp for the second time it's 1:30 (after clock goes backward)
		DateTime dateTimeSecondTime = new DateTime(timestampSecondTimeItSHalfOne, DateTimeZone.forID("Europe/London"));
		showJodaDateAndTimeAndTimezone(dateTimeSecondTime);
		System.out.println(dateTimeSecondTime.getMillis());
	}
	
	private static void showJodaDateTimeDoesnotHandleLeapSecond() {
		System.out.println("showJodaDateTimeDoesnotHandleLeapSecond");
		DateTime dt = new DateTime("2005-12-31T23:59:59.000");
		DateTime dtPlus1Second = dt.plusSeconds(1);
		System.out.println("Does Joda DateTime handle the leap second at the end of 2005 : "+(dtPlus1Second.getYear() == 2005));
	}
	
	private static void showSomeJodaDateTimeAndTimezoneClassesBehaviourDependingOnSystemTimezone() {
		System.out.println("showSomeJodaDateTimeAndTimezoneClassesBehaviourDependingOnSystemTimezone");
		DateTime dt = new DateTime();
		//"new DateTime()" uses jvm/system configuration (timezone), therefore the result of "new DateTime()" varies when executed in different timezones.
		//You may test it by executing that method using different OS timezone configuration.
		showSomeJodaDateTimeAndTimezoneClassesBehaviour(dt);
	}
	private static void showSomeJodaDateTimeAndTimezoneClassesBehaviourNotDependingOnSystemTimezone() {
		System.out.println("showSomeJodaDateTimeAndTimezoneClassesBehaviourNotDependingOnSystemTimezone");
		DateTime dt = new DateTime(DateTimeZone.forID("America/New_York"));
		//other possibilities to have DateTime not depending on execution environment : 
		//	1) change value of DateTimeZone.getDefault.(), but this is very invasive,and may affect every part of the program using the default timezone
		//	2) Instead of using "new DateTime(myTimezone)" each time we need it, we could use one "BusinessDateTimeFactory" (or several if needed) that returns a DateTime initialized with the appropriate timezone so we would avoid bugs due to wrongly initialized DateTime objects. 
		showSomeJodaDateTimeAndTimezoneClassesBehaviour(dt);
	}
	private static void showSomeJodaDateTimeAndTimezoneClassesBehaviour(DateTime dt) {
		showJodaDateAndTimeAndTimezone(dt);
	}
	private static void showHourDifferenceBetweenLondonAndNewYorkVariesDueToDST() {
		System.out.println("showHourDifferenceBetweenLondonAndNewYorkVariesDueToDST");
		//Date in year : 	January		NY-switchToSummerTime	Lon-switchToSummerTime	Lon-switchToWinterTime	NY-switchToWinterTime	...EnfOfYear
		//hour difference :	+5			+4						+5						+4						+5
		System.out.println("beginning of the year, hour difference between London and New York is : 5 hours");
		whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020, DateTimeConstants.JANUARY,  8,  12,  0, 0, "America/New_York", "Europe/London");//usual +5 hours
		System.out.println("New York clock goes 1 hour forward : hour difference now is : 4 hours");
		try {
			whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020, DateTimeConstants.MARCH,  8,  2,  30, 0, "America/New_York", "Europe/London");//delta is 4 due to DST(summer time hour change) for NewYork at this date
		} catch(org.joda.time.IllegalInstantException e) {
			//yeah, 2:30am does not exist on 8th of March 2020 due to DST so an exception is raised
			//todo : nothing
		}
		whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020, DateTimeConstants.MARCH,  8,  7,  30, 0, "Europe/London", "America/New_York");//delta is 4 due to DST(summer time hour change) for NewYork at this date
		System.out.println("London clock goes 1 hour forward : hour difference now is : 5 hours");
		whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020,  DateTimeConstants.MARCH,  29, 0, 30, 0, "America/New_York", "Europe/London");//usual +5 hours because DST for 
		//London Sunday, October 25, 2:00 am
		System.out.println("London clock goes 1 hour backward : hour difference now is : 4 hours");
		whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020,  DateTimeConstants.OCTOBER,  25, 0, 30, 0, "America/New_York", "Europe/London");//delta is 4 hours
		//NY Sunday, November 1, 2:00 am
		System.out.println("New York clock goes 1 hour backward : hour difference now is : 5 hours");
		whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(2020,  DateTimeConstants.NOVEMBER,  1, 2, 30, 0, "America/New_York", "Europe/London");//back to 5
	}
	private static void showSomeDST() {
		System.out.println("showSomeDST");
		showDSTNewYork();
		showDSTLondon();
		showDSTBerlin();
		showDSTLondonClockBackward();
	}
	private static void showDSTNewYork() {
		//8th of March 2020 2am -> 3am
		DateTime nyDST = ISODateTimeFormat.dateHourMinute().withZone(DateTimeZone.forID("America/New_York")).parseDateTime("2020-03-08T01:30");
		showJodaDateAndTimeAndTimezone(nyDST);
		DateTime oneHourLater = nyDST.plusHours(1);
		showJodaDateAndTimeAndTimezone(oneHourLater);
	}
	private static void showDSTLondon() {
		//29th of March 2020 1am -> 2am
		DateTime londonDST = ISODateTimeFormat.dateHourMinute().withZone(DateTimeZone.forID("Europe/London")).parseDateTime("2020-03-29T00:30");
		showJodaDateAndTimeAndTimezone(londonDST);
		DateTime oneHourLater = londonDST.plusHours(1);
		showJodaDateAndTimeAndTimezone(oneHourLater);
	}
	private static void showDSTBerlin() {
		//29th of March 2020 2am -> 3am
		DateTime berlinDST = ISODateTimeFormat.dateHourMinute().withZone(DateTimeZone.forID("Europe/Berlin")).parseDateTime("2020-03-29T01:30");
		showJodaDateAndTimeAndTimezone(berlinDST);
		DateTime oneHourLater = berlinDST.plusHours(1);
		showJodaDateAndTimeAndTimezone(oneHourLater);
	}
	private static void showDSTLondonClockBackward() {
		//25th of October 2020 2am -> 1am
		DateTime londonDST = ISODateTimeFormat.dateHourMinute().withZone(DateTimeZone.forID("Europe/London")).parseDateTime("2020-10-25T01:30");
		showJodaDateAndTimeAndTimezone(londonDST);
		System.out.println(londonDST.getMillis());
		System.out.println("+ 1 hour");
		DateTime oneHourAfter = londonDST.plusHours(1);
		showJodaDateAndTimeAndTimezone(oneHourAfter);
		System.out.println(oneHourAfter.getMillis());
	}
	
	private static void showParsingOfSomeStringsThroughPatterns() {
		System.out.println("showParsingOfSomeStringsThroughPatterns");
		showParsingOfSomeStringsThroughDateTimeFormatWithPatterns();
		showParsingOfSomeStringsThroughISO8601UTC();
		showParsingOfSomeStringsInAPredefinedTimezone();
	}
	private static void showParsingOfSomeStringsThroughDateTimeFormatWithPatterns() {
		showJodaDateAndTimeAndTimezone(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime("2001-07-04T12:08:56.235-07:00"));//fails -> timezone is always Berlin...
		showJodaDateAndTimeAndTimezone(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").parseDateTime("2001-07-04T12:08:56.235-06:00"));//fails -> timezone is always Berlin...
		showJodaDate(DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime("01/01/2000"));
		showJodaDateAndTime(DateTimeFormat.forPattern("dd/MM/yyyy-hh").parseDateTime("01/01/2000-08"));
		showJodaDate(DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime("01/01/-4"));
		showJodaDate(DateTimeFormat.forPattern("ww/yyyy").parseDateTime("10/2000"));
	}
	private static void showParsingOfSomeStringsThroughISO8601UTC() {
		showJodaDateAndTimeAndTimezone(ISODateTimeFormat.dateTimeParser().parseDateTime("2012-01-19T12:00:00-05:00"));//The resulting DateTime is expressed in the execution environment's timezone, that can be changed using DateTime.withZone(...)
		showJodaDateAndTimeAndTimezone(ISODateTimeFormat.dateTimeParser().parseDateTime("2012-01-19T19:00:00.000Z"));//use the 1Z representation  (01:00:00.000-05:00 is equal to 06:00:00.000Z)
	}
	private static void showParsingOfSomeStringsInAPredefinedTimezone() {
		String stringDateToParseToDateTime = "2000-01-01";
		showParsingOfADateStringInAPredefinedTimezone(stringDateToParseToDateTime, DateTimeZone.forID("Europe/Berlin"));
		showParsingOfADateStringInAPredefinedTimezone(stringDateToParseToDateTime, DateTimeZone.forID("Europe/London"));
		showParsingOfADateStringInAPredefinedTimezone(stringDateToParseToDateTime, DateTimeZone.forID("America/New_York"));

		String stringDateHourToParseToDateTime = "2000-01-01T15";
		showParsingOfADateHourStringInAPredefinedTimezone(stringDateHourToParseToDateTime, DateTimeZone.forID("Europe/Berlin"));
		showParsingOfADateHourStringInAPredefinedTimezone(stringDateHourToParseToDateTime, DateTimeZone.forID("Europe/London"));
		showParsingOfADateHourStringInAPredefinedTimezone(stringDateHourToParseToDateTime, DateTimeZone.forID("America/New_York"));
		
		String stringDateHourMinuteToParseToDateTime = "2000-01-01T15:10";
		showParsingOfADateHourMinuteStringInAPredefinedTimezone(stringDateHourMinuteToParseToDateTime, DateTimeZone.forID("Europe/Berlin"));
		showParsingOfADateHourMinuteStringInAPredefinedTimezone(stringDateHourMinuteToParseToDateTime, DateTimeZone.forID("Europe/London"));
		showParsingOfADateHourMinuteStringInAPredefinedTimezone(stringDateHourMinuteToParseToDateTime, DateTimeZone.forID("America/New_York"));
	}
	private static void showParsingOfADateStringInAPredefinedTimezone(String stringToParseToDateTime, DateTimeZone dateTimeZone) {
		showJodaDateAndTimezone(ISODateTimeFormat.date().withZone(dateTimeZone).parseDateTime(stringToParseToDateTime));//withZone() tells the parser to read a date as if it was expressed in a specific timezone
	}
	private static void showParsingOfADateHourStringInAPredefinedTimezone(String stringToParseToDateTime, DateTimeZone dateTimeZone) {
		showJodaDateAndTimeAndTimezone(ISODateTimeFormat.dateHour().withZone(dateTimeZone).parseDateTime(stringToParseToDateTime));
	}
	private static void showParsingOfADateHourMinuteStringInAPredefinedTimezone(String stringToParseToDateTime, DateTimeZone dateTimeZone) {
		showJodaDateAndTimeAndTimezone(ISODateTimeFormat.dateHourMinute().withZone(dateTimeZone).parseDateTime(stringToParseToDateTime));
	}
	
	private static void showSomeDatesThroughPatterns() {
		System.out.println("showSomeDatesThroughPatterns");
		showSomeDatesThroughPatternsAsISO8601();
		showSomeDatesThroughPatternsWithDateTimeFormat();
	}
	private static void showSomeDatesThroughPatternsAsISO8601() {
		DateTime dt = new DateTime();
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		System.out.println(fmt.print(dt));
	}
	private static void showSomeDatesThroughPatternsWithDateTimeFormat() {
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("dd/MM/yyyy").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd G").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss:SSS").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss:SSSz").print(new DateTime()));
		 System.out.println(DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss:SSSZ").print(new DateTime()));
	}
	
	private static void showDifferencesBetweenDifferentTimezones() {
		System.out.println("showDifferencesBetweenDifferentTimezones");
		DateTimeZone berlinTz = DateTimeZone.forID("Europe/Berlin");
		DateTimeZone londonTz = DateTimeZone.forID("Europe/London");
		DateTimeZone belfastTz = DateTimeZone.forID("Europe/Belfast");
		int standardOffsetBerlinInMs = berlinTz.getStandardOffset(new DateTime(2000, DateTimeConstants.JANUARY, 1, 0, 0, berlinTz).getMillis());
		int standardOffsetLondonInMs = londonTz.getStandardOffset(new DateTime(2000, DateTimeConstants.JANUARY, 1, 0, 0, londonTz).getMillis());
		int standardOffsetBelfastInMs = belfastTz.getStandardOffset(new DateTime(2000, DateTimeConstants.JANUARY, 1, 0, 0, belfastTz).getMillis());
		System.out.println(standardOffsetBerlinInMs / DateTimeConstants.MILLIS_PER_HOUR);
		System.out.println(standardOffsetLondonInMs / DateTimeConstants.MILLIS_PER_HOUR);
		System.out.println(standardOffsetBelfastInMs / DateTimeConstants.MILLIS_PER_HOUR);
	}

	private static void showAllTimezonesAvailable() {
		System.out.println("showAllTimezonesAvailable");
		for(String id : DateTimeZone.getAvailableIDs()) {
	 		System.out.println(id);
	 	}
	}

	private static void showLeapAndNonLeapYear() {
		System.out.println("showLeapAndNonLeapYear");
		System.out.println("is "+2003+" a leap year : "+isLeapYear(2003));
		System.out.println("is "+2004+" a leap year : "+isLeapYear(2004));
	}

	private static void showAddingMonthChangesYear() {
		System.out.println("showAddingMonthChangesYear");
		DateTime dt = new DateTime("2000-12-15T00:00:00.000");
		showJodaDateAndTime(dt);
		DateTime dtOneMonthLater = dt.plusMonths(1);
		System.out.println("+1 month");
		showJodaDateAndTime(dtOneMonthLater);
	}
	private static void showAddingDayChangesYear() {
		System.out.println("showAddingDayChangesYear");
		DateTime dt = new DateTime("2000-12-31T00:00:00.000");
		showJodaDateAndTime(dt);
		DateTime dtOneDayLater = dt.plusDays(1);
		System.out.println("+1 day");
		showJodaDateAndTime(dtOneDayLater);
	}
	private static void showAddingDayChangesMonth() {
		System.out.println("showAddingDayChangesMonth");
		DateTime dt = new DateTime("2000-01-31T00:00:00.000");
		showJodaDateAndTime(dt);
		DateTime dtOneDayLater = dt.plusDays(1);
		System.out.println("+1 day");
		showJodaDateAndTime(dtOneDayLater);
	}
	private static void showAddingHourChangesDay() {
		System.out.println("showAddingHourChangesDay");
		DateTime dt = new DateTime("2000-01-01T23:30:00.000");
		showJodaDateAndTime(dt);
		DateTime dtOneHourLater = dt.plusHours(1);
		System.out.println("+1 hour");
		showJodaDateAndTime(dtOneHourLater);
	}

	private static void showYear1970() {
		System.out.println("showYear1970");
		DateTime dt = new DateTime("1970-01-01T00:00:00.000+00:00");
		showJodaDate(dt);
	}
	private static void showYearMinus5() {
		System.out.println("showYearMinus5");
		DateTime dt = new DateTime("-0004-01-01T00:00:00.000+00:00");
		showJodaDateAndEra(dt);
	}
	private static void showYearSumerCivilization() {
		System.out.println("showYearSumerCivilization");
		DateTime dt = new DateTime("-3400-01-01T00:00:00.000+00:00");
		showJodaDateAndEra(dt);
	}
	private static void showYearTyrannosaurus() {
		System.out.println("showYearTyrannosaurus");
		DateTime dt = new DateTime("-68000000-01-01T00:00:00.000+00:00");
		showJodaDateAndEra(dt);
	}
	
	//util methods
	private static void showJodaDate(ReadableDateTime dt) {
		System.out.println(dt.get(DateTimeFieldType.year()) +" - "+dt.get(DateTimeFieldType.monthOfYear()) +" - "+dt.get(DateTimeFieldType.dayOfMonth()));
	}
	private static void showJodaDateAndEra(ReadableDateTime dt) {
		System.out.println(dt.get(DateTimeFieldType.year()) +" - "+dt.get(DateTimeFieldType.monthOfYear()) +" - "+dt.get(DateTimeFieldType.dayOfMonth())+ ", era : "+(dt.get(DateTimeFieldType.era()) == DateTimeConstants.AD ? "AD" : "BC" ));
	}
	private static void showJodaDateAndTime(ReadableDateTime dt) {
		System.out.println(dt.get(DateTimeFieldType.year()) +" - "+dt.get(DateTimeFieldType.monthOfYear()) +" - "+dt.get(DateTimeFieldType.dayOfMonth()) +" - "+dt.get(DateTimeFieldType.hourOfDay())+" - "+dt.get(DateTimeFieldType.minuteOfHour())+" - "+dt.get(DateTimeFieldType.secondOfMinute()));
	}
	private static void showJodaDateAndTimeAndTimezone(ReadableDateTime dt) {
		System.out.println(dt.get(DateTimeFieldType.year()) +" - "+dt.get(DateTimeFieldType.monthOfYear()) +" - "+dt.get(DateTimeFieldType.dayOfMonth()) +" - "+dt.get(DateTimeFieldType.hourOfDay())+" - "+dt.get(DateTimeFieldType.minuteOfHour())+" - "+dt.get(DateTimeFieldType.secondOfMinute())+", tz : "+dt.getZone().getID());
	}
	private static void showJodaDateAndTimezone(ReadableDateTime dt) {
		System.out.println(dt.get(DateTimeFieldType.year()) +" - "+dt.get(DateTimeFieldType.monthOfYear()) +" - "+dt.get(DateTimeFieldType.dayOfMonth()) +", tz : "+dt.getZone().getID());
	}
	private static void whenItIsXHourAtYTimezoneThenWhatTimeIsItInZTimezone(int year, int month, int day, int hour, int minute, int second, String timezoneIdY, String timezoneIdZ) {
		LocalDateTime ldtY = new LocalDateTime(DateTimeZone.forID(timezoneIdY)).withYear(year).withMonthOfYear(month).withDayOfMonth(day).withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second);
		DateTime dateTimeY = ldtY.toDateTime(DateTimeZone.forID(timezoneIdY));
		showJodaDateAndTimeAndTimezone(dateTimeY);
		showJodaDateAndTimeAndTimezone(dateTimeY.withZone(DateTimeZone.forID(timezoneIdZ)));
	}
	private static Boolean isLeapYear(int year) {
		return new DateTime(year, DateTimeConstants.FEBRUARY, 15, 0, 0, DateTimeZone.forID("Europe/Berlin")).toLocalDate().dayOfMonth().withMaximumValue().getDayOfMonth() == 29;
	}
}
