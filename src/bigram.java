
import java.util.ArrayList;
import java.util.HashMap;

/*
Performs the bigram testing on the input sentence. Bigram is a NLP technique where two adjacent words are grouped
and analysed to get the context.
 */

public class bigram extends POSTaggerExample
{
    /*Holds the list of nouns present in the sentence.*/
    public static ArrayList<String> Trainlist = new ArrayList<String>();
    /*Set of bigram word pairs*/
    public String[][] bigramizedWords = new String[100][1000];
    public String[] testwordbigram = new String[1000];

    /*Holds the actual set of words from the schema*/
    public String[] words = new String[1000];

    /*To hold the misspelt/incomplete word*/
    public static String testword;

    /*Indices for arrays*/
    public static int tracker=0,tracker2=0;

    public int[] bigramcount = new int[1000];
    public double matches = 0;
    public double denominator = 0; /*This will hold the sum of the bigrams of the 2 words*/

    /*To hold all the bigram computation results*/
    public double[] results = new double[100];

    /*To hold the final bigram correction word*/
    public static String maxbigramresultholder="";

    /*To hold the maximum value of bigram accuracy*/
    public static  double max=0;

    int testwordlength;
    HashMap<Integer,Double> thresholds=new HashMap<>(); /*Hashmap to store the thresholds in*/
    Double checkthreshold; /*To hold the specific value for the word*/

    static HashMap<String,String> bigramsentenceparser=new HashMap<>();

    /*Var to hold modified nounlist in case of columns bigram*/
    ArrayList<String> Modifiednounlist=new ArrayList<>();

    /*Var to hold modified sentence copy*/
    String Modifiedsentencecopy="";

    /*Setting up the bigram parser*/
    public void getbigramready(ArrayList<String > nounlist)
    {
        Trainlist.clear();
        for(String noun:nounlist)
        {
            Trainlist.add(noun);
        }

        /*Convert all the correct words to bigram*/

        /*	The trackers are just pointers to the position*/
        tracker=0;

        for (int i = 0; i < Trainlist.size(); i++)
        {/*For every noun that is present in Trainlist populate the bigram words matrix*/

            String gram  = Trainlist.get(i);

            int line = gram.length();

            for(int x=0;x<=line-2;x++)
            {
                /*Every pair of words is grouped together in the bigram matrix*/
                bigramizedWords[tracker][x] = gram.substring(x, x+2);
            }

            /*words stores a specific word/noun from Trainlist */
            words[tracker] = gram;
            tracker++;

            /*Store the number of bigrams for a given word in a array*/
            bigramcount[i]= line-1;

        }

        /* Setting thresholds based on word length.
        The values have been obtained after extensive testing for all cases of words of all lengths
         */
        thresholds.put(10,0.7);
        thresholds.put(9,0.7);
        thresholds.put(8,0.7);
        thresholds.put(7,0.7);
        thresholds.put(6,0.7);
        thresholds.put(5,0.8);
        thresholds.put(4,0.8);
        thresholds.put(3,0.8);

    }

    public void initialisenoun(String noun)
    {
        /*Initializing the test word/misspelt word*/
        testword=noun;
        testwordlength=testword.length(); /*Length of the test word given*/
    }


    /*Initialize the bigram for the specific word*/
    public void initialize()
    {
        tracker2=0;
        /*convert testword to bigram */

        int line = testword.length();

        testwordbigram=null;
        testwordbigram=new String[1000];
        for(int x=0;x<=line-2;x++)
        {
            /*Grouping two adjacent words to form a bigram*/
            testwordbigram[x] = testword.substring(x, x+2);
            tracker2++;
        }
    }

