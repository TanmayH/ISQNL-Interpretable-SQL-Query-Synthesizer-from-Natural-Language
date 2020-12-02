import java.util.ArrayList;

public class SimpleChecker
{
    public ArrayList<String> simplechecker(ArrayList<String> nounsslist, ArrayList<String> samplenameslist, int value)
    {
        ArrayList<String> simplecheckdata = new ArrayList<>();

        /*value =1 implies that the simple checker is called to check columns */
        if(value==1)
        {

            simplecheckdata.clear();
            simplecheckdata.addAll(samplenameslist);
            simplecheckdata.retainAll(nounsslist);
            nounsslist.removeAll(simplecheckdata);

        }
        else
        {

            for(String str:nounsslist)
            {
                if(samplenameslist.contains(str))
                {
                    simplecheckdata.add(str);
                }
            }
        }
        return simplecheckdata;
    }

}
