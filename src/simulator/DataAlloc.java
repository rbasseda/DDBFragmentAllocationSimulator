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
  private MyNode Owner;
  private char AllocType;
  private int DCounter[][]=new int[10][100];
  public DataAlloc(MyNode O,char allT) {
    int i;
    int j;
    Owner=O;
    AllocType=allT;
    for(i=0;i<10;++i)
      for(j=0;j<100;++j)DCounter[i][j]=0;
  }
  public void SetAllType(char AT){
    AllocType=AT;
  }
  public void Hint(char D, int DI){
    int i=0;
    int j=0;
    int zero=0;
    int Commolative=0;
    char NextN='Z';
    switch(AllocType){  //Normal Allocation
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
        DCounter[DI][i]++;
        if(DCounter[DI][i]>10){
          Owner.MoveDataLoc(DI,D);
          System.out.print(" Time: "+Integer.toString(Owner.GetMyClock()));
          System.out.print(" Migrate Data "+Integer.toString(DI)+" from "+Owner.Name+" to "+D+"\n");
          DCounter[DI][i]=0;
          }
        break;
        case 'R':
        NextN=Owner.GetNextNodeInRoute(D);
        if(NextN=='A')i=0;
        if(NextN=='B')i=1;
        if(NextN=='C')i=2;
        if(NextN=='D')i=3;
        if(NextN=='E')i=4;
        if(NextN=='F')i=5;
        if(NextN=='G')i=6;
        if(NextN=='H')i=7;
        if(NextN=='I')i=8;
        DCounter[DI][i]++;
        for(j=0;j<100;++j){
          Commolative+=DCounter[DI][j];
          if(DCounter[DI][j]==0)zero++;
          }
        if(DCounter[DI][i]>5){
        //if(DCounter[DI][i]>(2*(Commolative/(100-zero)))&&DCounter[DI][i]>10){
          Owner.MoveDataLoc(DI,NextN);
          System.out.print(" Time: "+Integer.toString(Owner.GetMyClock()));
          System.out.print(" Migrate Data "+Integer.toString(DI)+" from "+Owner.Name+" to "+NextN+"\n");
          DCounter[DI][i]=0;
          }
        break;

    }
  }
}