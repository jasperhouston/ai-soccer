import java.awt.*;

public class CareBears extends Player {
    // this chunk used for OrangeCrush Strat
    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;
    static final int WINGBACK = 4;
    static final int BALLDISTANCETOLEAD = 3;
    static final int SWEEPERDISTBACK = 8;
    static final int OPP_DIST_TO_PASS = 2;
    static final int Lead = 1;
    static final int NorthWing = 2;
    static final int SouthWing = 3;
    static final int Sweeper = 4;

    // this chunk used for Silverback Strat
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

    // used for OrangeCrush
    static int cycle;
    static int haveBall;
    static int leader;
    static int roles[];
    static int ball[];
    static int balld[];
    static int synchro[];

    // used for Silverback and OC
    static int[] plx = new int[4];
    static int[] ply = new int[4];

    // used for Silverback
    static int[] look = new int[8];
    static int balldir;
    static int balldist;
    static int ballx;
    static int bally;
    static int[] oppdir= new int[4];
    static int[] oppdist = new int[4];
    static int[] oppx = new int[4];
    static int[] oppy = new int[4];

    // used for strategy management
    static public final int SILVERBACK_NUM = 0;
    static public final int ORANGECRUSH_NUM = 1;
    static public final int POINTS_LOST_TO_SWITCH = 1;
    static int strategyNum = 1;
    static int pointLostCount = 0;


    public void InitializeGame () {
        cycle = -1;
        haveBall = 0;
        plx = new int[4];
        ply = new int[4];
        roles = new int[4];
        ball = new int[4];
        balld = new int[4];
        synchro = new int[4];
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
        roles[1] = NorthWing;
        roles[2] = SouthWing;
        roles[3] = Sweeper;
    }

