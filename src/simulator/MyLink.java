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
  private MyNode H1;//=new MyNode();
  private MyNode H2;//=new MyNode();
  private  boolean busy;
  private  int BW;
  private  int D;
  private  int clock,pclock,rclock,direction,CountQDel,OACountQDel;
  private  float AvgQDel;
  private  float OAAvgQDel;
  private  pack ponl;
  private  coordinator coord;
  private  int IdleCount,TIdleCount;
  public MyLink(MyNode h1, MyNode h2,int bw,int del, coordinator co){
    clock=0;
    rclock=0;
    H1=h1;
    H2=h2;
    BW=bw;
    D=del;
    coord=co;
    AvgQDel=0;
    OAAvgQDel=0;
    CountQDel=0;
    OACountQDel=0;
    IdleCount=0;
    TIdleCount=0;
    busy=false;
    }
  public MyLink(){
    clock=0;
    rclock=0;
    AvgQDel=0;
    CountQDel=0;
    IdleCount=0;
    TIdleCount=0;
    busy=false;
    }
  public boolean Get(pack p,MyNode Head){
    int pl=0;
    clock=coord.GetClock();
    if(pclock<=(clock-1)){
      pl=p.GetL();
      rclock=clock+(int)(pl/BW)+1;
      pclock=clock+(int)(pl/BW)+D+1;
      ponl=new pack();
      ponl=p;
      busy=true;
      if(Head.Name==H1.Name){
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
  public void Notify(int cl){
    clock=cl;
    if(busy==false){
      IdleCount++;
      TIdleCount++;
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
      Put();
      busy=false;
      ponl=null;
      };

    }
  public float GetTotalUtilization(){
    float Temp;
    Temp=(float)(1-(float)((float)TIdleCount/(float)coord.GetSimLength()));
    return(Temp);
  }
  public int GetTIdle(){
    return(TIdleCount);
  }
  public float GetUtilization(int Ratio){
    float Temp;
    int IdleTemp;
    IdleTemp=IdleCount;
    IdleCount=0;
    Temp=(float)(1-(float)((float)IdleTemp/(float)Ratio));
    return(Temp);
  }
  private void Put(){
    AvgQDel=(float)(AvgQDel*CountQDel+ponl.CalcEnqDelay(clock))/(CountQDel +1);
    OAAvgQDel=(float)(OAAvgQDel*OACountQDel+ponl.CalcEnqDelay(clock))/(OACountQDel +1);
    CountQDel++;
    OACountQDel++;
    if(direction==1){
      H2.Get(ponl);
      //System.out.println("Link Between: "+H1.Name+" to "+H2.Name+" for "+ponl.GetS()+" to "+ponl.GetD());
      }
    else {
      H1.Get(ponl);
      //System.out.println("Link Between: "+H2.Name+" to "+H1.Name+" for "+ponl.GetS()+" to "+ponl.GetD());
      };

    }
  public char GetHead1(){
    return(H1.Name);
  }
  public char GetHead2(){
    return(H2.Name);
  }
  public char GetOtherHead(char HH){
    if(H1.Name==HH)return(H2.Name);
    if(H2.Name==HH)return(H1.Name);
    return('0');
  }
  public float GetLinkDelay(){
    return(AvgQDel);
  }
  public float GetOverALinkDelay(){
    return(OAAvgQDel);
  }
  public void FlushLinkQDel(){
    AvgQDel=0;
    CountQDel=0;
  }
  }
