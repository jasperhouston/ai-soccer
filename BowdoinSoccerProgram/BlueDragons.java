import java.awt.*;

public class BlueDragons extends Player
{
    static final int CONTROL_TIME = 13;
    static final int Lead = 1;
    static final int Second = 2;
    static final int Third = 3;
    static final int Sweeper = 4;
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

    static int cycle;
    static int haveBall;
    static int leader;
    
    static int[] plx = new int[4];
    static int[] ply = new int[4];

    
    static int[] roles = new int[4];
    static int[] ballhave = new int[4];
    static int[] balldist = new int[4];
    static int[] balldir = new int[4];
    static int[] synchro = new int[4];
    
    static int ballx;
    static int bally;
    
    static int[] opx = new int[4];
    static int[] opy = new int[4];


    public void InitializeGame () {
        cycle = -1;
        haveBall = 0;

    }

    public void InitializePoint () {
        int i;
        leader = 0;
        haveBall = 0;
        for (i=0; i<=3; i++) {
            plx[i] = 0;
            ply[i] = 0;
            synchro[i] = 0;
        }
        roles[0] = Lead;
        roles[1] = Second;
        roles[2] = Third;
        roles[3] = Sweeper;
    }

    public int InitializePlayer(int player) {
        int action = WEST;
        /* Mark where I am */
        plx[player] = GetLocation().x;
        ply[player] = GetLocation().y;
        ballhave[player] = HaveBall(player);
        balldist[player] = GetBallDistance();
        balldir[player] = GetBallDirection();
        
        /* Generate orders */
        if(player == 0) {    
            Behave();
            for(int i = 0; i < 4; i++)
                synchro[i] = 1;
        }
        else {
            synchro[player] = 0;
        }
        
        switch (roles[player]) {
            case Lead: action =  Lead();
            break;
            case Second: action =  Second();
            break;
            case Third: action =  Third();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;        
    }
    
    public int Player1() {
        return InitializePlayer(0);
    }

    public int Player2() {
        return InitializePlayer(1);
    }

    public int Player3() {
        return InitializePlayer(2);
    }

    public int Player4() {
        return InitializePlayer(3);
    }
    
    public void WonPoint () {};

    public void LostPoint () {};

    public void GameOver () {};

    public int HaveBall(int id) {
        int BallDir = GetBallDirection();
        if ((GetBallDistance() == 1) &&
        ((BallDir == EAST) || (BallDir == NORTHEAST)
            || (BallDir == SOUTHEAST) || (BallDir == NORTH)
            || (BallDir == SOUTH))) {
            haveBall = CONTROL_TIME;
            return 1;
        }
        return 0;
    }

    public void Behave () {

        int newl = 0;
        int i;
        cycle = (cycle + 1) % 4;
        if (cycle == 0) {
            haveBall--;
        }

        /* Whoever has the ball gets to be the leader */
        for ( i = 0; i < 4; i++) {
            if (balldist[i] < balldist[newl])
                newl = i;
            if (ballhave[i] == 1) {
                Regroup(i);
                break;
            }
        }
        if (i == 4) /* No one was on the ball, pick the closest guy */
            Regroup(newl);
    }

    public void Regroup (int newLead) {
        int i, score, good;
        good = 0;   /* Make Java happy */
        for (i=0; i<4; i++) {
            roles[i] = 0;
        }

        leader = newLead;
        roles[leader] = Lead;

        /* easternmost unassigned player is Sweeper */
        score = -1;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (plx[i] > score) {
                    score = plx[i];
                    good = i;
                }
            }
        }
        roles[good] = Sweeper;

