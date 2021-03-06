
/**
 *
 * @author Jasper Houston and Will deBruynKops 
 * @version v3.7
 */
public class CareBears2point0 extends Player
{
    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;
    static final int WINGBACK = 4;
    static final int BALLDISTANCETODIAG = 3;
    static final int SWEEPERDISTBACK = 8;
    static final int OPP_DIST_TO_PASS = 2;
    static final int Lead = 1;
    static final int LeadB = 2;
    static final int LeadC = 3;
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
    static public final int XSQUARES = 80;
    static public final int YSQUARES = 40;

    private static boolean diagonal = false;
    static int diagDirection = 0;
    static int cycle;
    static int haveBall;
    static int leader;
    static int roles[];
    static int ball[];
    static int balld[];
    static int synchro[];

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
        roles[1] = LeadB;
        roles[2] = LeadC;
        roles[3] = Sweeper;
        if(ID == 1)
            System.out.println();
    }

    public int Player1() {
        GetData();
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
            case Lead: action =  PolarLead();
            break;
            case LeadB: action =  PolarLead();
            break;
            case LeadC: action =  PolarLead();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player2() {
        GetData();
        /* Mark where I am */
        int action = WEST;
        plx[1] = GetLocation().x;
        ply[1] = GetLocation().y;
        ball[1] = HaveBall(1);
        balld[1] = GetBallDistance();
        synchro[1] = 0;
        switch (roles[1]) {
            case Lead: action =  PolarLead();
            break;
            case LeadB: action =  PolarLead();
            break;
            case LeadC: action =  PolarLead();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player3() {
        GetData();
        int action = WEST;
        /* Mark where I am */
        plx[2] = GetLocation().x;
        ply[2] = GetLocation().y;
        ball[2] = HaveBall(2);
        balld[2] = GetBallDistance();
        synchro[2] = 0;
        switch (roles[2]) {
            case Lead: action =  PolarLead();
            break;
            case LeadB: action =  PolarLead();
            break;
            case LeadC: action =  PolarLead();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player4() {
        GetData();
        int action = WEST;
        /* Mark where I am */
        plx[3] = GetLocation().x;
        ply[3] = GetLocation().y;

        ball[3] = HaveBall(3);
        balld[3] = GetBallDistance();
        synchro[3] = 0;
        switch (roles[3]) {
            case Lead: action =  PolarLead();
            break;
            case LeadB: action =  PolarLead();
            break;
            case LeadC: action =  PolarLead();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public void WonPoint () {
        diagonal = false;
        double x = Math.random();
        int num = 5;
        System.out.print("Grizzlies P" + ID + ": ");
        if(x < 1.0/num)
            System.out.println("I like you. You remind me of me when I was young and stupid.");
        else if(x < 2.0/num)
            System.out.println("I see you’ve set aside this special time to humiliate yourself in public.");
        else if(x < 3.0/num)
            System.out.println("I’ll try being nicer if you’ll try being smarter.");
        else if(x < 4.0/num)
            System.out.println("You don't like me. That's a shame, I'll pencil in some time to cry about it later.");
        else
            System.out.println("Grief can be a burden, but also an anchor. You get used to the weight, how it holds you in place.");       
    }
    public void LostPoint () {
        diagonal = false;
        double x = Math.random();
        int num = 5;
        System.out.print("Grizzlies P" + ID + ": ");
        if(x < 1.0/num)
            System.out.println("If I throw a stick, will you go away?");
        else if(x < 2.0/num)
            System.out.println("First is the worst, second is the best...");
        else if(x < 3.0/num)
            System.out.println("Anyone can have an off decade.");
        else if(x < 4.0/num)
            System.out.println("Without losers, where would the winners be?");
        else
            System.out.println("It's only after we've lost everything that we're free to do anything.");
        
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

    public void Regroup (int newLead) {
        int i, score, good;
        good = 0;   /* Make Java happy */
        for (i=0; i<4; i++) {
            roles[i] = 0;
        }

        /* easternmost player is Sweeper */
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

        if (roles[newLead] == 0) {
            roles[newLead] = Lead;
            leader = newLead;
        }

        // assign other two players to LeadB and LeadC
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                roles[i] = LeadB;
                break;
            }
        }

        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                roles[i] = LeadC;
                break;
            }
        }

        // ensures a leader is assigned.
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                roles[i] = Lead;
                leader = i;
                break;
            }
        }

    }

    public int PolarLead() {
        if (diagonal == true) {
            return(getOpen());
        }
        //If it's touching the ball, it acts according to SurroundBall() function
        if(balldist == 1) {
            return SurroundBall();
        }
        return GoTowardsBall();
    }
    
    public int GoTowardsBall() {
        if(look[balldir] == EMPTY) {
            return balldir;
        }
        if(balldir == NORTH) {
            if(look[NORTHEAST] == EMPTY)
                return NORTHEAST;
            if(look[EAST] == EMPTY)
                return EAST;
        }
        if(balldir == SOUTH) {
            if(look[SOUTHEAST] == EMPTY)
                return SOUTHEAST;
            if(look[EAST] == EMPTY)
                return EAST;
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
            int east_diagonal = EAST+offset_from_east/2;
            int west_diagonal = ((((EAST+3*offset_from_east/2) % 8) + 8) % 8);
            boolean teammate_in_way_of_kick = false;
            
            if(look[east_diagonal] == EMPTY) {
                return east_diagonal;
            }
            if(look[EAST] == EMPTY) {
                return EAST;
            }
            
            for(int i = 0; i < 4; i++) {
                if(ply[ID-1] - offset_from_east == ply[i])
                    teammate_in_way_of_kick = true;
            }
            //if there is an opponent to the northwest and no teammate to the northeast
            if(look[east_diagonal] != TEAMMATE && look[west_diagonal] == OPPONENT) {
                if(!teammate_in_way_of_kick) {
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
                    return KICK;
                }
            }
        }
        //does nothing (PLAYER) if there's an opponent on the other side of the ball
        for(int i = 0; i < 4; i++) {
            if(oppdir[i] == balldir && oppdist[i] == 2)
                return PLAYER;
        }
        return balldir;
    }

    public int Sweeper () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, initiate diagonal pass */
        if (GetBallDistance() < BALLDISTANCETODIAG) {
            if (y > YSQUARES/2) {
                diagonal = true;
                diagDirection = NORTH;
                return(DiagPass());
            } else {
                diagonal = true;
                diagDirection = SOUTH;
                return(DiagPass());
            }
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

    public int DiagPass() {
        if (minOppdist() < 4) {
            diagonal = false;
            Regroup(ID - 1);
            return (PolarLead());
        }
        if (diagDirection == NORTH) {
            // get to the southeast of the ball and kick
            if (Look(SOUTH) == BALL) {
                return SOUTHEAST;
            }
            if (Look(SOUTHEAST) == BALL) {
                return SOUTH;
            }
            if (Look(EAST) == BALL) {
                return SOUTHEAST;
            }
            if (Look(NORTHEAST) == BALL) {
                return EAST;
            }
            if (Look(NORTH) == BALL) {
                return EAST;
            }
            if (Look(NORTHWEST) == BALL) {
                diagonal = false;
                return(KICK);
            }
            if (Look(WEST) == BALL) {
                return SOUTH;
            }
            if (Look(SOUTHWEST) == BALL) {
                return SOUTH;
            }

        }
        if (diagDirection == SOUTH) {
            // get to the northeast of the ball and kick
            if (Look(SOUTH) == BALL) {
                return EAST;
            }
            if (Look(SOUTHEAST) == BALL) {
                return EAST;
            }
            if (Look(EAST) == BALL) {
                return NORTHEAST;
            }
            if (Look(NORTHEAST) == BALL) {
                return NORTH;
            }
            if (Look(NORTH) == BALL) {
                return NORTHEAST;
            }
            if (Look(NORTHWEST) == BALL) {
                return NORTH;
            }
            if (Look(WEST) == BALL) {
                return NORTH;
            }
            if (Look(SOUTHWEST) == BALL) {
                diagonal = false;
                return(KICK);
            }
        }
        return(GetBallDirection());
    }

    public int getOpen() {
        if (diagDirection == NORTH) {
            return NORTH;
        }
        if (diagDirection == SOUTH) {
            return SOUTH;
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

        ballx = ballx*(1000-weight)/1000 + ballx_temp*(weight)/1000;
        bally = bally*(1000-weight)/1000 + bally_temp*(weight)/1000;
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

            ballx = ballx*(1000-weight)/1000 + oppx_temp*(weight)/1000;
            bally = bally*(1000-weight)/1000 + oppy_temp*(weight)/1000;
        }
    }

}