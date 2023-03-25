package com.javawhizz.App.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OwnersPojo {
    private int aPurse;
    private Map<String,Integer> allrounder;
    private Map<String,Integer> batsman;
    private Map<String,Integer> bowler;
    private Map<String,Integer> wicketkeeper;

}
