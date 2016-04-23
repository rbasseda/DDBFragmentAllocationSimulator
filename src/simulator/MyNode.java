package simulator;
import simulator.*;

import java.util.*;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

public class MyNode {
  public char name;
  private MyLink links[]=new MyLink[9];
  private MyLink tempLink;
  private PacketsQueue qq[]=new PacketsQueue[9];
  private PacketsQueue tempQueue;
  private  int maxLinks,recievedPacketsCount,packetGeneratorCount;
  private  float avgDelay;
  private  DataAlloc myDataAllocator;
  private  int clock;
  private  PacketGenerator myPacketGenerator[]=new PacketGenerator[10];
  private  PacketGenerator tempPacketGenerator;
  private  rtTable myRoutingTable;
  private  int routeType;
  public MyNode(char N){
    name=N;
    avgDelay=0;
    recievedPacketsCount=0;
    myRoutingTable=new rtTable(name);
    maxLinks=0;
    packetGeneratorCount=0;
    routeType=0;
    myDataAllocator=new DataAlloc(this,'N');
  }
  public MyNode(){
    avgDelay=0;
    recievedPacketsCount=0;
    maxLinks=0;
    packetGeneratorCount=0;
    routeType=0;
    myDataAllocator=new DataAlloc(this,'N');
  }
  public MyNode(char N, int RouteT){
    name=N;
    avgDelay=0;
    recievedPacketsCount=0;
    routeType=RouteT;
    myRoutingTable=new rtTable(name);
    maxLinks=0;
    packetGeneratorCount=0;
    myDataAllocator=new DataAlloc(this,'N');
  }
  public MyNode(char N,int RouteT,char AllType){
    name=N;
    avgDelay=0;
    recievedPacketsCount=0;
    routeType=RouteT;
    myRoutingTable=new rtTable(name);
    maxLinks=0;
    packetGeneratorCount=0;
    myDataAllocator=new DataAlloc(this,AllType);
  }

  public void setParam(char N,int RouteT,char AllT){
	    name=N;
	    routeType=RouteT;
	    myRoutingTable=new rtTable(name);
	    myDataAllocator.setAllType(AllT);

	  }

  public void AddLink(MyLink al){
    links[maxLinks]=al;
    maxLinks++;
    tempQueue=new PacketsQueue(links[(maxLinks-1)],this);
    qq[(maxLinks-1)]=tempQueue;
  }
  public char getDataLocation(int dataItem){
    return(Coordinator.getInstance().getDataItemLoc(dataItem));
  }
  public void moveDataLocation(int dataItem,char destinationNode){
	  Coordinator.getInstance().moveDataItem(dataItem,destinationNode);
  }


