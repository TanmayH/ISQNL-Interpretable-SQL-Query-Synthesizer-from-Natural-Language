package com;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.*;

public class MappingParser
{
    public ArrayList<String> tables;
    public Set tablesslist;
    public  ArrayList<String> remaining= new ArrayList<>();
    public ArrayList<String> attached = new ArrayList<>();//contains actual wordss
    public String res="";
    public ArrayList<String> actualremaining= new ArrayList<>();//contains remaining logic words
    public boolean isJoinable;
    boolean primary=true;
    public  String finalstring;


    public MappingParser(HashMap<String,String> forw) throws  Exception

    {
        remaining.clear();
        actualremaining.clear();
        res="";
       // tablesslist.clear();

        finalstring="";
        
        parser(forw);

        joinable();
        remaining.removeAll(attached);

        for (String remtables:remaining) {
            actualremaining.add((String)forw.get(remtables));
        }

    }

    public void parser(HashMap<String,String> map) throws  Exception{
//        JSONParser parser = new JSONParser();
//        Object object = parser.parse(new FileReader("mappingparser.json"));
        ArrayList<String> temp= new ArrayList<>();
//        JSONObject jsonObject = (JSONObject)object;
//        for (String table:temptables) {
//            temp.add((String) jsonObject.get(table));
//        }
        temp.addAll(map.keySet());
        tables=temp;
        
        remaining.addAll(tables);
    }

    public String OnClauseMapper(String map,String tablename) throws Exception{
        JSONParser parser = new JSONParser();
        String temp="";
        
        //parse sub json map
        Object object= parser.parse(map);
        JSONObject js=(JSONObject)object;
        Set columnsmapped=js.keySet();
        //columnsmapped contains tablelist that is mapped to tablename

        ArrayList<String> temptables=new ArrayList<>();
        temptables.addAll(tables);
        //now intersect the initial tables detected and the columnsmapped
        temptables.retainAll(columnsmapped);
        if (temptables.size()==0)
            return "";//if nothing then we can't join!!


        Iterator<String> itr=temptables.iterator();
        if(primary){
            attached.add(tablename);
            temp=temp+tablename;
            primary=false;

        }
        
        while (itr.hasNext()){

            String mappedtable=itr.next();

            if (!attached.contains(mappedtable)) {
                Object obj1 = parser.parse(js.get(mappedtable).toString());
                JSONObject jsp = (JSONObject) obj1;
                String fromid = (String) jsp.get("from");
                String toid = (String) jsp.get("to");
                attached.add(mappedtable);
                temp += " INNER JOIN " + mappedtable + " ON " + tablename + "." + fromid + "=" + mappedtable + "." + toid + " ";
            }
        }



        return temp;
    }

    //detect is joinable

    private void joinable()  {


        //parse json

        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("resources/fkmappings.json"));
            JSONObject jsonObject = (JSONObject)object;
            tablesslist=jsonObject.keySet();//get the primary tables

            

            tablesslist.retainAll(tables);//retain only the tables detected from earlier clause

            if(tablesslist.size()==0)
            {this.finalstring="";
                return;}
            //if intersection is 0 throw exception


            Iterator<String> itr = tablesslist.iterator();
            //iterate on intersected primary tables

            while (itr.hasNext()){
                String tablename=itr.next();
                Map tableinfo=(Map) jsonObject.get(tablename);

                //tableinfo -selected data for a table

                
                

                String temp=OnClauseMapper(tableinfo.toString(),tablename);
                if(temp=="")
                    continue;
                else {
                    res += temp;

                }

            }

            this.finalstring=res;
            

            // typecasting obj to JSONObject





        }




        catch (Exception e) {
            e.printStackTrace();
        }
    }




    //create a recursion and
    public static void main(String[] args) throws Exception{
        ArrayList<String> tables= new ArrayList<>();
        HashMap<String,String> hash= new HashMap<>();
        hash.put("t_cstmrs","customers");
        hash.put("t_ggrphy","geography");
        hash.put("t_prdcat","product");

        tables.add("customers");
        tables.add("geography");
        tables.add("product");

        MappingParser s= new MappingParser(hash);

        
    }


}