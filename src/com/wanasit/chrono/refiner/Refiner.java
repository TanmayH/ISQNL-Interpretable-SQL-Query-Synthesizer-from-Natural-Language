package com.wanasit.chrono.refiner;

import com.wanasit.chrono.ChronoOption;
import com.wanasit.chrono.ParsedResult;

import java.util.List;

public interface Refiner {
    
    List<ParsedResult> refine(List<ParsedResult> results, String text, ChronoOption options);
}
