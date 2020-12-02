package com;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/*
Final processing before outputting the SQL query is performed by this class.
 */

public class Postprocess
{
    public String finalstring;
    String [] SqlKey = {"select","from","join","between","where","on"};
    /*Boolean variable that holds the condition of whether a join of tables has been performed or not.*/

    public  Boolean isJoin;

    ArrayList<String> finaltokens= new ArrayList<>();

    /*The processes sentence is stored in posts.*/

    String posts;



    /* The variable revtables performs a mapping between the logical table names and the actual table names.
     * Similarly with revcolumns between columns.*/

    HashMap<String,String > revtables;
    HashMap<String,String> revcols;
    public Postprocess(String posts, Boolean isJoin, HashMap<String,String> revtables, HashMap<String,String> revcols) throws  Exception{
        this.posts=posts;
        this.revcols=revcols;
        this.revtables=revtables;
        this.isJoin=isJoin;
        converter();
    }


    public  void converter()throws  Exception
    {
        ArrayList<String> sqlkeys = new ArrayList<>();
        String currentsqlkey="";
        sqlkeys.addAll(Arrays.asList(SqlKey));
        String[] tokens=posts.split(" ");
        String table,column;
        for (String toks:tokens)
        {
            /*If join has been performed, retrieve the tables and columns that map with what
             * is found in the sentence*/

            if (isJoin){
                /*table gets the actual name of a table whose logical name is passed.*/
                table=revtables.get(toks);
                /*column gets the actual name of a column whose logical name is passed.*/
                column=revcols.get(toks);

                if (column!=null && table!=null){
                    if(currentsqlkey.equalsIgnoreCase("from")|| currentsqlkey.equalsIgnoreCase("join")){
                        finaltokens.add(table);
                    }
                    else
                    {
                        finaltokens.add(column);
                    }
                }

                else if (table!=null){
                    finaltokens.add(table);
                }
                else if (column!=null)
                    finaltokens.add(column);

                else{
                    /*By default, just add the token*/
                    finaltokens.add(toks);
                    if(sqlkeys.contains(toks.toLowerCase())){
                        currentsqlkey = toks.toLowerCase();
                    }
                }


            }

            else
            {
                /*No Join has been performed*/
                table=revtables.get(toks);
                column=revcols.get(toks);

                if(table != null && column!=null){

                    if(currentsqlkey.equalsIgnoreCase("from") || currentsqlkey.equalsIgnoreCase("join")){
                        finaltokens.add(table);
                    }
                    else
                    {
                        System.out.println(currentsqlkey);
                        String temp[]=column.split("\\.");
                        /*Column names have an addditional word before them to identify the  SQL
                         * clause that needs to be performed. temp[1] is the actual column   */
                        finaltokens.add(temp[1]);


                    }

                }
                else if ((table)!=null)
                    finaltokens.add(table);
                else if (column!=null)
                {/*Split based on the regular expression test of \. which means the presence of a full stop
                 NOTE: \. in regex would mean any character after \*/
                    String temp[]=column.split("\\.");

                    /*Column names have an addditional word before them to identify the  SQL
                     * clause that needs to be performed. temp[1] is the actual column   */
                    finaltokens.add(temp[1]);
                }
                else{
                    finaltokens.add(toks);
                    if(sqlkeys.contains(toks.toLowerCase())){
                        currentsqlkey = toks.toLowerCase();
                    }
                }

            }
        }


    }

    @Override
    public String toString()
    {
        String result="";

        for (String tokens:finaltokens)
        {

            result+=" "+tokens+" ";


        }


        /*replace all the double spaces between words with single space*/
        result=result.replaceAll("\\s{2,}"," ").trim();


        this.finalstring=result;
        return result;
    }

    /*code to test post processing in java*/
    public static void main(String[] args)throws Exception
    {
        String posts= "SELECT * FROM category WHERE LOWER( category ) = 'clothing'";
        HashMap<String,String> revtables= new HashMap<>();
        HashMap<String,String> revcols= new HashMap<>();
        revtables.put("category","t_prdcat");
        revcols.put("category","t_prdcat.ProductCategoryName");
        Postprocess ob= new Postprocess(posts,false,revtables,revcols);
        System.out.println(ob);

    }


}
