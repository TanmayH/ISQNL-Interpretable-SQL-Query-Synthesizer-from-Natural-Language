package com;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class date_column_extractor
{
    /*

    Extract the synonym values for a key from JSON

     */

    public date_column_extractor()
    {

    }

    public  date_column_extractor(String table)
    {
        try {
            find_date(table);


        } catch (FileNotFoundException e)

        {
            e.printStackTrace();
        }
    }

    public static int ordinalIndexOf(String str, String substr, int n)
    {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public ArrayList<String> find_date(String c1) throws FileNotFoundException
    {


            HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
            ArrayList<String> arrayList = new ArrayList<>();
            HashSet<String>  datecolumns=new HashSet<>();


            try{
            Scanner sc = new Scanner(new File("resources/date.json")).useDelimiter("\n");


            while (sc.hasNextLine())
            {
                String s = sc.nextLine();


                if (s.contains("[") && s.contains("]"))//mapping exist
                {

                    String column = s.substring(s.indexOf("\"") + 1, ordinalIndexOf(s, "\"", 2));



                    if (column.contains(c1.trim()))
                    {

                        //now retrieving the synonms
                        String synonyms = s.substring(ordinalIndexOf(s, "\"", 3), s.indexOf("]"));//till end of line


                        //cleaning all these inputs
                        for (String s1 : synonyms.split("\""))
                        {
                            if (!s1.equals(","))
                            {

                                arrayList.add(s1);
                                String[] splitdatecolumns=s1.split(" ");
                                for(String str:splitdatecolumns)
                                {
                                    if(!str.matches(""))
                                     datecolumns.add(str.trim());
                                }



                            }

                        }
                        arrayList.clear();

                        hashMap.put(column, new ArrayList<String> (arrayList));

                    } else continue;
                }

                arrayList.clear();
            }

            //grab data from the hashmap and display. Now hashmap contains only relevant columns
            for (String s : hashMap.keySet())
            {

                String a = hashMap.get(s).toString();


                arrayList.add(a);
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        arrayList.clear();
        arrayList.addAll(datecolumns);
        return arrayList;
    }

   public static void main(String[] args) throws FileNotFoundException
    {
        date_column_extractor obj=new date_column_extractor();
        
        
    }
}


