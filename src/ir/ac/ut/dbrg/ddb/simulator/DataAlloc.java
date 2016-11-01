package simulator;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

public class DataAlloc {
  private MyNode owner;
  private char allocType;
  private int dCounter[][]=new int[10][100];
  public DataAlloc(MyNode O,char allT) {
    int i;
    int j;
    owner=O;
    allocType=allT;
    for(i=0;i<10;++i)
      for(j=0;j<100;++j)dCounter[i][j]=0;
  }
  public void setAllType(char AT){
    allocType=AT;
  }
  public void hint(char D, int DI){
    int i=0;
    int j=0;
    int zero=0;
    int Commolative=0;
    char NextN='Z';
    switch(allocType){  //Normal Allocation
      case 'N':
        if(D=='A')i=0;
        if(D=='B')i=1;
        if(D=='C')i=2;
        if(D=='D')i=3;
        if(D=='E')i=4;
        if(D=='F')i=5;
        if(D=='G')i=6;
        if(D=='H')i=7;
        if(D=='I')i=8;
        dCounter[DI][i]++;
        if(dCounter[DI][i]>10){
          owner.moveDataLocation(DI,D);
          System.out.print(" Time: "+Integer.toString(owner.getClock()));
          System.out.print(" Migrate Data "+Integer.toString(DI)+" from "+owner.name+" to "+D+"\n");
          dCounter[DI][i]=0;
          }
        break;
        case 'R':
        NextN=owner.getNextNodeInRoute(D);
        if(NextN=='A')i=0;
        if(NextN=='B')i=1;
        if(NextN=='C')i=2;
        if(NextN=='D')i=3;
        if(NextN=='E')i=4;
        if(NextN=='F')i=5;
        if(NextN=='G')i=6;
        if(NextN=='H')i=7;
        if(NextN=='I')i=8;
        dCounter[DI][i]++;
        for(j=0;j<100;++j){
          Commolative+=dCounter[DI][j];
          if(dCounter[DI][j]==0)zero++;
          }
        if(dCounter[DI][i]>5){
        //if(DCounter[DI][i]>(2*(Commolative/(100-zero)))&&DCounter[DI][i]>10){
          owner.moveDataLocation(DI,NextN);
          System.out.print(" Time: "+Integer.toString(owner.getClock()));
          System.out.print(" Migrate Data "+Integer.toString(DI)+" from "+owner.name+" to "+NextN+"\n");
          dCounter[DI][i]=0;
          }
        break;

    }
  }
}