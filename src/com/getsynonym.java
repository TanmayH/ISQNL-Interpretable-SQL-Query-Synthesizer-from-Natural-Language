/*Class to extract synonyms for a given word*/
//package synonyms;

package com;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class getsynonym
{
    XMLParser xml = new XMLParser();
    ArrayList<String> synlist=new ArrayList<String>();
    ArrayList<String> uselesssynonymslist=new ArrayList<>();
    ArrayList<String> returnlist=new ArrayList<>();

    /*Set of words to be ignored by the synonym generator since they generate unexpected/erronous results when parsed with it */
    //String[] uselesssynonyms={"orders","order","last","year","yearly","first","list","name"};


    /*Function to iterate through then nouns and call the generator for each of them*/
    public ArrayList<String> run (ArrayList<String> nounlist,ArrayList<String> datanames)
    {
        xml.input = "synonymsappendix";

        /*Creating an arraylist of all the useless synonyms*/
        uselesssynonymslist.addAll(xml.xmlParser());
        for (String a:nounlist)
        {
            /*If the given word is not a part of useless synonynms list, generate its synonyms */
            if(!uselesssynonymslist.contains(a))
            {
                synlist = this.generate(a);
                /*Adding all tbe elements returned by the simple checker to synlist*/
                returnlist.addAll(simplechecker(synlist, datanames));
            }

        }
        /*returnlist holds all the possible synnonym overall*/
        return returnlist;
    }
    public ArrayList<String> generate(String wordForm)
    {
        /*setting path for the WordNet Directory*/
        File f=new File("./WordNet/2.1/dict");
        System.setProperty("wordnet.database.dir", f.toString());

        /*Creating a WordNet database instance*/
        WordNetDatabase database = WordNetDatabase.getFileInstance();
        /*Acquiring the synsets for the wordForm passed*/
        Synset[] synsets = database.getSynsets(wordForm);
        ArrayList<String> al = new ArrayList<String>();
        if (synsets.length > 0)

        {

            HashSet hs = new HashSet();

            /*Generation of synonyms*/
            for (int i = 0; i < synsets.length; i++)
            {
                /*getWordForms gives all the synnoyms*/
                String[] wordForms = synsets[i].getWordForms();
                for (int j = 0; j < wordForms.length; j++)
                {
                    al.add(wordForms[j]);
                }


                /*Removing duplicates*/
                hs.addAll(al);
                al.clear();
                al.addAll(hs);
            }
        }
        return al;
    }

    /*To check which of the synonyms generated map to the nouns required by the nounslist*/
    public ArrayList<String> simplechecker(ArrayList<String> nounsslist,ArrayList<String> samplenameslist)
    {
        int x[]=new int[100];
        int k=0;

        ArrayList<String> simplecheckdata=new ArrayList<>();
        simplecheckdata.clear();
        simplecheckdata.addAll(samplenameslist);
        simplecheckdata.retainAll(nounsslist);
        nounsslist.removeAll(simplecheckdata);
        return simplecheckdata;
    }


}

