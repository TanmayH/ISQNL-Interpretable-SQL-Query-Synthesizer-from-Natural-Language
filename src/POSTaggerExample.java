import com.*;
import com.wanasit.chrono.performance.ChronoPerformanceTest;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class POSTaggerExample
{
    public static String sentencecopy="";
    public  static HashMap<String,String> revtables=new HashMap<>();
    public  static HashMap<String,String> revcol=new HashMap<>();
    static ArrayList<String> tablenameslist=new ArrayList<>();
    static ArrayList<String> datalist=new ArrayList<>();
    static ArrayList<String> columnnameslist=new ArrayList<>();

    static HashMap<String,ArrayList<String>> map=new HashMap<>();
    public static  HashMap<String,ArrayList<String>> coltodata=new HashMap<>();
    public static  ArrayList<String> nounlist=new ArrayList<>();
    public static  ArrayList<String> uselesswordlist=new ArrayList<>();
    public static  ArrayList<String> hypernymsuseless=new ArrayList<>();
    public static bigram b; /*Bigram Obhect*/
    public static SimpleChecker sc; /*SimpleChecker Obhect*/
    public static Concatenation cc;
    public static Hypernym hh;
    public static getsynonym syntable;




    public static void main(String[] args) throws  Exception
    {
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File("outputs.txt")));
        printWriter.close();//Clearing the buffer
        InputStream tokenModelIn = null;
        InputStream posModelIn = null;
        int counter = 0;


        /*will contain the enriched input statement */
        String st = "";

        /*will contain the unmodified input statement */
        String unmodified="";


        /*will contain the string to be sent for between case */
        String processedstringforbetween="";



        /*creating an object of xml parser to access the xml file */
        XMLParser xml=new XMLParser();

        /*the pos tagger is often inaccurate at times hence some words need to be filtered
          out from the input stream so as to prevent them from interfering with important data
          check map.xml with appendix tag for these words
         */
        xml.input="appendix";
        uselesswordlist.addAll(xml.xmlParser());

        /*loading the words for which hypernymn checks have to be
         check map.xml for these words
         ignored*/
        xml.input="hypernymsappendix";
        hypernymsuseless.addAll(xml.xmlParser());



        try
        {
            File f = new File("Sample_Input.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));


            /*reading input from sample_input.txt begins from here */

            /*reading input line by line */
            while ((unmodified = br.readLine()) != null)
            {
                /*the input enricher class wiil add additional information to the input sentence by replacing the
                  keyword defined in InputEnrichStatements.json
                  example show female customers ---> show gender is F customers
                 */
                InputEnricher inputEnricher=new InputEnricher(unmodified);
                st = inputEnricher.finalstring;
                st=st.replaceAll("\\?","");



                /*clearing all the buffers before execution of the next query */
                tablenameslist.clear();
                columnnameslist.clear();
                map.clear();
                datalist.clear();
                coltodata.clear();
                nounlist.clear();

                b=new bigram();
                sc=new SimpleChecker();
                cc= new Concatenation();
                syntable = new getsynonym();
                hh=new Hypernym();



                st=st.toLowerCase();
                /*postagging sentence will hold the sentence that will be given as input to the pos tagger */
                String postaggingsentence=st;


                /*performing a series of tokenising operations on the sentence to be pos tagged with the view
                 that these tokens do no interfere during pos tagging
                 */
                postaggingsentence=postaggingsentence.replaceAll("\\?","");
                postaggingsentence=postaggingsentence.replaceAll("\\$","");
                postaggingsentence=postaggingsentence.replaceAll(" \\. ","") ;
                postaggingsentence=postaggingsentence.replaceAll(","," ");

                /*to remove whitespaces at the beginning and the end */
                postaggingsentence=postaggingsentence.trim();



                /*to detect whether any data is contained in '' */
                Pattern p= Pattern.compile("\\'([^\']*)\\'");
                Matcher m=p.matcher(st);

                /*to extract words contained in quotes as is */
                /*Any words contained inside quotes are extracted and stored as is
                  and do not undergo pos tagging
                 */
                while(m.find())
                {

                    /*whatever data is inside quotes it be converted to
                      lowercase and will be loaded into noun */
                    String quotedword=m.group(0).toLowerCase();

                    /*The noun will still be containing '' surrounding it,
                      therefore to replace the quotes we perform the following
                     */
                    quotedword=quotedword.replaceAll("'","").toLowerCase();
                    /*Just to remove the starting and trailing whitespace around the word
                      if any
                     */
                    quotedword=quotedword.trim();

                    nounlist.add(quotedword);

                    /*In order to prevent the postagger to tag the quoted words we replace it
                      in the pos tagging sentence with blank
                      */
                    postaggingsentence=postaggingsentence.replaceAll(m.group(0).toLowerCase(),"");

                }




                /*After performing all the above modifications for the pos tagging sentence we
                 initialize the processedstringforbetween with the modified pos tagging sentence
                 */
                processedstringforbetween=postaggingsentence;





                /*part of code snippet replaces all occurences of date in text format with date as per SQL format*/
                /*the chronoperformancetest object will return a text date if a date is
                  present in the text st
                  the returened text will is separated by #
                  that is it contains date_inidicative_text_of_the_sentence#date


                */
                ChronoPerformanceTest obj1=new ChronoPerformanceTest(st);
                String txtdate=obj1.finalstring;
                String text="";
                String dateparsed="";
                String month="";
                String year="";


                /*set to true when day month year or day month  is present in NL */
                boolean datedetect=false;

                /*set to true when only month or only year or month year present */
                boolean datedetectcase2=false;


                /*
                the following snippet is to prevent date_indicative_text_of_the_sentence from getting
                into the pos tagger
                 */
                if(txtdate!=null&&!(txtdate.matches("")))
                {

                    String[] splittextdate = txtdate.split("#");
                    text = splittextdate[0];
                    dateparsed = "'"+splittextdate[1]+"'";
                    datedetect=true;

                    /*clear the date_indicative_text_of_the_sentence from st */
                    st=st.replaceAll(text," ");

                    /*replace the NL text with date only for processedstringforbetween */
                    processedstringforbetween=processedstringforbetween.replaceAll(text,splittextdate[1]);

                    /*clear the date_indicative_text_of_the_sentence from postaggingsentence */

                    postaggingsentence=postaggingsentence.replaceAll(text,"");
                }







                /*This is built to further the abilities of the chrono module
                  as the chrono module does not have parsers to detect
                  only month or only year or month year
                */

                /*This code snippet will be executed only if
                  chrono fails to detect date
                 */
                if(!datedetect)
                {


                    /*the month year date format class
                    will initialize the final string variable to
                    some value if it can detect a month or a year
                    or both
                     */
                    MonthYearDateFormat mydf = new MonthYearDateFormat(st);


                    if (mydf.finalstring != null && !mydf.finalstring.matches(""))
                    {

                        /*if month is found mydf.month will carry the integer associated
                         with that month
                         */
                        month=mydf.month;
                        /*If year is found mydf.year will carry the integer year
                         * */
                        year=mydf.year;


                        /*This is to indicate that the second check for date has passed */
                        datedetectcase2=true;

                        dateparsed=mydf.finalstring;


                        if(!month.matches("")&&month!=null)
                        {

                            /*the year needs to be replaced in st because
                              it will be present as an integer and may be detected
                              as integer during further processing of the sentence
                             */
                            st = st.replaceAll(year, "");

                           /*As we do not want the pos tagger to tag either the NL month
                            or the year we replace it with blank
                             * */

                            postaggingsentence=postaggingsentence.replaceAll(year,"");
                            postaggingsentence=postaggingsentence.replaceAll(month,"");
                        }
                        else
                        {
                            if(!year.matches("")&&year!=null)
                            {
                                /*
                                   This is to check whether the pos tagger contains
                                   the word year along with the integer
                                   As per our design any 4 digit integer will be considered as
                                   as year if and only if it is preceded by year
                                   ex year 1940

                                 */


                                /*this part of the code checks if year, last year or previous year
                                  is present in the sentence because these words if present need to be replaced

                                 */

                                /*A special scenario arises when an indirect reference to year is made
                                  like last year or previous year
                                  These 2 phrases need to be replaced to prevent any of their words from
                                  accidentally getting tagged
                                * */
                                if(postaggingsentence.contains("year "+year)||postaggingsentence.contains("last year")||postaggingsentence.contains("previous year"))
                                {
                                    if(postaggingsentence.contains("year "+year))
                                    {
                                        postaggingsentence = postaggingsentence.replaceAll("year " + year, "");
                                    }
                                    else if(postaggingsentence.contains("last year"))
                                    {
                                        postaggingsentence=postaggingsentence.replaceAll("last year","");
                                    }
                                    else if (postaggingsentence.contains("previous year"))
                                    {
                                        postaggingsentence=postaggingsentence.replaceAll("previous year","");
                                    }
                                }
                                else
                                {
                                    /*if either of the above conditions are not satisfied then
                                      we conclude that second check for date has failed
                                     */
                                    datedetectcase2=false;
                                }
                            }
                            else
                            {
                                datedetectcase2 = false;
                            }
                        }
                    }
                }

                /*convert the input sentence into lower case for case insensitive search*/
                st=st.toLowerCase();

                /*to check if it is a between case
                The notable characteristic of between case  of date
                is that the chrono class will detect 2 dates and return
                them in chronological order with an and between them
                ex input: 12th January 2004 and 15th January 2005
                   return 2004-01-12 and 2005-01-15
                * */



                /*The boolean variable to remember whether between
                  is present in the sentence
                 */
                boolean between=false;


                ArrayList<String> dataforbetweencase=new ArrayList<>(); /*This arraylist will store the 2 dates in between case of date */

                /*We check the contents of dateparsed
                  and check if there is an and in it
                 */
                if(dateparsed.contains("and"))
                {
                    /*if and is present then 2 dates are present in the
                    sentence and we need to to separate them and store them
                     */
                    between=true;

                    String dateforbetween=dateparsed;
                    dateforbetween=dateforbetween.replaceAll("'","");
                    String[] dateslistforbetween=dateforbetween.split(" and ");
                    /*Add the dates to the dataforbetween case arraylist */
                    dataforbetweencase.addAll(Arrays.asList(dateslistforbetween));


                    /*It can happen that due to detection of the date in the input
                      the first check for date (chrono) or the second check for date
                      (Month Year) may have passed
                      Therefore to invalidate those we set datedetect and datedetectcase2 to false
                      Therefore the system forgets that date was present instead remembers that
                      that between_case_of_dates is present
                     */
                    datedetect=false;
                    datedetectcase2=false;

                }
                /*between check ends here */




                /*making copies of sentence for further use */

                sentencecopy=st;






                /*POS tagging the sentence*/
                /*each token will be associated with a tag as per penn treebank model */


                /*this is the beginning of the pos tagger code
                 The code can be followed by using the standard documentation
                 of performing pos tagging using OPEN NLP
                 */
                tokenModelIn = new FileInputStream("en-token.bin");
                TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
                Tokenizer tokenizer = new TokenizerME(tokenModel);
                /*The pos tagging sentence is passed for pos tagging
                 NOTE: The pos tagging sentence is the one that has been generated by performing the above
                 stated additions and deletions
                 It is not the same as the input sentence therefore lesser/more words may be tagged
                 */
                String tokens[] = tokenizer.tokenize(postaggingsentence);
                posModelIn = new FileInputStream("en-pos-maxent.bin");
                POSModel posModel = new POSModel(posModelIn);
                POSTaggerME posTagger = new POSTaggerME(posModel);
                String tags[] = posTagger.tag(tokens);
                double probs[] = posTagger.probs();


                /*Printing the tokens along with their tags
                 *  */
                System.out.println("Token\t:\tTag\t:\tProbability\n---------------------------------------------");
                for (int i = 0; i < tokens.length; i++)
                {
                    System.out.println(tokens[i] + "\t:\t" + tags[i] + "\t:\t" + probs[i]);
                }

                System.out.println("\n\n");




                /*code snippets below perform 3 important tasks
                 *perform concatenation check
                 *collect keywords from the pos tagging sentence (*unify all tags as NN (all tags are empirically found to yield keywords)
                 *collect numbers if any from pos tagging sentence
                 *Intution: Numbers if present in the input sentence
                  cannot be column names or table names and therefore have to be data
                 *Hence they are segregated in the beginning itself
                 */


                /* code logic to perform concatenation check

                 *club words that come consecutively and have these tags as single words ex yearlyinome
                 *Also maintain the 2 words that create the word in an arraylist  ex yearly+income
                 *Store them in a hashmap key = concatenated word value = list containing the 2 composing words

                 */
                HashMap<String, ArrayList> concatcontainer = new HashMap<>();
                concatcontainer=cc.getConcatenationContainer(tags,tokens);

                for (int i = 0; i < tags.length; i++)
                {



                    /*if numbers or date are detected in the sentence then store them separately as data in  datalist */
                    if (tags[i].matches("CD"))
                    {
                        /*the tag CD is given to integers
                        Integers in the sentence imply that it has to be data
                        Note: This was the reason we performed some pre-processing on the text
                        to remove the date which could have also have been numbers

                         */
                        /*the datalist stores all the data */
                        datalist.add(tokens[i]);
                    }



                   /*call the mapper function by passing
                    the tags and tokens as arguments
                    The mapper function will insert the tags as keys of the hashmap 'map'
                    and tokens as the values associated with that key
                    In our case it will mantain a hashmap containing the NN key only(as all our tags are resolved to that )
                    and all the tokens
                    */
                    mapper(tags[i], tokens[i]);

                }


           /* As from above the mapper function will populate the
              the hashmap map
              The next step is to collect all the words that have the tag NN and store them into the nounlist
            */
                if(map.get("NN")!=null)
                    nounlist .addAll( map.get("NN"));



             /*
               Before proceeding to the next phase we need to ensure that we are able to extract sufficient
               keywords out of the text thus we check if the nounlist has any values
               If it does not have contain any values then we cannnot proceed with other steps
               therefore  we raise the exception: RE-ENTER QUERY :POSSIBLY GRAMMAR IS WRONG : TAGGER IS UNABLE TO FIND APPROPRIATE TOKENS

              */

                if (nounlist != null)
                {

                    /*A few words require to be removed
                     from the nounlist. Most of these words
                     are common words that are used while asking a
                     query in natural language and do not add any value to the query
                     All these words can be found in the appendix tag of the resource file
                     map.xml
                     */
                    nounlist.removeAll(uselesswordlist);



                    /*we create a connection object that will establish the connection to the
                     the json files (tables.json) and (columns.json) and read them respectively
                     */
                    Connection ob=new Connection();

                    try
                    {
                        /*this part of the code snippet will call the read tables method in connection
                          and perform necessary initializations
                         */
                        ob.readTables();
                        /*
                          the revtables is a hashmap containing the logical table names as keys and the actual table
                          names as values.
                         */
                        revtables=ob.revtables;

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }



                    /*
                    this array list holds the logical table names
                    */
                    ArrayList<String> sampletablenames = new ArrayList<>();



                    /*populate the smapletablenames arraylist with the logical table names
                     * */
                    sampletablenames.addAll(ob.tablenameslist);



                    cc.setConcatKeyContainer(concatcontainer);
                    tablenameslist.addAll(cc.concatTableCheck(concatcontainer,tablenameslist,nounlist,sampletablenames));
                    nounlist=cc.newRemoveTableNounList();


                    /*concat check is considered as the first level check for table names
                     * */
                    /*After concat check the other four level of checks are  as below with their descriptions*/




                    /*simple check for tables
                    Will perform a one-one correspondence check
                    ex if nounlist contains gender, age ,mary
                    And the logical table names are age,gender ,...
                    Then the simplechecker will return gender ,age and they will be added to tablenameslist
                    The second argument 0 inidicates that the simple check is being called for table name
                    check and should not remove the logic words  from the nounslist as they need to retained
                    for column check.
                    * */
                    tablenameslist.addAll(sc.simplechecker(nounlist, sampletablenames,0));


                    /* bigram check for tables here
                    Bigram check is mainly used to check and correct spell errors
                    Example if nounlist contains customrs
                    Bigram check will check customrs with customers
                    If the degree of bigram similarity between words is > threshold value
                    it will consider customers as the actual value and return an arraylist
                    containing the corrected actual values.
                    The second argument 0 inidicates that the bigram check is being called for table name
           #         check and should not remove the incorrect words or residue from the nounslist

                    * */

                    tablenameslist.addAll(b.bigramchecker(sampletablenames, 0,nounlist));

                    /*Synonym check for tables
                     * This check relies on a standard corpora to generate the synonyms of the
                     *logical table names and compare them with the words in the nounlist
                     * It will return an array list of table names found through this check
                     * This check is found redundant in most cases, So in future it should either be
                     removed or replaced
                      The second argument 0 inidicates that the synonym check is being called for table name
           #         check and should not remove the logic  words  from the nounslist
                       */

                    ArrayList<String> synonyms = syntable.run(nounlist, sampletablenames);
                    tablenameslist.addAll(synonyms);



                    /*hypernym check for tables
                    * This check relies on finding ancestor of the words in the nounlist. It checks if the ancestor of
                      any of the words in the nounlist is a logical table name. If yes then the logical table name is returned
                      For example it will check Australia->(in nounlist) with country (->in logical table names list)
                      As country is the ancestor of Australia country will be returned.
                      The second argument 0 inidcates that the bigram check is being called for table name
                      check and should not remove the logic words from the nounslist
                    */
                    ArrayList<String> hypernyms = new ArrayList<>();
                    hypernyms = hh.hypernymchecker(nounlist, sampletablenames, 0,hypernymsuseless,coltodata);
                    tablenameslist.addAll(hypernyms);
                    coltodata=hh.getAfterTableColToData();


                    /* This completes the 5 layers of checking for table names
                     * Now the table names list contains the logical  table names*/








                  /* Multiple logic words in the table names list may be referencing to the same table.
                    Hence we need to make sure that even though multiple words are
                    given only a single table entry corressponding to them are added to the table names list.
                    Example if the sentence contains client and customers then we need
                    to ensure that only one copy of  customer table logic words is present in the
                    actual tablenameslist . The following code snippet achieves the same
                    */


                    /*create an empty set tablenm to hold the actual table  name
                     *  */
                    Set<String> tablenm = new HashSet<>();

                  /*create an arraylist unique logic to hold the unique logical table
                    names
                   */
                    ArrayList<String> uniquelogic=new ArrayList<>();






                    int x,y;
                    for(String name:tablenameslist)
                    {

                        /*retrieve the size of the actual table name set initially */

                        x=tablenm.size();
                        /*retrieve the actual table name from the logical table names
                          using revtables map and add it to the set
                         */
                        tablenm.add(revtables.get(name));
                        /*check the size of the tablenm set after addition */



                        y=tablenm.size();
                        /*check if the size of the set has changed after addition
                         */


                        if(x!=y)
                        {

                            /*if the set size has changed add a single logical table name from tablenames
                            list as the logical table for the actual table.
                            Note If any more logical tables are there for the same actual table
                            then  they will not be added as the size of the set wont change then */
                            uniquelogic.add(name);

                        }



                    }






                /*After applying the above filter the tablenames list is cleared and
                  re-populated with only single logic name for each actual table name
                  that was detected.
               */



                    tablenameslist.clear();
                    tablenameslist.addAll(uniquelogic);


                 /*Similarly we create another arraylist called actual table list to hold the
                   actual table names as shown below.
                   TO this new arraylist we add the contents of the set tablnm which contains unique
                   actual table names
                  */
                    ArrayList<String> actualtablelist=new ArrayList<>();
                    actualtablelist.clear();
                    actualtablelist.addAll(tablenm);








                   /*We now create an inner join object
                     and pass into its constructor  the actual table list
                     This class will internally check if the given set of tables
                     can be joint directly  using  primary keys
                     Use warshalls algorithm it will also try to find an inderect
                     path to join 2 tables through using composite primary keys
                    */
                    InnerJoin inj=new InnerJoin(actualtablelist);



                    /*create a new array list to hold the actual table names of the tables that have been
                      joint
                     */
                    ArrayList<String> jointables = new ArrayList<>();
                    jointables.addAll(inj.jointables);







                   /*
                      the array list logic table names will hold the logic table names of only the
                      tables that can be joint
                   */
                    ArrayList<String> logictablenames=new ArrayList<>();

                    /*
                    iterate throught the tablenameslist and retrive the actual table associated
                    with the logical table name
                    Check if the join table list contains the retrieved actual table.
                    If it does then add that logic table name to the logic table names list
                     */
                    for(String str:tablenameslist)
                    {
                        if(jointables.contains(revtables.get(str)))
                        {
                            logictablenames.add(str);
                        }

                    }






                    /*
                    If we are able to get a non empty list containing more than
                    one element it means that the sentence
                    contains 2 or more joinable tables and hence we proceed with it
                    as join table case

                     */


                    boolean joincase;

                    /*This array list will hold the actual table names as present in the database */
                    ArrayList<String> actualtablenamelist =new ArrayList<>();


                    if ((jointables.size() > 1)||(tablenameslist.size()>0))
                    {
                        /*The code below is used  for query building  under join case*/

                        if(jointables.size()>1)
                        {
                            /*indicates that it is  a join condition */
                            joincase =true;
                            /*clear the table names list*/
                            tablenameslist.clear();
                            /*add all the joinable table names to the table names list*/
                            tablenameslist.addAll(logictablenames);

                            /*in case of join we are interested in those tables that are joinable */
                            actualtablenamelist.addAll(jointables);

                        }
                        else
                        {


                            /*indicates that it is not a join condition */
                            joincase = false;


                               /*the assumption is that if it is not a join condition then we can
                               proceed with the first table detected
                                */
                            String maintable =tablenameslist.get(0);

                            tablenameslist.clear();

                            tablenameslist.add(maintable);


                            /*will hold the actual table name of the main table */
                            actualtablenamelist.add(revtables.get(maintable));



                        }


                        /*to hold the logical column names to test the nounlist against*/
                        ArrayList<String> samplecols=new ArrayList<>();



                   /*for the purpose of recovering date columns from date.json file later
                      we get the actual table names  that are being used */

                        /*will hold the actual table name of the table containing date */
                        ArrayList<String> datetablelist=new ArrayList<>();
                        datetablelist.clear();

                       /*in case of join condition all the actual names will be added
                         in case of non-join condition only the main table name will
                         be added

                        */
                        datetablelist.addAll(actualtablenamelist);








             /*
             ob is an object of the connection class
             We pass tablenameslist that contains the logical names of all the  joinable tables/ main table
             in case of non-join along with revtables map (a map of logic -> actual) and
             along with actual table names
             to retrieve all the logical columns names that are present in the current table
              */

                        ob.readColumns(tablenameslist,revtables,actualtablenamelist);
                       /* a map that maintains the mapping of logical column name to
                          actual column
                        */
                        revcol=ob.revcolumns;
                        /*will initialize the sample columns list
                        which can be used as a sample space of all
                        the logical column names to be used for further check
                         */
                        samplecols.addAll(ob.columnnameslist);







                        /*to maintain reverse mapping of columns as well */





                /* Now we take the columns in the sentence and all the columns
                   in the  detected tables through 5 levels of checks
                 * concatenation ,simple,bigram ,synonyms, hypernyms *
                 * At each stage if we are able to find a column corresponding to the remaining nouns we remove that noun
                  from the nounlist so as to prevent it from interfering with data*/




                /*start of concatenation check
                 * pass the concatkeycontainer along with the sample columns with integer
                  value =1 indicating that if check is succesful then remove testing words
                  from the nounlist*/
                        columnnameslist.addAll(cc.concatColumnCheck(samplecols));


                      /*if the concatenation check has passed
                        the concatenated word will be added to
                        the columnnameslist and hence the size of that will
                        be greater than 0

                       */
                        cc.modifyColumnList(columnnameslist,concatcontainer,nounlist,sentencecopy,processedstringforbetween);
                        sentencecopy=cc.getModifiedSentenceCopy();
                        processedstringforbetween=cc.getModifiedPSFB();
                        nounlist=cc.getColumnRemovedNounList();







                        /*simple check for columns */

                        if (nounlist.size() != 0)
                        {
                            columnnameslist.addAll(sc.simplechecker(nounlist, samplecols,1));
                        }

                        /*bigram check for columns */
                        if (nounlist.size() != 0)
                        {

                            columnnameslist.addAll(b.bigramchecker(samplecols, 1,nounlist));
                            nounlist=b.getNewNounList();


                        }



                        /*synonym check for columns */
                        if (nounlist.size() != 0)
                        {
                            getsynonym colsynonym = new getsynonym();
                            ArrayList<String> aka = colsynonym.run(nounlist, samplecols);
                            columnnameslist.addAll(aka);
                            nounlist.removeAll(aka);
                        }




                        /*hypernym check for columns */

                        if (nounlist.size() != 0)
                        {
                            columnnameslist.addAll(hh.hypernymchecker(nounlist, samplecols, 1,hypernymsuseless,coltodata));
                            coltodata=hh.getAfterTableColToData();

                        }






                        /*initialize the data list to hold all the nouns */
                        datalist.addAll(nounlist);

                        /*remove the column names from the datalist */
                        datalist.removeAll(columnnameslist);

                        /*remove the table names from the data list */
                        datalist.removeAll(tablenameslist);

                        /*remove the mis-spelt words that have been corrected by bigram
                        from datalist
                         */

                        datalist=b.removeMisSpelt(datalist);



                        /*remove the words contained in data appendix from datalist */
                        xml.input="dataappendix";
                        List<String> dataappendix=new ArrayList<>();
                        dataappendix=xml.xmlParser();
                        datalist.removeAll(dataappendix);


                         /*
                        the keyset of the sentence parser  will hold
                        the words that are to be replaced for example
                        if sentence parser contains cstomers->customers
                        then cstomers will be present in the keyset
                         */
                        processedstringforbetween=b.replaceForBetween(processedstringforbetween,columnnameslist);
                        sentencecopy=b.getModifiedSentenceCopy();




                        /* As with the case of tables there can be 2 or more logical
                        names used in the sentence that in reality refer to a single
                        actual column
                        Hence  to introduce uniformity we need to maintain a single copy
                        of the column names in the sentence
                         */



                        /*A set that will contain the actual column names */
                        HashSet<String> uniquecol=new HashSet<>();

                        /*A  list that will hold only one logical column name for each actual column */
                        ArrayList<String> uniquecombinedcolslist=new ArrayList<>();


                        /*
                           A map that will contain the Actual columnn name mapped to single logical column name
                           to introduce uniformity

                         */

                        HashMap<String,String> SingleActualColumnToSingleLogicalColumn =new HashMap<>();
                        int l,r;
                        for(String name:columnnameslist)
                        {
                            l=uniquecol.size();
                            uniquecol.add(revcol.get(name));
                            r=uniquecol.size();

                            /*If a logical name referencing to a new actual column is
                              present following code will be invoked
                             */
                            if(l!=r)
                            {

                                SingleActualColumnToSingleLogicalColumn.put(revcol.get(name),name);

                                uniquecombinedcolslist.add(name);

                            }
                            else
                            {
                                String singlelogiccolumnnamereplacement=SingleActualColumnToSingleLogicalColumn.get(revcol.get(name));
                              /*Introduce uniformity in the sentence copies by replacing muliple logic words referencing to the same column
                               with single logic word for that column*/
                                sentencecopy= sentencecopy.replaceAll("\\b" + name + "\\b",singlelogiccolumnnamereplacement);
                                processedstringforbetween= processedstringforbetween.replaceAll("\\b" + name + "\\b",singlelogiccolumnnamereplacement);

                            }

                        }



                        columnnameslist.clear();
                        /*add the set of unique logical names with one one mapping to
                        actual column names to column names list
                         */
                        columnnameslist.addAll(uniquecombinedcolslist);












                       /*The variable res will contain the SQL statement that
                         is constructed
                        */
                        String res = "";


                        /*
                        filterset is just used as filter to remove any duplicates from
                        table, column or datalist
                         */
                        HashSet<String> filterset = new HashSet<>();
                        filterset.addAll(tablenameslist);
                        tablenameslist.clear();
                        tablenameslist.addAll(filterset);
                        filterset.clear();

                        filterset.addAll(actualtablelist);
                        actualtablelist.clear();
                        actualtablelist.addAll(filterset);
                        filterset.clear();



                        filterset.addAll(columnnameslist);
                        columnnameslist.clear();
                        columnnameslist.addAll(filterset);
                        filterset.clear();

                        filterset.addAll(datalist);
                        datalist.clear();
                        datalist.addAll(filterset);
                        filterset.clear();




                        /*probable date list  will hold all the date columns in the
                         joinable tables or table(non-join)
                         */
                        ArrayList<String> probabledatelist=new ArrayList<>();

                        if(datedetect||datedetectcase2)
                        {
                            date_column_extractor dateob = new date_column_extractor();
                            for(String str:actualtablelist)
                            {
                                ArrayList<String> datelogicalcolumns=dateob.find_date(str);
                                probabledatelist.addAll(datelogicalcolumns);

                            }

                        }




                        /*initialize the default date column to empty string*/
                        String defaultdatecolumn="";

                        if(probabledatelist.size()>0)
                        {
                            /*store the first value as default date */
                            defaultdatecolumn+=probabledatelist.get(0);
                        }
                        probabledatelist.retainAll(columnnameslist);

                        if(probabledatelist.size()==0)
                        {
                            /*if no columns have been found add
                             default date column to probable date
                             list
                             */
                            if(!defaultdatecolumn.matches(""))
                                probabledatelist.add(defaultdatecolumn);
                        }


                        /*this code snippet is based on the intution that
                         there can be only one date column*/
                        ArrayList<String> singledatecolumnisolator=new ArrayList<>();
                        singledatecolumnisolator.addAll(probabledatelist);
                        String datecolumn="";



                        /*the following code helps find date column
                         *nearest to date in NLP*/

                        if(singledatecolumnisolator.size()!=0&&singledatecolumnisolator!=null)
                        {
                            /*randomly chosen high initialization of min */
                            int min = 5000;

                            for (String str : singledatecolumnisolator)
                            {
                                dateparsed=dateparsed.replaceAll("'","");

                                /*find index of date column in the input sentence */
                                int i = processedstringforbetween.indexOf(str);
                                /*find index of date in input sentence */
                                int j = processedstringforbetween.indexOf(dateparsed);

                                /*calculate the difference and check if lesser than min

                                 */
                                int check = j - i;

                                if (Math.abs(check) < min)
                                {
                                    /*if true then init min  with the absolute value of
                                     difference and init date column with least result
                                     */
                                    min = check;
                                    datecolumn="";
                                    datecolumn+=str;
                                }


                            }
                        }








                        /* ADDITION OF SELECT */
                        res = res + "SELECT";

                        /*boolean variables that will be set when group operations
                          are detected
                         */

                        boolean groupoperations=false;



                            /*checks and appends count(colname) or sum(colname) to the query
                             lookup documentation of CountSum2 class for more implementation details */
                        CountSum2 countSum =  new CountSum2 (sentencecopy, columnnameslist,revcol,tablenameslist);
                        if (countSum.finalstring != "")
                        {
                            res += " " + countSum.finalstring;
                            groupoperations=true;
                        }

                           /*checks and appends AVG(colname) to the query
                           lookup documentation of the Average class for more details
                            */
                        Average average = new Average(sentencecopy, columnnameslist);
                        if (!average.finalstring.matches(""))
                        {

                            if(groupoperations)
                            {
                                res+=" , ";
                            }
                            res += " " + average.finalstring;
                            groupoperations=true;
                        }

                          /*checks for the presence of min-max operation in the sentence
                          look up documentation of the MinMax class for more details
                           */
                        Minmax minmax = new Minmax(sentencecopy, columnnameslist);
                        if (!minmax.finalstring.matches(""))
                        {
                            if(groupoperations)
                            {
                                res+=" , ";
                            }

                            res += " " + minmax.finalstring;
                            groupoperations=true;
                        }


                            /*Check for distinct if and only if no group operations
                              are present
                              * */
                        if(columnnameslist.size()!=0&&columnnameslist!=null&&!groupoperations)
                        {
                            Distinct distinct = new Distinct(sentencecopy);
                            if (!distinct.finalstring.matches(""))
                            {
                                res += " " + distinct.finalstring;

                            }

                        }









                        /*operation check ends here if no operation on columns is detected
                         this indicates select all the columns or select specific columns*/



                          /*create a duplicate copy of the current column names list
                          called native columns
                         */
                        ArrayList<String> nativecolumns=new ArrayList<>();
                        nativecolumns.addAll(columnnameslist);




                        /*col to data is a map that is used to store the ancestors
                          of the data items in hypernyms
                          It stores ancestors as the key and the data that led to it as value

                          coltodata generally maps where clause equal to relation
                          and hence we need to remove those columns from entering select
                          which we do using the following code snippet

                         */

                        /*first add all the columns mapped by hypernyms
                        into Aux
                         */
                        ArrayList<String> Aux = new ArrayList<>();
                        Aux.addAll(coltodata.keySet());

                        /*remove Aux from columnnameslist */
                        columnnameslist.removeAll(Aux);






                       /*checks if sentence specifies like operation
                       if it does have like operation then remove the like
                       data item from the datalist
                       look up documentation of like for more details
                       */
                        Like like=new Like(sentencecopy,columnnameslist);
                        datalist.removeAll(like.datalist);


                        /*the data for between case primararily contains date data
                        which was stored separately to prevent  interference
                        with other data
                        * */
                        datalist.addAll(dataforbetweencase);

                        /*checks if the operation inidicates between operation
                          check out implementation details of between for more details

                        */




                        Between between1=new Between(processedstringforbetween,datalist,tablenameslist,revtables,columnnameslist,revcol);




                        /*remove all the between mapped data from the datalists */
                        datalist.removeAll(dataforbetweencase);
                        datalist.removeAll(between1.datalist);





                        /**Before where clause can be executed we must ensure that
                        the substrings which result in a condition on group functions
                        are removed .

                         *For example I/P show product id  having average price greater than 2000 group by product category
                         We must modify the sentence to replace average price greater than 2000 so that it does not interfere
                         with where clause checks

                         *The having clause  identifies group operations and conditions on them
                         * example AVG( column_name) > 200
                         * Therefore the columnnames list supplied to having clause must have only the column names that are included
                           in group operations
                         */



                        ArrayList<String> having_columns = new ArrayList<>();
                        having_columns.addAll(countSum.usedcols);
                        having_columns.addAll(minmax.usedcols);
                        having_columns.addAll(average.usedcols);


                        Having having=new Having(sentencecopy,having_columns,tablenameslist,groupoperations);


                        /**
                         * whereclause modified is a reduced string that id formed by replacing having
                           substrings*/

                        String whereclausemodified=""+sentencecopy;
                        ArrayList<String> having_substrings=having.getSubstrings();
                        for(String substring:having_substrings)
                        {
                            whereclausemodified=whereclausemodified.replaceAll(substring," a a a a a ");
                        }



                        /**checks for the presence of where clause in the data
                           check implemenatation details in the conditional
                           where class
                          *remove all group operations columns as they are handled by having
                         */


                         nativecolumns.removeAll(average.usedcols);
                         nativecolumns.removeAll(minmax.usedcols);
                         nativecolumns.removeAll(countSum.usedcols);



                        conditionalwhere whereclause=new conditionalwhere(nativecolumns,datalist,whereclausemodified,coltodata,revcol);



                        /*checks for the presence of order by operation
                        check implementation details in the OrderBy
                         */
                        OrderByClause orderby = new OrderByClause(sentencecopy, columnnameslist);






                       /*Remove those columns that are associated with a future operation
                       such as like, whereclause, order by ,between  and date column which is a
                       part of where clause but handled separately
                       */
                        if(datecolumn!=null&&!datecolumn.matches(""))
                            columnnameslist.remove(datecolumn);

                        columnnameslist.removeAll(like.processedcolumns);
                        columnnameslist.removeAll(whereclause.columnsprocessed);
                        columnnameslist.removeAll(orderby.columnsprocessed);
                        columnnameslist.removeAll(between1.columnsprocessed);
                        columnnameslist.removeAll(countSum.usedcols);
                        columnnameslist.removeAll(average.usedcols);
                        columnnameslist.removeAll(minmax.usedcols);



                        /*code snippet that checks whether all is present in the sentence */
                        boolean localflag=false;

                        /*allsynonyms is a list that will contain the synonyms of all */
                        List<String> allsynonyms=new ArrayList<>();

                        /*synonyms of all will be returned by xmlParser() */
                        xml.input="All";
                        allsynonyms=xml.xmlParser();


                        /*to check whether the operation is SELECT *  */

                        if (columnnameslist == null || columnnameslist.size() == 0&&(!groupoperations))
                        {
                            res += " *";
                            localflag=true;
                        }

                        /*to check whether any synonym of all is present in the sentence which may indicate select * */
                        if(!localflag&&columnnameslist.size()==0&&(!groupoperations))
                        {

                            for(String str:allsynonyms)
                            {
                                if(sentencecopy.contains(str))
                                {
                                    localflag=true;
                                    res+=" *";
                                }
                            }
                        }





                        if(!localflag)
                        {

                            if(!groupoperations)
                            {
                                columnnameslist.addAll(like.processedcolumns);
                                columnnameslist.addAll(orderby.columnsprocessed);
                                columnnameslist.addAll(between1.columnsprocessed);
                                columnnameslist.addAll(singledatecolumnisolator);
                                columnnameslist.addAll(Aux);
                                columnnameslist.addAll(whereclause.columnsprocessed);

                                HashSet<String> filter = new HashSet<>();
                                filter.addAll(columnnameslist);
                                columnnameslist.clear();
                                columnnameslist.addAll(filter);
                            }

                            Select select = new Select(columnnameslist);

                            if(groupoperations)
                            {
                                if(columnnameslist.size()>0)
                                {
                                    res += " , " + select.finalstring;
                                }
                                else
                                {
                                    res +=" "+select.finalstring;
                                }
                            }
                            else
                            {


                                res+= " "+select.finalstring;
                            }

                        }






                        if(joincase)
                        {
                            res += inj.finalstring;
                        }
                        else
                        {
                            res += "  FROM " + tablenameslist.get(0);
                        }




                        boolean whereappended=false;


                        /* appending like clause  */
                        if(!like.finalstring.matches(""))
                        {
                            datalist.removeAll(like.datalist);
                            whereappended=true;
                            res+=" WHERE "+like.finalstring;
                        }



                        /*appending between clause */
                        if(!between1.finalstring.matches(""))
                        {
                            if(whereappended)
                            {
                                res+=" AND ";
                            }
                            else
                            {
                                res+=" WHERE ";
                                whereappended=true;

                            }
                            res+=" "+between1.finalstring+" ";
                        }




                        /* appending where clause */
                        if (!whereclause.finalstring.matches(""))
                        {
                            if(whereappended)
                            {
                                res += " AND " + whereclause.finalstring;
                            }
                            else
                            {


                                res +=" WHERE "+whereclause.finalstring;
                                whereappended=true;
                            }

                        }



                        /*date logic */

                        ArrayList<String> dateconditionalsbefore =new ArrayList<>();

                        ArrayList<String> dateconditionalsafter =new ArrayList<>();
                        /*load the words synonymous to before from xml */
                        xml.input="before";
                        dateconditionalsbefore.addAll(xml.xmlParser());

                        /*load the word synonymous to after from xml */
                        xml.input="after";
                        dateconditionalsafter.addAll(xml.xmlParser());



                        if (datedetect||datedetectcase2)
                        {
                            /*construct date query */
                               Datequery dateob=new  Datequery(tablenameslist,datedetect, whereappended, unmodified, datecolumn, dateparsed, year, month, probabledatelist, nativecolumns, dateconditionalsbefore, dateconditionalsafter);

                             if(!dateob.getPredateappend().matches(""))
                             {
                                 res += dateob.getPredateappend();
                                 whereappended = true;

                             }

                        }





                        if(datecolumn!=null&&!datecolumn.matches(""))
                            columnnameslist.remove(datecolumn);

                        columnnameslist.removeAll(like.processedcolumns);
                        columnnameslist.removeAll(whereclause.columnsprocessed);
                        columnnameslist.removeAll(orderby.columnsprocessed);
                        columnnameslist.removeAll(between1.columnsprocessed);
                        columnnameslist.removeAll(countSum.usedcols);
                        columnnameslist.removeAll(average.usedcols);
                        columnnameslist.removeAll(minmax.usedcols);


    /*before performing group by operation we remove all the columns processed during
   like , where ,between , count sum etc
     */

                        Groupby groupby=new Groupby(sentencecopy,columnnameslist,groupoperations);
                        if(!groupby.finalstring.matches(""))
                        {
                            res +=" "+groupby.finalstring;
                        }

                        /*having condition only works on columns having group operations on them */


                       if(!having.getResultString().matches(""))
                       {
                           if (!groupby.finalstring.matches(""))
                           {
                               /*having clause possibility as group by exists*/
                               res += " HAVING " + having.getResultString();
                           }
                           else {
                               if (whereappended)
                               {
                                   res += " AND " + having.getResultString();
                               }
                               else
                                   {
                                   res += " WHERE " + having.getResultString();
                                   }
                           }
                       }



                        /*appending order by  clause */
                        if (!orderby.finalstring.matches(""))
                            res += " " + orderby.finalstring;








                        counter++;
                        PrintWriter pr = new PrintWriter(new FileOutputStream(new File("outputs.txt"), true));
                        /*append the input to the outputs.txt file */
                        pr.append(counter + " " + unmodified + "\n");


                   /*post process the queries to replace the logical table and column names with the
                   actual table and column names*/




                        Postprocess postprocess;


                        if(joincase)
                        {
                            postprocess=new Postprocess(res,true,revtables,revcol);
                        }
                        else
                        {
                            postprocess=new Postprocess(res,false,revtables,revcol);
                        }


                        res=postprocess.toString();


                        /*append the result to the outputs.txt file */
                        pr.append(res + "\n");
                        pr.append("\n");
                        pr.close();

                    }
                    else
                    {
                        PrintWriter pr = new PrintWriter(new FileOutputStream(new File("outputs.txt"), true));
                        counter++;
                        st ="NO TABLES COULD BE DETECTED";
                        pr.append(counter + " " + st + "\n");
                        pr.append("\n");
                        pr.close();
                    }
                }
                else
                {

                    PrintWriter pr = new PrintWriter(new FileOutputStream(new File("outputs.txt"), true));
                    counter++;
                    st ="RE-ENTER QUERY :POSSIBLY GRAMMAR IS WRONG : TAGGER IS UNABLE TO FIND APPROPRIATE TOKENS";
                    pr.append(counter + " " + st + "\n");
                    pr.append("\n");
                    pr.close();

                }
            }
        }

        catch(Exception e)
        {
            e.printStackTrace();
            counter++;
            PrintWriter pr = new PrintWriter(new FileOutputStream(new File("outputs.txt"), true));
            st = "EXCEPTION OCCURRED: FOLLOW THE STACK TRACE TO FIND EXACT LOCATION";
            pr.append(counter + " " + st + "\n");
            pr.append("\n");
            pr.close();


        }





    }











    public static void mapper(String mapkey,String Item)
    {
        ArrayList<String> itemlist=map.get(mapkey);
        if(itemlist==null)
        {
            itemlist=new ArrayList<String>();
            itemlist.add(Item);
            map.put(mapkey, itemlist);

        }
        else
        {
            if(!itemlist.contains(Item))
            {
                itemlist.add(Item);
            }
        }
    }


