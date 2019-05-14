package mxstar.symbol;

public class StArrayType extends StType {
    public StType baseType;

    public StArrayType() {}
    public StArrayType(StType baseType) {
        this.baseType = baseType;
    }

    @Override
    public boolean match(StType oth) {
        if (oth instanceof StClassType && (((StClassType) oth).name.equals("null"))) {
            return true;
        } else if (oth instanceof StArrayType && baseType.match(((StArrayType) oth).baseType)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getByte() {
        return 8;
    }
}
