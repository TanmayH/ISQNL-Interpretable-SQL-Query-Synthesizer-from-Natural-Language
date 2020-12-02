package com;

import java.util.ArrayList;
/*Generates the SQL syntax for the SELECT clause.
  Receives column names and places them onto the final string*/
public class Select {                                       /* DO NOT MODIFY ANY CLASS */

    public String finalstring="";
    public  Select(ArrayList<String> columnnames)
    {                                                       /*Constructor initialises finalstring with the list of columns present*/
        int i;
        if(columnnames.size()!=0&&columnnames!=null)
        {
            /*Remove all null-named columns from the array list*/
            columnnames.remove("");
            for (i = 0; i < columnnames.size() - 1; i++)
            {                                                  /*As long as the column name does not match null*/
                if(!columnnames.get(i).matches(""))
                    finalstring += columnnames.get(i) + " , ";
            }
            /*Preventing the extra ',' after the last column has been parsed*/
            finalstring += columnnames.get(i) + " ";
        }
        else
        {
            finalstring+=" ";
        }

    }

}