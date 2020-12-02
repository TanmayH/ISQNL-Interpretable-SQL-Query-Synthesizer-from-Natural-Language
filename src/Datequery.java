import java.util.ArrayList;

public class Datequery
{

    public  String predateappend="";

    public String getPredateappend()
    {

        return this.predateappend;
    }




    public Datequery(ArrayList<String> tablenameslist,boolean datedetect, boolean whereappended, String unmodified, String datecolumn, String dateparsed, String year, String month, ArrayList<String> probabledatelist, ArrayList<String> nativecolumns, ArrayList<String> dateconditionalsbefore, ArrayList<String> dateconditionalsafter)
    {


        if ((nativecolumns.size() == 0 && tablenameslist.size() != 0) || (datecolumn.matches("")))
        {
            if(probabledatelist.size()>0)
            {


                /*date detect is the case when chrono module detects the date in the NL sentence */
                if (datedetect)
                {
                    /*check if where clause is appended to the result */
                    if (!whereappended)
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



    }




}