  public void recievePacket(Packet p){
    //System.out.println("Recieved Pack by "+Name+" for "+p.GetS()+" "+p.GetD());
    if(p.getDest()==name){
      clock=Coordinator.getInstance().getClock();
      ++recievedPacketsCount;
      switch(p.getType()){
        case 'Q':
            --recievedPacketsCount;
            p.replyQueryPack();
            PacketsQueue RoutedQ;//=new Queue();
            char tdest;
            tdest=p.getDest();
            RoutedQ=route(tdest);
            RoutedQ.getPackage(p,clock);
            myDataAllocator.hint(p.getDest(),p.getDataItem());
            break;
        case 'A':
            --recievedPacketsCount;
            avgDelay=(avgDelay*recievedPacketsCount+p.calculateDelay(clock))/(recievedPacketsCount+1);
            ++recievedPacketsCount;
            break;
        case 'D':
        	Coordinator.getInstance().moveDataItemCommit(p.getDataItem(),p.getDest(),p.calculateDelay(clock));
            break;
        default:
            System.out.print("\n Unknown Reciepant of Packet ");
            break;
          }
      //System.out.print(Name+" Packet SendT:");
      //System.out.print(p.GetSendTS()+" now"+Integer.toString(clock)+ " Packet Delay:");
      //System.out.println(Integer.toString(p.CalcDelay(clock))+" AvgDelay"+Float.toString(AvgDelay));
      }
    else
      {
      PacketsQueue RoutedQ;//=new Queue();
      char tdest;
      tdest=p.getDest();
      RoutedQ=route(tdest);
      RoutedQ.getPackage(p,clock);
      };

  }
  public float getAvgDelay(){
    return(avgDelay);
  }
  public int getRecievedPackCount(){
    return(recievedPacketsCount);
  }
  private PacketsQueue route(char dest){
    int count=0;
    char chTemp='x';
    char NextN;
    boolean DestFound=false;
    NextN=getNextNodeInRouting(dest);
      for(count=0;count<maxLinks;++count){
        chTemp=qq[count].getDest();
        if(chTemp==NextN){
          DestFound=true;
          break;
          };

      };
    return(qq[count]);


  }
  public char getNextNodeInRoute(char DD){
    return(getNextNodeInRouting(DD));
  }
  private char getNextNodeInRouting(char dest){
    char ret;
    ret=myRoutingTable.getNextNode(dest);
    return(ret);
  }
  public boolean addPacketGenerator(int StartT,int FinishT,char Dest,int Rate,int Length, int FID){
    tempPacketGenerator=new PacketGenerator(this,StartT,FinishT,Rate,Dest,Length,FID);
    myPacketGenerator[packetGeneratorCount]=tempPacketGenerator;
    packetGeneratorCount++;
    return(true);
  }
  public boolean addPacketGenerator(int Length, int FID,char Dest){
    tempPacketGenerator=new PacketGenerator(this,0,32000,20,Dest,Length,FID);
    myPacketGenerator[packetGeneratorCount]=tempPacketGenerator;
    packetGeneratorCount++;
    return(true);
  }
  public boolean addPacketGenerator(int StartT,int FinishT,int Rt,int FID){
    tempPacketGenerator=new PacketGenerator(this,StartT,FinishT,Rt,'Z',50,FID);
    myPacketGenerator[packetGeneratorCount]=tempPacketGenerator;
    packetGeneratorCount++;
    return(true);
  }
  public int getClock(){
    return(Coordinator.getInstance().getClock());
  }
  public void notify(int cl){
    int count=0;
    for(count=0;count<maxLinks;++count)
      try{
        qq[count].notify(cl);
      }catch(RuntimeException r){
        //System.out.print("Queue Generation Error"+r+Integer.toString(count).toCharArray() +"\n" ) ;
      }
    for(count=0;count<packetGeneratorCount;++count)
    try{
        myPacketGenerator[count].notify(cl);
      }catch(RuntimeException r){
        //System.out.print("PacketGenerator Generation Error"+r+" "+Name+"  "+Integer.toString(count).toCharArray()+" "+Integer.toString(cl).toCharArray()+"\n") ;
      }
    myRoutingTable.hint();

  }
  public StringBuffer getQueuesStatus(){
    StringBuffer retVal=new StringBuffer("");
    int dropCnt=0,count=0;
    float AvgQL=0;
    char otherSide='x';
    for(count=0;count<maxLinks;++count){
      otherSide=links[count].getOtherHead(name);
//      dropCnt=qq[count]
    };
    return(retVal);
  }

private class PacketGenerator{
  int startClock,finishClock,rate,plength;
  MyNode owner;
  char dest;
  boolean status;
  Packet recent;
  int flowID,serialN;
  int cdcounter;
  public PacketGenerator(MyNode O,int ST, int FT, int R,char D, int L,int FlowID){
    owner=O;
    startClock=ST;
    finishClock=FT;
    rate=R;
    dest=D;
    status=false;
    plength=L;
    flowID=FlowID;
    serialN=0;
    cdcounter=0;
  }
  public void notify(int cl){
    if(startClock<=cl&&cl<finishClock)status=true;
    else status=false;
    if(status==true){
//      System.out.println("Packet Generated"+owner.Name+" "+Integer.toString(SN));
      if(cdcounter==0){
        generate(cl);
        cdcounter=rate;
        }else cdcounter--;
    };


  }
  private void generate(int cl){
    int clock;
    int DI;
    Random myRand=new Random();
    DI=(int)(myRand.nextFloat()*10);
    clock=cl;
    //DI();
    if(owner.getDataLocation(DI)!=owner.name){
    	recent=new Packet(owner.name,owner.getDataLocation(DI),'Q',DI,plength,clock,flowID,serialN,true);
        owner.recievePacket(recent);
        serialN++;
      }
    //System.out.print("Generated packet at "+Integer.toString(clock)+" in "+owner.Name);
    //System.out.println(" FID:"+Integer.toString(FID)+" SN:"+Integer.toString(SN));

  }
}
private class PacketsQueue{
  MyLink ll;
  MyNode owner;
  Packet pps[]=new Packet[20];
  int length;
  int dropCount;
  public PacketsQueue(MyLink l,MyNode o){
    ll=l;
    owner=o;
    length=0;
    dropCount=0;
  }
  public PacketsQueue(){
    length=0;
    dropCount=0;
  }
  public char getDest(){
    return(ll.getOtherHead(owner.name));
  }
  public void getPackage(Packet ppp,int cl){
    //System.out.println("Enqueued Packet in "+owner.Name+" for "+ppp.GetS()+" to "+ppp.GetD());
    ppp.setEnqueueTimeStamp(cl);
    if(length>19){
      length=20;
      dropCount++;
      //System.out.println("Packet "+ppp.GetS()+" to "+ppp.GetD() +"droped in "+owner.Name );
      }
    else{
        pps[length]=ppp;
        length++;
      };
  }
  public void notify(int cl){
    int count=0;
    if(length>0){
      if(ll.getPackage(pps[0],owner)==true){
        //System.out.println("Dequeued Packet in "+owner.Name+" for "+pps[0].GetS()+" to "+pps[0].GetD());
        --length;
        for(count=0;count<19;++count)
          pps[count]=pps[count+1];
          };
      };
  }
  public int getDropCount(){
    return(dropCount);
  }

}

