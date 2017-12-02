
/**
 * Write a description of class Silverbacks here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Silverbacks extends Player
{
    static public final int Sweeper = 1;
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
        return MakeMove(0);
    }

    public int Player2() {
        return MakeMove(1);
    }

    public int Player3() {
        return MakeMove(2);
    }

    public int Player4() {
        return MakeMove(3);
    }

    public int MakeMove(int player) {
        GetData(player);
        if(balldist == 1) {
            return SurroundBall(player);
        }
        return GetBehindBall(player);
    }
    

    public int GetBehindBall(int player) {
        if(plx[player] < ballx && Math.abs(ply[player] - bally) == 1) {
            if(look[EAST] == EMPTY)
                return EAST;
        }
        if(look[balldir] == EMPTY) {
            return balldir;
        }
        if(balldir%4 == 0) {
            return InterceptBall(player);
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
    public int SurroundBall(int player) {
        boolean opp_in_way = false;
        int offset_from_east = balldir - EAST;
        if(look[WEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if(oppx[i] == plx[player] && oppy[i] < ply[player])
                    opp_in_way = true;
            }
            if(!opp_in_way)
                return KICK;
        }
        else if(look[NORTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if((plx[player] - oppx[i] == ply[player] - oppy[i]) && oppy[i] < ply[player])
                    opp_in_way = true;
            }
            if(!opp_in_way)
                return KICK;
        }
        else if(look[SOUTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if((plx[player] - oppx[i] == oppy[i] - ply[player]) && oppy[i] < ply[player])
                    opp_in_way = true;
            }
            if(!opp_in_way)
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
            else if(look[(EAST+3*offset_from_east)%8] == EMPTY)
                return (EAST+3*offset_from_east)%8;
        }
        //what to do here??
        else if(balldir == NORTH || balldir == SOUTH) {
            //if all opponents are far away go northeast or east
            int east_diagonal = EAST+offset_from_east/2;
            int west_diagonal = ((((EAST+3*offset_from_east/2) % 8) + 8) % 8);
            //if there is an opponent to the northwest and no teammate to the northeast
            if(look[east_diagonal] != TEAMMATE && look[west_diagonal] == OPPONENT)
                //should make sure no opponent is there
                return KICK;
            if(look[east_diagonal] == EMPTY)
                return east_diagonal;
            else if(look[EAST] == EMPTY)
                return EAST;    
                
        }
        //does nothing (PLAYER) if there's an opponent on the other side of the ball
        for(int i = 0; i < 4; i++) {
            if(oppdir[i] == balldir && oppdist[i] == 2)
                return PLAYER;
        }
        return balldir;
    }
    
    public int minOppdist() {
        int mindist = oppdist[0];
        for(int i = 1; i < 4; i++) {
            if(oppdist[i] < mindist)
                mindist = oppdist[i];
        }
        return mindist;
    }

    public int InterceptBall(int player) {
        if(bally < ply[player]) {
            if(look[NORTHEAST] == EMPTY)
                return NORTHEAST;
            if(ballx <= plx[player] && look[NORTH] == EMPTY)
                return NORTH;
            if(look[EAST] == EMPTY)
                return EAST;
            if(look[NORTH] == EMPTY)
                return NORTH;
        }
        else if(bally > ply[player]) {
            if(look[SOUTHEAST] == EMPTY)
                return SOUTHEAST;
            if(ballx <= plx[player])
                return SOUTH;
            return EAST;
        }
        else if(ballx > plx[player]) {
            if(look[EAST] == EMPTY) {
                return EAST;
            }
            return SOUTHEAST;
        }
        else {
            if(look[WEST] == EMPTY) {
                return WEST;
            }
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

    public boolean CanIScore(int player) {
        boolean iCanScore = false;
        if(GetBallDistance() == 1) {
            if(look[WEST] == BALL) {
                if(plx[player] < 10) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if(oppx[i] == plx[player] && oppy[i] < ply[player])
                            return false;
                    }
                }
            }
            else if(look[NORTHWEST] == BALL) {
                if(plx[player] < 7) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if((plx[player] - oppx[i] == ply[player] - oppy[i]) && oppy[i] < ply[player])
                            return false;
                    }
                }        
            }
            else if(look[SOUTHWEST] == BALL) {
                if(plx[player] < 7) {
                    iCanScore = true;
                    for(int i = 0; i < 4; i++) {
                        if((plx[player] - oppx[i] == oppy[i] - ply[player]) && oppy[i] < ply[player])
                            return false;
                    }
                }            
            }
        }
        else
            return false;
        return true;
    }

    public void GetData(int player) {
        plx[player] = GetLocation().x;
        ply[player] = GetLocation().y;
        for(int i = 0; i < 8; i++)
            look[i] = Look(i);
        updateBallLocation(player);
        updateOppLocation(player);
    }

    public void updateBallLocation(int player) {
        balldist = GetBallDistance();
        balldir = GetBallDirection();

        int weight = 1000;
        int ballx_temp = 0;
        int bally_temp = 0;

        if(look[0] == 9) {
            ballx_temp = plx[player];
            bally_temp = ply[player] - 1;
        }
        else if(look[1] == 9) {
            ballx_temp = plx[player] + 1;
            bally_temp = ply[player] - 1;
        }
        else if(look[2] == 9) {
            ballx_temp = plx[player] + 1;
            bally_temp = ply[player];
        }
        else if(look[3] == 9) {
            ballx_temp = plx[player] + 1;
            bally_temp = ply[player] + 1;
        }
        else if(look[4] == 9) {           
            ballx_temp = plx[player];
            bally_temp = ply[player] + 1;
        }
        else if(look[5] == 9) {            
            ballx_temp = plx[player] - 1;
            bally_temp = ply[player] + 1;
        }
        else if(look[6] == 9) {            
            ballx_temp = plx[player] - 1;
            bally_temp = ply[player];
        }
        else if(look[7] == 9) {            
            ballx_temp = plx[player] - 1;
            bally_temp = ply[player] - 1;
        }
        else {
            int balldist_sqrt2 = (int)(balldist*Math.sqrt(2));

            //just made up this exponential, we should try to optimize it
            weight = (int)(1200*Math.exp(-0.1*balldist));

            if(balldir == 0) {
                ballx_temp = plx[player];
                bally_temp = ply[player] - balldist;
            }
            if(balldir == 1) {
                ballx_temp = plx[player] + balldist_sqrt2;
                bally_temp = ply[player] - balldist_sqrt2;
            }
            if(balldir == 2) {
                ballx_temp = plx[player] + balldist;
                bally_temp = ply[player];
            }
            if(balldir == 3) {
                ballx_temp = plx[player] + balldist_sqrt2;
                bally_temp = ply[player] + balldist_sqrt2;
            }
            if(balldir == 4) {
                ballx_temp = plx[player];
                bally_temp = ply[player] + balldist;
            }
            if(balldir == 5) {
                ballx_temp = plx[player] - balldist_sqrt2;
                bally_temp = ply[player] + balldist_sqrt2;
            }
            if(balldir == 6) {
                ballx_temp = plx[player] - balldist;
                bally_temp = ply[player];
            }
            if(balldir == 7) {
                ballx_temp = plx[player] - balldist_sqrt2;
                bally_temp = ply[player] - balldist_sqrt2;
            }
        }

        ballx = ballx*(1000-weight) + ballx_temp*(weight);
        bally = bally*(1000-weight) + bally_temp*(weight);
    }

    public void updateOppLocation(int player) {
        for(int i = 0; i < 4; i++) {
            oppdist[i] = GetOpponentDistance(i+1);
            oppdir[i] = GetOpponentDirection(i+1);
            int weight = 1000;
            int oppx_temp = 0;
            int oppy_temp = 0;

            if(look[0] == 9) {
                oppx_temp = plx[player];
                oppy_temp = ply[player] - 1;
            }
            else if(look[1] == 9) {
                oppx_temp = plx[player] + 1;
                oppy_temp = ply[player] - 1;
            }
            else if(look[2] == 9) {
                oppx_temp = plx[player] + 1;
                oppy_temp = ply[player];
            }
            else if(look[3] == 9) {
                oppx_temp = plx[player] + 1;
                oppy_temp = ply[player] + 1;
            }
            else if(look[4] == 9) {           
                oppx_temp = plx[player];
                oppy_temp = ply[player] + 1;
            }
            else if(look[5] == 9) {            
                oppx_temp = plx[player] - 1;
                oppy_temp = ply[player] + 1;
            }
            else if(look[6] == 9) {            
                oppx_temp = plx[player] - 1;
                oppy_temp = ply[player];
            }
            else if(look[7] == 9) {            
                oppx_temp = plx[player] - 1;
                oppy_temp = ply[player] - 1;
            }
            else {
                int oppdist_sqrt2 = (int)(oppdist[i]*Math.sqrt(2));

                //just made up this exponential, we should try to optimize it
                weight = (int)(1200*Math.exp(-0.1*oppdist[i]));

                if(oppdir[i]== 0) {
                    oppx_temp = plx[player];
                    oppy_temp = ply[player] - oppdist[i];
                }
                if(oppdir[i]== 1) {
                    oppx_temp = plx[player] + oppdist_sqrt2;
                    oppy_temp = ply[player] - oppdist_sqrt2;
                }
                if(oppdir[i]== 2) {
                    oppx_temp = plx[player] + oppdist[i];
                    oppy_temp = ply[player];
                }
                if(oppdir[i]== 3) {
                    oppx_temp = plx[player] + oppdist_sqrt2;
                    oppy_temp = ply[player] + oppdist_sqrt2;
                }
                if(oppdir[i]== 4) {
                    oppx_temp = plx[player];
                    oppy_temp = ply[player] + oppdist[i];
                }
                if(oppdir[i]== 5) {
                    oppx_temp = plx[player] - oppdist_sqrt2;
                    oppy_temp = ply[player] + oppdist_sqrt2;
                }
                if(oppdir[i]== 6) {
                    oppx_temp = plx[player] - oppdist[i];
                    oppy_temp = ply[player];
                }
                if(oppdir[i]== 7) {
                    oppx_temp = plx[player] - oppdist_sqrt2;
                    oppy_temp = ply[player] - oppdist_sqrt2;
                }
            }

            ballx = ballx*(1000-weight) + oppx_temp*(weight);
            bally = bally*(1000-weight) + oppy_temp*(weight);
        }
    }
}
