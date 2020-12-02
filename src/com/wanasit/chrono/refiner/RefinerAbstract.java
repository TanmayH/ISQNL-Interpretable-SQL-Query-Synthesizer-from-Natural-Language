package com.wanasit.chrono.refiner;

import com.wanasit.chrono.ChronoOption;
import com.wanasit.chrono.ParsedResult;

import java.util.List;

public abstract class RefinerAbstract implements Refiner {
    
    public abstract List<ParsedResult> refine(List<ParsedResult> results, String text, ChronoOption options);
}
