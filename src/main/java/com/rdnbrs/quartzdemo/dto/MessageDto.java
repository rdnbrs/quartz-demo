package com.rdnbrs.quartzdemo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDto {

    private static final long serialVersionUID = 4321761252954619538L;

    private boolean valid;
    private String msg;
    private Object data;

    public MessageDto(boolean valid, String msg) {
        super();
        this.valid = valid;
        this.msg = msg;
    }

    public MessageDto(boolean valid) {
        super();
        this.valid = valid;
    }

    public static MessageDto failure(String msg) {
        return new MessageDto(false, msg);
    }

    public static MessageDto failure(Exception e) {
        return new MessageDto(false, e.getMessage());
    }

    public static MessageDto failure() {
        return new MessageDto(false);
    }

    public static MessageDto success() {
        return new MessageDto(true);
    }

    public static MessageDto success(String msg) {
        return new MessageDto(true, msg);
    }

    public void setData(Object data) {
        this.valid = true;
        this.data = data;
    }

}
