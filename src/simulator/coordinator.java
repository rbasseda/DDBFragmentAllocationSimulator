package simulator;
import simulator.MyNode;

import java.util.*;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

public class coordinator {
    private int clock,SimLength;
    private int DataPacketSN=0;
    private float AvgDataPacketDelay=0;
    private MyNode[] Nodes=new MyNode[10];
    private MyNode Temp;
    private MyLink TempL;
    private MyLink[] Links=new MyLink[100];
    private int NodeCount,LinkCount;
    private char NodeNames[]=new char[10];
    private int LinkCost[][]=new int [10][10];
    private float LinkDelay[][]=new float [10][10];
    private int LinkBW[][]=new int [10][10];
    private int LinkPropagDel[][]=new int [10][10];
    private int MatrixUF=100,MatrixUT;
    private DataMap MyMapping=new DataMap();
    private int RouteType;
    private int[] DropFlow=new int[20];
    private int FlowCnt;
    private char AllocT='Q';
  public coordinator() {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          LinkDelay[i][j]=0;
          LinkBW[i][j]=1;
          LinkPropagDel[i][j]=32001;
          LinkCost[i][j]=32001;
        };
      NodeNames[i]='\0';
      };
    NodeCount=0;
    LinkCount=0;
    SimLength=100;
    MatrixUT=MatrixUF;
    RouteType=0;
    FlowCnt=0;
  }
  public coordinator(int SimL, int RouteT) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          LinkDelay[i][j]=0;
          LinkBW[i][j]=1;
          LinkPropagDel[i][j]=32001;
          LinkCost[i][j]=32001;
        };
      NodeNames[i]='\0';
      };
    NodeCount=0;
    LinkCount=0;
    SimLength=SimL;
    MatrixUT=MatrixUF;
    RouteType=RouteT;
    FlowCnt=0;
  }
  public coordinator(int SimL, int RouteT,char AllT) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          LinkDelay[i][j]=0;
          LinkBW[i][j]=1;
          LinkPropagDel[i][j]=32001;
          LinkCost[i][j]=32001;
        };
      NodeNames[i]='\0';
      };
    NodeCount=0;
    LinkCount=0;
    SimLength=SimL;
    MatrixUT=MatrixUF;
    RouteType=RouteT;
    AllocT=AllT;
    FlowCnt=0;
  }

  public coordinator(int SimL) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          LinkDelay[i][j]=0;
          LinkBW[i][j]=1;
          LinkPropagDel[i][j]=32001;
          LinkCost[i][j]=32001;
        };
      NodeNames[i]='\0';
      };
    NodeCount=0;
    LinkCount=0;
    SimLength=SimL;
    MatrixUT=MatrixUF;
    RouteType=0;
  }
  public int GetClock(){
    return(clock);
    }
  public void RunCoord(RoutingUI MyUI){
    RoutingUI TempUI;
    TempUI=MyUI;
    int count;
    Topology1();
    for(count=0;count<NodeCount;++count){
      try{
        Temp=new MyNode();
        Nodes[count]=Temp;
        Nodes[count].SetParam(NodeNames[count],(coordinator)this,RouteType,AllocT);
      }catch (RuntimeException r){
        System.out.println("RunTime Exception Creation On My Node "+ r);
      };
      };
    setLinkObjects();
    SenarioSimpleTest2();
    //Sequencer
    for(clock=0;clock<SimLength;++clock){
      for(count=0;count<NodeCount;++count)
        Nodes[count].Notify(clock);
      for(count=0;count<LinkCount;++count)
        Links[count].Notify(clock);
      ChangeCostSenario1(clock);
      updateDelayMatrix();
    };
    TempUI.updateClock(clock);
  }
  public int GetSimLength(){
    return(SimLength);
  }
  public float GetLinkDelay(int i,int j){
    return(LinkDelay[i][j]);
  }
  public int GetLinkCost(int i, int j){
    return(LinkCost[i][j]);
  }
  public char GetNodeName(int index){
    return(NodeNames[index]);
  }
  public int GetMaxNodeCount(){
    return(NodeCount);
  }
    private void Senario2(int RateF){
    Nodes[0].AddPackGenerator(2,700,'I',10+RateF,100,1);
    Nodes[3].AddPackGenerator(3,3000,'G',5,100+RateF,2);
    FlowCnt=2;
  }
  private void Senario1(int RateF){
    Nodes[0].AddPackGenerator(500,1000,'B',5+RateF,150,1);
    Nodes[0].AddPackGenerator(5000,70000,'B',5+RateF,150,2);
    Nodes[0].AddPackGenerator(5000,70000,'D',5+RateF,150,3);
    Nodes[1].AddPackGenerator(150,20000,'F',10+RateF,150,4);
    Nodes[2].AddPackGenerator(100,40000,'H',5+RateF,100,5);
    Nodes[3].AddPackGenerator(150,30000,'G',10+RateF,200,6);
    Nodes[4].AddPackGenerator(1500,3000,'G',10+RateF,200,7);
    Nodes[7].AddPackGenerator(500,900,'B',15+RateF,300,8);
    Nodes[8].AddPackGenerator(1000,4000,'C',10+RateF,300,9);
    Nodes[8].AddPackGenerator(2000,3000,'A',10+RateF,200,10);
    Nodes[3].AddPackGenerator(3000,3500,'I',20+RateF,300,11);
    Nodes[6].AddPackGenerator(1000,4000,'E',30+RateF,300,12);
    FlowCnt=12;
  }

  private void Senario2(){
    Nodes[0].AddPackGenerator(2,700,'I',1,100,1);
    Nodes[3].AddPackGenerator(3,3000,'G',5,100,4);
    FlowCnt=2;
  }
  private void Senario1(){
    Nodes[0].AddPackGenerator(500,1000,'B',5,150,1);
    Nodes[0].AddPackGenerator(5000,70000,'B',5,150,1);
    Nodes[0].AddPackGenerator(5000,70000,'D',5,150,1);
    Nodes[1].AddPackGenerator(150,20000,'F',10,150,2);
    Nodes[2].AddPackGenerator(100,40000,'H',5,100,3);
    Nodes[3].AddPackGenerator(150,30000,'G',10,200,4);
    Nodes[4].AddPackGenerator(1500,3000,'G',10,2000,4);
    Nodes[8].AddPackGenerator(500,900,'B',15,300,5);
    Nodes[8].AddPackGenerator(1000,4000,'C',10,300,5);
    FlowCnt=9;
  }
  private void SenarioSimpleTest1(){
    Nodes[0].AddPackGenerator(0,10000,100,1);
    Nodes[8].AddPackGenerator(20000,100000,100,1);
    FlowCnt=2;
  }
  private void SenarioSimpleTest2(){
    Nodes[0].AddPackGenerator(0,60000,100,1);
    Nodes[2].AddPackGenerator(0,60000,50,1);
    Nodes[3].AddPackGenerator(0,60000,50,1);
    Nodes[7].AddPackGenerator(0,60000,50,1);
    //Nodes[8].AddPackGenerator(20000,100000,100,1);
    FlowCnt=4;
  }

  private void ChangeCostSenario1(int cl){
    ScheduledChangeLC(cl,500,1,2,(float)0.5,1);//B-C change
    ScheduledChangeLC(cl,600,0,4,0,5);//A-E change
    ScheduledChangeLC(cl,300,4,5,0,-3);//E-F change
    ScheduledChangeLC(cl,800,6,7,(float)0.2,1);//G-H change
  }
  public void AddDrop(int FID){
    DropFlow[FID]++;
  }
  public StringBuffer GetFlowStatus(){
    StringBuffer Temp=new StringBuffer("\n");
    int count=0;
    for(count=1;count<=FlowCnt;++count)
      Temp.append("\n Flow Count "+Integer.toString(count)+" droped count "+Integer.toString(DropFlow[count]));
    return(Temp);
  }
  public StringBuffer GetOverAllRep(){
    StringBuffer Temp=new StringBuffer("");
    int count=0;
    int TDrop=0;
    float TAvgQN=0,TAvgQNV=0;
    float TAvgQL=0,TAvgQLV=0;
    float TAvgUL=0,TAvgULV=0;
    int TRPCount=0;
    for(count=1;count<=FlowCnt;++count)
      TDrop=TDrop+DropFlow[count];
    for(count=0;count<NodeCount;++count)
      TRPCount=TRPCount+Nodes[count].GetRecievedPackCount();
    for(count=0;count<NodeCount;++count)
      TAvgQN=TAvgQN+Nodes[count].GetAvgDelay();
    TAvgQNV=TAvgQN/NodeCount;
    for(count=0;count<LinkCount;++count)
      TAvgQL=TAvgQL+Links[count].GetOverALinkDelay();
    TAvgQLV=TAvgQL/LinkCount;
    for(count=0;count<LinkCount;++count)
      TAvgUL=TAvgUL+Links[count].GetTotalUtilization();
    TAvgULV=TAvgUL/LinkCount;
    Temp.append(" Drop Count "+Integer.toString(TDrop)+" Avg Delay On Node "+Float.toString(TAvgQNV)+" Total Count of Recieved Packet by Nodes "+Integer.toString(TRPCount));
    Temp.append("\n Avg Delay On Link "+Float.toString(TAvgQLV)+" Avg Link Utillization On Link "+Float.toString(TAvgULV));
    Temp.append("\n Avg Delay On Data Migration: "+Float.toString(AvgDataPacketDelay));
    return(Temp);
  }
  private void Topology1(){
    int counti,countj,Loc;
    NodeCount=9;
    NodeNames[0]='A';
    NodeNames[1]='B';
    NodeNames[2]='C';
    NodeNames[3]='D';
    NodeNames[4]='E';
    NodeNames[5]='F';
    NodeNames[6]='G';
    NodeNames[7]='H';
    NodeNames[8]='I';
    int[][]   TLink={{0  ,0  ,200,0  ,100,0  ,0  ,0  ,0  ,0  }
                    ,{0  ,0  ,150,0  ,150,0  ,0  ,100,0  ,0  }
                    ,{200,150,0  ,100,0  ,0  ,0  ,0  ,0  ,0  }
                    ,{0  ,0  ,100,0  ,0  ,0  ,0  ,0  ,0  ,0  }
                    ,{100,150,0  ,0  ,0  ,200,100,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,200,0  ,0  ,0  ,300,0  }
                    ,{0  ,0  ,0  ,0  ,100,0  ,0  ,150,200,0  }
                    ,{0  ,100,0  ,0  ,0  ,0  ,150,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,0  ,300,200,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  } };
    for(counti=0;counti<10;++counti)
      for(countj=0;countj<10;++countj)
        LinkBW[counti][countj]=TLink[counti][countj];
    int[][]  TDLink={{0  ,0  ,2  ,0  ,7  ,0  ,0  ,0  ,0  ,0  }
                    ,{0  ,0  ,15 ,0  ,20 ,0  ,0  ,9  ,0  ,0  }
                    ,{2  ,15 ,0  ,11 ,0  ,0  ,0  ,0  ,0  ,0  }
                    ,{0  ,0  ,11 ,0  ,0  ,0  ,0  ,0  ,0  ,0  }
                    ,{7  ,20 ,0  ,0  ,0  ,12 ,12 ,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,12 ,0  ,0  ,0  ,3  ,0  }
                    ,{0  ,0  ,0  ,0  ,12 ,0  ,0  ,8  ,6  ,0  }
                    ,{0  ,9  ,0  ,0  ,0  ,0  ,8  ,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,0  ,3  ,6  ,0  ,0  ,0  }
                    ,{0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  ,0  } };
    for(counti=0;counti<10;++counti)
      for(countj=0;countj<10;++countj)
        LinkPropagDel[counti][countj]=TDLink[counti][countj];
    int[][]  TCLink={{32001,32001,7    ,32001,5    ,32001,32001,32001,32001,32001}
                    ,{32001,32001,12   ,32001,3    ,32001,32001,11   ,32001,32001}
                    ,{7    ,12   ,32001,8    ,32001,32001,32001,32001,32001,32001}
                    ,{32001,32001,8    ,32001,32001,32001,32001,32001,32001,32001}
                    ,{5    ,3    ,32001,32001,32001,14   ,16   ,32001,32001,32001}
                    ,{32001,32001,32001,32001,14   ,32001,32001,32001,5    ,32001}
                    ,{32001,32001,32001,32001,16   ,32001,32001,10   ,9    ,32001}
                    ,{32001,11   ,32001,32001,32001,32001,10   ,32001,32001,32001}
                    ,{32001,32001,32001,32001,32001,5    ,9    ,32001,32001,32001}
                    ,{32001,32001,32001,32001,32001,32001,32001,32001,32001,32001} };
    for(counti=0;counti<10;++counti)
      for(countj=0;countj<10;++countj)
        LinkCost[counti][countj]=TCLink[counti][countj];
    /*MyMapping.SetLoc(0,'A');
    MyMapping.SetLoc(1,'A');
    MyMapping.SetLoc(2,'B');
    MyMapping.SetLoc(3,'H');
    MyMapping.SetLoc(4,'D');
    MyMapping.SetLoc(5,'F');
    MyMapping.SetLoc(6,'B');
    MyMapping.SetLoc(7,'C');
    MyMapping.SetLoc(8,'I');
    MyMapping.SetLoc(9,'E');*/
    LocatingData('A','B','C','D','E','F','G','H','I','Z');
  }
  private void LocatingData(char p1,char p2,char p3,char p4,char p5,char p6,char p7,char p8,char p9,char p10){
    int counti,NodeCount=10,Loc;
    Random MyRand=new Random();
    if(p2=='Z')NodeCount=1;
    if(p3=='Z')NodeCount=2;
    if(p4=='Z')NodeCount=3;
    if(p5=='Z')NodeCount=4;
    if(p6=='Z')NodeCount=5;
    if(p7=='Z')NodeCount=6;
    if(p8=='Z')NodeCount=7;
    if(p9=='Z')NodeCount=8;
    if(p10=='Z')NodeCount=9;
    for(counti=0;counti<100;++counti){
        Loc=(int)(MyRand.nextFloat()*NodeCount);
        if(Loc==NodeCount)Loc--;
        switch(Loc){
          case 0:
            MyMapping.SetLoc(counti,p1);
          case 1:
            MyMapping.SetLoc(counti,p2);
          case 2:
            MyMapping.SetLoc(counti,p3);
          case 3:
            MyMapping.SetLoc(counti,p4);
          case 4:
            MyMapping.SetLoc(counti,p5);
          case 5:
            MyMapping.SetLoc(counti,p6);
          case 6:
            MyMapping.SetLoc(counti,p7);
          case 7:
            MyMapping.SetLoc(counti,p8);
          case 8:
            MyMapping.SetLoc(counti,p9);
          case 9:
            MyMapping.SetLoc(counti,p10);
          default:
            MyMapping.SetLoc(counti,p1);
        }
      }

  }
  public void moveDataItem(int DI,char DL){
    char DestOfData;
    char SourceOfData;
    int DataLength=1000;
    int count=0;
    DestOfData=DL;
    if(MyMapping.GetMoving(DI)==false){
        MyMapping.SetMovin(DI);
        SourceOfData=MyMapping.GetLoc(DI);
        pack recent=new pack(clock,DestOfData,SourceOfData,DataLength,100,DataPacketSN,true,'D',DI);
        for(count=0;count<NodeCount;++count)
          if(Nodes[count].Name==SourceOfData){
            Nodes[count].Get(recent);
            break;
          }
        DataPacketSN++;

      }
  }
  public void moveDataItemCommit(int DI,char DL,int Delay){
    float RecDDel=0;
    RecDDel=AvgDataPacketDelay*(DataPacketSN-1);
    AvgDataPacketDelay=(RecDDel+(float)Delay)/DataPacketSN;
    MyMapping.RemoveMoving(DI);
    MyMapping.MoveLoc(DI,DL);
  }
  public char GetDataItemLoc(int DI){
    return(MyMapping.GetLoc(DI));
  }
  private void setNodeObjects(){
    int count=0;
    for(count=0;count<NodeCount;++count)
      try{
      Nodes[count].SetParam(NodeNames[count],this,RouteType,AllocT);
      }catch(RuntimeException r){
        System.out.println("RunTime Exception PPPPP "+ r);
      };
  }
  private void setLinkObjects(){
    int counti,countj;
    LinkCount=0;
    for(counti=0;counti<(NodeCount-1);++counti)
      for(countj=counti+1;countj<NodeCount;++countj)
        if(LinkBW[counti][countj]!=0){
          TempL=new MyLink(Nodes[counti],Nodes[countj],LinkBW[counti][countj],LinkPropagDel[counti][countj],this);
          Links[LinkCount]=TempL;
          Nodes[counti].AddLink(Links[LinkCount]);
          Nodes[countj].AddLink(Links[LinkCount]);
          ++LinkCount;
          };

  }
  private void updateDelayMatrix(){
    int counti,countj,countt;
    countt=0;
    if(MatrixUT==0){
      for(counti=0;counti<(NodeCount-1);++counti)
        for(countj=counti+1;countj<NodeCount;++countj)
          if(LinkBW[counti][countj]!=0){
            LinkDelay[counti][countj]=Links[countt].GetLinkDelay();
            LinkDelay[countj][counti]=Links[countt].GetLinkDelay();
            Links[countt].FlushLinkQDel();
            ++countt;
            };
      MatrixUT=MatrixUF;
    }else MatrixUT--;
  }
  public StringBuffer NodeStatus(int index){
    float AGD=0;
    int RPCount=0;
    StringBuffer statusValue=new StringBuffer("");
    if(NodeNames[index]=='\0'){
      statusValue.append("Null Value");
      } else {
        statusValue.append(" NodeName  "+NodeNames[index]+" ");
        AGD=Nodes[index].GetAvgDelay();
        RPCount=Nodes[index].GetRecievedPackCount();
        statusValue.append("Average Delay:"+Float.toString(AGD));
        statusValue.append("  Recieved Packet Count:"+Integer.toString(RPCount));
      };
    return(statusValue);
  }
  public StringBuffer LinkStatus(int index){
    float AGD=0;
    float Utl=0;
    int IdleC=0;
    StringBuffer statusValue=new StringBuffer("");
    statusValue.append(" Link between  "+Links[index].GetHead1()+" and "+ Links[index].GetHead2());
    AGD=Links[index].GetOverALinkDelay();
    Utl=Links[index].GetTotalUtilization();
    IdleC=Links[index].GetTIdle();
    statusValue.append(" Average Delay:"+Float.toString(AGD));
    statusValue.append("  Link Utilization:"+Float.toString(Utl));
    statusValue.append("  Idle Count:"+Integer.toString(IdleC));
    return(statusValue);
  }

  private void changeLCost(int i, int j, float mult, int added){
    LinkCost[i][j]=(int)(LinkCost[i][j]*mult+added);
    LinkCost[j][i]=(int)(LinkCost[j][i]*mult+added);
  }
  private void ScheduledChangeLC(int n,int s,int i, int j, float m, int a){
    if(n==s)changeLCost(i,j,m,a);

  }
  public int GetMaxLinks(){
  return(LinkCount);
  }
  private class DataMap{
    private int moveCount;
    private boolean[] Movement=new boolean[100];
    private char Location[]=new char[100];
    public DataMap(){
    int i=0;
    moveCount=0;
    for(i=0;i<100;++i)Movement[i]=false;
    }
    public void SetLoc(int DI,char L){
      Location[DI]=L;
    }
    public void MoveLoc(int DI,char L){
      moveCount++;
      Location[DI]=L;
    }
    public int GetMoveCount(){
      return(moveCount);
    }

    public char GetLoc(int DI){
      return(Location[DI]);
    }
    public void SetMovin(int DI){
      Movement[DI]=true;
    }
    public void RemoveMoving(int DI){
      Movement[DI]=false;
    }
    public boolean GetMoving(int DI){
      return(Movement[DI]);
    }

  }
}