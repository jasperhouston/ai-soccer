import java.util.concurrent.TimeUnit;
/**
 * Write a description of class Silverbacks here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Silverbacks extends Player
{
    static public final int NORTH = 0;
    static public final int NORTHEAST = 1;
    static public final int EAST = 2;
    static public final int SOUTHEAST = 3;
    static public final int SOUTH = 4;
    static public final int SOUTHWEST = 5;
    static public final int WEST = 6;
    static public final int NORTHWEST = 7;
    static public final int PLAYER = 8;
    static public final int BALL = 9;
    static public final int KICK = 10;
    static public final int BOUNDARY = 11;
    static public final int EASTPLAYER = 12;
    static public final int WESTPLAYER = 13;
    static public final int OPPONENT = 14;
    static public final int TEAMMATE = 15;
    static public final int EMPTY = 16;
    static public final int GOALLINE = 17;
    static public final int SIDELINE = 18;
    static public final int XSQUARES = 80;
    static public final int YSQUARES = 40;

    static int[] plx = new int[4];
    static int[] ply = new int[4];
    static int[] look = new int[8];

    static int balldir;
    static int balldist;
    static int ballx;
    static int bally;
    static int[] oppdir= new int[4];
    static int[] oppdist = new int[4];
    static int[] oppx = new int[4];
    static int[] oppy = new int[4];

    public int getAction() {
        switch(ID)
        {
            case 1:
            return Player1();
            case 2:
            return Player2();
            case 3:
            return Player3();
            case 4:
            return Player4();
        }
        return BALL;
    }

    public int Player1() {
        return MakeMove();
    }

    public int Player2() {
        return MakeMove();
    }

    public int Player3() {
        return MakeMove();
    }

    public int Player4() {
        return MakeMove();
    }

    public int MakeMove() {
        GetData();
        if(balldist < 2) 
            return Leader();
        for(int i = 0; i < 4; i++) {
            if(i != ID && plx[ID-1] <= plx[i])
                return Leader();
        }
        return Leader();
    }

    public int Leader() {
        //If it's touching the ball, it acts according to SurroundBall() function
        if(balldist == 1) {
            return SurroundBall();
        }
        /*
        if(balldist == 2.2) {
            if((balldir == SOUTHEAST || balldir == NORTHEAST) && look[EAST] == EMPTY)
                return EAST;
            if(balldir == SOUTHWEST && look[SOUTH] == EMPTY)
                return SOUTH;
            if(balldir == NORTHWEST && look[NORTH] == EMPTY)
                return NORTH;   
        }
        */
        if(look[balldir] == EMPTY) {
            return balldir;
        }
        if(balldir%4 == 0) {
            return InterceptBall();
        }
        if(balldir == SOUTHWEST) {
            if(look[SOUTH] == EMPTY)
                return SOUTH;
            else if(look[WEST] == EMPTY)
                return WEST;
        }
        if(balldir == WEST) {
            if(look[NORTHWEST] == EMPTY)
                return NORTHWEST;
            else if(look[SOUTHWEST] == EMPTY)
                return SOUTHWEST;
        }
        if(balldir == NORTHWEST) {
            if(look[NORTH] == EMPTY)
                return NORTH;
            else if(look[WEST] == EMPTY)
                return NORTHWEST;
        }
        return PLAYER;        
    }

    //moves/kicks depending on where the ball is
    //I think there should be more situations in which it kicks it
    public int SurroundBall() {
        boolean opp_NE_ball = false;
        boolean opp_SE_ball = false;
        int offset_from_east = balldir - EAST;     
                    
        if(look[WEST] == BALL) {
            boolean opp_west = false;
            double opp_west_distance = 100;
            for(int i = 0; i < 4; i++) {
                if((double)((int)(oppdist[i])) == oppdist[i] && oppdist[i] < 10)
                    if(oppdir[i] == WEST) {
                        opp_west = true;
                        opp_west_distance = Math.min(opp_west_distance, oppdist[i]);
                    }
                if(oppdist[i] == Math.sqrt(5)) {
                    if(oppdir[i] == NORTHEAST)
                        opp_NE_ball = true;
                    else if(oppdir[i] == SOUTHEAST)
                        opp_SE_ball = true;
                }   
            }
            if(!opp_west)
                return KICK;
            else if(opp_west_distance > 3) {
                if(!opp_NE_ball && look[SOUTH] == EMPTY)
                    return SOUTH;
                if(!opp_SE_ball && look[NORTH] == EMPTY)
                    return NORTH;
            }
            if(look[NORTH] == TEAMMATE && look[SOUTH] == EMPTY)
                return SOUTH;
            if(look[SOUTH] == TEAMMATE && look[NORTH] == EMPTY)
                return NORTH;
        }
        else if(look[NORTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                double a = oppdist[i]/Math.sqrt(2);
                if((double)((int)(a)) == a)
                    opp_NE_ball = true;
            } 
            if(!opp_NE_ball)
                return KICK;
        }
        else if(look[SOUTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                double a = oppdist[i]/Math.sqrt(2);
                if((double)((int)(a)) == a)
                    opp_SE_ball = true;
            } 
            if(!opp_SE_ball)
                return KICK;      
        }
        else if(balldir == EAST) {
            if(look[NORTHEAST] == EMPTY)
                return NORTHEAST;
            else if(look[SOUTHEAST] == EMPTY)
                return SOUTHEAST;
            else if(look[NORTH] == EMPTY)
                return NORTH;
            else if(look[SOUTH] == EMPTY)
                return SOUTH;
        }
        else if(balldir == NORTHEAST || balldir == SOUTHEAST) {
            if(look[EAST] == EMPTY)
                return EAST;
            else if(look[EAST+2*offset_from_east] == EMPTY)
                return EAST+2*offset_from_east;
            else if(look[EAST-offset_from_east] == EMPTY)
                return EAST-offset_from_east;
            else if(look[((EAST+3*offset_from_east)+ 8) % 8] == EMPTY)
                return ((EAST+3*offset_from_east/2)+ 8) % 8;
        }
        //what to do here??
        else if(balldir == NORTH || balldir == SOUTH) {
            System.out.println("North or south of ball");
            int east_diagonal = EAST+offset_from_east/2;
            int west_diagonal = ((((EAST+3*offset_from_east/2) % 8) + 8) % 8);
            boolean teammate_in_way_of_kick = false;
            
            if(look[east_diagonal] == EMPTY) {
                System.out.println("East Diagonal");
                return east_diagonal;
            }
            if(look[EAST] == EMPTY) {
                System.out.println("East");                
                return EAST;
            }
            
            for(int i = 0; i < 4; i++) {
                System.out.println(ply[i]);
                if(ply[ID-1] - offset_from_east == ply[i])
                    teammate_in_way_of_kick = true;
            }
            //if there is an opponent to the northwest and no teammate to the northeast
            if(look[east_diagonal] != TEAMMATE && look[west_diagonal] == OPPONENT) {
                if(!teammate_in_way_of_kick) {
                    System.out.println("Kick1");
                    return KICK;
                }
            }
            if(look[WEST] == OPPONENT) {
                boolean teammate_can_block = false;
                for(int i = 0; i < 4; i++) {
                    if(plx[ID-1] == plx[i] && ply[ID-1] - offset_from_east == ply[i])
                        teammate_can_block = true;
                }
                if(!teammate_can_block && !teammate_in_way_of_kick) {
                    System.out.println("Kick2");
                    return KICK;
                }
            }
            if(look[east_diagonal] == EMPTY) {
                System.out.println("East Diagonal");
                return east_diagonal;
            }
            if(look[EAST] == EMPTY) {
                System.out.println("East");                
                return EAST;
            }
        }
        //does nothing (PLAYER) if there's an opponent on the other side of the ball
        for(int i = 0; i < 4; i++) {
            if(oppdir[i] == balldir && oppdist[i] == 2)
                return PLAYER;
        }
        return balldir;
    }


    public int InterceptBall() {
        if(bally < ply[ID-1]) {
            if(look[NORTHEAST] == EMPTY)
                return NORTHEAST;
            if(ballx <= plx[ID-1] && look[NORTH] == EMPTY)
                return NORTH;
            if(look[EAST] == EMPTY)
                return EAST;
            if(look[NORTH] == EMPTY)
                return NORTH;
        }
        else if(bally > ply[ID-1]) {
            if(look[SOUTHEAST] == EMPTY)
                return SOUTHEAST;
            if(ballx <= plx[ID-1]&& look[SOUTH] == EMPTY)
                return SOUTH;
            if(look[EAST] == EMPTY)
                return EAST;
            if(look[NORTH] == EMPTY)
                return SOUTH;
        }
        else if(ballx >= plx[ID-1]) {
            if(look[EAST] == EMPTY)
                return EAST;
            if(look[SOUTHEAST] == EMPTY)
                return SOUTHEAST;
        }
        else {
            if(look[WEST] == EMPTY)
                return WEST;
            if(look[SOUTHWEST] == EMPTY)
                return SOUTHWEST;
        }
        if(look[balldir] == EMPTY)
            return balldir;
        if(balldir < 3 || balldir > 5) {
            if(look[(balldir+1)%8] == EMPTY)
                return (balldir+1)%8;
        }
        else {
            if(look[balldir-1] == EMPTY)
                return balldir-1;
        }
        if(look[balldir+1] == EMPTY)
            return balldir+1;
        return balldir;
    }

    public boolean CanIScore() {
        boolean iCanScore = false;
        if(balldist == 1) {
            if(look[WEST] == BALL) {
                if(plx[ID-1] < 10) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if(oppx[i] == plx[ID-1] && oppy[i] < ply[ID-1])
                            return false;
                    }
                }
            }
            else if(look[NORTHWEST] == BALL) {
                if(plx[ID-1] < 7) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if((plx[ID-1] - oppx[i] == ply[ID-1] - oppy[i]) && oppy[i] < ply[ID-1])
                            return false;
                    }
                }        
            }
            else if(look[SOUTHWEST] == BALL) {
                if(plx[ID-1] < 7) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if((plx[ID-1] - oppx[i] == oppy[i] - ply[ID-1]) && oppy[i] < ply[ID-1])
                            return false;
                    }
                }            
            }
        }
        else
            return false;
        return true;
    }

    public void GetData() {
        plx[ID-1] = GetLocation().x;
        ply[ID-1] = GetLocation().y;
        for(int i = 0; i < 8; i++)
            look[i] = Look(i);
        updateBallLocation();
        updateOppLocation();
    }

    public void updateBallLocation() {
        balldist = GetBallDistance();
        balldir = GetBallDirection();

        int weight = 1000;
        double ballx_temp = 0;
        double bally_temp = 0;

        if(look[0] == 9) {
            ballx_temp = plx[ID-1];
            bally_temp = ply[ID-1] - 1;
        }
        else if(look[1] == 9) {
            ballx_temp = plx[ID-1] + 1;
            bally_temp = ply[ID-1] - 1;
        }
        else if(look[2] == 9) {
            ballx_temp = plx[ID-1] + 1;
            bally_temp = ply[ID-1];
        }
        else if(look[3] == 9) {
            ballx_temp = plx[ID-1] + 1;
            bally_temp = ply[ID-1] + 1;
        }
        else if(look[4] == 9) {           
            ballx_temp = plx[ID-1];
            bally_temp = ply[ID-1] + 1;
        }
        else if(look[5] == 9) {            
            ballx_temp = plx[ID-1] - 1;
            bally_temp = ply[ID-1] + 1;
        }
        else if(look[6] == 9) {            
            ballx_temp = plx[ID-1] - 1;
            bally_temp = ply[ID-1];
        }
        else if(look[7] == 9) {            
            ballx_temp = plx[ID-1] - 1;
            bally_temp = ply[ID-1] - 1;
        }
        else {
            double balldist_sqrt2 = (balldist*Math.sqrt(2));

            //just made up this exponential, we should try to optimize it
            weight = (int)(1200*Math.exp(-0.1*balldist));

            if(balldir == 0) {
                ballx_temp = plx[ID-1];
                bally_temp = ply[ID-1] - balldist;
            }
            if(balldir == 1) {
                ballx_temp = plx[ID-1] + balldist_sqrt2;
                bally_temp = ply[ID-1] - balldist_sqrt2;
            }
            if(balldir == 2) {
                ballx_temp = plx[ID-1] + balldist;
                bally_temp = ply[ID-1];
            }
            if(balldir == 3) {
                ballx_temp = plx[ID-1] + balldist_sqrt2;
                bally_temp = ply[ID-1] + balldist_sqrt2;
            }
            if(balldir == 4) {
                ballx_temp = plx[ID-1];
                bally_temp = ply[ID-1] + balldist;
            }
            if(balldir == 5) {
                ballx_temp = plx[ID-1] - balldist_sqrt2;
                bally_temp = ply[ID-1] + balldist_sqrt2;
            }
            if(balldir == 6) {
                ballx_temp = plx[ID-1] - balldist;
                bally_temp = ply[ID-1];
            }
            if(balldir == 7) {
                ballx_temp = plx[ID-1] - balldist_sqrt2;
                bally_temp = ply[ID-1] - balldist_sqrt2;
            }
        }

        ballx = (int)(ballx*(1000-weight) + ballx_temp*(weight));
        bally = (int)(bally*(1000-weight) + bally_temp*(weight));
    }

    public void updateOppLocation() {
        for(int i = 0; i < 4; i++) {
            oppdist[i] = GetOpponentDistance(i+1);
            oppdir[i] = GetOpponentDirection(i+1);
            int weight = 1000;
            double oppx_temp = 0;
            double oppy_temp = 0;

            if(look[0] == 9) {
                oppx_temp = plx[ID-1];
                oppy_temp = ply[ID-1] - 1;
            }
            else if(look[1] == 9) {
                oppx_temp = plx[ID-1] + 1;
                oppy_temp = ply[ID-1] - 1;
            }
            else if(look[2] == 9) {
                oppx_temp = plx[ID-1] + 1;
                oppy_temp = ply[ID-1];
            }
            else if(look[3] == 9) {
                oppx_temp = plx[ID-1] + 1;
                oppy_temp = ply[ID-1] + 1;
            }
            else if(look[4] == 9) {           
                oppx_temp = plx[ID-1];
                oppy_temp = ply[ID-1] + 1;
            }
            else if(look[5] == 9) {            
                oppx_temp = plx[ID-1] - 1;
                oppy_temp = ply[ID-1] + 1;
            }
            else if(look[6] == 9) {            
                oppx_temp = plx[ID-1] - 1;
                oppy_temp = ply[ID-1];
            }
            else if(look[7] == 9) {            
                oppx_temp = plx[ID-1] - 1;
                oppy_temp = ply[ID-1] - 1;
            }
            else {
                int oppdist_sqrt2 = (int)(oppdist[i]*Math.sqrt(2)+0.5);

                //just made up this exponential, we should try to optimize it
                weight = (int)(1200*Math.exp(-0.1*oppdist[i]));

                if(oppdir[i]== 0) {
                    oppx_temp = plx[ID-1];
                    oppy_temp = ply[ID-1] - oppdist[i];
                }
                if(oppdir[i]== 1) {
                    oppx_temp = plx[ID-1] + oppdist_sqrt2;
                    oppy_temp = ply[ID-1] - oppdist_sqrt2;
                }
                if(oppdir[i]== 2) {
                    oppx_temp = plx[ID-1] + oppdist[i];
                    oppy_temp = ply[ID-1];
                }
                if(oppdir[i]== 3) {
                    oppx_temp = plx[ID-1] + oppdist_sqrt2;
                    oppy_temp = ply[ID-1] + oppdist_sqrt2;
                }
                if(oppdir[i]== 4) {
                    oppx_temp = plx[ID-1];
                    oppy_temp = ply[ID-1] + oppdist[i];
                }
                if(oppdir[i]== 5) {
                    oppx_temp = plx[ID-1] - oppdist_sqrt2;
                    oppy_temp = ply[ID-1] + oppdist_sqrt2;
                }
                if(oppdir[i]== 6) {
                    oppx_temp = plx[ID-1] - oppdist[i];
                    oppy_temp = ply[ID-1];
                }
                if(oppdir[i]== 7) {
                    oppx_temp = plx[ID-1] - oppdist_sqrt2;
                    oppy_temp = ply[ID-1] - oppdist_sqrt2;
                }
            }

            oppx[i] = (int)(oppx[i]*(1000-weight)/1000 + oppx_temp*(weight)/1000);
            oppy[i] = (int)(oppy[i]*(1000-weight)/1000 + oppy_temp*(weight)/1000);
            //System.out.println("ballx: " + ballx);
            //System.out.println("bally: " + bally);            
        }
    }
}
