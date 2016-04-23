package simulator;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

public class MyLink{
  private MyNode head1;//=new MyNode();
  private MyNode head2;//=new MyNode();
  private  boolean busy;
  private  int bandwidth;
  private  int delay;
  private  int clock,pclock,rclock,direction,countQDel,oaCountQDel;
  private  float avgQDel;
  private  float oaAvgQDel;
  private  Packet ponl;
  private  Coordinator coord;
  private  int idleCount,tIdleCount;
  public MyLink(MyNode h1, MyNode h2,int bw,int del, Coordinator co){
    clock=0;
    rclock=0;
    head1=h1;
    head2=h2;
    bandwidth=bw;
    delay=del;
    coord=co;
    avgQDel=0;
    oaAvgQDel=0;
    countQDel=0;
    oaCountQDel=0;
    idleCount=0;
    tIdleCount=0;
    busy=false;
    }
  public MyLink(){
    clock=0;
    rclock=0;
    avgQDel=0;
    countQDel=0;
    idleCount=0;
    tIdleCount=0;
    busy=false;
    }
  public boolean getPackage(Packet p,MyNode Head){
    int pl=0;
    clock=coord.getClock();
    if(pclock<=(clock-1)){
      pl=p.getLength();
      rclock=clock+(int)(pl/bandwidth)+1;
      pclock=clock+(int)(pl/bandwidth)+delay+1;
      ponl=new Packet();
      ponl=p;
      busy=true;
      if(Head.name==head1.name){
        direction=1;
        //System.out.print("Packet is putted on Link between "+H1.Name+" to "+H2.Name);
        //System.out.print(" for transmitting "+p.GetS()+" to "+p.GetD());
        //System.out.println(" will released at "+Integer.toString(pclock)+" now: "+Integer.toString(clock) );
        }
      else {
        direction=2;
        //System.out.print("Packet is putted on Link between "+H2.Name+" to "+H1.Name);
        //System.out.print(" for transmitting "+p.GetS()+" to "+p.GetD());
        //System.out.println(" will released at "+Integer.toString(pclock)+" now: "+Integer.toString(clock) );
        };

      return(true);
      }
      else{
        return(false);
      }
    }
  public void notify(int cl){
    clock=cl;
    if(busy==false){
      idleCount++;
      tIdleCount++;
      return;
      };
    if(pclock<=clock){
      if(direction==1){
        //System.out.print("Packet is released from Link between "+H1.Name+" to "+H2.Name);
        //System.out.println(" for transmitting "+ponl.GetS()+" to "+ponl.GetD());
        }
      else {
        //System.out.print("Packet is released from Link between "+H2.Name+" to "+H1.Name);
        //System.out.println(" for transmitting "+ponl.GetS()+" to "+ponl.GetD());
        };
      put();
      busy=false;
      ponl=null;
      };

    }
  public float getTotalUtilization(){
    float Temp;
    Temp=(float)(1-(float)((float)tIdleCount/(float)coord.getSimLength()));
    return(Temp);
  }
  public int getTIdle(){
    return(tIdleCount);
  }
  public float getUtilization(int ratio){
    float temp;
    int idleTemp;
    idleTemp=idleCount;
    idleCount=0;
    temp=(float)(1-(float)((float)idleTemp/(float)ratio));
    return(temp);
  }
  private void put(){
    avgQDel=(float)(avgQDel*countQDel+ponl.calculateEnqueueDelay(clock))/(countQDel +1);
    oaAvgQDel=(float)(oaAvgQDel*oaCountQDel+ponl.calculateEnqueueDelay(clock))/(oaCountQDel +1);
    countQDel++;
    oaCountQDel++;
    if(direction==1){
      head2.recievePacket(ponl);
      //System.out.println("Link Between: "+H1.Name+" to "+H2.Name+" for "+ponl.GetS()+" to "+ponl.GetD());
      }
    else {
      head1.recievePacket(ponl);
      //System.out.println("Link Between: "+H2.Name+" to "+H1.Name+" for "+ponl.GetS()+" to "+ponl.GetD());
      };

    }
  public char getHead1(){
    return(head1.name);
  }
  public char getHead2(){
    return(head2.name);
  }
  public char getOtherHead(char oneHead){
    if(head1.name==oneHead)return(head2.name);
    if(head2.name==oneHead)return(head1.name);
    return('0');
  }
  public float getLinkDelay(){
    return(avgQDel);
  }
  public float getOverALinkDelay(){
    return(oaAvgQDel);
  }
  public void flushLinkQDel(){
    avgQDel=0;
    countQDel=0;
  }
  }
