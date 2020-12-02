package com;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class CountSum2
{

    HashMap<String, String> revcols = new HashMap<>(); /*For the revcols mapping */

    public String finalstring = ""; /*Holds the finalstring */

    /*An ArrayList to hold the countwords */
    ArrayList countwords = new ArrayList<String>();

    /*An ArrayList to hold the sumwords */
    ArrayList sumwords = new ArrayList<String>();

    /*String holding the final result */
    String res = "";

    /*Hashset to show the columns involved in the count and sum operations*/
    public HashSet<String> usedcols=new HashSet<>();


    /*Constructor method*/
    public CountSum2(String s, ArrayList<String> columns, HashMap<String, String> revcol,ArrayList<String> tables)
    {
        XMLParser xml=new XMLParser();

        this.revcols = revcol; /*Assigning it to the mapping*/

        /*Adding the "count" indicatory words to its mapper*/
        xml.input="count";
        countwords.addAll(xml.xmlParser());

        /*Adding the "sum" indicatory words to its mapper*/
        xml.input="sum";
        sumwords.addAll(xml.xmlParser());



        /*Calling the process method to process the user query and parse all count and sum operations*/
        this.process(s, columns,tables);

        /*Trimming all unwanted spaces*/
        res = res.trim();

        /*To remove the initial ","*/
        if (res != "")
        {
            res = res.substring(1, res.length());

            finalstring += res;
        }
    }

    /*Method to process the query and parse all "sum" and "count" operations*/
    public void process(String s, ArrayList<String> columns,ArrayList<String> tables)
    {
        /*Splitting the string into an array*/
        String[] ele = s.split(" ");

        /*sum_p indicates SUM operation, count_p indicates COUNT operation*/
        boolean sum_p = false, count_p = false;

        /*For each string in the query*/
        for (String str : ele)
        {
            /*Removing erronous results due to null strings or garbage strings*/
            if (str!=null && !str.matches("") && !str.matches(" "))
            {
                /*To check if the given word is a part of the countwords mapper*/
                if (countwords.contains(str.toLowerCase()) && !count_p && !sum_p)
                {
                    /*Set to true indicating a COUNT() operation is present*/
                    count_p = true;
                }
                /*To check if the given word is a part of the sumwords mapper*/
                else if (sumwords.contains(str.toLowerCase()) && !sum_p && !count_p)
                {
                    /*Set to true indicating a SUM() operation is present*/
                    sum_p = true;
                }
                /*If the string is a column/table name*/
                else if (columns.contains(str) || tables.contains(str))
                {
                    /*If the string is a table name, add a COUNT(*) operation*/
                    if (tables.contains(str) && !columns.contains(str)&&(count_p||sum_p))
                    {
                        if (res.indexOf("COUNT( * )")==-1) /*Check to remove duplicate entries of COUNT(*)*/
                            res += ",COUNT( * )"; /*Add subquery to result*/
                        count_p=false;
                        sum_p=false;
                    }
                    /*The string is a column name*/
                    else
                    {
                        /*Count operation on the column*/
                        if (count_p)
                        {
                            if (res.indexOf("COUNT( "+str+" )")==-1)/*Check to remove duplicate entries of COUNT(col)*/
                                res += ",COUNT( " + str + " )"; /*Add subquery to result*/
                            count_p = false;
                            sum_p = false;
                            usedcols.add(str);
                        }
                        /*If sum keyword is detected*/
                        else if (sum_p)
                        {
                            /*Check the column type, if column is of integer type perform SUM OPERATION
                            else, perform COUNT operation*/
                            boolean check = this.typecheck(str);

                            /*Column is non-integer type: perform COUNT operation*/
                            if (check)
                            {
                                if (res.indexOf("COUNT( "+str+" )")==-1) /*Check to remove duplicate entries of COUNT(col)*/
                                    res += ",COUNT( " + str + " )"; /*Add subquery to result*/
                            }

                            /*Column is integer type: perform SUM operation*/
                            else
                            {
                                if (res.indexOf("SUM( "+str+" )")==-1) /*Check to remove duplicate entries of SUM(col)*/
                                    res += ",SUM( " + str + " )"; /*Add subquery to result*/
                            }
                            count_p = false;
                            sum_p = false;
                            /*Add the column to usedcols*/
                            usedcols.add(str);
                        }
                    }
                }
            }
        }

        /*If a count operation was detected but no column was detected, add a COUNT(*) operation */
        if (count_p)
        {
            if (res.indexOf("COUNT( * )")==-1) /*Check to remove duplicate entries of COUNT(*)*/
                res += ",COUNT( * )";
        }
    }

    /*Method to check the type of the column*/
    public Boolean typecheck(String column)
    {
        try
        {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(new FileReader("resources/quotes.json"));
            JSONObject cols = (JSONObject) object;


            String columns = (String) cols.get(revcols.get(column));



            /*Implies column is of string type*/
            if (columns.equalsIgnoreCase("true"))
                return true;
                /*Implies column is of integer type*/
            else
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }


    /*Main function for test*/
    /*
    public static void main(String[] args)
    {
        //Bunch of tables
        ArrayList<String> tables=new ArrayList<>();
        tables.add("customers");
        tables.add("t_prds");
        tables.add("t_ggrphy");
        tables.add("t_saldtls");
        tables.add("t_prdcat");

        //Bunch of columns
        ArrayList<String> columns = new ArrayList<>();
        columns.add("t_cstmrs.Age");
        columns.add("t_cstmrs.BirthDate");
        columns.add("t_cstmrs.YearlyIncome");
        columns.add("t_cstmrs.TotalChildren");
        columns.add("t_cstmrs.intChildrenAtHome");
        columns.add("t_cstmrs.HouseOwnerFlag");
        columns.add("t_cstmrs.intCarsOwned");
        columns.add("t_cstmrs.DateFirstPurchase");
        columns.add("t_cstmrs.FirstName");
        columns.add("t_cstmrs.LastName");
        columns.add("t_cstmrs.Educations");

        String s1, s2, s3, s4, s5, s6, s7,s8,s9;
        s1 = "Show count of t_cstmrs.Age with age 40"; //Using count
        s2 = "Find total t_cstmrs.YearlyIncome"; //Using sum on integer column
        s3 = "Find total number of t_cstmrs.intChildrenAtHome"; //Using total number on interger column
        s4 = "Find total number of t_cstmrs.FirstName"; //Using total number on non integer column
        s5 = "Find sum of t_cstmrs.FirstName"; //Sum on non integer column
        s6 = "Find subtotal of t_cstmrs.intChildrenAtHome and show count of t_cstmrs.Age with age 40"; //Both count and sum
        s7 = "Find something and something else "; //No count and sum
        s8 = "get number of male customers"; //With tablename
        s9="show all the customers";



        CountSum2 ob1 = new CountSum2(s1, columns, null,tables);
        CountSum2 ob2 = new CountSum2(s2, columns, null,tables);
        CountSum2 ob3 = new CountSum2(s3, columns, null,tables);
        CountSum2 ob4 = new CountSum2(s4, columns, null,tables);
        CountSum2 ob5 = new CountSum2(s5, columns, null,tables);
        CountSum2 ob6 = new CountSum2(s6, columns, null,tables);
        CountSum2 ob7 = new CountSum2(s7, columns, null,tables);
        CountSum2 ob8 = new CountSum2(s8, columns, null,tables);
        CountSum2 ob9 = new CountSum2(s9, columns, null,tables);

    }*/

}