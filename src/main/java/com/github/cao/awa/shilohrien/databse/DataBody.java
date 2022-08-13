package com.github.cao.awa.shilohrien.databse;

import com.github.cao.awa.shilohrien.databse.increment.requirement.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.exception.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.builder.*;
import it.unimi.dsi.fastutil.objects.*;

public final class DataBody implements Cloneable {
    private boolean hasRequirements = false;
    private Object2ObjectArrayMap<Integer, DataRequirements> requirements = new Object2ObjectArrayMap<>();
    private Object2ObjectLinkedOpenHashMap<Integer, Object> map = new Object2ObjectLinkedOpenHashMap<>();
    private int mainKey = 0;

    public DataBody() {

    }

    public DataBody(Object2ObjectLinkedOpenHashMap<Integer, Object> map) {
        this.map = map;
    }

    public static DataBody body() {
        return new DataBody();
    }

    /**
     * <p>Insert an element to body end </p>
     * <br>
     *
     * @param value the value
     * @return this
     */
    public DataBody add(Object value) {
        this.map.put(++ mainKey, value);
        return this;
    }

    /**
     * <p>Update an element</p>
     * <p>If not key present, will create a NULL_BODY and update</p>
     * <br>
     *
     * @param key the key
     * @param value update value
     * @return this
     */
    public DataBody update(Integer key, Object value) {
        if (this.map.containsKey(key)) {
            this.map.replace(key, value);
        } else {
            this.map.put(key, value);
        }
        return this;
    }

    /**
     * <p>Modify an element, need key present</p>
     * <p>If key is not preset, will fail</p>
     * <br>
     *
     * @param key the key
     * @param value modify value
     * @return this
     */
    public DataBody modify(Integer key, Object value) {
        this.map.replace(key, value);
        return this;
    }

    /**
     * <p>Insert an element to body end </p>
     * <br>
     *
     * @param value the value
     * @param requirements requirements of this value
     * @return this
     */
    public DataBody add(Object value, DataRequirements requirements) {
        this.map.put(++ mainKey, value);
        if (requirements != null) {
            this.hasRequirements = true;
            this.requirements.put(mainKey, requirements);
        }
        return this;
    }

    /**
     * <p>Insert an element to body end </p>
     * <br>
     *
     * @param value the value
     * @param requirement requirement of this value
     * @return this
     */
    public DataBody add(Object value, DataRequirement requirement) {
        this.map.put(++ mainKey, value);
        if (requirement != null) {
            this.hasRequirements = true;
            this.requirements.put(mainKey, new DataRequirements().add(requirement));
        }
        return this;
    }

    /**
     * <p>Let all elements be require target requirement</p>
     * <br>
     *
     * @param requirement requirement of all elements
     * @return this
     */
    public DataBody allRequire(DataRequirement requirement) {
        hasRequirements = true;
        for (Integer index : this.map.keySet()) {
            DataRequirements requirements = this.requirements.get(index);
            if (requirements == null) {
                this.requirements.put(index, new DataRequirements());
                requirements = this.requirements.get(index);
            }
            requirements.add(requirement);
        }
        return this;
    }

    /**
     * <p>Get elements</p>
     * <br>
     *
     * @return value collection
     */
    public ObjectCollection<Object> elements() {
        return map.values();
    }

    /**
     * <p>Get keys</p>
     * <br>
     *
     * @return key collection
     */
    public ObjectCollection<Integer> indexes() {
        return map.keySet();
    }

    /**
     * <p>Replace requirements</p>
     * <br>
     *
     * @param requirements requirements
     * @return this
     */
    public DataBody setRequirements(Object2ObjectOpenHashMap<Integer, DataRequirements> requirements) {
        this.requirements.clear();
        this.requirements.putAll(requirements);
        return this;
    }

    /**
     * <p>Check target body is satisfy requirement of this body</p>
     * <br>
     * @param body target
     */
    public void check(DataBody body) {
        if (body.map.size() > this.map.size()) {
            throw new DataNotSatisfyException("Excess data start from index " + (this.map.size() + 1));
        }
        if (this.hasRequirements) {
            for (Integer t : this.map.keySet()) {
                DataRequirements requirements = this.requirements.get(t);
                if (requirements != null) {
                    ObjectArrayList<String> reasons = requirements.ensureSatisfy(body.get(t));
                    if (! (reasons.size() == 0)) {
                        throw new DataNotSatisfyException("The data(index " + t + ") are not satisfy requirements: " + reasons);
                    }
                }
            }
        }
    }

    /**
     * <p>Get element use index</p>
     * <br>
     *
     * @param index index
     * @return element
     */
    public Object get(int index) {
        return this.map.get(index);
    }

    /**
     * <p>Check self is satisfy requirements</p>
     * <br>
     */
    public void check() {
        if (this.hasRequirements) {
            for (Integer t : this.map.keySet()) {
                DataRequirements requirements = this.requirements.get(t);
                if (requirements != null) {
                    ObjectArrayList<String> reasons = requirements.ensureSatisfy(get(t));
                    if (! (reasons.size() == 0)) {
                        throw new DataNotSatisfyException("The data(index " + t + ") are not satisfy requirements: " + reasons);
                    }
                }
            }
        }
    }

    /**
     * <p>Find index of element</p>
     * <br>
     *
     * @param element target
     * @return index
     */
    public int getIndex(Object element) {
        return this.map.values().stream().toList().indexOf(element) + 1;
    }

    /**
     * <p>Size of self map</p>
     * <br>
     *
     * @return size
     */
    public int size() {
        return this.map.size();
    }

    public DataBody clone() throws CloneNotSupportedException {
        DataBody body = (DataBody) super.clone();
        body.map = map.clone();
        body.requirements = requirements.clone();
        return body;
    }

    public String toString() {
        StringBuilderConductor builder = new StringBuilderConductor();
        builder.append("[");
        for (Object s : map.values()) {
            builder.append(s).append(", ");
        }
        builder.deleteLast(2);
        builder.append("]");
        return builder.toString();
    }
}
