package io.github.invvk.wgef.abstraction.flags.data;

public class BooleanVal {

    private int val = -1;

    private final boolean defaultVal;

    public BooleanVal(boolean defaultVal) {
        this.defaultVal = defaultVal;
        this.setVal(defaultVal);
    }

    public boolean hasInitialised() {
        return val != -1;
    }

    public void setVal(boolean val) {
        this.val = val ? 1 : 0;
    }

    public boolean getState() {
        return val != -1 && val != 0;
    }

    public boolean isFalse() {
        return !getState();
    }

    public boolean isTrue() {
        return getState();
    }

    public boolean getDefault() {
        return this.defaultVal;
    }

    public void reset() {
        this.val = -1;
    }

}
