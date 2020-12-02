package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class YearMonth
{
    public String year="";
    public String month="";



    String[] months=new String[]{"january","jan","february","feb","march","mar","april","apr","may","june","jun","july","august","aug","september","sep","october","oct","november","nov","december","dec"};  /*adding the month names in all formats into a array of strings*/

    ArrayList<String> monthslist=new ArrayList<>();  /* create an arraylist*/


    protected static String regPattern1 = "(\\sjanuary\\s|\\sjan\\s|\\sfebruary\\s|\\sfeb\\s|\\smarch\\s|\\smar\\s|\\sapril\\s|\\sapr\\s|\\smay\\s|\\sjune\\s|\\sjun\\s|\\sjuly\\s|\\saugust\\s|\\saug\\s|\\sseptember\\s|\\ssep\\s|\\soctober\\s|\\soct\\s|\\snovember\\s|\\snov\\s|\\sdecember\\s|\\sdec\\s)"; /* regular expression to identify the month names */
    protected static String regPattern2 = "(\\d{4})";  /*regular expression to identify year*/

    String stuff(String text)    /* function takes the text as an argument*/
    {




        String datereturn="";
        String text1 = text.toLowerCase();     /*convert the text to lower case */
        Pattern pattern1 = Pattern.compile(regPattern1);
        Pattern pattern2 = Pattern.compile(regPattern2);

        Matcher matcher1 = pattern1.matcher(text1);  /*match the text with first pattern*/
        Matcher matcher2 = pattern2.matcher(text1);   /*match the text with second pattern*/

        String m;
        int yr,i=0;

        //while(true)
        //{
        while(matcher2.find())       /*loop until there is no year left in the text to match*/
        {
            i++;



            yr=Integer.parseInt(matcher2.group(1));   /* get the year form the text*/
            year+=yr;                                 /*add yr to the sstring*/
            datereturn =" AND YEAR = "+yr;
            System.out.print(yr +"-");                 /*print yr*/




            break;
        }

        monthslist.addAll(Arrays.asList(months));  /*add the minths to the list*/
        String splittest[]=text.split("\\s");


        ArrayList<String> monthname=new ArrayList<>(); /*create an arraylist*/

        for(String str:splittest)           /*start for each loop*/
        {
            if(!str.matches(""))        /*check if there is a month name*/
            {
                if(monthslist.contains(str))   /* check if monthlist contains the string*/
                {
                    monthname.add(str);   /*add the string to the list*/
                }
            }

        }



        for(String str:monthname)        /* for each loop starts*/
        {
            switch (str)                /* switch starts*/
            {
                case "jan":
                case "january":       /*if str is jan or january, append 01 to month, append MONTH = , O1 to datereturn, and print 01 and break*/

                    month+=01;
                    datereturn="MONTH = "+01+datereturn;
                    
                    break;
                case "feb":
                case "february":            /*if str is feb or febuary, append 02 to month, append MONTH = , O2 to datereturn, and print 02 and break*/
                    month+=02;
                    datereturn="MONTH = "+02+datereturn;
                    
                    break;

                case "mar":
                case "march":            /*if str is mar or march, append 3 to month, append MONTH = , O3 to datereturn, and print 03 and break*/
                    month+=3;
                    datereturn="MONTH = "+03+datereturn;
                    
                    break;
                case "apr":
                case "april":            /*if str is apr or april, append 4 to month, append MONTH = , O4 to datereturn, and print 04 and break*/
                    month+=4;
                    datereturn="MONTH = "+04+datereturn;
                    
                    break;
                case "may":              /*if str is may, append 5 to month, append MONTH = , O5 to datereturn, and print 05 and break*/
                    month+=5;
                    datereturn="MONTH = "+05+datereturn;
                    
                    break;
                case "jun":
                case "june":             /*if str is jun or june, append 6 to month, append MONTH = , O6 to datereturn, and print 06 and break*/
                    month+=6;
                    datereturn="MONTH = "+06+datereturn;
                    
                    break;
                case "jul":
                case "july":         /*if str is jul or july, append 7 to month, append MONTH = , O7 to datereturn, and print 07 and break*/
                    month+=7;
                    datereturn="MONTH = "+07+datereturn;
                    
                    break;
                case "aug":
                case "august":       /*if str is aug or august, append 8 to month, append MONTH = , O8 to datereturn, and print 08 and break*/
                    month+=8;
                    datereturn="MONTH = 08"+datereturn;
                    
                    break;
                case "sep":
                case "september":       /*if str is sep or september, append 9 to month, append MONTH = , O9 to datereturn, and print 09 and break*/
                    month+=9;
                    datereturn="MONTH = 09"+datereturn;
                    
                    break;
                case "oct":
                case "october":          /*if str is oct or october, append 10 to month, append MONTH = , 10 to datereturn, and print 10 and break*/
                    month+=10;
                    datereturn="MONTH = 10"+datereturn;
                    
                    break;
                case "nov":
                case "november":            /*if str is nov or november, append 11 to month, append MONTH = , 11 to datereturn, and print 11 and break*/
                    month+=11;
                    datereturn="MONTH = 11"+datereturn;
                    
                    break;
                case "dec":
                case "december":             /*if str is dec or december, append 12 to month, append MONTH = , 12 to datereturn, and print 12 and break*/
                    month+=12;
                    datereturn="MONTH = 12"+datereturn;
                    
                    break;

            }
            break;
        }


        return  datereturn;  /*return datereturn*/
        // }


    }
}
public class MonthYearDateFormat
{
    public String finalstring="";
    public String month="";
    public String year="";


    public  static  void main(String[] args)
    {
        String test="Customers born in  march";
        MonthYearDateFormat myd=new MonthYearDateFormat(test);  /*create an object of MonthYearDateFormat*/
    }






    public   MonthYearDateFormat(String s)
    {

       Calendar cd=Calendar.getInstance();

       finalstring="";

        YearMonth ym = new YearMonth();      /*create an object of YearMonth*/
        finalstring=ym.stuff(s);   /*call the function*/
        month=ym.month;
        year=ym.year;

        int yearcontainer=0;

        if(s.contains("last year")||s.contains("previous year"))
        {
              yearcontainer = cd.get(Calendar.YEAR);
              yearcontainer += -1;
        }
        if(yearcontainer!=0)
        {
            year =""+yearcontainer;
          finalstring+=year;
        }


    }

}