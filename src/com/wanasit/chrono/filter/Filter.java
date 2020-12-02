package com.wanasit.chrono.filter;

import com.wanasit.chrono.ChronoOption;
import com.wanasit.chrono.ParsedResult;
import com.wanasit.chrono.refiner.RefinerAbstract;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter extends RefinerAbstract {
    
    public abstract boolean isValid(String text, ChronoOption options, ParsedResult result);
    
    @Override
    public List<ParsedResult> refine(List<ParsedResult> results, String text, ChronoOption options) {
        
        List<ParsedResult> filteredResults = new ArrayList<ParsedResult>();
        for (ParsedResult result : results)
        {
            if (isValid(text, options, result)) filteredResults.add(result);
        }
        
        return filteredResults;
    }
    
}