        /* unassigned players are assigned Second and Third */
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                roles[i] = Second;
                break;
            }
        }
        
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                roles[i] = Third;
                break;
            }
        }

    }

    public int Lead () {
        int i, kickSouth;
        int x = GetLocation().x;
        int y = GetLocation().y;

        /* If lead is east of the ball and an opponent is around, get the ball
        out of here */
        if ((GetOpponentDistance(1) < 2) &&
        ((Look(SOUTHWEST) == BALL) || (Look(WEST) == BALL) || (Look(NORTHWEST) == BALL)))
            return KICK;

        /* Try to kick away from the bulk of the players */
        kickSouth = 0;
        for (i=0; i<4; i++) {
            if ((y < FieldY() - 4) && (ply[i] < y)) {
                kickSouth++;
            }
            if ((y > 5) && (ply[i] > y)) {
                kickSouth--;
            }
        }

        /* Kick south if we "should" kick south or if we are near defending goal */
        if (Look(SOUTHWEST) == BALL) {
            if ((x > 3 * FieldX() / 4) || (kickSouth > 0)) {
                return KICK;
            }
            /* else try to move to kick straight west */
            return SOUTH;
        }

        /* Similarly for north */
        if (Look(NORTH) == BALL) {
            if ((x > 3 * FieldX() / 4) || (kickSouth < 0)) {
                return KICK;
            }
            return NORTH;
        }

        if (Look(WEST) == BALL) {
            /* If there is a strong preference to kick the ball north or south,
            try to move to do so */
            if (kickSouth >= 3) {
                return NORTH;
            }
            if (kickSouth <= -3) {
                return SOUTH;
            }
            /* Otherwise kick toward the goal */
            return KICK;
        }

        if (Look(NORTH) == BALL) {
            /* If an opponent can kick toward my goal, try to kick the ball
            away */
            if (Look(WEST) == OPPONENT && Look(NORTHWEST) == OPPONENT) {
                return KICK;
            }

            /* If ball is near my defending goal, really try to kick it */
            if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(NORTHWEST) == OPPONENT)) {
                return KICK;
            }

            /* If there are no opponents and I want to kick south, move to try
            to do so */
            if ((kickSouth >= 0) && Look(NORTHEAST) == EMPTY) {
                return(NORTHEAST);
            }

            /* else if I want to kick north, just move E so I can kick NORTHWEST */
            return(EAST);
        }

        if (Look(NORTHEAST) == BALL) {
            /* If an opponent can get to the ball, get between it and the ball */
            if (Look(NORTH) == EMPTY && (Look(NORTHWEST) == OPPONENT)) {
                return(NORTH);
            }
            /* else get between the ball and the defending goal */
            return(EAST);
        }

        if (Look(EAST) == BALL) {
            /* Get into position to kick the ball, if possible */
            if ((kickSouth > 0) && Look(NORTHEAST) == EMPTY) {
                return(NORTHEAST);
            }
            if ((kickSouth < 0) && Look(SOUTHEAST) == EMPTY) {
                return(SOUTHEAST);
            }

            /* else try to block any opponents */
            if (Look(WEST) == OPPONENT) {
                return(WEST);
            }

            /* else just move out of the way */
            if (Look(NORTH) == EMPTY) {
                return(NORTH);
            }
            return(SOUTH);
        }

        if (Look(SOUTHEAST) == BALL) {
            /* If an opponent can get to the ball, get between it and the ball */
            if (Look(SOUTH) == EMPTY && (Look(SOUTHWEST) == OPPONENT)) {
                return(SOUTH);
            }
            /* else get between the ball and the defending goal */
            return(EAST);
        }

        if (Look(SOUTH) == BALL) {
            /* If an opponent can kick toward my goal, try to kick the ball
            away */
            if (Look(WEST) == OPPONENT && (Look(SOUTHWEST) == OPPONENT)) {
                return(KICK);
            }

            /* If ball is near my defending goal, really try to kick it */
            if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(SOUTHWEST) == OPPONENT)) {
                return(KICK);
            }

            /* If there are no opponents and I want to kick south, move to try
            to do so */
            if ((kickSouth >= 0) && Look(SOUTHEAST) == EMPTY) {
                return(SOUTHEAST);
            }

            /* else if I want to kick north, just move E so I can kick NORTHWEST */
            return(EAST);
        }

        /* else just move toward the ball */
        return(GetBallDirection());
    }

    public int Second () {
        return Lead();
    }

    public int Third () {
        return Lead();
    }

    public int Sweeper () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < 2) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader])) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader])) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = SOUTH;
        }
        if (Look(WEST) == EMPTY && (x > plx[leader] + 8)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + 8)) {
            ew = EAST;
        }

        if ((ew == EAST) && (ns == NORTH)) {
            return(NORTHEAST);
        }
        if ((ew == EAST) && (ns == SOUTH)) {
            return(SOUTHEAST);
        }
        if ((ew == WEST) && (ns == NORTH)) {
            return(NORTHWEST);
        }
        if ((ew == WEST) && (ns == SOUTH)) {
            return(SOUTHWEST);
        }
        if (ew == EAST) {
            return(EAST);
        }
        if (ew == WEST) {
            return(WEST);
        }
        if (ns == NORTH) {
            return(NORTH);
        }
        if (ns == SOUTH) {
            return(SOUTH);
        }

        return(GetBallDirection());
    }

    public void updateBallLocation() {
         int weight;
         int total_weight;
         int ballx_temp;
         int bally_temp;
         ballx = 0;
         bally = 0;
         for(int player = 0; player < 4; player ++) {
             if(look[0] == 9) {
                weight= 1000;
                ballx_temp = plx[player];
                bally_temp = ply[player] - 1;
             }
             else if(look[1] == 9) {
                weight= 1000;
                ballx_temp = plx[player] + 1;
                bally_temp = ply[player] - 1;
             }
             else if(look[2] == 9) {
                weight= 1000;
                ballx_temp = plx[player] + 1;
                bally_temp = ply[player];
             }
             else if(look[3] == 9) {
                weight= 1000;
                ballx_temp = plx[player] + 1;
                bally_temp = ply[player] + 1;
             }
             else if(look[4] == 9) {
                weight= 1000;
                ballx_temp = plx[player];
                bally_temp = ply[player] + 1;
             }
             else if(look[5] == 9) {
                weight= 1000;
                ballx_temp = plx[player] - 1;
                bally_temp = ply[player] + 1;
             }
             else if(look[6] == 9) {
                weight= 1000;
                ballx_temp = plx[player] - 1;
                bally_temp = ply[player];
             }
             else if(look[7] == 9) {
                weight= 10;
                ballx_temp = plx[player] - 1;
                bally_temp = ply[player] - 1;
             }
             else {
                 int distToB = balldist[player];
                 int distToB_sqrt2 = (int)(distToB*Math.sqrt(2));
                 if(distToB < 4)
                    weight= 10;
                 else if(distToB < 8)
                    weight= 8;
                 else if(distToB < 12)
                    weight= 6;
                 else if(distToB < 16)
                    weight= 4;
                 else if(distToB < 20)
                    weight= 2;
                 else
                    weight= 1;
                 int dirToB = balldir[player];
                 if(dirToB == 0) {
                    ballx_temp = plx[player];
                    bally_temp = ply[player] - distToB;
                 }
                 if(dirToB == 1) {
                    ballx_temp = plx[player] + distToB_sqrt2;
                    bally_temp = ply[player] - distToB_sqrt2;
                 }
                 if(dirToB == 2) {
                    ballx_temp = plx[player] + distToB;
                    bally_temp = ply[player];
                 }
                 if(dirToB == 3) {
                    ballx_temp = plx[player] + distToB_sqrt2;
                    bally_temp = ply[player] + distToB_sqrt2;
                 }
                 if(dirToB == 4) {
                    ballx_temp = plx[player];
                    bally_temp = ply[player] + distToB;
                 }
                 if(dirToB == 5) {
                    ballx_temp = plx[player] - distToB_sqrt2;
                    bally_temp = ply[player] + distToB_sqrt2;
                 }
                 if(dirToB == 6) {
                    ballx_temp = plx[player] - distToB;
                    bally_temp = ply[player];
                 }
                 if(dirToB == 7) {
                    ballx_temp = plx[player] - distToB_sqrt2;
                    bally_temp = ply[player] - distToB_sqrt2;
                 }
             }
             ballx += weight*ballx_temp;
             bally += weight*bally_temp;
             total_weight += weight;
         }
         ballx = ballx/total_weight;
         bally = bally/total_weight;
    }

    public int getAction()
    {
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
}
