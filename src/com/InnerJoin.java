package com;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;


/*
This Class represents the tables as the vertices and draws up all transitive relations possible using warshalls algorithm

The edges are represented by the relations from the fkmappings.json

INPUT: Set of tables whose relations are defined in the json file

OUTPUT: Best possible On clause possible for the set of tables. Also returns the set of tables which were joined successfully and remaining

un-joined tables

*/


public class InnerJoin
{


    Boolean isJoinable= false;
    public ArrayList<String> jointables=new ArrayList<>();
    int n;
    String [][] adj;


    HashMap<String,Integer> indices= new HashMap<>();
    HashMap<String,String> onclausemapper = new HashMap<>();
    HashMap<Integer,String> backindices = new HashMap<>();
    HashSet<String> edgeset= new HashSet<>();
    public String finalstring="";

    public String secondpass(String child){
        String base= " INNER JOIN ";
        if (child==null)
            return "";
        String [] toks= child.split(",");
        String result="";
        int i;
        ArrayList<String>newtoks= new ArrayList<>();
        for (i=0;i<toks.length;i++){
            if (toks[i]==null||toks[i].length()==0)
                continue;
            newtoks.add(toks[i]);
        }


        for (i=0;i<newtoks.size();i+=2){

            String table= newtoks.get(i);
            String on= newtoks.get(i+1);
            if (!edgeset.contains(table)){
                edgeset.add(table);

                result+=base+" "+table+on;
                jointables.add(table);
            }
        }
        return result;



    }
    public String getOn(String onmap) throws  Exception{
        JSONParser parser = new JSONParser();
        Object object = parser.parse(onmap);
        JSONObject js = (JSONObject)object;
        return (String)js.get("on") ;
    }
    public ArrayList<String> addChildren(String map) throws  Exception{
        JSONParser parser = new JSONParser();
        Object object = parser.parse(map);
        JSONObject js = (JSONObject)object;
        Set<String> children =js.keySet();
        ArrayList<String> child= new ArrayList<>();

        child.addAll(children);
        for (String on:child) {
            onclausemapper.put(on,js.get(on).toString());

        }
        return child;

    }
    public Boolean getTransition(String transition)
    {
        if (transition==null)
            return false;
        return true;
    }

    public void converttoadj() throws Exception{
        JSONParser parser = new JSONParser();
        try {
            Object object= parser.parse(new FileReader("resources/fkmappingsnew.json"));
            JSONObject object1= (JSONObject) object;
            Set<String> nodes = object1.keySet();
            int n= nodes.size();
            this.n=n;
            int i=0;

            for (String node :nodes) {
                indices.put(node,i++);
                backindices.put(i,node);
            }
            i=0;

            String[][] adjacency= new String[n][n];
            int j=0;
            for (String node:nodes) {
                Map adj = (Map)object1.get(node);

                ArrayList<String> temp =addChildren(adj.toString());
                for (String child:temp){
                    if (child==null)
                        continue;
                    String temps=getOn(onclausemapper.get(child));
                    adjacency[indices.get(node)][indices.get(child)]=child+" , "+temps;
                    adjacency[indices.get(child)][indices.get(node)]=node+" , "+temps;
                }

            }


            adj=adjacency;


        }
        catch (Exception e){
            
            e.printStackTrace();
        }

    }

    public  void  warshalls(){
        int i,j,k;
        for (k=0;k<n;k++){
            for (i=0;i<n;i++){
                for (j=0;j<n;j++)
                {
                    String curr= adj[i][j];
                    if (getTransition(curr))
                        continue;
                    String row=adj[i][k];
                    String col= adj[k][j];
                    if (getTransition(col) && getTransition(row))
                    {
                        adj[i][j]= row+","+col+",";
                    }
                }
            }
        }


    }

    public void parse(ArrayList<String> tablenames)
    {
        if (tablenames.size()>=2)
            this.isJoinable=true;
        if (tablenames.size()<=1)
            return;

        String maintable=tablenames.get(0);

        int primary=indices.get(maintable);
        int i;
        String result="";
        for (i=1;i<tablenames.size();i++)
        {
            String childtable=tablenames.get(i);
            String temp= adj[primary][indices.get(childtable)];
            result=result+secondpass(temp);
        }

         jointables.add(maintable);
        this.finalstring=" FROM "+maintable+ result;

    }



    public InnerJoin(ArrayList<String> tables)
    {
        finalstring="";

        try
        {

            converttoadj();
        }
        catch (Exception e)
        {

            e.printStackTrace();
            

        }
        warshalls();
        parse(tables);

    }


   /* public static void main(String[] args) throws Exception
    {
        ArrayList<String> arr= new ArrayList<>();
        arr.add("t_ggrphy");
        arr.add("t_cstmrs");
        arr.add("t_prds");
        InnerJoin in=new InnerJoin(arr);


       
       

    } */

}