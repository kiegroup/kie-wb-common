/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;

public abstract class XMLGregorianCalendar
        implements Cloneable {

    /**
     * Default no-arg constructor.
     *
     * <p>Note: Always use the {@link DatatypeFactory} to
     * construct an instance of <code>XMLGregorianCalendar</code>.
     * The constructor on this class cannot be guaranteed to
     * produce an object with a consistent state and may be
     * removed in the future.</p>
     */
    public XMLGregorianCalendar() {
    }

    /**
     * <p>Unset all fields to undefined.</p>
     *
     * <p>Set all int fields to {@link DatatypeConstants#FIELD_UNDEFINED} and reference fields
     * to null.</p>
     */
    public abstract void clear();

    /**
     * <p>Reset this <code>XMLGregorianCalendar</code> to its original values.</p>
     *
     * <p><code>XMLGregorianCalendar</code> is reset to the same values as when it was created with
     * {@link DatatypeFactory#newXMLGregorianCalendar()},
     * {@link DatatypeFactory#newXMLGregorianCalendar(String lexicalRepresentation)},
     * {@link DatatypeFactory#newXMLGregorianCalendar(
     *BigInteger year,
     * int month,
     * int day,
     * int hour,
     * int minute,
     * int second,
     * BigDecimal fractionalSecond,
     * int timezone)},
     * {@link DatatypeFactory#newXMLGregorianCalendar(
     *int year,
     * int month,
     * int day,
     * int hour,
     * int minute,
     * int second,
     * int millisecond,
     * int timezone)},
     * {@link DatatypeFactory#newXMLGregorianCalendar(GregorianCalendar cal)},
     * {@link DatatypeFactory#newXMLGregorianCalendarDate(
     *int year,
     * int month,
     * int day,
     * int timezone)},
     * {@link DatatypeFactory#newXMLGregorianCalendarTime(
     *int hours,
     * int minutes,
     * int seconds,
     * int timezone)},
     * {@link DatatypeFactory#newXMLGregorianCalendarTime(
     *int hours,
     * int minutes,
     * int seconds,
     * BigDecimal fractionalSecond,
     * int timezone)} or
     * {@link DatatypeFactory#newXMLGregorianCalendarTime(
     *int hours,
     * int minutes,
     * int seconds,
     * int milliseconds,
     * int timezone)}.
     * </p>
     *
     * <p><code>reset()</code> is designed to allow the reuse of existing <code>XMLGregorianCalendar</code>s
     * thus saving resources associated with the creation of new <code>XMLGregorianCalendar</code>s.</p>
     */
    public abstract void reset();

    /**
     * <p>Set low and high order component of XSD <code>dateTime</code> year field.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of <code>null</code>.</p>
     * @param year value constraints summarized in <a href="#datetimefield-year">year field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>year</code> parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setYear(BigInteger year);

    /**
     * <p>Set year of XSD <code>dateTime</code> year field.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of
     * {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Note: if the absolute value of the <code>year</code> parameter
     * is less than 10^9, the eon component of the XSD year field is set to
     * <code>null</code> by this method.</p>
     * @param year value constraints are summarized in <a href="#datetimefield-year">year field of date/time field mapping table</a>.
     * If year is {@link DatatypeConstants#FIELD_UNDEFINED}, then eon is set to <code>null</code>.
     */
    public abstract void setYear(int year);

    /**
     * <p>Set month.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param month value constraints summarized in <a href="#datetimefield-month">month field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>month</code> parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setMonth(int month);

    /**
     * <p>Set days in month.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param day value constraints summarized in <a href="#datetimefield-day">day field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>day</code> parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setDay(int day);

    /**
     * <p>Set the number of minutes in the timezone offset.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param offset value constraints summarized in <a href="#datetimefield-timezone">
     * timezone field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>offset</code> parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setTimezone(int offset);

    /**
     * <p>Set time as one unit.</p>
     * @param hour value constraints are summarized in
     * <a href="#datetimefield-hour">hour field of date/time field mapping table</a>.
     * @param minute value constraints are summarized in
     * <a href="#datetimefield-minute">minute field of date/time field mapping table</a>.
     * @param second value constraints are summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if any parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     * @see #setTime(int, int, int, BigDecimal)
     */
    public void setTime(int hour, int minute, int second) {

        setTime(
                hour,
                minute,
                second,
                null // fractional
        );
    }

    /**
     * <p>Set hours.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param hour value constraints summarized in <a href="#datetimefield-hour">hour field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>hour</code> parameter is outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setHour(int hour);

    /**
     * <p>Set minutes.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param minute value constraints summarized in <a href="#datetimefield-minute">minute field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>minute</code> parameter is outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setMinute(int minute);

    /**
     * <p>Set seconds.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param second value constraints summarized in <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>second</code> parameter is outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setSecond(int second);

    /**
     * <p>Set milliseconds.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @param millisecond value constraints summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>millisecond</code> parameter is outside value constraints for the field as specified
     * in <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setMillisecond(int millisecond);

    /**
     * <p>Set fractional seconds.</p>
     *
     * <p>Unset this field by invoking the setter with a parameter value of <code>null</code>.</p>
     * @param fractional value constraints summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @throws IllegalArgumentException if <code>fractional</code> parameter is outside value constraints for the field as specified
     * in <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public abstract void setFractionalSecond(BigDecimal fractional);

    /**
     * <p>Set time as one unit, including the optional infinite precision
     * fractional seconds.</p>
     * @param hour value constraints are summarized in
     * <a href="#datetimefield-hour">hour field of date/time field mapping table</a>.
     * @param minute value constraints are summarized in
     * <a href="#datetimefield-minute">minute field of date/time field mapping table</a>.
     * @param second value constraints are summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @param fractional value of <code>null</code> indicates this optional
     * field is not set.
     * @throws IllegalArgumentException if any parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public void setTime(
            int hour,
            int minute,
            int second,
            BigDecimal fractional) {

        setHour(hour);
        setMinute(minute);
        setSecond(second);
        setFractionalSecond(fractional);
    }

    /**
     * <p>Set time as one unit, including optional milliseconds.</p>
     * @param hour value constraints are summarized in
     * <a href="#datetimefield-hour">hour field of date/time field mapping table</a>.
     * @param minute value constraints are summarized in
     * <a href="#datetimefield-minute">minute field of date/time field mapping table</a>.
     * @param second value constraints are summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.
     * @param millisecond value of {@link DatatypeConstants#FIELD_UNDEFINED} indicates this
     * optional field is not set.
     * @throws IllegalArgumentException if any parameter is
     * outside value constraints for the field as specified in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.
     */
    public void setTime(int hour, int minute, int second, int millisecond) {

        setHour(hour);
        setMinute(minute);
        setSecond(second);
        setMillisecond(millisecond);
    }

    /**
     * <p>Return high order component for XML Schema 1.0 dateTime datatype field for
     * <code>year</code>.
     * <code>null</code> if this optional part of the year field is not defined.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-year">year field of date/time field mapping table</a>.</p>
     * @return eon of this <code>XMLGregorianCalendar</code>. The value
     * returned is an integer multiple of 10^9.
     * @see #getYear()
     * @see #getEonAndYear()
     */
    public abstract BigInteger getEon();

    /**
     * <p>Return low order component for XML Schema 1.0 dateTime datatype field for
     * <code>year</code> or {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-year">year field of date/time field mapping table</a>.</p>
     * @return year  of this <code>XMLGregorianCalendar</code>.
     * @see #getEon()
     * @see #getEonAndYear()
     */
    public abstract int getYear();

    /**
     * <p>Return XML Schema 1.0 dateTime datatype field for
     * <code>year</code>.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-year">year field of date/time field mapping table</a>.</p>
     * @return sum of <code>eon</code> and <code>BigInteger.valueOf(year)</code>
     * when both fields are defined. When only <code>year</code> is defined,
     * return it. When both <code>eon</code> and <code>year</code> are not
     * defined, return <code>null</code>.
     * @see #getEon()
     * @see #getYear()
     */
    public abstract BigInteger getEonAndYear();

    /**
     * <p>Return number of month or {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-month">month field of date/time field mapping table</a>.</p>
     * @return year  of this <code>XMLGregorianCalendar</code>.
     */
    public abstract int getMonth();

    /**
     * Return day in month or {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-day">day field of date/time field mapping table</a>.</p>
     * @see #setDay(int)
     */
    public abstract int getDay();

    /**
     * Return timezone offset in minutes or
     * {@link DatatypeConstants#FIELD_UNDEFINED} if this optional field is not defined.
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-timezone">timezone field of date/time field mapping table</a>.</p>
     * @see #setTimezone(int)
     */
    public abstract int getTimezone();

    /**
     * Return hours or {@link DatatypeConstants#FIELD_UNDEFINED}.
     * Returns {@link DatatypeConstants#FIELD_UNDEFINED} if this field is not defined.
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-hour">hour field of date/time field mapping table</a>.</p>
     * @see #setTime(int, int, int)
     */
    public abstract int getHour();

    /**
     * Return minutes or {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * Returns {@link DatatypeConstants#FIELD_UNDEFINED} if this field is not defined.
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-minute">minute field of date/time field mapping table</a>.</p>
     * @see #setTime(int, int, int)
     */
    public abstract int getMinute();

    /**
     * <p>Return seconds or {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Returns {@link DatatypeConstants#FIELD_UNDEFINED} if this field is not defined.
     * When this field is not defined, the optional xs:dateTime
     * fractional seconds field, represented by
     * {@link #getFractionalSecond()} and {@link #getMillisecond()},
     * must not be defined.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.</p>
     * @return Second  of this <code>XMLGregorianCalendar</code>.
     * @see #getFractionalSecond()
     * @see #getMillisecond()
     * @see #setTime(int, int, int)
     */
    public abstract int getSecond();

    /**
     * <p>Return millisecond precision of {@link #getFractionalSecond()}.</p>
     *
     * <p>This method represents a convenience accessor to infinite
     * precision fractional second value returned by
     * {@link #getFractionalSecond()}. The returned value is the rounded
     * down to milliseconds value of
     * {@link #getFractionalSecond()}. When {@link #getFractionalSecond()}
     * returns <code>null</code>, this method must return
     * {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     *
     * <p>Value constraints for this value are summarized in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.</p>
     * @return Millisecond  of this <code>XMLGregorianCalendar</code>.
     * @see #getFractionalSecond()
     * @see #setTime(int, int, int)
     */
    public int getMillisecond() {

        BigDecimal fractionalSeconds = getFractionalSecond();

        // is field undefined?
        if (fractionalSeconds == null) {
            return DatatypeConstants.FIELD_UNDEFINED;
        }

        return getFractionalSecond().movePointRight(3).intValue();
    }

    /**
     * <p>Return fractional seconds.</p>
     *
     * <p><code>null</code> is returned when this optional field is not defined.</p>
     *
     * <p>Value constraints are detailed in
     * <a href="#datetimefield-second">second field of date/time field mapping table</a>.</p>
     *
     * <p>This optional field can only have a defined value when the
     * xs:dateTime second field, represented by {@link #getSecond()},
     * does not return {@link DatatypeConstants#FIELD_UNDEFINED}.</p>
     * @return fractional seconds  of this <code>XMLGregorianCalendar</code>.
     * @see #getSecond()
     * @see #setTime(int, int, int, BigDecimal)
     */
    public abstract BigDecimal getFractionalSecond();

    // comparisons

    /**
     * <p>Compare two instances of W3C XML Schema 1.0 date/time datatypes
     * according to partial order relation defined in
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime-order">W3C XML Schema 1.0 Part 2, Section 3.2.7.3,
     * <i>Order relation on dateTime</i></a>.</p>
     *
     * <p><code>xsd:dateTime</code> datatype field mapping to accessors of
     * this class are defined in
     * <a href="#datetimefieldmapping">date/time field mapping table</a>.</p>
     * @param xmlGregorianCalendar Instance of <code>XMLGregorianCalendar</code> to compare
     * @return The relationship between <code>this</code> <code>XMLGregorianCalendar</code> and
     * the specified <code>xmlGregorianCalendar</code> as
     * {@link DatatypeConstants#LESSER},
     * {@link DatatypeConstants#EQUAL},
     * {@link DatatypeConstants#GREATER} or
     * {@link DatatypeConstants#INDETERMINATE}.
     * @throws NullPointerException if <code>xmlGregorianCalendar</code> is null.
     */
    public abstract int compare(XMLGregorianCalendar xmlGregorianCalendar);

    /**
     * <p>Normalize this instance to UTC.</p>
     *
     * <p>2000-03-04T23:00:00+03:00 normalizes to 2000-03-04T20:00:00Z</p>
     * <p>Implements W3C XML Schema Part 2, Section 3.2.7.3 (A).</p>
     * @return <code>this</code> <code>XMLGregorianCalendar</code> normalized to UTC.
     */
    public abstract XMLGregorianCalendar normalize();

    /**
     * <p>Compares this calendar to the specified object. The result is
     * <code>true</code> if and only if the argument is not null and is an
     * <code>XMLGregorianCalendar</code> object that represents the same
     * instant in time as this object.</p>
     * @param obj to compare.
     * @return <code>true</code> when <code>obj</code> is an instance of
     * <code>XMLGregorianCalendar</code> and
     * {@link #compare(XMLGregorianCalendar obj)}
     * returns {@link DatatypeConstants#EQUAL},
     * otherwise <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof XMLGregorianCalendar)) {
            return false;
        }
        return compare((XMLGregorianCalendar) obj) == DatatypeConstants.EQUAL;
    }

    /**
     * <p>Returns a hash code consistent with the definition of the equals method.</p>
     * @return hash code of this object.
     */
    public int hashCode() {

        // Following two dates compare to EQUALS since in different timezones.
        // 2000-01-15T12:00:00-05:00 == 2000-01-15T13:00:00-04:00
        //
        // Must ensure both instances generate same hashcode by normalizing
        // this to UTC timezone.
        int timezone = getTimezone();
        if (timezone == DatatypeConstants.FIELD_UNDEFINED) {
            timezone = 0;
        }
        XMLGregorianCalendar gc = this;
        if (timezone != 0) {
            gc = this.normalize();
        }
        return gc.getYear()
                + gc.getMonth()
                + gc.getDay()
                + gc.getHour()
                + gc.getMinute()
                + gc.getSecond();
    }

    /**
     * <p>Return the lexical representation of <code>this</code> instance.
     * The format is specified in
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime-order">XML Schema 1.0 Part 2, Section 3.2.[7-14].1,
     * <i>Lexical Representation</i>".</a></p>
     *
     * <p>Specific target lexical representation format is determined by
     * {@link #getXMLSchemaType()}.</p>
     * @return XML, as <code>String</code>, representation of this <code>XMLGregorianCalendar</code>
     * @throws IllegalStateException if the combination of set fields
     * does not match one of the eight defined XML Schema builtin date/time datatypes.
     */
    public abstract String toXMLFormat();

    /**
     * <p>Return the name of the XML Schema date/time type that this instance
     * maps to. Type is computed based on fields that are set.</p>
     *
     * <table border="2" rules="all" cellpadding="2">
     * <thead>
     * <tr>
     * <th align="center" colspan="7">
     * Required fields for XML Schema 1.0 Date/Time Datatypes.<br/>
     * <i>(timezone is optional for all date/time datatypes)</i>
     * </th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>Datatype</td>
     * <td>year</td>
     * <td>month</td>
     * <td>day</td>
     * <td>hour</td>
     * <td>minute</td>
     * <td>second</td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#DATETIME}</td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#DATE}</td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#TIME}</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td>X</td>
     * <td>X</td>
     * <td>X</td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#GYEARMONTH}</td>
     * <td>X</td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#GMONTHDAY}</td>
     * <td></td>
     * <td>X</td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#GYEAR}</td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#GMONTH}</td>
     * <td></td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@link DatatypeConstants#GDAY}</td>
     * <td></td>
     * <td></td>
     * <td>X</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * </tbody>
     * </table>
     * @return One of the following class constants:
     * {@link DatatypeConstants#DATETIME},
     * {@link DatatypeConstants#TIME},
     * {@link DatatypeConstants#DATE},
     * {@link DatatypeConstants#GYEARMONTH},
     * {@link DatatypeConstants#GMONTHDAY},
     * {@link DatatypeConstants#GYEAR},
     * {@link DatatypeConstants#GMONTH} or
     * {@link DatatypeConstants#GDAY}.
     * @throws java.lang.IllegalStateException if the combination of set fields
     * does not match one of the eight defined XML Schema builtin
     * date/time datatypes.
     */
    public abstract QName getXMLSchemaType();

    /**
     * <p>Returns a <code>String</code> representation of this <code>XMLGregorianCalendar</code> <code>Object</code>.</p>
     *
     * <p>The result is a lexical representation generated by {@link #toXMLFormat()}.</p>
     * @return A non-<code>null</code> valid <code>String</code> representation of this <code>XMLGregorianCalendar</code>.
     * @throws IllegalStateException if the combination of set fields
     * does not match one of the eight defined XML Schema builtin date/time datatypes.
     * @see #toXMLFormat()
     */
    public String toString() {

        return toXMLFormat();
    }

    /**
     * Validate instance by <code>getXMLSchemaType()</code> constraints.
     * @return true if data values are valid.
     */
    public abstract boolean isValid();

    /**
     * <p>Add <code>duration</code> to this instance.</p>
     *
     * <p>The computation is specified in
     * <a href="http://www.w3.org/TR/xmlschema-2/#adding-durations-to-dateTimes">XML Schema 1.0 Part 2, Appendix E,
     * <i>Adding durations to dateTimes</i>></a>.
     * <a href="#datetimefieldmapping">date/time field mapping table</a>
     * defines the mapping from XML Schema 1.0 <code>dateTime</code> fields
     * to this class' representation of those fields.</p>
     * @param duration Duration to add to this <code>XMLGregorianCalendar</code>.
     * @throws NullPointerException when <code>duration</code> parameter is <code>null</code>.
     */
    public abstract void add(Duration duration);

    /**
     * <p>Creates and returns a copy of this object.</p>
     * @return copy of this <code>Object</code>
     */
    public abstract Object clone();
}