    /*Performs the bigram mapping of the nouns in the sentence*/
    public String bigramize()
    {
        int id=-1;
        for (int i = 0; i < Trainlist.size(); i++)
        {

            matches =0;
            denominator=0;

            /*For every set of bigramized words, if the words exist,for every word in testwordbigram
             * that is not null, if it is equal to the bigramised word in the matrix, a match has been made.*/
            for(int k = 0; k < bigramizedWords[i].length; k++)
            {
                if(bigramizedWords[i][k] != null)
                {


                    for(int j = 0; j < testwordbigram.length; j++)
                    {
                        if(testwordbigram[j] != null)
                        {
                            if(bigramizedWords[i][k].equals(testwordbigram[j]))
                            {
                                matches++;
                            }
                        }
                    }
                }
            }
            matches*=2;
            denominator = bigramcount[i] + tracker2;
            results[i] = matches/denominator;

            /* To obtain the threshold based on word length*/
            if (testwordlength>10)
            {
                /*If the word length is greater than 10, use the same word length as for 10*/
                checkthreshold=thresholds.get(10);
            }
            else
            {
                /*Else obtain based on threshold from dictionary*/
                checkthreshold=thresholds.get(testwordlength);
            }

            /*If the value of results[i] obtained from the bigram checking is greater than the threshold obtained previously and if the word length of the testword and the correction
            word are within range of each other (+/- 1), then set the corresponding correction word as the bigram solution
             */
            if ((results[i] >= checkthreshold) && (results[i] > max) && (words[i].length()==testwordlength || words[i].length()==testwordlength+1 || words[i].length()==testwordlength-1) )
            {
                max = results[i];
                maxbigramresultholder = words[i];
            }

        }
        return null;
    }

    public ArrayList<String> bigramchecker(ArrayList<String> actualnameslist,int i,ArrayList<String> nounlist)
    {
        ArrayList<String> samplewords=actualnameslist;
        ArrayList<String> bigramcontainer=new ArrayList<String>();
        ArrayList<String> residue=new ArrayList<>();

        /*To hold the bigram result*/
        String returnword=null;

        /*Outputs of the bigram function*/
        bigramcontainer.clear();

        /*Setting up the correction words for the bigram object*/
        this.getbigramready(samplewords);



        /*For each word in the nounlist, perform a bigram check*/
        for(String words:nounlist)
        {
            if (words.length()>2)
            {
                /*Initialize the bigram for the test word*/
                this.initialisenoun(words);

                /*Set up the bigram mapping for the test word*/
                this.initialize();

                /*Perform the bigram check*/
                this.bigramize();

                /*Store the return result*/
                returnword = this.maxbigramresultholder;

                /*Clear the values*/
                this.maxbigramresultholder = "";
                this.max = 0;

                if (!returnword.matches("") && returnword != null)
                {
                    bigramsentenceparser.put(words, returnword);
                    bigramcontainer.add(returnword);
                    if (i == 1)
                    {
                        residue.add(words);
                    }

                }
            }
        }
        if(i==1)
        {
            nounlist.removeAll(residue);
            Modifiednounlist=nounlist;
        }


        return bigramcontainer;
    }

    public ArrayList<String> removeMisSpelt(ArrayList<String> datalist)
    {
        datalist.removeAll(bigramsentenceparser.keySet());
        return datalist;
    }

    public String replaceForBetween (String processedstringforbetween,ArrayList<String> columnnameslist)
    {
        ArrayList<String> keyset = new ArrayList<String>();
        keyset.addAll(bigramsentenceparser.keySet());

         /* every word in the sentence will be replaced
            by the word the bigram sentence parser maps it to
            bigramsentenceparser mapping will be incorrect column name to correct column name
         */
        for (int i = 0; i < keyset.size(); i++)
        {
            String key = keyset.get(i);
            String value = bigramsentenceparser.get(keyset.get(i));

            if (columnnameslist.contains(value))
            {
                sentencecopy= sentencecopy.replaceAll("\\b" + key + "\\b",  value);
                processedstringforbetween=processedstringforbetween.replaceAll("\\b"+key+"\\b",value);

            }

        }
        Modifiedsentencecopy=sentencecopy;
        return processedstringforbetween;
    }

    public ArrayList<String> getNewNounList ()
    {
        return Modifiednounlist;
    }

    public String getModifiedSentenceCopy()
    {
        return Modifiedsentencecopy;
    }
}

