import java.util.ArrayList;
import java.util.HashMap;

public class Concatenation
{
    SimpleChecker sc;
    ArrayList<String> nounlistmodified;
    ArrayList<String> concatkeycontainer;
    ArrayList<String> nounlistmodifiedaftercolumn;
    String modifiedsentencecopy;
    String modifiedpsfb;
    public HashMap<String,ArrayList> getConcatenationContainer(String[] tags, String[] tokens)
    {
        /*we will use the integer concat to remember the previous index randomly initialize it to 2
         */
        ArrayList<String> concatenationlist;
        HashMap<String, ArrayList> concatcontainer = new HashMap<>();
        int concat = -2;
        String temporary;
        String temporary2;
        String temporary3;

        for (int i = 0; i < tags.length; i++)
        {
            /*check the tags of the words
              and unify all those below as NN
             */
            if (tags[i].matches("NNS") || tags[i].matches("NNP") || tags[i].matches("NNPS") || tags[i].matches("JJ") || tags[i].matches("VBP") || tags[i].matches("NN") || tags[i].matches("FW") || tags[i].matches("UH") || tags[i].matches("RB") || tags[i].matches(".") || tags[i].matches("VBG") || tags[i].matches("VBN") || tokens[i].matches("F") || tokens[i].matches("M") || tokens[i].matches("S")) {

                tags[i] = "NN";

                        /*word concatenation should be performed only if the
                          current index is one more than previous index
                          Otherwise it implies that the tags are not occuring consecutively
                         */
                if ((concat == i - 1)) {
                    temporary2 = tokens[i];
                    temporary = tokens[i - 1];
                    temporary3 = temporary + temporary2;


                    concatenationlist = new ArrayList<>();
                    concatenationlist.add(temporary);
                    concatenationlist.add(temporary2);
                    concatcontainer.put(temporary3, concatenationlist);


                }

                /*remember the value of the previous index
                 */
                concat = i;
            }
        }

        return concatcontainer;

    }

    public void setConcatKeyContainer(HashMap<String,ArrayList> concatcontainer)
    {
        concatkeycontainer = new ArrayList<>();
        concatkeycontainer.addAll(concatcontainer.keySet());
    }

    public ArrayList<String> concatTableCheck(HashMap<String,ArrayList> concatcontainer, ArrayList<String> tablenameslist, ArrayList<String> nounlist,ArrayList<String> sampletablenames)
    {
        sc=new SimpleChecker();
        nounlistmodified=new ArrayList<>();

        /*add all the concatenated words to the concat key container
        Note: concatkey container is a map that contains the concatenated keyword as keys
        and the consitituting words as values
         ex FirstName -> First , Name
        * */

        /*
          check if the concatkeycontainer is empty if not proceed to check if any of the
          concatenated  words is   a table name
        * */

        if (concatkeycontainer.size() != 0 || concatkeycontainer != null)
        {



                        /*
                          perform simple check of the concatenated words and the tablenames
                          The simple checker will return an arraylist containing the tablenames
                          from the given list of concatenated words
                          After this operation the tablenameslist which was so far empty will contain
                          the concatenated words that are tablenames
                          ex FirstName
                         */

            tablenameslist.addAll(sc.simplechecker(concatkeycontainer, sampletablenames,1));



                        /* now as we have already identified a concatenated word
                           we remove the the 2 constituting words of the concatenated
                           words from the  nounlist
                           ex We need to remove First and Name from the nounlist
                         */
            for (String name : tablenameslist)
            {

                /*we use the concatenated word as key and retrieve the values stored against it*/
                ArrayList nounsbeforeconcat = concatcontainer.get(name);

                /*we remove the 2 constituting words from the nounlist*/
                nounlist.removeAll(nounsbeforeconcat);
            }



        }

        nounlistmodified=nounlist;

        return tablenameslist;

    }

    public void modifyColumnList(ArrayList<String> columnnameslist,HashMap<String,ArrayList> concatcontainer, ArrayList<String> nounlist,String sentencecopy, String processedstringforbetween)
    {
        nounlistmodifiedaftercolumn=new ArrayList<>();
        if (columnnameslist.size() != 0)
        {
            /*indicates concatenation check has passed */


            for (String name :columnnameslist)
            {
                                /* We need to replace the 2 constituting
                                   words with the single concatenated word
                                   in the input sentence  and remove them
                                   from the nounlist
                                 */

                ArrayList<String> arr = concatcontainer.get(name);
                sentencecopy = sentencecopy.replaceAll(arr.get(0) + " " + arr.get(1), name);
                processedstringforbetween=processedstringforbetween.replaceAll(arr.get(0) + " " + arr.get(1),name);
                nounlist.removeAll(arr);
            }
//            System.out.println("SS:"+sentencecopy);

        }
        nounlistmodifiedaftercolumn=nounlist;
        modifiedpsfb=processedstringforbetween;
        modifiedsentencecopy=sentencecopy;
    }

    public ArrayList<String> concatColumnCheck(ArrayList<String> samplecols)
    {
        sc=new SimpleChecker();
        return sc.simplechecker(concatkeycontainer,samplecols,1);
    }

    public ArrayList<String> newRemoveTableNounList()
    {
        return this.nounlistmodified;
    }

    public ArrayList<String> getColumnRemovedNounList()
    {
        return nounlistmodifiedaftercolumn;
    }

    public String getModifiedSentenceCopy()
    {
        return modifiedsentencecopy;
    }

    public String getModifiedPSFB()
    {
        return modifiedpsfb;
    }
}
