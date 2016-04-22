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

public class Coordinator {
    private int clock,simLength;
    private int dataPacketSN=0;
    private float avgDataPacketDelay=0;
    private MyNode[] nodes=new MyNode[10];
    private MyNode temp;
    private MyLink tempL;
    private MyLink[] links=new MyLink[100];
    private int nodeCount,linkCount;
    private char nodeNames[]=new char[10];
    private int linkCost[][]=new int [10][10];
    private float linkDelay[][]=new float [10][10];
    private int linkBW[][]=new int [10][10];
    private int linkPropagDel[][]=new int [10][10];
    private int matrixUF=100,matrixUT;
    private DataMap myMapping=new DataMap();
    private int routeType;
    private int[] dropFlow=new int[20];
    private int flowCnt;
    private char allocT='Q';
  public Coordinator() {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          linkDelay[i][j]=0;
          linkBW[i][j]=1;
          linkPropagDel[i][j]=32001;
          linkCost[i][j]=32001;
        };
      nodeNames[i]='\0';
      };
    nodeCount=0;
    linkCount=0;
    simLength=100;
    matrixUT=matrixUF;
    routeType=0;
    flowCnt=0;
  }
  public Coordinator(int SimL, int RouteT) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          linkDelay[i][j]=0;
          linkBW[i][j]=1;
          linkPropagDel[i][j]=32001;
          linkCost[i][j]=32001;
        };
      nodeNames[i]='\0';
      };
    nodeCount=0;
    linkCount=0;
    simLength=SimL;
    matrixUT=matrixUF;
    routeType=RouteT;
    flowCnt=0;
  }
  public Coordinator(int SimL, int RouteT,char AllT) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          linkDelay[i][j]=0;
          linkBW[i][j]=1;
          linkPropagDel[i][j]=32001;
          linkCost[i][j]=32001;
        };
      nodeNames[i]='\0';
      };
    nodeCount=0;
    linkCount=0;
    simLength=SimL;
    matrixUT=matrixUF;
    routeType=RouteT;
    allocT=AllT;
    flowCnt=0;
  }

  public Coordinator(int SimL) {
    int i,j;
    for(i=0;i<10;++i){
      for(j=0;j<10;++j){
          linkDelay[i][j]=0;
          linkBW[i][j]=1;
          linkPropagDel[i][j]=32001;
          linkCost[i][j]=32001;
        };
      nodeNames[i]='\0';
      };
    nodeCount=0;
    linkCount=0;
    simLength=SimL;
    matrixUT=matrixUF;
    routeType=0;
  }
  public int getClock(){
    return(clock);
    }
  public void runCoord(RoutingUI MyUI){
    RoutingUI TempUI;
    TempUI=MyUI;
    int count;
    topology1();
    for(count=0;count<nodeCount;++count){
      try{
        temp=new MyNode();
        nodes[count]=temp;
        nodes[count].SetParam(nodeNames[count],(Coordinator)this,routeType,allocT);
      }catch (RuntimeException r){
        System.out.println("RunTime Exception Creation On My Node "+ r);
      };
      };
    setLinkObjects();
    senarioSimpleTest2();
    //Sequencer
    for(clock=0;clock<simLength;++clock){
      for(count=0;count<nodeCount;++count)
        nodes[count].Notify(clock);
      for(count=0;count<linkCount;++count)
        links[count].notify(clock);
      changeCostSenario1(clock);
      updateDelayMatrix();
    };
    TempUI.updateClock(clock);
  }
  public int getSimLength(){
    return(simLength);
  }
  public float getLinkDelay(int i,int j){
    return(linkDelay[i][j]);
  }
  public int getLinkCost(int i, int j){
    return(linkCost[i][j]);
  }
  public char getNodeName(int index){
    return(nodeNames[index]);
  }
  public int getMaxNodeCount(){
    return(nodeCount);
  }
    private void senario2(int RateF){
    nodes[0].AddPackGenerator(2,700,'I',10+RateF,100,1);
    nodes[3].AddPackGenerator(3,3000,'G',5,100+RateF,2);
    flowCnt=2;
  }
  private void senario1(int RateF){
    nodes[0].AddPackGenerator(500,1000,'B',5+RateF,150,1);
    nodes[0].AddPackGenerator(5000,70000,'B',5+RateF,150,2);
    nodes[0].AddPackGenerator(5000,70000,'D',5+RateF,150,3);
    nodes[1].AddPackGenerator(150,20000,'F',10+RateF,150,4);
    nodes[2].AddPackGenerator(100,40000,'H',5+RateF,100,5);
    nodes[3].AddPackGenerator(150,30000,'G',10+RateF,200,6);
    nodes[4].AddPackGenerator(1500,3000,'G',10+RateF,200,7);
    nodes[7].AddPackGenerator(500,900,'B',15+RateF,300,8);
    nodes[8].AddPackGenerator(1000,4000,'C',10+RateF,300,9);
    nodes[8].AddPackGenerator(2000,3000,'A',10+RateF,200,10);
    nodes[3].AddPackGenerator(3000,3500,'I',20+RateF,300,11);
    nodes[6].AddPackGenerator(1000,4000,'E',30+RateF,300,12);
    flowCnt=12;
  }

  private void senario2(){
    nodes[0].AddPackGenerator(2,700,'I',1,100,1);
    nodes[3].AddPackGenerator(3,3000,'G',5,100,4);
    flowCnt=2;
  }
  private void senario1(){
    nodes[0].AddPackGenerator(500,1000,'B',5,150,1);
    nodes[0].AddPackGenerator(5000,70000,'B',5,150,1);
    nodes[0].AddPackGenerator(5000,70000,'D',5,150,1);
    nodes[1].AddPackGenerator(150,20000,'F',10,150,2);
    nodes[2].AddPackGenerator(100,40000,'H',5,100,3);
    nodes[3].AddPackGenerator(150,30000,'G',10,200,4);
    nodes[4].AddPackGenerator(1500,3000,'G',10,2000,4);
    nodes[8].AddPackGenerator(500,900,'B',15,300,5);
    nodes[8].AddPackGenerator(1000,4000,'C',10,300,5);
    flowCnt=9;
  }
  private void senarioSimpleTest1(){
    nodes[0].AddPackGenerator(0,10000,100,1);
    nodes[8].AddPackGenerator(20000,100000,100,1);
    flowCnt=2;
  }
  private void senarioSimpleTest2(){
    nodes[0].AddPackGenerator(0,60000,100,1);
    nodes[2].AddPackGenerator(0,60000,50,1);
    nodes[3].AddPackGenerator(0,60000,50,1);
    nodes[7].AddPackGenerator(0,60000,50,1);
    //Nodes[8].AddPackGenerator(20000,100000,100,1);
    flowCnt=4;
  }

  private void changeCostSenario1(int cl){
    scheduledChangeLC(cl,500,1,2,(float)0.5,1);//B-C change
    scheduledChangeLC(cl,600,0,4,0,5);//A-E change
    scheduledChangeLC(cl,300,4,5,0,-3);//E-F change
    scheduledChangeLC(cl,800,6,7,(float)0.2,1);//G-H change
  }
  public void addDrop(int FID){
    dropFlow[FID]++;
  }
  public StringBuffer getFlowStatus(){
    StringBuffer Temp=new StringBuffer("\n");
    int count=0;
    for(count=1;count<=flowCnt;++count)
      Temp.append("\n Flow Count "+Integer.toString(count)+" droped count "+Integer.toString(dropFlow[count]));
    return(Temp);
  }
  public StringBuffer getOverAllRep(){
    StringBuffer Temp=new StringBuffer("");
    int count=0;
    int TDrop=0;
    float TAvgQN=0,TAvgQNV=0;
    float TAvgQL=0,TAvgQLV=0;
    float TAvgUL=0,TAvgULV=0;
    int TRPCount=0;
    for(count=1;count<=flowCnt;++count)
      TDrop=TDrop+dropFlow[count];
    for(count=0;count<nodeCount;++count)
      TRPCount=TRPCount+nodes[count].GetRecievedPackCount();
    for(count=0;count<nodeCount;++count)
      TAvgQN=TAvgQN+nodes[count].GetAvgDelay();
    TAvgQNV=TAvgQN/nodeCount;
    for(count=0;count<linkCount;++count)
      TAvgQL=TAvgQL+links[count].getOverALinkDelay();
    TAvgQLV=TAvgQL/linkCount;
    for(count=0;count<linkCount;++count)
      TAvgUL=TAvgUL+links[count].getTotalUtilization();
    TAvgULV=TAvgUL/linkCount;
    Temp.append(" Drop Count "+Integer.toString(TDrop)+" Avg Delay On Node "+Float.toString(TAvgQNV)+" Total Count of Recieved Packet by Nodes "+Integer.toString(TRPCount));
    Temp.append("\n Avg Delay On Link "+Float.toString(TAvgQLV)+" Avg Link Utillization On Link "+Float.toString(TAvgULV));
    Temp.append("\n Avg Delay On Data Migration: "+Float.toString(avgDataPacketDelay));
    return(Temp);
  }
  private void topology1(){
    int counti,countj,Loc;
    nodeCount=9;
    nodeNames[0]='A';
    nodeNames[1]='B';
    nodeNames[2]='C';
    nodeNames[3]='D';
    nodeNames[4]='E';
    nodeNames[5]='F';
    nodeNames[6]='G';
    nodeNames[7]='H';
    nodeNames[8]='I';
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
        linkBW[counti][countj]=TLink[counti][countj];
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
        linkPropagDel[counti][countj]=TDLink[counti][countj];
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
        linkCost[counti][countj]=TCLink[counti][countj];
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
            myMapping.SetLoc(counti,p1);
          case 1:
            myMapping.SetLoc(counti,p2);
          case 2:
            myMapping.SetLoc(counti,p3);
          case 3:
            myMapping.SetLoc(counti,p4);
          case 4:
            myMapping.SetLoc(counti,p5);
          case 5:
            myMapping.SetLoc(counti,p6);
          case 6:
            myMapping.SetLoc(counti,p7);
          case 7:
            myMapping.SetLoc(counti,p8);
          case 8:
            myMapping.SetLoc(counti,p9);
          case 9:
            myMapping.SetLoc(counti,p10);
          default:
            myMapping.SetLoc(counti,p1);
        }
      }

  }
  public void moveDataItem(int DI,char DL){
    char DestOfData;
    char SourceOfData;
    int DataLength=1000;
    int count=0;
    DestOfData=DL;
    if(myMapping.GetMoving(DI)==false){
        myMapping.SetMovin(DI);
        SourceOfData=myMapping.GetLoc(DI);
        pack recent=new pack(clock,DestOfData,SourceOfData,DataLength,100,dataPacketSN,true,'D',DI);
        for(count=0;count<nodeCount;++count)
          if(nodes[count].Name==SourceOfData){
            nodes[count].Get(recent);
            break;
          }
        dataPacketSN++;

      }
  }
  public void moveDataItemCommit(int DI,char DL,int Delay){
    float RecDDel=0;
    RecDDel=avgDataPacketDelay*(dataPacketSN-1);
    avgDataPacketDelay=(RecDDel+(float)Delay)/dataPacketSN;
    myMapping.RemoveMoving(DI);
    myMapping.MoveLoc(DI,DL);
  }
  public char getDataItemLoc(int DI){
    return(myMapping.GetLoc(DI));
  }
  private void setNodeObjects(){
    int count=0;
    for(count=0;count<nodeCount;++count)
      try{
      nodes[count].SetParam(nodeNames[count],this,routeType,allocT);
      }catch(RuntimeException r){
        System.out.println("RunTime Exception PPPPP "+ r);
      };
  }
  private void setLinkObjects(){
    int counti,countj;
    linkCount=0;
    for(counti=0;counti<(nodeCount-1);++counti)
      for(countj=counti+1;countj<nodeCount;++countj)
        if(linkBW[counti][countj]!=0){
          tempL=new MyLink(nodes[counti],nodes[countj],linkBW[counti][countj],linkPropagDel[counti][countj],this);
          links[linkCount]=tempL;
          nodes[counti].AddLink(links[linkCount]);
          nodes[countj].AddLink(links[linkCount]);
          ++linkCount;
          };

  }
  private void updateDelayMatrix(){
    int counti,countj,countt;
    countt=0;
    if(matrixUT==0){
      for(counti=0;counti<(nodeCount-1);++counti)
        for(countj=counti+1;countj<nodeCount;++countj)
          if(linkBW[counti][countj]!=0){
            linkDelay[counti][countj]=links[countt].getLinkDelay();
            linkDelay[countj][counti]=links[countt].getLinkDelay();
            links[countt].flushLinkQDel();
            ++countt;
            };
      matrixUT=matrixUF;
    }else matrixUT--;
  }
  public StringBuffer nodeStatus(int index){
    float AGD=0;
    int RPCount=0;
    StringBuffer statusValue=new StringBuffer("");
    if(nodeNames[index]=='\0'){
      statusValue.append("Null Value");
      } else {
        statusValue.append(" NodeName  "+nodeNames[index]+" ");
        AGD=nodes[index].GetAvgDelay();
        RPCount=nodes[index].GetRecievedPackCount();
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
    statusValue.append(" Link between  "+links[index].getHead1()+" and "+ links[index].getHead2());
    AGD=links[index].getOverALinkDelay();
    Utl=links[index].getTotalUtilization();
    IdleC=links[index].getTIdle();
    statusValue.append(" Average Delay:"+Float.toString(AGD));
    statusValue.append("  Link Utilization:"+Float.toString(Utl));
    statusValue.append("  Idle Count:"+Integer.toString(IdleC));
    return(statusValue);
  }

  private void changeLCost(int i, int j, float mult, int added){
    linkCost[i][j]=(int)(linkCost[i][j]*mult+added);
    linkCost[j][i]=(int)(linkCost[j][i]*mult+added);
  }
  private void scheduledChangeLC(int n,int s,int i, int j, float m, int a){
    if(n==s)changeLCost(i,j,m,a);

  }
  public int getMaxLinks(){
  return(linkCount);
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