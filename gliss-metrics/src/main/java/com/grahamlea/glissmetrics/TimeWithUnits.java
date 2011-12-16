package com.grahamlea.glissmetrics;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TimeWithUnits {

    private static final Pattern TIME_STRING_PATTERN = Pattern.compile("(\\d+) +([a-z]+)");

    private final long units;
    private final TimeUnit timeUnit;

    TimeWithUnits(long units, TimeUnit timeUnit) {
        if (timeUnit.convert(1, TimeUnit.MILLISECONDS) > 1)
            throw new IllegalArgumentException(getClass().getSimpleName() + " does not support precisions more specific than milliseconds.");

        this.units = units;
        this.timeUnit = timeUnit;
    }

    public TimeWithUnits(String timeString) {
        Matcher matcher = TIME_STRING_PATTERN.matcher(timeString);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid time string: " + timeString);

        units = Long.parseLong(matcher.group(1));

        TimeUnit parsedUnit = null;
        if (units == 1) {
            try {
                parsedUnit = TimeUnit.valueOf(matcher.group(2).toUpperCase() + "S");
            } catch (IllegalArgumentException e) {
            }
        }
        if (parsedUnit == null) {
            parsedUnit = TimeUnit.valueOf(matcher.group(2).toUpperCase());
        }
        timeUnit = parsedUnit;
    }

    protected long getUnits() {
        return units;
    }

    protected TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long asMillis() {
        return timeUnit.toMillis(units);
    }

    @Override
    public String toString() {
        String timeUnitName = timeUnit.name().toLowerCase();
        if (units == 1)
            return timeUnitName.substring(0, timeUnitName.length() - 1);
        else
            return units + " " + timeUnitName;
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass() == this.getClass() && isEquivalentTo((TimeWithUnits) o);
    }

    public boolean isEquivalentTo(TimeWithUnits other) {
        return timeUnit.toMillis(units) == other.timeUnit.toMillis(other.units);
    }

    @Override
    public int hashCode() {
        return (int) (timeUnit.hashCode() ^ (13 * units));
    }
}