 private class rtTable {
  char key[]=new char [10];
  char nextNode[]= new char [10];
  int cost[]=new int [10];
  float avgDel[]=new float [10];
  int updateFreq=10;
  int updateVar=0;
  int nCount=0;
  int myIndex;
  char myName;
  int routeStyle;
  public rtTable(char Owner,int RouteT){
    myName=Owner;
    routeStyle=RouteT;
    initTable();
  }
  public rtTable(char Owner){
    myName=Owner;
    routeStyle=0;
    initTable();
  }
  public void hint(){
    if(updateVar==0){
      switch(routeStyle){
        case(0):
          updateTableDijkstra();
        case(1):
          updateTableDijkstra();
        default:
          updateTableDijkstra();
          };
      updateVar=10;
      }
    else
      {
      updateVar--;
      };
    }
  private void initTable(){
    int count=0;
    nCount=Coordinator.getInstance().getMaxNodeCount();
    for(count=0;count<nCount;count++){
      key[count]=Coordinator.getInstance().getNodeName(count);
      if(key[count]==myName)myIndex=count;
      };

  }
  private void updateTableDijkstra(){
    int count=0,recent;
    boolean accessed[]=new boolean[10];
    int distances[]=new int [10];
    int lessC,lessI;
    for(count=0;count<nCount;++count)
      if(Coordinator.getInstance().getNodeName(count)==myName)myIndex=count;

    for(count=0;count<10;++count){
      accessed[count]=false;
      distances[count]=32000;
      };
    accessed[myIndex]=true;
    for(count=0;count<nCount;++count){
      cost[count]=32000;//Mng.GetLinkCost(MyIndex,count);
    };
    cost[myIndex]=0;
    recent=myIndex;
    do{
        for(count=0;count<nCount;++count){
          if(cost[count]>(cost[recent]+Coordinator.getInstance().getLinkCost(recent,count))){
            cost[count]=cost[recent]+Coordinator.getInstance().getLinkCost(recent,count);
            if(recent==myIndex)nextNode[count]=key[count];
            else nextNode[count]=nextNode[recent];
            };
        };
        lessC=32001;
        lessI=0;
        for(count=0;count<nCount;++count){
          if((cost[count]<lessC)&&!(accessed[count])){
            lessC=cost[count];
            lessI=count;
            };
        };
        recent=lessI;
        accessed[recent]=true;
      }while(!(evaluateVector(accessed,nCount)));
  }
  private boolean evaluateVector(boolean in[],int m){
    boolean ret=false;
    int count;
    ret=in[0];
    for(count=1;count<m;++count)ret=ret&&in[count];
    return(ret);
  }
  public char getNextNode(char dd){
    int count=0;
    char RTret='x';
    for(count=0;count<nCount;++count){
      if(key[count]==dd)RTret=nextNode[count];
      };
    return(RTret);
  }
 }
}