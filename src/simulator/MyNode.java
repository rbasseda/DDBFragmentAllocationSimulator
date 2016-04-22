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
  public char Name;
  private MyLink LL[]=new MyLink[9];
  private MyLink TempL;
  private Queue qq[]=new Queue[9];
  private Queue TempQ;
  private  int MaxLinks,RPCount,PGenCount;
  private  float AvgDelay;
  private  coordinator Mnger;
  private  DataAlloc MyDA;
  private  int clock;
  private  PacketGenerator PG[]=new PacketGenerator[10];
  private  PacketGenerator TempPG;
  private  rtTable MyRTTable;
  private  int RouteType;
  public MyNode(char N,coordinator Manager){
    Name=N;
    AvgDelay=0;
    RPCount=0;
    Mnger=Manager;
    MyRTTable=new rtTable(Mnger,Name);
    MaxLinks=0;
    PGenCount=0;
    RouteType=0;
    MyDA=new DataAlloc(this,'N');
  }
  public MyNode(){
    AvgDelay=0;
    RPCount=0;
    MaxLinks=0;
    PGenCount=0;
    RouteType=0;
    MyDA=new DataAlloc(this,'N');
  }
  public MyNode(char N,coordinator Manager, int RouteT){
    Name=N;
    AvgDelay=0;
    RPCount=0;
    Mnger=Manager;
    RouteType=RouteT;
    MyRTTable=new rtTable(Mnger,Name);
    MaxLinks=0;
    PGenCount=0;
    MyDA=new DataAlloc(this,'N');
  }
  public MyNode(char N,coordinator Manager, int RouteT,char AllType){
    Name=N;
    AvgDelay=0;
    RPCount=0;
    Mnger=Manager;
    RouteType=RouteT;
    MyRTTable=new rtTable(Mnger,Name);
    MaxLinks=0;
    PGenCount=0;
    MyDA=new DataAlloc(this,AllType);
  }

  public void SetParam(char N,coordinator Manager){
    Name=N;
    Mnger=Manager;
    MyRTTable=new rtTable(Mnger,Name);
  }
  public void SetParam(char N,coordinator Manager,int RouteT){
    Name=N;
    Mnger=Manager;
    RouteType=RouteT;
    MyRTTable=new rtTable(Mnger,Name);

  }
  public void SetParam(char N,coordinator Manager,int RouteT,char AllT){
    Name=N;
    Mnger=Manager;
    RouteType=RouteT;
    MyRTTable=new rtTable(Mnger,Name);
    MyDA.SetAllType(AllT);

  }

  public void AddLink(MyLink al){
    LL[MaxLinks]=al;
    MaxLinks++;
    TempQ=new Queue(LL[(MaxLinks-1)],this);
    qq[(MaxLinks-1)]=TempQ;
  }
  public char GetDataLoc(int DI){
    return(Mnger.GetDataItemLoc(DI));
  }
  public void MoveDataLoc(int DI,char N){
    Mnger.moveDataItem(DI,N);
  }


  public void Get(pack p){
    //System.out.println("Recieved Pack by "+Name+" for "+p.GetS()+" "+p.GetD());
    if(p.GetD()==Name){
      clock=Mnger.GetClock();
      ++RPCount;
      switch(p.getType()){
        case 'Q':
            --RPCount;
            p.replyQueryPack();
            Queue RoutedQ;//=new Queue();
            char tdest;
            tdest=p.GetD();
            RoutedQ=Route(tdest);
            RoutedQ.Get(p,clock);
            MyDA.Hint(p.GetD(),p.GetDataItem());
            break;
        case 'A':
            --RPCount;
            AvgDelay=(AvgDelay*RPCount+p.CalcDelay(clock))/(RPCount+1);
            ++RPCount;
            break;
        case 'D':
            Mnger.moveDataItemCommit(p.GetDataItem(),p.GetD(),p.CalcDelay(clock));
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
      Queue RoutedQ;//=new Queue();
      char tdest;
      tdest=p.GetD();
      RoutedQ=Route(tdest);
      RoutedQ.Get(p,clock);
      };

  }
  public float GetAvgDelay(){
    return(AvgDelay);
  }
  public int GetRecievedPackCount(){
    return(RPCount);
  }
  private Queue Route(char dest){
    int count=0;
    char chTemp='x';
    char NextN;
    boolean DestFound=false;
    NextN=NextNode(dest);
      for(count=0;count<MaxLinks;++count){
        chTemp=qq[count].GetDest();
        if(chTemp==NextN){
          DestFound=true;
          break;
          };

      };
    return(qq[count]);


  }
  public char GetNextNodeInRoute(char DD){
    return(NextNode(DD));
  }
  private char NextNode(char dest){
    char ret;
    ret=MyRTTable.GetNextNode(dest);
    return(ret);
  }
  public boolean AddPackGenerator(int StartT,int FinishT,char Dest,int Rate,int Length, int FID){
    TempPG=new PacketGenerator(this,StartT,FinishT,Rate,Dest,Length,FID);
    PG[PGenCount]=TempPG;
    PGenCount++;
    return(true);
  }
  public boolean AddPackGenerator(int Length, int FID,char Dest){
    TempPG=new PacketGenerator(this,0,32000,20,Dest,Length,FID);
    PG[PGenCount]=TempPG;
    PGenCount++;
    return(true);
  }
  public boolean AddPackGenerator(int StartT,int FinishT,int Rt,int FID){
    TempPG=new PacketGenerator(this,StartT,FinishT,Rt,'Z',50,FID);
    PG[PGenCount]=TempPG;
    PGenCount++;
    return(true);
  }
  public int GetMyClock(){
    return(Mnger.GetClock());
  }
  public void Notify(int cl){
    int count=0;
    for(count=0;count<MaxLinks;++count)
      try{
        qq[count].Notify(cl);
      }catch(RuntimeException r){
        //System.out.print("Queue Generation Error"+r+Integer.toString(count).toCharArray() +"\n" ) ;
      }
    for(count=0;count<PGenCount;++count)
    try{
        PG[count].Notify(cl);
      }catch(RuntimeException r){
        //System.out.print("PacketGenerator Generation Error"+r+" "+Name+"  "+Integer.toString(count).toCharArray()+" "+Integer.toString(cl).toCharArray()+"\n") ;
      }
    MyRTTable.hint();

  }
  public StringBuffer GetQueuesStatus(){
    StringBuffer retVal=new StringBuffer("");
    int dropCnt=0,count=0;
    float AvgQL=0;
    char otherSide='x';
    for(count=0;count<MaxLinks;++count){
      otherSide=LL[count].GetOtherHead(Name);
//      dropCnt=qq[count]
    };
    return(retVal);
  }

private class PacketGenerator{
  int startClock,finishClock,Rate,plength;
  MyNode owner;
  char dest;
  boolean status;
  pack recent;
  int FID,SN;
  int cdcounter;
  public PacketGenerator(MyNode O,int ST, int FT, int R,char D, int L,int FlowID){
    owner=O;
    startClock=ST;
    finishClock=FT;
    Rate=R;
    dest=D;
    status=false;
    plength=L;
    FID=FlowID;
    SN=0;
    cdcounter=0;
  }
  public void Notify(int cl){
    if(startClock<=cl&&cl<finishClock)status=true;
    else status=false;
    if(status==true){
//      System.out.println("Packet Generated"+owner.Name+" "+Integer.toString(SN));
      if(cdcounter==0){
        Generate(cl);
        cdcounter=Rate;
        }else cdcounter--;
    };


  }
  private void Generate(int cl){
    int clock;
    int DI;
    Random MyRand=new Random();
    DI=(int)(MyRand.nextFloat()*10);
    clock=cl;
    //DI();
    if(owner.GetDataLoc(DI)!=owner.Name){
        recent=new pack(clock,owner.GetDataLoc(DI),owner.Name,plength,FID,SN,true,'Q',DI);
        owner.Get(recent);
        SN++;
      }
    //System.out.print("Generated packet at "+Integer.toString(clock)+" in "+owner.Name);
    //System.out.println(" FID:"+Integer.toString(FID)+" SN:"+Integer.toString(SN));

  }
}
private class Queue{
  MyLink ll;
  MyNode owner;
  pack pps[]=new pack[20];
  int length;
  int dropCount;
  public Queue(MyLink l,MyNode O){
    ll=l;
    owner=O;
    length=0;
    dropCount=0;
  }
  public Queue(){
    length=0;
    dropCount=0;
  }
  public char GetDest(){
    return(ll.GetOtherHead(owner.Name));
  }
  public void Get(pack ppp,int cl){
    //System.out.println("Enqueued Packet in "+owner.Name+" for "+ppp.GetS()+" to "+ppp.GetD());
    ppp.SetEnqueueTS(cl);
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
  public void Notify(int cl){
    int count=0;
    if(length>0){
      if(ll.Get(pps[0],owner)==true){
        //System.out.println("Dequeued Packet in "+owner.Name+" for "+pps[0].GetS()+" to "+pps[0].GetD());
        --length;
        for(count=0;count<19;++count)
          pps[count]=pps[count+1];
          };
      };
  }
  public int GetDropCount(){
    return(dropCount);
  }

}

 private class rtTable {
  char key[]=new char [10];
  char nextNode[]= new char [10];
  int cost[]=new int [10];
  float AvgDel[]=new float [10];
  int updateFreq=10;
  int updateVar=0;
  int NCount=0;
  int MyIndex;
  char MyName;
  coordinator Mng;
  int RouteStyle;
  public rtTable(coordinator coor,char Owner,int RouteT){
    Mng=coor;
    MyName=Owner;
    RouteStyle=RouteT;
    initTable();
  }
  public rtTable(coordinator coor,char Owner){
    Mng=coor;
    MyName=Owner;
    RouteStyle=0;
    initTable();
  }
  public void hint(){
    if(updateVar==0){
      switch(RouteStyle){
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
    NCount=Mng.GetMaxNodeCount();
    for(count=0;count<NCount;count++){
      key[count]=Mng.GetNodeName(count);
      if(key[count]==MyName)MyIndex=count;
      };

  }
  private void updateTableDijkstra(){
    int count=0,recent;
    boolean accessed[]=new boolean[10];
    int distances[]=new int [10];
    int lessC,lessI;
    for(count=0;count<NCount;++count)
      if(Mng.GetNodeName(count)==MyName)MyIndex=count;

    for(count=0;count<10;++count){
      accessed[count]=false;
      distances[count]=32000;
      };
    accessed[MyIndex]=true;
    for(count=0;count<NCount;++count){
      cost[count]=32000;//Mng.GetLinkCost(MyIndex,count);
    };
    cost[MyIndex]=0;
    recent=MyIndex;
    do{
        for(count=0;count<NCount;++count){
          if(cost[count]>(cost[recent]+Mng.GetLinkCost(recent,count))){
            cost[count]=cost[recent]+Mng.GetLinkCost(recent,count);
            if(recent==MyIndex)nextNode[count]=key[count];
            else nextNode[count]=nextNode[recent];
            };
        };
        lessC=32001;
        lessI=0;
        for(count=0;count<NCount;++count){
          if((cost[count]<lessC)&&!(accessed[count])){
            lessC=cost[count];
            lessI=count;
            };
        };
        recent=lessI;
        accessed[recent]=true;
      }while(!(evaluateVector(accessed,NCount)));
  }
  private boolean evaluateVector(boolean in[],int m){
    boolean ret=false;
    int count;
    ret=in[0];
    for(count=1;count<m;++count)ret=ret&&in[count];
    return(ret);
  }
  public char GetNextNode(char dd){
    int count=0;
    char RTret='x';
    for(count=0;count<NCount;++count){
      if(key[count]==dd)RTret=nextNode[count];
      };
    return(RTret);
  }
 }
}