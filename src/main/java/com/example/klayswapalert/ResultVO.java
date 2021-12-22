package com.example.klayswapalert;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultVO {
    public String success;
    public String code;
    public List<ResultSubVO> result;
    public String page;
    public String total;
    public String limit;
}
