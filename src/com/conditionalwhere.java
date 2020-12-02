package com;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class conditionalwhere
{
   public  String finalstring="";
    ArrayList<String> columnslist=new ArrayList<String>();
    ArrayList<String> datalist=new ArrayList<String>();
    String sentence="";
    HashMap<String,ArrayList<String>> hypernymmapped=new HashMap<>();
    List<String> greater =new ArrayList<>();
    List<String> less=new ArrayList<>();
    List<String> equals=new ArrayList<>();
    List<String> dataprocesscompletelist=new ArrayList<>();
    public     HashSet<String> columnsprocessed=new HashSet<>();
    HashMap<String,String> revcol;


    public  boolean isQuotes(String column) throws  Exception
    {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("resources/quotes.json"));
        JSONObject cols = (JSONObject) object;


        String columns = (String) cols.get(revcol.get(column));


        /*columns = null can be the primary reason */
        if(columns!=null)
        if (columns.equalsIgnoreCase("true"))
            return true;
        else
            return false;

        return  true;

    }
     public String nearestcolumforequalcasestring(String s) throws Exception
     {


         String columnname="";
         String returnstring="";
         boolean flag=false;
         /*
         observation is that either the column is present on immediate right or nearest column on left
         */
         for(String str:datalist) {
             if (s.contains(str))
             {
                 if (str.matches("")) {
                     continue;
                 }

                 columnname = "";

                 String[] pieces = s.split(" " + str + " ");

                 if (pieces.length >= 2) {
                     int k = 0;
                     String[] brokenpiece = pieces[1].split(" ");

                     if (brokenpiece.length >= 1) {
                         while (brokenpiece[k].matches("") || brokenpiece[k].matches(" ")) {
                             k++;
                         }

                         if (columnslist.contains(brokenpiece[k])) {

                             columnname += brokenpiece[k];

                             if (columnsprocessed.contains(columnname) && !columnname.matches("") && !columnname.matches(" ")) {

                                 returnstring += " OR ";
                             } else {
                                 if (flag && !columnname.matches("") && !columnname.matches(" ")) {

                                     returnstring += " AND ";
                                 }

                             }

                             if (!columnname.matches("") && !str.matches("") && !columnname.matches(" ") && !str.matches(" ")) {
                                 flag = true;

                                 if (isQuotes(columnname)) {
                                     returnstring += " LOWER( " + columnname + " ) = '" + str + "' ";
                                 } else {
                                     returnstring += " " + columnname + " = '" + str + "' ";
                                 }
                                 columnsprocessed.add(columnname);

                             }
                             continue;
                         }
                     }
                 }


                 columnname = "";

                 if (nearestleftcolumn(pieces[0]) != null)
                     columnname += nearestleftcolumn(pieces[0]);


                 boolean localflag = false;
                 if (columnname.contains("NOT")) {
                     localflag = true;
                     columnname = columnname.replaceAll("NOT", "");
                     columnname.replaceAll(" ", "");
                 }


                 if (columnsprocessed.contains(columnname) && !columnname.matches(" ") && !columnname.matches("")) {

                     returnstring += " OR ";
                 } else {


                     if (flag && !columnname.matches("") && !columnname.matches(" ")) {


                         returnstring += " AND ";
                     }

                 }


                 boolean checkflag = false;

                 if (!columnname.matches("") && !str.matches("")) {
                     flag = true;
                     if (localflag) {


                         if (isQuotes(columnname)) {
                             returnstring += "  LOWER( " + columnname + " )  != '" + str + "' ";
                         } else {
                             returnstring += " " + columnname + "  != " + str + " ";
                         }
                         localflag = false;
                     } else {


                         if (isQuotes(columnname)) {
                             returnstring += " LOWER( " + columnname + " ) = '" + str + "' ";
                         } else {
                             returnstring += "  " + columnname + "  = " + str + " ";
                         }


                         columnsprocessed.add(columnname);
                     }
                 }


             }
         }



         return returnstring;
     }

    public String nearestleftcolumn(String s)
    {

        boolean flag=false;
        s=s.replaceAll("   "," ");
        s=s.replaceAll("  "," ");
        String broken[]=s.split(" ");
        int j=broken.length-1;

        while(j>=0)
        {


            if(broken[broken.length-1].equalsIgnoreCase("NOT")||broken[broken.length-1].equalsIgnoreCase("NO"))
            {
                flag=true;
            }

            if(columnslist.contains(broken[j]))
            {
                if(flag)
                {
                    return " NOT "+broken[j];
                }
                else
                {
                    return broken[j];
                }
            }

            j--;

        }
        return  null;
    }


    public String nearestdata(String s)
    {
        s=s.replaceAll("   "," ");
        s=s.replaceAll("  "," ");
        int i=0;
        String[] broken=s.split(" ");
        boolean flag=false;
        int j;
        for(i=0;i<broken.length;i++)
        {

           String data="";

           if(broken[i].matches("'")&&!flag)
            {

                j=i;
                i++;
                while (!(broken[i].matches(  "'" ))&& (i < broken.length))
                {
                    if(i==j+1)
                    {
                        data +=broken[i];
                    }
                    else
                    {
                        data+=" "+broken[i];
                    }

                     i++;
                }
                if (broken[i].matches("'"))
                {

                    if (datalist.contains(data))
                    {
                        dataprocesscompletelist.add(data);
                        return "'"+data+"'";

                    }
                    else
                        {

                            flag = true;
                            i=j;
                        }

                }
            }


            if(datalist.contains(broken[i]))
            {
                data=broken[i];
                dataprocesscompletelist.add(data);
                return data;
            }

        }

        return  null;

    }


    public void load()
    {
        XMLParser ob=new XMLParser();
        ob.input="Greater";
        greater=ob.xmlParser();

        ob.input="Lesser";
        less=ob.xmlParser();

        ob.input="Equals";
        equals=ob.xmlParser();

    }




    public  void wherestatementgenerator(String original)
    {
        boolean flag = false;

        for (String keyword : greater)
        {
            if (original.contains(keyword))
            {
                Pattern p = Pattern.compile("(.*?)" + keyword + "(.*)");
                Matcher m = p.matcher(original);

                while (m.find())
                {
                    String colname="";
                    if(nearestleftcolumn(m.group(1))!=null)
                    colname= nearestleftcolumn(m.group(1));

                    String data = nearestdata(m.group(2));
                    if (flag && (colname != null) && (data != null))
                    {
                        if (!colname.matches("") && !colname.matches(" "))
                        {
                            if (columnsprocessed.contains(colname))
                            {
                                finalstring += " OR ";

                            } else
                                {


                                finalstring += " AND ";
                            }


                        }
                    }
                    if ((colname != null) && (data != null)&&(!colname.matches(""))&&(!data.matches("")))
                    {

                        finalstring += colname + " > " + data ;
                        columnsprocessed.add(colname);
                        flag = true;
                    }
                }
            }

        }

        for (String keyword : less)
        {
            if (original.contains(keyword))
            {
                Pattern p = Pattern.compile("(.*?)" + keyword + "(.*)");
                Matcher m = p.matcher(original);
                while (m.find())
                {
                    String colname="";
                    if(nearestleftcolumn(m.group(1))!=null)
                     colname += nearestleftcolumn(m.group(1));
                     String data = nearestdata(m.group(2));



                    if (flag && (colname != null) && (data != null))
                    {
                        if(!colname.matches("")&&!colname.matches(""))
                        {
                            if (columnsprocessed.contains(colname)) {
                                finalstring += " OR ";
                            } else {

                                finalstring += " AND ";
                            }
                        }

                    }
                    if (colname != null && data != null&&!colname.matches("")&&!data.matches("")&&!colname.matches(" ")&&!data.matches(" "))
                    {

                        finalstring += colname + " < " + data;
                        flag = true;

                       columnsprocessed.add(colname);
                    }

                }
            }

        }

    }

    public String wherestatementequaltoclause(String original,HashMap<String,ArrayList<String>> hypernymnsmap) throws Exception
    {

      String returned=nearestcolumforequalcasestring(original);


      if(finalstring.length()>=1&&!returned.matches(""))
        {


            finalstring=finalstring+" AND "+returned;
        }
        else
        {
            finalstring+=returned;
        }
        if(hypernymnsmap!=null)
        {
         Set<String> keyofhyper=hypernymnsmap.keySet();
         ArrayList<String> keys =new ArrayList<>();
         keys.addAll(keyofhyper);

         for(String str:keys)
         {
             ArrayList<String> data=new ArrayList<>();
             data.clear();
             data.addAll(hypernymnsmap.get(str));

             for(String item:data)
             {
                 if(!item.matches("")&&!item.matches(" "))
                 {
                     if (columnsprocessed.contains(str))
                     {
                         finalstring += " OR ";
                     }
                     else
                         {
                         if (!finalstring.matches(""))
                         {

                             finalstring += " AND ";
                         }
                         }
                         try
                         {
                         if (isQuotes(str))
                         {
                             finalstring +=" LOWER( "+ str + " ) = '" + item + "' ";
                         }
                         else
                             {
                             finalstring +=" "+ str + " = " + item + " ";
                             }
                         columnsprocessed.add(str);

                     }

                     catch (Exception e) {
                         e.printStackTrace();
                     }

                 }
             }
         }



        }

        return null;
    }

    public String Postprocess(String sentence)
    {
         /*the query generated inside where will not necessarily be in the right order
           so we need to reorder the part query so that it follows the syntax required by SQL
           Example: Age = 30 AND Color = blue OR Age = 40 needs to be converted to
           Color = blue AND ( Age = 30 OR Age = 40 )

      */

        if(sentence.contains("OR")&&sentence.contains("AND"))
        {


            HashSet<String> partqueries=new HashSet<>();
            HashMap<String,ArrayList<String>> reconstructivemapping=new HashMap<>();
            String returnedcolumn="";

            String[] subpartqueries=sentence.split(" AND ");

            for(String str:subpartqueries)
            {

                partqueries.addAll(Arrays.asList(str.split(" OR ")));
            }
            partqueries.remove("");
            partqueries.remove(" ");


            for(String str:partqueries)
            {

                returnedcolumn=getcolumn(str);

                if(!returnedcolumn.matches(""))
                {
                    if (!reconstructivemapping.keySet().contains(returnedcolumn))
                    {


                        ArrayList<String> a1=new ArrayList<>();
                        a1.add(str);
                        reconstructivemapping.put(returnedcolumn,a1);
                    }
                    else
                    {

                        ArrayList   a1=reconstructivemapping.get(returnedcolumn);
                        a1.add(str);
                        reconstructivemapping.put(returnedcolumn,a1);
                    }
                }

            }

            Set<String> keyset=reconstructivemapping.keySet();
            HashSet<String> keys=new HashSet<>();
            keys.addAll(keyset);

            keys.remove("");
            keys.remove(" ");



            String reorderedsentence="";

            int j=0;
            for(String keyvalue:keys)
            {
                j++;

                ArrayList<String>  a1=reconstructivemapping.get(keyvalue);

                a1.remove("");
                a1.remove(" ");
                if(a1.size()>0)
                {

                    reorderedsentence+=" ( ";

                    for(int i=0;i<a1.size();i++)
                    {

                        reorderedsentence+=" "+a1.get(i)+" ";

                        if(i!=a1.size()-1)
                        {

                            reorderedsentence+=" OR ";
                        }

                    }

                    reorderedsentence += " ) ";

                }
                if(j!=keys.size())
                {

                    reorderedsentence+=" AND ";
                }

            }


            return reorderedsentence;



        }

        return  "";
    }



    public  String getcolumn(String query)
    {

        String column="";

        for (String col:columnsprocessed)
        {

            if(query.contains(col))
            {
                column += col;
                break;

            }
        }
        return  column;


    }









    public conditionalwhere(ArrayList<String> columnslist, ArrayList<String> datalist, String sentence, HashMap<String,ArrayList<String>> hypernymmapped,HashMap<String,String> revcol) throws Exception
    {

        sentence=sentence.replaceAll("'","");
        load();


        this.columnslist=columnslist;
        this.datalist.addAll(datalist);
        this.sentence=sentence;
        this.hypernymmapped=hypernymmapped;
        wherestatementgenerator(sentence);
        this.revcol=revcol;
        this.datalist.removeAll(dataprocesscompletelist);

        ArrayList<String> dataprocesscompleted=new ArrayList<>();


        if(hypernymmapped!=null)
        {
            Set<String> keyset=hypernymmapped.keySet();


            if(keyset!=null&&keyset.size()!=0)
            {

                for(String str:keyset)
                {

                    this.datalist.removeAll(hypernymmapped.get(str));
                }
            }


        }




        if((hypernymmapped!=null)&&((datalist.size()!=0)||(hypernymmapped.keySet().size()!=0)))
        {
            wherestatementequaltoclause(sentence,hypernymmapped);
        }





        String returnsentence=Postprocess(finalstring);

        if(!returnsentence.matches(""))
        {

        finalstring="";
        finalstring+=returnsentence;
        }

    }

