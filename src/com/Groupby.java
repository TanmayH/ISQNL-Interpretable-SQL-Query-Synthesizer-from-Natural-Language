package com;

import java.util.ArrayList;

public class Groupby
{

    public String finalstring="";




    public static void main(String[] args)
    {

        String sentence="number of customers in canada grouped by each state";

        ArrayList<String> columns=new ArrayList<String>();

        columns.add("state");


        Groupby ob=new Groupby(sentence,columns,false);


        


    }



    public Groupby(String sentence, ArrayList<String> columns,boolean groupoperations)
    {

        if (groupoperations)
        {


            if(columns.size()!=0)
                {

                  boolean groupbyappend=false;
                  for(String colname:columns)
                  {
                      if(!colname.matches(""))
                      {
                          if (!groupbyappend)
                          {
                              finalstring += "GROUP BY "+colname+" ";
                              groupbyappend = true;
                              continue;

                          }


                          finalstring+=" , "+colname+" ";

                      }
                  }
                }

        }
        else
            {
            ArrayList<String> groupbykeywords = new ArrayList<>();
            XMLParser xml = new XMLParser();
            xml.input = "GroupBy";
            groupbykeywords.addAll(xml.xmlParser());

            for (String str : groupbykeywords) {
                if (sentence.contains(" " + str + " ")) {
                    String[] brokensentence1;


                    brokensentence1 = sentence.split(str);


                    if (brokensentence1 != null && brokensentence1.length >= 2) {
                        if (brokensentence1[1].contains("by")) {

                            String[] byssubstrings = brokensentence1[1].split("by");

                            if (byssubstrings.length >= 2) {
                                String columnfinder = byssubstrings[1];
                                columnfinder = columnfinder.replaceAll("and", "");
                                columnfinder = columnfinder.replaceAll("  ", " ");
                                columnfinder = columnfinder.replaceAll(",", " ");
                                String[] brokensentence2 = columnfinder.split(" ");

                                boolean flag = false;
                                int k = -5;

                                for (int j = 0; j < brokensentence2.length; j++) {


                                    if (columns.contains(brokensentence2[j]) && !flag) {

                                        finalstring += "GROUP BY " + brokensentence2[j] + " ";
                                        k = j;
                                        flag = true;
                                        continue;
                                    }


                                    if (flag && columns.contains(brokensentence2[j])) {


                                        if (j == k + 1) {

                                            k += 1;
                                            if (!brokensentence2[j].matches("")) {

                                                finalstring += ", " + brokensentence2[j] + " ";
                                            }

                                        } else {
                                            break;
                                        }
                                    }

                                }
                            }


                        }
                    }
                }
            }


        }
       }

}
