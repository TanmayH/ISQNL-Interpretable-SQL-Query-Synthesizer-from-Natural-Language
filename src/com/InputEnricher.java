package com;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/*

This class enriches the input sentence by relacing it with a set of pre-defined markers(like male may be represented by a character 'M') which are present in the
column/other transformations(could be extended to..)

INPUT: Natural language sentence
OUTPUT: Enriched sentence as per the database requirements

*/




public class InputEnricher
{


        public  String sentence,finalstring;

        public InputEnricher(String sentence)
        {
            this.sentence=sentence;
           convert();
        }
        public void convert()
        {
            ArrayList<String> newtoks= new ArrayList<>();


            try {


                JSONParser parser= new JSONParser();
                Object obj = parser.parse(new FileReader("resources/InputEnrichStatements"));
                JSONObject jsonObject = (JSONObject) obj;
                Map map= (Map) jsonObject;
                Set<String> set=map.keySet();
                String [] toks= sentence.split(" ");
                for ( String tok:toks) {

                    if(set.contains(tok)) {
                        String newtok = (String)map.get(tok);
                        newtoks.add(newtok+" ");
                        continue;
                    }
                    newtoks.add(tok+" ");
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

            String rec="";
            for (String newtok:newtoks) {
                rec+=newtok;
            }
            this.finalstring=rec;
        }
    /*    public static void main(String[] args)
        {

            InputEnricher m= new InputEnricher("get all the customers who are ");
            m.convert()
            
        } */
    }

