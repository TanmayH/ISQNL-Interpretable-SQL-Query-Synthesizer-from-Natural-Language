package com;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;

public class Connection
{
    public HashMap<String,ArrayList<String>> tables = new HashMap<>();
    public Set<String> tabls;
    public ArrayList<String> temp= new ArrayList<>();
    public HashMap<String,String> revtables= new HashMap<>();
    public  ArrayList<String> tablenameslist = new ArrayList<>();
    public  ArrayList<String> columnnameslist= new ArrayList<>();
    public HashMap<String,String> revcolumns = new HashMap<>();

    public void   Connection() throws Exception{
        readTables();
    }

   public  void  Connection(ArrayList<String> tables,HashMap<String,String>revtables)throws Exception
    {
        HashSet<String> temp=new HashSet<>();
        temp.addAll(tables);
        ArrayList<String> temptables= new ArrayList<>();
        temptables.addAll(tables);


    }

    public void readTables() throws  Exception
    {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("resources/tables.json"));
        JSONObject jsonObject = (JSONObject)object;
        tabls=jsonObject.keySet();
        String tables;
        Iterator<String > itr = tabls.iterator();
        while (itr.hasNext())
        {
            tables=itr.next();
            temp=(ArrayList)jsonObject.get(tables);
            tablenameslist.addAll(temp);
            for (String tabs:temp)
            {
                revtables.put(tabs,tables);
            }

        }

        
        


    }

    public void readColumns(ArrayList<String> tableslist,HashMap<String,String> revtables,ArrayList<String> actualtableslist) throws  Exception
    {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("resources/columns.json"));
        JSONObject jsonObject = (JSONObject) object;
      ArrayList<String> tmp=new ArrayList<>();
        String columns;
        HashSet<String> preprocess = new HashSet<>();

        for (String tok : actualtableslist)
        {

            preprocess.add(tok);

        }
        
 for (String tablename : preprocess)
        {

            try {

                

                tablename=tablename.trim();

                JSONObject js= (JSONObject) jsonObject.get(tablename);

                
                js.keySet().remove(null);
                js.keySet().remove(null);
                tmp.clear();
                tmp.addAll(js.keySet());

                Iterator<String> itr = tmp.iterator();
                while (itr.hasNext())
                {
                    columns = itr.next();
                    columns=columns.trim();
                    temp.clear();

                    temp.addAll((ArrayList) js.get(columns));

                    columnnameslist.addAll(temp);

                    for (String temps : temp)
                    {
                        revcolumns.put(temps, columns);
                    }
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
                continue;
            }


        }

    }


}