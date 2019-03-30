package mxstar.symbol;

public class StPrimitiveType extends StType {
    public String name;
    public StPrimitiveSymbol symbol;

    public StPrimitiveType() {}
    public StPrimitiveType(String name, StPrimitiveSymbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public boolean match(StType oth) {
        if (oth instanceof StPrimitiveType && ((StPrimitiveType) oth).name.equals(name)) {
            return true;
        } else {
            return false;
        }
    }
}