    // can't just call MakeMove for this one because Player1 has to generate orders
    // in the OrangeCrush strategy
    public int Player1() {
        if (strategyNum == 0) { // Silverbacks strategy
            return Silverbacks();
        } else { // OrangeCrush strategy, only 2 strats as of now
            int action = WEST;
            /* Mark where I am */
            plx[0] = GetLocation().x;
            ply[0] = GetLocation().y;
            ball[0] = HaveBall(0);
            balld[0] = GetBallDistance();
            /* Generate orders */
            Behave();
            for (int i = 0; i < 4; i++)
            synchro[i] = 1;
            switch (roles[0]) {
                case Lead: action =  Lead();
                break;
                case NorthWing: action =  NorthWing();
                break;
                case SouthWing: action =  SouthWing();
                break;
                case Sweeper: action =  Sweeper();
                break;
            }
            return action;
        }
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

    public int MakeMove(int p) {
        if (strategyNum == 0) { // Silverbacks strategy
            return Silverbacks();
        } else { // OrangeCrush strategy, only 2 strats as of now
            return OrangeCrush(p);
        }
    }

    public int OrangeCrush(int p) {
        int action = WEST;
        /* Mark where I am */
        plx[p] = GetLocation().x;
        ply[p] = GetLocation().y;
        ball[p] = HaveBall(p);
        balld[p] = GetBallDistance();
        synchro[p] = 0;
        switch (roles[p]) {
            case Lead: action =  Lead();
            break;
            case NorthWing: action =  NorthWing();
            break;
            case SouthWing: action =  SouthWing();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public void WonPoint () {
        System.out.println("Won that point, wahoo!");
    }

    public void LostPoint () {
        System.out.println("Lost that point, oops!");
        pointLostCount += 1;
        if (pointLostCount >= POINTS_LOST_TO_SWITCH * 4) {
            pointLostCount = 0;
            switch (strategyNum) {
                case SILVERBACK_NUM: {
                    System.out.println("Switching to OrangeCrush strat");
                    strategyNum = ORANGECRUSH_NUM;
                    break;
                }
                case ORANGECRUSH_NUM: {
                    System.out.println("Switching to Silverback strat");
                    strategyNum = SILVERBACK_NUM;
                    break;
                }
            }
        }
    }

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
            if (balld[i] < balld[newl])
            newl = i;
            if (ball[i] == 1) {
                Regroup(i);
                i = 5;
            }
        }
        if (i == 4) /* No one was on the ball, pick the closest guy */
        Regroup(newl);
        // if (haveBall <= 0) {
        //     leader = 0;
        // }
    }

    public int Silverbacks() {
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
        if(balldist == 1) {
            return SurroundBall();
        }
        if(balldist > 2.2 && balldist < 2.3) {
            if((balldir == SOUTHEAST || balldir == NORTHEAST) && look[EAST] == EMPTY)
            return EAST;
            if(balldir == SOUTHWEST && look[SOUTH] == EMPTY)
            return SOUTH;
            if(balldir == NORTHWEST && look[NORTH] == EMPTY)
            return NORTH;
        }
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
        boolean opp_in_way = false;
        int offset_from_east = balldir - EAST;
        if(look[WEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if(oppx[i] == plx[ID-1] && oppy[i] < ply[ID-1])
                opp_in_way = true;
            }
            if(!opp_in_way)
            return KICK;
        }
        else if(look[NORTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if((plx[ID-1] - oppx[i] == ply[ID-1] - oppy[i]) && oppy[i] < ply[ID-1])
                opp_in_way = true;
            }
            if(!opp_in_way)
            return KICK;
        }
        else if(look[SOUTHWEST] == BALL) {
            for(int i = 0; i < 4; i++) {
                if((plx[ID-1] - oppx[i] == oppy[i] - ply[ID-1]) && oppy[i] < ply[ID-1])
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
            else if(look[((EAST+3*offset_from_east)+ 8) % 8] == EMPTY)
            return ((EAST+3*offset_from_east/2)+ 8) % 8;
        }
        //what to do here??
        else if(balldir == NORTH || balldir == SOUTH) {
            System.out.println("North or south of ball");
            //if all opponents are far away go northeast or east
            int east_diagonal = EAST+offset_from_east/2;
            int west_diagonal = ((((EAST+3*offset_from_east/2) % 8) + 8) % 8);
            boolean teammate_in_way_of_kick = false;
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
                    if(plx[ID-1] + 1 == plx[i] && ply[ID-1] - offset_from_east == ply[i])
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

    public int minOppdist() {
        int mindist = oppdist[0];
        for(int i = 1; i < 4; i++) {
            if(oppdist[i] < mindist)
            mindist = oppdist[i];
        }
        return mindist;
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
            if(ballx <= plx[ID-1])
            return SOUTH;
            return EAST;
        }
        else if(ballx > plx[ID-1]) {
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

    public boolean CanIScore() {
        boolean iCanScore = false;
        if(GetBallDistance() == 1) {
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
        int ballx_temp = 0;
        int bally_temp = 0;

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
            int balldist_sqrt2 = (int)(balldist*Math.sqrt(2));

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

        ballx = ballx*(1000-weight) + ballx_temp*(weight);
        bally = bally*(1000-weight) + bally_temp*(weight);
    }

    public void updateOppLocation() {
        for(int i = 0; i < 4; i++) {
            oppdist[i] = GetOpponentDistance(i+1);
            oppdir[i] = GetOpponentDirection(i+1);
            int weight = 1000;
            int oppx_temp = 0;
            int oppy_temp = 0;

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
                int oppdist_sqrt2 = (int)(oppdist[i]*Math.sqrt(2));

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

            ballx = ballx*(1000-weight) + oppx_temp*(weight);
            bally = bally*(1000-weight) + oppy_temp*(weight);
        }
    }



    public void Regroup (int newLead) {
        int i, score, good;
        good = 0;   /* Make Java happy */
        for (i=0; i<4; i++) {
            roles[i] = 0;
        }

        leader = newLead;
        roles[leader] = Lead;

        /* southernmost unassigned player is south wing */
        score = -1;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (ply[i] > score) {
                    score = ply[i];
                    good = i;
                }
            }
        }
        roles[good] = SouthWing;

        /* northernmost unassigned player is north wing */
        score = 10000;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (ply[i] < score) {
                    score = ply[i];
                    good = i;
                }
            }
        }
        roles[good] = NorthWing;

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
    }

    public int Lead () {
        int i, kickSouth;
        int x = GetLocation().x;
        int y = GetLocation().y;

        /* If lead is east of the ball and an opponent is around, get the ball
        out of here */
        if ((GetOpponentDistance(1) < OPP_DIST_TO_PASS) &&
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

    public int NorthWing () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader] - WINGSPAN)) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader] - WINGSPAN)) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = SOUTH;
        }
        if (Look(WEST) == EMPTY && (x > plx[leader] + WINGBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + WINGBACK)) {
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

    public int SouthWing () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader] + WINGSPAN)) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader] + WINGSPAN)) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = NORTH;
        }
        if (Look(WEST) == EMPTY && (x > plx[leader] + WINGBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + WINGBACK)) {
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

    public int Sweeper () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
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


        if (Look(WEST) == EMPTY && (x > plx[leader] + SWEEPERDISTBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + SWEEPERDISTBACK)) {
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

}
