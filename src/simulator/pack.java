package simulator;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

  public class pack{
      private char source,dest;
      private char PType;
      private int DataItem;
      private int length,SendTS,EnqueueTS;
      private int FID,SN;
      private boolean Data;
      public pack(){
      Data=true;
      PType='Q';
      DataItem=0;
      }
      public pack(int FlowID,int SNumber){
        FID=FlowID;
        SN=SNumber;
        PType='Q';
        DataItem=0;
      }
      public pack(int cl,char D,char S, int L, int flowID,int sNumber){
        length=L;
        source=S;
        dest=D;
        SendTS=cl;
        FID=flowID;
        SN=sNumber;
        PType='Q';
        DataItem=0;
      }
      public pack(int cl,char D,char S, int L, int FlowID,int SNumber,boolean DataHand){
        length=L;
        source=S;
        dest=D;
        SendTS=cl;
        FID=FlowID;
        SN=SNumber;
        Data=DataHand;
        DataItem=0;
      }
      public pack(int cl,char D,char S, int L, int FlowID,int SNumber,boolean DataHand,char PT,int DI){
        length=L;
        source=S;
        dest=D;
        SendTS=cl;
        FID=FlowID;
        SN=SNumber;
        Data=DataHand;
        PType=PT;
        DataItem=DI;

      }

      public void SetParam(int cl,char D,char S,int L){
        length=L;
        source=S;
        dest=D;
        SendTS=cl;
      }
      public void SetParam(int cl,char D,char S,int L,char PT,int DI){
        length=L;
        source=S;
        dest=D;
        SendTS=cl;
        PType=PT;
        DataItem=DI;
      }
      public void changeType(char PT){
        PType=PT;
      }
      public char getType(){
        return(PType);
      }
      public boolean isAnswer(){
        if(PType=='Q')return(false);
        else return(true);
      }

      public int CalcDelay(int ccl){
        int Del;
        Del=ccl-SendTS;
        return(Del);
      }
      public int GetL(){//Getting Length
        return(length);
      }
      public char GetS(){//Getting Source
        return(source);
      }
      public char GetD(){//Getting Destination
        return(dest);
      }
      public void SetEnqueueTS(int cl){
        EnqueueTS=cl;
      }
      public int CalcEnqDelay(int ccl){
        int EDel;
        EDel=ccl-EnqueueTS;
        EnqueueTS=0;
        return(EDel);
      }
      public int GetSendTS(){
        return(SendTS);
      }
      public int GetDataItem(){
        return(DataItem);
      }
      public void replyQueryPack(){
        char T;
        T=dest;
        dest=source;
        source=T;
        PType='A';
      }
  }
