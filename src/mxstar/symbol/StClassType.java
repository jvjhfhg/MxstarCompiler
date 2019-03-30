package mxstar.symbol;

public class StClassType extends StType {
    public String name;
    public StClassSymbol symbol;

    public StClassType() {}
    public StClassType(String name, StClassSymbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public boolean match(StType oth) {
        if (oth instanceof StClassType) {
            String othName = ((StClassType) oth).name;
            if ((othName.equals("null") && name.equals("string")) || (othName.equals("string") && name.equals("null"))) {
                return false;
            } else {
                return othName.equals("null") || name.equals("null") || name.equals(othName);
            }
        } else {
            return false;
        }
    }
}
