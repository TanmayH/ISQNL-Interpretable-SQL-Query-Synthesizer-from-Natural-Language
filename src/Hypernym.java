import com.WordNet;

import java.util.ArrayList;
import java.util.HashMap;

public class Hypernym
{
    public HashMap<String, ArrayList<String>> modifiedcoltodata;

    public ArrayList<String> hypernymchecker(ArrayList<String> nounsslist, ArrayList<String> samplenameslist, int value, ArrayList<String> hypernymsuseless, HashMap<String,ArrayList<String>> coltodata)
    {
        modifiedcoltodata=new HashMap<>();
        /* Setting up the synsets and the hypernyms files*/
        WordNet hyp=new WordNet("resources/synsets.txt","resources/hypernyms.txt");
        String ancestor;

        /*List to hold all the hyprernyms*/
        ArrayList<String> hypernymlist = new ArrayList<>();
        hypernymlist.clear();

        /*Iterating through all the strings in the nounlist*/
        for (int i=0;i<nounsslist.size();i++)
        {
            /*Get the string from the nounlist*/
            String string1=nounsslist.get(i);

            /*Iterating through all the actual table/column names for each word in the nounlist*/
            for (String check:samplenameslist)
            {
                /*Condition to filter out certain words that give erronous results with hypernym*/
                if (!(hypernymsuseless.contains(string1) || hypernymsuseless.contains(check)))
                {
                    try {
                        /*Obtain ancestor of the two words...word from nounlist and word from samplenameslist via hypernym class*/
                        ancestor = hyp.findhypernym(string1.toLowerCase(), check); //Lower case

                        /*If an ancestor is detected*/
                        if (!ancestor.equals("Not found"))
                        {
                            /*Add the ancestor to hypernym list*/
                            hypernymlist.add(ancestor);

                            /*Implies hypernym check for columns*/
                            if (value == 1)
                            {
                                /*To add to the column to data mapper based on existence or not*/
                                if (coltodata.keySet().contains(ancestor))
                                {
                                    coltodata.get(ancestor).add(string1);
                                }
                                else
                                {
                                    ArrayList<String> newdata=new ArrayList<>();
                                    newdata.add(string1);
                                    coltodata.put(ancestor, newdata);
                                }
                            }
                            break;
                        }
                    } catch (IllegalArgumentException e)
                    {
                        ;
                    }
                }
            }
        }

        /*To remove all the hypernyms from the nounlist*/
        if(value==1)
        {
            nounsslist.removeAll(hypernymlist);
        }

        modifiedcoltodata=coltodata;
        return hypernymlist;
    }

    public HashMap<String,ArrayList<String>> getAfterTableColToData()
    {
        return modifiedcoltodata;
    }
}
