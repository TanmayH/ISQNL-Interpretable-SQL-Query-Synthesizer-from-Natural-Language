/*Class to implement the like clause */
package com;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Like
{
    public String likecolumn="";
    public String likedataword="";
    String orginal;
    public  Boolean islike=false;
    Boolean negative=false;
    Boolean starts=false;
    Boolean ends=false;
    public String finalstring="";
    String[] parse;
    public HashSet<String> processedcolumns=new HashSet<>();
    ArrayList<String> parsed= new ArrayList<>();
    List<String> start= new ArrayList<>();
    List<String> end= new ArrayList<>();
    List<String> loaded= new ArrayList<>();
    List<String> like=new ArrayList<>();
    ArrayList<String> columnlist;
    ArrayList<String> processeddata=new ArrayList<>();
    public ArrayList<String> datalist =new ArrayList<String>();



    public Like(String original, ArrayList<String> columnlist)
    {
        /*To hold the orignial user query*/
        this.orginal=original;

        /*List of detected columns */
        this.columnlist=columnlist;

        /*Call to the load() function-- To load all the 'like' words*/
        load();

        /*Call to the loadModules() function -- To load the individual start, end and like keyword components*/
        loadModules();

        /*Call to the final string generator function---to generate the sub query with the like clauses*/
        likefinalstringgenerator();
    }

    /* This method loads all the words which indicate a 'like' keyword in the XML parser
     * To check if the sentence contains any "Like" words*/
    public void load()
    {
        XMLParser xml= new XMLParser();
        xml.input="Like"; /*Passing "Like" as an input to the XML parser */
        loaded=xml.xmlParser();
    }


    /* To load the respective keysets for the "starts" ,"ends" and "like" keywords */
    public void loadModules()
    {
        XMLParser xml= new XMLParser();

        /* To load the keywords for "starts" descriptor in the query */
        xml.input="starts";
        start=xml.xmlParser();

        /* To load the keywords for "ends" descriptor in the query */
        xml.input="ends";
        end=xml.xmlParser();

        /* To load the keywords for "like" descriptor in the query */
        xml.input="like";
        like=xml.xmlParser();
    }

    /*Method to check if the sentence passed is of a "Like" type-- that is, if it has any "like" type keywords*/
    public boolean isLike(String sentence)
    {
        /*Calling the initial load function to set the total list of "Like" words */
        load();

        /*Splitting the sentence as a space seperated set of words*/
        parse= sentence.split(" ");

        /*Checking to see if any of the words in the sentence are a part of the "Like" set of words */
        for ( String word:parse)
        {
            /*parsed.add(word); */

            if (loaded.contains(word))
            {
                /*On finding a match, return true, indicating that the sentence is of "Like" type and must be processed further */
                islike=true;
                return true;
            }

        }

        /* Return false, indicating that there is no "like" word in the sentence, and thus LIKE clause does not apply to it */
        return  false;
    }

    /*Method to check which is the nearest column to the left of the "Like" word */

    public String nearestleftcolumn(String s)
    {
        /*Flag to check whether a NOT keyword should be appended to the like clause*/
        boolean flag=false;
        s=s.replaceAll("  "," ");
        String broken[]=s.split(" ");
        int j=broken.length-1;

        /*Parsing through the enture set of words in the string */
        while(j>=0)
        {

            /*If NOT keyword detected..set flag to true*/
            if(broken[broken.length-1].equalsIgnoreCase("NOT"))
            {
                flag=true;
            }

            /*If the word detected is a column, indicates the clause is to be applied to this column*/

            if(columnlist.contains(broken[j]))
            {

                /*Indicates the query will be of the form NOT LIKE*/
                if(flag)
                {
                    return  broken[j]+" NOT";
                }
                /*Indicates the query will be of the form LIKE*/
                else
                {
                    return broken[j];
                }
            }

            j--;

        }
        return  null;

    }

    /*Method to find the nearest data from the left side of the string*/
    public String nearestdata(String s)
    {

        s=s.replaceAll("'","");
        s=s.replaceAll("  "," ");
        int i=0;
        String[] broken=s.split(" ");
        if(broken.length>0)
        {
            return broken[1];
        }
        return  null;
    }


    public void likefinalstringgenerator()
    {

        boolean flag=false;



        /*To check the location of the "starts with" keyword in the sentence*/
        for(String keyword:start)
        {
            /*For every keyword in the "starts" XML output, check if the work is in the sentence
            If so, find its location, the nearest column to the left, whether a NOT is indicated or not
            and the nearest data member.
            */
            if(orginal.contains(keyword))
            {
                Pattern p=Pattern.compile("(.*?)"+keyword+"(.*)");
                Matcher m=p.matcher(orginal);
                while(m.find())
                {
                    /*To find the nearest left column*/
                    String colname =nearestleftcolumn(m.group(1));

                    /*To find the nearest data member in that given match set*/
                    String data=nearestdata(m.group(2));


                    /*Appending the sub clause to the main string*/
                    if(flag&&(colname!=null)&&(data!=null))
                    {
                        finalstring+=" AND "; /*If its the first sub clause , dont add AND else do */
                    }
                    if(colname!=null&&data!=null)
                    {
                        processedcolumns.add(colname);
                        datalist.add(data);
                        finalstring += " LOWER( "+colname + " ) LIKE '" + data + "%'"; /*"starts" indicating & only after the substring */
                        flag=true;
                    }

                }

            }

        }

        /*To check the location of the "ends with" keyword in the sentence*/
        for(String keyword:end)
        {
            /*For every keyword in the "ends" XML output, check if the work is in the sentence
            If so, find its location, the nearest column to the left, whether a NOT is indicated or not
            and the nearest data member.
            */
            if (orginal.contains(keyword))
            {
                Pattern p = Pattern.compile("(.*?)" + keyword + "(.*)");
                Matcher m = p.matcher(orginal);
                while (m.find())
                {
                    /*To find the nearest left column*/
                    String colname = nearestleftcolumn(m.group(1));

                    /*To find the nearest data member in that given match set*/
                    String data = nearestdata(m.group(2));




                    /*Appending the sub clause to the main string*/
                    if (flag && (colname != null) && (data != null))
                    {
                        finalstring += " AND "; /*If its the first sub clause , dont add AND else do */
                    }
                    if (colname != null && data != null)
                    {
                        processedcolumns.add(colname);
                        datalist.add(data);
                        finalstring += " LOWER( "+colname + " ) LIKE '%" + data + "'"; /*"ends" indicating % only before the substring*/
                        flag = true;
                    }

                }

            }
        }

        /*To check the location of the "like" keyword in the sentence*/
        for(String keyword:like)
        {
            /*For every keyword in the "like" XML output, check if the work is in the sentence
            If so, find its location, the nearest column to the left, whether a NOT is indicated or not
            and the nearest data member.
            */

            if (orginal.contains(keyword))
            {
                Pattern p = Pattern.compile("(.*?)" + keyword + "(.*)");
                Matcher m = p.matcher(orginal);
                while (m.find()) {

                    /*To find the nearest left column*/
                    String colname = nearestleftcolumn(m.group(1));
                    /*To find the nearest data member in that given match set*/
                    String data = nearestdata(m.group(2));




                    /*Appending the sub clause to the main string*/
                    if (flag && (colname != null) && (data != null))
                    {
                        finalstring += " AND "; /*If its the first sub clause , dont add AND else do */
                    }
                    if (colname != null && data != null)
                    {
                        processedcolumns.add(colname);
                        datalist.add(data);
                        finalstring += "LOWER( "+ colname + " ) LIKE '%" + data + "%'"; /*"like" indicating % on both sides of substring*/
                        flag = true;
                    }

                }

            }

        }

    }




    public static void main(String[] args)
    {
        String s="get all the customers whose name does not start with ab and whose emailId is like gmail.com and surname ends with cd";
        ArrayList<String> cols= new ArrayList<>();
        cols.add("name");
        cols.add("surname");
        cols.add("emailId");
        Like ob= new Like(s,cols);
        ob.isLike(s);

    }

}

