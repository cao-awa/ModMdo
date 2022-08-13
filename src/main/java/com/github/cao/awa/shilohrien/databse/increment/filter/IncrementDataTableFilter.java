package com.github.cao.awa.shilohrien.databse.increment.filter;

import com.github.cao.awa.shilohrien.databse.*;
import com.github.cao.awa.shilohrien.databse.increment.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.*;

import java.util.*;
import java.util.function.*;

public record IncrementDataTableFilter<T>(IncrementDataTable<T> table) {
    /**
     * <p>Manual filter switcher </p>
     * <p>Switching filter by user </p>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param predicate
     *         the conditions of filtering
     * @param parallel
     *         switch parallel
     * @return filter results
     */
    public Collection<DataBody> filterRow(String rowName, Predicate<Object> predicate, boolean parallel) {
        if (parallel) {
            return filterRow2(rowName, predicate);
        }
        return filterRow1(rowName, predicate);
    }

    /**
     * <p>Filter bodies using paralleled stream </p>
     * <p>Will speed up most in a huge data table </p>
     * <p>But will hinder filter when data is not much </p>
     * <br>
     * </p>Recommends switch this filter if amount higher than 350000 </p> <br>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param predicate
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow2(String rowName, Predicate<Object> predicate) {
        int index = table.getHead().getIndex(rowName);
        return table.values().parallelStream().filter(body -> predicate.test(body.get(index))).toList();
    }

    /**
     * <p>Filter bodies using not paralleled stream </p>
     * <p>Will not hinder when data is not much</p>
     * <p>But get a long lag in any huge data tables(≥600000)</p>
     * <br>
     * </p>Recommends switch to this filter if amount lower than 350000 </p> <br>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param predicate
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow1(String rowName, Predicate<Object> predicate) {
        int index = table.getHead().getIndex(rowName);
        return table.values().stream().filter(body -> predicate.test(body.get(index))).toList();
    }

    /**
     * <p>Manual filter switcher </p>
     * <p>Switching filter by user </p>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param requirements
     *         the conditions of filtering
     * @param parallel
     *         switch parallel
     * @return filter results
     */
    public Collection<DataBody> filterRow(String rowName, DataRequirements requirements, boolean parallel) {
        if (parallel) {
            return filterRow2(rowName, requirements);
        }
        return filterRow1(rowName, requirements);
    }

    /**
     * <p>Filter bodies using paralleled stream </p>
     * <p>Will speed up most in a huge data table </p>
     * <p>But will hinder filter when data is not much </p>
     * <br>
     * </p>Recommends switch this filter if amount higher than 350000 </p> <br>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param requirements
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow2(String rowName, DataRequirements requirements) {
        int index = table.getHead().getIndex(rowName);
        return table.values().parallelStream().parallel().filter(body -> requirements.satisfy(body.get(index))).toList();
    }

    /**
     * <p>Filter bodies using not paralleled stream </p>
     * <p>Will not hinder when data is not much</p>
     * <p>But get a long lag in any huge data tables(≥600000)</p>
     * <br>
     * </p>Recommends switch to this filter if amount lower than 350000 </p> <br>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param requirements
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow1(String rowName, DataRequirements requirements) {
        int index = table.getHead().getIndex(rowName);
        return table.values().stream().filter(body -> requirements.satisfy(body.get(index))).toList();
    }

    /**
     * <p>Auto filter switcher </p>
     * <p>Using recommends to switch filter </p>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param requirements
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow(String rowName, DataRequirements requirements) {
        if (table.size() > 350000) {
            return filterRow2(rowName, requirements);
        } else {
            return filterRow1(rowName, requirements);
        }
    }

    /**
     * <p>Auto filter switcher </p>
     * <p>Using recommends to switch filter </p>
     * <br>
     *
     * @param rowName
     *         the name of body row
     * @param predicate
     *         the conditions of filtering
     * @return filter results
     */
    public Collection<DataBody> filterRow(String rowName, Predicate<Object> predicate) {
        if (table.size() > 350000) {
            return filterRow2(rowName, predicate);
        } else {
            return filterRow1(rowName, predicate);
        }
    }
}
