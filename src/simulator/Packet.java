package simulator;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

  public class Packet{
      private char source,dest;
      private char packetType;
      private int dataItem;
      private int length,sendTS,enqueueTimeStamp;
      private int flowID,serialN;
      private boolean data;
      public Packet(){
      data=true;
      packetType='Q';
      dataItem=0;
      }



	public Packet(char source, char dest, char packetType, int dataItem,
			int length, int sendTS, int flowID, int serialN, boolean data) {
		super();
		this.source = source;
		this.dest = dest;
		this.packetType = packetType;
		this.dataItem = dataItem;
		this.length = length;
		this.sendTS = sendTS;
		this.flowID = flowID;
		this.serialN = serialN;
		this.data = data;
	}



	public void setParam(int cl,char D,char S,int L){
        length=L;
        source=S;
        dest=D;
        sendTS=cl;
      }
      public void setParam(int cl,char D,char S,int L,char PT,int DI){
        length=L;
        source=S;
        dest=D;
        sendTS=cl;
        packetType=PT;
        dataItem=DI;
      }
      public void changeType(char PT){
        packetType=PT;
      }
      public char getType(){
        return(packetType);
      }
      public boolean isAnswer(){
        if(packetType=='Q')return(false);
        else return(true);
      }

      public int calculateDelay(int ccl){
        int Del;
        Del=ccl-sendTS;
        return(Del);
      }
      public int getLength(){//Getting Length
        return(length);
      }
      public char getSource(){//Getting Source
        return(source);
      }
      public char getDest(){//Getting Destination
        return(dest);
      }
      public void setEnqueueTimeStamp(int cl){
        enqueueTimeStamp=cl;
      }
      public int calculateEnqueueDelay(int ccl){
        int EDel;
        EDel=ccl-enqueueTimeStamp;
        enqueueTimeStamp=0;
        return(EDel);
      }
      public int GetSendTS(){
        return(sendTS);
      }
      public int getDataItem(){
        return(dataItem);
      }
      public void replyQueryPack(){
        char T;
        T=dest;
        dest=source;
        source=T;
        packetType='A';
      }
  }
