package Project1_FLP.Enum;

import java.util.Random;

public enum Station_Type {
    TypeA(1),
    TypeB(2),
    TypeC(3),
    TypeD(4);


    private final int value;

    private Station_Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Station_Type getStation_Type(int value) {
        return switch (value) {
            case 1 -> TypeA;
            case 2 -> TypeB;
            case 3 -> TypeC;
            case 4 -> TypeD;
            default -> null;
        };

    }

    public static int getStationNumber() {
        return Station_Type.values().length;
    }


}
