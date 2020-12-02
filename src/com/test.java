package com;
import java.util.ArrayList;

public class test
{
    public static void main(String[] args)
    {
        getsynonym ob = new getsynonym();
        ArrayList<String> c1=new ArrayList<>();
        ArrayList<String> synlist= new ArrayList<>();
        ArrayList<String> sampletables=new ArrayList<>();
        ArrayList<String> tables=new ArrayList<>();

        //Sample list of tablenames
        sampletables.add("customer");
        sampletables.add("geography");
        sampletables.add("crimson");
        sampletables.add("number");
        sampletables.add("Shops");

        //Sample list of nouns
        c1.add("client");
        c1.add("bullshit");
        c1.add("bullshit2");
        c1.add("location");
        c1.add("red");
        c1.add("bullshit3");
        c1.add("bullshit4");
        c1.add("number");
        c1.add("bullshit5");

        for (String a:c1)
        {
            synlist=ob.generate(a);
            
            tables.addAll(simplechecker(synlist,sampletables));
        }

        
        
        
        //ob.get("resources//tabledata.txt");
    }

    public static ArrayList<String> simplechecker(ArrayList<String> nounsslist,ArrayList<String> samplenameslist)
    {
        int x[]=new int[100];
        int k=0;

        ArrayList<String> simplecheckdata=new ArrayList<>();
        simplecheckdata.clear();
        simplecheckdata.addAll(samplenameslist);
        //
        simplecheckdata.retainAll(nounsslist);
        //
        nounsslist.removeAll(simplecheckdata);
        return simplecheckdata;
    }
}

