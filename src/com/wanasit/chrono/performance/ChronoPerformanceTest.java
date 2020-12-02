package com.wanasit.chrono.performance;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.wanasit.chrono.Chrono.Parse;

public class ChronoPerformanceTest {

     String sentence;
    public String finalstring;


   public ChronoPerformanceTest(String input)
   {
        this.sentence=input;
        this.finalstring=testParsingDenseTextOfDates(input);
    }


    public static String testParsingEmptyText(String sentence) {

       StringBuilder builder = new StringBuilder();

          builder.append(sentence);


       String longText = builder.toString();

      long startTimestamp = System.currentTimeMillis();
       for (int i = 0; i < 10; i++)
            Parse(sentence);


        long endTimestamp = System.currentTimeMillis();

        System.out.println();
        return Parse(sentence).toString();

    }

    public static void testParsingTextWithDates(String sentence) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builder.append("This text contains date " + new Date() + " ");
            builder.append("plus some random text");
            builder.append("and some other random text");
        }

        String longText = builder.toString();

        long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) Parse(longText);

        long endTimestamp = System.currentTimeMillis();
        System.out.println("Parsing " + longText.length()
                + " charactors text with 100 dates take time " + (endTimestamp - startTimestamp)/10
                + " ms");

        System.out.println();

        System.out.println(Parse(sentence));
    }

    public static String testParsingDenseTextOfDates(String sentence)
    {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 400; i++) {
            builder.append(new Date() + " ");
        }

        String longText = builder.toString();

        long startTimestamp = System.currentTimeMillis();
        Parse(longText);

        long endTimestamp = System.currentTimeMillis();


//        System.out.println("Parsing " + longText.length()
//                + " charactors text of 400 dates take time " + (endTimestamp - startTimestamp)
//                + " ms");
//        System.out.println();

        String process=Parse(sentence).toString();


        String text=null;
        String date=null;

       process=process.replace("[","");
       process=process.replace("]","");



        if(process!=null&&!process.matches(""))
        {
            String splitdatetext[]=process.split("#");
            text = splitdatetext[0];
            date = splitdatetext[1];
        }

 


       return process;
    }

    public static void testParsingDenseTextOfVariousDateFormats(String sentence)
    {
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder builder = new StringBuilder("HEAD ");
        for (int i = 0; i < 100; i++) {
            builder.append(new Date() + " AND ");
            builder.append(format1.format(new Date()) + " AND ");
            builder.append(format2.format(new Date()) + " AND ");
            builder.append(format3.format(new Date()) + " AND ");
            builder.append("plus some random text");
        }

        String longText = builder.toString();

        long startTimestamp = System.currentTimeMillis();
        for (int i=0; i< 10; i++) Parse(longText);

        long endTimestamp = System.currentTimeMillis();
        System.out.println("Parsing " + longText.length()
                + " charactors text of 400 dates of various formats take time "
                + (endTimestamp - startTimestamp)/10 + " ms");
        System.out.println();
        System.out.println(Parse(sentence));
    }

    public static void main(String args[])
    {
       String  sentence= "what is date on  May 2017";

       // testParsingEmptyText(sentence);

        testParsingDenseTextOfDates(sentence);

          //  testParsingTextWithDates(sentence);
          //  Date res=Chrono.casual.ParseDate("29 feb");
         //   System.out.println(res);

    }
}
