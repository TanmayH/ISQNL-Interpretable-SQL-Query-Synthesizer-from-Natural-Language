package com;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/*Class to implement the BETWEEN clause */
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Between
{

    int cnt=0;
    String res="";
    String[] words=new String[50];
    HashMap<String,String> revcol=new HashMap<>();
    public  ArrayList<String> datalist=new ArrayList<>();
    public HashSet<String> columnsprocessed=new HashSet<>();
    public String finalstring="";


    public Between(String s,ArrayList<String> data,ArrayList<String> tables ,HashMap<String,String> revtables,ArrayList<String> columns,HashMap<String,String> revcol) throws Exception {

        this.revcol = revcol;

        /*Creating a date extractor object */
        date_column_extractor obj = new date_column_extractor();

        ArrayList<String> datecols = new ArrayList<>();
        ArrayList<String> tempcols = new ArrayList<>();

        /*To extract all the date type columns from the given detected table*/
        for (String str : tables)
        {
            tempcols.addAll(obj.find_date(revtables.get(str)));
            datecols.addAll(tempcols);
        }






        String col = "";
        int flag = 0;
        String word1 = "", word2 = "";

        /*Splitting the words using space as a delimiter*/
        words = s.split(" ");
        int cnt = 0;

        /*For each word in the sentence, detect if it is of "between type" */
        for (String iter : words)
        {
            /*Detecting whether the word is of "between" type */
            if (iter.equalsIgnoreCase("between") || iter.equalsIgnoreCase("range") || iter.equalsIgnoreCase("middle"))
            {
                /*Searching for the start and end for the between range to the right side of the keyword*/
                for (int i = cnt + 1; i < words.length; i++)
                {
                    if (data.contains(words[i]))
                    {
                        /*Locating first data item, set to word1 and set flag=1 */
                        if (flag == 0)
                        {
                            word1 = words[i];
                            flag = 1;
                        }
                        /*Locating second data item, set to word2 and set flag=2 */
                        else if (flag == 1)
                        {
                            word2 = words[i];
                            flag = 2;
                            break;
                        }
                    }
                }

                /*Checking if the data is an integer or floating number*/
                if (Pattern.matches("[0-9]+\\.?[0-9]*", word1) && Pattern.matches("[0-9]+\\.?[0-9]*", word2))
                {
                    for (int i = cnt - 1; i >= 0; i--)
                    {
                        if (columns.contains(words[i]) && !typecheck(words[i]))
                        {
                            /*Using typecheck(i) to detect the nearest column which is of integer type
                             * and retrieving the column to which the operation is applied */
                            col = words[i];
                            break;
                        }
                    }
                }
                else
                    /*If the values are not integer or floating point numbers, then they must be dates*/
                {
                    for (int i = cnt - 1; i >= 0; i--)
                    {
                        if (datecols.contains(words[i])) {
                            col = words[i];   /*Retrieving the column to which the operation is applied*/
                            word1 = "'" + word1 + "'";
                            word2 = "'" + word2 + "'";
                            break;
                        }

                    }
                }

                if (!word1.matches("") && !word2.matches("")) {
                    if (res == "")
                    {
                        /*Checking for if the qeury specfies was a NOT between type */
                        if (words[cnt - 1].equalsIgnoreCase("NOT")&&!col.matches("")) {

                            res += " NOT "; /*Adding NOT to the sub query*/
                        }

                        if(!col.matches(""))  /*If first subquery, dont add AND */
                            res = col + " BETWEEN " + word1 + " AND " + word2;
                    }
                    else
                    { /*Otherwise add AND*/
                        if (!words[cnt - 1].equalsIgnoreCase("NOT")&&!col.matches(""))
                        {
                            res += " AND " + col + " BETWEEN " + word1 + " AND " + word2;

                        }
                        else
                        {/*Checking for the NOT Between case */
                            if(!col.matches(""))
                                res += " AND " + col + " NOT BETWEEN " + word1 + " AND " + word2;

                        }

                    }


                    columnsprocessed.add(col); /*Adding the column to columns procesed list */
                    datalist.add(word1);
                    datalist.add(word2);

                }
            }
            cnt++;
            flag = 0;
        }


        finalstring += res;
        /*finalstring here indicates the final subquery */

    }

    /*Method to check the type of the column.. if it is integer type or date type */
    public Boolean typecheck(String column)
    {
        try {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(new FileReader("resources/quotes.json"));
            JSONObject cols = (JSONObject) object;
            String columns = (String) cols.get(revcol.get(column));
            if (columns.equalsIgnoreCase("true")) /*Implies a date column */
                return true;
            else
                return false; /*Implies a integer column */
        }
        catch (Exception e)
        {
            return false;
        }

    }


    /*To test*/
    public static void main(String[] args) {
        try
        {
            HashMap<String,String> revcol=new HashMap<>();
            revcol.put("income","t_cstmrs.YearlyIncome");
            revcol.put("firstname","t_cstmrs.FirstName");
            revcol.put("education","t_cstmrs.Education");
            revcol.put("occupation","t_cstmrs.Occupation");
            revcol.put("birthdate","t_cstmrs.BirthDate");
            revcol.put("gender","t_cstmrs.Gender");
            revcol.put("carsowned","t_cstmrs.intCarsOwned");
            revcol.put("yearlyincome","t_cstmrs.YearlyIncome");
            revcol.put("surname","t_cstmrs.LastName");
            revcol.put("distancetravelled","t_cstmrs.CommuteDistance");
            revcol.put("houseownerflag","t_cstmrs.HouseOwnerFlag");
            revcol.put("datefirstpurchase","t_cstmrs.DateFirstPurchase");
            revcol.put("numberofchildren","t_cstmrs.TotalChildren");
            revcol.put("totalchildren","t_cstmrs.TotalChildren");
            revcol.put("commutedistance","t_cstmrs.CommuteDistance");
            revcol.put("dateofbirth","t_cstmrs.BirthDate");
            revcol.put("work","t_cstmrs.Occupation");
            revcol.put("sex","t_cstmrs.Gender");
            revcol.put("emailaddress","t_cstmrs.EmailAddress");
            revcol.put("emailid","t_cstmrs.EmailAddress");
            revcol.put("phone number","t_cstmrs.Phone");
            revcol.put("lastname","t_cstmrs.LastName");
            revcol.put("qualification","t_cstmrs.Education");
            revcol.put("purchasedate","t_cstmrs.DateFirstPurchase");
            revcol.put("maritalstatus","t_cstmrs.MaritalStatus");
            revcol.put("phone","t_cstmrs.Phone");
            revcol.put("dob","t_cstmrs.BirthDate");
            revcol.put("contact number","t_cstmrs.Phone");
            revcol.put("name","t_cstmrs.FullName");
            revcol.put("addressline2","t_cstmrs.AddressLine2");
            revcol.put("addressline1","t_cstmrs.AddressLine1");
            revcol.put("number of cars","t_cstmrs.intCarsOwned");
            revcol.put("children at home","t_cstmrs.intChildrenAtHome");
            revcol.put("fullname","t_cstmrs.FullName");
            revcol.put("age","t_cstmrs.Age");

            ArrayList<String> columns=new ArrayList<>();
            columns.add("t_cstmrs.Age");
            columns.add("t_cstmrs.BirthDate");
            columns.add("income");
            columns.add("t_cstmrs.TotalChildren");
            columns.add("t_cstmrs.intChildrenAtHome");
            columns.add("t_cstmrs.HouseOwnerFlag");
            columns.add("t_cstmrs.intCarsOwned");
            columns.add("t_cstmrs.DateFirstPurchase");

            ArrayList<String> tables=new ArrayList<>();
            tables.add("customers");

            HashMap<String,String> tablemap=new HashMap<>();
            tablemap.put("customers","t_cstmrs");

            ArrayList<String> data=new ArrayList<>();

            data.add("30");
            data.add("40");
            data.add("France");
            data.add("2");
            data.add("4");
            data.add("2017-06-08");
            data.add("2018-05-04");
            data.add("red");
            data.add("yellow");
            data.add("2017-06-05");
            data.add("2018=07-04");



            Between ob = new Between( "income in the range 30 and 40 and t_cstmrs.TotalChildren not between 2 and 4 and birthdate between 2017-06-08 and 2018-05-04 and datefirstpurchase between 2017-06-05 and 2018=07-04", data,tables,tablemap,columns,revcol);


            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}