//    public static void main(String[] args)
//    {
//        ArrayList<String> columnslist=new ArrayList<>();
//        columnslist.add("name");
//        columnslist.add("yearlyIncome");
//        columnslist.add("country");
//        columnslist.add("age");
//        HashMap<String,String> revcoltest=new HashMap<>();
//        revcoltest.put("name","t_cstmrs.FullName");
//        revcoltest.put("age","t_cstmrs.Age");
//        revcoltest.put("color","t_prds.Color");
//        ArrayList<String> dataslist=new ArrayList<>();
//
//        dataslist.add("30");
//        dataslist.add("Jasmine Lee");
//        dataslist.add("Misbah ul haq");
//   //     dataslist.add("france");
//      //  dataslist.add("3 years");
//        HashMap<String,ArrayList<String>> map=new HashMap<>();
//       ArrayList<String> colors=new ArrayList<>();
//       colors.add("red");
//       colors.add("blue");
//
//       map.put("color",colors);
//        String sentence="show customers whose name is ' Jasmine Lee ' or ' Misbah ul haq ' and age is equal to 30 and age is older than 24";
//
//        try
//       {
//           conditionalwhere obj = new conditionalwhere(columnslist, dataslist, sentence, map, revcoltest);
//
//       }
//       catch (Exception e)
//       {
//           e.printStackTrace();
//
//       }
//
//
//    }
//
//
















}