/*    public static String datequeryconstructor(boolean datedetect,boolean whereappended,String unmodified,String datecolumn,String dateparsed,String year,String month,ArrayList<String> probabledatelist,ArrayList<String> nativecolumns,ArrayList<String> dateconditionalsbefore,ArrayList<String> dateconditionalsafter)
    {

        /*inputs to this function are
        1. datedetect ->true if date is detected via chrono module
        2. whereappended ->if where is appended to the query already
        3. the unmodified input NL sentence
        4. datecolumn -> the identified date column if any
        5. dateparsed->  the NL date converted as per SQL format by chrono
        6. year -> if datedetect case 1 is false then the MonthYearDate format class will detect the date in which case it will initialise the string year
        7. month ->MonthYearDate format class will initialize the month
        8. probabledatelist -> a list of probabaledatecolumns
        9. nativecolumns -> a copy of columnnameslist
        10. dateconditionalsbefore -> a list containing the synonyms of before
        11. dateconditionalsafter -> a list containing the synonyms of after
         */

 /*       String predateappend="";

        if ((nativecolumns.size() == 0 && tablenameslist.size() != 0) || (datecolumn.matches("")))
        {
            if(probabledatelist.size()>0)
            {


                /*date detect is the case when chrono module detects the date in the NL sentence */
   /*             if (datedetect)
                {
                    /*check if where clause is appended to the result */
     /*               if (!whereappended)
                        predateappend += " WHERE ";
                    else
                        predateappend += " AND ";


                    predateappend += probabledatelist.get(0) + " = " + "'"+dateparsed+"'";
                    whereappended = true;
                }
                else {

                    if (!whereappended)
                        predateappend += " WHERE ";
                    else
                        predateappend += " AND ";

                    whereappended = true;

                    boolean commonflag = false;

                    if (!month.matches(""))
                    {
                        predateappend += "MONTH( " + probabledatelist.get(0) + " ) = " + "'"+month+"'";
                        commonflag = true;
                    }

                    if ((!year.matches("")))
                    {
                        if (commonflag) {
                            predateappend += " AND ";
                        }
                        predateappend += " YEAR( " + probabledatelist.get(0) + " ) = " + "'"+year+"'";
                    }

                }

            }
        }

        else
        {


            if (tablenameslist != null && tablenameslist.size() != 0)
            {
                if(datedetect)
                {

                    if (datecolumn.matches(""))
                    {
                        if (!whereappended)
                            predateappend += " WHERE ";
                        else
                            predateappend += " AND ";
                        predateappend += probabledatelist.get(0) + " = " + "'"+dateparsed+"'";
                        whereappended=true;
                    }
                    else
                    {

                        if(!whereappended)
                        {
                            predateappend +=" WHERE ";
                            whereappended=true;
                        }
                        else
                        {
                            predateappend+=" AND ";
                        }

                        predateappend += datecolumn + " = " + "'"+dateparsed+"'";
                    }


                }
                else
                {


                    if(!whereappended)
                    {
                        predateappend+=" WHERE ";
                    }
                    else
                    {
                        predateappend+=" AND ";
                    }

                    boolean commonflag=false;
                    if(!month.matches(""))
                    {
                        predateappend+=" MONTH( "+datecolumn+" ) = "+"'"+month+"'";

                        commonflag=true;

                    }
                    if(!year.matches(""))
                    {
                        if(commonflag)
                        {
                            predateappend+=" AND ";
                        }

                        predateappend+="YEAR( "+datecolumn+" ) = "+"'"+year+"'";
                    }

                }
            }
        }

        boolean after=false;
        for(String str:dateconditionalsafter)
        {
            if(unmodified.toLowerCase().contains(str))
            {
                predateappend=predateappend.replaceAll(" = "," > ");
                after=true;
                break;
            }
        }
        if(!after)
        {
            for (String str : dateconditionalsbefore)
            {
                if(unmodified.toLowerCase().contains(str))
                {
                    predateappend=predateappend.replaceAll(" = "," < ");
                }

            }
        }


        return predateappend;
    } */



}






