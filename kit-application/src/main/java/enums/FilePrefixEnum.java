package enums;

import java.util.Base64;
import java.util.Base64.Decoder;

public enum FilePrefixEnum {

    CF("RkMy", "RkMyLVBQVi0=");

    private final String name;
    private final String code;

    private final Decoder decoder = Base64.getDecoder();

    public String getName() {
        return new String(decoder.decode(this.name));
    }
    public String getCode() {
        return new String(decoder.decode(this.code));
    }

    FilePrefixEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
